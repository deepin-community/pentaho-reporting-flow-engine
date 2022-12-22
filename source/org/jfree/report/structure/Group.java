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
 * $Id: Group.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.structure;

import org.jfree.report.expressions.Expression;

/**
 * A report group. A group is a repeated section which is bound to an
 * expression.
 * <p/>
 * <h2>Default Behaviour</h2> Whether a new group should be started is evaluated
 * by the group's expression. If that expression returns Boolean.TRUE, a new
 * group instance is started. (That expression answers the Questions: 'Does this
 * group instance end here?').
 * <p/>
 * If the group expression is invalid or there is no group expression at all, a
 * group will consume all rows until the datasource is no longer advanceable.
 *
 * @author David Gilbert
 * @author Thomas Morgner
 */
public class Group extends Section
{
  private Expression groupingExpression;

  /**
   * Constructs a group with no fields, and an empty header and footer.
   */
  public Group()
  {
    setType("group");
    setRepeat(true);
  }

  /**
   * Returns a string representation of the group (useful for debugging).
   *
   * @return A string.
   */
  public String toString()
  {
    final StringBuffer b = new StringBuffer();
    b.append("Group={Name='");
    b.append(getName());
    b.append("} ");
    return b.toString();
  }

  public Expression getGroupingExpression()
  {
    return groupingExpression;
  }

  public void setGroupingExpression(final Expression groupingExpression)
  {
    this.groupingExpression = groupingExpression;
  }

  public Group getGroup()
  {
    return this;
  }


  public Object clone()
      throws CloneNotSupportedException
  {
    final Group group = (Group) super.clone();
    if (groupingExpression != null)
    {
      group.groupingExpression = (Expression) groupingExpression.clone();
    }
    return group;
  }
}
