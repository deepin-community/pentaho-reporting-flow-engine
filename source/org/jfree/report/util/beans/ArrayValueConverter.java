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
 * $Id: ArrayValueConverter.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.util.beans;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.jfree.report.util.CSVTokenizer;

/**
 * An ValueConverter that handles Arrays. Conversion to arrays is done
 * using a CSV string.
 *
 * @author Thomas Morgner
 */
public class ArrayValueConverter implements ValueConverter
{
  /** The converter for the array elements. */
  private ValueConverter elementConverter;
  /** The element type. */
  private Class elementType;

  /**
   * Creates a new ArrayValueConverter for the given element type and
   * array type.
   * @param arrayClass the array type
   * @param elementConverter the value converter for the array elements.
   */
  public ArrayValueConverter (final Class arrayClass,
                              final ValueConverter elementConverter)
  {
    if (elementConverter == null)
    {
      throw new NullPointerException("elementConverter must not be null");
    }
    if (arrayClass == null)
    {
      throw new NullPointerException("arrayClass must not be null");
    }
    this.elementType = arrayClass;
    this.elementConverter = elementConverter;
  }

  /**
   * Converts an object to an attribute value.
   *
   * @param o the object.
   * @return the attribute value.
   * @throws BeanException if there was an error during the conversion.
   */
  public String toAttributeValue (final Object o) throws BeanException
  {
    final int size = Array.getLength(o);
    final StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < size; i++)
    {
      if (i != 0)
      {
        buffer.append(",");
      }
      buffer.append(elementConverter.toAttributeValue(Array.get(o, i)));
    }
    return buffer.toString();
  }

  /**
   * Converts a string to a property value.
   *
   * @param s the string.
   * @return a property value.
   * @throws BeanException if there was an error during the conversion.
   */
  public Object toPropertyValue (final String s) throws BeanException
  {
    final CSVTokenizer tokenizer = new CSVTokenizer(s);
    final ArrayList elements = new ArrayList();
    while (tokenizer.hasMoreTokens())
    {
      final String token = tokenizer.nextToken();
      elements.add(elementConverter.toPropertyValue(token));
    }
    final Object retval =
            Array.newInstance(elementType, elements.size());
    for (int i = 0; i < elements.size(); i++)
    {
      final Object o = elements.get(i);
      Array.set(retval, i, o);
    }
    return retval;
  }
}
