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
 * $Id: DriverConnectionProvider.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.data.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Creation-Date: 07.04.2006, 15:04:26
 *
 * @author Thomas Morgner
 */
public class DriverConnectionProvider implements ConnectionProvider
{
  private Properties properties;
  private String url;
  private String driver;
  private transient Connection connection;

  public DriverConnectionProvider()
  {
    this.properties = new Properties();
  }

  public String getProperty(final String key)
  {
    return properties.getProperty(key);
  }

  public Object setProperty(final String key, final String value)
  {
    return properties.setProperty(key, value);
  }

  public String getUrl()
  {
    return url;
  }

  public void setUrl(final String url)
  {
    this.url = url;
  }

  public String getDriver()
  {
    return driver;
  }

  public void setDriver(final String driver)
  {
    this.driver = driver;
  }

  public Connection getConnection() throws SQLException
  {
    if (url == null)
    {
      throw new NullPointerException("URL must not be null when connecting");
    }

    if (connection == null)
    {
      try
      {
        if (driver != null)
        {
          Class.forName(driver);
        }
      }
      catch(Exception e)
      {
        throw new SQLException("Unable to load the driver", e.getMessage());
      }

      connection = DriverManager.getConnection(url, properties);
    }
    return connection;
  }
}
