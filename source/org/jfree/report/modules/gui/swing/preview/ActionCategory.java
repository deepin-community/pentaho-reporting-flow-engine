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
 * $Id: ActionCategory.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.preview;

import org.jfree.report.modules.gui.swing.common.SwingGuiContext;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Creation-Date: 01.12.2006, 18:49:32
 *
 * @author Thomas Morgner
 */
public class ActionCategory implements Comparable
{
  private String resourceBase;
  private String resourcePrefix;
  private int position;
  private ResourceBundleSupport resources;
  private String name;

  public ActionCategory()
  {
    name = "";
  }

  public void initialize(SwingGuiContext context)
  {
    resources = new ResourceBundleSupport
        (context.getLocale(), resourceBase);
  }

  public String getResourceBase()
  {
    return resourceBase;
  }

  public void setResourceBase(final String resourceBase)
  {
    this.resourceBase = resourceBase;
  }

  public String getResourcePrefix()
  {
    return resourcePrefix;
  }

  public void setResourcePrefix(final String resourcePrefix)
  {
    this.resourcePrefix = resourcePrefix;
  }

  public int getPosition()
  {
    return position;
  }

  public void setPosition(final int position)
  {
    this.position = position;
  }

  public String getName()
  {
    return name;
  }

  public void setName(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    this.name = name;
  }

  /**
   * Returns the display name for the export action.
   *
   * @return The display name.
   */
  public String getDisplayName()
  {
    return resources.getString(resourcePrefix + "name");
  }

  /**
   * Returns the short description for the export action.
   *
   * @return The short description.
   */
  public String getShortDescription()
  {
    return resources.getString(resourcePrefix + "description");
  }

  /**
   * Returns the mnemonic key code.
   *
   * @return The code.
   */
  public Integer getMnemonicKey()
  {
    return resources.getOptionalMnemonic(resourcePrefix + "mnemonic");
  }

  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    final ActionCategory that = (ActionCategory) o;

    if (position != that.position)
    {
      return false;
    }
    if (!name.equals(that.name))
    {
      return false;
    }

    return true;
  }

  public int hashCode()
  {
    int result;
    result = position;
    result = 29 * result + name.hashCode();
    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less than,
   * equal to, or greater than the specified object.<p>
   * <p/>
   *
   * @param o the Object to be compared.
   * @return a negative integer, zero, or a positive integer as this object is
   *         less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it from
   *                            being compared to this Object.
   */
  public int compareTo(final Object o)
  {
    ActionCategory other = (ActionCategory) o;
    if (position < other.position)
    {
      return -1;
    }
    if (position > other.position)
    {
      return 1;
    }
    return name.compareTo(other.name);
  }

  public String toString()
  {
    return "ActionCategory{" +
        "name='" + name + '\'' +
        ", position=" + position +
        ", resourceBase='" + resourceBase + '\'' +
        ", resourcePrefix='" + resourcePrefix + '\'' +
        ", resources=" + resources +
        '}';
  }
}
