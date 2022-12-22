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
 * $Id: AutoTableCellContentReadHandler.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.misc.autotable.xml;

import org.jfree.report.modules.factories.report.flow.AbstractElementReadHandler;
import org.jfree.report.modules.factories.report.flow.FlowReportFactoryModule;
import org.jfree.report.modules.misc.autotable.AutoTableCellContent;
import org.jfree.report.structure.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Creation-Date: Dec 9, 2006, 5:47:25 PM
 *
 * @author Thomas Morgner
 */
public class AutoTableCellContentReadHandler extends AbstractElementReadHandler
{
  private AutoTableCellContent autoTableCellContent;

  public AutoTableCellContentReadHandler()
  {
    autoTableCellContent = new AutoTableCellContent();
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    super.startParsing(attrs);

    final Element element = getElement();
    // Copy all other attributes ..
    final int attrLength = attrs.getLength();
    for (int i = 0; i < attrLength; i++)
    {
      final String uri = attrs.getURI(i);
      final String local = attrs.getLocalName(i);
      if (FlowReportFactoryModule.NAMESPACE.equals(uri) == false)
      {
        final String value = attrs.getValue(i);
        element.setAttribute(uri, local, value);
      }
    }
  }

  protected Element getElement()
  {
    return autoTableCellContent;
  }
}
