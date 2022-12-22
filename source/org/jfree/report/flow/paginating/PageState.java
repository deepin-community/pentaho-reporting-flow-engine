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
 * $Id: PageState.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow.paginating;

import java.io.Serializable;

import org.jfree.report.flow.ReportTargetState;
import org.jfree.report.flow.layoutprocessor.LayoutController;

/**
 * Creation-Date: 11.11.2006, 19:01:53
 *
 * @author Thomas Morgner
 */
public class PageState implements Serializable
{
  private ReportTargetState targetState;
  private LayoutController layoutController;
  private int pageCursor;

  public PageState(final ReportTargetState targetState,
                   final LayoutController layoutController,
                   final int pageCursor)
  {
    this.layoutController = layoutController;
    this.pageCursor = pageCursor;
    this.targetState = targetState;

  }

  public int getPageCursor()
  {
    return pageCursor;
  }

  public ReportTargetState getTargetState()
  {
    return targetState;
  }

  public LayoutController getLayoutController()
  {
    return layoutController;
  }
}
