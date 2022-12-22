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
 * $Id: ContentElementLayoutController.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow.layoutprocessor;

import org.jfree.report.DataFlags;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.data.DefaultDataFlags;
import org.jfree.report.expressions.Expression;
import org.jfree.report.flow.FlowController;
import org.jfree.report.flow.LayoutExpressionRuntime;
import org.jfree.report.flow.ReportContext;
import org.jfree.report.flow.ReportTarget;
import org.jfree.report.structure.ContentElement;
import org.jfree.report.structure.Node;

/**
 * Creation-Date: 24.11.2006, 15:06:56
 *
 * @author Thomas Morgner
 */
public class ContentElementLayoutController extends ElementLayoutController
{
  public ContentElementLayoutController()
  {
  }

  protected LayoutController processContent(final ReportTarget target)
      throws DataSourceException, ReportProcessingException, ReportDataFactoryException
  {

    final Node node = getElement();
    final FlowController flowController = getFlowController();
    final LayoutExpressionRuntime er =
        LayoutControllerUtil.getExpressionRuntime(flowController, node);
    final ContentElement element = (ContentElement) node;
    final Expression ex = element.getValueExpression();
    final Object value;

    if (ex != null)
    {
      try
      {
        ex.setRuntime(er);
        value = ex.computeValue();
      }
      finally
      {
        ex.setRuntime(null);
      }
    }
    else
    {
      value = null;
    }

    // This should be a very rare case indeed ..
    if (value instanceof DataFlags)
    {
      target.processContent((DataFlags) value);

      final ContentElementLayoutController derived = (ContentElementLayoutController) clone();
      derived.setProcessingState(ElementLayoutController.FINISHING);
      derived.setFlowController(flowController);
      return derived;
    }

    if (value instanceof Node)
    {
      // we explictly allow structural content here.
      // As this might be a very expensive thing, if we
      // keep it in a single state, we continue on a separate state.
      final Node valueNode = (Node) value;
      valueNode.updateParent(node.getParent());
      final ReportContext reportContext = flowController.getReportContext();
      final LayoutControllerFactory layoutControllerFactory =
          reportContext.getLayoutControllerFactory();

      // actually, this is the same as if the element were a
      // child element of a section. The only difference is
      // that there can be only one child, and that there is no
      // direct parent-child direction.

      final ContentElementLayoutController derived =
          (ContentElementLayoutController) clone();
      derived.setProcessingState(ElementLayoutController.WAITING_FOR_JOIN);
      derived.setFlowController(flowController);

      return layoutControllerFactory.create
          (flowController, valueNode, derived);
    }

    if (ex != null)
    {
      target.processContent (new DefaultDataFlags(ex.getName(), value, true));
    }

    final ContentElementLayoutController derived = (ContentElementLayoutController) clone();
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
    final ContentElementLayoutController derived = (ContentElementLayoutController) clone();
    derived.setProcessingState(ElementLayoutController.FINISHING);
    derived.setFlowController(flowController);
    return derived;
  }
}
