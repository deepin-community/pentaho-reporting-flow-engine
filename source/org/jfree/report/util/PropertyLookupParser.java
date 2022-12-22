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
 * $Id: PropertyLookupParser.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.util;

import java.io.Serializable;

/**
 * The property lookup parser is used to resolve embedded references to
 * properties within strings.
 * <p>
 * The default format of the property specification is:
 * <code>${property-name}</code> where 'property-name is the name of the
 * property. If this construct is found within the text, it is replaced with
 * the value returned from a call to "lookupVariable".
 *
 * @author Thomas Morgner
 */
public abstract class PropertyLookupParser implements Serializable
{
  /** A parse state indicator signaling that the parser is outside a property. */
  private static final int EXPECT_DOLLAR = 0;
  /** A parse state indicator signaling that an open brace is expected. */
  private static final int EXPECT_OPEN_BRACE = 1;

  /**
   * A parse state indicator signaling that a closed brace is expected. All chars
   * received, which are not equal to the closed brace, count as property name.
   */
  private static final int EXPECT_CLOSE_BRACE = 2;
  /** The initial marker char, a $ by default. */
  private char markerChar;
  /** The closing brace char. */
  private char closingBraceChar;
  /** The opening brace char. */
  private char openingBraceChar;
  /** The escape char. */
  private char escapeChar;

  /**
   * Initializes the parser to the default format of "${..}". The
   * escape char will be a backslash.
   */
  protected PropertyLookupParser ()
  {
    markerChar = '$';
    closingBraceChar = '}';
    openingBraceChar = '{';
    escapeChar = '\\';
  }

  /**
   * Returns the currently defined closed-brace char.
   *
   * @return the closed-brace char.
   */
  public char getClosingBraceChar ()
  {
    return closingBraceChar;
  }

  /**
   * Defines the closing brace character.
   * @param closingBraceChar the closed-brace character.
   */
  public void setClosingBraceChar (final char closingBraceChar)
  {
    this.closingBraceChar = closingBraceChar;
  }

  /**
   * Returns the escape char.
   * @return the escape char.
   */
  public char getEscapeChar ()
  {
    return escapeChar;
  }

  /**
   * Defines the escape char.
   *
   * @param escapeChar the escape char
   */
  public void setEscapeChar (final char escapeChar)
  {
    this.escapeChar = escapeChar;
  }

  /**
   * Returns the currently defined opening-brace char.
   *
   * @return the opening-brace char.
   */
  public char getOpeningBraceChar ()
  {
    return openingBraceChar;
  }

  /**
   * Defines the opening brace character.
   * @param openingBraceChar the opening-brace character.
   */
  public void setOpeningBraceChar (final char openingBraceChar)
  {
    this.openingBraceChar = openingBraceChar;
  }

  /**
   * Returns initial property marker char.
   * @return the initial property marker character.
   */
  public char getMarkerChar ()
  {
    return markerChar;
  }

  /**
   * Defines initial property marker char.
   * @param markerChar the initial property marker character.
   */
  public void setMarkerChar (final char markerChar)
  {
    this.markerChar = markerChar;
  }

  /**
   * Translates the given string and resolves the embedded property references.
   *
   * @param value the raw value,
   * @return the fully translated string.
   */
  public String translateAndLookup (final String value)
  {
    if (value == null)
    {
      return null;
    }

    final char[] chars = value.toCharArray();
    final StringBuffer result = new StringBuffer(chars.length);
    boolean haveEscape = false;
    int state = EXPECT_DOLLAR;
    final StringBuffer propertyName = new StringBuffer();

    for (int i = 0; i < chars.length; i++)
    {
      final char c = chars[i];

      if (haveEscape)
      {
        haveEscape = false;
        if (state == EXPECT_CLOSE_BRACE)
        {
          propertyName.append(c);
        }
        else
        {
          result.append(c);
        }
        continue;
      }

      if (state == EXPECT_DOLLAR && c == markerChar)
      {
        state = EXPECT_OPEN_BRACE;
        continue;
      }
      if (state == EXPECT_OPEN_BRACE)
      {
        if (c == openingBraceChar)
        {
          state = EXPECT_CLOSE_BRACE;
          continue;
        }
        else
        {
          result.append(markerChar);
          state = 0;
        }
      }
      if (state == EXPECT_CLOSE_BRACE && c == closingBraceChar)
      {
        final String s = lookupVariable(propertyName.toString());
        if (s == null)
        {
          result.append(markerChar);
          result.append(openingBraceChar);
          result.append(propertyName);
          result.append(closingBraceChar);
        }
        else
        {
          result.append(s);
        }
        propertyName.delete(0, propertyName.length());
        state = 0;
        continue;
      }

      if (c == escapeChar)
      {
        haveEscape = true;
        continue;
      }

      if (state == EXPECT_CLOSE_BRACE)
      {
        propertyName.append(c);
      }
      else
      {
        result.append(c);
      }
    }

    if (state >= EXPECT_OPEN_BRACE)
    {
      result.append(markerChar);
      if (state >= EXPECT_CLOSE_BRACE)
      {
        result.append(openingBraceChar);
        result.append(propertyName);
      }
    }
    return result.toString();
  }

  /**
   * Looks up the property with the given name.
   *
   * @param property the name of the property to look up.
   * @return the translated value.
   */
  protected abstract String lookupVariable (String property);
}
