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
 * $Id: PageStateList.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow.paginating;

import java.util.ArrayList;

import org.jfree.layouting.StateException;
import org.jfree.report.DataSourceException;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.util.WeakReferenceList;


/**
 * The ReportState list stores a report states for the beginning of every page. The list
 * is filled on repagination and read when a report or a page of the report is printed.
 * <p/>
 * Important: This list stores page start report states, not arbitary report states. These
 * ReportStates are special: they can be reproduced by calling processPage on the report.
 * <p/>
 * Internally this list is organized as a list of WeakReferenceLists, where every
 * WeakReferenceList stores a certain number of page states. The first 20 states are
 * stored in an ordinary list with strong-references, so these states never get
 * GarbageCollected (and so they must never be restored by reprocessing them). The next
 * 100 states are stored in 4-element ReferenceLists, so if a reference is lost, only 4
 * states have to be reprocessed. All other states are stored in 10-element lists.
 *
 * @author Thomas Morgner
 */
public class PageStateList
{
  /**
   * The position of the master element in the list. A greater value will reduce the
   * not-freeable memory used by the list, but restoring a single page will require more
   * time.
   */

  /**
   * The maxmimum masterposition size.
   */
  private static final int MASTERPOSITIONS_MAX = 10;

  /**
   * The medium masterposition size.
   */
  private static final int MASTERPOSITIONS_MED = 4;

  /**
   * The max index that will be stored in the primary list.
   */
  private static final int PRIMARY_MAX = 20;

  /**
   * The max index that will be stored in the master4 list.
   */
  private static final int MASTER4_MAX = 120;

  /**
   * Internal WeakReferenceList that is capable to restore its elements. The elements in
   * this list are page start report states.
   */
  private static final class MasterList extends WeakReferenceList
  {
    /**
     * The master list.
     */
    private final PageStateList master;

    /**
     * Creates a new master list.
     *
     * @param list          the list.
     * @param maxChildCount the maximum number of elements in this list.
     */
    private MasterList (final PageStateList list, final int maxChildCount)
    {
      super(maxChildCount);
      this.master = list;
    }

    /**
     * Function to restore the state of a child after the child was garbage collected.
     *
     * @param index the index.
     * @return the restored ReportState of the given index, or null, if the state could
     *         not be restored.
     */
    protected Object restoreChild (final int index)
    {
      final PageState master = (PageState) getMaster();
      if (master == null)
      {
        return null;
      }
      final int max = getChildPos(index);
      try
      {
        return this.restoreState(max, master);
      }
      catch (Exception rpe)
      {
        return null;
      }
    }

    /**
     * Internal handler function restore a state. Count denotes the number of pages
     * required to be processed to restore the page, when the reportstate master is used
     * as source element.
     *
     * @param count     the count.
     * @param rootstate the root state.
     * @return the report state.
     *
     * @throws ReportProcessingException if there was a problem processing the report.
     */
    private PageState restoreState (final int count, final PageState rootstate)
        throws ReportProcessingException, StateException,
        ReportDataFactoryException, DataSourceException
    {
      if (rootstate == null)
      {
        throw new NullPointerException("Master is null");
      }
      PageState state = rootstate;
      for (int i = 0; i <= count; i++)
      {
        final PaginatingReportProcessor pageProcess = master.getPageProcess();
        state = pageProcess.processPage(state);
        set(state, i + 1);
        // todo: How to prevent endless loops. Should we prevent them at all?
      }
      return state;
    }
  }

  /**
   * The list of master states. This is a list of WeakReferenceLists. These
   * WeakReferenceLists contain their master state as first child. The weakReferenceLists
   * have a maxSize of 10, so every 10th state will protected from being
   * garbageCollected.
   */
  private ArrayList masterStates10; // all states > 120
  /**
   * The list of master states. This is a list of WeakReferenceLists. These
   * WeakReferenceLists contain their master state as first child. The weakReferenceLists
   * have a maxSize of 4, so every 4th state will protected from being garbageCollected.
   */
  private ArrayList masterStates4; // all states from 20 - 120

  /**
   * The list of primary states. This is a list of ReportStates and is used to store the
   * first 20 elements of this state list.
   */
  private ArrayList primaryStates; // all states from 0 - 20

  /**
   * The number of elements in this list.
   */
  private int size;

  private PaginatingReportProcessor pageProcess;

  /**
   * Creates a new reportstatelist. The list will be filled using the specified report and
   * output target. Filling of the list is done elsewhere.
   *
   * @param proc the reportprocessor used to restore lost states (null not permitted).
   * @throws NullPointerException if the report processor is <code>null</code>.
   */
  public PageStateList (final PaginatingReportProcessor proc)
  {
    if (proc == null)
    {
      throw new NullPointerException("ReportProcessor null");
    }

    this.pageProcess = proc;

    primaryStates = new ArrayList();
    masterStates4 = new ArrayList();
    masterStates10 = new ArrayList();

  }

  /**
   * Returns the index of the WeakReferenceList in the master list.
   *
   * @param pos         the position.
   * @param maxListSize the maximum list size.
   * @return the position within the masterStateList.
   */
  private int getMasterPos (final int pos, final int maxListSize)
  {
    //return (int) Math.floor(pos / maxListSize);
    return (pos / maxListSize);
  }

  protected PaginatingReportProcessor getPageProcess ()
  {
    return pageProcess;
  }

  /**
   * Returns the number of elements in this list.
   *
   * @return the number of elements in the list.
   */
  public int size ()
  {
    return this.size;
  }

  /**
   * Adds this report state to the end of the list.
   *
   * @param state the report state.
   */
  public void add (final PageState state)
  {
    if (state == null)
    {
      throw new NullPointerException();
    }

    // the first 20 Elements are stored directly into an ArrayList
    if (size() < PRIMARY_MAX)
    {
      primaryStates.add(state);
      this.size++;
    }
    // the next 100 Elements are stored into a list of 4-element weakReference
    //list. So if an Element gets lost (GCd), only 4 states need to be replayed.
    else if (size() < MASTER4_MAX)
    {
      final int secPos = size() - PRIMARY_MAX;
      final int masterPos = getMasterPos(secPos, MASTERPOSITIONS_MED);
      if (masterPos >= masterStates4.size())
      {
        final MasterList master = new MasterList(this, MASTERPOSITIONS_MED);
        masterStates4.add(master);
        master.add(state);
      }
      else
      {
        final MasterList master = (MasterList) masterStates4.get(masterPos);
        master.add(state);
      }
      this.size++;
    }
    // all other Elements are stored into a list of 10-element weakReference
    //list. So if an Element gets lost (GCd), 10 states need to be replayed.
    else
    {
      final int thirdPos = size() - MASTER4_MAX;
      final int masterPos = getMasterPos(thirdPos, MASTERPOSITIONS_MAX);
      if (masterPos >= masterStates10.size())
      {
        final MasterList master = new MasterList(this, MASTERPOSITIONS_MAX);
        masterStates10.add(master);
        master.add(state);
      }
      else
      {
        final MasterList master = (MasterList) masterStates10.get(masterPos);
        master.add(state);
      }
      this.size++;
    }
  }

  /**
   * Removes all elements in the list.
   */
  public void clear ()
  {
    masterStates10.clear();
    masterStates4.clear();
    primaryStates.clear();
    this.size = 0;
  }

  /**
   * Retrieves the element on position <code>index</code> in this list.
   *
   * @param index the index.
   * @return the report state.
   */
  public PageState get (int index)
  {
    if (index >= size() || index < 0)
    {
      throw new IndexOutOfBoundsException
              ("Index is invalid. Index was " + index + "; size was " + size());
    }
    if (index < PRIMARY_MAX)
    {
      return (PageState) primaryStates.get(index);
    }
    else if (index < MASTER4_MAX)
    {
      index -= PRIMARY_MAX;
      final MasterList master
              = (MasterList) masterStates4.get(getMasterPos(index, MASTERPOSITIONS_MED));
      return (PageState) master.get(index);
    }
    else
    {
      index -= MASTER4_MAX;
      final MasterList master
              = (MasterList) masterStates10.get(getMasterPos(index, MASTERPOSITIONS_MAX));
      return (PageState) master.get(index);
    }
  }
}
