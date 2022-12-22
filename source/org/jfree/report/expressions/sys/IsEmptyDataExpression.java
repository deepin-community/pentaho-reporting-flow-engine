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
 * $Id: IsEmptyDataExpression.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.expressions.sys;

import org.jfree.report.DataSourceException;
import org.jfree.report.ReportData;
import org.jfree.report.expressions.AbstractExpression;
import org.jfree.report.expressions.ExpressionRuntime;

/**
 * Creation-Date: 31.01.2006, 18:55:09
 *
 * @author Thomas Morgner
 */
public class IsEmptyDataExpression extends AbstractExpression
{
  public IsEmptyDataExpression()
  {
  }

  /**
   * Return the current expression value. <P> The value depends (obviously) on
   * the expression implementation.
   *
   * @return the value of the function.
   */
  public Object computeValue() throws DataSourceException
  {

    final ExpressionRuntime runtime = getRuntime();
    if (runtime == null)
    {
      return null;
    }

    final ReportData data = runtime.getData();
    if (data == null)
    {
      return null;
    }
    synchronized(data)
    {
      if (data.getCursorPosition() > 0)
      {
        return Boolean.FALSE;
      }
      if (data.isAdvanceable() == false)
      {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
  }
}
