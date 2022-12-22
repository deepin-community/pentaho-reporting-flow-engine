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
 * $Id: DefaultActionFactory.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.common;

import java.util.HashMap;
import java.util.Iterator;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;


/**
 * Creation-Date: 16.11.2006, 16:28:03
 *
 * @author Thomas Morgner
 */
public class DefaultActionFactory implements ActionFactory
{
  private static final String PREFIX =
      "org.jfree.report.modules.gui.swing.actions.";

  public DefaultActionFactory()
  {
  }

  public ActionPlugin[] getActions(SwingGuiContext context, String category)
  {
    final Configuration configuration = context.getConfiguration();
    final String prefix = PREFIX + category;
    final Iterator keys = configuration.findPropertyKeys(prefix);
    if (keys.hasNext() == false)
    {
      return new ActionPlugin[0];
    }

    final HashMap plugins = new HashMap();
    while (keys.hasNext())
    {
      final String key = (String) keys.next();
      final String base = key.substring(prefix.length());
      if (isPluginKey(base) == false)
      {
        continue;
      }

      final String clazz = configuration.getConfigProperty(key);
      final Object maybeActionPlugin = ObjectUtilities.loadAndInstantiate
          (clazz, DefaultActionFactory.class, ActionPlugin.class);
      if (maybeActionPlugin == null)
      {
        continue;
      }

      final ActionPlugin plugin = (ActionPlugin) maybeActionPlugin;
      if (plugin.initialize(context) == false)
      {
        continue;
      }
      final String role = plugin.getRole();
      if (role == null)
      {
        plugins.put(plugin, plugin);
      }
      else
      {
        final ActionPlugin otherPlugin = (ActionPlugin) plugins.get(role);
        if (otherPlugin != null)
        {
          if (plugin.getRolePreference() > otherPlugin.getRolePreference())
          {
            plugins.put(role, plugin);
          }
        }
        else
        {
          plugins.put(role, plugin);
        }
      }
    }

    return (ActionPlugin[]) plugins.values().toArray
        (new ActionPlugin[plugins.size()]);
  }

  private boolean isPluginKey(final String base)
  {
    if (base.length() < 1)
    {
      return false;
    }
    if (base.indexOf('.', 1) > 0)
    {
      return false;
    }
    return true;
  }
}
