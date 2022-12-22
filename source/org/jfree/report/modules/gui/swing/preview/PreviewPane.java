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
 * $Id: PreviewPane.java 10756 2009-12-02 15:58:24Z tmorgner $
 * ------------
 * (C) Copyright 2000-2005, by Object Refinery Limited.
 * (C) Copyright 2005-2007, by Pentaho Corporation.
 */

package org.jfree.report.modules.gui.swing.preview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jfree.layouting.modules.output.graphics.PageDrawable;
import org.jfree.layouting.modules.output.graphics.DrawablePanel;
import org.jfree.report.JFreeReportBoot;
import org.jfree.report.flow.ReportJob;
import org.jfree.report.flow.ReportStructureRoot;
import org.jfree.report.modules.gui.common.IconTheme;
import org.jfree.report.modules.gui.swing.common.ActionPlugin;
import org.jfree.report.modules.gui.swing.common.SwingGuiContext;
import org.jfree.report.modules.gui.swing.common.SwingUtil;
import org.jfree.report.modules.gui.swing.common.KeyedComboBoxModel;
import org.jfree.report.modules.gui.swing.common.CenterLayout;
import org.jfree.report.modules.gui.swing.printing.PrintReportProcessor;
import org.jfree.report.util.Worker;
import org.jfree.report.util.TextUtilities;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 11.11.2006, 19:36:13
 *
 * @author Thomas Morgner
 */
public class PreviewPane extends JPanel
{
  private class PreviewGuiContext implements SwingGuiContext
  {
    public PreviewGuiContext()
    {
    }

    public Window getWindow()
    {
      return SwingUtil.getWindowAncestor(PreviewPane.this);
    }

    public Locale getLocale()
    {
      ReportJob report = getReportJob();
      if (report != null)
      {
        return report.getReportStructureRoot().getLocale();
      }
      return Locale.getDefault();
    }

    public IconTheme getIconTheme()
    {
      return PreviewPane.this.getIconTheme();
    }

    public Configuration getConfiguration()
    {
      ReportJob report = getReportJob();
      if (report != null)
      {
        return report.getConfiguration();
      }
      return JFreeReportBoot.getInstance().getGlobalConfig();
    }
  }

  private class RepaginationRunnable implements Runnable
  {
    private PrintReportProcessor processor;

    public RepaginationRunnable(PrintReportProcessor processor)
    {
      this.processor = processor;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to
     * create a thread, starting the thread causes the object's <code>run</code>
     * method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take
     * any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run()
    {
      final UpdatePaginatingPropertyHandler startPaginationNotify =
          new UpdatePaginatingPropertyHandler(processor, true, 0);
      if (SwingUtilities.isEventDispatchThread())
      {
        startPaginationNotify.run();
      }
      else
      {
        SwingUtilities.invokeLater(startPaginationNotify);
      }

      // Perform the pagination ..
      final int pageCount = processor.getNumberOfPages();

      final UpdatePaginatingPropertyHandler endPaginationNotify =
          new UpdatePaginatingPropertyHandler(processor, false, pageCount);
      if (SwingUtilities.isEventDispatchThread())
      {
        endPaginationNotify.run();
      }
      else
      {
        SwingUtilities.invokeLater(endPaginationNotify);
      }

    }
  }

  private class UpdatePaginatingPropertyHandler implements Runnable
  {
    private boolean paginating;
    private int pageCount;
    private PrintReportProcessor processor;

    public UpdatePaginatingPropertyHandler(final PrintReportProcessor processor,
                                           final boolean paginating,
                                           final int pageCount)
    {
      this.processor = processor;
      this.paginating = paginating;
      this.pageCount = pageCount;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to
     * create a thread, starting the thread causes the object's <code>run</code>
     * method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take
     * any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run()
    {
      if (processor != getPrintReportProcessor())
      {
        return;
      }

      if (paginating == false)
      {
        setNumberOfPages(pageCount);
        if (getPageNumber() < 1)
        {
          setPageNumber(1);
        }
        else if (getPageNumber() > pageCount)
        {
          setPageNumber(pageCount);
        }
      }
      setPaginating(paginating);
    }
  }

  private class PreviewUpdateHandler implements PropertyChangeListener
  {
    public PreviewUpdateHandler()
    {
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
      final String propertyName = evt.getPropertyName();
      if (PAGINATING_PROPERTY.equals(propertyName))
      {
        if (isPaginating())
        {
          drawablePanel.setDrawableAsRawObject(getPaginatingDrawable());
        }
        else
        {
          updateVisiblePage(getPageNumber());
        }
      }
      else if (REPORT_JOB_PROPERTY.equals(propertyName))
      {
        if (getReportJob() == null)
        {
          drawablePanel.setDrawableAsRawObject(getNoReportDrawable());
        }
        // else the paginating property will be fired anyway ..
      }
      else if (PAGE_NUMBER_PROPERTY.equals(propertyName))
      {
        if (isPaginating())
        {
          return;
        }

        updateVisiblePage(getPageNumber());
      }
    }
  }

  private class UpdateZoomHandler implements PropertyChangeListener
  {
    public UpdateZoomHandler()
    {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and
     *            the property that has changed.
     */

    public void propertyChange(PropertyChangeEvent evt)
    {
      if ("zoom".equals(evt.getPropertyName()) == false)
      {
        return;
      }

      final double zoom = getZoom();
      pageDrawable.setZoom(zoom);
      zoomModel.setSelectedKey(new Double(zoom));
      if (zoomModel.getSelectedKey() == null)
      {
        zoomModel.setSelectedItem(formatZoomText(zoom));
      }
      drawablePanel.revalidate();
    }
  }

  private static final double ZOOM_FACTORS[] = {
      0.5, 0.75, 1, 1.20, 1.50, 2.00
  };
  private static final int DEFAULT_ZOOM_INDEX = 2;
  public static final String PAGE_NUMBER_PROPERTY = "pageNumber";
  public static final String NUMBER_OF_PAGES_PROPERTY = "numberOfPages";
  public static final String STATUS_TEXT_PROPERTY = "statusText";
  public static final String STATUS_TYPE_PROPERTY = "statusType";
  public static final String REPORT_CONTROLLER_PROPERTY = "reportController";
  public static final String REPORT_JOB_PROPERTY = "reportJob";
  public static final String ZOOM_PROPERTY = "zoom";
  public static final String CLOSED_PROPERTY = "closed";
  public static final String PAGINATING_PROPERTY = "paginating";
  public static final String ICON_THEME_PROPERTY = "iconTheme";
  public static final String TITLE_PROPERTY = "title";
  public static final String MENU_PROPERTY = "menu";

  private Object paginatingDrawable;
  private Object noReportDrawable;
  private PageBackgroundDrawable pageDrawable;

  private DrawablePanel drawablePanel;
  private ReportController reportController;
  private JMenu[] menus;
  private JToolBar toolBar;
  private String statusText;
  private String title;
  private int statusType;
  private boolean closed;
  private ReportJob reportJob;

  private int numberOfPages;
  private int pageNumber;
  private SwingGuiContext swingGuiContext;
  private IconTheme iconTheme;
  private double zoom;
  private boolean paginating;

  private PrintReportProcessor printReportProcessor;


  private Worker paginationWorker;
  private JPanel innerReportControllerHolder;
  private JPanel toolbarHolder;
  private JPanel outerReportControllerHolder;
  private boolean reportControllerInner;
  private String reportControllerLocation;
  private JComponent reportControllerComponent;
  private KeyedComboBoxModel zoomModel;


  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public PreviewPane()
  {
    this.menus = new JMenu[0];
    setLayout(new BorderLayout());

    zoomModel = new KeyedComboBoxModel();
    zoomModel.setAllowOtherValue(true);
    zoom = ZOOM_FACTORS[DEFAULT_ZOOM_INDEX];

    pageDrawable = new PageBackgroundDrawable();

    drawablePanel = new DrawablePanel();
    drawablePanel.setOpaque(false);
    drawablePanel.setBackground(Color.green);

    swingGuiContext = new PreviewGuiContext();

    final JPanel reportPaneHolder = new JPanel(new CenterLayout());
    reportPaneHolder.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    reportPaneHolder.add(drawablePanel);

    final JScrollPane s1 = new JScrollPane(reportPaneHolder);
    s1.getVerticalScrollBar().setUnitIncrement(20);

    innerReportControllerHolder = new JPanel();
    innerReportControllerHolder.setLayout(new BorderLayout());
    innerReportControllerHolder.add(s1, BorderLayout.CENTER);

    toolbarHolder = new JPanel();
    toolbarHolder.setLayout(new BorderLayout());
    toolbarHolder.add(innerReportControllerHolder, BorderLayout.CENTER);

    outerReportControllerHolder = new JPanel();
    outerReportControllerHolder.setLayout(new BorderLayout());
    outerReportControllerHolder.add(toolbarHolder, BorderLayout.CENTER);

    add(outerReportControllerHolder, BorderLayout.CENTER);

    addPropertyChangeListener(new PreviewUpdateHandler());
    addPropertyChangeListener("zoom", new UpdateZoomHandler());
  }

  public synchronized PrintReportProcessor getPrintReportProcessor()
  {
    return printReportProcessor;
  }

  protected synchronized void setPrintReportProcessor(final PrintReportProcessor printReportProcessor)
  {
    this.printReportProcessor = printReportProcessor;
  }

  public JMenu[] getMenu()
  {
    return menus;
  }

  protected void setMenu(final JMenu[] menus)
  {
    if (menus == null)
    {
      throw new NullPointerException();
    }
    final JMenu[] oldmenu = this.menus;
    this.menus = (JMenu[]) menus.clone();
    firePropertyChange(MENU_PROPERTY, oldmenu, this.menus);
  }

  public JToolBar getToolBar()
  {
    return toolBar;
  }

  public String getStatusText()
  {
    return statusText;
  }

  public void setStatusText(final String statusText)
  {
    String oldStatus = this.statusText;
    this.statusText = statusText;

    firePropertyChange(STATUS_TEXT_PROPERTY, oldStatus, statusText);
  }

  public int getStatusType()
  {
    return statusType;
  }

  public void setStatusType(final int statusType)
  {
    int oldType = this.statusType;
    this.statusType = statusType;

    firePropertyChange(STATUS_TYPE_PROPERTY, oldType, statusType);
  }

  public ReportController getReportController()
  {
    return reportController;
  }

  public void setReportController(final ReportController reportController)
  {
    ReportController oldController = this.reportController;
    this.reportController = reportController;
    firePropertyChange(REPORT_CONTROLLER_PROPERTY, oldController, reportController);

    // Now add the controller to the GUI ..
    refreshReportController(reportController);
  }

  public void refreshReportController(final ReportController newReportController)
  {
    if (newReportController != null)
    {
      final JComponent rcp = newReportController.getControlPanel();
      // if either the controller component or its position (inner vs outer)
      // and border-position has changed, then refresh ..
      if (reportControllerComponent != rcp ||
          reportControllerInner != newReportController.isInnerComponent() ||
          ObjectUtilities.equal(reportControllerLocation,
              newReportController.getControllerLocation()))
      {
        if (reportControllerComponent != null)
        {
          outerReportControllerHolder.remove(reportControllerComponent);
          innerReportControllerHolder.remove(reportControllerComponent);
        }
        final String sanLocation = sanitizeLocation(
            newReportController.getControllerLocation());
        final boolean innerComponent = newReportController.isInnerComponent();
        if (rcp != null)
        {
          if (innerComponent)
          {
            innerReportControllerHolder.add(rcp, sanLocation);
          }
          else
          {
            outerReportControllerHolder.add(rcp, sanLocation);
          }
        }
        reportControllerComponent = rcp;
        reportControllerLocation = sanLocation;
        reportControllerInner = innerComponent;
      }
    }
    else
    {
      if (reportControllerComponent != null)
      {
        outerReportControllerHolder.remove(reportControllerComponent);
        innerReportControllerHolder.remove(reportControllerComponent);
      }
      reportControllerComponent = null;
    }
  }


  private String sanitizeLocation(final String location)
  {
    if (BorderLayout.NORTH.equals(location))
    {
      return BorderLayout.NORTH;
    }
    if (BorderLayout.SOUTH.equals(location))
    {
      return BorderLayout.SOUTH;
    }
    if (BorderLayout.WEST.equals(location))
    {
      return BorderLayout.WEST;
    }
    if (BorderLayout.EAST.equals(location))
    {
      return BorderLayout.EAST;
    }
    return BorderLayout.NORTH;
  }

  public ReportJob getReportJob()
  {
    return reportJob;
  }

  public void setReportJob(final ReportJob reportJob)
  {
    ReportJob oldJob = this.reportJob;
    this.reportJob = reportJob;

    firePropertyChange(REPORT_JOB_PROPERTY, oldJob, reportJob);
    if (reportJob == null)
    {
      initializeWithoutJob();
    }
    else
    {
      initializeFromReport();
    }
  }

  public double getZoom()
  {
    return zoom;
  }

  public void setZoom(final double zoom)
  {
    double oldZoom = this.zoom;
    this.zoom = zoom;
    firePropertyChange(ZOOM_PROPERTY, oldZoom, zoom);
  }

  public boolean isClosed()
  {
    return closed;
  }

  public void setClosed(final boolean closed)
  {
    boolean oldClosed = this.closed;
    this.closed = closed;
    firePropertyChange(CLOSED_PROPERTY, oldClosed, closed);
    if (closed)
    {
      prepareShutdown();
    }
  }

  private void prepareShutdown()
  {
    synchronized (this)
    {
      if (paginationWorker != null)
      {
        synchronized (paginationWorker)
        {
          paginationWorker.finish();
        }
        paginationWorker = null;
      }
      if (printReportProcessor != null)
      {
        printReportProcessor.close();
        printReportProcessor = null;
      }
      closeToolbar();
    }
  }

  private int getUserDefinedCategoryPosition()
  {
    return TextUtilities.parseInt
        (swingGuiContext.getConfiguration().getConfigProperty
            ("org.jfree.report.modules.gui.swing.user-defined-category.position"), 15000);
  }


  public Locale getLocale()
  {
    ReportStructureRoot report = getReportJob().getReportStructureRoot();
    if (report != null)
    {
      return report.getLocale();
    }
    return super.getLocale();
  }

  public int getNumberOfPages()
  {
    return numberOfPages;
  }

  public void setNumberOfPages(final int numberOfPages)
  {
    final int oldPageNumber = this.numberOfPages;
    this.numberOfPages = numberOfPages;
    firePropertyChange(NUMBER_OF_PAGES_PROPERTY, oldPageNumber, numberOfPages);
  }

  public int getPageNumber()
  {
    return pageNumber;
  }

  public void setPageNumber(final int pageNumber)
  {
    final int oldPageNumber = this.pageNumber;
    this.pageNumber = pageNumber;
    firePropertyChange(PAGE_NUMBER_PROPERTY, oldPageNumber, pageNumber);
  }

  public IconTheme getIconTheme()
  {
    return iconTheme;
  }

  protected void setIconTheme(IconTheme theme)
  {
    IconTheme oldTheme = this.iconTheme;
    this.iconTheme = theme;
    firePropertyChange(ICON_THEME_PROPERTY, oldTheme, theme);
  }

  protected void initializeFromReport()
  {
    setIconTheme(PreviewPaneUtilities.createIconTheme(reportJob.getConfiguration()));

    zoomModel.clear();
    for (int i = 0; i < ZOOM_FACTORS.length; i++)
    {
      zoomModel.add(new Double(ZOOM_FACTORS[i]), formatZoomText(ZOOM_FACTORS[i]));
    }
    zoom = ZOOM_FACTORS[DEFAULT_ZOOM_INDEX];
    zoomModel.setSelectedKey(new Double(ZOOM_FACTORS[DEFAULT_ZOOM_INDEX]));

    HashMap actions = PreviewPaneUtilities.loadActions(swingGuiContext);
    buildMenu(actions);


    if (toolBar != null)
    {
      toolbarHolder.remove(toolBar);
    }
    toolBar = buildToolbar(actions);
    if (toolBar != null)
    {
      toolbarHolder.add(toolBar, BorderLayout.NORTH);
    }

    startPagination();
  }

  private JToolBar buildToolbar(final HashMap actions)
  {
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);

    final ActionCategory[] cats = (ActionCategory[])
        actions.keySet().toArray(new ActionCategory[actions.size()]);
    Arrays.sort(cats);

    for (int i = 0; i < cats.length; i++)
    {
      final ActionCategory cat = cats[i];
      final ActionPlugin[] plugins = (ActionPlugin[]) actions.get(cat);
      PreviewPaneUtilities.addActionsToToolBar(toolBar, plugins, this);
    }

    return toolBar;
  }

  private void closeToolbar()
  {
    if (toolBar.getParent() != toolbarHolder)
    {
      // ha!, we detected that the toolbar is floating ...
      // Log.debug (currentToolbar.getParent());
      final Window w = SwingUtilities.windowForComponent(toolBar);
      if (w != null)
      {
        w.setVisible(false);
        w.dispose();
      }
    }
    toolBar.setVisible(false);
  }

  public SwingGuiContext getSwingGuiContext()
  {
    return swingGuiContext;
  }

  public KeyedComboBoxModel getZoomModel()
  {
    return zoomModel;
  }

  private String formatZoomText(final double zoom)
  {
    final NumberFormat numberFormat =
        NumberFormat.getPercentInstance(swingGuiContext.getLocale());
    return (numberFormat.format(zoom));
  }


  private void buildMenu(final HashMap actions)
  {
    final HashMap menus = new HashMap();
    final int userPos = getUserDefinedCategoryPosition();

    final ActionCategory[] categories = new ActionCategory[actions.size()];
    boolean insertedUserDefinedActions = false;
    int catCount = 0;
    final Iterator iterator = actions.entrySet().iterator();
    while (iterator.hasNext())
    {
      final Map.Entry entry = (Map.Entry) iterator.next();
      final ActionCategory cat = (ActionCategory) entry.getKey();
      categories[catCount] = cat;
      catCount += 1;
      final ActionPlugin[] plugins = (ActionPlugin[]) entry.getValue();

      if (insertedUserDefinedActions == false && cat.getPosition() > userPos)
      {
        ReportController controller = getReportController();
        if (controller != null)
        {
          controller.initialize(this);
          final JMenu[] controlerMenus = controller.getMenus();
          for (int i = 0; i < controlerMenus.length; i++)
          {
            final ActionCategory userCategory = new ActionCategory();
            userCategory.setName("X-User-Category-" + i);
            userCategory.setPosition(userPos + i);
            menus.put(userCategory, controlerMenus[i]);
          }
        }

        insertedUserDefinedActions = true;
      }

      final JMenu menu = PreviewPaneUtilities.createMenu(cat);
      int count = PreviewPaneUtilities.buildMenu(menu, plugins, this);
      menus.put(cat, menu);
    }

    final CategoryTreeItem[] categoryTreeItems =
        PreviewPaneUtilities.buildMenuTree(categories);

    ArrayList menuList = new ArrayList();
    for (int i = 0; i < categoryTreeItems.length; i++)
    {
      final CategoryTreeItem item = categoryTreeItems[i];
      final JMenu menu = (JMenu) menus.get(item.getCategory());
      // now connect all menus ..
      final CategoryTreeItem[] childs = item.getChilds();
      Arrays.sort(childs);
      for (int j = 0; j < childs.length; j++)
      {
        CategoryTreeItem child = childs[j];
        final JMenu childMenu = (JMenu) menus.get(child.getCategory());
        if (childMenu != null)
        {
          menu.add(childMenu);
        }
      }

      if (item.getParent() == null)
      {
        menuList.add(item);
      }
    }

    Collections.sort(menuList);
    ArrayList retval = new ArrayList();
    for (int i = 0; i < menuList.size(); i++)
    {
      final CategoryTreeItem item = (CategoryTreeItem) menuList.get(i);
      JMenu menu = (JMenu) menus.get(item.getCategory());
      if (menu.getItemCount() > 0)
      {
        retval.add(menu);
      }
    }

    setMenu((JMenu[]) retval.toArray(new JMenu[retval.size()]));
  }

//  private JMenu createViewMenu(ActionCategory cat)
//  {
//    JMenu zoom = new JMenu("Zoom");
//    zoom.add(new ActionMenuItem(new ZoomOutAction(this)));
//    zoom.add(new ActionMenuItem(new ZoomInAction(this)));
//    zoom.addSeparator();
//
//    for (int i = 0; i < ZOOM_FACTORS.length; i++)
//    {
//      double factor = ZOOM_FACTORS[i];
//      zoom.add(new ActionMenuItem(new ZoomAction(factor, this)));
//    }
//
//    zoom.addSeparator();
//    zoom.add(new ActionMenuItem(new ZoomCustomAction(this)));
//
//    JMenu menu = new JMenu("View");
//    menu.add(zoom);
//    menu.addSeparator();
//    menu.add(new ActionMenuItem("Paginated"));
//    menu.add(new ActionMenuItem("Flow"));
//    return menu;
//  }

  protected void initializeWithoutJob()
  {
    final Configuration globalConfig =
        JFreeReportBoot.getInstance().getGlobalConfig();
    setIconTheme(PreviewPaneUtilities.createIconTheme(globalConfig));

    zoomModel.clear();
    for (int i = 0; i < ZOOM_FACTORS.length; i++)
    {
      zoomModel.add(new Double(ZOOM_FACTORS[i]), formatZoomText(ZOOM_FACTORS[i]));
    }
    zoom = ZOOM_FACTORS[DEFAULT_ZOOM_INDEX];
    zoomModel.setSelectedKey(new Double(ZOOM_FACTORS[DEFAULT_ZOOM_INDEX]));

    HashMap actions = PreviewPaneUtilities.loadActions(swingGuiContext);
    buildMenu(actions);
    if (toolBar != null)
    {
      toolbarHolder.remove(toolBar);
    }
    toolBar = buildToolbar(actions);
    if (toolBar != null)
    {
      toolbarHolder.add(toolBar, BorderLayout.NORTH);
    }

  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(final String title)
  {
    String oldTitle = this.title;
    this.title = title;
    firePropertyChange(TITLE_PROPERTY, oldTitle, title);
  }

  public double[] getZoomFactors()
  {
    return (double[]) ZOOM_FACTORS.clone();
  }

  public boolean isPaginating()
  {
    return paginating;
  }

  public void setPaginating(final boolean paginating)
  {
    boolean oldPaginating = this.paginating;
    this.paginating = paginating;
    firePropertyChange(PAGINATING_PROPERTY, oldPaginating, paginating);
  }

  private synchronized void startPagination()
  {
    if (paginationWorker != null)
    {
      // make sure that old pagination handler does not run longer than
      // necessary..
      synchronized(paginationWorker)
      {
        paginationWorker.finish();
      }
      paginationWorker = null;
    }

    if (printReportProcessor != null)
    {
      printReportProcessor.close();
      printReportProcessor = null;
    }

    final ReportJob reportJob = getReportJob();
    printReportProcessor = new PrintReportProcessor(reportJob.derive());

    paginationWorker = new Worker();
    paginationWorker.setWorkload
        (new RepaginationRunnable(printReportProcessor));
  }

  public Object getNoReportDrawable()
  {
    return noReportDrawable;
  }

  public void setNoReportDrawable(final Object noReportDrawable)
  {
    this.noReportDrawable = noReportDrawable;
  }

  public Object getPaginatingDrawable()
  {
    return paginatingDrawable;
  }

  public void setPaginatingDrawable(final Object paginatingDrawable)
  {
    this.paginatingDrawable = paginatingDrawable;
  }

  protected void updateVisiblePage(int pageNumber)
  {
    //
    if (printReportProcessor == null)
    {
      throw new IllegalStateException();
    }

    // todo: This can be very expensive - so we better move this off the event-dispatcher
    final int pageIndex = getPageNumber() - 1;
    if (pageIndex < 0 || pageIndex >= printReportProcessor.getNumberOfPages())
    {
      drawablePanel.setDrawable(null);
      pageDrawable.setBackend(null);
    }
    else
    {
      final PageDrawable drawable = printReportProcessor.getPageDrawable(pageIndex);
      this.pageDrawable.setBackend(drawable);
      this.drawablePanel.setDrawableAsRawObject(pageDrawable);
    }
  }
}
