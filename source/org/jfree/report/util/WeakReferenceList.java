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
 * $Id: WeakReferenceList.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.util;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * The WeakReference list uses <code>java.lang.ref.WeakReference</code>s to store its
 * contents. In contrast to the WeakHashtable, this list knows how to restore missing
 * content, so that garbage collected elements can be restored when they are accessed.
 * <p/>
 * By default this list can contain 25 elements, where the first element is stored using a
 * strong reference, which is not garbage collected.
 * <p/>
 * Restoring the elements is not implemented, concrete implementations will have to
 * override the <code>restoreChild(int)</code> method. The <code>getMaxChildCount</code>
 * method defines the maxmimum number of children in the list. When more than
 * <code>maxChildCount</code> elements are contained in this list, add will always return
 * false to indicate that adding the element failed.
 * <p/>
 * To customize the list, override createReference to create a different kind of
 * reference.
 * <p/>
 * This list is able to add or clearFromParent elements, but inserting or removing of elements is
 * not possible.
 * <p/>
 *
 * @author Thomas Morgner
 */
public abstract class WeakReferenceList implements Serializable, Cloneable
{
  /**
   * The master element.
   */
  private Object master;

  /**
   * Storage for the references.
   */
  private Reference[] childs;

  /**
   * The current number of elements.
   */
  private int size;

  /**
   * The maximum number of elements.
   */
  private final int maxChilds;

  /**
   * Creates a new weak reference list. The storage of the list is limited to
   * getMaxChildCount() elements.
   *
   * @param maxChildCount the maximum number of elements.
   */
  protected WeakReferenceList (final int maxChildCount)
  {
    this.maxChilds = maxChildCount;
    this.childs = new Reference[maxChildCount - 1];
  }

  /**
   * Returns the maximum number of children in this list.
   *
   * @return the maximum number of elements in this list.
   */
  protected final int getMaxChildCount ()
  {
    return maxChilds;
  }

  /**
   * Returns the master element of this list. The master element is the element stored by
   * a strong reference and cannot be garbage collected.
   *
   * @return the master element
   */
  protected Object getMaster ()
  {
    return master;
  }

  /**
   * Attempts to restore the child stored on the given index.
   *
   * @param index the index.
   * @return null if the child could not be restored or the restored child.
   */
  protected abstract Object restoreChild (int index);

  /**
   * Returns the child stored at the given index. If the child has been garbage collected,
   * it gets restored using the restoreChild function.
   *
   * @param index the index.
   * @return the object.
   */
  public Object get (final int index)
  {
    if (isMaster(index))
    {
      return master;
    }
    else
    {
      final Reference ref = childs[getChildPos(index)];
      if (ref == null)
      {
        throw new IllegalStateException("State: " + index);
      }
      Object ob = ref.get();
      if (ob == null)
      {
        ob = restoreChild(index);
        childs[getChildPos(index)] = createReference(ob);
      }
      return ob;
    }
  }

  /**
   * Replaces the child stored at the given index with the new child which can be null.
   *
   * @param report the object.
   * @param index  the index.
   */
  public void set (final Object report, final int index)
  {
    if (isMaster(index))
    {
      master = report;
    }
    else
    {
      childs[getChildPos(index)] = createReference(report);
    }
  }

  /**
   * Creates a new reference for the given object.
   *
   * @param o the object.
   * @return a WeakReference for the object o without any ReferenceQueue attached.
   */
  private Reference createReference (final Object o)
  {
    return new WeakReference(o);
  }

  /**
   * Adds the element to the list. If the maximum size of the list is exceeded, this
   * function returns false to indicate that adding failed.
   *
   * @param rs the object.
   * @return true, if the object was successfully added to the list, false otherwise
   */
  public boolean add (final Object rs)
  {
    if (size == 0)
    {
      master = rs;
      size = 1;
      return true;
    }
    else
    {
      if (size < getMaxChildCount())
      {
        childs[size - 1] = createReference(rs);
        size++;
        return true;
      }
      else
      {
        // was not able to add this to this list, maximum number of entries reached.
        return false;
      }
    }
  }

  /**
   * Returns true, if the given index denotes a master index of this list.
   *
   * @param index the index.
   * @return true if the index is a master index.
   */
  protected boolean isMaster (final int index)
  {
    return index % getMaxChildCount() == 0;
  }

  /**
   * Returns the internal storage position for the child.
   *
   * @param index the index.
   * @return the internal storage index.
   */
  protected int getChildPos (final int index)
  {
    return index % getMaxChildCount() - 1;
  }

  /**
   * Returns the size of the list.
   *
   * @return the size.
   */
  public int getSize ()
  {
    return size;
  }

  /**
   * Serialisation support. The transient child elements are not saved.
   *
   * @param out the output stream.
   * @throws IOException if there is an I/O error.
   */
  private void writeObject (final java.io.ObjectOutputStream out)
          throws IOException
  {
    final Reference[] orgChilds = childs;
    try
    {
      childs = null;
      out.defaultWriteObject();
    }
    finally
    {
      childs = orgChilds;
    }
  }

  /**
   * Serialisation support. The transient child elements were not saved.
   *
   * @param in the input stream.
   * @throws IOException            if there is an I/O error.
   * @throws ClassNotFoundException if a serialized class is not defined on this system.
   */
  private void readObject (final java.io.ObjectInputStream in)
          throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    childs = new Reference[getMaxChildCount() - 1];
    for (int i = 0; i < childs.length; i++)
    {
      childs[i] = createReference(null);
    }
  }

  /**
   * Creates and returns a copy of this object.  The precise meaning of "copy" may depend
   * on the class of the object. The general intent is that, for any object <tt>x</tt>,
   * the expression: <blockquote>
   * <pre>
   * x.clone() != x</pre></blockquote>
   * will be true, and that the expression: <blockquote>
   * <pre>
   * x.clone().getClass() == x.getClass()</pre></blockquote>
   * will be <tt>true</tt>, but these are not absolute requirements. While it is typically
   * the case that: <blockquote>
   * <pre>
   * x.clone().equals(x)</pre></blockquote>
   * will be <tt>true</tt>, this is not an absolute requirement.
   * <p/>
   * By convention, the returned object should be obtained by calling
   * <tt>super.clone</tt>.  If a class and all of its superclasses (except
   * <tt>Object</tt>) obey this convention, it will be the case that
   * <tt>x.clone().getClass() == x.getClass()</tt>.
   * <p/>
   * By convention, the object returned by this method should be independent of this
   * object (which is being cloned).  To achieve this independence, it may be necessary to
   * modify one or more fields of the object returned by <tt>super.clone</tt> before
   * returning it.  Typically, this means copying any mutable objects that comprise the
   * internal "deep structure" of the object being cloned and replacing the references to
   * these objects with references to the copies.  If a class contains only primitive
   * fields or references to immutable objects, then it is usually the case that no fields
   * in the object returned by <tt>super.clone</tt> need to be modified.
   * <p/>
   * The method <tt>clone</tt> for class <tt>Object</tt> performs a specific cloning
   * operation. First, if the class of this object does not implement the interface
   * <tt>Cloneable</tt>, then a <tt>CloneNotSupportedException</tt> is thrown. Note that
   * all arrays are considered to implement the interface <tt>Cloneable</tt>. Otherwise,
   * this method creates a new instance of the class of this object and initializes all
   * its fields with exactly the contents of the corresponding fields of this object, as
   * if by assignment; the contents of the fields are not themselves cloned. Thus, this
   * method performs a "shallow copy" of this object, not a "deep copy" operation.
   * <p/>
   * The class <tt>Object</tt> does not itself implement the interface <tt>Cloneable</tt>,
   * so calling the <tt>clone</tt> method on an object whose class is <tt>Object</tt> will
   * result in throwing an exception at run time.
   *
   * @return a clone of this instance.
   *
   * @throws CloneNotSupportedException if the object's class does not support the
   *                                    <code>Cloneable</code> interface. Subclasses that
   *                                    override the <code>clone</code> method can also
   *                                    throw this exception to indicate that an instance
   *                                    cannot be cloned.
   * @see Cloneable
   */
  protected Object clone ()
          throws CloneNotSupportedException
  {
    final WeakReferenceList list = (WeakReferenceList) super.clone();
    list.childs = (Reference[]) childs.clone();
    return list;
  }
}
