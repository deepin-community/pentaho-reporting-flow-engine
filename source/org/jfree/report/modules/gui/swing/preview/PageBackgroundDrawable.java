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
 * $Id: PageBackgroundDrawable.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.preview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import javax.swing.UIManager;

import org.jfree.layouting.modules.output.graphics.PageDrawable;

/**
 * Creation-Date: 17.11.2006, 20:31:36
 *
 * @author Thomas Morgner
 */
public class PageBackgroundDrawable 
{
  private PageDrawable backend;
  private boolean borderPainted;
  private float shadowSize;
  private double zoom;

  public PageBackgroundDrawable()
  {
    this.shadowSize = 6;
    this.borderPainted = false;
    this.zoom = 1;
  }

  public PageDrawable getBackend()
  {
    return backend;
  }

  public void setBackend(final PageDrawable backend)
  {
    this.backend = backend;
  }

  public boolean isBorderPainted()
  {
    return borderPainted;
  }

  public void setBorderPainted(final boolean borderPainted)
  {
    this.borderPainted = borderPainted;
  }

  public double getZoom()
  {
    return zoom;
  }

  public void setZoom(final double zoom)
  {
    this.zoom = zoom;
  }

  public Dimension getPreferredSize()
  {
    if (backend == null)
    {
      return new Dimension(0, 0);
    }
    final Dimension preferredSize = backend.getPreferredSize();

    return new Dimension
        ((int) ((preferredSize.width + shadowSize) * zoom),
            (int) ((preferredSize.height + shadowSize) * zoom));
  }

  public boolean isPreserveAspectRatio()
  {
    return true;
  }

  public float getShadowSize()
  {
    return shadowSize;
  }

  public void setShadowSize(final float shadowSize)
  {
    this.shadowSize = shadowSize;
  }

  /**
   * Draws the object.
   *
   * @param g2   the graphics device.
   * @param area the area inside which the object should be drawn.
   */
  public void draw(Graphics2D g2, Rectangle2D area)
  {
    if (backend == null)
    {
      return;
    }

    final PageFormat pageFormat = backend.getPageFormat();
    final float outerW = (float) pageFormat.getWidth();
    final float outerH = (float) pageFormat.getHeight();

    final float innerX = (float) pageFormat.getImageableX();
    final float innerY = (float) pageFormat.getImageableY();
    final float innerW = (float) pageFormat.getImageableWidth();
    final float innerH = (float) pageFormat.getImageableHeight();

    //double paperBorder = paperBorderPixel * zoomFactor;

    /** Prepare background **/
    g2.transform(AffineTransform.getScaleInstance(getZoom(), getZoom()));
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

    /** Prepare background **/
    Rectangle2D pageArea =
        new Rectangle2D.Float(0, 0, outerW, outerH);

    g2.setPaint(Color.white);
    g2.fill(pageArea);


    Graphics2D g22 = (Graphics2D) g2.create();
    backend.draw(g22, new Rectangle2D.Double
        (0, 0, pageFormat.getImageableWidth(), pageFormat.getImageableHeight()));
    g22.dispose();

    /**
     * The border around the printable area is painted when the corresponding property is
     * set to true.
     */
    final Rectangle2D printingArea = new Rectangle2D.Float(innerX, innerY, innerW, innerH);

    /** Paint Page Shadow */
    final Rectangle2D southborder = new Rectangle2D.Float
        (getShadowSize(), outerH,
            outerW, getShadowSize());

    g2.setPaint(UIManager.getColor("controlShadow"));

    g2.fill(southborder);

    final Rectangle2D eastborder = new Rectangle2D.Float
        (outerW, getShadowSize(),getShadowSize(), outerH);

    g2.fill(eastborder);
    final Rectangle2D transPageArea = new Rectangle2D.Float(0, 0, outerW, outerH);

    g2.setPaint(Color.black);
    g2.draw(transPageArea);
    if (isBorderPainted())
    {
      g2.setPaint(Color.gray);
      g2.draw(printingArea);
    }

  }
}
