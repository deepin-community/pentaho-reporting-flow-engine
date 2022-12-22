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
 * $Id: SQLReportData.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.data.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.jfree.report.DataSourceException;
import org.jfree.report.ReportData;

/**
 * Creation-Date: 19.02.2006, 17:37:42
 *
 * @author Thomas Morgner
 */
public class SQLReportData implements ReportData
{
  private ResultSet resultSet;
  private int rowCount;
  private int columnCount;
  private int cursor;
  private String[] columnNames;
  private boolean labelMapping;

  public SQLReportData(final ResultSet resultSet,
                       final boolean labelMapping)
      throws SQLException, DataSourceException
  {
    if (resultSet == null)
    {
      throw new NullPointerException();
    }
    if (resultSet.getType() == ResultSet.TYPE_FORWARD_ONLY)
    {
      throw new IllegalArgumentException();
    }
    this.resultSet = resultSet;
    this.labelMapping = labelMapping;

    if (resultSet.last())
    {
      rowCount = resultSet.getRow();
    }
    else
    {
      rowCount = 0;
    }

    final ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
    columnCount = resultSetMetaData.getColumnCount();
    columnNames = new String[columnCount];
    for (int i = 1; i <= columnCount; i++)
    {
      if (labelMapping)
      {
        columnNames[i - 1] = resultSetMetaData.getColumnLabel(i);
      }
      else
      {
        columnNames[i - 1] = resultSetMetaData.getColumnName(i);
      }
    }

    if (resultSet.first() == false)
    {
      throw new DataSourceException("Unable to reset the dataset.");
    }
    cursor = 0;
  }

  public boolean isLabelMapping()
  {
    return labelMapping;
  }

  public int getRowCount() throws DataSourceException
  {
    return rowCount;
  }

  /**
   * This operation checks, whether a call to next will be likely to succeed. If
   * there is a next data row, this should return true.
   *
   * @return
   * @throws org.jfree.report.DataSourceException
   *
   */
  public boolean isAdvanceable() throws DataSourceException
  {
    return cursor < rowCount;
  }

  public int getColumnCount() throws DataSourceException
  {
    return columnCount;
  }

  public boolean setCursorPosition(final int row) throws DataSourceException
  {
    if (row < 0)
    {
      throw new DataSourceException("Negative row number is not valid");
    }
    if (row > rowCount)
    {
      return false;
      // throw new DataSourceException("OutOfBounds:");
    }

    try
    {
      if (cursor == 0)
      {
        resultSet.beforeFirst();
        return true;
      }

      if (resultSet.absolute(row))
      {
        cursor = row;
        return true;
      }
      return false;
      //throw new DataSourceException("Unable to scroll the resultset.");
    }
    catch (SQLException e)
    {
      throw new DataSourceException("Failed to move the cursor: ", e);
    }
  }

  public boolean next() throws DataSourceException
  {
    try
    {
      if (resultSet.next())
      {
        cursor += 1;
        return true;
      }
      else
      {
        return false;
      }
    }
    catch (SQLException e)
    {
      throw new DataSourceException("Failed to move the cursor: ", e);
    }
  }

  public void close() throws DataSourceException
  {
    try
    {
      resultSet.close();
    }
    catch (SQLException e)
    {
      throw new DataSourceException("Failed to close the resultset: ", e);
    }
  }

  public String getColumnName(final int column) throws DataSourceException
  {
    return columnNames[column];
  }

  public Object get(final int column) throws DataSourceException
  {
    if (isReadable() == false)
    {
      throw new DataSourceException("Cannot read from this datasource");
    }

    try
    {
      final Object retval = resultSet.getObject(column + 1);
      if (resultSet.wasNull())
      {
        return null;
      }
      return retval;
    }
    catch (SQLException e)
    {
      throw new DataSourceException("Failed to query data", e);
    }
  }

  public int getCursorPosition() throws DataSourceException
  {
    return cursor;
  }

  public boolean isReadable() throws DataSourceException
  {
    return cursor > 0 && rowCount > 0;
  }
}
