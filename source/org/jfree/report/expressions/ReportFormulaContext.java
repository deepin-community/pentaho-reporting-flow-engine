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
 * $Id: ReportFormulaContext.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.expressions;

import org.jfree.report.DataFlags;
import org.jfree.report.DataRow;
import org.jfree.report.DataSourceException;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.formula.ContextEvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.LocalizationContext;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;
import org.pentaho.reporting.libraries.formula.operators.OperatorFactory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.DateTimeType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Creation-Date: 29.11.2006, 17:54:33
 *
 * @author Thomas Morgner
 */
public class ReportFormulaContext implements FormulaContext
{
  private FormulaContext backend;
  private DataRow dataRow;
  private Object declaringElement;

  public ReportFormulaContext(FormulaContext backend,
                              DataRow dataRow)
  {
    this.backend = backend;
    this.dataRow = dataRow;
  }

  public LocalizationContext getLocalizationContext()
  {
    return backend.getLocalizationContext();
  }

  public Configuration getConfiguration()
  {
    return backend.getConfiguration();
  }

  public FunctionRegistry getFunctionRegistry()
  {
    return backend.getFunctionRegistry();
  }

  public TypeRegistry getTypeRegistry()
  {
    return backend.getTypeRegistry();
  }

  public OperatorFactory getOperatorFactory()
  {
    return backend.getOperatorFactory();
  }

  public boolean isReferenceDirty(Object name) throws ContextEvaluationException
  {
    try
    {
      final DataFlags flags = dataRow.getFlags(String.valueOf(name));
      if (flags == null)
      {
        throw new ContextEvaluationException
            (new LibFormulaErrorValue(LibFormulaErrorValue.ERROR_REFERENCE_NOT_RESOLVABLE));
      }
      return flags.isChanged();
    }
    catch (Exception e)
    {
      throw new ContextEvaluationException
          (new LibFormulaErrorValue(LibFormulaErrorValue.ERROR_REFERENCE_NOT_RESOLVABLE));
    }
  }

  public Type resolveReferenceType(Object name)
  {
    try
    {
      final DataFlags flags = dataRow.getFlags(String.valueOf(name));
      if (flags != null)
      {
        if (flags.isDate())
        {
          return DateTimeType.DATE_TYPE;
        }
        if (flags.isNumeric())
        {
          return NumberType.GENERIC_NUMBER;
        }
        return TextType.TYPE;
      }
    }
    catch (DataSourceException ex)
    {
    }
    return AnyType.TYPE;
  }

  public Object resolveReference(Object name) throws ContextEvaluationException
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    try
    {
      return dataRow.get(String.valueOf(name));
    }
    catch (DataSourceException e)
    {
      DebugLog.log("Error while resolving formula reference: ", e);
      throw new ContextEvaluationException(new LibFormulaErrorValue
          (LibFormulaErrorValue.ERROR_REFERENCE_NOT_RESOLVABLE));
    }
  }

  public DataRow getDataRow()
  {
    return dataRow;
  }

  public void setDataRow(final DataRow dataRow)
  {
    this.dataRow = dataRow;
  }

  public Object getDeclaringElement()
  {
    return declaringElement;
  }

  public void setDeclaringElement(final Object declaringElement)
  {
    this.declaringElement = declaringElement;
  }
}
