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
 * $Id: OutOfOrderSectionReadHandler.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.factories.report.flow;

import org.jfree.report.JFreeReportInfo;
import org.jfree.report.structure.Element;
import org.jfree.report.structure.Section;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Creation-Date: 09.04.2006, 14:57:38
 *
 * @author Thomas Morgner
 */
public class OutOfOrderSectionReadHandler extends SectionReadHandler
{
  private Section outOfOrderSection;
  private String role;
  private boolean printInFlow;

  /**
   * Creates a new generic read handler. The given namespace and tagname can be
   * arbitary values and should not be confused with the ones provided by the
   * XMLparser itself.
   *
   * @param namespace
   * @param tagName
   */
  public OutOfOrderSectionReadHandler()
  {
    outOfOrderSection = new Section();
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
    role = attrs.getValue(getUri(), "role");
    final String printInFlowValue = attrs.getValue(getUri(), "print-inflow");
    if (printInFlowValue != null)
    {
      printInFlow = "true".equals(printInFlowValue);
    }
    else
    {
      printInFlow = true;
    }

  }

  /**
   * Done parsing.
   *
   * @throws SAXException       if there is a parsing error.
   * @throws XmlReaderException if there is a reader error.
   */
  protected void doneParsing() throws SAXException
  {
    super.doneParsing();
    final Section outOfOrderSection = (Section) getElement();
    outOfOrderSection.setAttribute(JFreeReportInfo.REPORT_NAMESPACE, "print-in-flow", String.valueOf(printInFlow));
    outOfOrderSection.setAttribute(JFreeReportInfo.REPORT_NAMESPACE, "role", role);
  }

  protected Element getElement()
  {
    return outOfOrderSection;
  }
}
