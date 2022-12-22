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
 * $Id: ReportParameters.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.util;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The report parameters collection is a map with string keys. The parameters
 * can be used in a query and will appear as part of the datarow.
 *
 * @author Thomas Morgner
 */
public final class ReportParameters implements Serializable, Cloneable
{
  /**
   * Storage for the properties.
   */
  private HashMap properties;

  /**
   * Copy constructor.
   *
   * @param props an existing ReportProperties instance.
   */
  public ReportParameters (final ReportParameters props)
  {
    this.properties = new HashMap(props.properties);
  }

  /**
   * Default constructor.
   */
  public ReportParameters ()
  {
    this.properties = new HashMap();
  }

  /**
   * Adds a property to this properties collection. If a property with the given name
   * exist, the property will be replaced with the new value. If the value is null, the
   * property will be removed.
   *
   * @param key   the property key.
   * @param value the property value.
   */
  public void put (final String key, final Object value)
  {
    if (key == null)
    {
      throw new NullPointerException
              ("ReportProperties.put (..): Parameter 'key' must not be null");
    }
    if (value == null)
    {
      this.properties.remove(key);
    }
    else
    {
      this.properties.put(key, value);
    }
  }

  /**
   * Retrieves the value stored for a key in this properties collection.
   *
   * @param key the property key.
   * @return The stored value, or <code>null</code> if the key does not exist in this
   *         collection.
   */
  public Object get (final String key)
  {
    if (key == null)
    {
      throw new NullPointerException
              ("ReportProperties.get (..): Parameter 'key' must not be null");
    }
    return this.properties.get(key);
  }

  /**
   * Retrieves the value stored for a key in this properties collection, and returning the
   * default value if the key was not stored in this properties collection.
   *
   * @param key          the property key.
   * @param defaultValue the default value to be returned when the key is not stored in
   *                     this properties collection.
   * @return The stored value, or the default value if the key does not exist in this
   *         collection.
   */
  public Object get (final String key, final Object defaultValue)
  {
    if (key == null)
    {
      throw new NullPointerException
              ("ReportProperties.get (..): Parameter 'key' must not be null");
    }
    final Object o = this.properties.get(key);
    if (o == null)
    {
      return defaultValue;
    }
    return o;
  }

  /**
   * Returns all property keys as array.
   *
   * @return an enumeration of the property keys.
   */
  public String[] keys ()
  {
    return (String[]) this.properties.keySet().toArray(new String[properties.size()]);
  }

  /**
   * Removes all properties stored in this collection.
   */
  public void clear ()
  {
    this.properties.clear();
  }

  /**
   * Checks whether the given key is stored in this collection of ReportProperties.
   *
   * @param key the property key.
   * @return true, if the given key is known.
   */
  public boolean containsKey (final String key)
  {
    if (key == null)
    {
      throw new NullPointerException
              ("ReportProperties.containsKey (..): Parameter key must not be null");
    }
    return this.properties.containsKey(key);
  }

  /**
   * Clones the properties.
   *
   * @return a copy of this ReportProperties object.
   *
   * @throws CloneNotSupportedException this should never happen.
   */
  public Object clone ()
          throws CloneNotSupportedException
  {
    final ReportParameters p = (ReportParameters) super.clone();
    p.properties = (HashMap) this.properties.clone();
    return p;
  }

  public int size()
  {
    return properties.size();
  }
}
