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
 * $Id: PdfExportTask.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import org.jfree.layouting.modules.output.pdf.PdfOutputProcessor;
import org.jfree.report.ReportConfigurationException;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.flow.ReportJob;
import org.jfree.report.flow.streaming.StreamingReportProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * Creation-Date: 02.12.2006, 15:34:17
 *
 * @author Thomas Morgner
 */
public class PdfExportTask implements Runnable
{
  private ReportJob job;
  private File targetFile;

  public PdfExportTask(final ReportJob job)
      throws ReportConfigurationException
  {
    if (job == null)
    {
      throw new NullPointerException();
    }
    this.job = job;
    final Configuration config = job.getConfiguration();
    final String targetFileName = config.getConfigProperty("org.jfree.report.modules.gui.common.pdf.TargetFileName");

    targetFile = new File(targetFileName);
    if (targetFile.exists())
    {
      if (targetFile.delete())
      {
        throw new ReportConfigurationException("Target-File exists, but cannot be removed.");
      }
    }
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to
   * create a thread, starting the thread causes the object's <code>run</code>
   * method to be called in that separately executing thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may take any
   * action whatsoever.
   *
   * @see Thread#run()
   */
  public void run()
  {
    try
    {
      final FileOutputStream fout = new FileOutputStream(targetFile);
      final StreamingReportProcessor sp = new StreamingReportProcessor();
      final PdfOutputProcessor outputProcessor = new PdfOutputProcessor(job.getConfiguration(), fout);
      sp.setOutputProcessor(outputProcessor);
      sp.processReport(job);

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        job.close();
        job = null;
      }
      catch(Exception e)
      {
        // ignore ..
      }
    }
  }
}
