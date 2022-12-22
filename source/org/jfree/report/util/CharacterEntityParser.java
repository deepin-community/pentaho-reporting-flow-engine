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
 * $Id: CharacterEntityParser.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.util;

import java.util.Enumeration;
import java.util.Properties;

/**
 * The character entity parser replaces all known occurrences of an entity in the format
 * &amp;entityname;.
 *
 * @author Thomas Morgner
 */
public class CharacterEntityParser
{
  /**
   * the entities, keyed by entity name.
   */
  private final Properties entities;

  /**
   * the reverse lookup entities, keyed by character.
   */
  private final Properties reverse;

  /**
   * Creates a new CharacterEntityParser and initializes the parser with the given set of
   * entities.
   *
   * @param characterEntities the entities used for the parser
   */
  public CharacterEntityParser (final Properties characterEntities)
  {
    entities = characterEntities;
    reverse = new Properties();
    final Enumeration keys = entities.keys();
    while (keys.hasMoreElements())
    {
      final String key = (String) keys.nextElement();
      final String value = entities.getProperty(key);
      reverse.setProperty(value, key);
    }
  }

  /**
   * create a new Character entity parser and initializes the parser with the entities
   * defined in the XML standard.
   *
   * @return the CharacterEntityParser initialized with XML entities.
   */
  public static CharacterEntityParser createXMLEntityParser ()
  {
    final Properties entities = new Properties();
    entities.setProperty("amp", "&");
    entities.setProperty("quot", "\"");
    entities.setProperty("lt", "<");
    entities.setProperty("gt", ">");
    entities.setProperty("apos", "\u0027");
    return new CharacterEntityParser(entities);
  }

  /**
   * returns the entities used in the parser.
   *
   * @return the properties for this parser.
   */
  private Properties getEntities ()
  {
    return entities;
  }

  /**
   * returns the reverse-lookup table for the entities.
   *
   * @return the reverse-lookup properties for this parsers.
   */
  private Properties getReverse ()
  {
    return reverse;
  }

  /**
   * Looks up the character for the entity name specified in <code>key</code>.
   *
   * @param key the entity name
   * @return the character as string with a length of 1
   */
  private String lookupCharacter (final String key)
  {
    return getEntities().getProperty(key);
  }

  /**
   * Performs a reverse lookup, to retrieve the entity name for a given character.
   *
   * @param character the character that should be translated into the entity
   * @return the entity name for the character or the untranslated character.
   */
  private String lookupEntity (final String character)
  {
    final String val = getReverse().getProperty(character);
    if (val == null)
    {
      return null;
    }
    else
    {
      return "&" + val + ";";
    }
  }

  /**
   * Encode the given String, so that all known entites are encoded. All characters
   * represented by these entites are now removed from the string.
   *
   * @param value the original string
   * @return the encoded string.
   */
  public String encodeEntities (final String value)
  {
    final StringBuffer writer = new StringBuffer();
    for (int i = 0; i < value.length(); i++)
    {
      final String character = String.valueOf(value.charAt(i));
      final String lookup = lookupEntity(character);
      if (lookup == null)
      {
        writer.append(character);
      }
      else
      {
        writer.append(lookup);
      }
    }
    return writer.toString();
  }

  /**
   * Decode the string, all known entities are replaced by their resolved characters.
   *
   * @param value the string that should be decoded.
   * @return the decoded string.
   */
  public String decodeEntities (final String value)
  {
    int parserIndex = 0;
    int subStart = value.indexOf("&", parserIndex);
    if (subStart == -1)
    {
      return value;
    }
    int subEnd = value.indexOf(";", subStart);
    if (subEnd == -1)
    {
      return value;
    }

    final StringBuffer bufValue = new StringBuffer(value.substring(0, subStart));
    do
    {
      // at this point we know, that there is at least one entity ..
      if (value.charAt(subStart + 1) == '#')
      {
        final int subValue = TextUtilities.parseInt(value.substring(subStart + 2, subEnd), 0);
        if ((subValue >= 1) && (subValue <= 65536))
        {
          final char[] chr = new char[1];
          chr[0] = (char) subValue;
          bufValue.append(chr);
        }
        else
        {
          // invalid entity, do not decode ..
          bufValue.append(value.substring(subStart, subEnd));
        }
      }
      else
      {
        final String entity = value.substring(subStart + 1, subEnd);
        final String replaceString = lookupCharacter(entity);
        if (replaceString != null)
        {
          bufValue.append(decodeEntities(replaceString));
        }
        else
        {
          bufValue.append("&");
          bufValue.append(entity);
          bufValue.append(";");
        }
      }
      parserIndex = subEnd + 1;
      subStart = value.indexOf("&", parserIndex);
      if (subStart == -1)
      {
        bufValue.append(value.substring(parserIndex));
        subEnd = -1;
      }
      else
      {
        subEnd = value.indexOf(";", subStart);
        if (subEnd == -1)
        {
          bufValue.append(value.substring(parserIndex));
        }
        else
        {
          bufValue.append(value.substring(parserIndex, subStart));
        }
      }
    }
    while (subStart != -1 && subEnd != -1);

    return bufValue.toString();
  }
}

