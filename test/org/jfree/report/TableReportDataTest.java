package org.jfree.report;

import javax.swing.table.DefaultTableModel;

/**
 * Creation-Date: 10.06.2007, 17:13:33
 *
 * @author Thomas Morgner
 */
public class TableReportDataTest
{
  public void testSingleLineData()
      throws ReportDataFactoryException, DataSourceException
  {
    final TableReportDataFactory factory = new TableReportDataFactory();
    factory.addTable("default", new DefaultTableModel(1, 1));

    final ReportData reportData = factory.queryData("default", null);
    if (reportData.isReadable())
    {
      throw new IllegalStateException();
    }
    if (reportData.isAdvanceable() == false)
    {
      throw new IllegalStateException();
    }

    final boolean b = reportData.next();
    if (b == false)
    {
      throw new IllegalStateException();
    }

    if (reportData.isReadable() == false)
    {
      throw new IllegalStateException();
    }
    if (reportData.isAdvanceable())
    {
      throw new IllegalStateException();
    }

    if (reportData.next())
    {
      throw new IllegalStateException();
    }
  }

  public void testEmptyData()
      throws ReportDataFactoryException, DataSourceException
  {
    final TableReportDataFactory factory = new TableReportDataFactory();
    factory.addTable("default", new DefaultTableModel(0, 0));

    final ReportData reportData = factory.queryData("default", null);
    if (reportData.isReadable())
    {
      throw new IllegalStateException();
    }
    if (reportData.isAdvanceable())
    {
      throw new IllegalStateException();
    }

    final boolean b = reportData.next();
    if (b)
    {
      throw new IllegalStateException();
    }
  }

  public static void main(String[] args)
      throws ReportDataFactoryException, DataSourceException
  {
    new TableReportDataTest().testEmptyData();
  }
}
