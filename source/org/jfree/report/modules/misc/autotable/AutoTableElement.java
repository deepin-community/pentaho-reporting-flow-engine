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
 * $Id: AutoTableElement.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.misc.autotable;

import java.util.ArrayList;

import org.jfree.report.structure.Element;
import org.jfree.report.structure.Section;

/**
 * Creation-Date: Dec 9, 2006, 5:46:48 PM
 *
 * @author Thomas Morgner
 */
public class AutoTableElement extends Element
{
  private ArrayList headerCells;
  private ArrayList contentCells;
  private ArrayList footerCells;

  public AutoTableElement()
  {
    headerCells = new ArrayList();
    footerCells = new ArrayList();
    contentCells = new ArrayList();
  }

  public void addHeader (Section headerCellElement)
  {
    headerCellElement.updateParent(this);
    headerCells.add(headerCellElement);
  }

  public void addFooter (Section footerCellElement)
  {
    footerCellElement.updateParent(this);
    footerCells.add(footerCellElement);
  }

  public void addContent (Section cellElement)
  {
    cellElement.updateParent(this);
    contentCells.add(cellElement);
  }

  public int getHeaderCount ()
  {
    return headerCells.size();
  }

  public int getFooterCount ()
  {
    return footerCells.size();
  }

  public int getContentCount ()
  {
    return contentCells.size();
  }

  public Section getHeaderCell (int index)
  {
    return (Section) headerCells.get(index);
  }

  public Section getFooterCell (int index)
  {
    return (Section) footerCells.get(index);
  }

  public Section getContentCell (int index)
  {
    return (Section) contentCells.get(index);
  }
}
