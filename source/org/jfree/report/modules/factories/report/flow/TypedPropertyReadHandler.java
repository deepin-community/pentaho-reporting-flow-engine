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
 * $Id: TypedPropertyReadHandler.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.factories.report.flow;

import java.beans.IntrospectionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.jfree.report.util.CharacterEntityParser;
import org.jfree.report.util.beans.BeanException;
import org.jfree.report.util.beans.BeanUtility;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.Base64;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * Creation-Date: 09.04.2006, 12:58:24
 *
 * @author Thomas Morgner
 */
public class TypedPropertyReadHandler extends PropertyReadHandler
{
  private boolean plainContent;
  private String encoding;
  private String className;
  private BeanUtility beanUtility;
  private String expressionName;
  private CharacterEntityParser entityParser;

  public TypedPropertyReadHandler(final BeanUtility beanDescription,
                                  final String expressionName,
                                  final CharacterEntityParser entityParser)
  {
    if (beanDescription == null)
    {
      throw new NullPointerException(
              "Expression must not be null");
    }
    if (entityParser == null)
    {
      throw new NullPointerException(
              "EntityParser must not be null");
    }
    this.expressionName = expressionName;
    this.beanUtility = beanDescription;
    this.entityParser = entityParser;
    this.plainContent = true;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException       if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    super.doneParsing();
    try
    {
      if (plainContent)
      {
        final String result = getResult();
        if ("base64".equals(encoding))
        {
          final byte[] data = Base64.decode(result.trim().toCharArray());
          final ByteArrayInputStream bin = new ByteArrayInputStream(data);
          final ObjectInputStream oin = new ObjectInputStream(bin);
          final Object value = oin.readObject();
          beanUtility.setProperty(getName(), value);
        }
        else
        {
          if (className != null)
          {
            ClassLoader cl = ObjectUtilities.getClassLoader
                    (TypedPropertyReadHandler.class);
            Class c = cl.loadClass(className);
            beanUtility.setPropertyAsString
                    (getName(), c, entityParser.decodeEntities(result));
          }
          else
          {
            beanUtility.setPropertyAsString
                    (getName(), entityParser.decodeEntities(result));
          }
        }
      }
    }
    catch (BeanException e)
    {
      e.printStackTrace();
      throw new ParseException("Unable to assign property '" + getName()
              + "' to expression '" + expressionName + "'", e, getLocator());
    }
    catch (ClassNotFoundException e)
    {
      throw new ParseException("Unable to assign property '" + getName()
              + "' to expression '" + expressionName + "'", e, getLocator());
    }
    catch (IOException e)
    {
      throw new ParseException("Unable to assign property '" + getName()
              + "' to expression '" + expressionName + "'", e, getLocator());
    }
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    super.startParsing(attrs);
    className = attrs.getValue(getUri(), "class");
    encoding = attrs.getValue(getUri(), "encoding");
    if (encoding == null)
    {
      encoding = "text";
    }
    else if (("text".equals(encoding) == false) && "base64".equals(
            encoding) == false)
    {
      DebugLog.log("Invalid value for attribute 'encoding'. Defaulting to 'text'");
      encoding = "text";
    }
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException       if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts)
          throws SAXException
  {
    if (isSameNamespace(uri) == false)
    {
      return null;
    }

    if ("property".equals(tagName))
    {
      plainContent = false;
      final String name = atts.getValue(uri, "name");
      if (name == null)
      {
        throw new ParseException("Required attribute 'name' is missing", getLocator());
      }
      try
      {
        final Class type = beanUtility.getPropertyType(name);
        final Object property = type.newInstance();
        final BeanUtility propertyUtility = new BeanUtility(property);
        return new TypedPropertyReadHandler
                (propertyUtility, expressionName, entityParser);
      }
      catch (BeanException e)
      {
        throw new ParseException("Property '" + name + "' for expression '" +
                className + "' is not valid. The specified class was not found.",
                e, getRootHandler().getDocumentLocator());
      }
      catch (IllegalAccessException e)
      {
        throw new ParseException(
                "Property '" + name + "' for expression '" + className +
                        "' is not valid. The specified class was not found.",
                e, getRootHandler().getDocumentLocator());
      }
      catch (InstantiationException e)
      {
        throw new ParseException(
                "Property '" + name + "' for expression '" + className +
                        "' is not valid. The specified class cannot be instantiated.",
                e, getRootHandler().getDocumentLocator());
      }
      catch (IntrospectionException e)
      {
        throw new ParseException(
                "Property '" + name + "' for expression '" + className +
                        "' is not valid. Introspection failed for this expression.",
                e, getRootHandler().getDocumentLocator());
      }
    }
    return null;
  }
}
