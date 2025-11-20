/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.docking.layout;

import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.action.predefined.CCloseAction;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.DefaultCDockable;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.docking.api.EditorViewHandling;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.docking.api.DockingModuleApi;

/**
 * Implementation of docking module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DockingLayoutModule implements DockingModuleApi {

    public static final String FILE_EXIT_GROUP_ID = MODULE_ID + ".exit";
    public static final String VIEW_BARS_GROUP_ID = MODULE_ID + ".view";
    public static final String EDITOR_FACTORY_ID = "editor";

    private ResourceBundle resourceBundle;
    private CControl control = null;
    private CGrid grid = null;

    private EditorFactory factory = null;
    private CLocation editorLocation = null;
    private MultiEditorProvider multiEditorProvider = null;
    private final Map<EditorProvider, EditorCDockable> editorMap = new HashMap<>();

    public DockingLayoutModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(this.getClass());
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
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
//        view.putAction(CDockable.ACTION_KEY_CLOSE, new CustomCloseAction(control));
        view.setLocation(CLocation.maximized());
        view.setCloseable(true);
        CStation<?> station = view.asStation();
        if (station instanceof StackDockStation) {
            ((StackDockStation) station).setTabPlacement(TabPlacement.TOP_OF_DOCKABLE);
        }

        grid = new CGrid(control);
        grid.add(0, 0, 1, 1, view);
        area.deploy(grid);
    }

    @Nonnull
    @Override
    public EditorViewHandling getEditorViewHandling() {
        return new EditorViewHandling() {

            @Override
            public void addEditorView(final EditorProvider editorProvider) {
                if (factory == null) {
                    factory = new EditorFactory();
                    control.addMultipleDockableFactory(EDITOR_FACTORY_ID, factory);
                    editorLocation = CLocation.base();

                    control.addFocusListener(new CFocusListener() {
                        @Override
                        public void focusGained(CDockable dockable) {
                            EditorProvider editor = (EditorProvider) ((EditorCDockable) dockable).getContent();
                            if (editor != null) {
                                //multiEditorProvider.setActiveEditor(editor);
                            }
                        }

                        @Override
                        public void focusLost(CDockable dockable) {
                        }
                    });
                }

                CContentArea area = control.getContentArea();

                EditorCDockable view = new EditorCDockable(factory);
                view.setContent((Component) editorProvider);
                view.addAction(new CCloseAction(control));
//                view.putAction(CDockable.ACTION_KEY_CLOSE, new CustomCloseAction(control));
                view.setLocation(editorLocation);
                view.addVetoClosingListener(new CVetoClosingListener() {
                    @Override
                    public void closing(CVetoClosingEvent event) {
                        // TODO attempt to release file
                        // event.event.cancel();
                    }

                    @Override
                    public void closed(CVetoClosingEvent event) {
                        for (int i = 0; i < event.getDockableCount(); i++) {
                            CDockable dockable = event.getDockable(i);
                            if (dockable instanceof EditorCDockable) {
                                EditorProvider editor = (EditorProvider) ((EditorCDockable) dockable).getContent();
                                Optional<FileHandler> activeFile = editor.getActiveFile();
                                if (activeFile.isPresent()) {
                                    multiEditorProvider.closeFile(activeFile.get());
                                }
                                removeEditorView(editorProvider);
                            }
                        }
                    }
                });

                view.setLocation(area.getCenterArea().asStation().getDropLocation());
                control.addDockable(view);
                view.setVisible(true);
                editorMap.put(editorProvider, view);
            }

            @Override
            public void removeEditorView(final EditorProvider editorProvider) {
                EditorCDockable dockable = editorMap.remove(editorProvider);
                if (dockable != null) {
                    dockable.setVisible(false);
                    control.removeDockable(dockable);
                    // TODO remove and release stuff
                }
            }

            @Override
            public void setMultiEditorProvider(MultiEditorProvider multiEditor) {
                multiEditorProvider = multiEditor;
            }

            @Override
            public void updateEditorView(EditorProvider editorProvider) {
                EditorCDockable dockable = editorMap.get(editorProvider);
                if (dockable != null) {
                    dockable.update();
                }
            }
        };
    }

//    @TabDockAction
//    @EclipseTabDockAction
//    private class CustomCloseAction extends CCloseAction {
//
//        public CustomCloseAction(CControl control) {
//            super(control);
//        }
//
//        @Override
//        public void close(CDockable dockable) {
//            super.close(dockable);
//            CDockable focusedView = control.getFocusedCDockable();
//            if (focusedView != null) {
//                EditorProvider editor = editorMap.get(focusedView);
//                multiEditorProvider.closeFile(editor);
//            }
//        }
//    }
}
