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
 * $Id: ElementLayoutController.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.flow.layoutprocessor;

import org.jfree.report.DataSourceException;
import org.jfree.report.ReportDataFactoryException;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.data.ExpressionSlot;
import org.jfree.report.data.PrecomputeNodeKey;
import org.jfree.report.data.PrecomputedExpressionSlot;
import org.jfree.report.data.PrecomputedValueRegistry;
import org.jfree.report.data.RunningExpressionSlot;
import org.jfree.report.data.StaticExpressionRuntimeData;
import org.jfree.report.expressions.Expression;
import org.jfree.report.flow.FlowControlOperation;
import org.jfree.report.flow.FlowController;
import org.jfree.report.flow.ReportTarget;
import org.jfree.report.flow.LayoutExpressionRuntime;
import org.jfree.report.structure.Element;
import org.jfree.layouting.util.AttributeMap;

/**
 * Creation-Date: 24.11.2006, 13:56:30
 *
 * @author Thomas Morgner
 */
public abstract class ElementLayoutController
    implements LayoutController
{
  protected static class ElementPrecomputeKey implements PrecomputeNodeKey
  {
    private String name;
    private String id;
    private String namespace;
    private String tagName;

    public ElementPrecomputeKey(final Element element)
    {
      this.name = element.getName();
      this.tagName = element.getType();
      this.namespace = element.getNamespace();
      this.id = element.getId();
    }

    public boolean equals(final Object obj)
    {
      if (this == obj)
      {
        return true;
      }
      if (obj == null || getClass() != obj.getClass())
      {
        return false;
      }

      final ElementPrecomputeKey that = (ElementPrecomputeKey) obj;

      if (id != null ? !id.equals(that.id) : that.id != null)
      {
        return false;
      }
      if (name != null ? !name.equals(that.name) : that.name != null)
      {
        return false;
      }
      if (namespace != null ? !namespace.equals(
          that.namespace) : that.namespace != null)
      {
        return false;
      }
      if (tagName != null ? !tagName.equals(
          that.tagName) : that.tagName != null)
      {
        return false;
      }

      return true;
    }

    public int hashCode()
    {
      int result = (name != null ? name.hashCode() : 0);
      result = 29 * result + (id != null ? id.hashCode() : 0);
      result = 29 * result + (namespace != null ? namespace.hashCode() : 0);
      result = 29 * result + (tagName != null ? tagName.hashCode() : 0);
      return result;
    }

    public boolean equals(final PrecomputeNodeKey otherKey)
    {
      return false;
    }
  }

  public static final int NOT_STARTED = 0;
  public static final int OPENED = 1;
  public static final int WAITING_FOR_JOIN = 2;
  public static final int FINISHING = 3;
  //public static final int JOINING = 4;
  public static final int FINISHED = 4;

  private int processingState;
  private FlowController flowController;
  private Element element;
  private LayoutController parent;
  private boolean precomputing;
  private AttributeMap attributeMap;
  private int expressionsCount;
  private int iterationCount;

  protected ElementLayoutController()
  {
    this.processingState = ElementLayoutController.NOT_STARTED;
  }


  public String toString()
  {
    return "ElementLayoutController{" +
        "processingState=" + processingState +
        ", element=" + element +
        ", precomputing=" + precomputing +
        ", expressionsCount=" + expressionsCount +
        ", iterationCount=" + iterationCount +
        '}';
  }

  /**
   * Retrieves the parent of this layout controller. This allows childs to query
   * their context.
   *
   * @return the layout controller's parent to <code>null</code> if there is no
   * parent.
   */
  public LayoutController getParent()
  {
    return parent;
  }


  /**
   * Initializes the layout controller. This method is called exactly once. It
   * is the creators responsibility to call this method.
   * <p/>
   * Calling initialize after the first advance must result in a
   * IllegalStateException.
   *
   * @param node           the currently processed object or layout node.
   * @param flowController the current flow controller.
   * @param parent         the parent layout controller that was responsible for
   *                       instantiating this controller.
   * @throws DataSourceException        if there was a problem reading data from
   *                                    the datasource.
   * @throws ReportProcessingException  if there was a general problem during
   *                                    the report processing.
   * @throws ReportDataFactoryException if a query failed.
   */
  public void initialize(final Object node,
                         final FlowController flowController,
                         final LayoutController parent)
      throws DataSourceException, ReportDataFactoryException,
      ReportProcessingException
  {

    if (processingState != ElementLayoutController.NOT_STARTED)
    {
      throw new IllegalStateException();
    }

    this.element = (Element) node;
    this.flowController = flowController;
    this.parent = parent;
    this.iterationCount = -1;
  }

  /**
   * Advances the layout controller to the next state. This method delegates the
   * call to one of the following methods: <ul> <li>{@link
   * #startElement(org.jfree.report.flow.ReportTarget)} <li>{@link
   * #processContent(org.jfree.report.flow.ReportTarget)} <li>{@link
   * #finishElement(org.jfree.report.flow.ReportTarget)} </ul>
   *
   * @param target the report target that receives generated events.
   * @return the new layout controller instance representing the new state.
   *
   * @throws DataSourceException        if there was a problem reading data from
   *                                    the datasource.
   * @throws ReportProcessingException  if there was a general problem during
   *                                    the report processing.
   * @throws ReportDataFactoryException if a query failed.
   */
  public final LayoutController advance(final ReportTarget target)
      throws DataSourceException, ReportProcessingException,
      ReportDataFactoryException
  {
    final int processingState = getProcessingState();
    switch (processingState)
    {
      case ElementLayoutController.NOT_STARTED:
        return startElement(target);
      case ElementLayoutController.OPENED:
        return processContent(target);
      case ElementLayoutController.FINISHING:
        return finishElement(target);
//      case ElementLayoutController.JOINING:
//        return joinWithParent();
      default:
        throw new IllegalStateException();
    }
  }

  /**
   * This method is called for each newly instantiated layout controller. The
   * returned layout controller instance should have a processing state of
   * either 'OPEN' or 'FINISHING' depending on whether there is any content or
   * any child nodes to process.
   *
   * @param target the report target that receives generated events.
   * @return the new layout controller instance representing the new state.
   *
   * @throws DataSourceException        if there was a problem reading data from
   *                                    the datasource.
   * @throws ReportProcessingException  if there was a general problem during
   *                                    the report processing.
   * @throws ReportDataFactoryException if a query failed.
   */
  protected LayoutController startElement(final ReportTarget target)
      throws DataSourceException, ReportProcessingException,
      ReportDataFactoryException
  {
    final Element s = getElement();

    FlowController fc = getFlowController();
    // Step 3: Add the expressions. Any expressions defined for the subreport
    // will work on the queried dataset.
    fc = startData(target, fc);

    final Expression[] expressions = s.getExpressions();
    fc = performElementPrecomputation(expressions, fc);

    if (s.isVirtual() == false)
    {
      attributeMap = computeAttributes(fc, s, target);
      target.startElement(attributeMap);
    }

    final ElementLayoutController derived = (ElementLayoutController) clone();
    derived.setProcessingState(ElementLayoutController.OPENED);
    derived.setFlowController(fc);
    derived.expressionsCount = expressions.length;
    derived.attributeMap = attributeMap;
    derived.iterationCount += 1;
    return derived;
  }

  public AttributeMap getAttributeMap()
  {
    return attributeMap;
  }

  public int getExpressionsCount()
  {
    return expressionsCount;
  }

  public int getIterationCount()
  {
    return iterationCount;
  }


  protected FlowController startData(final ReportTarget target,
                                     final FlowController fc)
      throws DataSourceException, ReportProcessingException,
      ReportDataFactoryException
  {
    return fc;
  }

  protected AttributeMap computeAttributes(final FlowController fc,
                                           final Element element,
                                           final ReportTarget target)
      throws DataSourceException
  {
    final LayoutExpressionRuntime ler =
        LayoutControllerUtil.getExpressionRuntime(fc, element);
    return LayoutControllerUtil.processAttributes(element, target, ler);
  }


  /**
   * Processes any content in this element. This method is called when the
   * processing state is 'OPENED'. The returned layout controller will retain
   * the 'OPENED' state as long as there is more content available. Once all
   * content has been processed, the returned layout controller should carry a
   * 'FINISHED' state.
   *
   * @param target the report target that receives generated events.
   * @return the new layout controller instance representing the new state.
   *
   * @throws DataSourceException        if there was a problem reading data from
   *                                    the datasource.
   * @throws ReportProcessingException  if there was a general problem during
   *                                    the report processing.
   * @throws ReportDataFactoryException if a query failed.
   */
  protected abstract LayoutController processContent(final ReportTarget target)
      throws DataSourceException, ReportProcessingException,
      ReportDataFactoryException;

  /**
   * Finishes the processing of this element. This method is called when the
   * processing state is 'FINISHING'. The element should be closed now and all
   * privatly owned resources should be freed. If the element has a parent, it
   * would be time to join up with the parent now, else the processing state
   * should be set to 'FINISHED'.
   *
   * @param target the report target that receives generated events.
   * @return the new layout controller instance representing the new state.
   *
   * @throws DataSourceException       if there was a problem reading data from
   *                                   the datasource.
   * @throws ReportProcessingException if there was a general problem during the
   *                                   report processing.
   * @throws ReportDataFactoryException if there was an error trying query data.
   */
  protected LayoutController finishElement(final ReportTarget target)
      throws ReportProcessingException, DataSourceException,
      ReportDataFactoryException
  {
    final FlowController fc = handleDefaultEndElement(target);
    final ElementLayoutController derived = (ElementLayoutController) clone();
    derived.setProcessingState(ElementLayoutController.FINISHED);
    derived.setFlowController(fc);
    return derived;
  }

  protected FlowController handleDefaultEndElement (final ReportTarget target)
      throws ReportProcessingException, DataSourceException,
      ReportDataFactoryException
  {
    final Element e = getElement();
    // Step 1: call End Element
    if (e.isVirtual() == false)
    {
      target.endElement(getAttributeMap());
    }

    FlowController fc = getFlowController();
    final PrecomputedValueRegistry pcvr =
        fc.getPrecomputedValueRegistry();
    // Step 2: Remove the expressions of this element
    final int expressionsCount = getExpressionsCount();
    if (expressionsCount != 0)
    {
      final ExpressionSlot[] activeExpressions = fc.getActiveExpressions();
      for (int i = activeExpressions.length - expressionsCount; i < activeExpressions.length; i++)
      {
        final ExpressionSlot slot = activeExpressions[i];
        pcvr.addFunction(slot.getName(), slot.getValue());
      }
      fc = fc.deactivateExpressions();
    }

    if (isPrecomputing() == false)
    {
      pcvr.finishElement(new ElementPrecomputeKey(e));
    }

    return fc;
  }
//
//  /**
//   * Joins the layout controller with the parent. This simply calls
//   * {@link #join(org.jfree.report.flow.FlowController)} on the parent. A join
//   * operation is necessary to propagate changes in the flow-controller to the
//   * parent for further processing.
//   *
//   * @return the joined parent.
//   * @throws IllegalStateException if this layout controller has no parent.
//   */
//  protected LayoutController joinWithParent()
//      throws ReportProcessingException, ReportDataFactoryException,
//      DataSourceException
//  {
//    final LayoutController parent = getParent();
//    if (parent == null)
//    {
//      // skip to the next step ..
//      throw new IllegalStateException("There is no parent to join with. " +
//                                      "This should not happen in a sane environment!");
//    }
//
//    return parent.join(getFlowController());
//  }

  public boolean isAdvanceable()
  {
    return processingState != ElementLayoutController.FINISHED;
  }

  public Element getElement()
  {
    return element;
  }

  public FlowController getFlowController()
  {
    return flowController;
  }

  public int getProcessingState()
  {
    return processingState;
  }

  public void setProcessingState(final int processingState)
  {
    this.processingState = processingState;
  }

  public void setFlowController(final FlowController flowController)
  {
    this.flowController = flowController;
  }

  public void setParent(final LayoutController parent)
  {
    this.parent = parent;
  }

  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException("Clone must be supported.");
    }
  }

  public boolean isPrecomputing()
  {
    return precomputing;
  }

  protected FlowController performElementPrecomputation(
      final Expression[] expressions,
      FlowController fc)
      throws ReportProcessingException, ReportDataFactoryException,
      DataSourceException
  {
    final Element element = getElement();
    final PrecomputedValueRegistry pcvr = fc.getPrecomputedValueRegistry();
    if (isPrecomputing() == false)
    {
      pcvr.startElement(new ElementPrecomputeKey(element));
    }

    if (expressions.length > 0)
    {
      final ExpressionSlot[] slots = new ExpressionSlot[expressions.length];
      final StaticExpressionRuntimeData runtimeData =
          LayoutControllerUtil.getStaticExpressionRuntime(fc, element);

      for (int i = 0; i < expressions.length; i++)
      {
        final Expression expression = expressions[i];
        if (isPrecomputing() == false && expression.isPrecompute())
        {
          // ok, we have to precompute the expression's value. For that
          // we fork a new layout process, compute the value and then come
          // back with the result.
          final Object value = LayoutControllerUtil.performPrecompute
              (i, new ElementPrecomputeKey(element),
                  this, getFlowController());
          slots[i] = new PrecomputedExpressionSlot(expression.getName(), value,
              expression.isPreserve());
        }
        else
        {
          // thats a bit easier; we dont have to do anything special ..
          slots[i] = new RunningExpressionSlot(expression, runtimeData,
              pcvr.currentNode());
        }
      }

      fc = fc.activateExpressions(slots);
    }
    return fc;
  }


  protected FlowController tryRepeatingCommit(final FlowController fc)
      throws DataSourceException
  {
    if (isPrecomputing() == false)
    {
      // ok, the user wanted us to repeat. So we repeat if the group in which
      // we are in, is not closed (and at least one advance has been fired
      // since the last repeat request [to prevent infinite loops]) ...
      final boolean advanceRequested = fc.isAdvanceRequested();
      final boolean advanceable = fc.getMasterRow().isAdvanceable();
      if (advanceable && advanceRequested)
      {
        // we check against the commited target; But we will not use the
        // commited target if the group is no longer active...
        final FlowController cfc =
            fc.performOperation(FlowControlOperation.COMMIT);
        final boolean groupFinished =
            LayoutControllerUtil.isGroupFinished(cfc, getElement());
        if (groupFinished == false)
        {
          return cfc;
        }
      }
    }
    return null;
  }


  /**
   * Derives a copy of this controller that is suitable to perform a
   * precomputation.
   *
   * @param fc
   * @return
   */
  public LayoutController createPrecomputeInstance(final FlowController fc)
  {
    final ElementLayoutController lc = (ElementLayoutController) clone();
    lc.setFlowController(fc);
    lc.setParent(null);
    lc.precomputing = true;
    return lc;
  }


  public Object getNode()
  {
    return getElement();
  }
}
