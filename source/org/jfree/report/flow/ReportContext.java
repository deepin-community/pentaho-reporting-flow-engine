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
 * $Id: ReportContext.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow;

import org.jfree.report.flow.layoutprocessor.LayoutControllerFactory;
import org.jfree.report.i18n.ResourceBundleFactory;
import org.pentaho.reporting.libraries.formula.FormulaContext;

/**
 * THe global report context. This context acts as global structure that holds
 * all processing factories and allows to store global attributes. The
 * attribute collection is a global collection, all layout controller have
 * shared access to the same collection.
 *
 * Each report run (prepare, paginate, content-generate) uses its own context
 * implementation - attributes are not shared or preserved among the different
 * runs.
 *
 * @author Thomas Morgner
 */
public interface ReportContext
{
  public FormulaContext getFormulaContext();
  public LayoutControllerFactory getLayoutControllerFactory();
  public String getExportDescriptor();
  public ResourceBundleFactory getResourceBundleFactory();
  public ReportStructureRoot getReportStructureRoot();

  public void setAttribute (Object key, Object value);
  public Object getAttribute (Object key);
}
