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
 * $Id: HtmlZipExportDialog.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.html;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.jfree.report.flow.ReportJob;
import org.jfree.report.modules.gui.common.DefaultIconTheme;
import org.jfree.report.modules.gui.common.GuiContext;
import org.jfree.report.modules.gui.swing.common.AbstractExportDialog;
import org.jfree.report.modules.gui.swing.common.JStatusBar;
import org.jfree.report.modules.gui.swing.common.action.ActionButton;
import org.jfree.report.modules.gui.swing.common.localization.JLabelLocaleUpdateHandler;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * A dialog that is used to perform the printing of a report into an HTML file.
 */
public class HtmlZipExportDialog extends AbstractExportDialog
{
  private static final String ZIP_FILE_EXTENSION = ".zip";

  /**
   * An action to select the export target file.
   */
  private class ActionSelectTargetFile extends AbstractAction
  {
    /**
     * Default constructor.
     */
    public ActionSelectTargetFile (final ResourceBundle resources)
    {
      putValue(Action.NAME, resources.getString("htmlexportdialog.select"));
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e the action event.
     */
    public void actionPerformed (final ActionEvent e)
    {
      performSelectFile();
    }

  }

  private JTextField filenameField;
  private JFileChooser fileChooserHtml;
  private JTextField dataDirField;
  private JStatusBar statusBar;
  private JRadioButton rbPageableExport;
  private JRadioButton rbStreamExport;
  private JRadioButton rbFlowExport;


  /**
   * Creates a non-modal dialog without a title and without a specified
   * <code>Frame</code> owner.  A shared, hidden frame will be set as the owner
   * of the dialog.
   */
  public HtmlZipExportDialog()
  {
    initializeComponents();
  }

  /**
   * Creates a non-modal dialog without a title with the specified
   * <code>Frame</code> as its owner.  If <code>owner</code> is
   * <code>null</code>, a shared, hidden frame will be set as the owner of the
   * dialog.
   *
   * @param owner the <code>Frame</code> from which the dialog is displayed
   */
  public HtmlZipExportDialog(final Frame owner)
  {
    super(owner);
    initializeComponents();
  }

  /**
   * Creates a non-modal dialog without a title with the specified
   * <code>Dialog</code> as its owner.
   *
   * @param owner the non-null <code>Dialog</code> from which the dialog is
   *              displayed
   */
  public HtmlZipExportDialog(final Dialog owner)
  {
    super(owner);
    initializeComponents();
  }

  public String getFilename()
  {
    return filenameField.getText();
  }

  public void setFilename(final String filename)
  {
    this.filenameField.setText(filename);
  }

  private void initializeComponents ()
  {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout(new GridBagLayout());

    filenameField = new JTextField();
    dataDirField = new JTextField();
    statusBar = new JStatusBar(new DefaultIconTheme());

    final JLabel targetLabel = new JLabel();
    addPropertyChangeListener(new JLabelLocaleUpdateHandler(targetLabel,
        SwingHtmlModule.BUNDLE_NAME, "htmlexportdialog.filename"));

    final JLabel dataLabel = new JLabel();
    addPropertyChangeListener(new JLabelLocaleUpdateHandler(dataLabel,
        SwingHtmlModule.BUNDLE_NAME, "htmlexportdialog.datafilename"));

    final JLabel exportMethodLabel =
        new JLabel(getResources().getString("htmlexportdialog.exportMethod"));
    addPropertyChangeListener("locale", new JLabelLocaleUpdateHandler(exportMethodLabel,
        SwingHtmlModule.BUNDLE_NAME, "htmlexportdialog.exportMethod"));


    rbStreamExport = new JRadioButton(getResources().getString
        ("htmlexportdialog.stream-export"));
    rbStreamExport.setSelected(true);
    rbFlowExport = new JRadioButton(getResources().getString
        ("htmlexportdialog.flow-export"));
    rbPageableExport = new JRadioButton(getResources().getString
        ("htmlexportdialog.pageable-export"));

    final ButtonGroup bgExport = new ButtonGroup();
    bgExport.add(rbStreamExport);
    bgExport.add(rbFlowExport);
    bgExport.add(rbPageableExport);

    final JPanel exportTypeSelectionPanel = new JPanel();
    exportTypeSelectionPanel.setLayout(new GridLayout(3, 1, 5, 5));
    exportTypeSelectionPanel.add(rbStreamExport);
    exportTypeSelectionPanel.add(rbFlowExport);
    exportTypeSelectionPanel.add(rbPageableExport);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(1, 1, 1, 5);
    contentPane.add(targetLabel, gbc);

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets(1, 1, 1, 5);
    contentPane.add(dataLabel, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 1;
    gbc.insets = new Insets(1, 1, 1, 1);
    contentPane.add(filenameField, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 2;
    gbc.gridy = 0;
    final HtmlZipExportDialog.ActionSelectTargetFile selectTargetAction =
        new HtmlZipExportDialog.ActionSelectTargetFile(getResources());
    contentPane.add(new ActionButton(selectTargetAction), gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 1;
    gbc.insets = new Insets(1, 1, 1, 1);
    contentPane.add(dataDirField, gbc);


    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 2;
    contentPane.add(exportMethodLabel, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(1, 1, 1, 1);
    contentPane.add(exportTypeSelectionPanel, gbc);


    final JButton btnCancel = new ActionButton(getCancelAction());
    final JButton btnConfirm = new ActionButton(getConfirmAction());

    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout());
    buttonPanel.add(btnConfirm);
    buttonPanel.add(btnCancel);
    btnConfirm.setDefaultCapable(true);
    buttonPanel.registerKeyboardAction(getConfirmAction(),
        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.weightx = 1;
    gbc.gridx = 0;
    gbc.gridwidth = 3;
    gbc.gridy = 15;
    gbc.insets = new Insets(10, 0, 10, 0);
    contentPane.add(buttonPanel, gbc);


    final JPanel contentWithStatus = new JPanel();
    contentWithStatus.setLayout(new BorderLayout());
    contentWithStatus.add(contentPane, BorderLayout.CENTER);
    contentWithStatus.add(statusBar, BorderLayout.SOUTH);

    setContentPane(contentWithStatus);

    getFormValidator().registerTextField(dataDirField);
    getFormValidator().registerTextField(filenameField);
  }


  public JStatusBar getStatusBar()
  {
    return statusBar;
  }

  protected boolean performValidate()
  {
    getStatusBar().clear();

    final String filename = getFilename();
    if (filename.trim().length() == 0)
    {
      getStatusBar().setStatus(JStatusBar.TYPE_ERROR,
              getResources().getString("htmlexportdialog.targetIsEmpty"));
      return false;
    }
    final File f = new File(filename);
    if (f.exists())
    {
      if (f.isFile() == false)
      {
        getStatusBar().setStatus(JStatusBar.TYPE_ERROR,
                getResources().getString("htmlexportdialog.targetIsNoFile"));
        return false;
      }
      if (f.canWrite() == false)
      {
        getStatusBar().setStatus(JStatusBar.TYPE_ERROR,
                getResources().getString("htmlexportdialog.targetIsNotWritable"));
        return false;
      }

      final String message = MessageFormat.format(getResources().getString
              ("htmlexportdialog.targetExistsWarning"),
              new Object[]{filename});
      getStatusBar().setStatus(JStatusBar.TYPE_WARNING, message);
    }

    try
    {
      final File dataDir = new File(dataDirField.getText());
      final File baseDir = new File("");

      if (IOUtils.getInstance().isSubDirectory(baseDir, dataDir) == false)
      {
        getStatusBar().setStatus(JStatusBar.TYPE_ERROR,
                getResources().getString("htmlexportdialog.targetPathIsAbsolute"));
        return false;
      }
    }
    catch (Exception e)
    {
      getStatusBar().setStatus(JStatusBar.TYPE_ERROR, "error.validationfailed");
      return false;
    }

    return true;
  }

  protected boolean performConfirm()
  {
    final String filename = getFilename();
    final File f = new File(filename).getAbsoluteFile();
    if (f.exists())
    {
      final String key1 = "htmlexportdialog.targetOverwriteConfirmation";
      final String key2 = "htmlexportdialog.targetOverwriteTitle";
      if (JOptionPane.showConfirmDialog(this,
              MessageFormat.format(getResources().getString(key1),
                      new Object[]{getFilename()}),
              getResources().getString(key2),
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
              == JOptionPane.NO_OPTION)
      {
        return false;
      }
    }

    return true;
  }

  protected void initializeFromJob(ReportJob job, final GuiContext guiContext)
  {
    statusBar.setIconTheme(guiContext.getIconTheme());
  }

  protected String getConfigurationPrefix()
  {
    return "org.jfree.report.modules.gui.common.html.zip.";
  }

  protected Configuration grabDialogContents(boolean full)
  {
    ModifiableConfiguration conf = new DefaultConfiguration();
    if (full)
    {
      conf.setConfigProperty
          ("org.jfree.report.modules.gui.common.html.zip.TargetFileName", filenameField.getText());
      conf.setConfigProperty
          ("org.jfree.report.modules.gui.common.html.zip.DataDirectory", dataDirField.getText());
    }
    conf.setConfigProperty
        ("org.jfree.report.modules.gui.common.html.zip.ExportMethod", getExportMethod());

    return conf;
  }

  protected void setDialogContents(Configuration properties)
  {
    filenameField.setText(properties.getConfigProperty
        ("org.jfree.report.modules.gui.common.html.zip.TargetFileName", ""));
    dataDirField.setText(properties.getConfigProperty
        ("org.jfree.report.modules.gui.common.html.zip.DataDirectory", ""));
    setExportMethod(properties.getConfigProperty
        ("org.jfree.report.modules.gui.common.html.zip.ExportMethod", ""));
  }


  protected String getConfigurationSuffix ()
  {
    return "_htmlexport_file";
  }

  public String getExportMethod()
  {
    if (rbPageableExport.isSelected())
    {
      return "pageable";
    }
    if (rbFlowExport.isSelected())
    {
      return "flow";
    }
    return "stream";
  }

  public void setExportMethod (String method)
  {
    if ("pageable".equals(method))
    {
      rbPageableExport.setSelected(true);
    }
    else if ("flow".equals(method))
    {
      rbFlowExport.setSelected(true);
    }
    else
    {
      rbStreamExport.setSelected(true);
    }
  }

  public void clear()
  {
    filenameField.setText("");
    dataDirField.setText("");
    rbStreamExport.setSelected(true);
  }

  protected String getResourceBaseName()
  {
    return SwingHtmlModule.BUNDLE_NAME;
  }

  /**
   * Selects a file to use as target for the report processing.
   */
  protected void performSelectFile ()
  {
    final File file = new File(getFilename());

    if (fileChooserHtml == null)
    {
      fileChooserHtml = new JFileChooser();
      fileChooserHtml.addChoosableFileFilter
              (new FilesystemFilter(new String[]{HtmlZipExportDialog.ZIP_FILE_EXTENSION},
                      getResources().getString("htmlexportdialog.zip-archives"), true));
      fileChooserHtml.setMultiSelectionEnabled(false);
    }

    fileChooserHtml.setCurrentDirectory(file);
    fileChooserHtml.setSelectedFile(file);
    final int option = fileChooserHtml.showSaveDialog(this);
    if (option == JFileChooser.APPROVE_OPTION)
    {
      final File selFile = fileChooserHtml.getSelectedFile();
      String selFileName = selFile.getAbsolutePath();

      // Test if ends on html
      if (StringUtils.endsWithIgnoreCase(selFileName, HtmlZipExportDialog.ZIP_FILE_EXTENSION) == false)
      {
        selFileName = selFileName + HtmlZipExportDialog.ZIP_FILE_EXTENSION;
      }
      setFilename(selFileName);
    }
  }

}
