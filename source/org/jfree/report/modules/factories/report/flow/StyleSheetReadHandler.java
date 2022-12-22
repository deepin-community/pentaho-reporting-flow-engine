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
 * $Id: StyleSheetReadHandler.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.factories.report.flow;

import org.jfree.layouting.input.style.StyleSheet;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * Creation-Date: 12.04.2006, 14:53:29
 *
 * @author Thomas Morgner
 */
public class StyleSheetReadHandler extends StringReadHandler
{
  private StyleSheet styleSheet;

  public StyleSheetReadHandler()
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
    String href = attrs.getValue(getUri(), "href");
    if (href != null)
    {
      final ResourceKey key = getRootHandler().getSource();
      final ResourceManager manager = getRootHandler().getResourceManager();
      try
      {
        final ResourceKey derivedKey = manager.deriveKey(key, href);
        final Resource resource = manager.create(derivedKey, null,
                StyleSheet.class);
        getRootHandler().getDependencyCollector().add(resource);
        styleSheet = (StyleSheet) resource.getResource();
      }
      catch (ResourceKeyCreationException e)
      {
        throw new ParseException
            ("Unable to derive key for " + key + " and " + href, getLocator());
      }
      catch (ResourceCreationException e)
      {
        DebugLog.log("Unable to parse resource for " + key + " and " + href);
      }
      catch (ResourceLoadingException e)
      {
        DebugLog.log("Unable to load resource data for " + key + " and " + href);
      }
      catch (ResourceException e)
      {
        DebugLog.log("Unable to load resource for " + key + " and " + href);
      }
    }
  }

  /**
   * Done parsing.
   *
   * @throws SAXException       if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    super.doneParsing();
    if (this.styleSheet != null)
    {
      return;
    }

    final String styleText = getResult();
    if (styleText.trim().length() == 0)
    {
      return;
    }
    try
    {
      final byte[] bytes = styleText.getBytes("UTF-8");

      final ResourceKey baseKey = getRootHandler().getSource();
      final ResourceManager resourceManager = getRootHandler().getResourceManager();
      final ResourceKey rawKey = resourceManager.createKey(bytes);

      final Resource resource = resourceManager.create
              (rawKey, baseKey, StyleSheet.class);
      this.styleSheet = (StyleSheet) resource.getResource();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject()
  {
    return styleSheet;
  }

  public StyleSheet getStyleSheet()
  {
    return styleSheet;
  }
}
