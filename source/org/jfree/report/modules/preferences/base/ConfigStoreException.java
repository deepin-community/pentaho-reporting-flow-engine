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
 * $Id: ConfigStoreException.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.preferences.base;

import org.pentaho.reporting.libraries.base.util.StackableException;


/**
 * The config store exception is throwns if an error prevents an operation on the current
 * configuration storage provider.
 *
 * @author Thomas Morgner
 */
public class ConfigStoreException extends StackableException
{
  /**
   * DefaultConstructor.
   */
  public ConfigStoreException ()
  {
  }

  /**
   * Creates a config store exception with the given message and root exception.
   *
   * @param s the exception message.
   * @param e the exception that caused all the trouble.
   */
  public ConfigStoreException (final String s, final Exception e)
  {
    super(s, e);
  }

  /**
   * Creates a config store exception with the given message.
   *
   * @param s the message.
   */
  public ConfigStoreException (final String s)
  {
    super(s);
  }
}
