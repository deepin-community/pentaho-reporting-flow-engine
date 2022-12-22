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
 * $Id: ImportedVariablesDataRow.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.data;

import org.jfree.report.DataRow;
import org.jfree.report.DataSourceException;
import org.jfree.report.flow.ParameterMapping;

/**
 * Creation-Date: 06.03.2006, 18:15:06
 *
 * @author Thomas Morgner
 */
public class ImportedVariablesDataRow extends StaticDataRow
{
  private String[] outerNames;
  private String[] innerNames;

  public ImportedVariablesDataRow(final GlobalMasterRow innerRow)
          throws DataSourceException
  {
    final DataRow globalView = innerRow.getGlobalView();
    final int cols = globalView.getColumnCount();
    this.outerNames = new String[cols];
    this.innerNames = outerNames;
    final Object[] values = new Object[outerNames.length];
    for (int i = 0; i < outerNames.length; i++)
    {
      outerNames[i] = globalView.getColumnName(i);
      values[i] = globalView.get(i);
    }
    setData(outerNames, values);
  }

  /**
   * Maps the inner-row into the outer data row. The parameter mapping's name
   * represents the *outer* name and the innernames.
   *
   * @param innerRow
   * @param parameterMappings
   * @throws DataSourceException
   */
  public ImportedVariablesDataRow(final GlobalMasterRow innerRow,
                                  final ParameterMapping[] parameterMappings)
          throws DataSourceException
  {
    this.outerNames = new String[parameterMappings.length];
    this.innerNames = new String[parameterMappings.length];
    final Object[] values = new Object[parameterMappings.length];
    final DataRow globalView = innerRow.getGlobalView();
    for (int i = 0; i < parameterMappings.length; i++)
    {
      final ParameterMapping mapping = parameterMappings[i];
      String name = mapping.getAlias();
      values[i] = globalView.get(name);
      innerNames[i] = name;
      outerNames[i] = mapping.getName();
    }
    setData(outerNames, values);
  }

  protected ImportedVariablesDataRow(final ImportedVariablesDataRow dataRow)
  {
    super(dataRow);
    outerNames = dataRow.outerNames;
    innerNames = dataRow.innerNames;
  }

  public ImportedVariablesDataRow advance (final GlobalMasterRow innerRow)
          throws DataSourceException
  {
    final DataRow globalView = innerRow.getGlobalView();
    final Object[] values = new Object[outerNames.length];
    for (int i = 0; i < innerNames.length; i++)
    {
      String name = innerNames[i];
      values[i] = globalView.get(name);
    }
    ImportedVariablesDataRow idr = new ImportedVariablesDataRow(this);
    idr.setData(outerNames, values);
    return idr;
  }
}
