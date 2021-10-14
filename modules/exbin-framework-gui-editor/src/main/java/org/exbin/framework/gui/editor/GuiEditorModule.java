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
package org.exbin.framework.gui.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.GuiEditorModuleApi;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.gui.undo.api.GuiUndoModuleApi;
import org.exbin.framework.gui.undo.api.UndoActionsHandler;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.gui.action.api.GuiActionModuleApi;
import org.exbin.framework.gui.action.api.MenuGroup;
import org.exbin.framework.gui.action.api.MenuPosition;
import org.exbin.framework.gui.action.api.PositionMode;
import org.exbin.framework.gui.editor.action.CloseAllFileAction;
import org.exbin.framework.gui.editor.action.CloseFileAction;
import org.exbin.framework.gui.editor.action.CloseOtherFileAction;
import org.exbin.framework.gui.file.api.FileDependentAction;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.undo.api.UndoFileHandler;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * XBUP framework editor module.
 *
 * @version 0.2.2 2021/10/06
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiEditorModule implements GuiEditorModuleApi {

    private XBApplication application;
    private final List<EditorProvider> editors = new ArrayList<>();
    private final Map<String, List<EditorProvider>> pluginEditorsMap = new HashMap<>();
    private EditorProvider editorProvider = null;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener = null;
    private ResourceBundle resourceBundle;

    private CloseFileAction closeFileAction;
    private CloseAllFileAction closeAllFileAction;
    private CloseOtherFileAction closeOtherFileAction;

    public GuiEditorModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(GuiEditorModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    private void setEditorProvider(EditorProvider editorProvider) {
        if (this.editorProvider == null) {
            GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
            actionModule.registerClipboardHandler(new ClipboardActionsHandler() {
                @Override
                public void performCut() {
                    if (editorProvider instanceof ClipboardActionsHandler) {
                        ((ClipboardActionsHandler) editorProvider).performCut();
                    }
                }

                @Override
                public void performCopy() {
                    if (editorProvider instanceof ClipboardActionsHandler) {
                        ((ClipboardActionsHandler) editorProvider).performCopy();
                    }
                }

                @Override
                public void performPaste() {
                    if (editorProvider instanceof ClipboardActionsHandler) {
                        ((ClipboardActionsHandler) editorProvider).performPaste();
                    }
                }

                @Override
                public void performDelete() {
                    if (editorProvider instanceof ClipboardActionsHandler) {
                        ((ClipboardActionsHandler) editorProvider).performDelete();
                    }
                }

                @Override
                public void performSelectAll() {
                    if (editorProvider instanceof ClipboardActionsHandler) {
                        ((ClipboardActionsHandler) editorProvider).performSelectAll();
                    }
                }

                @Override
                public boolean isSelection() {
                    if (editorProvider instanceof ClipboardActionsHandler) {
                        return ((ClipboardActionsHandler) editorProvider).isSelection();
                    }

                    return false;
                }

                @Override
                public boolean isEditable() {
                    if (editorProvider instanceof ClipboardActionsHandler) {
                        return ((ClipboardActionsHandler) editorProvider).isEditable();
                    }

                    return false;
                }

                @Override
                public boolean canSelectAll() {
                    if (editorProvider instanceof ClipboardActionsHandler) {
                        return ((ClipboardActionsHandler) editorProvider).canSelectAll();
                    }

                    return false;
                }

                @Override
                public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
                    clipboardActionsUpdateListener = updateListener;
                }

                @Override
                public boolean canPaste() {
                    if (editorProvider instanceof ClipboardActionsHandler) {
                        return ((ClipboardActionsHandler) editorProvider).canPaste();
                    }

                    return true;
                }

                @Override
                public boolean canDelete() {
                    if (editorProvider instanceof ClipboardActionsHandler) {
                        return ((ClipboardActionsHandler) editorProvider).canDelete();
                    }

                    return isEditable();
                }
            });
        }

        this.editorProvider = editorProvider;
    }

    @Override
    public void unregisterModule(String moduleId) {
        List<EditorProvider> pluginEditors = pluginEditorsMap.get(moduleId);
        if (pluginEditors != null) {
            for (EditorProvider editor : pluginEditors) {
                editors.remove(editor);
            }
            pluginEditorsMap.remove(moduleId);
        }
    }

    @Override
    public void registerMultiEditor(String pluginId, final MultiEditorProvider editorProvider) {
        registerEditor(pluginId, editorProvider);
        setEditorProvider(editorProvider);
    }

    @Override
    public void registerEditor(String pluginId, final EditorProvider editorProvider) {
        if (editorProvider instanceof UndoActionsHandler) {
            ((UndoActionsHandler) editorProvider).setUndoUpdateListener(() -> {
                GuiUndoModuleApi undoModule = application.getModuleRepository().getModuleByInterface(GuiUndoModuleApi.class);
                undoModule.updateUndoStatus();
            });
        }
        if (editorProvider instanceof ClipboardActionsHandler) {
            ((ClipboardActionsHandler) editorProvider).setUpdateListener(() -> {
                if (editorProvider == this.editorProvider) {
                    clipboardActionsUpdateListener.stateChanged();
                }
            });
        }
        editors.add(editorProvider);
        List<EditorProvider> pluginEditors = pluginEditorsMap.get(pluginId);
        if (pluginEditors == null) {
            pluginEditors = new ArrayList<>();
            pluginEditorsMap.put(pluginId, pluginEditors);
        }

        pluginEditors.add(editorProvider);
        this.editorProvider = editorProvider;
    }

    @Nonnull
    @Override
    public JComponent getEditorComponent() {
        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
        fileModule.setFileOperations(editorProvider);
        return editorProvider.getEditorComponent();
    }

    @Override
    public void registerUndoHandler() {
        GuiUndoModuleApi undoModule = application.getModuleRepository().getModuleByInterface(GuiUndoModuleApi.class);
        if (editorProvider instanceof UndoFileHandler) {
            undoModule.setUndoHandler(((UndoFileHandler) editorProvider).getUndoHandler());
        }
    }

    @Override
    public void registerMenuFileCloseActions() {
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuGroup(GuiFrameModuleApi.FILE_MENU_ID, new MenuGroup(GuiFileModuleApi.FILE_MENU_GROUP_ID, new MenuPosition(PositionMode.TOP)));
        actionModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, getCloseFileAction(), new MenuPosition(GuiFileModuleApi.FILE_MENU_GROUP_ID));
    }

    @Nonnull
    @Override
    public CloseFileAction getCloseFileAction() {
        if (closeFileAction == null) {
            closeFileAction = new CloseFileAction();
            ensureSetup();
            closeFileAction.setup(application, resourceBundle, (MultiEditorProvider) editorProvider);
        }
        return closeFileAction;
    }

    @Nonnull
    @Override
    public CloseAllFileAction getCloseAllFileAction() {
        if (closeAllFileAction == null) {
            closeAllFileAction = new CloseAllFileAction();
            ensureSetup();
            closeAllFileAction.setup(application, resourceBundle, (MultiEditorProvider) editorProvider);
        }
        return closeAllFileAction;
    }

    @Nonnull
    @Override
    public CloseOtherFileAction getCloseOtherFileAction() {
        if (closeOtherFileAction == null) {
            closeOtherFileAction = new CloseOtherFileAction();
            ensureSetup();
            closeOtherFileAction.setup(application, resourceBundle, (MultiEditorProvider) editorProvider);
        }
        return closeOtherFileAction;
    }

    @Override
    public void updateActionStatus() {
        FileDependentAction[] fileDepActions = new FileDependentAction[]{
            closeFileAction, closeAllFileAction, closeOtherFileAction
        };

        for (int i = 0; i < fileDepActions.length; i++) {
            FileDependentAction fileDepAction = fileDepActions[i];
            if (fileDepAction != null) {
                fileDepAction.updateForActiveFile();
            }
        }
    }
}
