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
 * $Id: ResourceBundleFactory.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.i18n;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A resource bundle factory defines the locale for a report and is used to create
 * resourcebundles.
 *
 * @author Thomas Morgner
 */
public interface ResourceBundleFactory extends Serializable
{
  public static final String DEFAULT_RESOURCE_BUNDLE_CONFIG_KEY =
          "org.jfree.report.ResourceBundle";
  /**
   * Creates a resource bundle for the given key. How that key is interpreted depends on
   * the used concrete implementation of this interface.
   *
   * @param key the key that identifies the resource bundle
   * @return the created resource bundle
   *
   * @throws java.util.MissingResourceException
   *          if no resource bundle for the specified base name can be found
   */
  public ResourceBundle getResourceBundle (final Locale locale, final String key);
}
