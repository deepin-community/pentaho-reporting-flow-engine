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
 * $Id: PrinterUtility.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.printing;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import org.jfree.report.flow.ReportJob;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * Creation-Date: 03.12.2006, 15:01:24
 *
 * @author Thomas Morgner
 */
public class PrinterUtility
{
  public static final String PRINTER_JOB_NAME_KEY =
      "org.jfree.report.modules.gui.common.print.JobName";
  public static final String NUMBER_COPIES_KEY =
      "org.jfree.report.modules.gui.common.print.NumberOfCopies";

  private PrinterUtility()
  {
  }

  public static void printDirectly(final ReportJob report)
      throws PrinterException
  {
    final Configuration reportConfiguration = report.getConfiguration();
    final String jobName = reportConfiguration.getConfigProperty
        (PRINTER_JOB_NAME_KEY, "JFreeReport");

    final PrinterJob printerJob = PrinterJob.getPrinterJob();
    printerJob.setJobName(jobName);
    printerJob.setPageable(new PrintReportProcessor(report));
    printerJob.setCopies(getNumberOfCopies(reportConfiguration));

    // this tries to resolve at least some of the pain ..
//    final PageFormat pageFormat = report.getPageFormat();
//    if (pageFormat != null)
//    {
//      report.setPageFormat(printerJob.validatePage(pageFormat));
//    }
//    else
//    {
//      report.setPageFormat(printerJob.defaultPage());
//    }
    printerJob.print();
  }

  public static boolean print(final ReportJob report)
      throws PrinterException
  {
    final Configuration reportConfiguration = report.getConfiguration();
    final String jobName = reportConfiguration.getConfigProperty
        (PRINTER_JOB_NAME_KEY, "JFreeReport");

    final PrintReportProcessor document = new PrintReportProcessor(report);

    final PrinterJob printerJob = PrinterJob.getPrinterJob();
//    final PageFormat pageFormat = report.getPageFormat();
//    if (pageFormat != null)
//    {
//      report.setPageFormat(printerJob.validatePage(pageFormat));
//    }
//    else
//    {
//      report.setPageFormat(printerJob.defaultPage());
//    }

    printerJob.setJobName(jobName);
    printerJob.setPageable(document);
    printerJob.setCopies(getNumberOfCopies(reportConfiguration));
    if (printerJob.printDialog())
    {
      printerJob.print();
      return true;
    }
    return false;
  }

  public static int getNumberOfCopies(final Configuration configuration)
  {
    try
    {
      return Math.max(1, Integer.parseInt
          (configuration.getConfigProperty(NUMBER_COPIES_KEY, "1")));
    }
    catch (Exception e)
    {
      DebugLog.log("PrintUtil: Number of copies declared for the report is invalid");
      return 1;
    }
  }

}
