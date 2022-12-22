/**
 * =========================================================
 * Pentaho-Reporting-Classic : a free Java reporting library
 * =========================================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
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
 * AbstractActionDowngrade.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

package org.jfree.report.modules.gui.swing.common.action;

import javax.swing.AbstractAction;

import org.jfree.report.modules.gui.swing.common.action.ActionDowngrade;

/**
 * A class that allows Action features introduced in JDK 1.3 to be used with JDK 1.2.2, by
 * defining the two new constants introduced by Sun in JDK 1.3.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractActionDowngrade extends AbstractAction implements ActionDowngrade {
    // kills a compile error for JDK's >= 1.3
    // ambiguous reference error ...
    /**
     * The key used for storing a <code>KeyStroke</code> to be used as the
     * accelerator for the action.
     */
    public static final String ACCELERATOR_KEY = ActionDowngrade.ACCELERATOR_KEY;

    /**
     * The key used for storing an int key code to be used as the mnemonic
     * for the action.
     */
    public static final String MNEMONIC_KEY = ActionDowngrade.MNEMONIC_KEY;

    /**
     * Creates a new action with a default (transparent) icon.
     */
    protected AbstractActionDowngrade() {
        // nothing required
    }
    
}
