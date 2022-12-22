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
 * $Id: Section.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.report.flow.FlowControlOperation;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A report section is a collection of other elements and sections.
 * <p/>
 * This implementation is not synchronized, to take care that you externally
 * synchronize it when using multiple threads to modify instances of this
 * class.
 * <p/>
 * Trying to add a parent of an band as child to the band, will result in an
 * exception.
 * <p/>
 * The attribute and style expressions added to the element are considered
 * unnamed and stateless. To define a named, statefull state expression, one
 * would create an ordinary named expression or function and would then
 * reference that expression from within a style or attribute expression.
 *
 * @author Thomas Morgner
 */
public class Section extends Element
{
  /**
   * An empty array to prevent object creation.
   */
  private static final Node[] EMPTY_ARRAY = new Node[0];
  private static final FlowControlOperation[] EMPTY_FLOWCONTROL = new FlowControlOperation[0];
  /**
   * All the elements for this band, stored by name.
   */
  private ArrayList allElements;

  /**
   * Cached elements.
   */
  private transient Node[] allElementsCached;

  private ArrayList operationsBefore;
  private ArrayList operationsAfter;
  private transient FlowControlOperation[] operationsBeforeCached;
  private transient FlowControlOperation[] operationsAfterCached;
  private boolean repeat;

  /**
   * Constructs a new band (initially empty).
   */
  public Section()
  {
    setType("section");
    allElements = new ArrayList();

  }

  /**
   * Adds a report element to the band.
   *
   * @param element the element that should be added
   * @throws NullPointerException     if the given element is null
   * @throws IllegalArgumentException if the position is invalid, either
   *                                  negative or greater than the number of
   *                                  elements in this band or if the given
   *                                  element is a parent of this element.
   */
  public void addNode(final Node element)
  {
    addNode(allElements.size(), element);
  }

  /**
   * Adds a report element to the band. The element will be inserted at the
   * specified position.
   *
   * @param position the position where to insert the element
   * @param element  the element that should be added
   * @throws NullPointerException     if the given element is null
   * @throws IllegalArgumentException if the position is invalid, either
   *                                  negative or greater than the number of
   *                                  elements in this band or if the given
   *                                  element is a parent of this element.
   */
  public void addNode(final int position, final Node element)
  {
    if (position < 0)
    {
      throw new IllegalArgumentException("Position < 0");
    }
    if (position > allElements.size())
    {
      throw new IllegalArgumentException("Position < 0");
    }
    if (element == null)
    {
      throw new NullPointerException("Band.addElement(...): element is null.");
    }

    // check for component loops ...
    if (element instanceof Section)
    {
      Node band = this;
      while (band != null)
      {
        if (band == element)
        {
          throw new IllegalArgumentException(
              "adding container's parent to itself");
        }
        band = band.getParent();
      }
    }

    // remove the element from its old parent ..
    // this is the default AWT behaviour when adding Components to Container
    final Node parent = element.getParent();
    if (parent != null)
    {
      if (parent == this)
      {
        // already a child, wont add twice ...
        return;
      }

      if (parent instanceof Section)
      {
        final Section section = (Section) parent;
        section.removeNode(element);
      }
      else
      {
        element.setParent(null);
      }
    }

    // add the element, update the childs Parent and the childs stylesheet.
    allElements.add(position, element);
    allElementsCached = null;

    // then add the parents, or the band's parent will be unregistered ..
    element.setParent(this);
  }

  /**
   * Adds a collection of elements to the band.
   *
   * @param elements the element collection.
   * @throws NullPointerException     if one of the given elements is null
   * @throws IllegalArgumentException if one of the given element is a parent of
   *                                  this element.
   */
  public void addNodes(final Collection elements)
  {
    if (elements == null)
    {
      throw new NullPointerException(
          "Band.addElements(...): collection is null.");
    }

    final Iterator iterator = elements.iterator();
    while (iterator.hasNext())
    {
      final Element element = (Element) iterator.next();
      addNode(element);
    }
  }

  /**
   * Returns the first element in the list that is known by the given name.
   *
   * @param name the element name.
   * @return the first element with the specified name, or <code>null</code> if
   *         there is no such element.
   *
   * @throws NullPointerException if the given name is null.
   */
  public Element getElementByName(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException("Band.getElement(...): name is null.");
    }

    final Node[] elements = getNodeArray();
    final int elementsSize = elements.length;
    for (int i = 0; i < elementsSize; i++)
    {
      final Node e = elements[i];
      if (e instanceof Element == false)
      {
        continue;
      }
      final Element element = (Element) e;
      final String elementName = element.getName();
      if (elementName != null)
      {
        if (elementName.equals(name))
        {
          return element;
        }
      }
    }
    return null;
  }

  /**
   * Removes an element from the band.
   *
   * @param e the element to be removed.
   * @throws NullPointerException if the given element is null.
   */
  public void removeNode(final Node e)
  {
    if (e == null)
    {
      throw new NullPointerException();
    }
    if (e.getParent() != this)
    {
      // this is none of my childs, ignore the request ...
      return;
    }

    e.setParent(null);
    allElements.remove(e);
    allElementsCached = null;
  }

  /**
   * Returns all child-elements of this band as immutable list.
   *
   * @return an immutable list of all registered elements for this band.
   *
   * @deprecated use <code>getElementArray()</code> instead.
   */
  public List getNodes()
  {
    return Collections.unmodifiableList(allElements);
  }

  /**
   * Returns the number of elements in this band.
   *
   * @return the number of elements of this band.
   */
  public int getNodeCount()
  {
    return allElements.size();
  }

  /**
   * Returns an array of the elements in the band. If the band is empty, an
   * empty array is returned.
   * <p/>
   * For performance reasons, a shared cached instance is returned. Do not
   * modify the returned array or live with the consquences.
   *
   * @return the elements.
   */
  public Node[] getNodeArray()
  {
    if (allElementsCached == null)
    {
      if (allElements.isEmpty())
      {
        allElementsCached = Section.EMPTY_ARRAY;
      }
      else
      {
        Node[] elements = new Node[allElements.size()];
        elements = (Node[]) allElements.toArray(elements);
        allElementsCached = elements;
      }
    }
    return allElementsCached;
  }

  /**
   * Returns the element stored add the given index.
   *
   * @param index the element position within this band
   * @return the element
   *
   * @throws IndexOutOfBoundsException if the index is invalid.
   */
  public Node getNode(final int index)
  {
    if (allElementsCached == null)
    {
      if (allElements.isEmpty())
      {
        allElementsCached = Section.EMPTY_ARRAY;
      }
      else
      {
        Node[] elements = new Node[allElements.size()];
        elements = (Node[]) allElements.toArray(elements);
        allElementsCached = elements;
      }
    }
    return allElementsCached[index];
  }

  /**
   * Returns a string representation of the band and all the elements it
   * contains, useful mainly for debugging purposes.
   *
   * @return a string representation of this band.
   */
  public String toString()
  {
    final StringBuffer b = new StringBuffer();
    b.append(this.getClass().getName());
    b.append("={name=\"");
    b.append(getName());
    b.append("\", namespace=\"");
    b.append(getNamespace());
    b.append("\", type=\"");
    b.append(getType());
    b.append("\", size=\"");
    b.append(allElements.size());
    b.append("\"}");
    return b.toString();
  }

  public FlowControlOperation[] getOperationBefore()
  {
    if (operationsBefore == null)
    {
      return Section.EMPTY_FLOWCONTROL;
    }
    if (operationsBeforeCached == null)
    {
      operationsBeforeCached = (FlowControlOperation[])
          operationsBefore.toArray(Section.EMPTY_FLOWCONTROL);
    }
    return operationsBeforeCached;
  }

  public FlowControlOperation[] getOperationAfter()
  {
    if (operationsAfter == null)
    {
      return Section.EMPTY_FLOWCONTROL;
    }
    if (operationsAfterCached == null)
    {
      operationsAfterCached = (FlowControlOperation[])
          operationsAfter.toArray(Section.EMPTY_FLOWCONTROL);
    }
    return operationsAfterCached;
  }

  public void setOperationBefore(final FlowControlOperation[] before)
  {
    if (operationsBefore == null)
    {
      operationsBefore = new ArrayList(before.length);
    }
    else
    {
      operationsBefore.clear();
      operationsBefore.ensureCapacity(before.length);
    }
    for (int i = 0; i < before.length; i++)
    {
      operationsBefore.add(before[i]);
    }

    operationsBeforeCached =
        (FlowControlOperation[]) before.clone();
  }

  public void setOperationAfter(final FlowControlOperation[] ops)
  {
    if (operationsAfter == null)
    {
      operationsAfter = new ArrayList(ops.length);
    }
    else
    {
      operationsAfter.clear();
      operationsAfter.ensureCapacity(ops.length);
    }
    for (int i = 0; i < ops.length; i++)
    {
      operationsAfter.add(ops[i]);
    }

    operationsAfterCached =
        (FlowControlOperation[]) ops.clone();
  }

  public void addOperationAfter(final FlowControlOperation op)
  {
    if (operationsAfter == null)
    {
      operationsAfter = new ArrayList();
    }
    operationsAfter.add(op);
    operationsAfterCached = null;
  }

  public void addOperationBefore(final FlowControlOperation op)
  {
    if (operationsBefore == null)
    {
      operationsBefore = new ArrayList();
    }
    operationsBefore.add(op);
    operationsBeforeCached = null;
  }

  public boolean isRepeat()
  {
    return repeat;
  }

  public void setRepeat(final boolean repeat)
  {
    this.repeat = repeat;
  }

  public Element findFirstChild (final String uri, final String tagName)
  {
    final Node[] nodes = getNodeArray();
    for (int i = 0; i < nodes.length; i++)
    {
      final Node node = nodes[i];
      if (node instanceof Element == false)
      {
        continue;
      }
      final Element e = (Element) node;
      if (ObjectUtilities.equal(uri, e.getNamespace()) &&
          ObjectUtilities.equal(tagName, e.getType()))
      {
        return e;
      }
    }
    return null;
  }

  public Object clone()
      throws CloneNotSupportedException
  {
    final Section section = (Section) super.clone();
    if (operationsAfter != null)
    {
      section.operationsAfter = (ArrayList) operationsAfter.clone();
    }
    if (operationsBefore != null)
    {
      section.operationsBefore = (ArrayList) operationsBefore.clone();
    }
    section.allElements = (ArrayList) allElements.clone();
    section.allElements.clear();
    final int elementSize = allElements.size();
    if (allElementsCached != null)
    {
      section.allElementsCached = (Node[]) allElementsCached.clone();
      for (int i = 0; i < allElementsCached.length; i++)
      {
        final Node eC = (Node) allElementsCached[i].clone();
        section.allElements.add(eC);
        section.allElementsCached[i] = eC;
        eC.setParent(section);
      }
    }
    else
    {
      for (int i = 0; i < elementSize; i++)
      {
        final Node e = (Node) allElements.get(i);
        final Node eC = (Node) e.clone();
        section.allElements.add(eC);
        eC.setParent(section);
      }
    }
    return section;
  }
}
