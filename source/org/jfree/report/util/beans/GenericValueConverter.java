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
 * $Id: GenericValueConverter.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.util.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

/**
 * A class that handles the conversion of {@link Integer} attributes to and from their
 * {@link String} representation.
 *
 * @author Thomas Morgner
 */
public class GenericValueConverter implements ValueConverter
{
  private PropertyDescriptor propertyDescriptor;
  private PropertyEditor propertyEditor;

  /**
   * Creates a new value converter.
   */
  public GenericValueConverter (final PropertyDescriptor pd)
          throws IntrospectionException
  {
    if (pd == null)
    {
      throw new NullPointerException("PropertyDescriptor must not be null.");
    }
    if (pd.getPropertyEditorClass() == null)
    {
      throw new IntrospectionException("Property has no editor.");
    }
    this.propertyDescriptor = pd;
    this.propertyEditor = createPropertyEditor(pd);
  }

  private PropertyEditor createPropertyEditor (final PropertyDescriptor pi)
          throws IntrospectionException
  {
    final Class c = pi.getPropertyEditorClass();
    try
    {
      return (PropertyEditor) c.newInstance();
    }
    catch (Exception e)
    {
      throw new IntrospectionException("Unable to create PropertyEditor.");
    }
  }


  /**
   * Converts the attribute to a string.
   *
   * @param o the attribute ({@link Integer} expected).
   * @return A string representing the {@link Integer} value.
   * @throws BeanException if there was an error during the conversion.
   */
  public String toAttributeValue (final Object o) throws BeanException
  {
    if (BeanUtility.getPropertyType(propertyDescriptor).isInstance(o) == false)
    {
      throw new ClassCastException("Give me a real type.");
    }

    propertyEditor.setValue(o);
    return propertyEditor.getAsText();
  }

  /**
   * Converts a string to a {@link Integer}.
   *
   * @param s the string.
   * @return a {@link Integer}.
   */
  public Object toPropertyValue (final String s)
  {
    propertyEditor.setAsText(s);
    return propertyEditor.getValue();
  }
}
