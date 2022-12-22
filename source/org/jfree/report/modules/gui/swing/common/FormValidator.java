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
 * $Id: FormValidator.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.gui.swing.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public abstract class FormValidator
{

  private class FormTextfieldListener
          implements DocumentListener, PropertyChangeListener
  {
    public FormTextfieldListener ()
    {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the
     *            property that has changed.
     */
    public void propertyChange (final PropertyChangeEvent evt)
    {
      if (DOCUMENT_PROPERTY_NAME.equals(evt.getPropertyName()))
      {
        final Document olddoc = (Document) evt.getOldValue();
        olddoc.removeDocumentListener(this);
        final Document newdoc = (Document) evt.getOldValue();
        newdoc.addDocumentListener(this);
      }
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate (final DocumentEvent e)
    {
      handleValidate();
    }

    /**
     * Gives notification that there was an insert into the document.  The range given by
     * the DocumentEvent bounds the freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate (final DocumentEvent e)
    {
      handleValidate();
    }

    /**
     * Gives notification that a portion of the document has been removed.  The range is
     * given in terms of what the view last saw (that is, before updating sticky
     * positions).
     *
     * @param e the document event
     */
    public void removeUpdate (final DocumentEvent e)
    {
      handleValidate();
    }
  }

  private class FormActionListener implements ActionListener
  {
    public FormActionListener ()
    {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed (final ActionEvent e)
    {
      handleValidate();
    }
  }

  private class FormItemListener implements ItemListener
  {
    public FormItemListener ()
    {
    }

    /**
     * Invoked when an item has been selected or deselected by the user. The code written
     * for this method performs the operations that need to occur when an item is selected
     * (or deselected).
     */
    public void itemStateChanged (final ItemEvent e)
    {
      handleValidate();
    }
  }

  private FormTextfieldListener formTextfieldListener;
  private FormActionListener actionListener;
  private static final String DOCUMENT_PROPERTY_NAME = "document";
  private FormItemListener itemListener;
  private boolean enabled;

  protected FormValidator ()
  {
    this.formTextfieldListener = new FormTextfieldListener();
    this.actionListener = new FormActionListener();
    this.itemListener = new FormItemListener();
  }

  public void registerTextField (final JTextComponent textField)
  {
    textField.getDocument().addDocumentListener(formTextfieldListener);
    textField.addPropertyChangeListener(DOCUMENT_PROPERTY_NAME, formTextfieldListener);
  }

  public void registerButton (final AbstractButton bt)
  {
    bt.addActionListener(actionListener);
  }

  public void registerComboBox (final JComboBox bt)
  {
    bt.addItemListener(itemListener);
  }

  public abstract Action getConfirmAction ();

  protected final void handleValidate ()
  {
    final Action confirmAction = getConfirmAction();
    if (confirmAction == null || enabled == false)
    {
      return;
    }

    if (performValidate() == false)
    {
      confirmAction.setEnabled(false);
    }
    else
    {
      confirmAction.setEnabled(true);
    }
  }

  public boolean isEnabled ()
  {
    return enabled;
  }

  public void setEnabled (final boolean enabled)
  {
    this.enabled = enabled;
  }

  public abstract boolean performValidate ();
}
