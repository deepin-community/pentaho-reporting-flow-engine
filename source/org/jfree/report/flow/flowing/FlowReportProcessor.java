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
 * $Id: FlowReportProcessor.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.flow.flowing;

import org.jfree.layouting.ChainingLayoutProcess;
import org.jfree.layouting.DefaultLayoutProcess;
import org.jfree.layouting.LayoutProcess;
import org.jfree.layouting.output.OutputProcessor;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.flow.AbstractReportProcessor;
import org.jfree.report.flow.LibLayoutReportTarget;
import org.jfree.report.flow.ReportJob;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * This is written to use LibLayout. It will never work with other report
 * targets.
 *
 * @author Thomas Morgner
 */
public class FlowReportProcessor extends AbstractReportProcessor
{
  private OutputProcessor outputProcessor;

  public FlowReportProcessor()
  {
  }

  public OutputProcessor getOutputProcessor()
  {
    return outputProcessor;
  }

  public void setOutputProcessor(final OutputProcessor outputProcessor)
  {
    this.outputProcessor = outputProcessor;
  }

  protected LibLayoutReportTarget createTarget(final ReportJob job)
  {
    if (outputProcessor == null)
    {
      throw new IllegalStateException(
              "OutputProcessor is invalid.");
    }
    final LayoutProcess layoutProcess =
            new ChainingLayoutProcess(new DefaultLayoutProcess(outputProcessor));
    final ResourceManager resourceManager = job.getReportStructureRoot().getResourceManager();
    final ResourceKey resourceKey = job.getReportStructureRoot().getBaseResource();

    return new LibLayoutReportTarget
            (job, resourceKey, resourceManager, layoutProcess);
  }

  /**
   * Bootstraps the local report processing. This way of executing the report
   * must be supported by *all* report processor implementations. It should
   * fully process the complete report.
   *
   * @param job
   * @throws ReportDataFactoryException
   */
  public void processReport (final ReportJob job)
          throws ReportDataFactoryException, DataSourceException,
          ReportProcessingException
  {
    if (job == null)
    {
      throw new NullPointerException();
    }

    synchronized (job)
    {
      // first, compute the globals
      processReportRun(job, createTarget(job));
      // second, paginate (this splits the stream into flows)
      processReportRun(job, createTarget(job));
      // third, generate the content.
      processReportRun(job, createTarget(job));
    }
  }

}
