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
 * $Id: TableReportDataFactory.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report;

import java.util.HashMap;
import javax.swing.table.TableModel;

/**
 * Creation-Date: 21.02.2006, 17:59:32
 *
 * @author Thomas Morgner
 */
public class TableReportDataFactory implements ReportDataFactory, Cloneable
{
  private HashMap tables;

  public TableReportDataFactory()
  {
    this.tables = new HashMap();
  }

  public TableReportDataFactory(String name, TableModel tableModel)
  {
    this();
    addTable(name, tableModel);
  }

  public void addTable(String name, TableModel tableModel)
  {
    tables.put(name, tableModel);
  }

  public void removeTable(String name)
  {
    tables.remove(name);
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The
   * Parameterset given here may contain more data than actually needed.
   * <p/>
   * The dataset may change between two calls, do not assume anything!
   *
   * @param query the name of the table.
   * @param parameters are ignored for this factory.
   * @return the report data or null.
   */
  public ReportData queryData(final String query, final DataSet parameters)
          throws ReportDataFactoryException
  {
    TableModel model = (TableModel) tables.get(query);
    if (model == null)
    {
      return null;
    }

    return new TableReportData(model);
  }

  public void open()
  {

  }

  public void close()
  {

  }

  /**
   * Derives a freshly initialized report data factory, which is independend of
   * the original data factory. Opening or Closing one data factory must not
   * affect the other factories.
   *
   * @return
   */
  public ReportDataFactory derive()
  {
    try
    {
      return (ReportDataFactory) clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException("Clone should not fail.");
    }
  }

  public Object clone () throws CloneNotSupportedException
  {
    TableReportDataFactory dataFactory = (TableReportDataFactory) super.clone();
    dataFactory.tables = (HashMap) tables.clone();
    return dataFactory;
  }
}
