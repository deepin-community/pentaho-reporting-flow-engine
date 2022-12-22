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
 * $Id: CSVQuoter.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.util;

/**
 * The <code>CSVQuoter</code> is a helper class to encode a string for the CSV file
 * format.
 *
 * @author Thomas Morgner.
 */
public class CSVQuoter
{
  /**
   * The separator used in the CSV file.
   */
  private char separator;
  /**
   * The quoting character or a single quote.
   */
  private char quate;
  /**
   * The double quote. This is a string containing the quate two times.
   */
  private String doubleQuate;

  /**
   * Creates a new CSVQuoter, which uses a comma as the default separator.
   */
  public CSVQuoter ()
  {
    this(',', '"');
  }

  /**
   * Creates a new <code>CSVQuoter</code>, which uses the defined separator.
   *
   * @param separator the separator.
   * @throws NullPointerException if the given separator is <code>null</code>.
   */
  public CSVQuoter (final char separator)
  {
    this(separator,  '"');
  }

  /**
   * Creates a new CSVQuoter with the given separator and quoting character.
   *
   * @param separator the separator
   * @param quate the quoting character
   */
  public CSVQuoter (final char separator, final char quate)
  {
    this.separator = separator;
    this.quate = quate;
    this.doubleQuate = "" + quate + quate;
  }

  /**
   * Encodes the string, so that the string can safely be used in CSV files. If the string
   * does not need quoting, the original string is returned unchanged.
   *
   * @param original the unquoted string.
   * @return The quoted string
   */
  public String doQuoting (final String original)
  {
    if (isQuotingNeeded(original))
    {
      final StringBuffer retval = new StringBuffer();
      retval.append(quate);
      applyQuote(retval, original);
      retval.append(quate);
      return retval.toString();
    }
    else
    {
      return original;
    }
  }

  /**
   * Decodes the string, so that all escape sequences get removed. If the string was not
   * quoted, then the string is returned unchanged.
   *
   * @param nativeString the quoted string.
   * @return The unquoted string.
   */
  public String undoQuoting (final String nativeString)
  {
    if (isQuotingNeeded(nativeString))
    {
      final StringBuffer b = new StringBuffer(nativeString.length());
      final int length = nativeString.length() - 1;
      int start = 1;

      int pos = start;
      while (pos != -1)
      {
        pos = nativeString.indexOf(doubleQuate, start);
        if (pos == -1)
        {
          b.append(nativeString.substring(start, length));
        }
        else
        {
          b.append(nativeString.substring(start, pos));
          start = pos + 1;
        }
      }
      return b.toString();
    }
    else
    {
      return nativeString;
    }
  }

  /**
   * Tests, whether this string needs to be quoted. A string is encoded if the string
   * contains a newline character, a quote character or the defined separator.
   *
   * @param str the string that should be tested.
   * @return true, if quoting needs to be applied, false otherwise.
   */
  private boolean isQuotingNeeded (final String str)
  {
    if (str.indexOf(separator) != -1)
    {
      return true;
    }
    if (str.indexOf('\n') != -1)
    {
      return true;
    }
    if (str.indexOf(quate, 1) != -1)
    {
      return true;
    }
    return false;
  }

  /**
   * Applies the quoting to a given string, and stores the result in the StringBuffer
   * <code>b</code>.
   *
   * @param b        the result buffer
   * @param original the string, that should be quoted.
   */
  private void applyQuote (final StringBuffer b, final String original)
  {
    // This solution needs improvements. Copy blocks instead of single
    // characters.
    final int length = original.length();

    for (int i = 0; i < length; i++)
    {
      final char c = original.charAt(i);
      if (c == quate)
      {
        b.append(doubleQuate);
      }
      else
      {
        b.append(c);
      }
    }
  }

  /**
   * Gets the separator used in this quoter and the CSV file.
   *
   * @return the separator (never <code>null</code>).
   */
  public char getSeparator ()
  {
    return separator;
  }

  /**
   * Returns the quoting character.
   *
   * @return the quote character.
   */
  public char getQuate ()
  {
    return quate;
  }
}
