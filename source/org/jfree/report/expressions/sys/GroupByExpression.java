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
 * $Id: GroupByExpression.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.expressions.sys;

import java.util.ArrayList;
import java.util.Arrays;

import org.jfree.report.DataFlags;
import org.jfree.report.DataRow;
import org.jfree.report.DataSourceException;
import org.jfree.report.expressions.AbstractExpression;

/**
 * Creation-Date: 08.10.2006, 16:28:37
 *
 * @author Thomas Morgner
 */
public class GroupByExpression extends AbstractExpression
{
  private ArrayList fields;
  private transient String[] fieldsCached;

  public GroupByExpression()
  {
    this.fields = new ArrayList();
  }

  /**
   * Return the current expression value. <P> The value depends (obviously) on
   * the expression implementation.
   *
   * @return the value of the function.
   */
  public Object computeValue() throws DataSourceException
  {
    final DataRow dr = getDataRow();
    final String[] columns = getField();
    for (int i = 0; i < columns.length; i++)
    {
      String column = columns[i];
      DataFlags df = dr.getFlags(column);
      if (df == null)
      {
        // invalid column or invalid implementation ...
        continue;
      }
      if (df.isChanged())
      {
        //Log.debug ("Field: " + df.getName() + " has changed to " + df.getValue());
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;
  }

  public void setField (final int index, final String field)
  {
    if (fields.size() == index)
    {
      fields.add(field);
    }
    else
    {
      fields.set(index, field);
    }
    fieldsCached = null;
  }

  public String getField (final int index)
  {
    return (String) fields.get(index);
  }

  public int getFieldCount ()
  {
    return fields.size();
  }

  public String[] getField ()
  {
    if (fieldsCached == null)
    {
      fieldsCached = (String[]) fields.toArray(new String[fields.size()]);
    }
    return (String[]) fieldsCached.clone();
  }

  public void setField (final String[] fields)
  {
    this.fields.clear();
    this.fields.addAll(Arrays.asList(fields));
    this.fieldsCached = (String[]) fields.clone();
  }

  public Object clone() throws CloneNotSupportedException
  {
    GroupByExpression co = (GroupByExpression) super.clone();
    co.fields = (ArrayList) fields.clone();
    co.fieldsCached = fieldsCached;
    return co;
  }
}
