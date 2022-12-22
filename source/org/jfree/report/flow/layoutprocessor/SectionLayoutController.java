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
 * $Id: SectionLayoutController.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow.layoutprocessor;

import org.jfree.report.DataSourceException;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.expressions.Expression;
import org.jfree.report.flow.FlowController;
import org.jfree.report.flow.ReportContext;
import org.jfree.report.flow.ReportTarget;
import org.jfree.report.structure.Node;
import org.jfree.report.structure.Section;

/**
 * Creation-Date: 24.11.2006, 13:56:10
 *
 * @author Thomas Morgner
 */
public class SectionLayoutController extends ElementLayoutController
{
  // we store the child instead of the index, as the report can be manipulated
  // it is safer this way ..
  private Node[] nodes;
  private int index;

  public SectionLayoutController()
  {
  }

  protected FlowController startData(final ReportTarget target,
                                     final FlowController fc)
      throws DataSourceException, ReportProcessingException,
      ReportDataFactoryException
  {
    final Section s = (Section) getElement();
    return LayoutControllerUtil.processFlowOperations
        (fc, s.getOperationBefore());
  }

  protected LayoutController processContent(final ReportTarget target)
      throws DataSourceException, ReportProcessingException,
      ReportDataFactoryException
  {

    final FlowController flowController = getFlowController();

    final Node[] nodes = getNodes();
    final int currentIndex = getIndex();
    if (currentIndex < nodes.length)
    {
      final Node node = nodes[currentIndex];
      final SectionLayoutController derived = (SectionLayoutController) clone();
      return processChild(derived, node, flowController);
    }
    else
    {
      final SectionLayoutController derived = (SectionLayoutController) clone();
      derived.setProcessingState(ElementLayoutController.FINISHING);
      return derived;
    }
  }

  protected LayoutController processChild(final SectionLayoutController derived,
                                          final Node node,
                                          final FlowController flowController)
      throws DataSourceException, ReportProcessingException,
      ReportDataFactoryException
  {
    final ReportContext reportContext = flowController.getReportContext();
    final LayoutControllerFactory layoutControllerFactory = reportContext.getLayoutControllerFactory();
    if (isDisplayable(node))
    {
      derived.setProcessingState(ElementLayoutController.WAITING_FOR_JOIN);
      return layoutControllerFactory.create(flowController, node, derived);
    }
    else
    {
      derived.setProcessingState(ElementLayoutController.WAITING_FOR_JOIN);
      final LayoutController childLc = layoutControllerFactory.create(flowController, node, derived);
      return LayoutControllerUtil.skipInvisibleElement(childLc);
//      derived.setIndex(derived.getIndex() + 1);
//      return LayoutControllerUtil.skipInvisibleElement(derived);
    }
  }

  protected boolean isDisplayable(final Node node)
      throws DataSourceException
  {
    if (node.isEnabled() == false)
    {
      return false;
    }

    final Expression expression = node.getDisplayCondition();
    if (expression == null)
    {
      return true;
    }

    final Object result = LayoutControllerUtil.evaluateExpression(getFlowController(), node, expression);
    if (Boolean.TRUE.equals(result))
    {
      return true;
    }
    return false;
  }

  /**
   * Finishes the processing of this element. This method is called when the
   * processing state is 'FINISHING'. The element should be closed now and all
   * privatly owned resources should be freed. If the element has a parent, it
   * would be time to join up with the parent now, else the processing state
   * should be set to 'FINISHED'.
   *
   * @param target the report target that receives generated events.
   * @return the new layout controller instance representing the new state.
   *
   * @throws DataSourceException        if there was a problem reading data from
   *                                    the datasource.
   * @throws ReportProcessingException  if there was a general problem during
   *                                    the report processing.
   * @throws ReportDataFactoryException if there was an error trying query
   *                                    data.
   */
  protected LayoutController finishElement(final ReportTarget target)
      throws ReportProcessingException, DataSourceException,
      ReportDataFactoryException
  {
    FlowController fc = handleDefaultEndElement(target);

    // unwind the stack ..
    final Section s = (Section) getElement();
    fc = finishData(target, fc);

    if (s.isRepeat())
    {
      final FlowController cfc = tryRepeatingCommit(fc);
      if (cfc != null)
      {
        // Go back to the beginning ...
        final SectionLayoutController derived = (SectionLayoutController) clone();
        derived.setProcessingState(ElementLayoutController.NOT_STARTED);
        derived.setFlowController(cfc);
        derived.resetSectionForRepeat();
        return derived;
      }
    }

    // Go back to the beginning ...
    final SectionLayoutController derived = (SectionLayoutController) clone();
    derived.setProcessingState(ElementLayoutController.FINISHED);
    derived.setFlowController(fc);
    return derived;
  }

  protected void resetSectionForRepeat()
  {
    setIndex(0);
  }

  protected FlowController finishData(final ReportTarget target,
                                      final FlowController fc)
      throws DataSourceException, ReportProcessingException
  {
    final Section s = (Section) getElement();
    return LayoutControllerUtil.processFlowOperations
        (fc, s.getOperationAfter());
  }

  /**
   * Joins with a delegated process flow. This is generally called from a child
   * flow and should *not* (I mean it!) be called from outside. If you do,
   * you'll suffer.
   *
   * @param flowController the flow controller of the parent.
   * @return the joined layout controller that incorperates all changes from the
   *         delegate.
   */
  public LayoutController join(final FlowController flowController)
  {
    final Node[] nodes = getNodes();
    int index = getIndex() + 1;
    for (; index < nodes.length; index++)
    {
      final Node node = nodes[index];
      if (node.isEnabled())
      {
        break;
      }
    }

    if (index < nodes.length)
    {
      final SectionLayoutController derived = (SectionLayoutController) clone();
      derived.setProcessingState(ElementLayoutController.OPENED);
      derived.setFlowController(flowController);
      derived.setIndex(index);
      return derived;
    }

    final SectionLayoutController derived = (SectionLayoutController) clone();
    derived.setProcessingState(ElementLayoutController.FINISHING);
    derived.setFlowController(flowController);
    return derived;
  }

  public Node[] getNodes()
  {
    if (nodes == null)
    {
      final Section s = (Section) getElement();
      nodes = s.getNodeArray();
    }
    return nodes;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(final int index)
  {
    this.index = index;
  }
}
