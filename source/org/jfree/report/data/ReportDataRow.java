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
 * $Id: ReportDataRow.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jfree.report.DataFlags;
import org.jfree.report.DataRow;
import org.jfree.report.DataSet;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportData;
import org.jfree.report.ReportDataFactory;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.util.IntegerCache;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 20.02.2006, 15:32:32
 *
 * @author Thomas Morgner
 */
public final class ReportDataRow implements DataRow
{
  private Map nameCache;
  private DataFlags[] data;
  private ReportData reportData;
  private int cursor;

  private ReportDataRow(final ReportData reportData) throws DataSourceException
  {
    if (reportData == null)
    {
      throw new NullPointerException();
    }

    synchronized (reportData)
    {
      this.reportData = reportData;

      reportData.setCursorPosition(ReportData.BEFORE_FIRST_ROW);
      final boolean readable;
      if (reportData.isAdvanceable())
      {
        readable = reportData.next() && reportData.isReadable();
        cursor = reportData.getCursorPosition();
      }
      else
      {
        readable = false;
        cursor = 0;
      }

      final HashMap nameCache = new HashMap();
      final int columnCount = reportData.getColumnCount();
      this.data = new DataFlags[columnCount];

      for (int i = 0; i < columnCount; i++)
      {
        final String columnName = reportData.getColumnName(i);
        if (columnName != null)
        {
          nameCache.put(columnName, IntegerCache.getInteger(i));
        }

        if (readable)
        {
          final Object value = reportData.get(i);
          this.data[i] = new DefaultDataFlags(columnName, value, true);
        }
        else
        {
          this.data[i] = new DefaultDataFlags(columnName, null, true);
        }
      }
      this.nameCache = Collections.unmodifiableMap(nameCache);
    }
  }

  private ReportDataRow(final ReportData reportData,
                        final ReportDataRow reportDataRow)
      throws DataSourceException
  {
    if (reportData == null)
    {
      throw new NullPointerException();
    }

    if (reportDataRow == null)
    {
      throw new NullPointerException();
    }

    synchronized (reportData)
    {
      this.reportData = reportData;
      this.cursor = reportData.getCursorPosition();
      final int columnCount = reportData.getColumnCount();
      this.data = new DataFlags[columnCount];

      for (int i = 0; i < columnCount; i++)
      {
        final String columnName = reportData.getColumnName(i);
        final Object value = reportData.get(i);
        final boolean changed = ObjectUtilities.equal(value, reportDataRow.get(i));
        this.data[i] = new DefaultDataFlags(columnName, value, changed);
      }
      this.nameCache = reportDataRow.nameCache;
    }
  }

  public static ReportDataRow createDataRow(final ReportDataFactory dataFactory,
                                            final String query,
                                            final DataSet parameters)
      throws DataSourceException, ReportDataFactoryException
  {
    final ReportData reportData = dataFactory.queryData(query, parameters);
    return new ReportDataRow(reportData);
  }

  /**
   * Returns the value of the expression or column in the tablemodel using the given column number as index. For
   * functions and expressions, the <code>getValue()</code> method is called and for columns from the tablemodel the
   * tablemodel method <code>getValueAt(row, column)</code> gets called.
   *
   * @param col the item index.
   * @return the value.
   * @throws IllegalStateException if the datarow detected a deadlock.
   */
  public Object get(final int col) throws DataSourceException
  {
    return data[col].getValue();
  }

  /**
   * Returns the value of the function, expression or column using its specific name. The given name is translated into
   * a valid column number and the the column is queried. For functions and expressions, the <code>getValue()</code>
   * method is called and for columns from the tablemodel the tablemodel method <code>getValueAt(row, column)</code>
   * gets called.
   *
   * @param col the item index.
   * @return the value.
   * @throws IllegalStateException if the datarow detected a deadlock.
   */
  public Object get(final String col) throws DataSourceException
  {
    final Integer colIdx = (Integer) nameCache.get(col);
    if (colIdx == null)
    {
      throw new DataSourceException
          ("Invalid name specified. There is no such column.");
    }

    return data[colIdx.intValue()].getValue();
  }

  /**
   * Returns the name of the column, expression or function. For columns from the tablemodel, the tablemodels
   * <code>getColumnName</code> method is called. For functions, expressions and report properties the assigned name is
   * returned.
   *
   * @param col the item index.
   * @return the name.
   */
  public String getColumnName(final int col)
  {
    return data[col].getName();
  }

  /**
   * Returns the number of columns, expressions and functions and marked ReportProperties in the report.
   *
   * @return the item count.
   */
  public int getColumnCount()
  {
    return data.length;
  }

  public DataFlags getFlags(final String col) throws DataSourceException
  {
    final Integer colIdx = (Integer) nameCache.get(col);
    if (colIdx == null)
    {
      throw new DataSourceException
          ("Invalid name specified. There is no such column.");
    }

    return data[colIdx.intValue()];
  }

  public DataFlags getFlags(final int col)
  {
    return data[col];
  }

  /**
   * Advances to the next row and attaches the given master row to the objects contained in that client data row.
   *
   * @param master
   * @return
   */
  public ReportDataRow advance() throws DataSourceException
  {
    synchronized (reportData)
    {
      if (reportData.getCursorPosition() != cursor)
      {
        // directly go to the position we need.
        if (reportData.setCursorPosition(cursor + 1) == false)
        {
          throw new DataSourceException("Unable to advance cursor position");
        }
      }
      else
      {
        if (reportData.next() == false)
        {
          throw new DataSourceException("Unable to advance cursor position");
        }
      }
      return new ReportDataRow(reportData, this);
    }
  }

  public boolean isAdvanceable() throws DataSourceException
  {
    synchronized (reportData)
    {
      if (reportData.getCursorPosition() != cursor)
      {
        // directly go to the position we need.
        if (reportData.setCursorPosition(cursor) == false)
        {
          return false;
        }
      }
      return reportData.isAdvanceable();
    }
  }

  public ReportData getReportData()
  {
    return reportData;
  }

  public int getCursor()
  {
    return cursor;
  }
}
