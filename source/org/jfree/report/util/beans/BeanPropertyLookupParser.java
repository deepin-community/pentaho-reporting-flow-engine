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
 * $Id: BeanPropertyLookupParser.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.util.beans;

import org.jfree.report.util.CSVTokenizer;
import org.jfree.report.util.PropertyLookupParser;

public abstract class BeanPropertyLookupParser extends PropertyLookupParser
{
  public BeanPropertyLookupParser ()
  {
  }

  /**
   *
   * @param name
   * @return
   */
  protected abstract Object performInitialLookup (String name);

  protected String lookupVariable (final String entity)
  {
    // first, split the entity into separate strings (separator is '.').

    final CSVTokenizer tokenizer = new CSVTokenizer(entity, ".");
    if (tokenizer.hasMoreTokens())
    {
      final String name = tokenizer.nextToken();
      final Object base = performInitialLookup(name);
      try
      {
        if (tokenizer.hasMoreTokens())
        {
          return continueLookupVariable(tokenizer, base);
        }
        else
        {
          return ConverterRegistry.toAttributeValue(base);
        }
      }
      catch (BeanException e)
      {
        return entity;
      }
    }
    return entity;
  }

  private static String continueLookupVariable (final CSVTokenizer tokenizer,
                                                final Object parent)
          throws BeanException
  {
    if (tokenizer.hasMoreTokens())
    {
      final String name = tokenizer.nextToken();
      final Object base = ConverterRegistry.toPropertyValue(name, parent.getClass());
      if (tokenizer.hasMoreTokens())
      {
        return continueLookupVariable(tokenizer, base);
      }
      else
      {
        return ConverterRegistry.toAttributeValue(base);
      }
    }
    return null;
  }

}
