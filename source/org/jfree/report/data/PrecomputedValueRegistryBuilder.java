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
 * $Id: PrecomputedValueRegistryBuilder.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.data;

/**
 * This class is currently very primitive and enforces a recomputation of
 * precomputed values each time.
 *
 * @author Thomas Morgner
 */
public class PrecomputedValueRegistryBuilder implements PrecomputedValueRegistry
{
  private PrecomputeNodeImpl node;

  public PrecomputedValueRegistryBuilder()
  {
  }

  public void startElementPrecomputation(final PrecomputeNodeKey element)
  {
    startElement(element);
  }

  public void finishElementPrecomputation(final PrecomputeNodeKey element)
  {
    finishElement(element);
  }

  public void startElement(PrecomputeNodeKey element)
  {
    PrecomputeNodeImpl newNode = new PrecomputeNodeImpl (element);
    if (node != null)
    {
      newNode.setParent(node);
    }
    node = newNode;
  }

  public void finishElement(PrecomputeNodeKey element)
  {
    node = (PrecomputeNodeImpl) node.getParent();
  }

  public PrecomputeNode currentNode()
  {
    return node;
  }

  public void addFunction(final String name, final Object value)
  {
    node.addFunction(name, value);
  }
}
