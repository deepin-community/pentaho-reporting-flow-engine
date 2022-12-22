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
 * $Id: CSVTableModelProducer.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.misc.tablemodel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.table.TableModel;

import org.jfree.report.util.CSVTokenizer;

/**
 * Creates a <code>TableModel</code> using a file formated in CSV for input. The
 * separation can be what ever you want (as it is an understandable regexp). The default
 * separator is a <code>,</code>.
 *
 * @author Mimil
 */
public class CSVTableModelProducer
{
  private BufferedReader reader;
  private String separator;
  private CSVTableModel tableModel;
  private boolean columnNameFirst;

  public CSVTableModelProducer (final InputStream in)
  {
    this(new BufferedReader(new InputStreamReader(in)));
  }

  public CSVTableModelProducer (final String filename)
          throws FileNotFoundException
  {
    this(new BufferedReader(new FileReader(filename)));
  }

  public CSVTableModelProducer (final BufferedReader r)
  {
    if (r == null)
    {
      throw new NullPointerException("The input stream must not be null");
    }
    this.reader = r;
    this.separator = ",";
  }

  public void close ()
          throws IOException
  {
    this.reader.close();
  }

  /**
   * Parses the input and stores data in a TableModel.
   *
   * @see this.getTableModel()
   */
  public synchronized TableModel parse ()
          throws IOException
  {
    if (tableModel != null)
    {
      return tableModel;
    }


    this.tableModel = new CSVTableModel();

    if (this.columnNameFirst == true)
    {   //read the fisrt line
      final String first = this.reader.readLine();

      if (first == null)
      {
        // after the end of the file it makes no sense to read anything.
        // so we can safely return ..
        return tableModel;
      }
      this.tableModel.setColumnNames(splitLine(first));
    }

    final ArrayList data = new ArrayList();
    String line;
    int maxLength = 0;
    while ((line = this.reader.readLine()) != null)
    {
      final String[] o = splitLine(line);
      if (o.length > maxLength)
      {
        maxLength = o.length;
      }
      data.add(o);
    }

    close();

    final Object[][] array = new Object[data.size()][];
    data.toArray(array);
    this.tableModel.setData(array);
    return tableModel;
  }

  private String[] splitLine (final String line)
  {
    final ArrayList row = new ArrayList();
    final CSVTokenizer tokenizer = new CSVTokenizer(line, getSeparator());
    while (tokenizer.hasMoreElements())
    {
      row.add(tokenizer.nextElement());
    }
    return (String[]) row.toArray(new String[row.size()]);
  }

  /**
   * Returns the current separator used to parse the input.
   *
   * @return a regexp
   */
  public String getSeparator ()
  {
    return separator;
  }

  /**
   * Sets the separator for parsing the input. It can be a regexp as we use the function
   * <code>String.split()</code>. The default separator is a <code>;</code>.
   *
   * @param separator a regexp
   */
  public void setSeparator (final String separator)
  {
    this.separator = separator;
  }

  /**
   * Creates the corrspondant TableModel of the input.
   *
   * @return the new TableModel
   */
  public TableModel getTableModel ()
          throws IOException
  {
    return this.parse();
  }

  /**
   * Tells if the first line of the input was column names.
   *
   * @return boolean
   */
  public boolean isColumnNameFirstLine ()
  {
    return columnNameFirst;
  }

  /**
   * Set if the first line of the input is column names or not.
   *
   * @param columnNameFirst boolean
   */
  public void setColumnNameFirstLine (final boolean columnNameFirst)
  {
    this.columnNameFirst = columnNameFirst;
  }

}
