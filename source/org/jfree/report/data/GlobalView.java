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
 * $Id: GlobalView.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.data;

import org.jfree.report.DataFlags;
import org.jfree.report.DataRow;
import org.jfree.report.DataSourceException;
import org.jfree.report.util.LazyNameMap;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * The global view holds all *named* data columns. Expressions which have no
 * name will not appear here. There is a slot for each name - if expressions
 * share the same name, the last name wins.
 * <p/>
 * This acts as some kind of global variables heap - which allows named
 * functions to export their values to a global space.
 * <p/>
 * This datarow is optimized for named access - the sequential access is only
 * generated when absolutly needed.
 *
 * @author Thomas Morgner
 */
public final class GlobalView implements DataRow
{
  private DataFlags[] oldData;
  private LazyNameMap oldCache;
  private DataFlags[] data;
  private LazyNameMap nameCache;
  private int length;

  private GlobalView()
  {
  }

  public static GlobalView createView()
  {
    GlobalView gv = new GlobalView();
    gv.nameCache = new LazyNameMap();
    gv.oldCache = new LazyNameMap();
    gv.data = new DataFlags[10];
    gv.oldData = new DataFlags[0];
    return gv;
  }


  private void ensureCapacity(int requestedSize)
  {
    final int capacity = this.data.length;
    if (capacity > requestedSize)
    {
      return;
    }
    final int newSize = Math.max(capacity * 2, requestedSize + 10);

    final DataFlags[] newData = new DataFlags[newSize];
    System.arraycopy(data, 0, newData, 0, length);

    this.data = newData;
  }

  /**
   * This adds the expression to the data-row and queries the expression for the
   * first time.
   *
   * @param name  the name of the field (cannot be null)
   * @param value the value of that field (may be null)
   * @throws DataSourceException
   */
  public synchronized void putField(final String name,
                                    final Object value,
                                    final boolean update)
      throws DataSourceException
  {
    if (name == null)
    {
      throw new NullPointerException("Name must not be null.");
    }

    final LazyNameMap.NameCarrier nc = nameCache.get(name);
    final DefaultDataFlags flagedValue = new DefaultDataFlags
        (name, value, computeChange(name, value));
    if (nc != null)
    {
      this.data[nc.getValue()] = flagedValue;
      if (update == false)
      {
        nc.increase();
      }
      return;
    }

    // oh fine, a new one ...
    // step 1: Search for a free slot
    for (int i = 0; i < length; i++)
    {
      DataFlags dataFlags = data[i];
      if (dataFlags == null)
      {
        data[i] = flagedValue;
        nameCache.setValue(name, i);
        return;
      }
    }

    // step 2: No Free Slot, so add
    ensureCapacity(length + 1);
    data[length] = flagedValue;
    nameCache.setValue(name, length);
    this.length += 1;
  }

  private boolean computeChange(String name, Object newValue)
      throws DataSourceException
  {
    final LazyNameMap.NameCarrier onc = oldCache.get(name);
    if (onc == null)
    {
      // A new data item, not known before ...
      return true;
    }

    final DataFlags dataFlags = oldData[onc.getValue()];
    if (dataFlags == null)
    {
      return true;
    }
    return ObjectUtilities.equal(dataFlags.getValue(), newValue) == false;
  }

  /**
   * Returns the value of the expression or column in the tablemodel using the
   * given column number as index. For functions and expressions, the
   * <code>getValue()</code> method is called and for columns from the
   * tablemodel the tablemodel method <code>getValueAt(row, column)</code> gets
   * called.
   *
   * @param col the item index.
   * @return the value.
   * @throws IllegalStateException if the datarow detected a deadlock.
   */
  public Object get(int col) throws DataSourceException
  {
    final DataFlags flag = getFlags(col);
    if (flag == null)
    {
      return null;
    }
    return flag.getValue();
  }

  /**
   * Returns the value of the function, expression or column using its specific
   * name. The given name is translated into a valid column number and the the
   * column is queried. For functions and expressions, the
   * <code>getValue()</code> method is called and for columns from the
   * tablemodel the tablemodel method <code>getValueAt(row, column)</code> gets
   * called.
   *
   * @param col the item index.
   * @return the value.
   * @throws IllegalStateException if the datarow detected a deadlock.
   */
  public Object get(String col) throws DataSourceException
  {
    final DataFlags flag = getFlags(col);
    if (flag == null)
    {
      return null;
    }
    return flag.getValue();
  }

  /**
   * Returns the name of the column, expression or function. For columns from
   * the tablemodel, the tablemodels <code>getColumnName</code> method is
   * called. For functions, expressions and report properties the assigned name
   * is returned.
   *
   * @param col the item index.
   * @return the name.
   */
  public String getColumnName(int col)
  {
    final DataFlags flag = getFlags(col);
    if (flag == null)
    {
      return null;
    }
    return flag.getName();
  }

  /**
   * Returns the number of columns, expressions and functions and marked
   * ReportProperties in the report.
   *
   * @return the item count.
   */
  public int getColumnCount()
  {
    return length;
  }

  public DataFlags getFlags(String col)
  {
    final LazyNameMap.NameCarrier idx = nameCache.get(col);
    if (idx != null)
    {
      final int idxVal = idx.getValue();
      final DataFlags df = data[idxVal];
      if (df != null)
      {
        return df;
      }
    }

    final LazyNameMap.NameCarrier oidx = oldCache.get(col);
    if (oidx == null)
    {
      return null;
    }

    final int oidxVal = oidx.getValue();
    if (oidxVal < oldData.length)
    {
      return oldData[oidxVal];
    }
    return null;
  }

  public DataFlags getFlags(int col)
  {
    final DataFlags df = data[col];
    if (df != null)
    {
      return df;
    }
    return oldData[col];
  }

  public GlobalView derive()
  {
    GlobalView gv = new GlobalView();
    gv.oldCache = (LazyNameMap) oldCache.clone();
    gv.data = (DataFlags[]) data.clone();
    gv.oldData = (DataFlags[]) oldData.clone();
    gv.length = length;
    gv.nameCache = (LazyNameMap) nameCache.clone();
    return gv;
  }

  public GlobalView advance()
  {
    GlobalView gv = new GlobalView();
    gv.oldCache = (LazyNameMap) nameCache.clone();
    gv.oldData = (DataFlags[]) data.clone();
    gv.data = new DataFlags[gv.oldData.length];
    gv.length = length;
    gv.nameCache = new LazyNameMap();
    return gv;
  }

  /**
   * Note: Dont remove the column. It will stay around here as long as the
   * process lives.
   *
   * @param name
   */
  public synchronized void removeColumn(String name)
  {
    final LazyNameMap.NameCarrier idx = nameCache.get(name);
    if (idx == null)
    {
      return;
    }
    idx.decrease();
    if (idx.getInstanceCount() < 1)
    {
      nameCache.remove(name);
      data[idx.getValue()] = null;

      // todo: In a sane world, we would now start to reindex the whole thing.
    }
  }

}
