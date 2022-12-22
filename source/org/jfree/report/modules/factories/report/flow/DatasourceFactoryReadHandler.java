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
 * $Id: DatasourceFactoryReadHandler.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.factories.report.flow;

import org.jfree.report.ReportDataFactory;
import org.jfree.report.modules.factories.data.base.DataFactoryReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;

/**
 * Creation-Date: 10.04.2006, 13:27:47
 *
 * @author Thomas Morgner
 */
public class DatasourceFactoryReadHandler extends AbstractXmlReadHandler
  implements DataFactoryReadHandler
{
  private ReportDataFactory dataFactory;

  public DatasourceFactoryReadHandler()
  {
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
    final String href = attrs.getValue(getUri(), "href");
    if (href == null)
    {
      throw new ParseException("Required attribute 'href' is missing.", getLocator());
    }
    final ResourceKey key = getRootHandler().getSource();
    final ResourceManager manager = getRootHandler().getResourceManager();
    try
    {
      final ResourceKey derivedKey = manager.deriveKey(key, href);
      final Resource resource = manager.create(derivedKey, null, ReportDataFactory.class);
      getRootHandler().getDependencyCollector().add(resource);
      dataFactory = (ReportDataFactory) resource.getResource();
    }
    catch (ResourceKeyCreationException e)
    {
      throw new ParseException
          ("Unable to derive key for " + key + " and " + href, getLocator());
    }
    catch (ResourceCreationException e)
    {
      throw new ParseException
          ("Unable to parse resource for " + key + " and " + href, getLocator());
    }
    catch (ResourceLoadingException e)
    {
      throw new ParseException
          ("Unable to load resource data for " + key + " and " + href, getLocator());
    }
    catch (ResourceException e)
    {
      throw new ParseException("Unable to parse resource for " + key + " and " + href, getLocator());
    }
  }

  public ReportDataFactory getDataFactory()
  {
    return dataFactory;
  }

  /**
   * Returns the object for this element or null, if this element does not
   * create an object.
   *
   * @return the object.
   * @throws SAXException if there is a parsing error.
   */
  public Object getObject() throws SAXException
  {
    return dataFactory;
  }
}
