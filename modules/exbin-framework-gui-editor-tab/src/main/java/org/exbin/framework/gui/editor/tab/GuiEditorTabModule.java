/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.gui.editor.tab;

import java.awt.Component;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JLabel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.gui.editor.tab.api.GuiEditorTabModuleApi;
import org.exbin.framework.gui.editor.tab.api.EditorViewHandling;

/**
 * Implementation of XBUP framework multi-tab editor module.
 *
 * @version 0.2.0 2016/08/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiEditorTabModule implements GuiEditorTabModuleApi {

    public static final String FILE_EXIT_GROUP_ID = MODULE_ID + ".exit";
    public static final String VIEW_BARS_GROUP_ID = MODULE_ID + ".view";
    public static final String TAB_EDITOR_FACTORY_ID = "tabEditor";

    private XBApplication application;
    private ResourceBundle resourceBundle;

//    private EditorFactory factory = null;
    private MultiEditorProvider multiEditorProvider = null;

    public GuiEditorTabModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;

        resourceBundle = LanguageUtils.getResourceBundleByClass(this.getClass());
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Override
    public Component getDockingPanel() {
        return new JLabel("TEST");
    }

    @Override
    public void addDockingView(Component component) {
    }

    @Override
    public EditorViewHandling getEditorViewHandling() {
        return new EditorViewHandling() {

            @Override
            public void addEditorView(final EditorProvider editorProvider) {
            }

            @Override
            public void removeEditorView(final EditorProvider editorProvider) {
            }

            @Override
            public void setMultiEditorProvider(MultiEditorProvider multiEditor) {
                multiEditorProvider = multiEditor;
            }

            @Override
            public void updateEditorView(EditorProvider editorProvider) {
//                EditorCDockable dockable = editorMap.get(editorProvider);
//                if (dockable != null) {
//                    dockable.update();
//                }
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
