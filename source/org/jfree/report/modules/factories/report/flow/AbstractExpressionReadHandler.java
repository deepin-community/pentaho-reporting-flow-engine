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
 * $Id: AbstractExpressionReadHandler.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.factories.report.flow;

import java.beans.IntrospectionException;

import org.jfree.report.expressions.Expression;
import org.jfree.report.expressions.FormulaExpression;
import org.jfree.report.expressions.FormulaFunction;
import org.jfree.report.util.CharacterEntityParser;
import org.jfree.report.util.beans.BeanUtility;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 09.04.2006, 13:23:32
 *
 * @author Thomas Morgner
 */
public abstract class AbstractExpressionReadHandler
    extends AbstractXmlReadHandler
{
  private Expression expression;
  private BeanUtility expressionBeanUtility;
  private CharacterEntityParser characterEntityParser;

  public AbstractExpressionReadHandler()
  {
    this.characterEntityParser = CharacterEntityParser.createXMLEntityParser();
  }

  protected String getDefaultClassName()
  {
    return null;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    String name = attrs.getValue(getUri(), "name");
    String className = attrs.getValue(getUri(), "class");
    String formula = attrs.getValue(getUri(), "formula");
    if (className == null)
    {
      if (formula != null)
      {
        String initial = attrs.getValue(getUri(), "initial");
        if (initial != null)
        {
          FormulaFunction function = new FormulaFunction();
          function.setInitial(initial);
          function.setFormula(formula);
          this.expression = function;
        }
        else
        {
          FormulaExpression expression = new FormulaExpression();
          expression.setFormula(formula);
          this.expression = expression;
        }
      }
      else
      {
        className = getDefaultClassName();
        if (className == null)
        {
          throw new ParseException("Required attribute 'class' is missing.",
              getRootHandler().getDocumentLocator());
        }
      }
    }

    if (expression == null)
    {
      expression = (Expression) ObjectUtilities.loadAndInstantiate
        (className, AbstractExpressionReadHandler.class, Expression.class);
      if (expression == null)
      {
        throw new ParseException("Expression '" + className +
            "' is not valid. The specified class is not an expression or function.",
             getRootHandler().getDocumentLocator());
      }
    }

    expression.setName(name);
    expression.setDeepTraversing("true".equals(attrs.getValue(getUri(), "deep-traversing")));
    expression.setPrecompute("true".equals(attrs.getValue(getUri(), "precompute")));
    expression.setPreserve("true".equals(attrs.getValue(getUri(), "preserve")));

    try
    {
      expressionBeanUtility = new BeanUtility(expression);
    }
    catch (ClassCastException e)
    {
      throw new ParseException("Expression '" + className +
          "' is not valid. The specified class is not an expression or function.",
          e, getRootHandler().getDocumentLocator());
    }
    catch (IntrospectionException e)
    {
      throw new ParseException("Expression '" + className +
          "' is not valid. Introspection failed for this expression.",
          e, getRootHandler().getDocumentLocator());
    }

  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts)
      throws SAXException
  {
    if (isSameNamespace(uri) == false)
    {
      return null;
    }
    if (tagName.equals("property"))
    {
      return new TypedPropertyReadHandler
          (expressionBeanUtility, expression.getName(),
              characterEntityParser);
    }
    return null;
  }

  public Expression getExpression()
  {
    return expression;
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
    return expression;
  }
}
