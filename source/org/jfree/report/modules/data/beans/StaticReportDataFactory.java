/**
 * ========================================
 * JFreeReport : a free Java report library
 * ========================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited, Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * $Id: StaticReportDataFactory.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.data.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;

import javax.swing.table.TableModel;

import org.jfree.report.DataSet;
import org.jfree.report.ReportData;
import org.jfree.report.ReportDataFactory;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.TableReportData;
import org.jfree.report.util.CSVTokenizer;
import org.jfree.report.util.DataSetUtility;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * This report data factory uses introspection to search for a report data
 * source. The query has the following format:
 *
 * &lt;full-qualified-classname&gr;#methodName(Parameters)
 * &lt;full-qualified-classname&gr;(constructorparams)#methodName(Parameters)
 * &lt;full-qualified-classname&gr;(constructorparams)
 *
 * @author Thomas Morgner
 */
public class StaticReportDataFactory implements ReportDataFactory
{
  public StaticReportDataFactory()
  {
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The
   * Parameterset given here may contain more data than actually needed.
   * <p/>
   * The dataset may change between two calls, do not assume anything!
   *
   * @param query
   * @param parameters
   * @return
   */
  public ReportData queryData(final String query, final DataSet parameters)
          throws ReportDataFactoryException
  {
    final int methodSeparatorIdx = query.indexOf('#');

    if ((methodSeparatorIdx + 1) >= query.length())
    {
      // If we have a method separator, then it cant be at the end of the text.
      throw new ReportDataFactoryException("Malformed query: " + query);
    }

    if (methodSeparatorIdx == -1)
    {
      // we have no method. So this query must be a reference to a tablemodel
      // instance.
      final String[] parameterNames;
      final int parameterStartIdx = query.indexOf('(');
      final String constructorName;
      if (parameterStartIdx == -1)
      {
        parameterNames = new String[0];
        constructorName = query;
      }
      else
      {
        parameterNames = createParameterList(query, parameterStartIdx);
        constructorName = query.substring(0, parameterStartIdx);
      }

      try
      {
        Constructor c = findDirectConstructor(constructorName, parameterNames.length);

        Object[] params = new Object[parameterNames.length];
        for (int i = 0; i < parameterNames.length; i++)
        {
          final String name = parameterNames[i];
          params[i] = DataSetUtility.getByName(parameters, name);
        }
        final Object o = c.newInstance(params);
        if (o instanceof TableModel)
        {
          return new TableReportData ((TableModel) o);
        }

        return (ReportData) o;
      }
      catch (Exception e)
      {
        throw new ReportDataFactoryException
                ("Unable to instantiate class for non static call.", e);
      }
    }

    return createComplexTableModel
            (query, methodSeparatorIdx, parameters);
  }

  private ReportData createComplexTableModel(final String query,
                                             final int methodSeparatorIdx,
                                             final DataSet parameters)
          throws ReportDataFactoryException
  {
    final String constructorSpec = query.substring(0, methodSeparatorIdx);
    final int constParamIdx = constructorSpec.indexOf('(');
    if (constParamIdx == -1)
    {
      // Either a static call or a default constructor call..
      return loadFromDefaultConstructor(query, methodSeparatorIdx, parameters);
    }

    // We have to find a suitable constructor ..
    final String className = query.substring(0, constParamIdx);
    final String[] parameterNames = createParameterList(constructorSpec, constParamIdx);
    final Constructor c = findIndirectConstructor(className, parameterNames.length);

    final String methodQuery = query.substring(methodSeparatorIdx + 1);
    final String[] methodParameterNames;
    final String methodName;
    final int parameterStartIdx = methodQuery.indexOf('(');
    if (parameterStartIdx == -1)
    {
      // no parameters. Nice.
      methodParameterNames = new String[0];
      methodName = methodQuery;
    }
    else
    {
      methodName = methodQuery.substring(0, parameterStartIdx);
      methodParameterNames = createParameterList(methodQuery, parameterStartIdx);
    }
    final Method m = findCallableMethod(className, methodName, methodParameterNames.length);

    try
    {
      final Object[] constrParams = new Object[parameterNames.length];
      for (int i = 0; i < parameterNames.length; i++)
      {
        final String name = parameterNames[i];
          constrParams[i] = DataSetUtility.getByName(parameters, name);
      }
      final Object o = c.newInstance(constrParams);

      final Object[] methodParams = new Object[methodParameterNames.length];
      for (int i = 0; i < methodParameterNames.length; i++)
      {
        final String name = methodParameterNames[i];
        methodParams[i] = DataSetUtility.getByName(parameters, name);
      }
      final Object data = m.invoke(o, methodParams);
      if (data instanceof TableModel)
      {
        return new TableReportData((TableModel) data);
      }
      return (ReportData) data;
    }
    catch (Exception e)
    {
      throw new ReportDataFactoryException
              ("Unable to instantiate class for non static call.");
    }
  }

  private ReportData loadFromDefaultConstructor(final String query,
                                                final int methodSeparatorIdx,
                                                final DataSet parameters)
      throws ReportDataFactoryException
  {
    final String className = query.substring(0, methodSeparatorIdx);
    final String methodSpec = query.substring(methodSeparatorIdx + 1);
    final String methodName;
    final String[] parameterNames;
    final int parameterStartIdx = methodSpec.indexOf('(');
    if (parameterStartIdx == -1)
    {
      // no parameters. Nice.
      parameterNames = new String[0];
      methodName = methodSpec;
    }
    else
    {
      parameterNames = createParameterList(methodSpec, parameterStartIdx);
      methodName = methodSpec.substring(0, parameterStartIdx);
    }

    try
    {
      final Method m = findCallableMethod(className, methodName, parameterNames.length);
      Object[] params = new Object[parameterNames.length];
      for (int i = 0; i < parameterNames.length; i++)
      {
        final String name = parameterNames[i];
        params[i] = DataSetUtility.getByName(parameters, name);
      }

      if (Modifier.isStatic(m.getModifiers()))
      {
        final Object o = m.invoke(null, params);
        if (o instanceof TableModel)
        {
          return new TableReportData((TableModel) o);
        }
        return (ReportData) o;
      }

      final ClassLoader classLoader = getClassLoader();
      final Class c = classLoader.loadClass(className);
      final Object o = c.newInstance();
      if (o == null)
      {
        throw new ReportDataFactoryException
                ("Unable to instantiate class for non static call.");
      }
      final Object data = m.invoke(o, params);
      if (data instanceof TableModel)
      {
        return new TableReportData((TableModel) data);
      }
      return (ReportData) data;
    }
    catch (ReportDataFactoryException rdfe)
    {
      throw rdfe;
    }
    catch (Exception e)
    {
      throw new ReportDataFactoryException
              ("Something went terribly wrong: ", e);
    }
  }

  private String[] createParameterList(final String query,
                                       final int parameterStartIdx)
          throws ReportDataFactoryException
  {
    final int parameterEndIdx = query.lastIndexOf(')');
    if (parameterEndIdx < parameterStartIdx)
    {
      throw new ReportDataFactoryException("Malformed query: " + query);
    }
    final String parameterText =
            query.substring(parameterStartIdx + 1, parameterEndIdx);
    final CSVTokenizer tokenizer = new CSVTokenizer(parameterText);
    final int size = tokenizer.countTokens();
    final String[] parameterNames = new String[size];
    int i = 0;
    while (tokenizer.hasMoreTokens())
    {
      parameterNames[i] = tokenizer.nextToken();
      i += 1;
    }
    return parameterNames;
  }

  protected ClassLoader getClassLoader()
  {
    return ObjectUtilities.getClassLoader(StaticReportDataFactory.class);
  }

  private Method findCallableMethod(final String className,
                                    final String methodName,
                                    final int paramCount)
          throws ReportDataFactoryException
  {
    ClassLoader classLoader = getClassLoader();

    if (classLoader == null)
    {
      throw new ReportDataFactoryException("No classloader!");
    }
    try
    {
      Class c = classLoader.loadClass(className);
      if (Modifier.isAbstract(c.getModifiers()))
      {
        throw new ReportDataFactoryException("Abstract class cannot be handled!");
      }

      Method[] methods = c.getMethods();
      for (int i = 0; i < methods.length; i++)
      {
        final Method method = methods[i];
        if (Modifier.isPublic(method.getModifiers()) == false)
        {
          continue;
        }
        if (method.getName().equals(methodName) == false)
        {
          continue;
        }
        final Class returnType = method.getReturnType();
        if (method.getParameterTypes().length != paramCount)
        {
          continue;
        }
        if (TableModel.class.isAssignableFrom(returnType) ||
            ReportData.class.isAssignableFrom(returnType))
        {
          return method;
        }
      }
    }
    catch (ClassNotFoundException e)
    {
      throw new ReportDataFactoryException("No such Class", e);
    }
    throw new ReportDataFactoryException("No such Method: " + className + "#" + methodName);
  }

  private Constructor findDirectConstructor(final String className,
                                            final int paramCount)
          throws ReportDataFactoryException
  {
    ClassLoader classLoader = getClassLoader();
    if (classLoader == null)
    {
      throw new ReportDataFactoryException("No classloader!");
    }

    try
    {
      Class c = classLoader.loadClass(className);
      if (TableModel.class.isAssignableFrom(c) == false &&
          ReportData.class.isAssignableFrom(c) == false)
      {
        throw new ReportDataFactoryException("The specified class must be either a TableModel or a ReportData implementation.");
      }
      if (Modifier.isAbstract(c.getModifiers()))
      {
        throw new ReportDataFactoryException("The specified class cannot be instantiated: it is abstract.");
      }

      Constructor[] methods = c.getConstructors();
      for (int i = 0; i < methods.length; i++)
      {
        final Constructor method = methods[i];
        if (Modifier.isPublic(method.getModifiers()) == false)
        {
          continue;
        }
        if (method.getParameterTypes().length != paramCount)
        {
          continue;
        }
        return method;
      }
    }
    catch (ClassNotFoundException e)
    {
      throw new ReportDataFactoryException("No such Class", e);
    }
    throw new ReportDataFactoryException
        ("There is no constructor in class " + className +
            " that accepts " + paramCount + " parameters.");
  }


  private Constructor findIndirectConstructor(final String className,
                                            final int paramCount)
          throws ReportDataFactoryException
  {
    ClassLoader classLoader = getClassLoader();
    if (classLoader == null)
    {
      throw new ReportDataFactoryException("No classloader!");
    }

    try
    {
      Class c = classLoader.loadClass(className);
      if (Modifier.isAbstract(c.getModifiers()))
      {
        throw new ReportDataFactoryException("The specified class cannot be instantiated: it is abstract.");
      }

      Constructor[] methods = c.getConstructors();
      for (int i = 0; i < methods.length; i++)
      {
        final Constructor method = methods[i];
        if (Modifier.isPublic(method.getModifiers()) == false)
        {
          continue;
        }
        if (method.getParameterTypes().length != paramCount)
        {
          continue;
        }
        return method;
      }
    }
    catch (ClassNotFoundException e)
    {
      throw new ReportDataFactoryException("No such Class", e);
    }
    throw new ReportDataFactoryException
        ("There is no constructor in class " + className +
            " that accepts " + paramCount + " parameters.");
  }


  public void open()
  {

  }

  public void close()
  {

  }

  /**
   * Derives a freshly initialized report data factory, which is independend of
   * the original data factory. Opening or Closing one data factory must not
   * affect the other factories.
   *
   * @return
   */
  public ReportDataFactory derive()
  {
    return this;
  }
}
