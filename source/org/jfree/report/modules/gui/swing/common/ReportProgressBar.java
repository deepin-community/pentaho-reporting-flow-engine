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
 * $Id: ReportProgressBar.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.common;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.jfree.report.event.ReportProgressEvent;
import org.jfree.report.event.ReportProgressListener;
import org.jfree.report.modules.gui.common.GuiCommonModule;

public class ReportProgressBar extends JProgressBar
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
      //updatePassMessage(activity, currentRow, page);
      setValue(currentRow);
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

  /**
   * The reuseable message format for the page label.
   */
  private MessageFormat pageMessageFormatter;
  /**
   * The reuseable message format for the pass label.
   */
  private MessageFormat currentRowFormatter;
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
   * Creates a horizontal progress bar that displays a border but no progress string. The
   * initial and minimum values are 0, and the maximum is 100.
   *
   * @see #setOrientation
   * @see #setBorderPainted
   * @see #setStringPainted
   * @see #setString
   * @see #setIndeterminate
   */
  public ReportProgressBar (Locale locale)
  {
    setLocale(locale);
    initialize();
  }

  /**
   * Creates a horizontal progress bar that displays a border but no progress
   * string. The initial and minimum values are 0, and the maximum is 100.
   *
   * @see #setOrientation
   * @see #setBorderPainted
   * @see #setStringPainted
   * @see #setString
   * @see #setIndeterminate
   */
  public ReportProgressBar()
  {
    setLocale(Locale.getDefault());
    initialize();
  }

  private void initialize()
  {
    resources = ResourceBundle.getBundle(GuiCommonModule.RESOURCE_BASE_NAME);
    pageMessageFormatter = new MessageFormat
        (resources.getString("progress-dialog.page-label"));
    currentRowFormatter = new MessageFormat
        (resources.getString("progress-dialog.current-row-label"));
  }
}
