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
 * $Id: AbstractExpression.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.expressions;

import java.util.Locale;

import org.jfree.report.DataRow;
import org.jfree.report.flow.ReportContext;
import org.jfree.report.flow.ReportStructureRoot;
import org.jfree.report.i18n.ResourceBundleFactory;
import org.jfree.report.structure.Element;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * A baseclass for simple, non-positionally parametrized expressions.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractExpression implements Expression
{
  private transient ExpressionRuntime runtime;
  private String name;
  private boolean deepTraversing;
  private boolean precompute;
  private boolean preserve;

  protected AbstractExpression()
  {
  }

  /**
   * Returns the name of the expression. An expression without a name cannot be
   * referenced from outside the element.
   *
   * @return the function name.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Sets the name of the expression.
   *
   * @param name the name.
   */
  public void setName(final String name)
  {
    this.name = name;
  }

  /**
   * Clones the expression, expression should be reinitialized after the
   * cloning. <P> Expression maintain no state, cloning is done at the beginning
   * of the report processing to disconnect the used expression from any other
   * object space.
   *
   * @return A clone of this expression.
   * @throws CloneNotSupportedException this should never happen.
   */
  public Object clone() throws CloneNotSupportedException
  {
    return (AbstractExpression) super.clone();
  }

  /**
   * Return a new instance of this expression. The copy is initialized and uses
   * the same parameters as the original, but does not share any objects.
   *
   * @return a copy of this function.
   */
  public Expression getInstance()
  {
    try
    {
      final AbstractExpression abstractExpression = (AbstractExpression) clone();
      abstractExpression.runtime = null;
      return abstractExpression;
    }
    catch (CloneNotSupportedException cne)
    {
      return null;
    }
  }

  /**
   * Defines the DataRow used in this expression. The dataRow is set when the
   * report processing starts and can be used to access the values of functions,
   * expressions and the reports datasource.
   *
   * @param runtime the runtime information for the expression
   */
  public void setRuntime(final ExpressionRuntime runtime)
  {
    this.runtime = runtime;
  }

  public ExpressionRuntime getRuntime()
  {
    return runtime;
  }

  /**
   * Returns the current {@link DataRow}.
   *
   * @return the data row.
   */
  protected DataRow getDataRow()
  {
    if (runtime == null)
    {
      return null;
    }
    return runtime.getDataRow();
  }

  protected ResourceBundleFactory getResourceBundleFactory()
  {
    if (runtime == null)
    {
      return null;
    }
    return runtime.getResourceBundleFactory();
  }

  protected Configuration getReportConfiguration()
  {
    if (runtime == null)
    {
      return null;
    }
    return runtime.getConfiguration();
  }

  protected Locale getParentLocale ()
  {
    if (runtime == null)
    {
      return null;
    }

    final Object declaringParent = runtime.getDeclaringParent();
    if (declaringParent instanceof Element)
    {
      final Element declaringElement = (Element) declaringParent;
      return declaringElement.getLocale();
    }

    final ReportContext reportContext = runtime.getReportContext();
    final ReportStructureRoot reportStructureRoot = reportContext.getReportStructureRoot();
    return reportStructureRoot.getLocale();
  }

  public boolean isPrecompute()
  {
    return precompute;
  }

  public void setPrecompute(final boolean precompute)
  {
    this.precompute = precompute;
  }

  public boolean isDeepTraversing()
  {
    return deepTraversing;
  }

  public void setDeepTraversing(final boolean deepTraversing)
  {
    this.deepTraversing = deepTraversing;
  }

  public boolean isPreserve()
  {
    return preserve;
  }

  public void setPreserve(final boolean preserve)
  {
    this.preserve = preserve;
  }
}
