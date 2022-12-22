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
 * $Id: LocaleValueConverter.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.util.beans;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Creation-Date: 24.01.2006, 19:19:03
 *
 * @author Thomas Morgner
 */
public class LocaleValueConverter implements ValueConverter
{
  public LocaleValueConverter()
  {
  }

  /**
   * Converts an object to an attribute value.
   *
   * @param o the object.
   * @return the attribute value.
   * @throws BeanException if there was an error during the conversion.
   */
  public String toAttributeValue(Object o) throws BeanException
  {
    Locale l = (Locale) o;
    if (l.getCountry().equals(""))
    {
      return l.getLanguage();
    }
    else if (l.getVariant().equals(""))
    {
      return l.getLanguage() + "_" + l.getCountry();
    }
    else
    {
      return l.getLanguage() + "_" + l.getCountry() + "_" + l.getVariant();
    }
  }

  /**
   * Converts a string to a property value.
   *
   * @param s the string.
   * @return a property value.
   * @throws BeanException if there was an error during the conversion.
   */
  public Object toPropertyValue(String s) throws BeanException
  {
    StringTokenizer strtok = new StringTokenizer(s.trim(), "_");
    if (strtok.hasMoreElements() == false)
    {
      throw new BeanException("This is no valid locale specification.");
    }
    String language = strtok.nextToken();
    String country = "";
    if (strtok.hasMoreTokens())
    {
      country = strtok.nextToken();
    }
    String variant = "";
    if (strtok.hasMoreTokens())
    {
      variant = strtok.nextToken();
    }
    return new Locale(language, country, variant);
  }
}
