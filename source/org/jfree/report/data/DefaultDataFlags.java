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
 * $Id: DefaultDataFlags.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.data;

import java.util.Date;

import org.jfree.report.DataFlags;

/**
 * Creation-Date: 20.02.2006, 16:00:34
 *
 * @author Thomas Morgner
 */
public class DefaultDataFlags implements DataFlags
{
  private Object value;
  private boolean changed;
  private String name;

  public DefaultDataFlags(final String name,
                          final Object value,
                          final boolean changed)
  {
    this.value = value;
    this.changed = changed;
    this.name = name;
  }

  public boolean isNumeric()
  {
    return value instanceof Number;
  }

  public boolean isDate()
  {
    return value instanceof Date;
  }

  public boolean isNull()
  {
    return value == null;
  }

  public boolean isZero()
  {
    if (isNumeric() == false)
    {
      return false;
    }
    Number n = (Number) value;
    return (n.floatValue() == 0);
  }

  public boolean isNegative()
  {
    if (isNumeric() == false)
    {
      return false;
    }
    Number n = (Number) value;
    return (n.floatValue() < 0);
  }

  public boolean isPositive()
  {
    if (isNumeric() == false)
    {
      return false;
    }
    Number n = (Number) value;
    return (n.floatValue() > 0);
  }

  public boolean isChanged()
  {
    return changed;
  }

  public Object getValue()
  {
    return value;
  }

  public String getName()
  {
    return name;
  }

  public String toString ()
  {
    StringBuffer b = new StringBuffer();
    b.append("DefaultDataFlags={name=");
    b.append(name);
    b.append(", value=");
    b.append(value);
    b.append(", changed=");
    b.append(changed);
    b.append("}");
    return b.toString();
  }
}
