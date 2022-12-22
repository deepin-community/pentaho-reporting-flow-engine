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
 * $Id: PrecomputedExpressionSlot.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.data;

import org.jfree.report.DataRow;
import org.jfree.report.DataSourceException;

/**
 * Creation-Date: 25.11.2006, 15:18:58
 *
 * @author Thomas Morgner
 */
public class PrecomputedExpressionSlot implements ExpressionSlot
{
  private String name;
  private Object value;
  private boolean preserve;

  public PrecomputedExpressionSlot(final String name,
                                   final Object value,
                                   final boolean preserve)
  {
    this.preserve = preserve;
    this.name = name;
    this.value = value;
  }

  public Object getValue() throws DataSourceException
  {
    return value;
  }

  public void advance() throws DataSourceException
  {
    // does nothing ...
  }

  public void updateDataRow(DataRow dataRow)
  {
    // does nothing ...
  }

  public String getName()
  {
    return name;
  }

  public boolean isDeepTraversing()
  {
    return false;
  }

  /**
   * Returns a clone of the object.
   *
   * @return A clone.
   * @throws CloneNotSupportedException if cloning is not supported for some
   *                                    reason.
   */
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  public boolean isPreserve()
  {
    return preserve;
  }

}
