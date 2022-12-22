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
 * $Id: RunningExpressionSlot.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.data;

import org.jfree.report.DataRow;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportData;
import org.jfree.report.expressions.Expression;
import org.jfree.report.expressions.ExpressionRuntime;
import org.jfree.report.expressions.Function;
import org.jfree.report.flow.ReportContext;
import org.jfree.report.i18n.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * Creation-Date: 25.11.2006, 15:18:58
 *
 * @author Thomas Morgner
 */
public class RunningExpressionSlot
    implements ExpressionSlot, ExpressionRuntime
{
  private StaticExpressionRuntimeData staticRuntimeData;
  private Expression expression;
  private Object value;
  private String name;
  private boolean queried;
  private DataRow dataRow;

  public RunningExpressionSlot(final Expression expression,
                               final StaticExpressionRuntimeData runtimeData,
                               final PrecomputeNode precomputeNode)
  {
    this.staticRuntimeData = runtimeData;
    this.expression = expression;
    this.name = expression.getName();
    this.expression.setRuntime(this);
    this.expression.setRuntime(null);
  }

  public Expression getExpression()
  {
    return expression;
  }

  public Object getValue() throws DataSourceException
  {
    if (queried == false)
    {
      //noinspection SynchronizeOnNonFinalField
      synchronized (expression)
      {
        expression.setRuntime(this);
        value = expression.computeValue();
        expression.setRuntime(null);
      }
      queried = true;
    }
    return value;
  }

  public String getName()
  {
    return name;
  }

  public DataRow getDataRow()
  {
    return dataRow;
  }

  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  public void updateDataRow(final DataRow dataRow)
  {
    this.dataRow = dataRow;
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
    return staticRuntimeData.getData();
  }

  public Object getDeclaringParent()
  {
    return staticRuntimeData.getDeclaringParent();
  }

  public Configuration getConfiguration()
  {
    return staticRuntimeData.getConfiguration();
  }

  public ResourceBundleFactory getResourceBundleFactory()
  {
    return staticRuntimeData.getResourceBundleFactory();
  }

  public void advance() throws DataSourceException
  {
    if (expression instanceof Function)
    {
      Function f = (Function) expression;
      expression.setRuntime(this);
      expression = f.advance();
      f.setRuntime(null);
      expression.setRuntime(null);
    }
    value = null;
    queried = false;
  }

  public boolean isDeepTraversing()
  {
    return expression.isDeepTraversing();
  }

  public int getCurrentRow()
  {
    return staticRuntimeData.getCurrentRow();
  }

  public ReportContext getReportContext()
  {
    return staticRuntimeData.getReportContext();
  }

  public boolean isPreserve()
  {
    return expression.isPreserve();
  }
}
