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
 * $Id: DefaultLayoutControllerFactory.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow.layoutprocessor;

import java.util.HashMap;
import java.util.Iterator;

import org.jfree.report.DataSourceException;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.flow.FlowController;
import org.jfree.report.flow.ReportJob;
import org.jfree.report.structure.Node;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A layout controller factory that selects layout controllers by their node
 * implementation type.
 *
 * @author Thomas Morgner
 */
public class DefaultLayoutControllerFactory implements LayoutControllerFactory
{
  private HashMap registry;
  private static final String PREFIX = "org.jfree.report.flow.structure.";

  public DefaultLayoutControllerFactory()
  {
    registry = new HashMap();

  }

  public void initialize (final ReportJob job)
  {
    final Configuration configuration = job.getConfiguration();

    final Iterator propertyKeys =
        configuration.findPropertyKeys(DefaultLayoutControllerFactory.PREFIX);
    while (propertyKeys.hasNext())
    {
      final String key = (String) propertyKeys.next();
      final String nodeClassName = key.substring
          (DefaultLayoutControllerFactory.PREFIX.length());
      final String procClassName = configuration.getConfigProperty(key);

      final Class nodeClass = load(nodeClassName);
      final Object processor = ObjectUtilities.loadAndInstantiate
          (procClassName, DefaultLayoutControllerFactory.class, LayoutController.class);
      if (nodeClass == null || processor == null)
      {
        // sanity check ..
        continue;
      }

      registry.put(nodeClassName, procClassName);
    }
  }

  private Class load (final String className)
  {
    if (className == null)
    {
      return null;
    }

    final ClassLoader classLoader = ObjectUtilities.getClassLoader
        (DefaultLayoutControllerFactory.class);
    try
    {
      return classLoader.loadClass(className);
    }
    catch (ClassNotFoundException e)
    {
      return null;
    }
  }

  public LayoutController create(final FlowController controller,
                                 final Object node,
                                 final LayoutController parent)
      throws ReportProcessingException, ReportDataFactoryException, DataSourceException
  {
    Class nodeClass = node.getClass();
    while (Node.class.isAssignableFrom(nodeClass))
    {
      final String targetClass = (String) registry.get(nodeClass.getName());
      if (targetClass != null)
      {
        final LayoutController lc = (LayoutController)
            ObjectUtilities.loadAndInstantiate (targetClass,
                DefaultLayoutControllerFactory.class, LayoutController.class);
        if (lc != null)
        {
          lc.initialize(node, controller, parent);
          return lc;
        }
      }
      nodeClass = nodeClass.getSuperclass();
    }

    throw new ReportProcessingException("No processor for node " + node);
  }
}
