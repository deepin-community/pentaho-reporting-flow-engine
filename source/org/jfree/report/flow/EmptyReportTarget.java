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
 * $Id: EmptyReportTarget.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow;

import org.jfree.layouting.namespace.NamespaceDefinition;
import org.jfree.layouting.util.AttributeMap;
import org.jfree.report.DataFlags;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportProcessingException;

/**
 * This target does nothing.
 *
 * @author Thomas Morgner
 */
public class EmptyReportTarget implements ReportTarget
{
  private ReportJob job;
  private String reportDescriptor;

  public EmptyReportTarget(final ReportJob job,
                           final String reportDescriptor)
  {
    this.job = job;
    this.reportDescriptor = reportDescriptor;
  }

  public void startReport(ReportStructureRoot report)
      throws DataSourceException, ReportProcessingException
  {

  }

  public void startElement(final AttributeMap attrs)
      throws DataSourceException, ReportProcessingException
  {

  }

  public void processText(String text)
      throws DataSourceException, ReportProcessingException
  {

  }

  public void processContent(final DataFlags value)
      throws DataSourceException, ReportProcessingException
  {

  }

  public void endElement(final AttributeMap attrs)
      throws DataSourceException, ReportProcessingException
  {

  }

  public void endReport(ReportStructureRoot report)
      throws DataSourceException, ReportProcessingException
  {

  }

  public NamespaceDefinition getNamespaceByUri(String uri)
  {
    return null;
  }

  public ReportJob getReportJob()
  {
    return job;
  }

  public String getExportDescriptor()
  {
    return reportDescriptor;
  }

  public void commit() throws ReportProcessingException
  {

  }
}
