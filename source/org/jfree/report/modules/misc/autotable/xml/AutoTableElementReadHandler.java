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
 * $Id: AutoTableElementReadHandler.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.misc.autotable.xml;

import java.util.ArrayList;

import org.jfree.report.modules.factories.report.flow.AbstractElementReadHandler;
import org.jfree.report.modules.factories.report.base.NodeReadHandlerFactory;
import org.jfree.report.modules.factories.report.base.NodeReadHandler;
import org.jfree.report.modules.misc.autotable.AutoTableElement;
import org.jfree.report.modules.misc.autotable.AutoTableModule;
import org.jfree.report.structure.Element;
import org.jfree.report.structure.Section;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

/**
 * Creation-Date: Dec 9, 2006, 5:47:25 PM
 *
 * @author Thomas Morgner
 */
public class AutoTableElementReadHandler extends AbstractElementReadHandler
{
  private AutoTableElement autoTableElement;
  private ArrayList headerSections;
  private ArrayList footerSections;
  private ArrayList contentSections;

  public AutoTableElementReadHandler()
  {
    autoTableElement = new AutoTableElement();
    headerSections = new ArrayList();
    contentSections = new ArrayList();
    footerSections = new ArrayList();
  }

  protected Element getElement()
  {
    return autoTableElement;
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts)
      throws SAXException
  {
    if (AutoTableModule.AUTOTABLE_NAMESPACE.equals(uri) == false)
    {
      return super.getHandlerForChild(uri, tagName, atts);
    }

    final NodeReadHandlerFactory factory = NodeReadHandlerFactory.getInstance();
    final NodeReadHandler handler = (NodeReadHandler) factory.getHandler(uri, tagName);
    if (handler == null)
    {
      return null;
    }

    if (tagName.equals("auto-table-header"))
    {
      headerSections.add(handler);
      return handler;
    }

    if (tagName.equals("auto-table-footer"))
    {
      footerSections.add(handler);
      return handler;
    }

    if (tagName.equals("auto-table-cell"))
    {
      contentSections.add(handler);
      return handler;
    }

    return null;
  }


  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    for (int i = 0; i < headerSections.size(); i++)
    {
      final NodeReadHandler handler = (NodeReadHandler) headerSections.get(i);
      autoTableElement.addHeader((Section) handler.getNode());
    }

    for (int i = 0; i < footerSections.size(); i++)
    {
      final NodeReadHandler handler = (NodeReadHandler) footerSections.get(i);
      autoTableElement.addFooter((Section) handler.getNode());
    }

    for (int i = 0; i < contentSections.size(); i++)
    {
      final NodeReadHandler handler = (NodeReadHandler) contentSections.get(i);
      autoTableElement.addContent((Section) handler.getNode());
    }
  }
}
