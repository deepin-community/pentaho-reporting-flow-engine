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
 * $Id: SubReport.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.structure;

import java.util.HashMap;
import java.util.Map;

import org.jfree.report.flow.ParameterMapping;

/**
 * Creation-Date: 04.03.2006, 21:38:21
 *
 * @author Thomas Morgner
 */
public class SubReport extends ReportDefinition
{
  private HashMap exportParameters;
  private HashMap inputParameters;

  public SubReport()
  {
    setType("sub-report");
    exportParameters = new HashMap();
    inputParameters = new HashMap();
  }

  public void addExportParameter (final String outerName,
                                  final String innerName)
  {
    exportParameters.put(outerName, innerName);
  }

  public void removeExportParameter (final String outerName)
  {
    exportParameters.remove(outerName);
  }

  public String getExportParameter (final String outerName)
  {
    return (String) exportParameters.get(outerName);
  }

  public String[] getExportParameters ()
  {
    return (String[])
            exportParameters.keySet().toArray(new String[exportParameters.size()]);
  }

  public String[] getPeerExportParameters ()
  {
    return (String[])
            exportParameters.values().toArray(new String[exportParameters.size()]);
  }

  public ParameterMapping[] getExportMappings ()
  {
    final Map.Entry[] inputEntries = (Map.Entry[])
        exportParameters.entrySet().toArray(new Map.Entry[exportParameters.size()]);
    final ParameterMapping[] mapping =
        new ParameterMapping[exportParameters.size()];

    for (int i = 0; i < inputEntries.length; i++)
    {
      final Map.Entry entry = inputEntries[i];
      mapping[i] = new ParameterMapping
          ((String) entry.getKey(), (String) entry.getValue());
    }
    return mapping;
  }

  public void addInputParameter (final String outerName,
                                 final String innerName)
  {
    inputParameters.put(outerName, innerName);
  }

  public void removeInputParameter (final String outerName)
  {
    inputParameters.remove(outerName);
  }

  public String getInnerParameter (final String outerName)
  {
    return (String) inputParameters.get(outerName);
  }

  public String[] getInputParameters ()
  {
    return (String[])
            inputParameters.keySet().toArray(new String[inputParameters.size()]);
  }

  public String[] getPeerInputParameters ()
  {
    return (String[])
            inputParameters.values().toArray(new String[inputParameters.size()]);
  }

  public ParameterMapping[] getInputMappings ()
  {
    final Map.Entry[] inputEntries = (Map.Entry[])
        inputParameters.entrySet().toArray(new Map.Entry[inputParameters.size()]);
    final ParameterMapping[] mapping =
        new ParameterMapping[inputParameters.size()];

    for (int i = 0; i < inputEntries.length; i++)
    {
      final Map.Entry entry = inputEntries[i];
      mapping[i] = new ParameterMapping
          ((String) entry.getKey(), (String) entry.getValue());
    }
    return mapping;
  }

  public boolean isGlobalImport()
  {
    return "*".equals(inputParameters.get("*"));
  }

  public boolean isGlobalExport()
  {
    return "*".equals(exportParameters.get("*"));
  }


  public Object clone()
      throws CloneNotSupportedException
  {
    final SubReport report = (SubReport) super.clone();
    report.inputParameters = (HashMap) inputParameters.clone();
    report.exportParameters = (HashMap) exportParameters.clone();
    return report;
  }
}
