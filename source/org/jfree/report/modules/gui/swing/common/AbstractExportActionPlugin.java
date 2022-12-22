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
 * $Id: AbstractExportActionPlugin.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.common;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.lang.reflect.Constructor;

import org.jfree.report.flow.ReportJob;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.apache.commons.logging.Log;

/**
 * Creation-Date: 02.12.2006, 14:21:07
 *
 * @author Thomas Morgner
 */
public abstract class AbstractExportActionPlugin extends AbstractActionPlugin
  implements ExportActionPlugin
{

  public AbstractExportActionPlugin()
  {
  }


  /**
   * Creates a progress dialog, and tries to assign a parent based on the given
   * preview proxy.
   *
   * @return the progress dialog.
   */
  protected ExportDialog createExportDialog(final String className)
      throws InstantiationException
  {
    final Window proxy = getContext().getWindow();
    if (proxy instanceof Frame)
    {
      final ClassLoader classLoader =
          ObjectUtilities.getClassLoader(AbstractActionPlugin.class);
      try
      {
        final Class aClass = classLoader.loadClass(className);
        final Constructor constructor =
            aClass.getConstructor(new Class[]{Frame.class});
        return (ExportDialog) constructor.newInstance(new Object[]{proxy});
      }
      catch (Exception e)
      {
        DebugLog.log("Failed to instantiate Export-Dialog with a 'Frame'-parent: " + className);
      }
    }
    else if (proxy instanceof Dialog)
    {
      final ClassLoader classLoader =
          ObjectUtilities.getClassLoader(AbstractActionPlugin.class);
      try
      {
        final Class aClass = classLoader.loadClass(className);
        final Constructor constructor =
            aClass.getConstructor(new Class[]{Dialog.class});
        return (ExportDialog) constructor.newInstance(new Object[]{proxy});
      }
      catch (Exception e)
      {
        DebugLog.log("Failed to instantiate Export-Dialog with a 'Dialog'-parent: " + className, e);
      }
    }

    final Object fallBack = ObjectUtilities.loadAndInstantiate
        (className, AbstractActionPlugin.class, ExportDialog.class);
    if (fallBack != null)
    {
      return (ExportDialog) fallBack;
    }

    DebugLog.log("Failed to instantiate Export-Dialog without a parent: " + className);
    throw new InstantiationException("Failed to instantiate Export-Dialog");
  }


  /**
   * Exports a report.
   *
   * @return A boolean.
   */
  public boolean performShowExportDialog(ReportJob job, String configKey)
  {
    try
    {
      final Configuration configuration = job.getConfiguration();
      final String dialogClassName = configuration.getConfigProperty(configKey);
      final ExportDialog dialog = createExportDialog(dialogClassName);
      final boolean dialogResult = dialog.performQueryForExport(job, getContext());
      return dialogResult;
    }
    catch (InstantiationException e)
    {
      DebugLog.log("Unable to configure the report job.");
      setStatusText("Unable to configure the report job.");
      return false;
    }
  }

}
