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
 * $Id: FlowOperationReadHandler.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.factories.report.flow;

import org.jfree.report.flow.FlowControlOperation;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

/**
 * Creation-Date: 09.04.2006, 14:47:59
 *
 * @author Thomas Morgner
 */
public class FlowOperationReadHandler extends AbstractXmlReadHandler
{
  private FlowControlOperation operation;

  public FlowOperationReadHandler()
  {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    final String value = attrs.getValue(getUri(), "operation");
    if (value == null)
    {
      throw new ParseException("Required attribute 'operation' is missing.", getLocator());
    }
    final String valueTrimmed = value.trim();
    if (FlowControlOperation.ADVANCE.toString().equals(valueTrimmed))
    {
      operation = FlowControlOperation.ADVANCE;
    }
    else if (FlowControlOperation.COMMIT.toString().equals(valueTrimmed))
    {
      operation = FlowControlOperation.COMMIT;
    }
    else if (FlowControlOperation.DONE.toString().equals(valueTrimmed))
    {
      operation = FlowControlOperation.DONE;
    }
    else if (FlowControlOperation.MARK.toString().equals(valueTrimmed))
    {
      operation = FlowControlOperation.MARK;
    }
    else if (FlowControlOperation.NO_OP.toString().equals(valueTrimmed))
    {
      operation = FlowControlOperation.NO_OP;
    }
    else if (FlowControlOperation.RECALL.toString().equals(valueTrimmed))
    {
      operation = FlowControlOperation.RECALL;
    }
    else
    {
      throw new ParseException("attribute 'operation' has an invalid value.", getLocator());
    }
  }

  public FlowControlOperation getOperation()
  {
    return operation;
  }

  /**
   * Returns the object for this element or null, if this element does not
   * create an object.
   *
   * @return the object.
   * @throws SAXException if there is a parsing error.
   */
  public Object getObject() throws SAXException
  {
    return operation;
  }
}
