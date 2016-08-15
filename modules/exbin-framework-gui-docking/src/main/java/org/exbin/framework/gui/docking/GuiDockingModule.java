/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.gui.docking;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockAction;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.action.predefined.CCloseAction;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.DefaultCDockable;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.station.stack.action.TabDockAction;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.docking.api.EditorViewHandling;
import org.exbin.framework.gui.docking.api.GuiDockingModuleApi;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Implementation of XBUP framework docking module.
 *
 * @version 0.2.0 2016/08/13
 * @author ExBin Project (http://exbin.org)
 */
public class GuiDockingModule implements GuiDockingModuleApi {

    public static final String FILE_EXIT_GROUP_ID = MODULE_ID + ".exit";
    public static final String VIEW_BARS_GROUP_ID = MODULE_ID + ".view";

    private XBApplication application;
    private ResourceBundle resourceBundle;
    private CControl control = null;
    private CGrid grid = null;

    public GuiDockingModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;

        resourceBundle = ActionUtils.getResourceBundleByClass(this.getClass());
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Override
    public Component getDockingPanel() {
        if (control == null) {
            control = new CControl();
            control.setTheme(ThemeMap.KEY_SMOOTH_THEME);
            control.putProperty(StackDockStation.TAB_PLACEMENT, TabPlacement.TOP_OF_DOCKABLE);
            grid = new CGrid(control);
        }

        CContentArea area = control.getContentArea();
        return area;
    }

    @Override
    public void addDockingView(Component component) {
        CContentArea area = control.getContentArea();

        DefaultCDockable view = new DefaultCDockable();
        view.add(component);
        view.setTitleText("Test");
        view.setTitleShown(false);
        view.setSingleTabShown(true);
        view.addAction(new CCloseAction(control));
        view.putAction(CDockable.ACTION_KEY_CLOSE, new CustomCloseAction(control));
        view.setLocation(CLocation.maximized());
        view.setCloseable(true);
        CStation<?> station = view.asStation();
        if (station instanceof StackDockStation) {
            ((StackDockStation) station).setTabPlacement(TabPlacement.TOP_OF_DOCKABLE);
        }

        grid.add(0, 0, 1, 1, view);
        area.deploy(grid);
    }

    @Override
    public EditorViewHandling getEditorViewHandling() {
        return new EditorViewHandling() {

            private final Map<CDockable, EditorProvider> viewMap = new HashMap<>();

            @Override
            public void addEditorView(final EditorProvider editorProvider, final MultiEditorProvider multiEditorProvider) {
                CContentArea area = control.getContentArea();

                final DefaultCDockable view = new DefaultCDockable();
                view.add((Component) editorProvider);
                view.setTitleText("Test");
                view.setTitleShown(false);
                view.setSingleTabShown(true);
                view.setStickySwitchable(false);
                view.setMinimizable(false);
                view.addAction(new CCloseAction(control));
                view.putAction(CDockable.ACTION_KEY_CLOSE, new CustomCloseAction(control));
                view.setLocation(CLocation.maximized());
                view.setCloseable(true);
                control.addFocusListener(new CFocusListener() {
                    @Override
                    public void focusGained(CDockable dockable) {
                        EditorProvider editor = viewMap.get(dockable);
                        if (editor != null) {
                            multiEditorProvider.setActiveEditor(editor);
                        }
                    }

                    @Override
                    public void focusLost(CDockable dockable) {
                    }
                });

                grid.add(0, 0, 1, 1, view);
                area.deploy(grid);
                viewMap.put(view, editorProvider);
            }

            @Override
            public void removeEditorView() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @TabDockAction
    @EclipseTabDockAction
    private static class CustomCloseAction extends CCloseAction {

        public CustomCloseAction(CControl control) {
            super(control);
            CDockable focusedView = control.getFocusedCDockable();
            // TODO
        }
    }
}
