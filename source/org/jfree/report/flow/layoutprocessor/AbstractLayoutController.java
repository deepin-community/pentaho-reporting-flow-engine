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
 * $Id: AbstractLayoutController.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow.layoutprocessor;

import org.jfree.report.flow.FlowController;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.ReportProcessingException;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 * @since 05.03.2007
 */
public abstract class AbstractLayoutController implements LayoutController
{
  private FlowController flowController;
  private LayoutController parent;
  private Object node;
  private boolean initialized;

  protected AbstractLayoutController()
  {
  }

  /**
   * Retrieves the parent of this layout controller. This allows childs to query
   * their context.
   *
   * @return the layout controller's parent to <code>null</code> if there is no
   * parent.
   */
  public LayoutController getParent()
  {
    return parent;
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
    if (initialized == true)
    {
      throw new IllegalStateException();
    }

    this.initialized = true;
    this.node = node;
    this.flowController = flowController;
    this.parent = parent;
  }

  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException("Clone failed?");
    }
  }


  public FlowController getFlowController()
  {
    return flowController;
  }


  public Object getNode()
  {
    return node;
  }


  public boolean isInitialized()
  {
    return initialized;
  }

  /**
   * Derives a copy of this controller that is suitable to perform a
   * precomputation.
   *
   * @param fc
   * @return
   */
  public LayoutController createPrecomputeInstance(FlowController fc)
  {
    final AbstractLayoutController lc = (AbstractLayoutController) clone();
    lc.flowController = fc;
    return lc;
  }
}
