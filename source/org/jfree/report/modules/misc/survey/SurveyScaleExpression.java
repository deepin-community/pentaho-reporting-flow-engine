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
 * $Id: SurveyScaleExpression.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.misc.survey;

import java.awt.Paint;
import java.awt.Shape;
import java.io.Serializable;

import org.jfree.report.DataSourceException;
import org.jfree.report.expressions.ColumnAggregationExpression;

/**
 * An expression that takes values from one or more fields in the current row of the
 * report, builds a {@link SurveyScale} instance that will present those values, and
 * returns that instance as the expression result.  The fields used by the expression are
 * defined using properties named '0', '1', ... 'N', which need to be specified after the
 * expression is created. These fields should contain {@link Number} instances.The {@link
 * SurveyScale} class implements the Drawable interface, so it can be displayed
 * using a DrawableElement.
 */
public class SurveyScaleExpression extends
        ColumnAggregationExpression implements Serializable
{
  /**
   * The name of the field containing the lower bound of the highlighted range.
   */
  private Number rangeLowerBound;

  /**
   * The name of the field containing the upper bound of the highlighted range.
   */
  private Number rangeUpperBound;

  /**
   * The range paint.
   */
  private Paint rangePaint;

  /**
   * An optional shape that is used (if present) for the first data value.
   */
  private Shape overrideShape;

  /**
   * A flag that controls whether or not the override shape is filled or not filled.
   */
  private boolean overrideShapeFilled;

  private int lowestValue;
  private int highestValue;

  public SurveyScaleExpression ()
  {
  }

  protected int getFieldListParameterPosition()
  {
    return 2;
  }

  public Number getRangeLowerBound()
  {
    return rangeLowerBound;
  }

  public void setRangeLowerBound(final Number rangeLowerBound)
  {
    this.rangeLowerBound = rangeLowerBound;
  }

  public Number getRangeUpperBound()
  {
    return rangeUpperBound;
  }

  public void setRangeUpperBound(final Number rangeUpperBound)
  {
    this.rangeUpperBound = rangeUpperBound;
  }

  public int getLowestValue()
  {
    return lowestValue;
  }

  public void setLowestValue(final int lowestValue)
  {
    this.lowestValue = lowestValue;
  }

  public int getHighestValue()
  {
    return highestValue;
  }

  public void setHighestValue(final int highestValue)
  {
    this.highestValue = highestValue;
  }

  /**
   * Returns the override shape.
   *
   * @return The override shape (possibly <code>null</code>).
   */
  public Shape getOverrideShape ()
  {
    return this.overrideShape;
  }

  /**
   * Sets the override shape.  The {@link SurveyScale} is created with a set of default
   * shapes, this method allows you to clearFromParent the *first* shape if you need to (leave it
   * as <code>null</code> otherwise).
   *
   * @param shape the shape (<code>null</code> permitted).
   */
  public void setOverrideShape (final Shape shape)
  {
    this.overrideShape = shape;
  }

  /**
   * Sets a flag that controls whether the override shape is filled or not.
   *
   * @param b the flag.
   */
  public void setOverrideShapeFilled (final boolean b)
  {
    this.overrideShapeFilled = b;
  }

  /**
   * Returns a {@link SurveyScale} instance that is set up to display the values in the
   * current row.
   *
   * @return a {@link SurveyScale} instance.
   */
  public Object computeValue () throws DataSourceException
  {
    final Number[] fieldValues = (Number[]) getFieldValues(Number.class);
    final SurveyScale result = new SurveyScale
            (this.lowestValue, this.highestValue, fieldValues);

    result.setRangeLowerBound(getRangeLowerBound());
    result.setRangeUpperBound(getRangeUpperBound());
    result.setRangePaint(this.rangePaint);

    if (this.overrideShape != null)
    {
      result.setShape(0, this.overrideShape);
      result.setShapeFilled(0, this.overrideShapeFilled);
    }
    return result;
  }

  public boolean isOverrideShapeFilled ()
  {
    return overrideShapeFilled;
  }

  public Paint getRangePaint ()
  {
    return rangePaint;
  }

  public void setRangePaint (final Paint rangePaint)
  {
    this.rangePaint = rangePaint;
  }
}
