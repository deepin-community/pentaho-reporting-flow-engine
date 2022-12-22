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
 * $Id: HtmlFileExportActionPlugin.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.html;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.jfree.report.ReportConfigurationException;
import org.jfree.report.flow.ReportJob;
import org.jfree.report.modules.gui.swing.common.AbstractExportActionPlugin;
import org.jfree.report.modules.gui.swing.common.SwingGuiContext;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Creation-Date: 30.11.2006, 12:19:00
 *
 * @author Thomas Morgner
 */
public class HtmlFileExportActionPlugin extends AbstractExportActionPlugin
{
  private ResourceBundleSupport resources;
  private static final String EXPORT_DIALOG_KEY = "org.jfree.report.modules.gui.swing.html.file.ExportDialog";

  public HtmlFileExportActionPlugin()
  {
  }

  protected String getConfigurationPrefix()
  {
    return "org.jfree.report.modules.gui.swing.html.export.file.";
  }

  public boolean initialize(SwingGuiContext context)
  {
    super.initialize(context);
    resources = new ResourceBundleSupport(context.getLocale(),
        SwingHtmlModule.BUNDLE_NAME);
    return true;
  }

  /**
   * Returns the display name for the export action.
   *
   * @return The display name.
   */
  public String getDisplayName()
  {
    return resources.getString("action.export-to-html.file.name");
  }

  /**
   * Returns the short description for the export action.
   *
   * @return The short description.
   */
  public String getShortDescription()
  {
    return resources.getString("action.export-to-html.file.description");
  }

  /**
   * Returns the small icon for the export action.
   *
   * @return The icon.
   */
  public Icon getSmallIcon()
  {
//    final Locale locale = getContext().getLocale();
//    return getIconTheme().getSmallIcon(locale, "action.export-to-html.small-icon");
    return null;
  }

  /**
   * Returns the large icon for the export action.
   *
   * @return The icon.
   */
  public Icon getLargeIcon()
  {
//    final Locale locale = getContext().getLocale();
//    return getIconTheme().getLargeIcon(locale, "action.export-to-html.icon");
    return null;
  }

  /**
   * Returns the accelerator key for the export action.
   *
   * @return The accelerator key.
   */
  public KeyStroke getAcceleratorKey()
  {
    return resources.getOptionalKeyStroke("action.export-to-html.file.accelerator");
  }

  /**
   * Returns the mnemonic key code.
   *
   * @return The code.
   */
  public Integer getMnemonicKey()
  {
    return resources.getOptionalMnemonic("action.export-to-html.file.mnemonic");
  }

  /**
   * Exports a report.
   *
   * @param job the report.
   * @return A boolean.
   */
  public boolean performExport(ReportJob job)
  {
    if (performShowExportDialog(job, EXPORT_DIALOG_KEY) == false)
    {
      return false;
    }

    try
    {
      final HtmlFileExportTask task = new HtmlFileExportTask(job);
      Thread worker = new Thread(task);
      setStatusText("Started Job");
      worker.start();
      return true;
    }
    catch (ReportConfigurationException e)
    {
      setStatusText("Failed to configure the export task.");
      return false;
    }
  }
}
