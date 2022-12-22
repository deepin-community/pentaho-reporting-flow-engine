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
 * SystemPropertiesPanel.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

package org.jfree.report.modules.gui.swing.preview.about;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * A panel containing a table of system properties.
 *
 * @author David Gilbert
 */
public class SystemPropertiesPanel extends JPanel
{

  /**
   * The table that displays the system properties.
   */
  private JTable table;

  /**
   * Allows for a popup menu for copying.
   */
  private JPopupMenu copyPopupMenu;

  /**
   * A copy menu item.
   */
  private JMenuItem copyMenuItem;

  /**
   * A popup listener.
   */
  private PopupListener copyPopupListener;

  /**
   * Constructs a new panel.
   */
  public SystemPropertiesPanel()
  {

    final String baseName = "org.jfree.ui.about.resources.AboutResources";
    final ResourceBundle resources = ResourceBundle.getBundle(baseName);

    setLayout(new BorderLayout());
    this.table = SystemPropertiesPanel.createSystemPropertiesTable();
    add(new JScrollPane(this.table));

    // Add a popup menu to copy to the clipboard...
    this.copyPopupMenu = new JPopupMenu();

    final String label = resources.getString("system-properties-panel.popup-menu.copy");
    final KeyStroke accelerator = (KeyStroke)
        resources.getObject("system-properties-panel.popup-menu.copy.accelerator");
    this.copyMenuItem = new JMenuItem(label);
    this.copyMenuItem.setAccelerator(accelerator);
    this.copyMenuItem.getAccessibleContext().setAccessibleDescription(label);
    this.copyMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        copySystemPropertiesToClipboard();
      }
    });
    this.copyPopupMenu.add(this.copyMenuItem);

    // add popup Listener to the table
    this.copyPopupListener = new PopupListener();
    this.table.addMouseListener(this.copyPopupListener);

  }

  /**
   * Creates and returns a JTable containing all the system properties.  This method returns a table that is configured
   * so that the user can sort the properties by clicking on the table header.
   *
   * @return a system properties table.
   */
  public static JTable createSystemPropertiesTable()
  {

    final String[] colnames = new String[]{"Property-Name", "Value"};
    final Properties sysProps = System.getProperties();

    final TreeMap data = new TreeMap(sysProps);
    final Map.Entry[] entries = (Map.Entry[]) data.entrySet().toArray(new Map.Entry[data.size()]);
    final DefaultTableModel properties = new DefaultTableModel(colnames, entries.length);
    for (int i = 0; i < entries.length; i++)
    {
      final Map.Entry entry = entries[i];
      properties.setValueAt(entry.getKey(), i, 0);
      properties.setValueAt(entry.getValue(), i, 1);
    }

    final JTable table = new JTable(properties);
    final TableColumnModel model = table.getColumnModel();
    TableColumn column = model.getColumn(0);
    column.setPreferredWidth(200);
    column = model.getColumn(1);
    column.setPreferredWidth(350);

    table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    return table;

  }

  /**
   * Copies the selected cells in the table to the clipboard, in tab-delimited format.
   */
  public void copySystemPropertiesToClipboard()
  {

    final StringBuffer buffer = new StringBuffer();
    final ListSelectionModel selection = this.table.getSelectionModel();
    final int firstRow = selection.getMinSelectionIndex();
    final int lastRow = selection.getMaxSelectionIndex();
    if ((firstRow != -1) && (lastRow != -1))
    {
      for (int r = firstRow; r <= lastRow; r++)
      {
        for (int c = 0; c < this.table.getColumnCount(); c++)
        {
          buffer.append(this.table.getValueAt(r, c));
          if (c != 2)
          {
            buffer.append('\t');
          }
        }
        buffer.append('\n');
      }
    }
    final StringSelection ss = new StringSelection(buffer.toString());
    final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
    cb.setContents(ss, ss);

  }


  /**
   * Returns the copy popup menu.
   *
   * @return Returns the copyPopupMenu.
   */
  protected final JPopupMenu getCopyPopupMenu()
  {
    return copyPopupMenu;
  }

  /**
   * Returns the table containing the system properties.
   *
   * @return Returns the table.
   */
  protected final JTable getTable()
  {
    return table;
  }

  /**
   * A popup listener.
   */
  private class PopupListener extends MouseAdapter
  {

    /**
     * Default constructor.
     */
    public PopupListener()
    {
    }

    /**
     * Mouse pressed event.
     *
     * @param e the event.
     */
    public void mousePressed(final MouseEvent e)
    {
      maybeShowPopup(e);
    }

    /**
     * Mouse released event.
     *
     * @param e the event.
     */
    public void mouseReleased(final MouseEvent e)
    {
      maybeShowPopup(e);
    }

    /**
     * Event handler.
     *
     * @param e the event.
     */
    private void maybeShowPopup(final MouseEvent e)
    {
      if (e.isPopupTrigger())
      {
        getCopyPopupMenu().show(getTable(), e.getX(), e.getY());
      }
    }
  }


}
