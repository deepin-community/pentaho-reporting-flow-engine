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
 * $Id: ReportReadHandler.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.factories.report.flow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.jfree.report.JFreeReport;
import org.jfree.report.modules.factories.data.base.DataFactoryReadHandlerFactory;
import org.jfree.report.modules.factories.data.base.DataFactoryReadHandler;
import org.jfree.report.structure.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertiesReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

/**
 * Creation-Date: 09.04.2006, 14:57:38
 *
 * @author Thomas Morgner
 */
public class ReportReadHandler extends SectionReadHandler
{
  private StringReadHandler queryReadHandler;
  private PropertiesReadHandler propertiesReadHandler;
  private DataFactoryReadHandler datasourceFactoryReadHandler;
  private ArrayList styleSheetReadHandlers;
  private JFreeReport report;

  /**
   * Creates a new generic read handler. The given namespace and tagname can be
   * arbitary values and should not be confused with the ones provided by the
   * XMLparser itself.
   */
  public ReportReadHandler()
  {
    report = new JFreeReport();
    styleSheetReadHandlers = new ArrayList();
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
    XmlReadHandler base = super.getHandlerForChild(uri, tagName, atts);
    if (base != null)
    {
      return base;
    }

    final DataFactoryReadHandlerFactory factory = DataFactoryReadHandlerFactory.getInstance();
    final DataFactoryReadHandler handler = (DataFactoryReadHandler) factory.getHandler(uri, tagName);
    if (handler != null)
    {
      datasourceFactoryReadHandler = handler;
      return handler;
    }


    if (FlowReportFactoryModule.NAMESPACE.equals(uri))
    {
      if ("query".equals(tagName))
      {
        queryReadHandler = new StringReadHandler();
        return queryReadHandler;
      }
      if ("configuration".equals(tagName))
      {
        propertiesReadHandler = new PropertiesReadHandler();
        return propertiesReadHandler;
      }
      if ("stylesheet".equals(tagName))
      {
        StyleSheetReadHandler srh = new StyleSheetReadHandler();
        styleSheetReadHandlers.add(srh);
        return srh;
      }
      if ("inline-stylesheet".equals(tagName))
      {
        StyleSheetReadHandler srh = new StyleSheetReadHandler();
        styleSheetReadHandlers.add(srh);
        return srh;
      }
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
    if (queryReadHandler == null)
    {
      throw new ParseException
              ("Required element 'query' is missing.", getLocator());
    }
    super.doneParsing();
    JFreeReport report = (JFreeReport) getElement();
    report.setQuery(queryReadHandler.getResult());
    if (propertiesReadHandler != null)
    {
      final Properties p = propertiesReadHandler.getResult();
      final Iterator entries = p.entrySet().iterator();
      while (entries.hasNext())
      {
        Map.Entry entry = (Map.Entry) entries.next();
        report.getEditableConfiguration().setConfigProperty
                ((String) entry.getKey(), (String) entry.getValue());
      }
    }
    if (datasourceFactoryReadHandler != null)
    {
      report.setDataFactory(datasourceFactoryReadHandler.getDataFactory());
    }
    for (int i = 0; i < styleSheetReadHandlers.size(); i++)
    {
      StyleSheetReadHandler handler = (StyleSheetReadHandler)
              styleSheetReadHandlers.get(i);
      report.addStyleSheet(handler.getStyleSheet());
    }
  }

  protected Element getElement()
  {
    return report;
  }
}
