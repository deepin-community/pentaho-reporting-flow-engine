/**
 * ===========================================
 * JFreeReport : a free Java reporting library
 * ===========================================
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
 * FastGlobalView.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */
package org.jfree.report.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;

import org.jfree.report.DataRow;
import org.jfree.report.JFreeReportBoot;
import org.jfree.report.DataFlags;
import org.jfree.report.DataSourceException;
import org.jfree.report.util.IntegerCache;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 10.08.2007, 14:07:32
 *
 * @author Thomas Morgner
 */
public final class FastGlobalView implements DataRow
{

    private static final boolean DEBUG = false;
    private HashSet duplicateColumns;
    private HashSet invalidColumns;
    private boolean modifiableNameCache;
    private HashMap nameCache;
    private String[] columnNames;
    private DataFlags[] columnValue;
    private DataFlags[] columnOldValue;
    private boolean[] columnChanged;
    private int[] columnPrev;
    private int length;
    private boolean warnInvalidColumns;

    public FastGlobalView(final FastGlobalView parent)
    {
        if (parent.modifiableNameCache)
        {
            this.duplicateColumns = (HashSet) parent.duplicateColumns.clone();
            this.nameCache = (HashMap) parent.nameCache.clone();
            this.modifiableNameCache = false;
            this.columnNames = (String[]) parent.columnNames.clone();
        }
        else
        {
            this.duplicateColumns = parent.duplicateColumns;
            this.nameCache = parent.nameCache;
            this.columnNames = parent.columnNames;
            this.modifiableNameCache = false;
        }
        this.columnChanged = parent.columnChanged.clone();
        this.columnValue = parent.columnValue.clone();
        this.columnOldValue = parent.columnOldValue.clone();
        this.columnPrev = parent.columnPrev.clone();
        this.length = parent.length;
        this.warnInvalidColumns = parent.warnInvalidColumns;
        this.invalidColumns = parent.invalidColumns;
    }

    public FastGlobalView()
    {
        this.warnInvalidColumns = "true".equals(JFreeReportBoot.getInstance().getGlobalConfig().getConfigProperty("org.jfree.report.WarnInvalidColumns"));
        if (warnInvalidColumns)
        {
            this.invalidColumns = new HashSet();
        }

        this.duplicateColumns = new HashSet();
        this.nameCache = new HashMap();
        this.modifiableNameCache = true;
        this.columnChanged = new boolean[20];
        this.columnNames = new String[20];
        this.columnValue = new DataFlags[20];
        this.columnOldValue = new DataFlags[20];
        this.columnPrev = new int[20];
    }

    public Object get(final int col) throws DataSourceException
    {
        final DataFlags flag = getFlags(col);
        if (flag == null)
        {
            return null;
        }
        return flag.getValue();
    }

    public Object get(final String col) throws DataSourceException
    {
        final DataFlags flag = getFlags(col);
        if (flag == null)
        {
            return null;
        }
        return flag.getValue();
    }

    public String getColumnName(final int col)
    {
        if (col < 0 || col >= length)
        {
            throw new IndexOutOfBoundsException("Column-Index " + col + " is invalid.");
        }
        return columnNames[col];
    }

    public int findColumn(final String name)
    {
        final Integer o = (Integer) nameCache.get(name);
        if (o == null)
        {
            return -1;
        }
        return o.intValue();
    }

    public int getColumnCount()
    {
        return length;
    }

    public FastGlobalView derive()
    {
        return new FastGlobalView(this);
    }

    public FastGlobalView advance()
    {
        final FastGlobalView advanced = new FastGlobalView(this);
        System.arraycopy(advanced.columnValue, 0, advanced.columnOldValue, 0, length);
        Arrays.fill(advanced.columnChanged, false);
        return advanced;
    }

    public void removeColumn(final String name)
    {
        final boolean needToRebuildCache;
        int idx = -1;
        if (duplicateColumns.contains(name))
        {
            needToRebuildCache = true;
            // linear index search from the end ..
            for (int i = length - 1; i >= 0; i -= 1)
            {
                if (ObjectUtilities.equal(name, columnNames[i]))
                {
                    idx = i;
                    break;
                }
            }
            if (idx < 0)
            {
                return;
            }
        }
        else
        {
            needToRebuildCache = false;
            final Integer o = (Integer) nameCache.get(name);
            if (o == null)
            {
                return;
            }
            idx = o.intValue();
        }

        if (DEBUG)
        {
            DebugLog.log("Removing column " + name + " (Length: " + length + " NameCache: " +
                    nameCache.size() + ", Idx: " + idx);
        }

        if (modifiableNameCache == false)
        {
            this.duplicateColumns = (HashSet) duplicateColumns.clone();
            this.columnNames = (String[]) columnNames.clone();
            this.nameCache = (HashMap) nameCache.clone();
            this.modifiableNameCache = true;
        }

        if (idx == (length - 1))
        {
            columnChanged[idx] = false;
            columnNames[idx] = null;
            columnValue[idx] = null;
            if (columnPrev[idx] == -1)
            {
                nameCache.remove(name);
            }
            else
            {
                nameCache.put(name, IntegerCache.getInteger(columnPrev[idx]));
            }
            // thats the easy case ..
            length -= 1;
            if (needToRebuildCache)
            {
                if (columnPrev[idx] == -1)
                {
                    DebugLog.log("Column marked as duplicate but no duplicate index recorded: " + name);
                }
                else
                {
                    if (columnPrev[columnPrev[idx]] == -1)
                    {
                        duplicateColumns.remove(name);
                    }
                }
            }
            return;
        }

        if (DEBUG)
        {
            DebugLog.log("Out of order removeal of a column: " + name);
        }

        if (columnPrev[idx] == -1)
        {
            nameCache.remove(name);
        }
        else
        {
            nameCache.put(name, IntegerCache.getInteger(columnPrev[idx]));
        }

        final int moveStartIdx = idx + 1;
        final int moveLength = length - moveStartIdx;
        System.arraycopy(columnNames, moveStartIdx, columnNames, idx, moveLength);
        System.arraycopy(columnChanged, moveStartIdx, columnChanged, idx, moveLength);
        System.arraycopy(columnOldValue, moveStartIdx, columnOldValue, idx, moveLength);
        System.arraycopy(columnValue, moveStartIdx, columnValue, idx, moveLength);
        System.arraycopy(columnPrev, moveStartIdx, columnPrev, idx, moveLength);
        columnNames[length - 1] = null;
        columnChanged[length - 1] = false;
        columnOldValue[length - 1] = null;
        columnPrev[length - 1] = 0;

        // Now it gets expensive: Rebuild the namecache ..
        final int newLength = moveLength + idx;
        nameCache.clear();
        duplicateColumns.clear();
        for (int i = 0; i < newLength; i++)
        {
            final String columnName = columnNames[i];
            final Integer oldVal = (Integer) nameCache.get(columnName);
            if (nameCache.containsKey(columnName))
            {
                duplicateColumns.add(columnName);
            }

            nameCache.put(columnName, IntegerCache.getInteger(i));
            if (oldVal != null)
            {
                columnPrev[i] = oldVal.intValue();
            }
            else
            {
                columnPrev[i] = -1;
            }
        }
        length -= 1;
        if (DEBUG)
        {
            DebugLog.log("New Namecache: " + nameCache);
        }
    }

    public void putField(final String name,
            final Object value,
            final boolean update)
    {
        if (DEBUG)
        {
            if (update)
            {
                DebugLog.log("  +   : " + name);
            }
            else
            {
                DebugLog.log("Adding: " + name);
            }
        }

        if (update == false)
        {
            if (modifiableNameCache == false)
            {
                this.columnNames = (String[]) columnNames.clone();
                this.nameCache = (HashMap) nameCache.clone();
                this.modifiableNameCache = true;
            }

            final DefaultDataFlags flagedValue = new DefaultDataFlags(name, value, true);

            // A new column ...
            ensureCapacity(length + 1);
            columnNames[length] = name;
            columnValue[length] = flagedValue;
            final Integer o = (Integer) nameCache.get(name);
            if (o == null)
            {
                columnPrev[length] = -1;
            }
            else
            {
                columnPrev[length] = o.intValue();
                duplicateColumns.add(name);
            }
            columnChanged[length] = true;
            columnOldValue[length] = null;
            nameCache.put(name, IntegerCache.getInteger(length));
            length += 1;
        }
        else
        {
            try
            {
                // Updating an existing column ...
                final Integer o = (Integer) nameCache.get(name);
                if (o == null)
                {
                    throw new IllegalStateException("Update to a non-existing column: " + name);
                }
                int idx = -1;
                if (duplicateColumns.contains(name))
                {
                    for (int i = 0; i < length; i += 1)
                    {
                        if (columnChanged[i] == false && ObjectUtilities.equal(name, columnNames[i]))
                        {
                            idx = i;
                            break;
                        }
                    }
                    if (idx < 0)
                    {
                        idx = o.intValue();
                    }
                }
                else
                {
                    idx = o.intValue();
                }
                final boolean changed = !ObjectUtilities.equal(value, columnValue[idx].getValue());
                final DefaultDataFlags flagedValue = new DefaultDataFlags(name, value, changed);
                columnNames[idx] = name;
                columnOldValue[idx] = columnValue[idx];
                columnValue[idx] = flagedValue;
                columnChanged[idx] = changed;
            }
            catch (DataSourceException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private void ensureCapacity(final int requestedSize)
    {
        final int capacity = this.columnNames.length;
        if (capacity > requestedSize)
        {
            return;
        }
        final int newSize = Math.max(capacity << 1, requestedSize + 10);

        final String[] newColumnNames = new String[newSize];
        System.arraycopy(columnNames, 0, newColumnNames, 0, length);
        this.columnNames = newColumnNames;

        final boolean[] newColumnChanged = new boolean[newSize];
        System.arraycopy(columnChanged, 0, newColumnChanged, 0, length);
        this.columnChanged = newColumnChanged;

        final int[] newColumnPrev = new int[newSize];
        System.arraycopy(columnPrev, 0, newColumnPrev, 0, length);
        this.columnPrev = newColumnPrev;

        final DataFlags[] newColumnValue = new DataFlags[newSize];
        System.arraycopy(columnValue, 0, newColumnValue, 0, length);
        this.columnValue = newColumnValue;

        final DataFlags[] newOldColumnValue = new DataFlags[newSize];
        System.arraycopy(columnOldValue, 0, newOldColumnValue, 0, length);
        this.columnOldValue = newOldColumnValue;
    }

    public DataFlags getFlags(final String col) throws DataSourceException
    {
        final int idx = findColumn(col);
        if (idx == -1)
        {
            return null;
        }
        return getFlags(idx);
    }

    public DataFlags getFlags(final int col) throws DataSourceException
    {
        if (col < 0 || col >= length)
        {
            throw new IndexOutOfBoundsException("Column-Index " + col + " is invalid.");
        }

        if (columnChanged[col] != false)
        {
            return columnValue[col];
        }

        final String columnName = columnNames[col];
        if (duplicateColumns.contains(columnName))
        {
            for (int i = col - 1; i >= 0; i--)
            {
                if (columnNames[i].equals(columnName) && columnChanged[i] == true)
                {
                    return columnValue[i];
                }
            }
        }

        return columnValue[col];
    }
}
