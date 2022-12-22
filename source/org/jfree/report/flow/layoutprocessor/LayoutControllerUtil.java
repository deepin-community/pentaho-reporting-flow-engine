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
 * $Id: LayoutControllerUtil.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow.layoutprocessor;

import java.util.Iterator;
import java.util.Map;

import org.jfree.layouting.input.style.CSSDeclarationRule;
import org.jfree.layouting.input.style.CSSStyleRule;
import org.jfree.layouting.input.style.StyleKey;
import org.jfree.layouting.input.style.StyleKeyRegistry;
import org.jfree.layouting.input.style.StyleRule;
import org.jfree.layouting.input.style.values.CSSValue;
import org.jfree.layouting.namespace.NamespaceDefinition;
import org.jfree.layouting.namespace.Namespaces;
import org.jfree.layouting.util.AttributeMap;
import org.jfree.report.DataSourceException;
import org.jfree.report.EmptyReportData;
import org.jfree.report.JFreeReportInfo;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.data.GlobalMasterRow;
import org.jfree.report.data.PrecomputeNode;
import org.jfree.report.data.PrecomputeNodeKey;
import org.jfree.report.data.PrecomputedValueRegistry;
import org.jfree.report.data.ReportDataRow;
import org.jfree.report.data.StaticExpressionRuntimeData;
import org.jfree.report.expressions.Expression;
import org.jfree.report.expressions.ExpressionRuntime;
import org.jfree.report.flow.EmptyReportTarget;
import org.jfree.report.flow.FlowControlOperation;
import org.jfree.report.flow.FlowController;
import org.jfree.report.flow.LayoutExpressionRuntime;
import org.jfree.report.flow.ReportContext;
import org.jfree.report.flow.ReportJob;
import org.jfree.report.flow.ReportStructureRoot;
import org.jfree.report.flow.ReportTarget;
import org.jfree.report.structure.Element;
import org.jfree.report.structure.Group;
import org.jfree.report.structure.Node;
import org.jfree.report.structure.Section;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Creation-Date: 24.11.2006, 15:01:22
 *
 * @author Thomas Morgner
 */
public class LayoutControllerUtil
{
  public static final EmptyReportData EMPTY_REPORT_DATA = new EmptyReportData();

  private LayoutControllerUtil()
  {
  }

  public static int findNodeInParent(final Section parentSection,
                                     final Node n)
  {
    final Node[] nodes = parentSection.getNodeArray();
    for (int i = 0; i < nodes.length; i++)
    {
      final Node node = nodes[i];
      if (node == n)
      {
        return i;
      }
    }
    return -1;
  }

  public static StaticExpressionRuntimeData getStaticExpressionRuntime
      (final FlowController fc,
       final Object declaringParent)
  {
    final GlobalMasterRow dataRow = fc.getMasterRow();
    final ReportJob reportJob = fc.getReportJob();
    final StaticExpressionRuntimeData sdd = new StaticExpressionRuntimeData();
    sdd.setData(dataRow.getReportDataRow().getReportData());
    sdd.setDeclaringParent(declaringParent);
    sdd.setConfiguration(reportJob.getConfiguration());
    sdd.setReportContext(fc.getReportContext());
    return sdd;
  }


  public static LayoutExpressionRuntime getExpressionRuntime
      (final FlowController fc, final Object node)
  {
    final LayoutExpressionRuntime ler = new LayoutExpressionRuntime();
    ler.setConfiguration(fc.getReportJob().getConfiguration());
    ler.setReportContext(fc.getReportContext());

    final GlobalMasterRow masterRow = fc.getMasterRow();
    ler.setDataRow(masterRow.getGlobalView());

    final ReportDataRow reportDataRow = masterRow.getReportDataRow();
    if (reportDataRow == null)
    {
      ler.setData(EMPTY_REPORT_DATA);
      ler.setCurrentRow(-1);
    }
    else
    {
      ler.setData(reportDataRow.getReportData());
      ler.setCurrentRow(reportDataRow.getCursor());
    }

    ler.setDeclaringParent(node);
    return ler;
  }


  public static FlowController processFlowOperations(FlowController fc,
                                                     final FlowControlOperation[] ops)
      throws DataSourceException
  {
    for (int i = 0; i < ops.length; i++)
    {
      final FlowControlOperation op = ops[i];
      fc = fc.performOperation(op);
    }
    return fc;
  }


  /**
   * Checks, whether the current group should continue. If there is no group, we assume that we should continue. (This
   * emulates the control-break-algorithm's default behaviour if testing an empty set of arguments.)
   *
   * @param fc   the current flow controller holding the data
   * @param node the current node.
   * @return true, if the group is finished and we should stop reiterating it, false if the group is not finished and we
   *         can start iterating it again.
   * @throws org.jfree.report.DataSourceException
   *
   */
  public static boolean isGroupFinished(final FlowController fc,
                                        final Node node)
      throws DataSourceException
  {
    final Node nodeParent = node.getParent();
    if (nodeParent == null)
    {
      return false;
    }
    Group group = nodeParent.getGroup();
    if (group == null)
    {
      return false;
    }

    // maybe we can move this state into the layoutstate itself so that
    // we do not have to rebuild that crap all the time.
    LayoutExpressionRuntime ler = null;

    // OK, now we are almost complete.
    while (group != null)
    {
      if (ler == null)
      {
        ler = getExpressionRuntime(fc, node);
      }

      ler.setDeclaringParent(group);

      final Expression groupingExpression = group.getGroupingExpression();
      if (groupingExpression != null)
      {
        groupingExpression.setRuntime(ler);
        final Object groupFinished;
        try
        {
          groupFinished = groupingExpression.computeValue();
        }
        finally
        {
          groupingExpression.setRuntime(null);
        }

        if (Boolean.TRUE.equals(groupFinished))
        {
          // If the group expression returns true, we should pack our belongings
          // and stop with that process. The group is finished.

          // In Cobol, this would mean that one of the group-fields has changed.
          return true;
        }
      }

      final Node parent = group.getParent();
      if (parent == null)
      {
        group = null;
      }
      else
      {
        group = parent.getGroup();
      }
    }
    return false;
  }


  private static void mergeDeclarationRule(final CSSDeclarationRule target,
                                           final CSSDeclarationRule source)
  {
    final StyleKey[] styleKeys = source.getPropertyKeysAsArray();
    for (int i = 0; i < styleKeys.length; i++)
    {
      final StyleKey key = styleKeys[i];
      final CSSValue value = source.getPropertyCSSValue(key);
      if (value == null)
      {
        continue;
      }

      final boolean sourceImportant = source.isImportant(key);
      final boolean targetImportant = target.isImportant(key);
      if (targetImportant)
      {
        continue;
      }
      target.setPropertyValue(key, value);
      target.setImportant(key, sourceImportant);
    }
  }

  private static CSSDeclarationRule processStyleAttribute
      (final Object styleAttributeValue,
       final Element node,
       final ExpressionRuntime runtime,
       CSSDeclarationRule targetRule)
      throws DataSourceException
  {
    if (targetRule == null)
    {
      try
      {
        targetRule = (CSSDeclarationRule) node.getStyle().clone();
      }
      catch (CloneNotSupportedException e)
      {
        targetRule = new CSSStyleRule(null, null);
      }
    }


    if (styleAttributeValue instanceof String)
    {
      // ugly, we have to parse that thing. Cant think of nothing
      // worse than that.
      final String styleText = (String) styleAttributeValue;
      try
      {
        final ReportContext reportContext = runtime.getReportContext();
        final ReportStructureRoot root = reportContext.getReportStructureRoot();
        final ResourceKey baseResource = root.getBaseResource();
        final ResourceManager resourceManager = root.getResourceManager();

        final byte[] bytes = styleText.getBytes("UTF-8");
        final ResourceKey key = resourceManager.createKey(bytes);
        final Resource resource = resourceManager.create
            (key, baseResource, StyleRule.class);

        final CSSDeclarationRule parsedRule =
            (CSSDeclarationRule) resource.getResource();
        mergeDeclarationRule(targetRule, parsedRule);
      }
      catch (Exception e)
      {
        // ignore ..
        e.printStackTrace();
      }
    }
    else if (styleAttributeValue instanceof CSSStyleRule)
    {
      final CSSStyleRule styleRule =
          (CSSStyleRule) styleAttributeValue;
      mergeDeclarationRule(targetRule, styleRule);
    }

    // ok, not lets fill in the stuff from the style expressions ..
    final Map styleExpressions = node.getStyleExpressions();
    final Iterator styleExIt = styleExpressions.entrySet().iterator();

    while (styleExIt.hasNext())
    {
      final Map.Entry entry = (Map.Entry) styleExIt.next();
      final String name = (String) entry.getKey();
      final Expression expression = (Expression) entry.getValue();
      try
      {
        expression.setRuntime(runtime);
        final Object value = expression.computeValue();
        final StyleKey keyByName =
            StyleKeyRegistry.getRegistry().findKeyByName(name);

        if (value instanceof CSSValue)
        {
          final CSSValue cssvalue = (CSSValue) value;
          if (keyByName != null)
          {
            targetRule.setPropertyValue(keyByName, cssvalue);
          }
          else
          {
            targetRule.setPropertyValueAsString(name, cssvalue.getCSSText());
          }
        }
        else if (value != null)
        {
          targetRule.setPropertyValueAsString(name, String.valueOf(value));
        }
      }
      finally
      {
        expression.setRuntime(null);
      }
    }
    return targetRule;
  }

  private static AttributeMap collectAttributes(final Element node,
                                                final ExpressionRuntime runtime)
      throws DataSourceException
  {

    AttributeMap attributes = node.getAttributeMap();
    final AttributeMap attributeExpressions = node.getAttributeExpressionMap();
    if (attributeExpressions.isEmpty())
    {
      return attributes;
    }
    
    final String[] namespaces = attributeExpressions.getNameSpaces();
    for (int i = 0; i < namespaces.length; i++)
    {
      final String namespace = namespaces[i];
      final Map attrEx = attributeExpressions.getAttributes(namespace);

      final Iterator attributeExIt = attrEx.entrySet().iterator();
      while (attributeExIt.hasNext())
      {
        final Map.Entry entry = (Map.Entry) attributeExIt.next();
        final String name = (String) entry.getKey();
        final Expression expression = (Expression) entry.getValue();
        try
        {
          expression.setRuntime(runtime);
          final Object value = expression.computeValue();
          if (attributes.isReadOnly())
          {
            attributes = new AttributeMap(attributes);
          }
          attributes.setAttribute(namespace, name, value);
        }
        finally
        {
          expression.setRuntime(null);
        }
      }
    }
    return attributes.createUnmodifiableMap();
  }

  public static AttributeMap processAttributes(final Element node,
                                               final ReportTarget target,
                                               final ExpressionRuntime runtime)
      throws DataSourceException
  {
    final AttributeMap attributes = collectAttributes(node, runtime);
    CSSDeclarationRule rule = null;


    final String[] attrNamespaces = attributes.getNameSpaces();
    for (int i = 0; i < attrNamespaces.length; i++)
    {
      final String namespace = attrNamespaces[i];
      final Map attributeMap = attributes.getAttributes(namespace);
      if (attributeMap == null || attributeMap.isEmpty())
      {
        continue;
      }

      final NamespaceDefinition nsDef = target.getNamespaceByUri(namespace);
      final Iterator attributeIt = attributeMap.entrySet().iterator();
      while (attributeIt.hasNext())
      {
        final Map.Entry entry = (Map.Entry) attributeIt.next();
        final String key = (String) entry.getKey();
        if (isStyleAttribute(nsDef, node.getType(), key))
        {
          final Object styleAttributeValue = entry.getValue();
          rule = processStyleAttribute(styleAttributeValue, node, runtime, rule);
        }
      }
    }

    // Just in case there was no style-attribute but there are style-expressions
    if (rule == null)
    {
      rule = processStyleAttribute(null, node, runtime, rule);
    }

    if (rule != null && rule.isEmpty() == false)
    {
      final AttributeMap retval = new AttributeMap(attributes);
      retval.setAttribute(Namespaces.LIBLAYOUT_NAMESPACE, "style", rule);
      retval.makeReadOnly();
      return retval;
    }

    return attributes;
  }

  private static boolean isStyleAttribute(final NamespaceDefinition def,
                                          final String elementName,
                                          final String attrName)
  {
    if (def == null)
    {
      return false;
    }

    final String[] styleAttr = def.getStyleAttribute(elementName);
    for (int i = 0; i < styleAttr.length; i++)
    {
      final String styleAttrib = styleAttr[i];
      if (attrName.equals(styleAttrib))
      {
        return true;
      }
    }
    return false;
  }

  public static AttributeMap createEmptyMap(final String namespace,
                                            final String tagName)
  {
    final AttributeMap map = new AttributeMap();
    map.setAttribute(JFreeReportInfo.REPORT_NAMESPACE,
        Element.NAMESPACE_ATTRIBUTE, namespace);
    map.setAttribute(JFreeReportInfo.REPORT_NAMESPACE,
        Element.TYPE_ATTRIBUTE, tagName);
    return map;
  }


  public static Object performPrecompute(final int expressionPosition,
                                         final PrecomputeNodeKey nodeKey,
                                         final LayoutController layoutController,
                                         final FlowController flowController)
      throws ReportProcessingException, ReportDataFactoryException,
      DataSourceException
  {
    final FlowController fc = flowController.createPrecomputeInstance();
    final PrecomputedValueRegistry pcvr = fc.getPrecomputedValueRegistry();

    pcvr.startElementPrecomputation(nodeKey);

    final LayoutController rootLc = layoutController.createPrecomputeInstance(fc);
    final LayoutController rootParent = rootLc.getParent();
    final ReportTarget target = new EmptyReportTarget(fc.getReportJob(), fc.getExportDescriptor());

    LayoutController lc = rootLc;
    while (lc.isAdvanceable())
    {
      lc = lc.advance(target);
      while (lc.isAdvanceable() == false && lc.getParent() != null)
      {
        final LayoutController parent = lc.getParent();
        lc = parent.join(lc.getFlowController());
      }
    }

    target.commit();
    final PrecomputeNode precomputeNode = pcvr.currentNode();
    final Object functionResult = precomputeNode.getFunctionResult(expressionPosition);
    pcvr.finishElementPrecomputation(nodeKey);
    return functionResult;
//    throw new IllegalStateException
//        ("Ups - we did not get to the root parent again. This is awful and we cannot continue.");
  }


  public static LayoutController skipInvisibleElement(final LayoutController layoutController)
      throws ReportProcessingException, ReportDataFactoryException, DataSourceException
  {
    final FlowController fc = layoutController.getFlowController();
    final ReportTarget target = new EmptyReportTarget(fc.getReportJob(), fc.getExportDescriptor());
    final LayoutController rootParent = layoutController.getParent();

    // Now start to iterate until the derived layout controller 'lc' that has this given parent
    // wants to join.
    LayoutController lc = layoutController;
    while (lc.isAdvanceable())
    {
      lc = lc.advance(target);
      while (lc.isAdvanceable() == false && lc.getParent() != null)
      {
        final LayoutController parent = lc.getParent();
        lc = parent.join(lc.getFlowController());
        if (parent == rootParent)
        {
          target.commit();
          return lc;
        }
      }
    }
    target.commit();
    throw new IllegalStateException
        ("Ups - we did not get to the root parent again. This is awful and we cannot continue.");
//    return lc;
  }

  public static Object evaluateExpression(final FlowController flowController,
                                          final Object declaringParent,
                                          final Expression expression)
      throws DataSourceException
  {
    final ExpressionRuntime runtime =
        getExpressionRuntime(flowController, declaringParent);

    try
    {
      expression.setRuntime(runtime);
      return expression.computeValue();
    }
    catch (DataSourceException dse)
    {
      throw dse;
    }
    catch (Exception e)
    {
      throw new DataSourceException("Failed to evaluate expression", e);
    }
    finally
    {
      expression.setRuntime(null);
    }
  }
}
