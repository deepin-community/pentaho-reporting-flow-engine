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
 * $Id: Expression.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.expressions;

import java.io.Serializable;

import org.jfree.report.DataSourceException;

/**
 * An expression is a lightweight computation that does not maintain a state.
 *
 * Expressions are used to calculate values within a single row of a report.
 * Expressions can use a dataRow to access other fields, expressions or
 * functions within the current row in the report.
 *
 * Statefull computations can be implemented using functions.
 *
 * @author Thomas Morgner
 * @see Function
 */
public interface Expression extends Cloneable, Serializable
{
  /**
   * Returns the name of the expression. An expression without a name cannot be
   * referenced from outside the element.
   *
   * @return the function name.
   */
  public String getName();

  /**
   * Sets the name of the expression.
   *
   * @param name the name.
   */
  public void setName(String name);

  /**
   * Return the current expression value. <P> The value depends (obviously) on
   * the expression implementation.
   *
   * @return the value of the function.
   */
  public Object computeValue() throws DataSourceException;

  /**
   * Clones the expression, expression should be reinitialized after the
   * cloning. <P> Expression maintain no state, cloning is done at the beginning
   * of the report processing to disconnect the used expression from any other
   * object space.
   *
   * @return A clone of this expression.
   * @throws CloneNotSupportedException this should never happen.
   */
  public Object clone()
          throws CloneNotSupportedException;


  /**
   * Return a new instance of this expression. The copy is initialized and uses
   * the same parameters as the original, but does not share any objects.
   *
   * @return a copy of this function.
   */
  public Expression getInstance();

  /**
   * Defines the DataRow used in this expression. The dataRow is set when the
   * report processing starts and can be used to access the values of functions,
   * expressions and the reports datasource.
   *
   * @param runtime the runtime information for the expression
   */
  public void setRuntime(ExpressionRuntime runtime);

  /**
   * A deep-traversing expression declares that it should receive updates from
   * all subreports. This mode should be activated if the expression's result
   * depends on values contained in the subreport.
   *
   * @return true, if the expression is deep-traversing, false otherwise.
   */
  public boolean isDeepTraversing ();

  /**
   * Defines, whether the expression is deep-traversing.
   *
   * @param deepTraversing true, if the expression is deep-traversing, false
   * otherwise.
   */
  public void setDeepTraversing (boolean deepTraversing);

  /**
   * Returns, whether the expression will be precomputed. For precomputed
   * expressions a parallel evaluation process is started and the result to
   * which the expression evaluates before it gets out of scope will be used
   * whenever an other expression queries this expression's value.
   *
   * @return true, if the expression is precomputed, false otherwise.
   */
  public boolean isPrecompute();

  /**
   * Defines, whether the expression will be precomputed. For precomputed
   * expressions a parallel evaluation process is started and the result to
   * which the expression evaluates before it gets out of scope will be used
   * whenever an other expression queries this expression's value.
   *
   * @param precompute true, if the expression is precomputed, false otherwise.
   */
  public void setPrecompute(boolean precompute);

  /**
   * Checks, whether the expression's result should be preserved in the
   * precomputed value registry. This way, the last value for that expression
   * can be retrieved after the report has been finished.
   *
   * The preserve-function will only preserve the last value that has been
   * evaluated before the expression went out of scope.
   *
   * @return true, if the expression's results should be preserved,
   * false otherwise.
   */
  public boolean isPreserve();

  /**
   * Defines, whether the expression's result should be preserved in the
   * precomputed value registry. This way, the last value for that expression
   * can be retrieved after the report has been finished.
   *
   * @param preserve true, if the expression's results should be preserved,
   * false otherwise.
   */
  public void setPreserve(boolean preserve);
}
