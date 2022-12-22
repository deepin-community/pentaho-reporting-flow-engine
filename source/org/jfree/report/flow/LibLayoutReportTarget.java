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
 * $Id: LibLayoutReportTarget.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.flow;

import java.util.Iterator;
import java.util.Map;

import org.jfree.layouting.LayoutProcess;
import org.jfree.layouting.LayoutProcessState;
import org.jfree.layouting.StateException;
import org.jfree.layouting.input.style.StyleSheet;
import org.jfree.layouting.layouter.context.DocumentContext;
import org.jfree.layouting.layouter.feed.InputFeed;
import org.jfree.layouting.layouter.feed.InputFeedException;
import org.jfree.layouting.namespace.NamespaceCollection;
import org.jfree.layouting.namespace.NamespaceDefinition;
import org.jfree.layouting.output.OutputProcessor;
import org.jfree.layouting.util.AttributeMap;
import org.jfree.report.DataFlags;
import org.jfree.report.DataSourceException;
import org.jfree.report.JFreeReport;
import org.jfree.report.JFreeReportInfo;
import org.jfree.report.ReportProcessingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Creation-Date: 07.03.2006, 18:56:37
 *
 * @author Thomas Morgner
 */
public class LibLayoutReportTarget extends AbstractReportTarget
  implements StatefullReportTarget
{
  protected static class LibLayoutReportTargetState
      implements ReportTargetState
  {
    private LayoutProcessState layoutProcess;
    private ReportJob reportJob;
    private ResourceKey baseResourceKey;
    private ResourceManager resourceManager;
    private NamespaceCollection namespaceCollection;

    public LibLayoutReportTargetState()
    {
    }

    public void fill (final LibLayoutReportTarget target) throws StateException
    {
      this.layoutProcess = target.getLayoutProcess().saveState();
      this.reportJob = target.getReportJob();
      this.baseResourceKey = target.getBaseResource();
      this.resourceManager = target.getResourceManager();
      this.namespaceCollection = target.getNamespaces();
    }

    public ReportTarget restore(final OutputProcessor out)
        throws StateException
    {
      final LayoutProcess layoutProcess = this.layoutProcess.restore(out);
      return new LibLayoutReportTarget(reportJob,
          baseResourceKey, resourceManager, layoutProcess, namespaceCollection);
    }
  }


  private InputFeed feed;
  private NamespaceCollection namespaces;
  private LayoutProcess layoutProcess;

  /**
   *
   * @param reportJob
   * @param baseResourceKey  may be null, if the report has not gone through the parser
   * @param resourceManager may be null, a generic resource manager will be built
   * @param layoutProcess
   */
  public LibLayoutReportTarget (final ReportJob reportJob,
                                final ResourceKey baseResourceKey,
                                final ResourceManager resourceManager,
                                final LayoutProcess layoutProcess)
  {
    super(reportJob, resourceManager, baseResourceKey);

    if (layoutProcess == null)
    {
      throw new NullPointerException();
    }
    this.layoutProcess = layoutProcess;
    this.feed = layoutProcess.getInputFeed();
  }

  protected LibLayoutReportTarget(final ReportJob reportJob,
                               final ResourceKey baseResource,
                               final ResourceManager resourceManager,
                               final LayoutProcess layoutProcess,
                               final NamespaceCollection namespaces)
  {
    this(reportJob, baseResource, resourceManager, layoutProcess);
    this.namespaces = namespaces;
  }

  public ReportTargetState saveState() throws StateException
  {
    final LibLayoutReportTargetState state = new LibLayoutReportTargetState();
    state.fill(this);
    return state;
  }

  public void commit()
  {

  }

  public NamespaceCollection getNamespaces()
  {
    return namespaces;
  }

  public boolean isPagebreakEncountered()
  {
    return layoutProcess.isPagebreakEncountered();
  }

  protected LayoutProcess getLayoutProcess()
  {
    return layoutProcess;
  }

  protected InputFeed getInputFeed ()
  {
    return feed;
  }

  public void startReport (ReportStructureRoot report)
          throws DataSourceException, ReportProcessingException
  {
    try
    {
      final InputFeed feed = getInputFeed();
      feed.startDocument();
      feed.startMetaInfo();

      feed.addDocumentAttribute(DocumentContext.BASE_RESOURCE_ATTR, report.getBaseResource());
      feed.addDocumentAttribute(DocumentContext.RESOURCE_MANAGER_ATTR, report.getResourceManager());

      String strictStyleMode = "false";
      if ("true".equals(strictStyleMode))
      {
        feed.addDocumentAttribute(DocumentContext.STRICT_STYLE_MODE, Boolean.TRUE);
      }

      final NamespaceDefinition[] namespaces = createDefaultNameSpaces();
      for (int i = 0; i < namespaces.length; i++)
      {
        final NamespaceDefinition definition = namespaces[i];
        feed.startMetaNode();
        feed.setMetaNodeAttribute("type", "namespace");
        feed.setMetaNodeAttribute("definition", definition);
        feed.endMetaNode();
      }

      if (report instanceof JFreeReport)
      {
        final JFreeReport realReport = (JFreeReport) report;
        final int size = realReport.getStyleSheetCount();
        for (int i = 0; i < size; i++)
        {
          final StyleSheet styleSheet = realReport.getStyleSheet(i);
          feed.startMetaNode();
          feed.setMetaNodeAttribute("type", "style");
          feed.setMetaNodeAttribute("#content", styleSheet);
          feed.endMetaNode();
        }
      }

      feed.endMetaInfo();
      this.namespaces = feed.getNamespaceCollection();
    }
    catch (InputFeedException dse)
    {
      dse.printStackTrace();
      throw new ReportProcessingException("Failed to process inputfeed", dse);
    }

  }

  public void startElement (final AttributeMap attrs)
          throws DataSourceException, ReportProcessingException
  {
    try
    {
      final String namespace = ReportTargetUtil.getNamespaceFromAttribute(attrs);
      final String type = ReportTargetUtil.getElemenTypeFromAttribute(attrs);
      final InputFeed feed = getInputFeed();
      feed.startElement(namespace, type);
      handleAttributes(attrs);
    }
    catch (InputFeedException e)
    {
      throw new ReportProcessingException("Failed to process inputfeed", e);
    }
  }

  public void processText(String text)
      throws DataSourceException, ReportProcessingException
  {
    try
    {
      final InputFeed feed = getInputFeed();
      feed.addContent(text);
    }
    catch (InputFeedException e)
    {
      throw new ReportProcessingException("Failed to process inputfeed", e);
    }
  }

  public void processContent (final DataFlags value)
          throws DataSourceException, ReportProcessingException
  {
    final InputFeed feed = getInputFeed();
    try
    {
      feed.setAttribute(JFreeReportInfo.REPORT_NAMESPACE, "content", value.getValue());
      feed.setAttribute(JFreeReportInfo.REPORT_NAMESPACE, "isChanged", String.valueOf(value.isChanged()));
      feed.setAttribute(JFreeReportInfo.REPORT_NAMESPACE, "isDate", String.valueOf(value.isDate()));
      feed.setAttribute(JFreeReportInfo.REPORT_NAMESPACE, "isNegative", String.valueOf(value.isNegative()));
      feed.setAttribute(JFreeReportInfo.REPORT_NAMESPACE, "isNull", String.valueOf(value.isNull()));
      feed.setAttribute(JFreeReportInfo.REPORT_NAMESPACE, "isNumber", String.valueOf(value.isNumeric()));
      feed.setAttribute(JFreeReportInfo.REPORT_NAMESPACE, "isPositive", String.valueOf(value.isPositive()));
      feed.setAttribute(JFreeReportInfo.REPORT_NAMESPACE, "isZero", String.valueOf(value.isZero()));
    }
    catch (InputFeedException e)
    {
      throw new ReportProcessingException("Failed to process inputfeed", e);
    }
  }

  public NamespaceDefinition getNamespaceByUri(String uri)
  {
    if (uri == null)
    {
      return null;
    }
    return namespaces.getDefinition(uri);
  }


  protected void handleAttributes(AttributeMap map)
          throws ReportProcessingException
  {
    try
    {
      final InputFeed feed = getInputFeed();
      final String[] namespaces = map.getNameSpaces();
      for (int i = 0; i < namespaces.length; i++)
      {
        final String namespace = namespaces[i];
        final Map localAttrs = map.getAttributes(namespace);
        final Iterator it = localAttrs.entrySet().iterator();
        while (it.hasNext())
        {
          Map.Entry entry = (Map.Entry) it.next();
          feed.setAttribute(namespace, (String) entry.getKey(), entry.getValue());
        }
      }
    }
    catch (InputFeedException e)
    {
      throw new ReportProcessingException("Failed to set attribute", e);
    }
  }

  public void endElement (final AttributeMap attrs)
          throws DataSourceException, ReportProcessingException
  {
    final InputFeed feed = getInputFeed();
    try
    {
      feed.endElement();
    }
    catch (InputFeedException e)
    {
      throw new ReportProcessingException("Failed to process inputfeed", e);
    }
  }

  public void endReport (ReportStructureRoot report)
          throws DataSourceException,
          ReportProcessingException
  {
    try
    {
      getInputFeed().endDocument();
    }
    catch (InputFeedException e)
    {
      throw new ReportProcessingException("Failed to process inputfeed", e);
    }
  }


  public void resetPagebreakFlag()
  {
    getInputFeed().resetPageBreakFlag();
  }

  public String getExportDescriptor()
  {
    return getLayoutProcess().getOutputMetaData().getExportDescriptor();
  }
}
