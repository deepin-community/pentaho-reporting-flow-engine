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
 * $Id: SwingUtil.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.common;

import java.awt.Component;
import java.awt.Window;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.awt.Dialog;
import java.awt.Container;
import java.lang.reflect.Method;

/**
 * Creation-Date: 20.11.2006, 22:42:21
 *
 * @author Thomas Morgner
 */
public class SwingUtil
{
  private SwingUtil()
  {
  }


  /**
   * Computes the center point of the current screen device. If this method is called on JDK 1.4, Xinerama-aware results
   * are returned. (See Sun-Bug-ID 4463949 for details).
   *
   * @return the center point of the current screen.
   */
  public static Point getCenterPoint()
  {
    final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    try
    {
      final Method method = GraphicsEnvironment.class.getMethod("getCenterPoint", (Class[]) null);
      return (Point) method.invoke(localGraphicsEnvironment, (Object[]) null);
    }
    catch (Exception e)
    {
      // ignore ... will fail if this is not a JDK 1.4 ..
    }

    final Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
    return new Point(s.width / 2, s.height / 2);
  }

  /**
   * Computes the maximum bounds of the current screen device. If this method is called on JDK 1.4, Xinerama-aware
   * results are returned. (See Sun-Bug-ID 4463949 for details).
   *
   * @return the maximum bounds of the current screen.
   */
  public static Rectangle getMaximumWindowBounds()
  {
    final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    try
    {
      final Method method = GraphicsEnvironment.class.getMethod("getMaximumWindowBounds", (Class[]) null);
      return (Rectangle) method.invoke(localGraphicsEnvironment, (Object[]) null);
    }
    catch (Exception e)
    {
      // ignore ... will fail if this is not a JDK 1.4 ..
    }

    final Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
    return new Rectangle(0, 0, s.width, s.height);
  }

  /**
   * Positions the specified frame in the middle of the screen.
   *
   * @param frame the frame to be centered on the screen.
   */
  public static void centerFrameOnScreen(final Window frame)
  {
    positionFrameOnScreen(frame, 0.5, 0.5);
  }

  /**
   * Positions the specified frame at a relative position in the screen, where 50% is considered to be the center of the
   * screen.
   *
   * @param frame             the frame.
   * @param horizontalPercent the relative horizontal position of the frame (0.0 to 1.0, where 0.5 is the center of the
   *                          screen).
   * @param verticalPercent   the relative vertical position of the frame (0.0 to 1.0, where 0.5 is the center of the
   *                          screen).
   */
  public static void positionFrameOnScreen(final Window frame,
                                           final double horizontalPercent,
                                           final double verticalPercent)
  {

    final Rectangle s = getMaximumWindowBounds();
    final Dimension f = frame.getSize();
    final int w = Math.max(s.width - f.width, 0);
    final int h = Math.max(s.height - f.height, 0);
    final int x = (int) (horizontalPercent * w) + s.x;
    final int y = (int) (verticalPercent * h) + s.y;
    frame.setBounds(x, y, f.width, f.height);

  }

  /**
   * Positions the specified frame at a random location on the screen while ensuring that the entire frame is visible
   * (provided that the frame is smaller than the screen).
   *
   * @param frame the frame.
   */
  public static void positionFrameRandomly(final Window frame)
  {
    positionFrameOnScreen(frame, Math.random(), Math.random());
  }

  /**
   * Positions the specified dialog within its parent.
   *
   * @param dialog the dialog to be positioned on the screen.
   */
  public static void centerDialogInParent(final Dialog dialog)
  {
    positionDialogRelativeToParent(dialog, 0.5, 0.5);
  }

  /**
   * Positions the specified dialog at a position relative to its parent.
   *
   * @param dialog            the dialog to be positioned.
   * @param horizontalPercent the relative location.
   * @param verticalPercent   the relative location.
   */
  public static void positionDialogRelativeToParent(final Dialog dialog,
                                                    final double horizontalPercent,
                                                    final double verticalPercent)
  {
    final Dimension d = dialog.getSize();
    final Container parent = dialog.getParent();
    final Dimension p = parent.getSize();

    final int baseX = parent.getX() - d.width;
    final int baseY = parent.getY() - d.height;
    final int w = d.width + p.width;
    final int h = d.height + p.height;
    int x = baseX + (int) (horizontalPercent * w);
    int y = baseY + (int) (verticalPercent * h);

    // make sure the dialog fits completely on the screen...
    final Rectangle s = getMaximumWindowBounds();
    x = Math.min(x, (s.width - d.width));
    x = Math.max(x, 0);
    y = Math.min(y, (s.height - d.height));
    y = Math.max(y, 0);

    dialog.setBounds(x + s.x, y + s.y, d.width, d.height);

  }

  public static Window getWindowAncestor(Component component)
  {
    while (component instanceof Window == false)
    {
      if (component == null)
      {
        return null;
      }
      component = component.getParent();
    }
    return (Window) component;
  }
}
