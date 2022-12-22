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
 * $Id: DefaultIconTheme.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.common;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.Icon;

import org.jfree.report.JFreeReportBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

/**
 * Creation-Date: 13.11.2006, 19:27:51
 *
 * @author Thomas Morgner
 */
public class DefaultIconTheme implements IconTheme
{
  private String bundleName;

  public DefaultIconTheme()
  {
    initialize(JFreeReportBoot.getInstance().getGlobalConfig());
  }

  public void initialize(Configuration configuration)
  {
    this.bundleName = configuration.getConfigProperty
        ("org.jfree.report.modules.gui.common.IconThemeBundle");
  }

  public Icon getSmallIcon(Locale locale, String id)
  {
    return getResourceBundleSupport(locale).getIcon(id, false);
  }

  public Icon getLargeIcon(Locale locale, String id)
  {
    return getResourceBundleSupport(locale).getIcon(id, true);
  }

  private ResourceBundleSupport getResourceBundleSupport(Locale locale)
  {
    if (bundleName == null)
    {
      throw new IllegalStateException
          ("No resource-bundle. Did you boot the engine?");
    }
    return new ResourceBundleSupport
        (locale, ResourceBundle.getBundle(bundleName, locale));
  }
}
