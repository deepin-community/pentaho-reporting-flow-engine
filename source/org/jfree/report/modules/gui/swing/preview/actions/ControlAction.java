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
 * $Id: ControlAction.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.preview.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jfree.report.modules.gui.swing.common.SwingCommonModule;
import org.jfree.report.modules.gui.swing.common.action.ActionDowngrade;
import org.jfree.report.modules.gui.swing.preview.PreviewPane;

/**
 * Creation-Date: 16.11.2006, 17:52:48
 *
 * @author Thomas Morgner
 */
public class ControlAction extends AbstractAction
{
  private ControlActionPlugin actionPlugin;
  private PreviewPane previewPane;

  /**
   * Defines an <code>Action</code> object with a default description string and
   * default icon.
   */
  public ControlAction(final ControlActionPlugin actionPlugin,
                       final PreviewPane previewPane)
  {
    this.actionPlugin = actionPlugin;
    this.previewPane = previewPane;
    putValue(Action.NAME, actionPlugin.getDisplayName());
    putValue(Action.SHORT_DESCRIPTION, actionPlugin.getShortDescription());
    putValue(ActionDowngrade.ACCELERATOR_KEY, actionPlugin.getAcceleratorKey());
    putValue(ActionDowngrade.MNEMONIC_KEY, actionPlugin.getMnemonicKey());
    putValue(Action.SMALL_ICON, actionPlugin.getSmallIcon());
    putValue(SwingCommonModule.LARGE_ICON_PROPERTY, actionPlugin.getLargeIcon());
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(ActionEvent e)
  {
    actionPlugin.configure(previewPane);
  }
}
