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
 * $Id: EmptyReportData.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report;

/**
 * Creation-Date: 22.04.2006, 14:25:04
 *
 * @author Thomas Morgner
 */
public class EmptyReportData implements ReportData
{
  public EmptyReportData()
  {
  }

  public int getCursorPosition() throws DataSourceException
  {
    return 0;
  }

  public boolean isReadable() throws DataSourceException
  {
    return false;
  }

  public boolean setCursorPosition(final int cursor) throws DataSourceException
  {
    if (cursor == ReportData.BEFORE_FIRST_ROW)
    {
      return true;
    }
    return false;
  }

  /**
   * This operation checks, whether a call to next will be likely to succeed. If there is a next data row, this should
   * return true.
   *
   * @return
   * @throws DataSourceException
   *
   */
  public boolean isAdvanceable() throws DataSourceException
  {
    return false;
  }

  public boolean next() throws DataSourceException
  {
    return false;
  }

  public void close() throws DataSourceException
  {

  }

  public int getColumnCount() throws DataSourceException
  {
    return 0;
  }

  public String getColumnName(final int column) throws DataSourceException
  {
    return null;
  }

  public Object get(final int column) throws DataSourceException
  {
    return null;
  }
}
