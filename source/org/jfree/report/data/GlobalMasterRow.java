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
 * $Id: GlobalMasterRow.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.data;

import org.jfree.report.DataRow;
import org.jfree.report.DataSourceException;
import org.jfree.report.flow.ReportContext;

/**
 * This data row holds all statefull information from the datasources of the
 * report.
 * <p/>
 * When doing subreports, a datarow only has access to its own dataset and the
 * columns from the next direct subreport, which have been marked as exported.
 *
 * @author Thomas Morgner
 */
public final class GlobalMasterRow
{
  // private StaticDataRow previousRow;
  private ReportDataRow reportDataRow;
  private ParameterDataRow parameterDataRow;
  private ExpressionDataRow expressionDataRow;
  private GlobalMasterRow parentDataRow;
  private FastGlobalView globalView;
  private ImportedVariablesDataRow importedDataRow;

  private GlobalMasterRow()
  {
  }

  public static GlobalMasterRow createReportRow(final ReportContext reportContext)
  {
    GlobalMasterRow gmr = new GlobalMasterRow();
    gmr.globalView = new FastGlobalView();
    gmr.expressionDataRow = new ExpressionDataRow(gmr, reportContext, 10);
    return gmr;
  }

  public static GlobalMasterRow createReportRow(final GlobalMasterRow parentRow,
                                                final ReportContext reportContext)
  {
    GlobalMasterRow gmr = createReportRow(reportContext);
    gmr.parentDataRow = parentRow;
    return gmr;
  }

  public ExpressionDataRow getExpressionDataRow()
  {
    return expressionDataRow;
  }

  public ReportDataRow getReportDataRow()
  {
    return reportDataRow;
  }

  public void setReportDataRow(final ReportDataRow reportDataRow)
          throws DataSourceException
  {
    if (this.reportDataRow != null)
    {
      final int dataColCount = this.reportDataRow.getColumnCount();
      for (int i = dataColCount - 1; i >= 0; i--)
      {
        final String columnName = this.reportDataRow.getColumnName(i);
        if (columnName != null)
        {
          globalView.removeColumn(columnName);
        }
      }
    }

    this.reportDataRow = reportDataRow;

    if (reportDataRow != null)
    {
      final boolean readable = reportDataRow.getReportData().isReadable();
      final int dataColCount = reportDataRow.getColumnCount();
      for (int i = 0; i < dataColCount; i++)
      {
        final String columnName = reportDataRow.getColumnName(i);
        if (columnName != null)
        {
          if (readable)
          {
            final Object columnValue = reportDataRow.get(i);
            globalView.putField(columnName, columnValue, false);
          }
          else
          {
            globalView.putField(columnName, null, false);
          }
        }
      }
    }
  }

  public ParameterDataRow getParameterDataRow()
  {
    return parameterDataRow;
  }

  public void setParameterDataRow(final ParameterDataRow parameterDataRow)
          throws DataSourceException
  {
    if (this.parameterDataRow != null)
    {
      final int parameterCount = this.parameterDataRow.getColumnCount();
      for (int i = parameterCount - 1; i >= 0; i--)
      {
        final String columnName = this.parameterDataRow.getColumnName(i);
        if (columnName != null)
        {
          globalView.removeColumn(columnName);
        }
      }
    }

    this.parameterDataRow = parameterDataRow;

    if (parameterDataRow != null)
    {
      final int parameterCount = parameterDataRow.getColumnCount();
      for (int i = 0; i < parameterCount; i++)
      {
        final String columnName = parameterDataRow.getColumnName(i);
        if (columnName != null)
        {
          final Object columnValue = parameterDataRow.get(i);
          globalView.putField(columnName, columnValue, false);
        }
      }
    }
  }

  public GlobalMasterRow getParentDataRow()
  {
    return parentDataRow;
  }

  public ImportedVariablesDataRow getImportedDataRow()
  {
    return importedDataRow;
  }

  public void setExportedDataRow(final ImportedVariablesDataRow importedDataRow)
          throws DataSourceException
  {
    if (importedDataRow != null)
    {
      final int parameterCount = importedDataRow.getColumnCount();
      for (int i = parameterCount - 1; i >= 0; i--)
      {
        final String columnName = importedDataRow.getColumnName(i);
        if (columnName != null)
        {
          globalView.removeColumn(columnName);
        }
      }
    }

    this.importedDataRow = importedDataRow;
    if (importedDataRow != null)
    {
      final int parameterCount = importedDataRow.getColumnCount();
      for (int i = 0; i < parameterCount; i++)
      {
        final String columnName = importedDataRow.getColumnName(i);
        if (columnName != null)
        {
          final Object columnValue = importedDataRow.get(i);
          globalView.putField(columnName, columnValue, false);
        }
      }
    }
  }

  /**
   * Derives an instance of this datarow. That copy is completly disconnected
   * from the original one and no change made to that copy affects the original
   * datarow.
   *
   * @return the derived datarow.
   */
  public GlobalMasterRow derive() throws DataSourceException
  {
    return derive(null);
  }

  private GlobalMasterRow derive(GlobalMasterRow subReportRow)
          throws DataSourceException
  {
    final GlobalMasterRow dataRow = new GlobalMasterRow();
    dataRow.parameterDataRow = parameterDataRow;
    dataRow.reportDataRow = reportDataRow;
    dataRow.expressionDataRow = expressionDataRow.derive(dataRow);
    dataRow.globalView = globalView.derive();
    if (parentDataRow != null)
    {
      dataRow.parentDataRow = parentDataRow.derive(subReportRow);
    }
    dataRow.importedDataRow = importedDataRow;
    return dataRow;
  }

  /**
   * This advances the cursor by one row and updates the flags.
   *
   * @return
   * @throws DataSourceException
   */
  public GlobalMasterRow advance() throws DataSourceException
  {
    return advance(false, null);
  }

  private GlobalMasterRow advance(final boolean deepTraversingOnly,
                                    final GlobalMasterRow subReportRow)
          throws DataSourceException
  {
    GlobalMasterRow dataRow = new GlobalMasterRow();
    dataRow.globalView = globalView.advance();
    dataRow.parameterDataRow = parameterDataRow;

    if (deepTraversingOnly == false && reportDataRow != null)
    {
      dataRow.reportDataRow = reportDataRow.advance();
    }
    else
    {
      dataRow.reportDataRow = reportDataRow;
    }
    dataRow.updateGlobalView();
    if (expressionDataRow != null)
    {
      dataRow.expressionDataRow =
              expressionDataRow.advance(dataRow, deepTraversingOnly);
    }
    if (parentDataRow != null)
    {
      // the parent row should get a grip on our data as well - just for the
      // deep traversing fun and so on ..
      dataRow.parentDataRow = parentDataRow.advance(true, dataRow);
    }
    if (importedDataRow != null)
    {
      if (subReportRow == null)
      {
        throw new NullPointerException();
      }
      dataRow.importedDataRow = importedDataRow.advance(subReportRow);
      dataRow.updateImportedParameterView();
    }
    return dataRow;
  }

  private void updateImportedParameterView() throws DataSourceException
  {
    if (importedDataRow == null)
    {
      return;
    }

    final int parameterCount = importedDataRow.getColumnCount();
    for (int i = 0; i < parameterCount; i++)
    {
      final String columnName = importedDataRow.getColumnName(i);
      if (columnName != null)
      {
        final Object columnValue = importedDataRow.get(i);
        globalView.putField(columnName, columnValue, true);
      }
    }
  }

  /** This updates the global view. */
  private void updateGlobalView() throws DataSourceException
  {
    if (parameterDataRow != null)
    {
      final int parameterCount = parameterDataRow.getColumnCount();
      for (int i = 0; i < parameterCount; i++)
      {
        final String columnName = parameterDataRow.getColumnName(i);
        if (columnName != null)
        {
          final Object columnValue = parameterDataRow.get(i);
          globalView.putField(columnName, columnValue, true);
        }
      }
    }
    if (reportDataRow != null)
    {
      final int dataColCount = reportDataRow.getColumnCount();
      for (int i = 0; i < dataColCount; i++)
      {
        final String columnName = reportDataRow.getColumnName(i);
        if (columnName != null)
        {
          final Object columnValue = reportDataRow.get(i);
          globalView.putField(columnName, columnValue, true);
        }
      }
    }
  }

  public boolean isAdvanceable() throws DataSourceException
  {
    if (reportDataRow == null)
    {
      return false;
    }
    return reportDataRow.isAdvanceable();
  }

  public DataRow getGlobalView()
  {
    return globalView;
  }

  /**
   * A call back method to communicate structural changes back to the master
   * rows. (This is only called from the expression row, as all other datarows
   * are static).
   *
   * @param chEvent
   */
  public void dataRowChanged(final MasterDataRowChangeEvent chEvent)
          throws DataSourceException
  {
    // rebuild the global view and tracks changes ..
    final int type = chEvent.getType();
    if (type == MasterDataRowChangeEvent.COLUMN_ADDED)
    {
      globalView.putField(chEvent.getColumnName(), chEvent.getColumnValue(), false);
    }
    else if (type == MasterDataRowChangeEvent.COLUMN_UPDATED)
    {
      globalView.putField(chEvent.getColumnName(), chEvent.getColumnValue(), true);
    }
    else if (type == MasterDataRowChangeEvent.COLUMN_REMOVED)
    {
      globalView.removeColumn(chEvent.getColumnName());
    }
  }
}
