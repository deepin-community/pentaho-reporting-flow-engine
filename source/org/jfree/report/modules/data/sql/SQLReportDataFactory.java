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
 * $Id: SQLReportDataFactory.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.data.sql;

import java.sql.Connection;
import java.util.HashMap;

import org.jfree.report.DataSet;
import org.jfree.report.ReportData;
import org.jfree.report.ReportDataFactoryException;

/**
 * Creation-Date: 19.02.2006, 17:37:33
 *
 * @author Thomas Morgner
 */
public class SQLReportDataFactory extends SimpleSQLReportDataFactory
{
  private HashMap querymappings;

  public SQLReportDataFactory(final Connection connection)
  {
    super(connection);
    querymappings = new HashMap();
  }


  public SQLReportDataFactory(final ConnectionProvider connectionProvider)
  {
    super(connectionProvider);
    querymappings = new HashMap();
  }

  public void setQuery(String name, String queryString)
  {
    if (queryString == null)
    {
      querymappings.remove(name);
    }
    else
    {
      querymappings.put(name, queryString);
    }
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The
   * Parameterset given here may contain more data than actually needed.
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
    if (query == null)
    {
      throw new NullPointerException("Query is null.");
    }
    final String realQuery = getQuery(query);
    if (realQuery == null)
    {
      throw new ReportDataFactoryException("Query '" + query + "' is not recognized.");
    }
    return super.queryData(realQuery, parameters);
  }

  public String getQuery(String name)
  {
    return (String) querymappings.get(name);
  }

  public String[] getQueryNames()
  {
    return (String[]) querymappings.keySet().toArray(
            new String[querymappings.size()]);
  }

}
