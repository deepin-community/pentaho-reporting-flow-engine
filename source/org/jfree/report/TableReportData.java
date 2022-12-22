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
 * $Id: TableReportData.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report;

import javax.swing.table.TableModel;

/**
 * Creation-Date: 19.02.2006, 17:00:10
 *
 * @author Thomas Morgner
 */
public class TableReportData implements ReportData
{
  private TableModel tableModel;
  private int cursor;
  private int rowMax;
  private int rowMin;
  private int cursorMaxPosition;

  public TableReportData(final TableModel tableModel)
  {
    this(tableModel, 0, tableModel.getRowCount());
  }

  public TableReportData(final TableModel tableModel, final int start, final int length)
  {
    this.tableModel = tableModel;
    this.rowMax = start + length;
    this.rowMin = start;
    this.cursorMaxPosition = length;
  }

  public int getColumnCount() throws DataSourceException
  {
    return tableModel.getColumnCount();
  }

  public boolean isReadable() throws DataSourceException
  {
    return cursor > 0 && cursor <= cursorMaxPosition ;
  }

  public boolean setCursorPosition(final int row) throws DataSourceException
  {
    if (row > cursorMaxPosition)
    {
      return false;
    }
    else if (row < 0)
    {
      return false;
    }
    cursor = row;
    return true;
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
    return cursor < cursorMaxPosition;
  }

  public String getColumnName(final int column) throws DataSourceException
  {
    return tableModel.getColumnName(column);
  }

  public Object get(final int column) throws DataSourceException
  {
    if (isReadable() == false)
    {
      throw new DataSourceException("Unable to read from datasource");
    }
    return tableModel.getValueAt(cursor - 1, column);
  }

  public boolean next() throws DataSourceException
  {
    if (cursor >= cursorMaxPosition)
    {
      return false;
    }

    cursor += 1;
    return true;
  }

  public void close() throws DataSourceException
  {
    // does nothing ...
  }

  public int getCursorPosition() throws DataSourceException
  {
    return cursor;
  }
}
