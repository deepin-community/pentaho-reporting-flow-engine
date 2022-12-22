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
 * $Id: PaginatingReportProcessor.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.flow.paginating;

import org.jfree.layouting.ChainingLayoutProcess;
import org.jfree.layouting.DefaultLayoutProcess;
import org.jfree.layouting.LayoutProcess;
import org.jfree.layouting.StateException;
import org.jfree.layouting.output.pageable.PageableOutputProcessor;
import org.jfree.layouting.util.IntList;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.flow.AbstractReportProcessor;
import org.jfree.report.flow.FlowController;
import org.jfree.report.flow.LibLayoutReportTarget;
import org.jfree.report.flow.ReportContext;
import org.jfree.report.flow.ReportJob;
import org.jfree.report.flow.ReportTarget;
import org.jfree.report.flow.ReportTargetState;
import org.jfree.report.flow.layoutprocessor.LayoutController;
import org.jfree.report.flow.layoutprocessor.LayoutControllerFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * Paginating report processors are multi-pass processors.
 * <p/>
 * This is written to use LibLayout. It will never work with other report
 * targets.
 *
 * Be aware that this class is not synchronized.
 *
 * @author Thomas Morgner
 */
public abstract class PaginatingReportProcessor extends AbstractReportProcessor
{
  private PageableOutputProcessor outputProcessor;
  private PageStateList stateList;
  private IntList physicalMapping;
  private IntList logicalMapping;

  protected PaginatingReportProcessor(final PageableOutputProcessor outputProcessor)
  {
    this.outputProcessor = outputProcessor;
  }

  public PageableOutputProcessor getOutputProcessor()
  {
    return outputProcessor;
  }

  protected LibLayoutReportTarget createTarget(final ReportJob job)
  {
    if (outputProcessor == null)
    {
      throw new IllegalStateException("OutputProcessor is invalid.");
    }
    final LayoutProcess layoutProcess =
        new ChainingLayoutProcess(new DefaultLayoutProcess(outputProcessor));
    final ResourceManager resourceManager = job.getReportStructureRoot().getResourceManager();
    final ResourceKey resourceKey = job.getReportStructureRoot().getBaseResource();

    return new LibLayoutReportTarget
        (job, resourceKey, resourceManager, layoutProcess);
  }

//  public void processReport(ReportJob job)
//      throws ReportDataFactoryException,
//      DataSourceException, ReportProcessingException
//  {
//    prepareReportProcessing(job);
//
//  }

  protected void prepareReportProcessing(final ReportJob job)
      throws ReportDataFactoryException, DataSourceException, ReportProcessingException
  {
    if (job == null)
    {
      throw new NullPointerException();
    }

    final long start = System.currentTimeMillis();
    // first, compute the globals
    processReportRun(job, createTarget(job));
    if (outputProcessor.isGlobalStateComputed() == false)
    {
      throw new ReportProcessingException
          ("Pagination has not yet been finished.");
    }

    // second, paginate
    processPaginationRun(job, createTarget(job));
    if (outputProcessor.isPaginationFinished() == false)
    {
      throw new ReportProcessingException
          ("Pagination has not yet been finished.");
    }

    if (outputProcessor.isContentGeneratable() == false)
    {
      throw new ReportProcessingException
          ("Illegal State.");
    }
    final long end = System.currentTimeMillis();
    System.out.println("Pagination-Time: " + (end - start));
  }

  protected PageStateList processPaginationRun(final ReportJob job,
                                               final LibLayoutReportTarget target)
      throws ReportDataFactoryException,
      DataSourceException, ReportProcessingException
  {
    if (job == null)
    {
      throw new NullPointerException();
    }
    stateList = new PageStateList(this);
    physicalMapping = new IntList(40);
    logicalMapping = new IntList(20);

    final ReportContext context = createReportContext(job, target);
    final LayoutControllerFactory layoutFactory =
        context.getLayoutControllerFactory();

    // we have the data and we have our position inside the report.
    // lets generate something ...
    final FlowController flowController = createFlowControler(context, job);

    LayoutController layoutController =
        layoutFactory.create(flowController, job.getReportStructureRoot(), null);

    try
    {
      stateList.add(new PageState(target.saveState(), layoutController,
          outputProcessor.getPageCursor()));
      int logPageCount = outputProcessor.getLogicalPageCount();
      int physPageCount = outputProcessor.getPhysicalPageCount();

      while (layoutController.isAdvanceable())
      {
        layoutController = layoutController.advance(target);
        target.commit();

        while (layoutController.isAdvanceable() == false &&
               layoutController.getParent() != null)
        {
          final LayoutController parent = layoutController.getParent();
          layoutController = parent.join(layoutController.getFlowController());
        }

        // check whether a pagebreak has been encountered.
        if (target.isPagebreakEncountered())
        {
          // So we hit a pagebreak. Store the state for later reuse.
          // A single state can refer to more than one physical page.

          final int newLogPageCount = outputProcessor.getLogicalPageCount();
          final int newPhysPageCount = outputProcessor.getPhysicalPageCount();

          final int result = stateList.size() - 1;
          for (; physPageCount < newPhysPageCount; physPageCount++)
          {
            physicalMapping.add(result);
          }

          for (; logPageCount < newLogPageCount; logPageCount++)
          {
            logicalMapping.add(result);
          }

          logPageCount = newLogPageCount;
          physPageCount = newPhysPageCount;

          final ReportTargetState targetState = target.saveState();
          final PageState state =
              new PageState (targetState, layoutController,
              outputProcessor.getPageCursor());
          stateList.add(state);

          // This is an assertation that we do not run into invalid states
          // later.
          if (PaginatingReportProcessor.ASSERTATION)
          {
            final ReportTarget reportTarget =
              targetState.restore(outputProcessor);
          }

          target.resetPagebreakFlag();
        }
      }

      // And when we reached the end, add the remaining pages ..
      final int newLogPageCount = outputProcessor.getLogicalPageCount();
      final int newPhysPageCount = outputProcessor.getPhysicalPageCount();

      final int result = stateList.size() - 1;
      for (; physPageCount < newPhysPageCount; physPageCount++)
      {
        physicalMapping.add(result);
      }

      for (; logPageCount < newLogPageCount; logPageCount++)
      {
        logicalMapping.add(result);
      }
    }
    catch (final StateException e)
    {
      throw new ReportProcessingException("Argh, Unable to save the state!");
    }

    DebugLog.log("After pagination we have " + stateList.size() + " states");
    return stateList;
  }

  public boolean isPaginated()
  {
    return outputProcessor.isPaginationFinished();
  }

  protected PageState getLogicalPageState (final int page)
  {
    return stateList.get(logicalMapping.get(page));
  }

  protected PageState getPhysicalPageState (final int page)
  {
    return stateList.get(physicalMapping.get(page));
  }

  public PageState processPage(final PageState previousState)
      throws StateException, ReportProcessingException,
      ReportDataFactoryException, DataSourceException
  {
    final ReportTargetState targetState = previousState.getTargetState();
    final LibLayoutReportTarget target =
        (LibLayoutReportTarget) targetState.restore(outputProcessor);
    outputProcessor.setPageCursor(previousState.getPageCursor());

    LayoutController position = previousState.getLayoutController();

    // we have the data and we have our position inside the report.
    // lets generate something ...

    while (position.isAdvanceable())
    {
      position = position.advance(target);
      target.commit();

      // else check whether this state is finished, and try to join the subflow
      // with the parent.
      while (position.isAdvanceable() == false &&
             position.getParent() != null)
      {
        final LayoutController parent = position.getParent();
        position = parent.join(position.getFlowController());
      }

      // check whether a pagebreak has been encountered.
      if (target.isPagebreakEncountered())
      {
        // So we hit a pagebreak. Store the state for later reuse.
        // A single state can refer to more than one physical page.
        final PageState state = new PageState
            (target.saveState(), position, outputProcessor.getPageCursor());
        target.resetPagebreakFlag();
        return state;
      }

    }

    // reached the finish state .. this is bad!
    return null;
  }

  private static final boolean ASSERTATION = true;
}
