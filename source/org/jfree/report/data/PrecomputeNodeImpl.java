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
 * $Id: PrecomputeNodeImpl.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.data;

import java.util.ArrayList;

/**
 * A precompute-node represents a resolved element or section of the report definition. Unlike the structural nodes,
 * these nodes can always have childs.
 * <p/>
 * The resulting tree gets pruned as early as possible - nodes which do not contain precomputed or preserved expressions
 * will not be stored.
 *
 * @author Thomas Morgner
 */
public class PrecomputeNodeImpl implements PrecomputeNode
{
  private PrecomputeNodeImpl parent;
  private PrecomputeNodeImpl next;
  private PrecomputeNodeImpl firstChild;
  private PrecomputeNodeImpl lastChild;

  private PrecomputeNodeKey key;
  private ArrayList functionResults;
  private ArrayList functionNames;

  public PrecomputeNodeImpl(PrecomputeNodeKey key)
  {
    if (key == null)
    {
      throw new NullPointerException();
    }
    this.key = key;
  }

  public PrecomputeNodeKey getKey()
  {
    return key;
  }

  public PrecomputeNode getParent()
  {
    return parent;
  }

  protected void setParent(final PrecomputeNodeImpl parent)
  {
    this.parent = parent;
  }

  public PrecomputeNode getNext()
  {
    return next;
  }

  protected void setNext(final PrecomputeNodeImpl next)
  {
    this.next = next;
  }

  public PrecomputeNode getFirstChild()
  {
    return firstChild;
  }

  protected void setFirstChild(final PrecomputeNodeImpl firstChild)
  {
    this.firstChild = firstChild;
  }

  public PrecomputeNode getLastChild()
  {
    return lastChild;
  }

  protected void setLastChild(final PrecomputeNodeImpl lastChild)
  {
    this.lastChild = lastChild;
  }

  public void add(PrecomputeNodeImpl node)
  {
    if (firstChild == null)
    {
      firstChild = node;
      firstChild.setParent(this);
      lastChild = node;
      return;
    }

    lastChild.setNext(node);
    lastChild.setParent(this);
  }

  public void addFunction(final String name, final Object value)
  {
    if (this.functionNames == null)
    {
      functionNames = new ArrayList();
      functionResults = new ArrayList();
    }

    this.functionNames.add(name);
    this.functionResults.add(value);
  }

  public int getFunctionCount()
  {
    if (functionNames == null)
    {
      return 0;
    }
    return functionNames.size();
  }

  public String getFunctionName(int idx)
  {
    if (functionNames == null)
    {
      throw new IndexOutOfBoundsException();
    }
    return (String) functionNames.get(idx);
  }

  public Object getFunctionResult(int idx)
  {
    if (functionResults == null)
    {
      throw new IndexOutOfBoundsException();
    }
    return functionResults.get(idx);
  }

  public void prune()
  {
    if (parent == null)
    {
      return;
    }

    if (parent.getLastChild() != this)
    {
      throw new IllegalStateException("Cannot prune. Not the last child.");
    }
    if (parent.getFirstChild() == this)
    {
      parent.setFirstChild(null);
    }
    parent.setLastChild(null);
  }
}
