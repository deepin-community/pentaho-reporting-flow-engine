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
 * $Id: AttributeNameGenerator.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.util;

import java.util.HashMap;

/**
 * Creation-Date: 21.11.2006, 13:24:41
 *
 * @author Thomas Morgner
 */
public class AttributeNameGenerator
{
  private HashMap names;

  public AttributeNameGenerator()
  {
    names = new HashMap();
  }

  public String generateName (final String base)
  {
    final Long keyObject = (Long) names.get(base);
    if (keyObject == null)
    {
      names.put (base, new Long(0));
      return base;
    }
    else
    {
      long keyIdx = keyObject.longValue();
      String newName = base + keyIdx;
      while (names.containsKey(newName))
      {
        keyIdx += 1;
        newName = base + keyIdx;
      }

      names.put (newName, new Long (0));
      names.put (base, new Long (keyIdx));
      return newName;
    }
  }

  public void reset ()
  {
    names.clear();
  }
}
