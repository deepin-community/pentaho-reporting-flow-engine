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
 * $Id: StaticReportDataFactoryTest.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.data.beans;

import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;

import junit.framework.TestCase;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.testcore.SimpleDataSet;

/**
 * Creation-Date: Jan 18, 2007, 6:08:26 PM
 *
 * @author Thomas Morgner
 */
public class StaticReportDataFactoryTest extends TestCase
{
  public StaticReportDataFactoryTest(String string)
  {
    super(string);
  }

  public void testDataFactory () throws ReportDataFactoryException
  {
    SimpleDataSet sdr = new SimpleDataSet();
    sdr.add("parameter1", "test");
    sdr.add("parameter2", new Integer(5));

    assertNotNull(sdr.get("parameter1"));
    assertNotNull(sdr.get("parameter2"));

    StaticReportDataFactory sfd = new StaticReportDataFactory();
    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTest#createSimpleTableModel", sdr));
    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTest#createSimpleTableModel()", sdr));
    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTest#createParametrizedTableModel(parameter2,parameter1)", sdr));

    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTestSupport", sdr));
    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTestSupport#createSimpleTableModel", sdr));
    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTestSupport#createSimpleTableModel()", sdr));
    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTestSupport#createParametrizedTableModel(parameter2,parameter1)", sdr));

    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTestSupport()", sdr));
    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTestSupport()#createSimpleTableModel", sdr));
    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTestSupport()#createSimpleTableModel()", sdr));
    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTestSupport()#createParametrizedTableModel(parameter2,parameter1)", sdr));

    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTestSupport(parameter1,parameter2)", sdr));
    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTestSupport(parameter1,parameter2)#createSimpleTableModel", sdr));
    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTestSupport(parameter1,parameter2)#createSimpleTableModel()", sdr));
    assertNotNull(sfd.queryData
        ("org.jfree.report.modules.data.beans.StaticReportDataFactoryTestSupport(parameter1,parameter2)#createParametrizedTableModel(parameter2,parameter1)", sdr));
  }

  public static TableModel createParametrizedTableModel (int i1, String s1)
  {
    assertEquals("Passing primitive parameters failed", 5, i1);
    assertEquals("Passing object parameters failed", "test", s1);
    return new DefaultTableModel();
  }

  public static TableModel createSimpleTableModel ()
  {
    return new DefaultTableModel();
  }

}
