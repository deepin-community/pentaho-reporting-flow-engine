/**
 * =========================================================
 * Pentaho-Reporting-Classic : a free Java reporting library
 * =========================================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
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
 * LibraryPanel.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

package org.jfree.report.modules.gui.swing.preview.about;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.libraries.base.versioning.DependencyInformation;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * A panel containing a table that lists the libraries used in a project.
 * <p/>
 * Used in the AboutFrame class.
 *
 * @author David Gilbert
 */
public class LibraryPanel extends JPanel
{

  /**
   * The table.
   */
  private JTable table;

  /**
   * Constructs a LibraryPanel.
   *
   * @param libraries a list of libraries (represented by Library objects).
   */
  public LibraryPanel(final List libraries)
  {

    setLayout(new BorderLayout());

    final String[] names = new String[]{"Name", "Version", "License", "Info"};
    final DefaultTableModel model = new DefaultTableModel(names, libraries.size());
    for (int i = 0; i < libraries.size(); i++)
    {
      final DependencyInformation depInfo = (DependencyInformation) libraries.get(i);
      model.setValueAt(depInfo.getName(), i, 0);
      model.setValueAt(depInfo.getVersion(), i, 1);
      model.setValueAt(depInfo.getLicenseName(), i, 2);
      model.setValueAt(depInfo.getInfo(), i, 3);
    }

    this.table = new JTable(model);
    add(new JScrollPane(this.table));

  }

  public LibraryPanel(final ProjectInformation projectInfo)
  {
    this(LibraryPanel.getLibraries(projectInfo));
  }

  private static List getLibraries(final ProjectInformation info)
  {
    if (info == null)
    {
      return new ArrayList();
    }
    final ArrayList libs = new ArrayList();
    LibraryPanel.collectLibraries(info, libs);
    return libs;
  }

  private static void collectLibraries(final ProjectInformation info,
                                       final List list)
  {
    DependencyInformation[] libs = info.getLibraries();
    for (int i = 0; i < libs.length; i++)
    {
      final DependencyInformation lib = libs[i];
      if (list.contains(lib) == false)
      {
        // prevent duplicates, they look ugly ..
        list.add(lib);
        if (lib instanceof ProjectInformation)
        {
          LibraryPanel.collectLibraries((ProjectInformation) lib, list);
        }
      }
    }

    libs = info.getOptionalLibraries();
    for (int i = 0; i < libs.length; i++)
    {
      final DependencyInformation lib = libs[i];
      if (list.contains(lib) == false)
      {
        // prevent duplicates, they look ugly ..
        list.add(lib);
        if (lib instanceof ProjectInformation)
        {
          LibraryPanel.collectLibraries((ProjectInformation) lib, list);
        }
      }
    }
  }

  protected JTable getTable()
  {
    return table;
  }
}
