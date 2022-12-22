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
 * $Id: SurveyScale.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.misc.survey;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.jfree.report.JFreeReportBoot;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.serializer.SerializerHelper;

/**
 * Draws a survey scale.  By implementing the Drawable interface, instances can be displayed within a report using the
 * DrawableElement class.
 *
 * @author David Gilbert
 */
public class SurveyScale implements Serializable
{
  private static final Number[] EMPTY_VALUES = new Number[0];

  /**
   * The lowest response value on the scale.
   */
  private int lowest;

  /**
   * The highest response value on the scale.
   */
  private int highest;

  /**
   * The lower margin.
   */
  private double lowerMargin = 0.10;

  /**
   * The upper margin.
   */
  private double upperMargin = 0.10;

  /**
   * A list of flags that control whether or not the shapes are filled.
   */
  private ArrayList fillShapes;

  /**
   * The values to display.
   */
  private Number[] values;

  /**
   * The lower bound of the highlighted range.
   */
  private Number rangeLowerBound;

  /**
   * The upper bound of the highlighted range.
   */
  private Number rangeUpperBound;

  /**
   * Draw a border?
   */
  private boolean drawBorder;

  /**
   * Draw the tick marks?
   */
  private boolean drawTickMarks;

  /**
   * Draw the scale values.
   */
  private boolean drawScaleValues;

  /**
   * The font used to display the scale values.
   */
  private Font scaleValueFont;

  /**
   * The paint used to draw the scale values.
   */
  private transient Paint scaleValuePaint;

  /**
   * The range paint.
   */
  private transient Paint rangePaint;

  /**
   * The shapes to display.
   */
  private transient ArrayList shapes;

  /**
   * The fill paint.
   */
  private transient Paint fillPaint;

  /**
   * The outline stroke for the shapes.
   */
  private transient Stroke outlineStroke;

  /**
   * The default shape, if no shape is defined in the shapeList for the given value.
   */
  private transient Shape defaultShape;

  /**
   * The tick mark paint.
   */
  private transient Paint tickMarkPaint;

  private transient Paint borderPaint;

  private int range;
  private double lowerBound;
  private double upperBound;
  private boolean useFontMetricsGetStringBounds;
  private boolean autoConfigure;

  /**
   * Creates a new default instance.
   */
  public SurveyScale()
  {
    this(1, 5, SurveyScale.EMPTY_VALUES);
  }

  /**
   * Creates a new instance.
   *
   * @param lowest  the lowest response value on the scale.
   * @param highest the highest response value on the scale.
   * @param values  the values to display.
   */
  public SurveyScale(final int lowest, final int highest,
                     final Number[] values)
  {
    final String configFontMetricsStringBounds = JFreeReportBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.jfree.report.modules.misc.survey.UseFontMetricsGetStringBounds", "auto");
    if ("auto".equals(configFontMetricsStringBounds))
    {
      useFontMetricsGetStringBounds = (ObjectUtilities.isJDK14() == true);
    }
    else
    {
      useFontMetricsGetStringBounds = "true".equals(configFontMetricsStringBounds);
    }

    this.lowest = lowest;
    this.highest = highest;
    if (values == null)
    {
      this.values = SurveyScale.EMPTY_VALUES;
    }
    else
    {
      this.values = (Number[]) values.clone();
    }

    this.drawTickMarks = true;
    this.tickMarkPaint = Color.gray;

    this.scaleValuePaint = Color.black;
    this.defaultShape = new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0);

    this.rangeLowerBound = null;
    this.rangeUpperBound = null;
    this.rangePaint = Color.lightGray;

    this.shapes = createShapeList();
    this.fillShapes = new ArrayList();
    this.fillShapes.add(0, Boolean.TRUE);
    this.fillPaint = Color.black;
    this.outlineStroke = new BasicStroke(0.5f);
    recompute();
  }

  public boolean isAutoConfigure()
  {
    return autoConfigure;
  }

  public void setAutoConfigure(final boolean autoConfigure)
  {
    this.autoConfigure = autoConfigure;
    recompute();
  }

  public int getLowest()
  {
    return lowest;
  }

  public void setLowest(final int lowest)
  {
    this.lowest = lowest;
    recompute();
  }

  public int getHighest()
  {
    return highest;
  }

  public void setHighest(final int highest)
  {
    this.highest = highest;
    recompute();
  }

  /**
   * This method is called whenever lowest or highest has changed. It will recompute the range and upper and lower
   * bounds.
   */
  protected void recompute()
  {
    this.range = Math.max(0, this.highest - this.lowest);
    this.lowerBound = this.lowest - (range * this.lowerMargin);
    this.upperBound = this.highest + (range * this.upperMargin);
  }

  protected int getRange()
  {
    return range;
  }

  protected void setRange(final int range)
  {
    this.range = range;
  }

  protected double getLowerBound()
  {
    return lowerBound;
  }

  protected void setLowerBound(final double lowerBound)
  {
    this.lowerBound = lowerBound;
  }

  protected double getUpperBound()
  {
    return upperBound;
  }

  protected void setUpperBound(final double upperBound)
  {
    this.upperBound = upperBound;
  }

  /**
   * Creates the shape list used when drawing the scale. The list returned must contain exactly 6 elements.
   *
   * @return
   */
  protected ArrayList createShapeList()
  {
    final ArrayList shapes = new ArrayList();
    //this.shapes.setShape(0, createDiagonalCross(3.0f, 0.5f));
    shapes.add(new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
    shapes.add(SurveyScale.createDownTriangle(4.0f));
    shapes.add(SurveyScale.createUpTriangle(4.0f));
    shapes.add(SurveyScale.createDiamond(4.0f));
    shapes.add(new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0));
    shapes.add(new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0));
    //this.shapes.setShape(5, createDiagonalCross(3.0f, 0.5f));
    return shapes;
  }


  /**
   * Creates a diamond shape.
   *
   * @param s the size factor (equal to half the height of the diamond).
   * @return A diamond shape.
   */
  public static Shape createDiamond(final float s)
  {
    final GeneralPath p0 = new GeneralPath();
    p0.moveTo(0.0f, -s);
    p0.lineTo(s, 0.0f);
    p0.lineTo(0.0f, s);
    p0.lineTo(-s, 0.0f);
    p0.closePath();
    return p0;
  }

  /**
   * Creates a triangle shape that points upwards.
   *
   * @param s the size factor (equal to half the height of the triangle).
   * @return A triangle shape.
   */
  public static Shape createUpTriangle(final float s)
  {
    final GeneralPath p0 = new GeneralPath();
    p0.moveTo(0.0f, -s);
    p0.lineTo(s, s);
    p0.lineTo(-s, s);
    p0.closePath();
    return p0;
  }

  /**
   * Creates a triangle shape that points downwards.
   *
   * @param s the size factor (equal to half the height of the triangle).
   * @return A triangle shape.
   */
  public static Shape createDownTriangle(final float s)
  {
    final GeneralPath p0 = new GeneralPath();
    p0.moveTo(0.0f, s);
    p0.lineTo(s, -s);
    p0.lineTo(-s, -s);
    p0.closePath();
    return p0;
  }


  /**
   * Returns the lower bound of the highlighted range.  A <code>null</code> value indicates that no range is set for
   * highlighting.
   *
   * @return The lower bound (possibly <code>null</code>).
   */
  public Number getRangeLowerBound()
  {
    return this.rangeLowerBound;
  }

  /**
   * Sets the lower bound for the range that is highlighted on the scale.
   *
   * @param bound the lower bound (<code>null</code> permitted).
   */
  public void setRangeLowerBound(final Number bound)
  {
    this.rangeLowerBound = bound;
  }

  /**
   * Returns the upper bound of the highlighted range.  A <code>null</code> value indicates that no range is set for
   * highlighting.
   *
   * @return The upper bound (possibly <code>null</code>).
   */
  public Number getRangeUpperBound()
  {
    return this.rangeUpperBound;
  }

  /**
   * Sets the upper bound for the range that is highlighted on the scale.
   *
   * @param bound the upper bound (<code>null</code> permitted).
   */
  public void setRangeUpperBound(final Number bound)
  {
    this.rangeUpperBound = bound;
  }

  /**
   * Returns a flag that controls whether or not a border is drawn around the scale.
   *
   * @return A boolean.
   */
  public boolean isDrawBorder()
  {
    return this.drawBorder;
  }

  /**
   * Sets a flag that controls whether or not a border is drawn around the scale.
   *
   * @param flag the flag.
   */
  public void setDrawBorder(final boolean flag)
  {
    this.drawBorder = flag;
  }

  /**
   * Returns the flag that controls whether the tick marks are drawn.
   *
   * @return A boolean.
   */
  public boolean isDrawTickMarks()
  {
    return this.drawTickMarks;
  }

  /**
   * Sets the flag that controls whether the tick marks are drawn.
   *
   * @param flag a boolean.
   */
  public void setDrawTickMarks(final boolean flag)
  {
    this.drawTickMarks = flag;
  }

  /**
   * Returns a flag that controls whether or not scale values are drawn.
   *
   * @return a boolean.
   */
  public boolean isDrawScaleValues()
  {
    return this.drawScaleValues;
  }

  /**
   * Sets a flag that controls whether or not scale values are drawn.
   *
   * @param flag the flag.
   */
  public void setDrawScaleValues(final boolean flag)
  {
    this.drawScaleValues = flag;
  }

  /**
   * Returns the font used to display the scale values.
   *
   * @return A font (never <code>null</code>).
   */
  public Font getScaleValueFont()
  {
    return this.scaleValueFont;
  }

  /**
   * Sets the font used to display the scale values.
   *
   * @param font the font (<code>null</code> not permitted).
   */
  public void setScaleValueFont(final Font font)
  {
    this.scaleValueFont = font;
  }

  /**
   * Returns the color used to draw the scale values (if they are visible).
   *
   * @return A paint (never <code>null</code>).
   */
  public Paint getScaleValuePaint()
  {
    return this.scaleValuePaint;
  }

  /**
   * Sets the color used to draw the scale values.
   *
   * @param paint the paint (<code>null</code> not permitted).
   */
  public void setScaleValuePaint(final Paint paint)
  {
    if (paint == null)
    {
      throw new IllegalArgumentException("Null 'paint' argument."); //$NON-NLS-1$
    }
    this.scaleValuePaint = paint;
  }

  /**
   * Returns the shape used to indicate the value of a response.
   *
   * @param index the value index (zero-based).
   * @return The shape.
   */
  public Shape getShape(final int index)
  {
    if (index < 0)
    {
      throw new IndexOutOfBoundsException();
    }
    if (index < shapes.size())
    {
      return (Shape) this.shapes.get(index);
    }
    return null;
  }

  /**
   * Sets the shape used to mark a particular value in the dataset.
   *
   * @param index the value index (zero-based).
   * @param shape the shape (<code>null</code> not permitted).
   */
  public void setShape(final int index, final Shape shape)
  {
    if (index < 0)
    {
      throw new IndexOutOfBoundsException();
    }
    if (shapes.size() > index)
    {
      this.shapes.set(index, shape);
    }
    else
    {
      while (shapes.size() < index)
      {
        shapes.ensureCapacity(index);
        shapes.add(null);
        shapes.add(shape);
      }
    }
  }

  /**
   * Returns a flag that controls whether the shape for a particular value should be filled.
   *
   * @param index the value index (zero-based).
   * @return A boolean.
   */
  public boolean isShapeFilled(final int index)
  {
    if (index < 0)
    {
      throw new IndexOutOfBoundsException();
    }
    if (index < fillShapes.size())
    {
      final Boolean b = (Boolean) this.fillShapes.get(index);
      if (b != null)
      {
        return b.booleanValue();
      }
    }
    return false;
  }

  /**
   * Sets the flag that controls whether the shape for a particular value should be filled.
   *
   * @param index the value index (zero-based).
   * @param fill  the flag.
   */
  public void setShapeFilled(final int index, final boolean fill)
  {
    //noinspection ConditionalExpression
    this.fillShapes.set(index, fill ? Boolean.TRUE : Boolean.FALSE);
  }

  /**
   * Returns the paint used to highlight the range.
   *
   * @return A {@link Paint} object (never <code>null</code>).
   */
  public Paint getRangePaint()
  {
    return this.rangePaint;
  }

  /**
   * Sets the paint used to highlight the range (if one is specified).
   *
   * @param paint the paint (<code>null</code> not permitted).
   */
  public void setRangePaint(final Paint paint)
  {
    if (paint == null)
    {
      throw new IllegalArgumentException("Null 'paint' argument."); //$NON-NLS-1$
    }
    this.rangePaint = paint;
  }

  public Paint getBorderPaint()
  {
    return borderPaint;
  }

  public void setBorderPaint(final Paint borderPaint)
  {
    if (borderPaint == null)
    {
      throw new IllegalArgumentException("Null 'paint' argument."); //$NON-NLS-1$
    }
    this.borderPaint = borderPaint;
  }

  /**
   * Returns the default shape, which is used, if a shape for a certain value is not defined.
   *
   * @return the default shape, never null.
   */
  public Shape getDefaultShape()
  {
    return defaultShape;
  }

  /**
   * Redefines the default shape.
   *
   * @param defaultShape the default shape
   * @throws NullPointerException if the given shape is null.
   */
  public void setDefaultShape(final Shape defaultShape)
  {
    if (defaultShape == null)
    {
      throw new NullPointerException("The default shape must not be null."); //$NON-NLS-1$
    }
    this.defaultShape = defaultShape;
  }

  public Paint getTickMarkPaint()
  {
    return tickMarkPaint;
  }

  public void setTickMarkPaint(final Paint tickMarkPaint)
  {
    if (tickMarkPaint == null)
    {
      throw new NullPointerException();
    }
    this.tickMarkPaint = tickMarkPaint;
  }

  public Number[] getValues()
  {
    return (Number[]) values.clone();
  }

  public Paint getFillPaint()
  {
    return fillPaint;
  }

  public void setFillPaint(final Paint fillPaint)
  {
    if (fillPaint == null)
    {
      throw new NullPointerException();
    }
    this.fillPaint = fillPaint;
  }

  public Stroke getOutlineStroke()
  {
    return outlineStroke;
  }

  public void setOutlineStroke(final Stroke outlineStroke)
  {
    if (outlineStroke == null)
    {
      throw new NullPointerException();
    }
    this.outlineStroke = outlineStroke;
  }

  public double getUpperMargin()
  {
    return upperMargin;
  }

  public void setUpperMargin(final double upperMargin)
  {
    this.upperMargin = upperMargin;
  }

  public double getLowerMargin()
  {
    return lowerMargin;
  }

  public void setLowerMargin(final double lowerMargin)
  {
    this.lowerMargin = lowerMargin;
  }

  /**
   * Draws the survey scale.
   *
   * @param g2   the graphics device.
   * @param area the area.
   */
  public void draw(final Graphics2D g2, final Rectangle2D area)
  {

    if (isDrawBorder())
    {
      drawBorder(g2, area);
    }

    drawRangeArea(area, g2);

    // draw tick marks...
    if (isDrawTickMarks())
    {
      drawTickMarks(g2, area);
    }

    // draw scale values...
    if (isDrawScaleValues())
    {
      drawScaleValues(g2, area);
    }

    drawValues(g2, area);
  }

  protected void drawValues(final Graphics2D g2,
                            final Rectangle2D area)
  {

    // draw data values...
    final Number[] values = getValues();
    if (values.length == 0)
    {
      return;
    }

    final double y = area.getCenterY();

    final Stroke outlineStroke = getOutlineStroke();
    final Shape defaultShape = getDefaultShape();

    g2.setPaint(getFillPaint());
    for (int i = 0; i < values.length; i++)
    {
      final Number n = values[i];
      if (n == null)
      {
        continue;
      }

      final double v = n.doubleValue();
      final double x = valueToJava2D(v, area);
      Shape valueShape = getShape(i);
      if (valueShape == null)
      {
        valueShape = defaultShape;
      }
      if (isShapeFilled(i))
      {
        g2.translate(x, y);
        g2.fill(valueShape);
        g2.translate(-x, -y);
      }
      else
      {
        g2.setStroke(outlineStroke);
        g2.translate(x, y);
        g2.draw(valueShape);
        g2.translate(-x, -y);
      }
    }
  }

  protected void drawScaleValues(final Graphics2D g2, final Rectangle2D area)
  {
    g2.setPaint(getScaleValuePaint());
    final Font valueFont = getScaleValueFont();
    if (valueFont != null)
    {
      g2.setFont(valueFont);
    }

    final Font f = g2.getFont();
    final FontMetrics fm = g2.getFontMetrics(f);
    final FontRenderContext frc = g2.getFontRenderContext();
    final double y = area.getCenterY();

    final int highest = getHighest();
    for (int i = getLowest(); i <= highest; i++)
    {
      final double x = valueToJava2D(i, area);
      final String text = String.valueOf(i);

      final float width;
      if (useFontMetricsGetStringBounds)
      {
        final Rectangle2D bounds = fm.getStringBounds(text, g2);
        // getStringBounds() can return incorrect height for some Unicode
        // characters...see bug parade 6183356, let's replace it with
        // something correct
        width = (float) bounds.getWidth();
      }
      else
      {
        width = fm.stringWidth(text);
      }


      final LineMetrics metrics = f.getLineMetrics(text, frc);
      final float descent = metrics.getDescent();
      final float leading = metrics.getLeading();
      final float yAdj = -descent - leading + (float) (metrics.getHeight() / 2.0);
      final float xAdj = -width / 2.0f;
      g2.drawString(text, (float) (x + xAdj), (float) (y + yAdj));
    }
  }

  protected void drawTickMarks(final Graphics2D g2, final Rectangle2D area)
  {
    g2.setPaint(getTickMarkPaint());
    g2.setStroke(new BasicStroke(0.1f));

    final int highest = getHighest();
    for (int i = getLowest(); i <= highest; i++)
    {
      for (int j = 0; j < 10; j++)
      {
        final double xx = valueToJava2D(i + j / 10.0, area);
        final Line2D mark = new Line2D.Double(xx, area.getCenterY() - 2.0, xx,
            area.getCenterY() + 2.0);
        g2.draw(mark);
      }
    }
    final double xx = valueToJava2D(highest, area);
    final Line2D mark = new Line2D.Double(xx, area.getCenterY() - 2.0, xx,
        area.getCenterY() + 2.0);
    g2.draw(mark);
  }

  protected void drawRangeArea(final Rectangle2D area, final Graphics2D g2)
  {
    final Number rangeUpperBound = getRangeUpperBound();
    final Number rangeLowerBound = getRangeLowerBound();
    if (rangeLowerBound == null || rangeUpperBound == null)
    {
      return;
    }
    final double x0 = valueToJava2D(rangeLowerBound.doubleValue(), area);
    final double x1 = valueToJava2D(rangeUpperBound.doubleValue(), area);
    final Rectangle2D rangeArea = new Rectangle2D.Double(x0, area.getY(),
        (x1 - x0), area.getHeight());
    g2.setPaint(getRangePaint());
    g2.fill(rangeArea);
  }

  protected void drawBorder(final Graphics2D g2, final Rectangle2D area)
  {
    g2.setStroke(getOutlineStroke());
    g2.setPaint(getBorderPaint());
    g2.draw(area);
  }

  /**
   * Translates a data value to Java2D coordinates.
   *
   * @param value the value.
   * @param area  the area.
   * @return The Java2D coordinate.
   */
  private double valueToJava2D(final double value,
                               final Rectangle2D area)
  {

    final double upperBound = getUpperBound();
    final double lowerBound = getLowerBound();
    return area.getMinX() + ((value - lowerBound) /
        (upperBound - lowerBound) * area.getWidth());

  }

  private void writeObject(final ObjectOutputStream out)
      throws IOException
  {
    out.defaultWriteObject();
    final SerializerHelper helper = SerializerHelper.getInstance();
    helper.writeObject(scaleValuePaint, out);
    helper.writeObject(rangePaint, out);
    helper.writeObject(fillPaint, out);
    helper.writeObject(outlineStroke, out);
    helper.writeObject(defaultShape, out);
    helper.writeObject(tickMarkPaint, out);
    helper.writeObject(borderPaint, out);
    final int size = shapes.size();
    out.writeInt(size);
    for (int i = 0; i < size; i++)
    {
      final Shape s = (Shape) shapes.get(i);
      helper.writeObject(s, out);
    }
  }

  private void readObject(final ObjectInputStream in)
      throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    final SerializerHelper helper = SerializerHelper.getInstance();
    scaleValuePaint = (Paint) helper.readObject(in);
    rangePaint = (Paint) helper.readObject(in);
    fillPaint = (Paint) helper.readObject(in);
    outlineStroke = (Stroke) helper.readObject(in);
    defaultShape = (Shape) helper.readObject(in);
    tickMarkPaint = (Paint) helper.readObject(in);
    borderPaint = (Paint) helper.readObject(in);
    shapes = new ArrayList();

    final int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      final Shape s = (Shape) helper.readObject(in);
      shapes.add(s);
    }

  }
}
