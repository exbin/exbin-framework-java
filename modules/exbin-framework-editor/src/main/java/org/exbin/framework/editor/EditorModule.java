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
package org.exbin.framework.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.operation.undo.api.OperationUndoModuleApi;
import org.exbin.framework.operation.undo.api.UndoActionsHandler;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.editor.action.CloseAllFileAction;
import org.exbin.framework.editor.action.CloseFileAction;
import org.exbin.framework.editor.action.CloseOtherFileAction;
import org.exbin.framework.editor.action.EditorActions;
import org.exbin.framework.file.api.FileDependentAction;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.operation.undo.api.UndoFileHandler;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * XBUP framework editor module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditorModule implements EditorModuleApi {

    private final List<EditorProvider> editors = new ArrayList<>();
    private final Map<String, List<EditorProvider>> pluginEditorsMap = new HashMap<>();
    private EditorProvider editorProvider = null;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener = null;
    private ResourceBundle resourceBundle;

    private CloseFileAction closeFileAction;
    private CloseAllFileAction closeAllFileAction;
    private CloseOtherFileAction closeOtherFileAction;
    private EditorActions editorActions;

    public EditorModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(EditorModule.class);
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
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.registerClipboardHandler(new ClipboardActionsHandler() {
                @Override
                public void performCut() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof ClipboardActionsHandler) {
                        ((ClipboardActionsHandler) fileHandler).performCut();
                    }
                }

                @Override
                public void performCopy() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof ClipboardActionsHandler) {
                        ((ClipboardActionsHandler) fileHandler).performCopy();
                    }
                }

                @Override
                public void performPaste() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof ClipboardActionsHandler) {
                        ((ClipboardActionsHandler) fileHandler).performPaste();
                    }
                }

                @Override
                public void performDelete() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof ClipboardActionsHandler) {
                        ((ClipboardActionsHandler) fileHandler).performDelete();
                    }
                }

                @Override
                public void performSelectAll() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof ClipboardActionsHandler) {
                        ((ClipboardActionsHandler) fileHandler).performSelectAll();
                    }
                }

                @Override
                public boolean isSelection() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof ClipboardActionsHandler) {
                        return ((ClipboardActionsHandler) fileHandler).isSelection();
                    }

                    return false;
                }

                @Override
                public boolean isEditable() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof ClipboardActionsHandler) {
                        return ((ClipboardActionsHandler) fileHandler).isEditable();
                    }

                    return false;
                }

                @Override
                public boolean canSelectAll() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof ClipboardActionsHandler) {
                        return ((ClipboardActionsHandler) fileHandler).canSelectAll();
                    }

                    return false;
                }

                @Override
                public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
                    clipboardActionsUpdateListener = updateListener;
                }

                @Override
                public boolean canPaste() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof ClipboardActionsHandler) {
                        return ((ClipboardActionsHandler) fileHandler).canPaste();
                    }

                    return true;
                }

                @Override
                public boolean canDelete() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof ClipboardActionsHandler) {
                        return ((ClipboardActionsHandler) fileHandler).canDelete();
                    }

                    return isEditable();
                }
            });
        }

        this.editorProvider = editorProvider;
    }

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
    public void registerEditor(String pluginId, final EditorProvider editorProvider) {
        if (editorProvider instanceof UndoActionsHandler) {
            ((UndoActionsHandler) editorProvider).setUndoUpdateListener(() -> {
                OperationUndoModuleApi undoModule = App.getModule(OperationUndoModuleApi.class);
                undoModule.updateUndoStatus();
            });
        }
        if (editorProvider instanceof ClipboardActionsHandler) {
            ((ClipboardActionsHandler) editorProvider).setUpdateListener(() -> {
                if (editorProvider == this.editorProvider) {
                    if (clipboardActionsUpdateListener != null) {
                        clipboardActionsUpdateListener.stateChanged();
                    }
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
        setEditorProvider(editorProvider);
    }

    @Nonnull
    @Override
    public JComponent getEditorComponent() {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileModule.setFileOperations(editorProvider);
        return editorProvider.getEditorComponent();
    }

    @Override
    public void registerUndoHandler() {
        OperationUndoModuleApi undoModule = App.getModule(OperationUndoModuleApi.class);
        if (editorProvider instanceof UndoFileHandler) {
            undoModule.setUndoHandler(((UndoFileHandler) editorProvider).getUndoHandler());
        }
    }

    @Override
    public void registerMenuFileCloseActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuGroup(WindowModuleApi.FILE_MENU_ID, new MenuGroup(FileModuleApi.FILE_MENU_GROUP_ID, new MenuPosition(PositionMode.TOP)));
        actionModule.registerMenuItem(WindowModuleApi.FILE_MENU_ID, MODULE_ID, getCloseFileAction(), new MenuPosition(FileModuleApi.FILE_MENU_GROUP_ID));
    }

    @Nonnull
    @Override
    public CloseFileAction getCloseFileAction() {
        if (closeFileAction == null) {
            closeFileAction = new CloseFileAction();
            ensureSetup();
            closeFileAction.setup(resourceBundle, (MultiEditorProvider) editorProvider);
        }
        return closeFileAction;
    }

    @Nonnull
    @Override
    public CloseAllFileAction getCloseAllFileAction() {
        if (closeAllFileAction == null) {
            closeAllFileAction = new CloseAllFileAction();
            ensureSetup();
            closeAllFileAction.setup(resourceBundle, (MultiEditorProvider) editorProvider);
        }
        return closeAllFileAction;
    }

    @Nonnull
    @Override
    public CloseOtherFileAction getCloseOtherFileAction() {
        if (closeOtherFileAction == null) {
            closeOtherFileAction = new CloseOtherFileAction();
            ensureSetup();
            closeOtherFileAction.setup(resourceBundle, (MultiEditorProvider) editorProvider);
        }
        return closeOtherFileAction;
    }

    @Nonnull
    @Override
    public EditorActions getEditorActions() {
        if (editorActions == null) {
            editorActions = new EditorActions();
            ensureSetup();
            editorActions.setup(resourceBundle, (MultiEditorProvider) editorProvider);
        }
        return editorActions;
    }

    @Override
    public void updateActionStatus() {
        FileDependentAction[] fileDepActions = new FileDependentAction[]{
            closeFileAction, closeAllFileAction, closeOtherFileAction
        };

        for (FileDependentAction fileDepAction : fileDepActions) {
            if (fileDepAction != null) {
                fileDepAction.updateForActiveFile();
            }
        }
    }
}
