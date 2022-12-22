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
 * $Id: BufferingLayoutController.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow.layoutprocessor;

import org.jfree.report.DataSourceException;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.flow.FlowController;
import org.jfree.report.flow.ReportTarget;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 * @since 05.03.2007
 */
public abstract class BufferingLayoutController
    extends AbstractLayoutController
{
  private BufferedReportTarget reportTarget;
  private LayoutController delegate;
  private boolean finished;

  protected BufferingLayoutController()
  {
    reportTarget = new BufferedReportTarget();
  }

  /**
   * Advances the processing position.
   *
   * @param target the report target that receives generated events.
   * @return the new layout controller instance representing the new state.
   *
   * @throws DataSourceException        if there was a problem reading data from
   *                                    the datasource.
   * @throws ReportProcessingException  if there was a general problem during
   *                                    the report processing.
   * @throws ReportDataFactoryException if a query failed.
   */
  public LayoutController advance(final ReportTarget target)
      throws DataSourceException, ReportDataFactoryException,
      ReportProcessingException
  {
    reportTarget.setTarget(target);
    if (delegate != null)
    {
      try
      {
        final BufferingLayoutController bc = (BufferingLayoutController) clone();
        bc.delegate = delegate.advance(reportTarget);
        return bc;
      }
      finally
      {
        reportTarget.setTarget(null);
      }
    }

    // write all buffered changes to the real report target.
    reportTarget.close(target);
    if (getParent() == null)
    {
      final BufferingLayoutController bc = (BufferingLayoutController) clone();
      bc.finished = true;
      return bc;
    }

    return joinWithParent();
  }

  /**
   * Joins the layout controller with the parent. This simply calls
   * {@link #join(org.jfree.report.flow.FlowController)} on the parent. A join
   * operation is necessary to propagate changes in the flow-controller to the
   * parent for further processing.
   *
   * @return the joined parent.
   * @throws IllegalStateException if this layout controller has no parent.
   * @throws org.jfree.report.ReportProcessingException
   * @throws org.jfree.report.ReportDataFactoryException
   * @throws org.jfree.report.DataSourceException
   */
  protected LayoutController joinWithParent()
      throws ReportProcessingException, ReportDataFactoryException,
      DataSourceException
  {
    final LayoutController parent = getParent();
    if (parent == null)
    {
      // skip to the next step ..
      throw new IllegalStateException("There is no parent to join with. " +
                                      "This should not happen in a sane environment!");
    }

    return parent.join(getFlowController());
  }


  /**
   * Initializes the layout controller. This method is called exactly once. It
   * is the creators responsibility to call this method.
   * <p/>
   * Calling initialize after the first advance must result in a
   * IllegalStateException.
   *
   * @param node           the currently processed object or layout node.
   * @param flowController the current flow controller.
   * @param parent         the parent layout controller that was responsible for
   *                       instantiating this controller.
   * @throws DataSourceException        if there was a problem reading data from
   *                                    the datasource.
   * @throws ReportProcessingException  if there was a general problem during
   *                                    the report processing.
   * @throws ReportDataFactoryException if a query failed.
   */
  public void initialize(final Object node, final FlowController flowController,
                         final LayoutController parent)
      throws DataSourceException, ReportDataFactoryException,
      ReportProcessingException
  {
    super.initialize(node, flowController, parent);
    delegate = getInitialDelegate();
  }

  protected abstract LayoutController getInitialDelegate();

  public boolean isAdvanceable()
  {
    if (delegate == null)
    {
      return finished == false;
    }
    return delegate.isAdvanceable();
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
      throws ReportProcessingException, DataSourceException
  {
    // the delegation is finished, the element has returned.
    final BufferingLayoutController bc = (BufferingLayoutController) clone();
    bc.delegate = null;
    return bc;
  }

  public Object clone()
  {
    final BufferingLayoutController o = (BufferingLayoutController) super.clone();
    o.reportTarget = (BufferedReportTarget) reportTarget.clone();
    return o;
  }
}
