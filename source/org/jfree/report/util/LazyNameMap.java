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
 * $Id: LazyNameMap.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Creation-Date: 06.03.2006, 20:20:17
 *
 * @author Thomas Morgner
 */
public class LazyNameMap implements Cloneable
{
  public static class NameCarrier
  {
    private int value;
    private int counter;

    public NameCarrier(final int value)
    {
      this.value = value;
      this.counter = 1;
    }

    public int getValue()
    {
      return value;
    }

    public int getInstanceCount()
    {
      return counter;
    }

    public void increase()
    {
      this.counter += 1;
    }

    public void decrease()
    {
      this.counter -= 1;
    }
  }

  private HashMap map;
  private ArrayList names;
  private boolean clean;

  public LazyNameMap()
  {
    map = new HashMap();
    names = new ArrayList();
    clean = false;
  }

  public boolean isClean()
  {
    return clean;
  }

  public void setValue(final String key, final int value)
  {
    if (clean)
    {
      map = (HashMap) map.clone();
      names = (ArrayList) names.clone();
      clean = false;
    }

    map.put(key, new NameCarrier(value));
  }

  public NameCarrier get(String key)
  {
    return (NameCarrier) map.get(key);
  }

  public NameCarrier remove(final String key)
  {
    NameCarrier nc = (NameCarrier) map.get(key);
    if (nc == null)
    {
      return null;
    }

    if (clean)
    {
      map = (HashMap) map.clone();
      names = (ArrayList) names.clone();
      clean = false;
    }
    return (NameCarrier) map.remove(key);
  }

  public Object clone()
  {
    try
    {
      LazyNameMap lm = (LazyNameMap) super.clone();
      lm.clean = true;
      clean = true;
      return lm;
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException("Clone failed for some reason");
    }
  }
}
