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
 * $Id: StaticExpressionRuntimeData.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.data;

import org.jfree.report.ReportData;
import org.jfree.report.flow.ReportContext;
import org.jfree.report.i18n.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * This class holds all expression-runtime information, which are known when
 * the expression gets added to the datarow. Once added, they won't change
 * anymore.
 *
 * @author Thomas Morgner
 */
public class StaticExpressionRuntimeData
{
  private Object declaringParent;
  private Configuration configuration;
  private ReportData data;
  private int currentRow;
  private ReportContext reportContext;

  public StaticExpressionRuntimeData()
  {
  }

  public int getCurrentRow()
  {
    return currentRow;
  }

  public void setCurrentRow(final int currentRow)
  {
    this.currentRow = currentRow;
  }

  public ReportData getData()
  {
    return data;
  }

  public void setData(final ReportData data)
  {
    this.data = data;
  }

  public ResourceBundleFactory getResourceBundleFactory()
  {
    return reportContext.getResourceBundleFactory();
  }

  public void setDeclaringParent(final Object declaringParent)
  {
    this.declaringParent = declaringParent;
  }

  public void setConfiguration(final Configuration configuration)
  {
    this.configuration = configuration;
  }

  public Object getDeclaringParent()
  {
    return declaringParent;
  }

  public Configuration getConfiguration()
  {
    return configuration;
  }

  public ReportContext getReportContext()
  {
    return reportContext;
  }

  public void setReportContext(final ReportContext reportContext)
  {
    this.reportContext = reportContext;
  }
}
