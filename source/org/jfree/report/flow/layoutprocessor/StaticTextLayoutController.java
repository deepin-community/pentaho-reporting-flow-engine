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
 * $Id: StaticTextLayoutController.java 10756 2009-12-02 15:58:24Z tmorgner $
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
import org.jfree.report.structure.StaticText;

/**
 * Creation-Date: 24.11.2006, 15:06:56
 *
 * @author Thomas Morgner
 */
public class StaticTextLayoutController extends AbstractLayoutController
{
  public static final int NOT_STARTED = 0;
  public static final int FINISHED = 2;

  private int state;

  public StaticTextLayoutController()
  {
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
      throws DataSourceException, ReportDataFactoryException, ReportProcessingException
  {
    if (state != StaticTextLayoutController.NOT_STARTED)
    {
      throw new IllegalStateException();
    }

    final StaticText text = (StaticText) getNode();
    target.processText(text.getText());

    final StaticTextLayoutController derived = (StaticTextLayoutController) clone();
    derived.state = StaticTextLayoutController.FINISHED;
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
    throw new UnsupportedOperationException("Static text does not have childs.");
  }

  /**
   * Checks, whether the layout controller would be advanceable. If this method
   * returns true, it is generally safe to call the 'advance()' method.
   *
   * @return true, if the layout controller is advanceable, false otherwise.
   */
  public boolean isAdvanceable()
  {
    return state != StaticTextLayoutController.FINISHED;
  }

  /**
   * Derives a copy of this controller that is suitable to perform a
   * precomputation. The returned layout controller must be independent from
   * the it's anchestor controller.
   *
   * @param fc a new flow controller for the precomputation.
   * @return a copy that is suitable for precomputation.
   */
  public LayoutController createPrecomputeInstance(final FlowController fc)
  {
    throw new UnsupportedOperationException
        ("Static Text does not perform any precomputation.");
  }
}
