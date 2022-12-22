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
 * $Id: JFreeReportCoreModule.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report;

import java.io.InputStream;

import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;


/**
 * The CoreModule is used to represent the base classes of JFreeReport in a
 * PackageManager-compatible way. Modules may request a certain core-version to be present
 * by referencing to this module.
 * <p>
 * This module is used to initialize the image and drawable factories. If the
 * Pixie library is available, support for WMF-files is added to the factories.
 *
 * @author Thomas Morgner
 */
public class JFreeReportCoreModule extends AbstractModule
{
  /**
   * The 'no-printer-available' property key.
   */
  public static final String NO_PRINTER_AVAILABLE_KEY
          = "org.jfree.report.NoPrinterAvailable";

  /**
   * The G2 fontrenderer bug override configuration key.
   */
  public static final String FONTRENDERER_ISBUGGY_FRC_KEY
          = "org.jfree.report.layout.fontrenderer.IsBuggyFRC";

  /**
   * The text aliasing configuration key.
   */
  public static final String FONTRENDERER_USEALIASING_KEY
          = "org.jfree.report.layout.fontrenderer.UseAliasing";

  /**
   * A configuration key that defines, whether errors will abort the report
   * processing. This defaults to true.
   */
  public static final String STRICT_ERROR_HANDLING_KEY
          = "org.jfree.report.StrictErrorHandling";
  /**
   * Creates a new module definition based on the 'coremodule.properties' file of this
   * package.
   *
   * @throws ModuleInitializeException if the file could not be loaded.
   */
  public JFreeReportCoreModule ()
          throws ModuleInitializeException
  {
    final InputStream in = ObjectUtilities.getResourceRelativeAsStream
            ("coremodule.properties", JFreeReportCoreModule.class);
    if (in == null)
    {
      throw new ModuleInitializeException
              ("File 'coremodule.properties' not found in JFreeReport package.");
    }
    loadModuleInfo(in);
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This
   * method is called only once in a modules lifetime. If the initializing cannot be
   * completed, throw a ModuleInitializeException to indicate the error,. The module will
   * not be available to the system.
   *
   * @param subSystem the subSystem.
   * @throws ModuleInitializeException
   *          if an error ocurred while initializing the module.
   */
  public void initialize (final SubSystem subSystem)
          throws ModuleInitializeException
  {
  }
}
