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

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.GuiEditorModuleApi;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.file.api.FileHandlingActionsApi;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.gui.undo.api.GuiUndoModuleApi;
import org.exbin.framework.gui.undo.api.UndoActionsHandler;
import org.exbin.xbup.operation.Command;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.operation.undo.XBUndoUpdateListener;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.gui.action.api.GuiActionModuleApi;

/**
 * XBUP framework editor module.
 *
 * @version 0.2.2 2021/09/26
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiEditorModule implements GuiEditorModuleApi {

    private XBApplication application;
    private final List<EditorProvider> editors = new ArrayList<>();
    private final Map<String, List<EditorProvider>> pluginEditorsMap = new HashMap<>();
    private EditorProvider editorProvider = null;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener = null;

    public GuiEditorModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;
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
    }

    @Override
    public Component getEditorPanel() {
        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
        FileHandlingActionsApi fileHandlingActions = fileModule.getFileHandlingActions();
        fileHandlingActions.setFileHandler(editorProvider);

        return editorProvider.getEditorComponent();
    }

    @Override
    public void registerUndoHandler() {
        GuiUndoModuleApi undoModule = application.getModuleRepository().getModuleByInterface(GuiUndoModuleApi.class);
        undoModule.setUndoHandler(new XBUndoHandler() {
            @Override
            public boolean canRedo() {
                if (editorProvider != null && editorProvider.getEditorComponent() instanceof UndoActionsHandler) {
                    return ((UndoActionsHandler) editorProvider.getEditorComponent()).canRedo();
                }

                return false;
            }

            @Override
            public boolean canUndo() {
                if (editorProvider != null && editorProvider.getEditorComponent() instanceof UndoActionsHandler) {
                    return ((UndoActionsHandler) editorProvider.getEditorComponent()).canUndo();
                }

                return false;
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void doSync() throws Exception {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void execute(Command command) throws Exception {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void addCommand(Command cmnd) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public List<Command> getCommandList() {
                return new ArrayList<>();
            }

            @Override
            public long getCommandPosition() {
                return 0;
            }

            @Override
            public long getMaximumUndo() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public long getSyncPoint() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public long getUndoMaximumSize() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public long getUsedSize() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void performRedo() throws Exception {
                if (editorProvider != null && editorProvider.getEditorComponent() instanceof UndoActionsHandler) {
                    ((UndoActionsHandler) editorProvider.getEditorComponent()).performRedo();
                    GuiUndoModuleApi undoModule = application.getModuleRepository().getModuleByInterface(GuiUndoModuleApi.class);
                    undoModule.updateUndoStatus();
                }
            }

            @Override
            public void performRedo(int count) throws Exception {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void performUndo() throws Exception {
                if (editorProvider != null && editorProvider.getEditorComponent() instanceof UndoActionsHandler) {
                    ((UndoActionsHandler) editorProvider.getEditorComponent()).performUndo();
                    GuiUndoModuleApi undoModule = application.getModuleRepository().getModuleByInterface(GuiUndoModuleApi.class);
                    undoModule.updateUndoStatus();
                }
            }

            @Override
            public void performUndo(int count) throws Exception {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setCommandPosition(long targetPosition) throws Exception {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setSyncPoint(long syncPoint) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setSyncPoint() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void addUndoUpdateListener(XBUndoUpdateListener listener) {
            }

            @Override
            public void removeUndoUpdateListener(XBUndoUpdateListener listener) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }
}
