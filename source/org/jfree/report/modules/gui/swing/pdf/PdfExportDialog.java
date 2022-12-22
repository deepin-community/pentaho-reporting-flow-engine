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
 * $Id: PdfExportDialog.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.pdf;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.jfree.layouting.modules.output.pdf.PdfOutputModule;
import org.jfree.report.flow.ReportJob;
import org.jfree.report.modules.gui.common.GuiContext;
import org.jfree.report.modules.gui.swing.common.AbstractExportDialog;
import org.jfree.report.modules.gui.swing.common.EncodingComboBoxModel;
import org.jfree.report.modules.gui.swing.common.JStatusBar;
import org.jfree.report.modules.gui.swing.common.action.ActionButton;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

/**
 * Creation-Date: 02.12.2006, 15:27:30
 *
 * @author Thomas Morgner
 */
public class PdfExportDialog extends AbstractExportDialog
{
  /** Useful constant. */
  private static final int CBMODEL_NOPRINTING = 0;

  /** Useful constant. */
  private static final int CBMODEL_DEGRADED = 1;

  /** Useful constant. */
  private static final int CBMODEL_FULL = 2;

  /**
   * Internal action class to enable/disable the Security-Settings panel.
   * Without encryption a pdf file cannot have any security settings enabled.
   */
  private class ActionSecuritySelection extends AbstractAction
  {
    /** Default constructor. */
    protected ActionSecuritySelection()
    {
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e the action event.
     */
    public void actionPerformed(final ActionEvent e)
    {
      updateSecurityPanelEnabled();
    }
  }

  /** Internal action class to select a target file. */
  private class ActionSelectFile extends AbstractAction
  {
    /** Default constructor. */
    protected ActionSelectFile(final ResourceBundle resources)
    {
      putValue(Action.NAME, resources.getString("pdfsavedialog.selectFile"));
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e the action event.
     */
    public void actionPerformed(final ActionEvent e)
    {
      performSelectFile();
    }
  }

  /** Security (none) radio button. */
  private JRadioButton rbSecurityNone;

  /** Security (40 bit) radio button. */
  private JRadioButton rbSecurity40Bit;

  /** Security (128 bit) radio button. */
  private JRadioButton rbSecurity128Bit;

  /** User password text field. */
  private JTextField txUserPassword;

  /** Owner password text field. */
  private JTextField txOwnerPassword;

  /** Confirm user password text field. */
  private JTextField txConfUserPassword;

  /** Confirm ownder password text field. */
  private JTextField txConfOwnerPassword;

  /** Allow copy check box. */
  private JCheckBox cxAllowCopy;

  /** Allow screen readers check box. */
  private JCheckBox cxAllowScreenReaders;

  /** Allow printing check box. */
  private JComboBox cbAllowPrinting;

  /** Allow assembly check box. */
  private JCheckBox cxAllowAssembly;

  /** Allow modify contents check box. */
  private JCheckBox cxAllowModifyContents;

  /** Allow modify annotations check box. */
  private JCheckBox cxAllowModifyAnnotations;

  /** Allow fill in check box. */
  private JCheckBox cxAllowFillIn;

  /** A model for the available encodings. */
  private EncodingComboBoxModel encodingModel;

  /** A file chooser. */
  private JFileChooser fileChooser;
  private static final String PDF_FILE_EXTENSION = ".pdf";
  private JStatusBar statusBar;
  private JTextField txFilename;
  private DefaultComboBoxModel printingModel;

  /**
   * Creates a non-modal dialog without a title and without a specified
   * <code>Frame</code> owner.  A shared, hidden frame will be set as the owner
   * of the dialog.
   */
  public PdfExportDialog()
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
  public PdfExportDialog(final Frame owner)
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
  public PdfExportDialog(final Dialog owner)
  {
    super(owner);
    initializeComponents();
  }

  private void initializeComponents ()
  {
    final JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new GridBagLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

    final JLabel lblFileName = new JLabel(getResources().getString("pdfsavedialog.filename"));
    final JLabel lblEncoding = new JLabel(getResources().getString("pdfsavedialog.encoding"));

    final JButton btnSelect = new ActionButton(new ActionSelectFile(getResources()));
    txFilename = new JTextField();
    statusBar = new JStatusBar();

    encodingModel = EncodingComboBoxModel.createDefaultModel(Locale.getDefault());
    encodingModel.addEncodingUnchecked("Identity-H", "PDF-Unicode encoding");
    encodingModel.addEncodingUnchecked("Identity-V", "PDF-Unicode encoding");
    encodingModel.sort();

    final JComboBox cbEncoding = new JComboBox(encodingModel);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(3, 1, 1, 1);
    mainPanel.add(lblFileName, gbc);

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.ipadx = 120;
    gbc.insets = new Insets(3, 1, 1, 1);
    mainPanel.add(txFilename, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.gridx = 2;
    gbc.gridy = 0;
    mainPanel.add(btnSelect, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets(1, 1, 1, 1);
    mainPanel.add(lblEncoding, gbc);

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.ipadx = 120;
    gbc.insets = new Insets(1, 1, 1, 1);
    mainPanel.add(cbEncoding, gbc);

    final JButton btnCancel = new ActionButton(getCancelAction());
    final JButton btnConfirm = new ActionButton(getConfirmAction());
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 2, 5, 5));
    buttonPanel.add(btnConfirm);
    buttonPanel.add(btnCancel);
    btnConfirm.setDefaultCapable(true);
    getRootPane().setDefaultButton(btnConfirm);
    buttonPanel.registerKeyboardAction(getConfirmAction(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    final JPanel buttonCarrier = new JPanel();
    buttonCarrier.setLayout(new FlowLayout(FlowLayout.RIGHT));
    buttonCarrier.add(buttonPanel);

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.weightx = 1;
    gbc.gridx = 0;
    gbc.gridwidth = 3;
    gbc.gridy = 6;
    gbc.insets = new Insets(10, 0, 0, 0);

    final JPanel mainPaneCarrier = new JPanel();
    mainPaneCarrier.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
    mainPaneCarrier.setLayout(new BorderLayout());
    mainPaneCarrier.add(mainPanel, BorderLayout.NORTH);

    final JPanel securityPaneCarrier = new JPanel();
    securityPaneCarrier.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
    securityPaneCarrier.setLayout(new BorderLayout());
    securityPaneCarrier.add(createSecurityPanel(), BorderLayout.NORTH);

    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.add("Export-Settings", mainPaneCarrier);
    tabbedPane.add("Security", securityPaneCarrier);

    final JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(tabbedPane, BorderLayout.CENTER);
    contentPane.add(buttonCarrier, BorderLayout.SOUTH);

    final JPanel contentWithStatus = new JPanel();
    contentWithStatus.setLayout(new BorderLayout());
    contentWithStatus.add(contentPane, BorderLayout.CENTER);
    contentWithStatus.add(getStatusBar(), BorderLayout.SOUTH);

    setContentPane(contentWithStatus);

    getFormValidator().registerTextField(txFilename);
    getFormValidator().registerTextField(txConfOwnerPassword);
    getFormValidator().registerTextField(txConfUserPassword);
    getFormValidator().registerTextField(txUserPassword);
    getFormValidator().registerTextField(txOwnerPassword);

  }


  public JStatusBar getStatusBar()
  {
    return statusBar;
  }

  protected boolean performConfirm()
  {
    final String filename = txFilename.getText();
    final File f = new File(filename);
    if (f.exists())
    {
      final String key1 = "pdfsavedialog.targetOverwriteConfirmation";
      final String key2 = "pdfsavedialog.targetOverwriteTitle";
      if (JOptionPane.showConfirmDialog(this,
              MessageFormat.format(getResources().getString(key1),
                      new Object[]{txFilename.getText()}),
              getResources().getString(key2),
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
              == JOptionPane.NO_OPTION)
      {
        return false;
      }
    }

    if (getEncryptionValue().equals(PdfOutputModule.SECURITY_ENCRYPTION_128BIT)
            || getEncryptionValue().equals(
            PdfOutputModule.SECURITY_ENCRYPTION_40BIT))
    {
      if (txOwnerPassword.getText().trim().length() == 0)
      {
        if (JOptionPane.showConfirmDialog(this,
                getResources().getString("pdfsavedialog.ownerpasswordEmpty"),
                getResources().getString("pdfsavedialog.warningTitle"),
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
                == JOptionPane.NO_OPTION)
        {
          return false;
        }
      }
    }
    return true;
  }

  protected boolean performValidate()
  {
    getStatusBar().clear();

    final String filename = txFilename.getText();
    if (filename.trim().length() == 0)
    {
      getStatusBar().setStatus(JStatusBar.TYPE_ERROR,
              getResources().getString("pdfsavedialog.targetIsEmpty"));
      return false;
    }
    final File f = new File(filename);
    if (f.exists())
    {
      if (f.isFile() == false)
      {
        getStatusBar().setStatus(JStatusBar.TYPE_ERROR,
                getResources().getString("pdfsavedialog.targetIsNoFile"));
        return false;
      }
      if (f.canWrite() == false)
      {
        getStatusBar().setStatus(JStatusBar.TYPE_ERROR,
                getResources().getString("pdfsavedialog.targetIsNotWritable"));
        return false;
      }

      final String message = MessageFormat.format(getResources().getString
              ("pdfsavedialog.targetOverwriteWarning"),
              new Object[]{filename});
      getStatusBar().setStatus(JStatusBar.TYPE_WARNING, message);
    }

    if (getEncryptionValue().equals(PdfOutputModule.SECURITY_ENCRYPTION_128BIT)
            || getEncryptionValue().equals(
            PdfOutputModule.SECURITY_ENCRYPTION_40BIT))
    {
      if (txUserPassword.getText().equals(
              txConfUserPassword.getText()) == false)
      {
        getStatusBar().setStatus(JStatusBar.TYPE_ERROR,
                getResources().getString("pdfsavedialog.userpasswordNoMatch"));
        return false;
      }
      if (txOwnerPassword.getText().equals(
              txConfOwnerPassword.getText()) == false)
      {
        getStatusBar().setStatus(JStatusBar.TYPE_ERROR,
                getResources().getString("pdfsavedialog.ownerpasswordNoMatch"));
        return false;
      }
    }

    return true;
  }

  protected void initializeFromJob(final ReportJob job, final GuiContext guiContext)
  {
    statusBar.setIconTheme(guiContext.getIconTheme());

//    encodingModel = EncodingComboBoxModel.createDefaultModel(Locale.getDefault());
//    encodingModel.addEncodingUnchecked("Identity-H", "PDF-Unicode encoding");
//    encodingModel.addEncodingUnchecked("Identity-V", "PDF-Unicode encoding");
//    encodingModel.sort();
//    cbEncoding.setModel(encodingModel);
  }

  protected String getConfigurationPrefix()
  {
    return "org.jfree.report.modules.gui.common.pdf.";
  }

  /**
   * Returns a new (and not connected to the default config from the job)
   * configuration containing all properties from the dialog.
   *
   * @param full
   */
  protected Configuration grabDialogContents(final boolean full)
  {
    final DefaultConfiguration config = new DefaultConfiguration();

    final String prefix = getConfigurationPrefix();
    config.setConfigProperty(prefix + "TargetFileName", txFilename.getText());
    config.setConfigProperty(prefix + "Encoding", encodingModel.getSelectedEncoding());
    config.getConfigProperty(prefix + "security.PrintLevel", getPrintLevel());
    config.getConfigProperty(prefix + "security.Encryption", getEncryptionValue());


    config.getConfigProperty(prefix + "security.UserPassword", txUserPassword.getText());
    config.getConfigProperty(prefix + "security.OwnerPassword", txOwnerPassword.getText());

    config.setConfigProperty(prefix + "security.AllowAssembly",
        String.valueOf(cxAllowAssembly.isSelected()));
    config.setConfigProperty(prefix + "security.AllowCopy",
        String.valueOf(cxAllowCopy.isSelected()));
    config.setConfigProperty(prefix + "security.AllowFillIn",
        String.valueOf(cxAllowFillIn.isSelected()));
    config.setConfigProperty(prefix + "security.AllowModifyAnnotations",
        String.valueOf(cxAllowModifyAnnotations.isSelected()));
    config.setConfigProperty(prefix + "security.AllowModifyContents",
        String.valueOf(cxAllowModifyContents.isSelected()));
    config.setConfigProperty(prefix + "security.AllowScreenReaders",
        String.valueOf(cxAllowScreenReaders.isSelected()));
    return config;
  }

  protected void setDialogContents(final Configuration config)
  {
    final String prefix = getConfigurationPrefix();
    txFilename.setText(config.getConfigProperty(prefix + "TargetFileName"));
    final String encoding = config.getConfigProperty(prefix + "Encoding");
    if (encoding != null && encoding.length() > 0)
    {
      encodingModel.setSelectedEncoding(encoding);
    }
    setPrintLevel(config.getConfigProperty(prefix + "security.PrintLevel"));
    setEncryptionValue(config.getConfigProperty(prefix + "security.Encryption"));

    txUserPassword.setText(config.getConfigProperty(prefix + "security.UserPassword"));
    txOwnerPassword.setText(config.getConfigProperty(prefix + "security.OwnerPassword"));
    txConfUserPassword.setText(config.getConfigProperty(prefix + "security.UserPassword"));
    txConfOwnerPassword.setText(config.getConfigProperty(prefix + "security.OwnerPassword"));

    cxAllowAssembly.setSelected("true".equals
        (config.getConfigProperty(prefix + "security.AllowAssembly")));
    cxAllowCopy.setSelected("true".equals
        (config.getConfigProperty(prefix + "security.AllowCopy")));
    cxAllowFillIn.setSelected("true".equals
        (config.getConfigProperty(prefix + "security.AllowFillIn")));
    cxAllowModifyAnnotations.setSelected("true".equals
        (config.getConfigProperty(prefix + "security.AllowModifyAnnotations")));
    cxAllowModifyContents.setSelected("true".equals
        (config.getConfigProperty(prefix + "security.AllowModifyContents")));
    cxAllowScreenReaders.setSelected("true".equals
        (config.getConfigProperty(prefix + "security.AllowScreenReaders")));
  }

  protected String getConfigurationSuffix()
  {
    return "_pdf_export";
  }

  public void clear()
  {
    txConfOwnerPassword.setText("");
    txConfUserPassword.setText("");
    txFilename.setText("");
    txOwnerPassword.setText("");
    txUserPassword.setText("");

    cxAllowAssembly.setSelected(false);
    cxAllowCopy.setSelected(false);
    cbAllowPrinting.setSelectedIndex(CBMODEL_NOPRINTING);
    cxAllowFillIn.setSelected(false);
    cxAllowModifyAnnotations.setSelected(false);
    cxAllowModifyContents.setSelected(false);
    cxAllowScreenReaders.setSelected(false);

    rbSecurityNone.setSelected(true);
    updateSecurityPanelEnabled();

    final String plattformDefaultEncoding = EncodingRegistry.getPlatformDefaultEncoding();
    encodingModel.setSelectedEncoding(plattformDefaultEncoding);
  }

  protected String getResourceBaseName()
  {
    return SwingPdfModule.BUNDLE_NAME;
  }


  /**
   * Updates the security panel state. If no encryption is selected, all
   * security setting components will be disabled.
   */
  protected void updateSecurityPanelEnabled()
  {
    final boolean b = (rbSecurityNone.isSelected() == false);
    txUserPassword.setEnabled(b);
    txOwnerPassword.setEnabled(b);
    txConfOwnerPassword.setEnabled(b);
    txConfUserPassword.setEnabled(b);
    cxAllowAssembly.setEnabled(b);
    cxAllowCopy.setEnabled(b);
    cbAllowPrinting.setEnabled(b);
    cxAllowFillIn.setEnabled(b);
    cxAllowModifyAnnotations.setEnabled(b);
    cxAllowModifyContents.setEnabled(b);
    cxAllowScreenReaders.setEnabled(b);
  }

  /** Initializes the class member components of the security panel. */
  private void createSecurityPanelComponents()
  {
    txUserPassword = new JPasswordField();
    txConfUserPassword = new JPasswordField();
    txOwnerPassword = new JPasswordField();
    txConfOwnerPassword = new JPasswordField();

    cxAllowCopy = new JCheckBox(getResources().getString(
            "pdfsavedialog.allowCopy"));
    cbAllowPrinting = new JComboBox(getPrintingComboBoxModel());
    cxAllowScreenReaders =
            new JCheckBox(getResources().getString(
                    "pdfsavedialog.allowScreenreader"));

    cxAllowAssembly = new JCheckBox(getResources().getString(
            "pdfsavedialog.allowAssembly"));
    cxAllowModifyContents =
            new JCheckBox(getResources().getString(
                    "pdfsavedialog.allowModifyContents"));
    cxAllowModifyAnnotations =
            new JCheckBox(getResources().getString(
                    "pdfsavedialog.allowModifyAnnotations"));
    cxAllowFillIn = new JCheckBox(getResources().getString(
            "pdfsavedialog.allowFillIn"));

  }

  /**
   * Creates a panel for the security settings.
   *
   * @return The panel.
   */
  private JPanel createSecurityPanel()
  {
    final JPanel securityPanel = new JPanel();
    securityPanel.setLayout(new GridBagLayout());

    createSecurityPanelComponents();

    final JLabel lblUserPass = new JLabel(getResources().getString(
            "pdfsavedialog.userpassword"));
    final JLabel lblUserPassConfirm =
            new JLabel(getResources().getString(
                    "pdfsavedialog.userpasswordconfirm"));
    final JLabel lblOwnerPass =
            new JLabel(getResources().getString("pdfsavedialog.ownerpassword"));
    final JLabel lblOwnerPassConfirm =
            new JLabel(getResources().getString(
                    "pdfsavedialog.ownerpasswordconfirm"));
    final JLabel lbAllowPrinting =
            new JLabel(getResources().getString("pdfsavedialog.allowPrinting"));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 0;
    gbc.gridwidth = 4;
    gbc.gridy = 0;
    gbc.insets = new Insets(5, 5, 5, 5);
    securityPanel.add(createSecurityConfigPanel(), gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets(5, 5, 5, 5);
    securityPanel.add(lblUserPass, gbc);

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.ipadx = 120;
    gbc.insets = new Insets(5, 5, 5, 5);
    securityPanel.add(txUserPassword, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.insets = new Insets(5, 5, 5, 5);
    securityPanel.add(lblOwnerPass, gbc);

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.ipadx = 120;
    gbc.insets = new Insets(5, 5, 5, 5);
    securityPanel.add(txOwnerPassword, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.insets = new Insets(5, 5, 5, 5);
    securityPanel.add(lblUserPassConfirm, gbc);

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 3;
    gbc.gridy = 1;
    gbc.ipadx = 120;
    gbc.insets = new Insets(5, 5, 5, 5);
    securityPanel.add(txConfUserPassword, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 2;
    gbc.gridy = 2;
    gbc.insets = new Insets(5, 5, 5, 5);
    securityPanel.add(lblOwnerPassConfirm, gbc);

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 3;
    gbc.gridy = 2;
    gbc.ipadx = 120;
    gbc.insets = new Insets(5, 5, 5, 5);
    securityPanel.add(txConfOwnerPassword, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add(cxAllowCopy, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add(cxAllowScreenReaders, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.gridy = 5;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add(cxAllowFillIn, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridwidth = 2;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add(cxAllowAssembly, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridwidth = 2;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add(cxAllowModifyContents, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridwidth = 2;
    gbc.gridy = 5;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add(cxAllowModifyAnnotations, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridwidth = 1;
    gbc.gridy = 6;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add(lbAllowPrinting, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridwidth = 3;
    gbc.gridy = 6;
    gbc.anchor = GridBagConstraints.WEST;
    securityPanel.add(cbAllowPrinting, gbc);

    return securityPanel;
  }

  /**
   * Creates the security config panel. This panel is used to select the level
   * of the PDF security.
   *
   * @return the created security config panel.
   */
  private JPanel createSecurityConfigPanel()
  {
    rbSecurityNone = new JRadioButton(getResources().getString(
            "pdfsavedialog.securityNone"));
    rbSecurity40Bit = new JRadioButton(getResources().getString(
            "pdfsavedialog.security40bit"));
    rbSecurity128Bit = new JRadioButton(getResources().getString(
            "pdfsavedialog.security128bit"));

    final Action securitySelectAction = new ActionSecuritySelection();
    rbSecurityNone.addActionListener(securitySelectAction);
    rbSecurity40Bit.addActionListener(securitySelectAction);
    rbSecurity128Bit.addActionListener(securitySelectAction);

    rbSecurity128Bit.setSelected(true);

    final JPanel pnlSecurityConfig = new JPanel();
    pnlSecurityConfig.setLayout(new GridLayout());
    pnlSecurityConfig.add(rbSecurityNone);
    pnlSecurityConfig.add(rbSecurity40Bit);
    pnlSecurityConfig.add(rbSecurity128Bit);

    final ButtonGroup btGrpSecurity = new ButtonGroup();
    btGrpSecurity.add(rbSecurity128Bit);
    btGrpSecurity.add(rbSecurity40Bit);
    btGrpSecurity.add(rbSecurityNone);

    return pnlSecurityConfig;
  }

  /**
   * Gets and initializes the the combobox model for the security setting
   * "allowPrinting".
   *
   * @return the combobox model containing the different values for the
   *         allowPrinting option.
   */
  private DefaultComboBoxModel getPrintingComboBoxModel()
  {
    if (printingModel == null)
    {
      final Object[] data = {
              getResources().getString("pdfsavedialog.option.noprinting"),
              getResources().getString("pdfsavedialog.option.degradedprinting"),
              getResources().getString("pdfsavedialog.option.fullprinting")
      };
      printingModel = new DefaultComboBoxModel(data);
    }
    return printingModel;
  }


  /** selects a file to use as target for the report processing. */
  protected void performSelectFile()
  {
    // lazy initialize ... the file chooser is one of the hot spots here ...
    if (fileChooser == null)
    {
      fileChooser = new JFileChooser();
      final FilesystemFilter filter = new FilesystemFilter(PDF_FILE_EXTENSION,
              "PDF Documents");
      fileChooser.addChoosableFileFilter(filter);
      fileChooser.setMultiSelectionEnabled(false);
    }

    final File file = new File(txFilename.getText());
    fileChooser.setCurrentDirectory(file);
    fileChooser.setSelectedFile(file);
    final int option = fileChooser.showSaveDialog(this);
    if (option == JFileChooser.APPROVE_OPTION)
    {
      final File selFile = fileChooser.getSelectedFile();
      String selFileName = selFile.getAbsolutePath();

      // Test if ends of pdf
      if (selFileName.toLowerCase().endsWith(PDF_FILE_EXTENSION) == false)
      {
        selFileName = selFileName + PDF_FILE_EXTENSION;
      }
      txFilename.setText(selFileName);
    }
  }

  /**
   * Defines whether the user is allowed to print the file.  If this right is
   * granted, the user is also able to print a degraded version of the file,
   * regardless of the <code>allowDegradedPrinting</code< property. If you
   * disabled printing but enabled degraded printing, then the user is able to
   * print a low-quality version of the document.
   *
   */
  public void setPrintLevel(final String printLevel)
  {
    if ("full".equals(printLevel))
    {
      this.cbAllowPrinting.setSelectedIndex(CBMODEL_FULL);
    }
    else if ("degraded".equals(printLevel))
    {
        this.cbAllowPrinting.setSelectedIndex(CBMODEL_DEGRADED);
    }
    else
    {
      this.cbAllowPrinting.setSelectedIndex(CBMODEL_NOPRINTING);
    }
  }

  public String getPrintLevel ()
  {
    if (cbAllowPrinting.getSelectedIndex() == CBMODEL_FULL)
    {
      return "full";
    }
    if (cbAllowPrinting.getSelectedIndex() == CBMODEL_DEGRADED)
    {
      return "degraded";
    }
    return "none";
  }


  /**
   * Queries the currently selected encryption. If an encryption is selected
   * this method returns either Boolean.TRUE or Boolean.FALSE, when no
   * encryption is set, <code>null</code> is returned. If no encryption is set,
   * the security properties have no defined state.
   *
   * @return the selection state for the encryption. If no encryption is set,
   *         this method returns null, if 40-bit encryption is set, the method
   *         returns Boolean.FALSE and on 128-Bit-encryption, Boolean.TRUE is
   *         returned.
   */
  public String getEncryptionValue()
  {
    if (rbSecurity40Bit.isSelected())
    {
      return PdfOutputModule.SECURITY_ENCRYPTION_40BIT;
    }
    if (rbSecurity128Bit.isSelected())
    {
      return PdfOutputModule.SECURITY_ENCRYPTION_128BIT;
    }
    return PdfOutputModule.SECURITY_ENCRYPTION_NONE;
  }

  /**
   * Defines the currently selected encryption.
   *
   * @param b the new encryption state, one of null, Boolean.TRUE or
   *          Boolean.FALSE
   */
  public void setEncryptionValue(final String b)
  {
    if (b != null)
    {
      if (b.equals(PdfOutputModule.SECURITY_ENCRYPTION_128BIT))
      {
        rbSecurity128Bit.setSelected(true);
        updateSecurityPanelEnabled();
        return;
      }
      else if (b.equals(PdfOutputModule.SECURITY_ENCRYPTION_40BIT))
      {
        rbSecurity40Bit.setSelected(true);
        updateSecurityPanelEnabled();
        return;
      }
      else if (b.equals(PdfOutputModule.SECURITY_ENCRYPTION_NONE) == false)
      {
        DebugLog.log("Invalid encryption value entered. " + b);
      }
    }
    rbSecurityNone.setSelected(true);
    updateSecurityPanelEnabled();
  }
}
