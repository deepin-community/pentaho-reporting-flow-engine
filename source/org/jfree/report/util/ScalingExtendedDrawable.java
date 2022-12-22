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
 * $Id: ScalingExtendedDrawable.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.util;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

/**
 * Creation-Date: 20.01.2006, 19:46:10
 *
 * @author Thomas Morgner
 */
public class ScalingExtendedDrawable extends DrawableWrapper 
{
  /**
   * The horizontal scale factor.
   */
  private float scaleX;
  /**
   * The vertical scale factor.
   */
  private float scaleY;


  /**
   * Default constructor. Initializes the scaling to 1.
   * @param drawable the drawable object
   */
  public ScalingExtendedDrawable(final Object drawable)
  {
    super(drawable);
    scaleX = 1;
    scaleY = 1;
  }

  /**
   * Returns the vertical scale factor.
   *
   * @return the scale factor.
   */
  public float getScaleY()
  {
    return scaleY;
  }

  /**
   * Defines the vertical scale factor.
   *
   * @param scaleY the scale factor.
   */
  public void setScaleY(final float scaleY)
  {
    this.scaleY = scaleY;
  }

  /**
   * Returns the horizontal scale factor.
   *
   * @return the scale factor.
   */
  public float getScaleX()
  {
    return scaleX;
  }

  /**
   * Defines the horizontal scale factor.
   *
   * @param scaleX the scale factor.
   */
  public void setScaleX(final float scaleX)
  {
    this.scaleX = scaleX;
  }

  /**
   * Draws the object.
   *
   * @param g2   the graphics device.
   * @param area the area inside which the object should be drawn.
   */
  public void draw(final Graphics2D g2, final Rectangle2D area)
  {
    final Object drawable = getBackend();
    if (drawable == null)
    {
      return;
    }

    final Graphics2D derived = (Graphics2D) g2.create();
    derived.scale(scaleX, scaleY);
    final Rectangle2D scaledArea = (Rectangle2D) area.clone();
    scaledArea.setRect(scaledArea.getX() * scaleX, scaledArea.getY() * scaleY,
            scaledArea.getWidth() * scaleX, scaledArea.getHeight() * scaleY);
    super.draw(derived, scaledArea);
    derived.dispose();
  }
}
