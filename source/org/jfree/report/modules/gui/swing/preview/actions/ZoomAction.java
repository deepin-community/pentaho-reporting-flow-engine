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
 * $Id: ZoomAction.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.preview.actions;

import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jfree.report.modules.gui.swing.common.SwingCommonModule;
import org.jfree.report.modules.gui.swing.common.action.ActionDowngrade;
import org.jfree.report.modules.gui.swing.preview.PreviewPane;
import org.jfree.report.util.ImageUtils;

/**
 * Creation-Date: 16.11.2006, 18:51:18
 *
 * @author Thomas Morgner
 */
public class ZoomAction extends AbstractAction
{
  private double zoom;
  private PreviewPane previewPane;

  /**
   * Defines an <code>Action</code> object with a default description string and
   * default icon.
   */
  public ZoomAction(final double zoom, final PreviewPane previewPane)
  {
    this.zoom = zoom;
    this.previewPane = previewPane;

    this.putValue(Action.NAME, NumberFormat.getPercentInstance
        (previewPane.getLocale()).format(zoom));
    this.putValue(ActionDowngrade.SMALL_ICON,
            ImageUtils.createTransparentIcon(16, 16));
    this.putValue(SwingCommonModule.LARGE_ICON_PROPERTY, ImageUtils.createTransparentIcon(24, 24));
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(ActionEvent e)
  {
    previewPane.setZoom(zoom);
  }
}
