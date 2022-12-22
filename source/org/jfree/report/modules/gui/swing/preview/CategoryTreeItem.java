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
 * $Id: CategoryTreeItem.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.preview;

import java.util.ArrayList;

/**
 * Creation-Date: 01.12.2006, 20:01:32
 *
 * @author Thomas Morgner
 */
public class CategoryTreeItem implements Comparable
{
  private CategoryTreeItem parent;
  private ActionCategory category;
  private ArrayList childs;
  private String name;

  public CategoryTreeItem(final ActionCategory category)
  {
    this.category = category;
    this.name = category.getName();
  }

  public String getName()
  {
    return name;
  }

  public CategoryTreeItem getParent()
  {
    return parent;
  }

  public void setParent(final CategoryTreeItem parent)
  {
    this.parent = parent;
  }

  public ActionCategory getCategory()
  {
    return category;
  }

  public void add(final CategoryTreeItem item)
  {
    if (childs == null)
    {
      childs = new ArrayList();
    }
    childs.add(item);
  }

  public CategoryTreeItem[] getChilds()
  {
    if (childs == null)
    {
      return new CategoryTreeItem[0];
    }
    return (CategoryTreeItem[]) childs.toArray(new CategoryTreeItem[childs.size()]);
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
    CategoryTreeItem other = (CategoryTreeItem) o;
    final int position = category.getPosition();
    final int otherPosition = other.getCategory().getPosition();
    if (position < otherPosition)
    {
      return -1;
    }
    if (position > otherPosition)
    {
      return 1;
    }
    return name.compareTo(other.name);
  }

}
