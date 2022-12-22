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
 * $Id: ConfigFactory.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.preferences.base;


/**
 * The config factory is used to access the currently active config storage
 * implementation. The implementation itself allows to read or store a set of properties
 * stored under a certain path.
 *
 * @author Thomas Morgner
 */
public final class ConfigFactory
{
  /**
   * The selector configuration key that defines the active config storage
   * implementation.
   */
  public static final String CONFIG_TARGET_KEY = "org.jfree.report.ConfigStore";

  /**
   * The singleton instance of the config factory.
   */
  private static ConfigFactory factory;
  /**
   * The user storage is used to store user dependend settings.
   */
  private ConfigStorage userStorage;
  /**
   * The system storage is used to store system wide settings.
   */
  private ConfigStorage systemStorage;

  /**
   * Returns the singleton instance of the config factory.
   *
   * @return the config factory
   */
  public synchronized static ConfigFactory getInstance ()
  {
    if (factory == null)
    {
      factory = new ConfigFactory();
      factory.defineSystemStorage(new NullConfigStorage());
      factory.defineUserStorage(new NullConfigStorage());
    }
    return factory;
  }

  /**
   * DefaultConstructor.
   */
  private ConfigFactory ()
  {
  }

  /**
   * Defines the user storage implementation that should be used. This method should only
   * be called by the module initialization methods.
   *
   * @param storage the user settings storage implementation.
   */
  public void defineUserStorage (final ConfigStorage storage)
  {
    if (storage == null)
    {
      throw new NullPointerException();
    }
    this.userStorage = storage;
  }

  /**
   * Defines the system storage implementation that should be used. This method should
   * only be called by the module initialization methods.
   *
   * @param storage the system settings storage implementation.
   */
  public void defineSystemStorage (final ConfigStorage storage)
  {
    if (storage == null)
    {
      throw new NullPointerException();
    }
    this.systemStorage = storage;
  }

  /**
   * Returns the user settings storage implementation used in the config subsystem.
   *
   * @return the user settingsstorage provider.
   */
  public ConfigStorage getUserStorage ()
  {
    return userStorage;
  }

  /**
   * Returns the system settings storage implementation used in the config subsystem.
   *
   * @return the system settings storage provider.
   */
  public ConfigStorage getSystemStorage ()
  {
    return systemStorage;
  }

  /**
   * Checks, whether the given string denotes a valid config storage path. Such an path
   * must not contain whitespaces or non-alphanumeric characters.
   *
   * @param path the path that should be tested.
   * @return true, if the path is valid, false otherwise.
   */
  public static boolean isValidPath (final String path)
  {
    final char[] data = path.toCharArray();
    for (int i = 0; i < data.length; i++)
    {
      if (Character.isJavaIdentifierPart(data[i]) == false)
      {
        return false;
      }
    }
    return true;
  }

  /**
   * Encodes the given configuration path. All non-ascii characters get
   * replaced by an escape sequence.
   *
   * @param path the path.
   * @return the translated path.
   */
  public static String encodePath (final String path)
  {
    final char[] data = path.toCharArray();
    final StringBuffer encoded = new StringBuffer();
    for (int i = 0; i < data.length; i++)
    {
      if (data[i] == '$')
      {
        // double quote
        encoded.append('$');
        encoded.append('$');
      }
      else if (Character.isJavaIdentifierPart(data[i]) == false)
      {
        // padded hex string
        encoded.append('$');
        final String hex = Integer.toHexString(data[i]);
        for (int x = hex.length(); x < 4; x++)
        {
          encoded.append('0');
        }
        encoded.append(hex);
      }
      else
      {
        encoded.append(data[i]);
      }
    }
    return encoded.toString();

  }
}
