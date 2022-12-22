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
 * $Id: WorkerPool.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.util;

/**
 * A simple static workpool. Worker threads are created when necessary.
 *
 * @author Thomas Morgner
 */
public class WorkerPool
{
  /**
   * The worker array.
   */
  private Worker[] workers;
  /**
   * A flag indicating whether idle workers are available.
   */
  private boolean workersAvailable;
  /**
   * the name prefix for all workers of this pool.
   */
  private String namePrefix;

  /**
   * Creates a new worker pool with the default size of 10 workers and the default name.
   */
  public WorkerPool ()
  {
    this(10);
  }

  /**
   * Creates a new workerpool with the given number of workers and the default name.
   *
   * @param size the maximum number of workers available.
   */
  public WorkerPool (final int size)
  {
    this(size, "WorkerPool-worker");
  }

  /**
   * Creates a new worker pool for the given number of workers and with the given name
   * prefix.
   *
   * @param size       the size of the worker pool.
   * @param namePrefix the name prefix for all created workers.
   */
  public WorkerPool (final int size, final String namePrefix)
  {
    if (size <= 0)
    {
      throw new IllegalArgumentException("Size must be > 0");
    }
    workers = new Worker[size];
    workersAvailable = true;
    this.namePrefix = namePrefix;
  }

  /**
   * Checks, whether workers are available.
   *
   * @return true, if at least one worker is idle, false otherwise.
   */
  public synchronized boolean isWorkerAvailable ()
  {
    return workersAvailable;
  }

  /**
   * Updates the workersAvailable flag after a worker was assigned.
   */
  private void updateWorkersAvailable ()
  {
    for (int i = 0; i < workers.length; i++)
    {
      if (workers[i] == null)
      {
        workersAvailable = true;
        return;
      }
      if (workers[i].isAvailable() == true)
      {
        workersAvailable = true;
        return;
      }
    }
    workersAvailable = false;
  }

  /**
   * Waits until a worker will be available.
   */
  private synchronized void waitForWorkerAvailable ()
  {
    while (isWorkerAvailable() == false)
    {
      try
      {
        // remove lock
        this.wait(5000);
      }
      catch (InterruptedException ie)
      {
        // ignored
      }
    }
  }

  /**
   * Returns a workerhandle for the given workload. This method will wait until an idle
   * worker is found.
   *
   * @param r the workload for the worker
   * @return a handle to the worker.
   */
  public synchronized WorkerHandle getWorkerForWorkload (final Runnable r)
  {
    waitForWorkerAvailable();

    int emptySlot = -1;
    for (int i = 0; i < workers.length; i++)
    {
      if (workers[i] == null)
      {
        // in the first run, try to avoid to create new threads...
        // reuse the already available threads
        if (emptySlot == -1)
        {
          emptySlot = i;
        }
        continue;
      }
      if (workers[i].isAvailable() == true)
      {
        workers[i].setWorkload(r);
        updateWorkersAvailable();
        return new WorkerHandle(workers[i]);
      }
    }
    if (emptySlot != -1)
    {
      workers[emptySlot] = new Worker();
      workers[emptySlot].setName(namePrefix + "-" + emptySlot);
      workers[emptySlot].setWorkerPool(this);
      workers[emptySlot].setWorkload(r);
      updateWorkersAvailable();
      return new WorkerHandle(workers[emptySlot]);
    }
    throw new IllegalStateException
            ("At this point, a worker should already have been assigned.");
  }

  /**
   * Marks the given worker as finished. The worker will be removed from the list of the
   * available workers.
   *
   * @param worker the worker which was finished.
   */
  public void workerFinished (final Worker worker)
  {
    if (worker.isFinish() == false)
    {
      throw new IllegalArgumentException("This worker is not in the finish state.");
    }
    for (int i = 0; i < workers.length; i++)
    {
      if (workers[i] == worker)
      {
        synchronized (this)
        {
          workers[i] = null;
          workersAvailable = true;
          this.notifyAll();
        }
        return;
      }
    }
    return;
  }

  /**
   * Marks the given worker as available.
   *
   * @param worker the worker which was available.
   */
  public synchronized void workerAvailable (final Worker worker)
  {
    for (int i = 0; i < workers.length; i++)
    {
      if (workers[i] == worker)
      {
        synchronized (this)
        {
          workersAvailable = true;
          this.notifyAll();
        }
        return;
      }
    }
    return;
  }

  /**
   * Finishes all worker of this pool.
   */
  public void finishAll ()
  {
    for (int i = 0; i < workers.length; i++)
    {
      if (workers[i] != null)
      {
        workers[i].finish();
      }
    }
  }
}
