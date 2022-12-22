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
 * $Id: BufferedReportTarget.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow.layoutprocessor;

import java.util.ArrayList;

import org.jfree.layouting.namespace.NamespaceDefinition;
import org.jfree.layouting.util.AttributeMap;
import org.jfree.report.DataFlags;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.flow.ReportStructureRoot;
import org.jfree.report.flow.ReportTarget;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 * @since 05.03.2007
 */
public class BufferedReportTarget implements ReportTarget, Cloneable
{
  public static class RecordedCall
  {
    private int methodId;
    private Object parameters;

    public RecordedCall(final int method, final Object parameters)
    {
      this.methodId = method;
      this.parameters = parameters;
    }

    public int getMethod()
    {
      return methodId;
    }

    public Object getParameters()
    {
      return parameters;
    }
  }

  private static final int MTH_START_REPORT = 1;
  private static final int MTH_START_ELEMENT = 2;
  private static final int MTH_PROCESS_TEXT = 3;
  private static final int MTH_PROCESS_CONTENT = 4;
  private static final int MTH_END_ELEMENT = 5;
  private static final int MTH_END_REPORT = 6;

  private ReportTarget target;
  private ArrayList calls;

  public BufferedReportTarget()
  {
    this.calls = new ArrayList();
  }

  public ReportTarget getTarget()
  {
    return target;
  }

  public void setTarget(final ReportTarget target)
  {
    this.target = target;
  }

  public void startReport(final ReportStructureRoot report)
      throws DataSourceException, ReportProcessingException
  {
    calls.add(new RecordedCall
        (BufferedReportTarget.MTH_START_REPORT, report));
  }

  public void startElement(final AttributeMap attrs)
      throws DataSourceException, ReportProcessingException
  {
    try
    {
      calls.add(new RecordedCall
          (BufferedReportTarget.MTH_START_ELEMENT, attrs.clone()));
    }
    catch (CloneNotSupportedException e)
    {
      throw new ReportProcessingException("Failed to clone attributes", e);
    }
  }

  public void processText(final String text)
      throws DataSourceException, ReportProcessingException
  {
    calls.add(new RecordedCall
        (BufferedReportTarget.MTH_PROCESS_TEXT, text));
  }

  public void processContent(final DataFlags value)
      throws DataSourceException, ReportProcessingException
  {
    calls.add(new RecordedCall
        (BufferedReportTarget.MTH_PROCESS_CONTENT, value));
  }

  public void endElement(final AttributeMap attrs)
      throws DataSourceException, ReportProcessingException
  {
    try
    {
      calls.add(new RecordedCall
          (BufferedReportTarget.MTH_END_ELEMENT, attrs.clone()));
    }
    catch (CloneNotSupportedException e)
    {
      throw new ReportProcessingException("Failed to clone attributes", e);
    }
  }

  public void endReport(final ReportStructureRoot report)
      throws DataSourceException, ReportProcessingException
  {
    calls.add(new RecordedCall
        (BufferedReportTarget.MTH_END_REPORT, report));
  }

  public String getExportDescriptor()
  {
    return target.getExportDescriptor();
  }

  public NamespaceDefinition getNamespaceByUri(final String uri)
  {
    return target.getNamespaceByUri(uri);
  }

  public void commit()
      throws ReportProcessingException
  {
    // ignored ..
  }

  public void close(final ReportTarget target)
      throws ReportProcessingException, DataSourceException
  {
    final RecordedCall[] objects = (RecordedCall[])
        calls.toArray(new RecordedCall[calls.size()]);

    for (int i = 0; i < objects.length; i++)
    {
      final RecordedCall call = objects[i];
      switch(call.getMethod())
      {
        case BufferedReportTarget.MTH_START_REPORT:
        {
          target.startReport((ReportStructureRoot) call.getParameters());
          break;
        }
        case BufferedReportTarget.MTH_START_ELEMENT:
        {
          target.startElement((AttributeMap) call.getParameters());
          break;
        }
        case BufferedReportTarget.MTH_PROCESS_TEXT:
        {
          target.processText((String) call.getParameters());
          break;
        }
        case BufferedReportTarget.MTH_PROCESS_CONTENT:
        {
          target.processContent((DataFlags) call.getParameters());
          break;
        }
        case BufferedReportTarget.MTH_END_ELEMENT:
        {
          target.endElement((AttributeMap) call.getParameters());
          break;
        }
        case BufferedReportTarget.MTH_END_REPORT:
        {
          target.endReport((ReportStructureRoot) call.getParameters());
          break;
        }
        default:
          throw new IllegalStateException("Invalid call recorded.");
      }
    }
  }

  public Object clone ()
  {
    try
    {
      final BufferedReportTarget o = (BufferedReportTarget) super.clone();
      o.calls = (ArrayList) calls.clone();
      return o;
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException("Clone failed");
    }
  }
}
