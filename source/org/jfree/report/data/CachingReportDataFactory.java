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
 * $Id: CachingReportDataFactory.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.jfree.report.DataSet;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportData;
import org.jfree.report.ReportDataFactory;
import org.jfree.report.ReportDataFactoryException;

/**
 * Creation-Date: 19.11.2006, 13:35:45
 *
 * @author Thomas Morgner
 */
public class CachingReportDataFactory implements ReportDataFactory
{
  private static class Parameters implements DataSet
  {
    private Object[] dataStore;
    private String[] nameStore;
    private Integer hashCode;

    protected Parameters(final DataSet dataSet) throws DataSourceException
    {
      final int columnCount = dataSet.getColumnCount();
      dataStore = new Object[columnCount];
      nameStore = new String[columnCount];

      for (int i = 0; i < columnCount; i++)
      {
        nameStore[i] = dataSet.getColumnName(i);
        dataStore[i] = dataSet.get(i);
      }
    }

    public int getColumnCount() throws DataSourceException
    {
      return dataStore.length;
    }

    public String getColumnName(final int column) throws DataSourceException
    {
      return nameStore[column];
    }

    public Object get(final int column) throws DataSourceException
    {
      return dataStore[column];
    }

    public boolean equals(final Object o)
    {
      if (this == o)
      {
        return true;
      }
      if (o == null || getClass() != o.getClass())
      {
        return false;
      }

      final Parameters that = (Parameters) o;

      if (!Arrays.equals(dataStore, that.dataStore))
      {
        return false;
      }
      if (!Arrays.equals(nameStore, that.nameStore))
      {
        return false;
      }

      return true;
    }

    public synchronized int hashCode()
    {
      if (hashCode != null)
      {
        return hashCode.intValue();
      }
      int hashCode = 0;
      for (int i = 0; i < dataStore.length; i++)
      {
        final Object o = dataStore[i];
        if (o != null)
        {
          hashCode = hashCode * 23 + o.hashCode();
        }
        else
        {
          hashCode = hashCode * 23;
        }
      }
      for (int i = 0; i < nameStore.length; i++)
      {
        final Object o = nameStore[i];
        if (o != null)
        {
          hashCode = hashCode * 23 + o.hashCode();
        }
        else
        {
          hashCode = hashCode * 23;
        }
      }
      this.hashCode = new Integer(hashCode);
      return hashCode;
    }
  }

  private HashMap queryCache;

  private ReportDataFactory backend;

  public CachingReportDataFactory(final ReportDataFactory backend)
  {
    if (backend == null)
    {
      throw new NullPointerException();
    }
    this.backend = backend;
    this.queryCache = new HashMap();
  }

  public void open()
  {
    backend.open();
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed.
   * <p/>
   * The dataset may change between two calls, do not assume anything!
   *
   * @param query
   * @param parameters
   * @return
   */
  public ReportData queryData(final String query, final DataSet parameters)
      throws ReportDataFactoryException
  {
    try
    {
      final HashMap parameterCache = (HashMap) queryCache.get(query);
      if (parameterCache == null)
      {
        // totally new query here.
        final HashMap newParams = new HashMap();
        queryCache.put(query, newParams);

        final Parameters params = new Parameters(parameters);
        final ReportData newData = backend.queryData(query, params);
        newParams.put(params, newData);
        newData.setCursorPosition(ReportData.BEFORE_FIRST_ROW);
        return newData;
      }
      else
      {
        // Lookup the parameters ...
        final Parameters params = new Parameters(parameters);
        final ReportData data = (ReportData) parameterCache.get(params);
        if (data != null)
        {
          data.setCursorPosition(ReportData.BEFORE_FIRST_ROW);
          return data;
        }

        final ReportData newData = backend.queryData(query, params);
        parameterCache.put(params, newData);
        newData.setCursorPosition(ReportData.BEFORE_FIRST_ROW);
        return newData;
      }
    }
    catch (DataSourceException e)
    {
      e.printStackTrace();
      throw new ReportDataFactoryException("Failed to query data", e);
    }
  }

  /**
   * Closes the report data factory and all report data instances that have been returned by this instance.
   */
  public void close()
  {
    final Iterator queries = queryCache.values().iterator();
    while (queries.hasNext())
    {
      final HashMap map = (HashMap) queries.next();
      final Iterator dataSets = map.values().iterator();
      while (dataSets.hasNext())
      {
        final ReportData data = (ReportData) dataSets.next();
        try
        {
          data.close();
        }
        catch (DataSourceException e)
        {
          // ignore, we'll finish up anyway ..
        }
      }
    }
    backend.close();
  }

  /**
   * Derives a freshly initialized report data factory, which is independend of the original data factory. Opening or
   * Closing one data factory must not affect the other factories.
   *
   * @return
   */
  public ReportDataFactory derive()
  {
    // If you see that exception, then you've probably tried to use that
    // datafactory from outside of the report processing. You deserve the
    // exception in that case ..
    throw new UnsupportedOperationException
        ("The CachingReportDataFactory cannot be derived.");
  }
}
