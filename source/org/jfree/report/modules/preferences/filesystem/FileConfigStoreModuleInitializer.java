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
 * $Id: FileConfigStoreModuleInitializer.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.preferences.filesystem;

import java.io.File;

import org.jfree.report.JFreeReportBoot;
import org.jfree.report.modules.preferences.base.ConfigFactory;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializer;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;

/**
 * The initializer is used to setup the file system storage provider and to register the
 * providers at the configfactory.
 * <p/>
 * The directories are specified in the report configuration at boot time. If an directory
 * name starts with "~/", the users home directory is used as base directory for that
 * string.
 *
 * @author Thomas Morgner
 */
public class FileConfigStoreModuleInitializer implements ModuleInitializer
{
  /**
   * The configuration key that specifies the base directory for the user configuration
   * storage.
   */
  public static final String USER_BASEDIR_CONFIG_KEY =
          "org.jfree.report.modules.misc.configstore.filesystem.UserTargetDir";

  /**
   * The configuration key that specifies the base directory for the system configuration
   * storage.
   */
  public static final String SYSTEM_BASEDIR_CONFIG_KEY =
          "org.jfree.report.modules.misc.configstore.filesystem.SystemTargetDir";

  /**
   * DefaultConstructor.
   */
  public FileConfigStoreModuleInitializer ()
  {
  }

  /**
   * Performs the module initialization and registers the storage providers at the config
   * factory.
   *
   * @throws ModuleInitializeException if an error occures
   */
  public void performInit ()
          throws ModuleInitializeException
  {
    final String userBaseDirectory =
            JFreeReportBoot.getInstance().getGlobalConfig().getConfigProperty
            (USER_BASEDIR_CONFIG_KEY, "~/.jfreereport/user");

    final String systemBaseDirectory =
            JFreeReportBoot.getInstance().getGlobalConfig().getConfigProperty
            (SYSTEM_BASEDIR_CONFIG_KEY, "~/.jfreereport/system");

    final ConfigFactory factory = ConfigFactory.getInstance();
    factory.defineUserStorage(new FileConfigStorage(getStoragePath(userBaseDirectory)));
    factory.defineSystemStorage(new FileConfigStorage(getStoragePath(systemBaseDirectory)));
  }

  /**
   * Tries to fint the specified directory and creates a new one if the directory does not
   * yet exist. An occurence of "~/" at the beginning of the name will be replaced with
   * the users home directory.
   *
   * @param baseDirectory the base directory as specified in the configuration.
   * @return the file object pointing to that directory.
   *
   * @throws ModuleInitializeException
   *          if an error occured or the directory could not be created.
   */
  private File getStoragePath (String baseDirectory)
          throws ModuleInitializeException
  {
    final File baseDirectoryFile;

    if (baseDirectory.startsWith("~/") == false)
    {
      baseDirectoryFile = new File(baseDirectory);
    }
    else
    {
      try
      {
        final String homeDirectory = System.getProperty("user.home");
        if (baseDirectory.equals("~/"))
        {
          baseDirectoryFile = new File(homeDirectory);
        }
        else
        {
          baseDirectory = "." + baseDirectory.substring(1);
          baseDirectoryFile = new File(homeDirectory, baseDirectory);
        }
      }
      catch (Exception e)
      {
        throw new ModuleInitializeException
                ("Failed to create the file config storage.", e);
      }
    }

    if (baseDirectoryFile.exists() == false)
    {
      if (baseDirectoryFile.mkdirs() == false)
      {
        throw new ModuleInitializeException
                ("Unable to create the specified directory.");
      }
    }
    else
    {
      if (baseDirectoryFile.canRead() == false ||
              baseDirectoryFile.canWrite() == false)
      {
        throw new ModuleInitializeException
                ("Unable to access the specified directory.");
      }
    }
    return baseDirectoryFile;
  }
}
