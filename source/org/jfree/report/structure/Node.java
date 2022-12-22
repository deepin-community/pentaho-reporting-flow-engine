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
 * $Id: Node.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.structure;

import java.io.Serializable;
import java.util.Locale;

import org.jfree.report.JFreeReport;
import org.jfree.report.expressions.Expression;

/**
 * A node is the most basic unit in a report. It acts as general superclass for
 * all other elements.
 *
 * @author Thomas Morgner
 */
public abstract class Node implements Serializable, Cloneable
{
  private Node parent;

  protected Node()
  {
  }

  public Node getParent()
  {
    return parent;
  }

  protected void setParent(final Node parent)
  {
    this.parent = parent;
  }

  /**
   * This is an extra method to allow me to track all *illegal* write-accesses
   * to the parent.
   *
   * @param parent
   */
  public void updateParent(final Node parent)
  {
    this.parent = parent;
  }

  public Group getGroup()
  {
    Node parent = getParent();
    while (parent != null)
    {
      if (parent instanceof Group)
      {
        return (Group) parent;
      }

      parent = parent.getParent();
    }
    return null;
  }

  public ReportDefinition getReport()
  {
    Node parent = getParent();
    while (parent != null)
    {
      if (parent instanceof ReportDefinition)
      {
        return (ReportDefinition) parent;
      }

      parent = parent.getParent();
    }
    return null;
  }

  public JFreeReport getRootReport()
  {
    Node parent = getParent();
    while (parent != null)
    {
      if (parent instanceof JFreeReport)
      {
        return (JFreeReport) parent;
      }

      parent = parent.getParent();
    }
    return null;
  }

  public Locale getLocale()
  {
    if (parent != null)
    {
      return parent.getLocale();
    }
    return Locale.getDefault();
  }

  public Expression getDisplayCondition()
  {
    return null;
  }

  public boolean isEnabled()
  {
    return true;
  }

  public Object clone () throws CloneNotSupportedException
  {
    return super.clone();
  }
}
