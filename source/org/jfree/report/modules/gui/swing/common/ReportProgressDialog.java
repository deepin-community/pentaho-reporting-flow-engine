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
 * $Id: ReportProgressDialog.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.common;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.jfree.report.event.ReportProgressEvent;
import org.jfree.report.event.ReportProgressListener;
import org.jfree.report.modules.gui.common.GuiCommonModule;


/**
 * A progress monitor dialog component that visualizes the report processing progress. It
 * will receive update events from the report processors and updates the UI according to
 * the latest event data.
 * <p/>
 * The progress will be computed according to the currently processed table row. This
 * approach provides relativly accurate data, but assumes that processing all bands
 * consumes roughly the same time.
 *
 * @author Thomas Morgner
 */
public class ReportProgressDialog extends JDialog
{
  private class ScreenUpdateRunnable implements Runnable
  {
    private int page;
    private int activity;
    private int currentRow;

    public ScreenUpdateRunnable (final int activity,
                                 final int currentRow,
                                 final int page)
    {
      this.activity = activity;
      this.currentRow = currentRow;
      this.page = page;
    }

    public void run ()
    {
      // todo
    }
  }

  private class ReportProgressHandler implements ReportProgressListener
  {
    public ReportProgressHandler()
    {
    }

    public void reportProcessingStarted(ReportProgressEvent event)
    {
      postUpdate(event);
    }

    public void reportProcessingUpdate(ReportProgressEvent event)
    {
      postUpdate(event);
    }

    public void reportProcessingFinished(ReportProgressEvent event)
    {
      postUpdate(event);
    }

    private void postUpdate (ReportProgressEvent event)
    {
      final ScreenUpdateRunnable runnable = new ScreenUpdateRunnable
              (event.getActivity(), event.getRow(), event.getPage());
      if (SwingUtilities.isEventDispatchThread())
      {
        runnable.run();
      }
      else
      {
        SwingUtilities.invokeLater(runnable);
      }
    }
  }

  private static class ToFrontHandler extends WindowAdapter
  {
    public ToFrontHandler()
    {
    }

    /**
     * Invoked when a window has been opened.
     */
    public void windowOpened (final WindowEvent e)
    {
      e.getWindow().toFront();
    }
  }

  /**
   * A label that carries the global message that describes the current task.
   */
  private JLabel messageCarrier;
  /**
   * A label containing the report processing pass count.
   */
  private JLabel passCountMessage;
  /**
   * A label containing the current page.
   */
  private JLabel pageCountMessage;
  /**
   * A label containing the currently processed row.
   */
  private JLabel rowCountMessage;
  /**
   * The progress bar that is used to visualize the progress.
   */
  private JProgressBar progressBar;
  /**
   * The reuseable message format for the page label.
   */
  private MessageFormat pageMessageFormatter;
  /**
   * The reuseable message format for the rows label.
   */
  private MessageFormat rowsMessageFormatter;
  /**
   * The reuseable message format for the pass label.
   */
  private MessageFormat passMessageFormatter;

  /**
   * The last page received.
   */
  private int lastPage;
  /**
   * The last pass values received.
   */
  private int lastPass;
  /**
   * The last max-row received.
   */
  private int lastMaxRow;
  /**
   * the cached value for the max-row value as integer.
   */
  private Integer lastMaxRowInteger;  // this values doesnt change much, so reduce GC work
  /**
   * a text which describes the layouting process.
   */
  private String layoutText;
  /**
   * a text that describes the export phase of the report processing.
   */
  private String outputText;


  /**
   * Localised resources.
   */
  private ResourceBundle resources;

  /**
   * Creates a non-modal dialog without a title and with the specified Dialog owner.
   *
   * @param dialog the owner of the dialog
   */
  public ReportProgressDialog (final Dialog dialog)
  {
    super(dialog);
    setLocale(dialog.getLocale());
    initConstructor();
  }

  /**
   * Creates a non-modal dialog without a title and with the specified Frame owner.
   *
   * @param frame the owner of the dialog
   */
  public ReportProgressDialog (final Frame frame)
  {
    super(frame);
    setLocale(frame.getLocale());
    initConstructor();
  }

  /**
   * Creates a non-modal dialog without a title and without a specified Frame owner.  A
   * shared, hidden frame will be set as the owner of the Dialog.
   */
  public ReportProgressDialog ()
  {
    initConstructor();
  }

  /**
   * Initializes the dialog (Non-GUI stuff).
   */
  private void initConstructor ()
  {
    resources = ResourceBundle.getBundle
        (GuiCommonModule.RESOURCE_BASE_NAME, getLocale());
    initialize();
    addWindowListener(new ToFrontHandler());

    setOutputText(resources.getString("progress-dialog.perform-output"));
    setLayoutText(resources.getString("progress-dialog.prepare-layout"));

    lastPass = -1;
    lastMaxRow = -1;
    lastPage = -1;
  }

  /**
   * Initializes the GUI components of this dialog.
   */
  private void initialize ()
  {
    final JPanel contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new GridBagLayout());

    pageMessageFormatter = new MessageFormat(resources.getString("progress-dialog.page-label"));
    rowsMessageFormatter = new MessageFormat(resources.getString("progress-dialog.rows-label"));
    passMessageFormatter = new MessageFormat(resources.getString("progress-dialog.pass-label"));

    messageCarrier = new JLabel(" ");
    passCountMessage = new JLabel(" ");
    rowCountMessage = new JLabel(" ");
    pageCountMessage = new JLabel(" ");
    progressBar = new JProgressBar();

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(3, 1, 5, 1);
    gbc.ipadx = 200;
    contentPane.add(messageCarrier, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.SOUTHWEST;
    gbc.insets = new Insets(3, 1, 1, 1);
    contentPane.add(passCountMessage, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets(3, 1, 1, 1);
    contentPane.add(progressBar, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.insets = new Insets(3, 1, 1, 1);
    contentPane.add(pageCountMessage, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.insets = new Insets(3, 10, 1, 1);
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPane.add(rowCountMessage, gbc);

    setContentPane(contentPane);
  }

  /**
   * Returns the current message.
   *
   * @return the current global message.
   */
  public String getMessage ()
  {
    return messageCarrier.getText();
  }

  /**
   * Defines the current message.
   *
   * @param message the current global message.
   */
  public void setMessage (final String message)
  {
    messageCarrier.setText(message);
  }

  /**
   * Updates the page message label if the current page has changed.
   *
   * @param page the new page parameter.
   */
  protected void updatePageMessage (final int page)
  {
    if (lastPage != page)
    {
      final Object[] parameters = new Object[]{new Integer(page)};
      pageCountMessage.setText(pageMessageFormatter.format(parameters));
      lastPage = page;
    }
  }

  /**
   * Updates the rows message label if either the rows or maxrows changed.
   *
   * @param rows    the currently processed rows.
   * @param maxRows the maximum number of rows in the report.
   */
  protected void updateRowsMessage (final int rows, final int maxRows)
  {
    if (maxRows != lastMaxRow)
    {
      lastMaxRowInteger = new Integer(maxRows);
      lastMaxRow = maxRows;
    }
    final Object[] parameters = new Object[]{
      new Integer(rows),
      lastMaxRowInteger
    };
    rowCountMessage.setText(rowsMessageFormatter.format(parameters));
  }

  /**
   * Updates the pass message label if either the pass or prepare state changed. The pass
   * reflects the current processing level, one level for every function dependency
   * level.
   *
   * @param pass    the current reporting pass.
   * @param prepare true, if the current run is a prepare run, false otherwise.
   */
  protected void updatePassMessage (final int pass, final boolean prepare)
  {
    if (lastPass != pass)
    {
      lastPass = pass;
      if (pass >= 0)
      {
        final Object[] parameters = new Object[]{new Integer(pass)};
        passCountMessage.setText(passMessageFormatter.format(parameters));
      }
      else
      {
        final String message;
        if (prepare)
        {
          message = getLayoutText();
        }
        else
        {
          message = getOutputText();
        }
        passCountMessage.setText(message);
      }
    }
  }

  /**
   * Returns the current pass message component.
   *
   * @return the pass message component.
   */
  protected final JLabel getPassCountMessage ()
  {
    return passCountMessage;
  }

  /**
   * Returns the current pagecount message component.
   *
   * @return the page message component.
   */
  protected final JLabel getPageCountMessage ()
  {
    return pageCountMessage;
  }

  /**
   * Returns the current row message component.
   *
   * @return the row message component.
   */
  protected final JLabel getRowCountMessage ()
  {
    return rowCountMessage;
  }

  /**
   * Returns the current pass message component.
   *
   * @return the pass message component.
   */
  protected final MessageFormat getPageMessageFormatter ()
  {
    return pageMessageFormatter;
  }

  /**
   * Returns the current pass message component.
   *
   * @return the pass message component.
   */
  protected final MessageFormat getRowsMessageFormatter ()
  {
    return rowsMessageFormatter;
  }

  /**
   * Returns the current pass message component.
   *
   * @return the pass message component.
   */
  protected final MessageFormat getPassMessageFormatter ()
  {
    return passMessageFormatter;
  }

  /**
   * Returns the output text message. This text describes the export phases of the report
   * processing.
   *
   * @return the output phase description.
   */
  public String getOutputText ()
  {
    return outputText;
  }

  /**
   * Defines the output text message. This text describes the export phases of the report
   * processing.
   *
   * @param outputText the output message.
   */
  public void setOutputText (final String outputText)
  {
    if (outputText == null)
    {
      throw new NullPointerException("OutputText must not be null.");
    }
    this.outputText = outputText;
  }

  /**
   * Returns the layout text. This text describes the prepare phases of the report
   * processing.
   *
   * @return the layout text.
   */
  public String getLayoutText ()
  {
    return layoutText;
  }

  /**
   * Defines the layout text message. This text describes the prepare phases of the report
   * processing.
   *
   * @param layoutText the layout message.
   */
  public void setLayoutText (final String layoutText)
  {
    if (layoutText == null)
    {
      throw new NullPointerException("LayoutText must not be null.");
    }
    this.layoutText = layoutText;
  }

  protected boolean isSameMaxRow (int row)
  {
    return lastMaxRow == row;
  }

}
