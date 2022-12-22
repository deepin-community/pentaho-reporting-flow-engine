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
 * $Id: ElementReadHandler.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.factories.report.flow;

import java.util.ArrayList;

import org.jfree.report.structure.Element;
import org.jfree.layouting.input.style.CSSStyleRule;
import org.jfree.layouting.input.style.StyleKey;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;

/**
 * Creation-Date: 09.04.2006, 13:55:36
 *
 * @author Thomas Morgner
 */
public abstract class ElementReadHandler extends AbstractXmlReadHandler
{
  private boolean virtual;
  private boolean enabled;
  private String style;
  private ArrayList expressionHandlers;
  private ArrayList styleExpressionHandlers;
  private ArrayList attributeExpressionHandlers;
  private ArrayList attributeHandlers;
  private ArrayList stylePropertyHandlers;
  private DisplayConditionReadHandler displayConditionReadHandler;

  protected ElementReadHandler()
  {
    expressionHandlers = new ArrayList();
    styleExpressionHandlers = new ArrayList();
    attributeExpressionHandlers = new ArrayList();
    stylePropertyHandlers = new ArrayList();
    attributeHandlers = new ArrayList();
  }

  public boolean isEnabled()
  {
    return enabled;
  }

  public String getStyle()
  {
    return style;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    super.startParsing(attrs);
    style = attrs.getValue(FlowReportFactoryModule.NAMESPACE, "style");
    final String enabledValue = attrs.getValue(FlowReportFactoryModule.NAMESPACE, "enabled");
    if (enabledValue != null)
    {
      enabled = "true".equals(enabledValue);
    }
    else
    {
      enabled = true;
    }

    final String virtualValue = attrs.getValue(FlowReportFactoryModule.NAMESPACE, "virtual");
    if (virtualValue != null)
    {
      virtual = "true".equals(virtualValue);
    }
    else
    {
      virtual = false;
    }
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException       if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts)
          throws SAXException
  {
    if (FlowReportFactoryModule.NAMESPACE.equals(uri))
    {
      if ("expression".equals(tagName))
      {
        ExpressionReadHandler erh = new ExpressionReadHandler();
        expressionHandlers.add(erh);
        return erh;
      }
      if ("style-expression".equals(tagName))
      {
        StyleExpressionReadHandler erh = new StyleExpressionReadHandler();
        styleExpressionHandlers.add(erh);
        return erh;
      }
      if ("style-property".equals(tagName))
      {
        PropertyReadHandler erh = new PropertyReadHandler();
        stylePropertyHandlers.add(erh);
        return erh;
      }
      if ("attribute-expression".equals(tagName))
      {
        AttributeExpressionReadHandler erh = new AttributeExpressionReadHandler();
        attributeExpressionHandlers.add(erh);
        return erh;
      }
      if ("attribute".equals(tagName))
      {
        AttributeReadHandler erh = new AttributeReadHandler();
        attributeHandlers.add(erh);
        return erh;
      }
      if ("display-condition".equals(tagName))
      {
        displayConditionReadHandler = new DisplayConditionReadHandler();
        return displayConditionReadHandler;
      }
    }
    return null;
  }

  protected void configureElement(Element e)
  {
    if (displayConditionReadHandler != null)
    {
      e.setDisplayCondition(displayConditionReadHandler.getExpression());
    }
    for (int i = 0; i < expressionHandlers.size(); i++)
    {
      final ExpressionReadHandler handler =
              (ExpressionReadHandler) expressionHandlers.get(i);
      e.addExpression(handler.getExpression());
    }
    for (int i = 0; i < styleExpressionHandlers.size(); i++)
    {
      final StyleExpressionReadHandler handler =
              (StyleExpressionReadHandler) styleExpressionHandlers .get(i);
      e.setStyleExpression(handler.getStyleKey(), handler.getExpression());
    }
    for (int i = 0; i < stylePropertyHandlers.size(); i++)
    {

      final PropertyReadHandler handler =
              (PropertyReadHandler) stylePropertyHandlers .get(i);
      final CSSStyleRule cssStyleRule = e.getStyle();
      cssStyleRule.setPropertyValueAsString(handler.getName(), handler.getResult());
    }
    for (int i = 0; i < attributeExpressionHandlers.size(); i++)
    {
      final AttributeExpressionReadHandler handler =
              (AttributeExpressionReadHandler) attributeExpressionHandlers .get(
                      i);
      e.setAttributeExpression(handler.getAttributeName(),
              handler.getExpression());
    }
    for (int i = 0; i < attributeHandlers.size(); i++)
    {
      final AttributeReadHandler handler =
              (AttributeReadHandler) attributeHandlers .get(i);
      e.setAttribute(handler.getNamespace(), handler.getName(), handler.getObject());
    }
    e.setEnabled(enabled);
    e.setVirtual(virtual);
    if (style != null)
    {
      e.setAttribute(FlowReportFactoryModule.NAMESPACE,"style", style);
    }
  }

  protected abstract Element getElement();

  /**
   * Returns the object for this element or null, if this element does not
   * create an object.
   *
   * @return the object.
   */
  public Object getObject() throws SAXException
  {
    return getElement();
  }
}
