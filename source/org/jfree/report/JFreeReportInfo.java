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
 * $Id: JFreeReportInfo.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report;

import org.jfree.layouting.LibLayoutInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.serializer.LibSerializerInfo;
import org.pentaho.reporting.libraries.resourceloader.LibLoaderInfo;
import org.pentaho.reporting.libraries.formula.LibFormulaInfo;
import org.pentaho.reporting.libraries.xmlns.LibXmlInfo;

/**
 * Details about the JFreeReport project.
 *
 * @author David Gilbert
 */
public class JFreeReportInfo extends ProjectInformation
{
  /**
   * This namespace covers all current reporting structures. These structures
   * are not being forwarded to libLayout and should be treated to be generally
   * invisible for all non-liblayout output targets.
   *
   * This is not an XML namespace, so dont expect to see documents using that
   * namespace. It is purely internal.
   */
  public static final String REPORT_NAMESPACE =
          "http://jfreereport.sourceforge.net/namespaces/engine";

  /**
   * This namespace contains the compatibility layer for the old JFreeReport
   * structures.
   *
   * This is not an XML namespace, so dont expect to see documents using that
   * namespace. It is purely internal.
   */
  public static final String COMPATIBILITY_NAMESPACE =
          "http://jfreereport.sourceforge.net/namespaces/engine/compatibility";

  private static JFreeReportInfo instance;

  public static synchronized JFreeReportInfo getInstance()
  {
    if (instance == null)
    {
      instance = new JFreeReportInfo();
    }
    return instance;
  }

  /**
   * Constructs an object containing information about the JFreeReport project.
   * <p/>
   * Uses a resource bundle to localise some of the information.
   */
  private JFreeReportInfo ()
  {
    super("flow-engine", "Pentaho Reporting Flow-Engine");
    setVersion("0.9.2");
    setInfo("http://reporting.pentaho.org/");
    setCopyright
            ("(C)opyright 2000-2007, by Pentaho Corporation, Object Refinery Limited and Contributors");

    addLibrary(LibSerializerInfo.getInstance());
    addLibrary(LibLoaderInfo.getInstance());
    addLibrary(LibLayoutInfo.getInstance());
    addLibrary(LibFormulaInfo.getInstance());
    addLibrary(LibXmlInfo.getInstance());

    final ProjectInformation pixieLibraryInfo = tryLoadPixieInfo();
    if (pixieLibraryInfo != null)
    {
      addLibrary(pixieLibraryInfo);
    }
    final ProjectInformation libfontsLibraryInfo = tryLoadLibFontInfo();
    if (libfontsLibraryInfo != null)
    {
      addLibrary(libfontsLibraryInfo);
    }

    setBootClass(JFreeReportBoot.class.getName());
  }

  /**
   * Tries to load the Pixie boot classes. If the loading fails,
   * this method returns null.
   *
   * @return the Pixie boot info, if Pixie is available.
   */
  private static ProjectInformation tryLoadPixieInfo ()
  {
    try
    {
      return (ProjectInformation) ObjectUtilities.loadAndInstantiate
              ("org.jfree.pixie.PixieInfo", JFreeReportInfo.class,
                  ProjectInformation.class);
    }
    catch (Exception e)
    {
      return null;
    }
  }

  /**
   * Tries to load the libFonts boot classes. If the loading fails,
   * this method returns null.
   *
   * @return the Pixie boot info, if Pixie is available.
   */
  private static ProjectInformation tryLoadLibFontInfo ()
  {
    try
    {
      return (ProjectInformation) ObjectUtilities.loadAndInstantiate
              ("org.jfree.fonts.LibFontInfo",
                  JFreeReportInfo.class, ProjectInformation.class);
    }
    catch (Exception e)
    {
      return null;
    }
  }

  /**
   * Checks, whether the Pixie-library is available and in a working condition.
   *
   * @return true, if Pixie is available, false otherwise.
   */
  public static boolean isPixieAvailable ()
  {
    return tryLoadPixieInfo() != null;
  }

  /**
   * Print the library version and information about the library.
   * <p/>
   * After there seems to be confusion about which version is currently used by the user,
   * this method will print the project information to clarify this issue.
   *
   * @param args ignored
   */
  public static void main (final String[] args)
  {
    final JFreeReportInfo info = new JFreeReportInfo();
    System.out.println(info.getName() + " " + info.getVersion());
    System.out.println("----------------------------------------------------------------");
    System.out.println(info.getCopyright());
    System.out.println(info.getInfo());
    System.out.println("----------------------------------------------------------------");
    System.out.println("This project is licenced under the terms of the "
            + info.getLicenseName() + ".");
    System.exit(0);
  }
}

