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
 * $Id: AttributeReadHandler.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.factories.report.flow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.jfree.report.util.CharacterEntityParser;
import org.jfree.report.util.beans.BeanException;
import org.jfree.report.util.beans.ConverterRegistry;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.Base64;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * Creation-Date: 09.04.2006, 12:58:24
 *
 * @author Thomas Morgner
 */
public class AttributeReadHandler extends PropertyReadHandler
{
  private String encoding;
  private String className;
  private Object value;
  private CharacterEntityParser entityParser;
  private String namespace;

  public AttributeReadHandler()
  {
    this.entityParser = CharacterEntityParser.createXMLEntityParser();
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
      final String result = getResult();
      if ("base64".equals(encoding))
      {
        final byte[] data = Base64.decode(result.trim().toCharArray());
        final ByteArrayInputStream bin = new ByteArrayInputStream(data);
        final ObjectInputStream oin = new ObjectInputStream(bin);
        value = oin.readObject();
      }
      else
      {
        if (className != null)
        {
          ClassLoader cl = ObjectUtilities.getClassLoader
                  (AttributeReadHandler.class);
          Class c = cl.loadClass(className);
          ConverterRegistry.toPropertyValue
                  (entityParser.decodeEntities(result), c);
        }
        else
        {
          ConverterRegistry.toPropertyValue
                  (entityParser.decodeEntities(result), String.class);
        }
      }
    }
    catch (BeanException e)
    {
      throw new ParseException("Unable to set attribute '" + getName() + "'", e, getLocator());
    }
    catch (ClassNotFoundException e)
    {
      throw new ParseException("Unable to set attribute '" + getName() + "'", e, getLocator() );
    }
    catch (IOException e)
    {
      throw new ParseException("Unable to set attribute '" + getName() + "'", e, getLocator());
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
    namespace = attrs.getValue(getUri(), "namespace-uri");
    if (namespace == null)
    {
      namespace = getNamespace();
    }

    className = attrs.getValue(getUri(), "class");
    if (className == null)
    {
      className = "java.lang.String";
    }
    encoding = attrs.getValue(getUri(), "encoding");
    if (encoding == null)
    {
      encoding = "text";
    }
    else if (("text".equals(encoding) == false) && "base64".equals(encoding) == false)
    {
      DebugLog.log ("Invalid value for attribute 'encoding'. Defaulting to 'text'");
      encoding = "text";
    }
  }

  public String getNamespace()
  {
    return namespace;
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject()
  {
    return value;
  }
}
