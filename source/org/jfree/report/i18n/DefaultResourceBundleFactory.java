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
 * $Id: DefaultResourceBundleFactory.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A default implementation of the ResourceBundleFactory, that creates resource bundles
 * using the specified locale.
 * <p/>
 * If not defined otherwise, this implementation uses <code>Locale.getDefault()</code> as
 * Locale.
 *
 * @author Thomas Morgner
 */
public class DefaultResourceBundleFactory implements ResourceBundleFactory
{

  /**
   * Creates a new DefaultResourceBundleFactory using the system's default locale as
   * factory locale.
   */
  public DefaultResourceBundleFactory ()
  {
  }

  /**
   * Creates a resource bundle named by the given key and using the factory's defined
   * locale.
   *
   * @param key the name of the resourcebundle, never null.
   * @return the created resource bundle
   *
   * @throws NullPointerException if <code>key</code> is
   *                              <code>null</code>
   * @throws java.util.MissingResourceException
   *                              if no resource bundle for the specified base name can be
   *                              found
   * @see ResourceBundle#getBundle(String,Locale)
   */
  public ResourceBundle getResourceBundle (final Locale locale, final String key)
  {
    return ResourceBundle.getBundle(key, locale);
  }
}
