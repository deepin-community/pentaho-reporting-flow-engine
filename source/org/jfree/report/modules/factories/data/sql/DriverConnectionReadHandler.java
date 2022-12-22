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
 * $Id: DriverConnectionReadHandler.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.factories.data.sql;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.jfree.report.modules.data.sql.DriverConnectionProvider;
import org.jfree.report.modules.data.sql.ConnectionProvider;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertiesReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

/**
 * Creation-Date: 07.04.2006, 18:09:25
 *
 * @author Thomas Morgner
 */
public class DriverConnectionReadHandler extends AbstractXmlReadHandler
  implements ConnectionReadHandler
{
  private StringReadHandler driverReadHandler;
  private StringReadHandler urlReadHandler;
  private PropertiesReadHandler propertiesReadHandler;
  private ConnectionProvider driverConnectionProvider;

  public DriverConnectionReadHandler()
  {
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
    if ("driver".equals(tagName))
    {
      driverReadHandler = new StringReadHandler();
      return driverReadHandler;
    }
    if ("url".equals(tagName))
    {
      urlReadHandler = new StringReadHandler();
      return urlReadHandler;
    }
    if ("properties".equals(tagName))
    {
      propertiesReadHandler = new PropertiesReadHandler();
      return propertiesReadHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException       if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    final DriverConnectionProvider provider = new DriverConnectionProvider();
    if (driverReadHandler != null)
    {
      provider.setDriver(driverReadHandler.getResult());
    }
    if (urlReadHandler != null)
    {
      provider.setUrl(urlReadHandler.getResult());
    }
    if (propertiesReadHandler != null)
    {
      final Properties p = (Properties) propertiesReadHandler.getObject();
      final Iterator it = p.entrySet().iterator();
      while (it.hasNext())
      {
        Map.Entry entry = (Map.Entry) it.next();
        provider.setProperty((String) entry.getKey(), (String) entry.getValue());
      }
    }
    driverConnectionProvider = provider;
  }

  /**
   * Returns the object for this element or null, if this element does not
   * create an object.
   *
   * @return the object.
   */
  public Object getObject() throws SAXException
  {
    return driverConnectionProvider;
  }

  public ConnectionProvider getProvider()
  {
    return driverConnectionProvider;
  }
}
