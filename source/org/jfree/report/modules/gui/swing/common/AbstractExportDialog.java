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
 * $Id: AbstractExportDialog.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.gui.swing.common;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;

import org.jfree.report.flow.ReportJob;
import org.jfree.report.modules.gui.common.GuiContext;
import org.jfree.report.modules.preferences.base.ConfigFactory;
import org.jfree.report.modules.preferences.base.ConfigStorage;
import org.jfree.report.modules.preferences.base.ConfigStoreException;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.DebugLog;

public abstract class AbstractExportDialog extends JDialog
  implements ExportDialog
{
  /**
   * Internal action class to confirm the dialog and to validate the input.
   */
  private class ConfirmAction extends AbstractAction
  {
    /**
     * Default constructor.
     */
    public ConfirmAction(final ResourceBundle resources)
    {
      putValue(Action.NAME, resources.getString("OptionPane.okButtonText"));
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e the action event.
     */
    public void actionPerformed(final ActionEvent e)
    {
      if (performValidate() && performConfirm())
      {
        setConfirmed(true);
        setVisible(false);
      }
    }
  }

  /**
   * Internal action class to cancel the report processing.
   */
  private class CancelAction extends AbstractAction
  {
    /**
     * Default constructor.
     */
    public CancelAction(final ResourceBundle resources)
    {
      putValue(Action.NAME, resources.getString("OptionPane.cancelButtonText"));
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e the action event.
     */
    public void actionPerformed(final ActionEvent e)
    {
      setConfirmed(false);
      setVisible(false);
    }
  }

  private class ExportDialogValidator extends FormValidator
  {
    public ExportDialogValidator()
    {
      super();
    }

    public boolean performValidate()
    {
      return AbstractExportDialog.this.performValidate();
    }

    public Action getConfirmAction()
    {
      return AbstractExportDialog.this.getConfirmAction();
    }
  }

  private class WindowCloseHandler extends WindowAdapter
  {
    public WindowCloseHandler()
    {
    }

    /**
     * Invoked when a window is in the process of being closed. The close
     * operation can be overridden at this point.
     */
    public void windowClosing(final WindowEvent e)
    {
      final Action cancelAction = getCancelAction();
      if (cancelAction != null)
      {
        cancelAction.actionPerformed(null);
      }
      else
      {
        setConfirmed(false);
        setVisible(false);
      }
    }
  }

  private Action cancelAction;
  private Action confirmAction;
  private FormValidator formValidator;
  private ResourceBundle resources;
  private boolean confirmed;
  private ReportJob reportJob;
  private GuiContext guiContext;

  /**
   * Creates a non-modal dialog without a title and without a specified
   * <code>Frame</code> owner.  A shared, hidden frame will be set as the owner
   * of the dialog.
   */
  public AbstractExportDialog()
  {
    initialize();
  }

  /**
   * Creates a non-modal dialog without a title with the specified
   * <code>Frame</code> as its owner.  If <code>owner</code> is
   * <code>null</code>, a shared, hidden frame will be set as the owner of the
   * dialog.
   *
   * @param owner the <code>Frame</code> from which the dialog is displayed
   */
  public AbstractExportDialog(final Frame owner)
  {
    super(owner);
    initialize();
  }


  /**
   * Creates a non-modal dialog without a title with the specified
   * <code>Dialog</code> as its owner.
   *
   * @param owner the non-null <code>Dialog</code> from which the dialog is
   *              displayed
   */
  public AbstractExportDialog(final Dialog owner)
  {
    super(owner);
    initialize();
  }

  private void initialize()
  {
    ResourceBundle resources = ResourceBundle.getBundle(SwingCommonModule.BUNDLE_NAME);

    cancelAction = new CancelAction(resources);
    confirmAction = new ConfirmAction(resources);

    formValidator = new ExportDialogValidator();
    setModal(true);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowCloseHandler());
  }


  public abstract JStatusBar getStatusBar();

  protected Action getCancelAction()
  {
    return cancelAction;
  }

  protected void setCancelAction(final Action cancelAction)
  {
    this.cancelAction = cancelAction;
  }

  protected Action getConfirmAction()
  {
    return confirmAction;
  }

  protected void setConfirmAction(final Action confirmAction)
  {
    this.confirmAction = confirmAction;
  }

  protected abstract boolean performValidate();

  protected FormValidator getFormValidator()
  {
    return formValidator;
  }

  protected abstract void initializeFromJob(ReportJob job,
                                            final GuiContext guiContext);

  protected ReportJob getReportJob()
  {
    return reportJob;
  }

  protected GuiContext getGuiContext()
  {
    return guiContext;
  }

  /**
   * Opens the dialog to query all necessary input from the user. This will not
   * start the processing, as this is done elsewhere.
   *
   * @param reportJob the report that should be processed.
   * @return true, if the processing should continue, false otherwise.
   */
  public boolean performQueryForExport(final ReportJob reportJob,
                                       final GuiContext guiContext)
  {
    this.reportJob = reportJob;
    this.guiContext = guiContext;

    final Locale locale = reportJob.getReportStructureRoot().getLocale();
    setLocale(locale);
    pack();
    clear();
    initializeFromJob(reportJob, guiContext);

    final FormValidator formValidator = getFormValidator();
    formValidator.setEnabled(false);
    final ModifiableConfiguration repConf = reportJob.getConfiguration();
    final boolean inputStorageEnabled = isInputStorageEnabled(repConf);

    Configuration loadedConfiguration;
    if (inputStorageEnabled)
    {
      loadedConfiguration = loadFromConfigStore(reportJob, repConf);
    }
    else
    {
      loadedConfiguration = repConf;
    }

    setDialogContents(loadedConfiguration);

    formValidator.setEnabled(true);
    formValidator.handleValidate();
    setModal(true);
    setVisible(true);
    if (isConfirmed() == false)
    {
      return false;
    }

    formValidator.setEnabled(false);

    final Configuration fullDialogContents = grabDialogContents(true);
    final Enumeration configProperties =
        fullDialogContents.getConfigProperties();
    while (configProperties.hasMoreElements())
    {
      final String key = (String) configProperties.nextElement();
      repConf.setConfigProperty(key, fullDialogContents.getConfigProperty(key));
    }

    if (inputStorageEnabled)
    {
      saveToConfigStore(reportJob, repConf);
    }

    formValidator.setEnabled(true);
    this.reportJob = null;
    return true;
  }

  private void saveToConfigStore(final ReportJob reportJob,
                                 final Configuration reportConfiguration)
  {
    final String configPath = ConfigFactory.encodePath(
        reportJob.getName() + getConfigurationSuffix());

    try
    {
      final boolean fullStorageEnabled = isFullInputStorageEnabled(reportConfiguration);
      final Configuration dialogContents = grabDialogContents(fullStorageEnabled);
      final ConfigStorage storage = ConfigFactory.getInstance().getUserStorage();
      storage.store(configPath, dialogContents);
    }
    catch (ConfigStoreException cse)
    {
      DebugLog.log("Unable to store the defaults in Export export dialog. [" + getClass() + "]");
    }
  }

  private Configuration loadFromConfigStore(final ReportJob reportJob,
                                            final Configuration defaultConfig)
  {
    final String configPath = ConfigFactory.encodePath(
        reportJob.getName() + getConfigurationSuffix());
    final ConfigStorage storage = ConfigFactory.getInstance().getUserStorage();
    try
    {
      return storage.load(configPath, defaultConfig);
    }
    catch (Exception cse)
    {
      DebugLog.log("Unable to load the defaults in Export export dialog. [" + getClass() + "]");
    }
    return defaultConfig;
  }

  protected abstract String getConfigurationPrefix();

  /**
   * Returns a new (and not connected to the default config from the job)
   * configuration containing all properties from the dialog.
   *
   * @param full
   * @return
   */
  protected abstract Configuration grabDialogContents(boolean full);

  protected abstract void setDialogContents(Configuration properties);

  protected abstract String getConfigurationSuffix();

  /**
   * Retrieves the resources for this dialog. If the resources are not
   * initialized, they get loaded on the first call to this method.
   *
   * @return this frames ResourceBundle.
   */
  protected ResourceBundle getResources()
  {
    if (resources == null)
    {
      resources = ResourceBundle.getBundle(getResourceBaseName());
    }
    return resources;
  }

  protected boolean isInputStorageEnabled(Configuration config)
  {
    final String confVal = config.getConfigProperty
        (getConfigurationPrefix() + "StoreDialogContents");
    return "none".equalsIgnoreCase(confVal) == false;
  }

  protected boolean isFullInputStorageEnabled(Configuration config)
  {
    final String confVal = config.getConfigProperty
        (getConfigurationPrefix() + "StoreDialogContents");
    return "all".equalsIgnoreCase(confVal);
  }

  /**
   * Returns <code>true</code> if the user confirmed the selection, and
   * <code>false</code> otherwise.  The file should only be saved if the result
   * is <code>true</code>.
   *
   * @return A boolean.
   */
  public boolean isConfirmed()
  {
    return confirmed;
  }

  /**
   * Defines whether this dialog has been finished using the 'OK' or the
   * 'Cancel' option.
   *
   * @param confirmed set to <code>true</code>, if OK was pressed,
   *                  <code>false</code> otherwise
   */
  protected void setConfirmed(final boolean confirmed)
  {
    this.confirmed = confirmed;
  }

  protected boolean performConfirm()
  {
    return true;
  }

  public abstract void clear();

  protected abstract String getResourceBaseName();


  /**
   * Resolves file names for the exports. An occurence of "~/" at the beginning
   * of the name will be replaced with the users home directory.
   *
   * @param baseDirectory the base directory as specified in the configuration.
   * @return the file object pointing to that directory.
   * @throws IllegalArgumentException if the base directory is null.
   */
  protected File resolvePath(String baseDirectory)
  {
    if (baseDirectory == null)
    {
      throw new IllegalArgumentException("The base directory must not be null");
    }

    if (baseDirectory.startsWith("~/") == false)
    {
      return new File(baseDirectory);
    }
    else
    {
      final String homeDirectory = System.getProperty("user.home");
      if ("~/".equals(baseDirectory))
      {
        return new File(homeDirectory);
      }
      else
      {
        baseDirectory = baseDirectory.substring(2);
        return new File(homeDirectory, baseDirectory);
      }
    }
  }
}
