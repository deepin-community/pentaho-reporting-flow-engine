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
 * $Id: AttrFunction.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.expressions.formula.sys;

import org.jfree.report.expressions.ReportFormulaContext;
import org.jfree.report.structure.Element;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.typing.coretypes.ErrorType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

/**
 * Creation-Date: 24.11.2006, 13:02:41
 *
 * @author Thomas Morgner
 */
public class AttrFunction implements Function
{
  public AttrFunction()
  {
  }

  public String getCanonicalName()
  {
    return "ATTR";
  }

  public TypeValuePair evaluate(FormulaContext context,
                                ParameterCallback parameters)
      throws EvaluationException
  {
    // we expect strings and will check, whether the reference for theses
    // strings is dirty.
    if (context instanceof ReportFormulaContext == false)
    {
      return new TypeValuePair(ErrorType.TYPE, new LibFormulaErrorValue
          (LibFormulaErrorValue.ERROR_REFERENCE_NOT_RESOLVABLE));
    }

    final ReportFormulaContext reportFormulaContext =
        (ReportFormulaContext) context;
    Object declaringElement = reportFormulaContext.getDeclaringElement();
    if (declaringElement instanceof Element == false)
    {
      return new TypeValuePair(ErrorType.TYPE, new LibFormulaErrorValue
          (LibFormulaErrorValue.ERROR_REFERENCE_NOT_RESOLVABLE));
    }

    final Element element = (Element) declaringElement;

    if (parameters.getParameterCount() == 1)
    {
      final String value = (String) parameters.getValue(0);
      return new TypeValuePair(AnyType.TYPE, element.getAttribute(value));
    }
    else if (parameters.getParameterCount() == 2)
    {
      final String namespace = (String) parameters.getValue(0);
      final String attrName = (String) parameters.getValue(1);
      return new TypeValuePair(AnyType.TYPE,
          element.getAttribute(namespace, attrName));
    }
    return new TypeValuePair(ErrorType.TYPE, new LibFormulaErrorValue
        (LibFormulaErrorValue.ERROR_INVALID_ARGUMENT));
  }

}
