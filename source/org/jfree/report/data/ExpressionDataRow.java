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
 * $Id: ExpressionDataRow.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.data;

import java.util.HashMap;

import org.jfree.report.DataFlags;
import org.jfree.report.DataRow;
import org.jfree.report.DataSourceException;
import org.jfree.report.flow.ReportContext;

/**
 * A datarow for all expressions encountered in the report. This datarow is a
 * stack-like structure, which allows easy adding and removing of expressions,
 * even if these expressions have been cloned and or otherwisely modified.
 *
 * @author Thomas Morgner
 */
public final class ExpressionDataRow implements DataRow
{
  private ExpressionSlot[] expressions;
  private int length;
  private HashMap nameCache;
  private GlobalMasterRow masterRow;
  private ReportContext reportContext;

  public ExpressionDataRow(final GlobalMasterRow masterRow,
                           final ReportContext reportContext,
                           int capacity)
  {
    this.masterRow = masterRow;
    this.nameCache = new HashMap(capacity);
    this.expressions = new ExpressionSlot[capacity];
    this.reportContext = reportContext;
  }

  private ExpressionDataRow(final GlobalMasterRow masterRow,
                            final ExpressionDataRow previousRow)
      throws CloneNotSupportedException
  {
    this.reportContext = previousRow.reportContext;
    this.masterRow = masterRow;
    this.nameCache = (HashMap) previousRow.nameCache.clone();
    this.expressions = new ExpressionSlot[previousRow.expressions.length];
    this.length = previousRow.length;
    for (int i = 0; i < length; i++)
    {
      final ExpressionSlot expression = previousRow.expressions[i];
      if (expression == null)
      {
//        Log.debug("Error: Expression is null...");
      }
      else
      {
        expressions[i] = (ExpressionSlot) expression.clone();
      }
    }
  }

  private void ensureCapacity(int requestedSize)
  {
    final int capacity = this.expressions.length;
    if (capacity > requestedSize)
    {
      return;
    }
    final int newSize = Math.max(capacity * 2, requestedSize + 10);

    final ExpressionSlot[] newExpressions = new ExpressionSlot[newSize];

    System.arraycopy(expressions, 0, newExpressions, 0, length);

    this.expressions = newExpressions;
  }

  /**
   * This adds the expression to the data-row and queries the expression for the
   * first time.
   *
   * @param ex
   * @param rd
   * @throws DataSourceException
   */
  public synchronized void pushExpression(final ExpressionSlot expressionSlot)
      throws DataSourceException
  {
    if (expressionSlot == null)
    {
      throw new NullPointerException();
    }

    ensureCapacity(length + 1);

    this.expressions[length] = expressionSlot;
    final String name = expressionSlot.getName();
    if (name != null)
    {
      nameCache.put(name, expressionSlot);
    }
    length += 1;

    expressionSlot.updateDataRow(masterRow.getGlobalView());
    // A manual advance to initialize the function.
    expressionSlot.advance();
    if (name != null)
    {
      final Object value = expressionSlot.getValue();
      final MasterDataRowChangeEvent chEvent = new MasterDataRowChangeEvent
          (MasterDataRowChangeEvent.COLUMN_ADDED, name, value);
      masterRow.dataRowChanged(chEvent);
    }
  }

  public synchronized void pushExpressions(final ExpressionSlot[] expressionSlots)
      throws DataSourceException
  {
    if (expressionSlots == null)
    {
      throw new NullPointerException();
    }

    ensureCapacity(length + expressionSlots.length);
    for (int i = 0; i < expressionSlots.length; i++)
    {
      ExpressionSlot expression = expressionSlots[i];
      if (expression == null)
      {
        continue;
      }
      pushExpression(expression);
    }
  }

  public synchronized void popExpressions(final int counter) throws
      DataSourceException
  {
    for (int i = 0; i < counter; i++)
    {
      popExpression();
    }
  }

  public synchronized void popExpression() throws DataSourceException
  {
    if (length == 0)
    {
      return;
    }
    String originalName = expressions[length - 1].getName();
    boolean preserve = expressions[length - 1].isPreserve();
    this.expressions[length - 1] = null;
    this.length -= 1;
    if (originalName != null)
    {
      int otherIndex = -1;
      for (int i = length - 1; i >= 0; i -= 1)
      {
        ExpressionSlot expression = expressions[i];
        if (originalName.equals(expression.getName()))
        {
          otherIndex = i;
          break;
        }
      }
      if (otherIndex == -1)
      {
        nameCache.remove(originalName);
      }
      else
      {
        nameCache.put(originalName, expressions[otherIndex]);
      }

      if (preserve == false)
      {
        final MasterDataRowChangeEvent chEvent = new MasterDataRowChangeEvent
            (MasterDataRowChangeEvent.COLUMN_REMOVED, originalName, null);
        masterRow.dataRowChanged(chEvent);
      }
      // for preserved elements we do not send an remove-event.
    }

  }

  /**
   * Returns the value of the expressions or column in the tablemodel using the
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
    return expressions[col].getValue();
  }

  /**
   * Returns the value of the function, expressions or column using its specific
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
    final ExpressionSlot es = (ExpressionSlot) nameCache.get(col);
    if (es == null)
    {
      return null;
    }

    return es.getValue();
  }

  /**
   * Returns the name of the column, expressions or function. For columns from
   * the tablemodel, the tablemodels <code>getColumnName</code> method is
   * called. For functions, expressions and report properties the assigned name
   * is returned.
   *
   * @param col the item index.
   * @return the name.
   */
  public String getColumnName(int col)
  {
    return expressions[col].getName();
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
    throw new UnsupportedOperationException();
  }

  public DataFlags getFlags(int col)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Advances to the next row and attaches the given master row to the objects
   * contained in that client data row.
   *
   * @param master
   * @param deepTraversing only advance expressions that have been marked as
   *                       deeply traversing
   * @return
   */
  public ExpressionDataRow advance(final GlobalMasterRow master,
                                   final boolean deepTraversing)
      throws DataSourceException
  {
    try
    {
      final ExpressionDataRow edr = new ExpressionDataRow(master, this);

      // It is defined, that new expressions get evaluated before any older
      // expression.
      for (int i = edr.length - 1; i >= 0; i--)
      {
        ExpressionSlot expressionSlot = edr.expressions[i];
        expressionSlot.updateDataRow(master.getGlobalView());
        if (deepTraversing == false ||
            (deepTraversing && expressionSlot.isDeepTraversing()))
        {
          expressionSlot.advance();
        }
        // Query the value (once per advance) ..
        final Object value = expressionSlot.getValue();
        final String name = expressionSlot.getName();
        if (name != null)
        {
          final MasterDataRowChangeEvent chEvent = new MasterDataRowChangeEvent
              (MasterDataRowChangeEvent.COLUMN_UPDATED, name, value);
          master.dataRowChanged(chEvent);
        }
      }
      return edr;
    }
    catch (CloneNotSupportedException e)
    {
      throw new DataSourceException("Cloning failed", e);
    }
  }

  public ExpressionDataRow derive(final GlobalMasterRow master)
      throws DataSourceException
  {
    try
    {
      return new ExpressionDataRow(master, this);
    }
    catch (CloneNotSupportedException e)
    {
      throw new DataSourceException("Cloning failed", e);
    }
  }

  public ExpressionSlot[] getSlots()
  {
    // to be totally safe from any tampering, we would have to do some sort of
    // deep-copy here.
    ExpressionSlot[] slots = new ExpressionSlot[length];
    System.arraycopy(expressions, 0, slots, 0, length);
    return slots;
  }
}
