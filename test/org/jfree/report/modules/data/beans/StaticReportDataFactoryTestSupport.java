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
 * $Id: StaticReportDataFactoryTestSupport.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.data.beans;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import junit.framework.TestCase;

/**
 * Creation-Date: Jan 18, 2007, 6:10:41 PM
 *
 * @author Thomas Morgner
 */
public class StaticReportDataFactoryTestSupport extends DefaultTableModel
{
  /**
   * Constructs a default <code>DefaultTableModel</code> which is a table of
   * zero columns and zero rows.
   */
  public StaticReportDataFactoryTestSupport()
  {
  }

  public StaticReportDataFactoryTestSupport(String parameter, int parameter2)
  {
    if ("test".equals(parameter) == false || parameter2 != 5)
    {
      throw new IllegalStateException();
    }
  }


  public TableModel createParametrizedTableModel (int i1, String s1)
  {
    TestCase.assertEquals("Passing primitive parameters failed", 5, i1);
    TestCase.assertEquals("Passing object parameters failed", "test", s1);
    return new DefaultTableModel();
  }

  public TableModel createSimpleTableModel ()
  {
    return new DefaultTableModel();
  }

}
