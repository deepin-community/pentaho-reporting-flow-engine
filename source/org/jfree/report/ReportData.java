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
 * $Id: ReportData.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report;

/**
 * A report data source is a ordered set of rows. For a report, we assume that
 * the report dataset does not change while the report is processed. Concurrent
 * updates will invalidate the whole precomputed layout.
 *
 * A report dataset will be accessed in a linear fashion. On certain points, the
 * cursor will be reset to the a previously read position, and processing the
 * data will restart from there. It is guaranteed, that the cursor will never
 * be set to a row that is beyond the last row that has been read with 'next()'.
 *
 * If the cursor is out of range, any call to get must return 'null'. 
 *
 * @author Thomas Morgner
 */
public interface ReportData extends DataSet
{
  public static final int BEFORE_FIRST_ROW = 0;

  public int getCursorPosition() throws DataSourceException;

  /**
   * Moves the cursor back to an already visited position. Calling this method
   * for an row number that has not yet been read using 'next' is undefined,
   * whether that call succeeds is implementation dependent.
   *
   * Calls to position zero (aka BEFORE_FIRST_ROW) will always succeeed (unless there is a physical
   * error, which invalidated the whole report-data object).
   *
   * @param cursor
   * @return true, if moving the cursor succeeded, false otherwise.
   * @throws DataSourceException
   */
  public boolean setCursorPosition(int cursor) throws DataSourceException;

  /**
   * This operation checks, whether a call to next will be likely to succeed.
   * If there is a next data row, this should return true.
   *
   * @return
   * @throws DataSourceException
   */
  public boolean isAdvanceable () throws DataSourceException;

  /**
   * This method produces the same result as 'setCursorPosition(getCursorPosition() + 1);'
   *
   * @return
   * @throws DataSourceException
   */
  public boolean next() throws DataSourceException;

  /**
   * Closes the datasource. This should be called at the end of each report
   * processing run. Whether this closes the underlying data-source backend
   * depends on the ReportDataFactory. Calling 'close()' on the ReportDataFactory
   * *must* close all report data objects.
   *
   * @throws DataSourceException
   */
  public void close() throws DataSourceException;

  /**
   * Checks, whether this report-data instance is currently readable. A report-data instance cannot be
   * readable if it is positioned before the first row. (The look-ahead system of 'isAdvanceable()' will
   * prevent that the datasource is positioned behind the last row.)
   *
   * @return true, if the datarow is valid, false otherwise.
   * @throws DataSourceException
   */
  public boolean isReadable() throws DataSourceException;
}
