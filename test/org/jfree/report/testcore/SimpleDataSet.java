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
 * $Id: SimpleDataSet.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.testcore;

import java.util.ArrayList;

import org.jfree.report.DataSet;

/**
 * Creation-Date: Jan 18, 2007, 6:09:26 PM
 *
 * @author Thomas Morgner
 */
public class SimpleDataSet implements DataSet
{
  private ArrayList names;
  private ArrayList values;

  public SimpleDataSet ()
  {
    names = new ArrayList();
    values = new ArrayList();
  }

  /**
   * Returns the column position of the column, expression or function with the given name
   * or -1 if the given name does not exist in this DataRow.
   *
   * @param name the item name.
   * @return the item index.
   */
  public int findColumn (String name)
  {
    return names.indexOf(name);
  }

  /**
   * Returns the value of the expression or column in the tablemodel using the given
   * column number as index. For functions and expressions, the <code>getValue()</code>
   * method is called and for columns from the tablemodel the tablemodel method
   * <code>getValueAt(row, column)</code> gets called.
   *
   * @param col the item index.
   * @return the value.
   *
   * @throws IllegalStateException if the datarow detected a deadlock.
   */
  public Object get (int col)
  {
    return values.get(col);
  }

  /**
   * Returns the value of the function, expression or column using its specific name. The
   * given name is translated into a valid column number and the the column is queried.
   * For functions and expressions, the <code>getValue()</code> method is called and for
   * columns from the tablemodel the tablemodel method <code>getValueAt(row,
   * column)</code> gets called.
   *
   * @param col the item index.
   * @return the value.
   *
   * @throws IllegalStateException if the datarow detected a deadlock.
   */
  public Object get (String col)
          throws IllegalStateException
  {
    // todo implement me
    int index = names.indexOf(col);
    if (index == -1)
    {
      return null;
    }
    return values.get(index);
  }

  /**
   * Returns the number of columns, expressions and functions and marked ReportProperties
   * in the report.
   *
   * @return the item count.
   */
  public int getColumnCount ()
  {
    return values.size();
  }

  /**
   * Returns the name of the column, expression or function. For columns from the
   * tablemodel, the tablemodels <code>getColumnName</code> method is called. For
   * functions, expressions and report properties the assigned name is returned.
   *
   * @param col the item index.
   * @return the name.
   */
  public String getColumnName (int col)
  {
    return (String) names.get(col);
  }

  public void add (String name, Object value)
  {
    names.add(name);
    values.add(value);
  }

}
