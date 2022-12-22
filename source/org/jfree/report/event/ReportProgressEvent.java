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
 * $Id: ReportProgressEvent.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.event;

import java.util.EventObject;

/**
 * Creation-Date: 15.11.2006, 21:02:25
 *
 * @author Thomas Morgner
 */
public class ReportProgressEvent extends EventObject
{
  public static final int COMPUTING_LAYOUT = 0;
  public static final int PRECOMPUTING_VALUES = 1;
  public static final int PAGINATING = 2;
  public static final int GENERATING_CONTENT = 3;

  private int activity;
  private int row;
  private int page;

  public ReportProgressEvent(final Object source,
                             final int activity,
                             final int row,
                             final int page)
  {
    super(source);
    this.page = page;
    this.activity = activity;
    this.row = row;
  }

  public int getRow()
  {
    return row;
  }

  public int getActivity()
  {
    return activity;
  }

  public int getPage()
  {
    return page;
  }
}
