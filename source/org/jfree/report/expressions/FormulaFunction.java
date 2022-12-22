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
 * $Id: FormulaFunction.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.expressions;

import org.jfree.report.DataSourceException;
import org.jfree.report.flow.ReportContext;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.parser.ParseException;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * Creation-Date: 04.11.2006, 19:24:04
 *
 * @author Thomas Morgner
 */
public class FormulaFunction extends AbstractExpression implements Function
{
  private String formulaNamespace;
  private String formulaExpression;
  private String formula;

  private String initialNamespace;
  private String initialExpression;
  private String initial;
  private transient Formula compiledFormula;
  private boolean initialized;

  public FormulaFunction()
  {
  }

  private synchronized FormulaContext getFormulaContext()
  {
    final ReportContext globalContext = getRuntime().getReportContext();
    return globalContext.getFormulaContext();
  }

  public String getInitial()
  {
    return initial;
  }

  public String getInitialExpression()
  {
    return initialExpression;
  }

  public String getInitialNamespace()
  {
    return initialNamespace;
  }

  public void setInitial(final String initial)
  {
    this.initial = initial;
    if (initial == null)
    {
      initialNamespace = null;
      initialExpression = null;
    }
    else
    {
      final int separator = initial.indexOf(':');
      if (separator <= 0 || ((separator + 1) == initial.length()))
      {
        if (formula.startsWith("="))
        {
          initialNamespace = "report";
          initialExpression = initial.substring(1);
        }
        else
        {
          // error: invalid formula.
          initialNamespace = null;
          initialExpression = null;
        }
      }
      else
      {
        initialNamespace = initial.substring(0, separator);
        initialExpression = initial.substring(separator + 1);
      }
    }
  }


  public String getFormula()
  {
    return formula;
  }

  public String getFormulaNamespace()
  {
    return formulaNamespace;
  }

  public String getFormulaExpression()
  {
    return formulaExpression;
  }

  public void setFormula(final String formula)
  {
    this.formula = formula;
    if (formula == null)
    {
      formulaNamespace = null;
      formulaExpression = null;
    }
    else
    {
      final int separator = formula.indexOf(':');
      if (separator <= 0 || ((separator + 1) == formula.length()))
      {
        if (formula.startsWith("="))
        {
          formulaNamespace = "report";
          formulaExpression = formula.substring(1);
        }
        else
        {
          // error: invalid formula.
          formulaNamespace = null;
          formulaExpression = null;
        }
      }
      else
      {
        formulaNamespace = formula.substring(0, separator);
        formulaExpression = formula.substring(separator + 1);
      }
    }
    this.compiledFormula = null;
  }

  /**
   * When the advance method is called, the function is asked to perform the
   * next step of its computation.
   * <p/>
   * The original function must not be altered during that step (or more
   * correctly, calling advance on the original expression again must not return
   * a different result).
   *
   * @return a copy of the function containing the new state.
   */
  public Function advance() throws DataSourceException
  {
    try
    {
      return (Function) clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new DataSourceException("Unable to derive a new instance");
    }
  }

  private Object computeInitialValue()
  {
    try
    {
      if (initial != null)
      {
        final Formula initFormula = new Formula(initialExpression);
        final ReportFormulaContext context =
            new ReportFormulaContext(getFormulaContext(), getDataRow());
        context.setDeclaringElement(getRuntime().getDeclaringParent());
        try
        {
          initFormula.initialize(context);
          return initFormula.evaluate();
        }
        finally
        {
          context.setDeclaringElement(null);
          context.setDataRow(null);
        }
      }

      // if the code above did not trigger, compute a regular thing ..
      return computeRegularValue();
    }
    catch (Exception e)
    {
      DebugLog.log("Failed to compute the initial value.");
      return null;
    }
  }

  private Object computeRegularValue()
  {
    try
    {
      if (compiledFormula == null)
      {
        compiledFormula = new Formula(formulaExpression);
      }

      final ReportFormulaContext context =
          new ReportFormulaContext(getFormulaContext(), getDataRow());
      context.setDeclaringElement(getRuntime().getDeclaringParent());
      try
      {
        compiledFormula.initialize(context);
        return compiledFormula.evaluate();
      }
      finally
      {
        context.setDeclaringElement(null);
        context.setDataRow(null);
      }
    }
    catch (Exception e)
    {
      DebugLog.log("Failed to compute the regular value.", e);
      return null;
    }
  }

  /**
   * Return the current expression value. <P> The value depends (obviously) on
   * the expression implementation.
   *
   * @return the value of the function.
   */
  public Object computeValue() throws DataSourceException
  {
    try
    {
      if (initialized == false)
      {
        initialized = true;
        return computeInitialValue();
      }
      return computeRegularValue();
    }
    catch (Exception e)
    {
      return null;
    }
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
    final FormulaFunction o = (FormulaFunction) super.clone();
    if (compiledFormula != null)
    {
      o.compiledFormula = (Formula) compiledFormula.clone();
    }
    return o;
  }


  /**
   * Returns the compiled formula. The formula is not connected to a formula
   * context.
   *
   * @return the formula.
   * @throws ParseException if the formula contains syntax errors.
   */
  public Formula getCompiledFormula()
      throws ParseException
  {
    if (compiledFormula == null)
    {
      compiledFormula = new Formula(formulaExpression);
    }
    return compiledFormula;
  }
}
