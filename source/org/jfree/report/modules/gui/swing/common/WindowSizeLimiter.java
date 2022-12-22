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
 * $Id: WindowSizeLimiter.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.common;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * A small helper class to limit the maximum size of an element to the elements maximum
 * size. If the element is resized, the defined maximum size is enforced on the element.
 *
 * @author Thomas Morgner
 */
public class WindowSizeLimiter extends ComponentAdapter
{
  /**
   * The current source.
   */
  private Object currentSource;

  /**
   * Default-Constructor.
   */
  public WindowSizeLimiter()
  {

  }

  /**
   * Invoked when the component's size changes.
   *
   * @param e the event.
   */
  public void componentResized (final ComponentEvent e)
  {
    if (e.getSource() == currentSource)
    {
      return;
    }

    if (e.getSource() instanceof Component)
    {
      currentSource = e.getSource();
      final Component c = (Component) e.getSource();
      final Dimension d = c.getMaximumSize();
      final Dimension s = c.getSize();
      if (s.width > d.width)
      {
        s.width = d.width;
      }
      if (s.height > d.height)
      {
        s.height = d.height;
      }
      c.setSize(s);
      currentSource = null;
    }

  }
}
