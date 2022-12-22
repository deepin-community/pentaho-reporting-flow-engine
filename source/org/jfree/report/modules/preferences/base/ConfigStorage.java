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
 * $Id: ConfigStorage.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.preferences.base;

import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * Config storage implementations are used to store a set of properties to a certain key.
 * <p/>
 * A valid configuration path does not contain dots, semicolons or colons.
 * <p/>
 * A valid path obeys to the same rules as java identifiers ..
 *
 * @author Thomas Morgner
 */
public interface ConfigStorage
{
  /**
   * Stores the given properties on the defined path.
   *
   * @param configPath the path on where to store the properties.
   * @param properties the properties which should be stored.
   * @throws ConfigStoreException if an error occured.
   */
  public void store (String configPath, Configuration properties)
          throws ConfigStoreException;

  /**
   * Loads the properties from the given path, specifying the given properties as
   * default.
   *
   * @param configPath the configuration path from where to read the properties.
   * @param defaults   the property set that acts as fallback to provide default values.
   * @return the loaded properties
   *
   * @throws ConfigStoreException if an error occured.
   */
  public Configuration load (String configPath, Configuration defaults)
          throws ConfigStoreException;

  /**
   * Tests, whether some configuration data exists for the given configuration.
   *
   * @param configPath the configuration path to the property storage.
   * @return true, if there are properties under this path, false otherwise.
   */
  public boolean isAvailable (String configPath);
}
