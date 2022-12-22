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
 * $Id: Element.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;

import org.jfree.layouting.input.style.CSSStyleRule;
import org.jfree.layouting.input.style.keys.box.BoxStyleKeys;
import org.jfree.layouting.input.style.values.CSSConstant;
import org.jfree.layouting.namespace.Namespaces;
import org.jfree.layouting.util.AttributeMap;
import org.jfree.layouting.util.LocaleUtility;
import org.jfree.report.JFreeReportInfo;
import org.jfree.report.expressions.Expression;

/**
 * An element is a node that can have attributes. The 'id' and the 'name'
 * attribute is defined for all elements.
 * <p/>
 * Both the name and the id attribute may be null.
 * <p/>
 * Properties in the 'http://jfreereport.sourceforge.net/namespaces/engine/flow'
 * namespace and in the 'http://jfreereport.sourceforge.net/namespaces/engine/compatibility'
 * namespace are considered internal. You should only touch them, if you really
 * know what you are doing.
 *
 * @author Thomas Morgner
 */
public abstract class Element extends Node
{
  private static final Expression[] EMPTY_EXPRESSIONS = new Expression[0];
  private static final Map EMPTY_MAP = Collections.unmodifiableMap(new HashMap());
  public static final String NAME_ATTRIBUTE = "name";
  public static final String ID_ATTRIBUTE = "id";
  /**
   * The type corresponds (somewhat) to the tagname of HTML.
   */
  public static final String TYPE_ATTRIBUTE = "type";
  /**
   * See XML-Namespaces for the idea of that one ...
   */
  public static final String NAMESPACE_ATTRIBUTE = "namespace";
  public static final String VIRTUAL_ATTRIBUTE = "virtual";

  private transient AttributeMap readOnlyAttributes;
  private AttributeMap attributes;
  private CSSStyleRule style;
  private ArrayList expressions;
  private transient AttributeMap readOnlyAttributeExpressions;
  private AttributeMap attributeExpressions;
  private HashMap styleExpressions;
  private Map cachedStyleExpressions;
  private boolean enabled;
  private boolean virtual;
  private Expression displayCondition;

  /**
   * Constructs an element.
   * <p/>
   * The element inherits the element's defined default ElementStyleSheet to
   * provide reasonable default values for common stylekeys. When the element is
   * added to the band, the bands stylesheet is set as parent to the element's
   * stylesheet.
   * <p/>
   * A datasource is assigned with this element is set to a default source,
   * which always returns null.
   */
  protected Element()
  {
    this.style = new CSSStyleRule(null, null);
    this.attributes = new AttributeMap();
    this.enabled = true;
    setNamespace(JFreeReportInfo.REPORT_NAMESPACE);
  }

  public String getNamespace()
  {
    return (String) getAttribute
        (JFreeReportInfo.REPORT_NAMESPACE, Element.NAMESPACE_ATTRIBUTE);
  }

  public void setNamespace(final String id)
  {
    setAttribute
        (JFreeReportInfo.REPORT_NAMESPACE, Element.NAMESPACE_ATTRIBUTE, id);
  }

  public String getId()
  {
    return (String) getAttribute
        (Namespaces.XML_NAMESPACE, Element.ID_ATTRIBUTE);
  }

  public void setId(final String id)
  {
    setAttribute(Namespaces.XML_NAMESPACE, Element.ID_ATTRIBUTE, id);
  }

  public String getType()
  {
    return (String) getAttribute
        (JFreeReportInfo.REPORT_NAMESPACE, Element.TYPE_ATTRIBUTE);
  }

  public void setType(final String type)
  {
    setAttribute
        (JFreeReportInfo.REPORT_NAMESPACE, Element.TYPE_ATTRIBUTE, type);
  }

  /**
   * Defines the name for this Element. The name must not be empty, or a
   * NullPointerException is thrown.
   * <p/>
   * Names can be used to lookup an element within a band. There is no
   * requirement for element names to be unique.
   *
   * @param name the name of this element
   */
  public void setName(final String name)
  {
    setAttribute(Namespaces.XML_NAMESPACE, Element.NAME_ATTRIBUTE, name);
  }


  /**
   * Returns the name of the Element. The name of the Element is never null.
   *
   * @return the name.
   */
  public String getName()
  {
    return (String) getAttribute
        (Namespaces.XML_NAMESPACE, Element.NAME_ATTRIBUTE);
  }

  public void setAttribute(final String name, final Object value)
  {
    setAttribute(getNamespace(), name, value);
  }

  public void setAttribute(final String namespace,
                           final String name,
                           final Object value)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    this.attributes.setAttribute(namespace, name, value);
    readOnlyAttributes = null;
  }

  public Object getAttribute(final String name)
  {
    return getAttribute(getNamespace(), name);
  }

  public Object getAttribute(final String namespace, final String name)
  {
    return this.attributes.getAttribute(namespace, name);
  }

  public AttributeMap getAttributeMap()
  {
    if (this.readOnlyAttributes == null)
    {
      this.readOnlyAttributes = this.attributes.createUnmodifiableMap();
    }
    else if (readOnlyAttributes.getChangeTracker() != this.attributes.getChangeTracker())
    {
      this.readOnlyAttributes = this.attributes.createUnmodifiableMap();
    }
    return readOnlyAttributes;
  }

  /**
   * Returns this elements private stylesheet. This sheet can be used to
   * override the default values set in one of the parent-stylesheets.
   *
   * @return the Element's stylesheet
   */
  public CSSStyleRule getStyle()
  {
    return style;
  }

  public void setVisibility(final CSSConstant v)
  {
    getStyle().setPropertyValue(BoxStyleKeys.VISIBILITY, v);
  }


  public CSSConstant getVisibility()
  {
    return (CSSConstant) getStyle().getPropertyCSSValue(BoxStyleKeys.VISIBILITY);
  }

  public void setAttributeExpression(final String attr,
                                     final Expression function)
  {
    setAttribute(getNamespace(), attr, function);
  }

  /**
   * Adds a function to the report's collection of expressions.
   *
   * @param namespace
   * @param attr
   * @param function  the function.
   */
  public void setAttributeExpression(final String namespace,
                                     final String attr,
                                     final Expression function)
  {

    if (attributeExpressions == null)
    {
      if (function == null)
      {
        return;
      }
      this.attributeExpressions = new AttributeMap();
    }
    attributeExpressions.setAttribute(namespace, attr, function);
    readOnlyAttributeExpressions = null;
  }

  /**
   * Returns the expressions for the report.
   *
   * @param attr
   * @return the expressions.
   */
  public Expression getAttributeExpression(final String attr)
  {
    return getAttributeExpression(getNamespace(), attr);
  }

  public Expression getAttributeExpression(final String namespace,
                                           final String attr)
  {
    if (attributeExpressions == null)
    {
      return null;
    }
    return (Expression) attributeExpressions.getAttribute(namespace, attr);
  }

  public Map getAttributeExpressions(final String namespace)
  {
    if (attributeExpressions == null)
    {
      return null;
    }
    return attributeExpressions.getAttributes(namespace);
  }

  public AttributeMap getAttributeExpressionMap()
  {
    if (this.attributeExpressions == null)
    {
      this.readOnlyAttributeExpressions = new AttributeMap();
      return readOnlyAttributeExpressions;
    }

    if (this.readOnlyAttributeExpressions == null)
    {
      this.readOnlyAttributeExpressions = this.attributeExpressions.createUnmodifiableMap();
    }
    else if (readOnlyAttributeExpressions.getChangeTracker() != this.attributeExpressions.getChangeTracker())
    {
      this.readOnlyAttributeExpressions = this.attributeExpressions.createUnmodifiableMap();
    }
    return readOnlyAttributeExpressions;
  }


  /**
   * Adds a function to the report's collection of expressions.
   *
   * @param function the function.
   * @param property
   */
  public void setStyleExpression(final String property,
                                 final Expression function)
  {
    if (function == null)
    {
      if (styleExpressions != null)
      {
        styleExpressions.remove(property);
        cachedStyleExpressions = null;
      }
    }
    else
    {
      if (styleExpressions == null)
      {
        styleExpressions = new HashMap();
      }
      styleExpressions.put(property, function);
      cachedStyleExpressions = null;
    }
  }

  /**
   * Returns the expressions for the report.
   *
   * @param property
   * @return the expressions.
   */
  public Expression getStyleExpression(final String property)
  {
    if (styleExpressions == null)
    {
      return null;
    }
    return (Expression) styleExpressions.get(property);
  }

  /** @noinspection ReturnOfCollectionOrArrayField*/
  public Map getStyleExpressions()
  {
    if (styleExpressions == null)
    {
      return Element.EMPTY_MAP;
    }
    if (cachedStyleExpressions != null)
    {
      return cachedStyleExpressions;
    }
    cachedStyleExpressions = Collections.unmodifiableMap(styleExpressions);
    return cachedStyleExpressions;
  }

  /**
   * Adds a function to the report's collection of expressions.
   *
   * @param function the function.
   */
  public void addExpression(final Expression function)
  {
    if (expressions == null)
    {
      expressions = new ArrayList();
    }
    expressions.add(function);
  }

  /**
   * Returns the expressions for the report.
   *
   * @return the expressions.
   */
  public Expression[] getExpressions()
  {
    if (expressions == null)
    {
      return Element.EMPTY_EXPRESSIONS;
    }
    return (Expression[]) expressions.toArray
        (new Expression[expressions.size()]);
  }

  /**
   * Sets the expressions for the report.
   *
   * @param expressions the expressions (<code>null</code> not permitted).
   */
  public void setExpressions(final Expression[] expressions)
  {
    if (expressions == null)
    {
      throw new NullPointerException(
          "JFreeReport.setExpressions(...) : null not permitted.");
    }
    if (this.expressions == null)
    {
      this.expressions = new ArrayList(expressions.length);
    }
    else
    {
      this.expressions.clear();
    }
    this.expressions.addAll(Arrays.asList(expressions));
  }

  /**
   * Returns true, if the element is enabled.
   *
   * @return true or false
   */
  public boolean isEnabled()
  {
    return enabled;
  }

  /**
   * Defines whether the element is enabled. Disabled elements will be fully
   * ignored by the report processor. This is a design time property to exclude
   * elements from the processing without actually having to deal with the other
   * complex properties.
   *
   * @param enabled
   */
  public void setEnabled(final boolean enabled)
  {
    this.enabled = enabled;
  }

  public Expression getDisplayCondition()
  {
    return displayCondition;
  }

  public void setDisplayCondition(final Expression displayCondition)
  {
    this.displayCondition = displayCondition;
  }

  public Locale getLocale()
  {
    final Locale locale = getLocaleFromAttributes();
    if (locale != null)
    {
      return locale;
    }
    return super.getLocale();
  }

  protected Locale getLocaleFromAttributes()
  {
    final Object mayBeXmlLang = getAttribute(Namespaces.XML_NAMESPACE, "lang");
    if (mayBeXmlLang instanceof String)
    {
      return LocaleUtility.createLocale((String) mayBeXmlLang);
    }
    else if (mayBeXmlLang instanceof Locale)
    {
      return (Locale) mayBeXmlLang;
    }

    final Object mayBeXhtmlLang = getAttribute(Namespaces.XHTML_NAMESPACE,
        "lang");
    if (mayBeXhtmlLang instanceof String)
    {
      return LocaleUtility.createLocale((String) mayBeXhtmlLang);
    }
    else if (mayBeXhtmlLang instanceof Locale)
    {
      return (Locale) mayBeXhtmlLang;
    }
//
//    final Object mayBeHtmlLang = getAttribute(Namespaces.XHTML_NAMESPACE, "lang");
//    if (mayBeHtmlLang instanceof String)
//    {
//      return LocaleUtility.createLocale((String) mayBeHtmlLang);
//    }
//    else if (mayBeHtmlLang instanceof Locale)
//    {
//      return (Locale) mayBeHtmlLang;
//    }

    return null;
  }

  public boolean isVirtual()
  {
    return virtual;
  }

  public void setVirtual(final boolean virtual)
  {
    this.virtual = virtual;
  }


  public Object clone()
      throws CloneNotSupportedException
  {
    final Element element = (Element) super.clone();
    element.style = (CSSStyleRule) style.clone();
    if (attributes != null)
    {
      element.attributes = (AttributeMap) attributes.clone();
    }

    if (attributeExpressions != null)
    {
      element.attributeExpressions = (AttributeMap) attributeExpressions.clone();
      final String[] namespaces = element.attributeExpressions.getNameSpaces();
      for (int i = 0; i < namespaces.length; i++)
      {
        final String namespace = namespaces[i];
        final Map attrsNs = element.attributeExpressions.getAttributes(
            namespace);
        final Iterator it =
            attrsNs.entrySet().iterator();
        while (it.hasNext())
        {
          final Map.Entry entry = (Map.Entry) it.next();
          final Expression exp = (Expression) entry.getValue();
          entry.setValue(exp.clone());
        }
      }
    }

    if (expressions != null)
    {
      element.expressions = (ArrayList) expressions.clone();
      element.expressions.clear();
      for (int i = 0; i < expressions.size(); i++)
      {
        final Expression expression = (Expression) expressions.get(i);
        element.expressions.add(expression.clone());
      }
    }
    if (styleExpressions != null)
    {
      element.styleExpressions = (HashMap) styleExpressions.clone();
      final Iterator styleExpressionsIt =
          element.styleExpressions.entrySet().iterator();
      while (styleExpressionsIt.hasNext())
      {
        final Map.Entry entry = (Map.Entry) styleExpressionsIt.next();
        final Expression exp = (Expression) entry.getValue();
        entry.setValue(exp.clone());
      }
    }

    if (displayCondition != null)
    {
      element.displayCondition = (Expression) displayCondition.clone();
    }
    return element;
  }
}
