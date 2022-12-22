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
 * $Id: JFreeReportXmlResourceFactory.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.factories.report.base;

import org.jfree.report.JFreeReport;
import org.jfree.report.JFreeReportBoot;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;

/**
 * Creation-Date: 08.04.2006, 14:27:36
 *
 * @author Thomas Morgner
 */
public class  JFreeReportXmlResourceFactory extends AbstractXmlResourceFactory
{
  public JFreeReportXmlResourceFactory()
  {
  }

  public Class getFactoryType()
  {
    return JFreeReport.class;
  }

  protected Configuration getConfiguration ()
  {
    return JFreeReportBoot.getInstance().getGlobalConfig();
  }

  protected Object finishResult(final Object res,
                                final ResourceManager manager,
                                final ResourceData data,
                                final ResourceKey context)
          throws ResourceCreationException, ResourceLoadingException
  {
    final JFreeReport report = (JFreeReport) res;
    if (report == null)
    {
      throw new ResourceCreationException("Report has not been parsed.");
    }
    if (context != null)
    {
      report.setBaseResource(context);
    }
    else
    {
      report.setBaseResource(data.getKey());
    }
    report.setResourceManager(manager);

    return report;
  }
}
