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
 * $Id: AutoTableItemLayoutController.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.misc.autotable.flow;

import org.jfree.report.DataFlags;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.data.ReportDataRow;
import org.jfree.report.flow.FlowController;
import org.jfree.report.flow.ReportTarget;
import org.jfree.report.flow.layoutprocessor.ElementLayoutController;
import org.jfree.report.flow.layoutprocessor.LayoutController;
import org.jfree.report.modules.misc.autotable.AutoTableCellContent;

/**
 * Creation-Date: Dec 9, 2006, 8:20:51 PM
 *
 * @author Thomas Morgner
 */
public class AutoTableItemLayoutController extends ElementLayoutController
{

  public AutoTableItemLayoutController()
  {
  }

  protected AutoTableLayoutController findTableParent ()
  {
    LayoutController parent = getParent();
    while (parent != null)
    {
      if (parent instanceof AutoTableLayoutController)
      {
        return (AutoTableLayoutController) parent;
      }

      parent = parent.getParent();
    }
    return null;
  }

  protected LayoutController processContent(final ReportTarget target)
      throws DataSourceException, ReportProcessingException, ReportDataFactoryException
  {
    final AutoTableCellContent content = (AutoTableCellContent) getElement();
    final FlowController flowController = getFlowController();
    final ReportDataRow reportDataRow =
        flowController.getMasterRow().getReportDataRow();

    final AutoTableLayoutController table = findTableParent();
    if (table == null)
    {
      throw new ReportProcessingException("Invalid state: have no auto-table as context.");
    }
    final int currentColumn = table.getCurrentColumn();

    if ("name".equals(content.getItem()))
    {
      final String columnName = reportDataRow.getColumnName(currentColumn);
      target.processText(columnName);
    }
    else if ("value".equals(content.getItem()))
    {
      final DataFlags flags = reportDataRow.getFlags(currentColumn);
      target.processContent(flags);
    }
    else
    {
      throw new ReportProcessingException("Invalid definition: Content-Item with no valid type");
    }

    AutoTableItemLayoutController derived = (AutoTableItemLayoutController) clone();
    derived.setProcessingState(ElementLayoutController.FINISHING);
    derived.setFlowController(flowController);
    return derived;

  }

  /**
   * Joins with a delegated process flow. This is generally called from a child
   * flow and should *not* (I mean it!) be called from outside. If you do,
   * you'll suffer.
   *
   * @param flowController the flow controller of the parent.
   * @return the joined layout controller that incorperates all changes from
   * the delegate.
   */
  public LayoutController join(final FlowController flowController)
  {
    final AutoTableItemLayoutController derived = (AutoTableItemLayoutController) clone();
    derived.setProcessingState(ElementLayoutController.FINISHING);
    derived.setFlowController(flowController);
    return derived;
  }
}
