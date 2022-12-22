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
 * $Id: LayoutExpressionRuntime.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.flow;

import org.jfree.report.DataRow;
import org.jfree.report.ReportData;
import org.jfree.report.expressions.ExpressionRuntime;
import org.jfree.report.i18n.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * Creation-Date: 04.03.2006, 16:41:49
 *
 * @author Thomas Morgner
 */
public class LayoutExpressionRuntime implements ExpressionRuntime
{
  private DataRow dataRow;
  private Configuration configuration;
  private ReportData reportData;
  private Object declaringParent;
  private int currentRow;
  private ReportContext reportContext;

  public LayoutExpressionRuntime()
  {
  }

  public void setCurrentRow(final int currentRow)
  {
    this.currentRow = currentRow;
  }

  public void setDataRow(final DataRow dataRow)
  {
    this.dataRow = dataRow;
  }

  public void setConfiguration(final Configuration configuration)
  {
    this.configuration = configuration;
  }

  public void setData(final ReportData reportData)
  {
    this.reportData = reportData;
  }

  public void setDeclaringParent(final Object declaringParent)
  {
    this.declaringParent = declaringParent;
  }


  /**
   * Returns the datarow.
   *
   * @return
   */
  public DataRow getDataRow()
  {
    return dataRow;
  }

  public Configuration getConfiguration()
  {
    return configuration;
  }

  public ResourceBundleFactory getResourceBundleFactory()
  {
    return reportContext.getResourceBundleFactory();
  }

  /**
   * Returns the report data used in this section. If subreports are used, this
   * does not reflect the complete report data.
   * <p/>
   * All access to the report data must be properly synchronized. Failure to do
   * so may result in funny results. Do not assume that the report data will be
   * initialized on the current cursor positon.
   *
   * @return
   */
  public ReportData getData()
  {
    return reportData;
  }

  public Object getDeclaringParent()
  {
    return declaringParent;
  }

  public int getCurrentRow()
  {
    return currentRow;
  }

  public ReportContext getReportContext()
  {
    return reportContext;
  }

  public void setReportContext (final ReportContext reportContext)
  {
    this.reportContext = reportContext;
  }
}
