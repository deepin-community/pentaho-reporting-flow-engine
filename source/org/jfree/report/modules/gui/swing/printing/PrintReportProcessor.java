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
 * $Id: PrintReportProcessor.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.printing;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;

import org.jfree.layouting.StateException;
import org.jfree.layouting.modules.output.graphics.GraphicsOutputProcessor;
import org.jfree.layouting.modules.output.graphics.PageDrawable;
import org.jfree.layouting.modules.output.graphics.QueryPhysicalPageInterceptor;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportDataFactory;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.flow.LibLayoutReportTarget;
import org.jfree.report.flow.ReportJob;
import org.jfree.report.flow.ReportTargetState;
import org.jfree.report.flow.layoutprocessor.LayoutController;
import org.jfree.report.flow.paginating.PageState;
import org.jfree.report.flow.paginating.PaginatingReportProcessor;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * A paginating report processor that outputs to Pageables.
 *
 * @author Thomas Morgner
 */
public class PrintReportProcessor extends PaginatingReportProcessor
    implements Pageable
{
  private ReportJob job;
  private Throwable error;

  public PrintReportProcessor(final ReportJob job)
  {
    super(new GraphicsOutputProcessor(job.getConfiguration()));
    this.job = job;

    synchronized(job)
    {
      final ReportDataFactory dataFactory = job.getDataFactory();
      if (dataFactory != null)
      {
        dataFactory.open();
      }
    }
  }

  protected GraphicsOutputProcessor getGraphicsProcessor()
  {
    return (GraphicsOutputProcessor) getOutputProcessor();
  }

  public boolean isError()
  {
    return error != null;
  }

  protected ReportJob getJob()
  {
    return job;
  }

  public void close()
  {
    getJob().close();
  }

  protected PageDrawable processPage(int page)
      throws ReportDataFactoryException,
      DataSourceException, ReportProcessingException, StateException
  {
    final ReportJob job = getJob();
    synchronized (job)
    {
      // set up the scene
      final PageState state = getPhysicalPageState(page);

      final ReportTargetState targetState = state.getTargetState();
      final GraphicsOutputProcessor outputProcessor = getGraphicsProcessor();
      outputProcessor.setPageCursor(state.getPageCursor());
      final QueryPhysicalPageInterceptor interceptor =
          new QueryPhysicalPageInterceptor(outputProcessor.getPhysicalPage(page));
      outputProcessor.setInterceptor(interceptor);

      final LibLayoutReportTarget target =
          (LibLayoutReportTarget) targetState.restore(outputProcessor);

      LayoutController position = state.getLayoutController();

      // we have the data and we have our position inside the report.
      // lets generate something ...
      while (position.isAdvanceable())
      {
        position = position.advance(target);
        target.commit();

        while (position.isAdvanceable() == false &&
               position.getParent() != null)
        {
          final LayoutController parent = position.getParent();
          position = parent.join(position.getFlowController());
        }

        if (interceptor.isMoreContentNeeded() == false)
        {
          outputProcessor.setInterceptor(null);
          return interceptor.getDrawable();
        }

      }

      outputProcessor.setInterceptor(null);
      return interceptor.getDrawable();
    }
  }

  /**
   * Returns the number of pages in the set. To enable advanced printing
   * features, it is recommended that <code>Pageable</code> implementations
   * return the true number of pages rather than the UNKNOWN_NUMBER_OF_PAGES
   * constant.
   *
   * @return the number of pages in this <code>Pageable</code>.
   */
  public synchronized int getNumberOfPages()
  {
    if (isError())
    {
      return 0;
    }

    if (isPaginated() == false)
    {
      try
      {
        prepareReportProcessing(getJob());
      }
      catch (Exception e)
      {
        DebugLog.log("PrintReportProcessor: ", e);
        error = e;
        return 0;
      }
    }
    DebugLog.log("After pagination, we have " +
        getGraphicsProcessor().getPhysicalPageCount() + " physical pages.");
    return getGraphicsProcessor().getPhysicalPageCount();
  }

  public boolean paginate()
  {
    if (isError())
    {
      return false;
    }

    if (isPaginated() == false)
    {
      try
      {
        prepareReportProcessing(getJob());
        return true;
      }
      catch (Exception e)
      {
        error = e;
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the <code>PageFormat</code> of the page specified by
   * <code>pageIndex</code>.
   *
   * @param pageIndex the zero based index of the page whose <code>PageFormat</code>
   *                  is being requested
   * @return the <code>PageFormat</code> describing the size and orientation.
   * @throws IndexOutOfBoundsException if the <code>Pageable</code> does not
   *                                   contain the requested page.
   */
  public synchronized PageFormat getPageFormat(final int pageIndex)
      throws IndexOutOfBoundsException
  {
    if (isError())
    {
      return null;
    }

    if (isPaginated() == false)
    {
      try
      {
        prepareReportProcessing(getJob());
      }
      catch (Exception e)
      {
        error = e;
        return null;
      }
    }

    try
    {
      final PageDrawable pageDrawable = processPage(pageIndex);
      return pageDrawable.getPageFormat();
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Unable to return a valid pageformat.");
    }
  }

  /**
   * Returns the <code>Printable</code> instance responsible for rendering the
   * page specified by <code>pageIndex</code>.
   *
   * @param pageIndex the zero based index of the page whose <code>Printable</code>
   *                  is being requested
   * @return the <code>Printable</code> that renders the page.
   * @throws IndexOutOfBoundsException if the <code>Pageable</code> does not
   *                                   contain the requested page.
   */
  public synchronized Printable getPrintable(final int pageIndex)
      throws IndexOutOfBoundsException
  {
    if (isError())
    {
      return null;
    }

    if (isPaginated() == false)
    {
      try
      {
        prepareReportProcessing(getJob());
      }
      catch (Exception e)
      {
        error = e;
        return null;
      }
    }

    try
    {
      final PageDrawable pageDrawable = processPage(pageIndex);
      return new DrawablePrintable(pageDrawable);
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Unable to return a valid pageformat.");
    }
  }

  public PageDrawable getPageDrawable(final int pageIndex)
  {
    if (isError())
    {
      return null;
    }

    if (isPaginated() == false)
    {
      try
      {
        prepareReportProcessing(getJob());
      }
      catch (Exception e)
      {
        error = e;
        return null;
      }
    }

    try
    {
      return processPage(pageIndex);
    }
    catch (Exception e)
    {
      error = e;
      DebugLog.log("Failed to process the page", e);
      throw new IllegalStateException("Unable to return a valid pageformat.");
    }
  }

  /**
   * Throws an unsupported operation exception. Printing is controlled by a
   * framework which calls this pageable class for each page. Therefore,
   * printing has to be invoked from outside.
   *
   * @param job
   * @throws UnsupportedOperationException
   */
  public final void processReport(final ReportJob job)
  {
    throw new UnsupportedOperationException("Printing is a passive process.");
  }
}
