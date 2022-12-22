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
 * $Id: ReportTargetUtil.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow;

import org.jfree.layouting.util.AttributeMap;
import org.jfree.report.JFreeReportInfo;
import org.jfree.report.structure.Element;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 * @since 20.03.2007
 */
public class ReportTargetUtil
{
  private ReportTargetUtil()
  {
  }


  public static String getNamespaceFromAttribute(final AttributeMap attrs)
  {
    final Object attribute = attrs.getAttribute
        (JFreeReportInfo.REPORT_NAMESPACE, Element.NAMESPACE_ATTRIBUTE);
    if (attribute instanceof String)
    {
      return (String) attribute;
    }
    return JFreeReportInfo.REPORT_NAMESPACE;
  }

  public static String getElemenTypeFromAttribute(final AttributeMap attrs)
  {
    final Object attribute = attrs.getAttribute
        (JFreeReportInfo.REPORT_NAMESPACE, Element.TYPE_ATTRIBUTE);
    if (attribute instanceof String)
    {
      return (String) attribute;
    }
    return "element";
  }

  public static boolean isElementOfType(final String uri,
                                          final String tagName,
                                          final AttributeMap attrs)
  {
    final String namespace = getNamespaceFromAttribute(attrs);
    if (ObjectUtilities.equal(namespace, uri) == false)
    {
      return false;
    }

    final String elementTagName = getElemenTypeFromAttribute(attrs);
    return ObjectUtilities.equal(tagName, elementTagName);
  }

}
