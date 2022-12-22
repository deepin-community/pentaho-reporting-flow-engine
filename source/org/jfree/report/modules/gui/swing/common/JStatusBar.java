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
 * $Id: JStatusBar.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */
package org.jfree.report.modules.gui.swing.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jfree.report.modules.gui.common.DefaultIconTheme;
import org.jfree.report.modules.gui.common.IconTheme;

public class JStatusBar extends JComponent
{
  public static final int TYPE_ERROR = 3;
  public static final int TYPE_WARNING = 2;
  public static final int TYPE_INFORMATION = 1;
  public static final int TYPE_NONE = 0;

  private JComponent otherComponents;
  private JLabel statusHolder;
  private IconTheme iconTheme;
  private int statusType;

  public JStatusBar()
  {
    this(new DefaultIconTheme());
  }

  public JStatusBar (final IconTheme theme)
  {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createMatteBorder
            (1, 0, 0, 0, UIManager.getDefaults().getColor("controlShadow")));
    statusHolder = new JLabel(" ");
    statusHolder.setMinimumSize(new Dimension(0, 20));
    add(statusHolder, BorderLayout.CENTER);

    otherComponents = new JPanel();
    add(otherComponents, BorderLayout.EAST);
    this.iconTheme = theme;
  }

  protected IconTheme getIconTheme()
  {
    return iconTheme;
  }

  public void setIconTheme(final IconTheme iconTheme)
  {
    IconTheme oldTheme = this.iconTheme;
    this.iconTheme = iconTheme;
    firePropertyChange("iconTheme", oldTheme, iconTheme);

    if (iconTheme == null)
    {
      statusHolder.setIcon(null);
    }
    else
    {
      updateTypeIcon(getStatusType());
    }
  }

  public JComponent getExtensionArea ()
  {
    return otherComponents;
  }

  public int getStatusType()
  {
    return statusType;
  }

  public String getStatusText()
  {
    return statusHolder.getText();
  }

  public void setStatusText (String text)
  {
    final String oldText = statusHolder.getText();
    this.statusHolder.setText(text);
    firePropertyChange("statusText", oldText, text);
  }

  public void setStatusType (int type)
  {
    int oldType = statusType;
    this.statusType = type;
    firePropertyChange("statusType", oldType, type);
    updateTypeIcon(type);
  }

  public void setStatus (final int type, final String text)
  {
    this.statusType = type;
    updateTypeIcon(type);
    statusHolder.setText(text);
  }

  private void updateTypeIcon(final int type)
  {
    if (iconTheme != null)
    {
      if (type == TYPE_ERROR)
      {
        final Icon res = getIconTheme().getSmallIcon(getLocale(), "statusbar.errorIcon");
        statusHolder.setIcon(res);
      }
      else if (type == TYPE_WARNING)
      {
        final Icon res = getIconTheme().getSmallIcon(getLocale(), "statusbar.warningIcon");
        statusHolder.setIcon(res);
      }
      else if (type == TYPE_INFORMATION)
      {
        final Icon res = getIconTheme().getSmallIcon(getLocale(), "statusbar.informationIcon");
        statusHolder.setIcon(res);
      }
      else
      {
        final Icon res = getIconTheme().getSmallIcon(getLocale(), "statusbar.otherIcon");
        statusHolder.setIcon(res);
      }
    }
  }

  public void clear ()
  {
    setStatus(TYPE_NONE, " ");
  }

  /**
   * Gets the locale of this component.
   *
   * @return this component's locale; if this component does not have a locale,
   *         the locale of its parent is returned
   * @throws java.awt.IllegalComponentStateException
   *          if the <code>Component</code> does not have its own locale and has
   *          not yet been added to a containment hierarchy such that the locale
   *          can be determined from the containing parent
   * @see #setLocale
   * @since JDK1.1
   */
  public Locale getLocale()
  {
    try
    {
      return super.getLocale();
    }
    catch(IllegalComponentStateException ice)
    {
      return Locale.getDefault();
    }
  }
}
