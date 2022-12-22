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
 * $Id: ColorValueConverter.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.util.beans;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A class that handles the conversion of {@link Integer} attributes to and from their
 * {@link String} representation.
 *
 * @author Thomas Morgner
 */
public class ColorValueConverter implements ValueConverter
{

  /**
   * Creates a new value converter.
   */
  public ColorValueConverter ()
  {
    super();
  }

  /**
   * Converts the attribute to a string.
   *
   * @param o the attribute ({@link Integer} expected).
   * @return A string representing the {@link Integer} value.
   */
  public String toAttributeValue (final Object o)
  {
    if (!(o instanceof Color))
    {
      throw new ClassCastException("Is no instance of java.awt.Color");
    }
    final Color c = (Color) o;

    try
    {
      final Field[] fields = Color.class.getFields();
      for (int i = 0; i < fields.length; i++)
      {
        final Field f = fields[i];
        if (Modifier.isPublic(f.getModifiers())
                && Modifier.isFinal(f.getModifiers())
                && Modifier.isStatic(f.getModifiers()))
        {
          final String name = f.getName();
          final Object oColor = f.get(null);
          if (oColor instanceof Color)
          {
            if (c.equals(oColor))
            {
              return name;
            }
          }
        }
      }
    }
    catch (Exception e)
    {
      //
    }

    // no defined constant color, so this must be a user defined color
    final String color = Integer.toHexString(c.getRGB() & 0x00ffffff);
    final StringBuffer retval = new StringBuffer(7);
    retval.append("#");

    final int fillUp = 6 - color.length();
    for (int i = 0; i < fillUp; i++)
    {
      retval.append("0");
    }

    retval.append(color);
    return retval.toString();
  }

  /**
   * Converts a string to a {@link Integer}.
   *
   * @param value the string.
   * @return a {@link Integer}.
   */
  public Object toPropertyValue (final String value)
  {
    if (value == null)
    {
      return Color.black;
    }
    try
    {
      // get color by hex or octal value
      return Color.decode(value);
    }
    catch (NumberFormatException nfe)
    {
      // if we can't decode lets try to get it by name
      try
      {
        // try to get a color by name using reflection
        final Field f = Color.class.getField(value);

        return (Color) f.get(null);
      }
      catch (Exception ce)
      {
        throw new IllegalArgumentException
                ("The color string '" + value + "' is not recognized.");
      }
    }
  }
}
