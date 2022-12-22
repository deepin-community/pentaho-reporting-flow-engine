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
 * $Id: XmlPrintReportTarget.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow.raw;

import java.awt.Image;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.jfree.layouting.namespace.NamespaceDefinition;
import org.jfree.layouting.namespace.Namespaces;
import org.jfree.layouting.namespace.NamespaceCollection;
import org.jfree.layouting.namespace.DefaultNamespaceCollection;
import org.jfree.layouting.util.AttributeMap;
import org.jfree.layouting.LibLayoutBoot;
import org.jfree.report.DataFlags;
import org.jfree.report.DataSourceException;
import org.jfree.report.JFreeReportInfo;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.util.AttributeNameGenerator;
import org.jfree.report.flow.ReportJob;
import org.jfree.report.flow.ReportStructureRoot;
import org.jfree.report.flow.ReportTarget;
import org.jfree.report.flow.ReportTargetUtil;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 * @since 20.03.2007
 */
public class XmlPrintReportTarget implements ReportTarget
{
  private ReportJob reportJob;
  private XmlWriter writer;
  private NamespaceCollection namespaces;
  private AttributeNameGenerator namespacePrefixGenerator;

  public XmlPrintReportTarget(final ReportJob job,
                              final XmlWriter writer)
  {
    this.reportJob = job;
    this.writer = writer;

    final NamespaceDefinition[] reportNamespaces = Namespaces.createFromConfig
        (reportJob.getConfiguration(), "org.jfree.report.namespaces.", null);
    final NamespaceDefinition[] layoutNamespaces = Namespaces.createFromConfig
        (LibLayoutBoot.getInstance().getGlobalConfig(),
            "org.jfree.layouting.namespaces.", null);
    DefaultNamespaceCollection namespaces = new DefaultNamespaceCollection();
    namespaces.addDefinitions(reportNamespaces);
    namespaces.addDefinitions(layoutNamespaces);
    this.namespaces = namespaces;
    this.namespacePrefixGenerator = new AttributeNameGenerator();
  }

  public ReportJob getReportJob()
  {
    return reportJob;
  }

  public void startReport(final ReportStructureRoot report)
      throws DataSourceException, ReportProcessingException
  {
    try
    {
      writer.writeComment("starting report");
    }
    catch (IOException e)
    {
      throw new ReportProcessingException("IOError", e);
    }
  }

  public void startElement(final AttributeMap attrs)
      throws DataSourceException, ReportProcessingException
  {
    final String namespace = ReportTargetUtil.getNamespaceFromAttribute(attrs);
    final String elementType =
        ReportTargetUtil.getElemenTypeFromAttribute(attrs);

    try
    {
      final AttributeList attrList = buildAttributeList(attrs);
      validateNamespace(namespace, attrList);
      writer.writeTag(namespace, elementType, attrList,
          XmlWriterSupport.OPEN);
    }
    catch (IOException e)
    {
      throw new ReportProcessingException("IOError", e);
    }
  }

  public void processContent(final DataFlags value)
      throws DataSourceException, ReportProcessingException
  {
    final Object rawvalue = value.getValue();
    if (rawvalue == null)
    {
      return;
    }

    // special handler for image (possibly also for URL ..)
    if (rawvalue instanceof Image)
    {
      // do nothing yet. We should define something for that later ..
      return;
    }

    try
    {
      final String text = String.valueOf(rawvalue);
      writer.writeTextNormalized(text, false);
    }
    catch (IOException e)
    {
      throw new ReportProcessingException("Failed", e);
    }
  }

  public void endElement(final AttributeMap attrs)
      throws DataSourceException, ReportProcessingException
  {
    try
    {
      writer.writeCloseTag();
    }
    catch (IOException e)
    {
      throw new ReportProcessingException("IOError", e);
    }
  }

  public void endReport(final ReportStructureRoot report)
      throws DataSourceException, ReportProcessingException
  {
    try
    {
      writer.writeComment("starting report");
    }
    catch (IOException e)
    {
      throw new ReportProcessingException("IOError", e);
    }
  }

  public NamespaceDefinition getNamespaceByUri(final String uri)
  {
    return namespaces.getDefinition(uri);
  }

  public void processText(final String text)
      throws DataSourceException, ReportProcessingException
  {
    try
    {
      writer.writeTextNormalized(text, false);
    }
    catch (IOException e)
    {
      throw new ReportProcessingException("IOError", e);
    }
  }


  public void commit()
      throws ReportProcessingException
  {
    try
    {
      writer.flush();
    }
    catch (IOException e)
    {
      throw new ReportProcessingException("Failed to flush", e);
    }
  }

  public String getExportDescriptor()
  {
    return "raw/text+xml";
  }

  protected AttributeList buildAttributeList(final AttributeMap attrs)
  {
    final AttributeList attrList = new AttributeList();
    final String[] namespaces = attrs.getNameSpaces();
    for (int i = 0; i < namespaces.length; i++)
    {
      final String attrNamespace = namespaces[i];
      if (isInternalNamespace(attrNamespace))
      {
        continue;
      }

      final Map localAttributes = attrs.getAttributes(attrNamespace);
      final Iterator entries = localAttributes.entrySet().iterator();
      while (entries.hasNext())
      {
        final Map.Entry entry = (Map.Entry) entries.next();
        final String key = String.valueOf(entry.getKey());
        validateNamespace(attrNamespace, attrList);
        attrList.setAttribute(attrNamespace, key,
            String.valueOf(entry.getValue()));
      }
    }
    return attrList;
  }

  private void validateNamespace(final String uri, final AttributeList list)
  {
    if (writer.isNamespaceDefined(uri))
    {
      return;
    }

    if (list.isNamespaceUriDefined(uri))
    {
      return;
    }

    final NamespaceDefinition def = getNamespaceByUri(uri);
    if (def != null)
    {
      final String prefix = def.getPreferredPrefix();
      if (writer.isNamespacePrefixDefined(prefix) == false &&
          list.isNamespacePrefixDefined(prefix) == false)
      {
        list.addNamespaceDeclaration (prefix, uri);
      }
      else
      {
        list.addNamespaceDeclaration
            (namespacePrefixGenerator.generateName(prefix), uri);
      }
    }
    else
    {
      list.addNamespaceDeclaration
          (namespacePrefixGenerator.generateName("auto"), uri);
    }
  }

  private boolean isInternalNamespace(final String namespace)
  {
    return JFreeReportInfo.REPORT_NAMESPACE.equals(namespace);
  }

}
