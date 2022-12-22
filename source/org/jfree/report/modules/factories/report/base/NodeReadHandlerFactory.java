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
 * $Id: NodeReadHandlerFactory.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.factories.report.base;

import java.util.Iterator;

import org.jfree.report.JFreeReportBoot;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractReadHandlerFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * Creation-Date: 27.11.2006, 11:48:02
 *
 * @author Thomas Morgner
 */
public class NodeReadHandlerFactory extends AbstractReadHandlerFactory
{

  private static final String PREFIX_SELECTOR =
      "org.jfree.report.modules.factories.report.base.node-factory-prefix.";

  private static NodeReadHandlerFactory readHandlerFactory;

  public NodeReadHandlerFactory()
  {
  }

  protected Class getTargetClass()
  {
    return NodeReadHandler.class;
  }

  public static synchronized NodeReadHandlerFactory getInstance()
  {
    if (readHandlerFactory == null)
    {
      readHandlerFactory = new NodeReadHandlerFactory();
      final Configuration config = JFreeReportBoot.getInstance().getGlobalConfig();
      final Iterator propertyKeys = config.findPropertyKeys(PREFIX_SELECTOR);
      while (propertyKeys.hasNext())
      {
        final String key = (String) propertyKeys.next();
        final String value = config.getConfigProperty(key);
        if (value != null)
        {
          readHandlerFactory.configure(config, value);
        }
      }
    }
    return readHandlerFactory;
  }
}
