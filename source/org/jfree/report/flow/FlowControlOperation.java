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
 * $Id: FlowControlOperation.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.flow;

/**
 * These objects define, how the iteration over the report definition affects
 * the data source.
 *
 * @author Thomas Morgner
 */
public class FlowControlOperation
{
  /**
   * Stores the current datarow state for a later recall. Markpoints from different
   * sources can be nested. Marking does not change the user datasource.
   */
  public static final FlowControlOperation MARK =
          new FlowControlOperation("mark");
  /**
   * Requests that the datasource should be moved to the next row. An advance
   * operation does not change the current cursor position. The cursor is not
   * moved until a 'COMMIT' operation has been reached.
   *
   * Repeatable sections will perform an auto-commit based on the group in which
   * they are in.
   */
  public static final FlowControlOperation ADVANCE =
          new FlowControlOperation("advance");
  /** Recalls a marked position. */
  public static final FlowControlOperation RECALL =
          new FlowControlOperation("recall");

  /** Do nothing. */
  public static final FlowControlOperation NO_OP =
          new FlowControlOperation("no-op");

  /**
   * Finishes (and closes) the currently open context. If the last mark has been
   * closed, the datasource is also closed.
   * <p/>
   * If all datasources have been closes, the empty datasource is used. This
   * datasource cannot be closed (closing has no effect on it).
   */
  public static final FlowControlOperation DONE =
          new FlowControlOperation("done");


  private final String myName; // for debug only

  /**
   * A commit checks for an pending advance request and commites that request
   * by moving the cursor of the currend datarow forward by one row.
   */
  public static final FlowControlOperation COMMIT =
          new FlowControlOperation("commit");

  protected FlowControlOperation(String name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    myName = name;
  }

  public String toString()
  {
    return myName;
  }

  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    final FlowControlOperation that = (FlowControlOperation) o;

    return myName.equals(that.myName);
  }

  public int hashCode()
  {
    return myName.hashCode();
  }
}
