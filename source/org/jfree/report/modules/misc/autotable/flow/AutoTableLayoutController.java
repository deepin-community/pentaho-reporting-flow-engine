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
 * $Id: AutoTableLayoutController.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.misc.autotable.flow;

import org.jfree.layouting.util.AttributeMap;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.data.ReportDataRow;
import org.jfree.report.flow.FlowControlOperation;
import org.jfree.report.flow.FlowController;
import org.jfree.report.flow.ReportContext;
import org.jfree.report.flow.ReportTarget;
import org.jfree.report.flow.layoutprocessor.ElementLayoutController;
import org.jfree.report.flow.layoutprocessor.LayoutController;
import org.jfree.report.flow.layoutprocessor.LayoutControllerFactory;
import org.jfree.report.flow.layoutprocessor.LayoutControllerUtil;
import org.jfree.report.modules.misc.autotable.AutoTableElement;
import org.jfree.report.modules.misc.autotable.AutoTableModule;

/**
 * Creation-Date: Dec 9, 2006, 6:05:58 PM
 *
 * @author Thomas Morgner
 */
public class AutoTableLayoutController extends ElementLayoutController
{
  public static final int HANDLING_HEADER = 0;
  public static final int HANDLING_DATA = 1;
  public static final int HANDLING_FOOTER = 2;

  private int currentColumn;
  private int processingState;
  private int columnCount;

  public AutoTableLayoutController()
  {
  }

  public void initialize(final Object node, final FlowController flowController, final LayoutController parent)
      throws DataSourceException, ReportDataFactoryException, ReportProcessingException
  {
    super.initialize(node, flowController, parent);
    final ReportDataRow reportDataRow =
        flowController.getMasterRow().getReportDataRow();
    this.columnCount = reportDataRow.getColumnCount();
  }

  protected LayoutController processContent(final ReportTarget target)
      throws DataSourceException, ReportProcessingException, ReportDataFactoryException
  {
    switch (processingState)
    {
      case AutoTableLayoutController.HANDLING_HEADER:
        return processHeader(target);
      case AutoTableLayoutController.HANDLING_FOOTER:
        return processFooter(target);
      case AutoTableLayoutController.HANDLING_DATA:
        return processData(target);
      default:
        throw new ReportProcessingException("No such state.");
    }

  }

  private LayoutController processData(final ReportTarget target)
      throws ReportProcessingException, DataSourceException, ReportDataFactoryException
  {
    // the auto-table is responsible for the iteration over the table.
    final AutoTableElement node = (AutoTableElement) getElement();
    if (node.getContentCount() == 0)
    {
      throw new ReportProcessingException
          ("An Auto-Table must have at least one defined column.");
    }

    if (currentColumn == 0)
    {
      // Start a new table-header section ..
      final AttributeMap elementMap = LayoutControllerUtil.createEmptyMap
          (AutoTableModule.AUTOTABLE_NAMESPACE, "data-row");
      target.startElement(elementMap);
    }

    if (currentColumn < columnCount)
    {
      // now delegate the processing to the section handler for the header ..
      final FlowController flowController = getFlowController();
      final ReportContext reportContext = flowController.getReportContext();
      final LayoutControllerFactory layoutControllerFactory =
          reportContext.getLayoutControllerFactory();

      final int idx = currentColumn % node.getContentCount();
      final AutoTableLayoutController derived = (AutoTableLayoutController) clone();
      return layoutControllerFactory.create
          (flowController, node.getContentCell(idx), derived);
    }

    // close the table-header section ..
    final AttributeMap elementMap = LayoutControllerUtil.createEmptyMap
        (AutoTableModule.AUTOTABLE_NAMESPACE, "data-row");
    target.endElement(elementMap);

    final FlowController flowController =
        getFlowController().performOperation(FlowControlOperation.ADVANCE);
    final FlowController cfc = tryRepeatingCommit(flowController);
    if (cfc != null)
    {
      // Go back to the beginning. We have made a commit, so the cursor points
      // to the next row of data ..
      final AutoTableLayoutController derived = (AutoTableLayoutController) clone();
      derived.setFlowController(cfc);
      derived.currentColumn = 0;
      return derived;
    }

    // Advance is impossible, that means we reached the end of the group or
    // the end of the table ..
    final AutoTableLayoutController derived = (AutoTableLayoutController) clone();
    derived.currentColumn = 0;
    derived.processingState = AutoTableLayoutController.HANDLING_FOOTER;
    return derived;
  }

  private LayoutController processFooter(final ReportTarget target)
      throws ReportProcessingException, DataSourceException, ReportDataFactoryException
  {
    final AutoTableElement node = (AutoTableElement) getElement();
    if (node.getFooterCount() == 0)
    {
      final AutoTableLayoutController derived = (AutoTableLayoutController) clone();
      derived.currentColumn = 0;
      derived.processingState = -1;
      derived.setProcessingState(ElementLayoutController.FINISHING);
      return derived;
    }

    if (currentColumn == 0)
    {
      // Start a new table-header section ..
      final AttributeMap elementMap = LayoutControllerUtil.createEmptyMap
          (AutoTableModule.AUTOTABLE_NAMESPACE, "footer-row");
      target.startElement(elementMap);
    }

    if (currentColumn < columnCount)
    {
      // now delegate the processing to the section handler for the header ..
      final FlowController flowController = getFlowController();
      final ReportContext reportContext = flowController.getReportContext();
      final LayoutControllerFactory layoutControllerFactory =
          reportContext.getLayoutControllerFactory();

      final int idx = currentColumn % node.getFooterCount();
      final AutoTableLayoutController derived = (AutoTableLayoutController) clone();
      return layoutControllerFactory.create
          (flowController, node.getFooterCell(idx), derived);
    }

    // close the table-header section ..
    final AttributeMap elementMap = LayoutControllerUtil.createEmptyMap
        (AutoTableModule.AUTOTABLE_NAMESPACE, "footer-row");
    target.endElement(elementMap);

    final AutoTableLayoutController derived = (AutoTableLayoutController) clone();
    derived.currentColumn = 0;
    derived.processingState = -1;
    derived.setProcessingState(ElementLayoutController.FINISHING);
    return derived;
  }

  private LayoutController processHeader(final ReportTarget target)
      throws ReportProcessingException, DataSourceException, ReportDataFactoryException
  {
    final AutoTableElement node = (AutoTableElement) getElement();
    if (node.getHeaderCount() == 0)
    {
      final AutoTableLayoutController derived = (AutoTableLayoutController) clone();
      derived.currentColumn = 0;
      derived.processingState = AutoTableLayoutController.HANDLING_DATA;
      return derived;
    }

    if (currentColumn == 0)
    {
      // Start a new table-header section ..
      final AttributeMap elementMap = LayoutControllerUtil.createEmptyMap
          (AutoTableModule.AUTOTABLE_NAMESPACE, "header-row");
      target.startElement(elementMap);
    }

    if (currentColumn < columnCount)
    {
      // now delegate the processing to the section handler for the header ..
      final FlowController flowController = getFlowController();
      final ReportContext reportContext = flowController.getReportContext();
      final LayoutControllerFactory layoutControllerFactory =
          reportContext.getLayoutControllerFactory();

      final int idx = currentColumn % node.getHeaderCount();
      final AutoTableLayoutController derived = (AutoTableLayoutController) clone();
      return layoutControllerFactory.create
          (flowController, node.getHeaderCell(idx), derived);
    }

    // close the table-header section ..
    final AttributeMap elementMap = LayoutControllerUtil.createEmptyMap
        (AutoTableModule.AUTOTABLE_NAMESPACE, "header-row");
    target.endElement(elementMap);

    final AutoTableLayoutController derived = (AutoTableLayoutController) clone();
    derived.currentColumn = 0;
    derived.processingState = AutoTableLayoutController.HANDLING_DATA;
    return derived;
  }

  /**
   * Joins with a delegated process flow. This is generally called from a child flow and should *not* (I mean it!) be
   * called from outside. If you do, you'll suffer.
   *
   * @param flowController the flow controller of the parent.
   * @return the joined layout controller that incorperates all changes from the delegate.
   */
  public LayoutController join(final FlowController flowController)
  {
    final AutoTableLayoutController derived = (AutoTableLayoutController) clone();
    derived.setFlowController(flowController);
    derived.currentColumn += 1;
    return derived;
  }

  public int getCurrentColumn()
  {
    return currentColumn;
  }
}
