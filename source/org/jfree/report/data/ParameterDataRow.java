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
 * $Id: ParameterDataRow.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.data;

import org.jfree.report.DataRow;
import org.jfree.report.DataSourceException;
import org.jfree.report.flow.ParameterMapping;
import org.jfree.report.util.ReportParameters;

/**
 * This is the first datarow in each report. It holds the values of all declared
 * input parameters. This datarow does not advance and does not keep track of
 * any changes, as parameters are considered read-only once the reporting has
 * started.
 *
 * @author Thomas Morgner
 */
public class ParameterDataRow extends StaticDataRow
{
  public ParameterDataRow(final ReportParameters parameters)
  {
    final String[] names = parameters.keys();
    final Object[] values = new Object[parameters.size()];

    for (int i = 0; i < names.length; i++)
    {
      final String key = names[i];
      values[i] = parameters.get(key);
    }
    setData(names, values);
  }

  public ParameterDataRow(final ParameterMapping[] parameters, final DataRow dataRow)
          throws DataSourceException
  {
    final String[] innerNames = new String[parameters.length];
    final Object[] values = new Object[parameters.length];
    for (int i = 0; i < parameters.length; i++)
    {
      final ParameterMapping parameter = parameters[i];
      final String name = parameter.getName();
      innerNames[i] = parameter.getAlias();
      values[i] = dataRow.get(name);
    }
    setData(innerNames, values);
  }
}
