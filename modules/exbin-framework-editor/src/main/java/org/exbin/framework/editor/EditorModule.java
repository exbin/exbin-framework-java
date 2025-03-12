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
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.menu.api.GroupMenuContributionRule;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorProviderChangeListener;
import org.exbin.framework.editor.api.EditorProviderComponentListener;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.menu.api.PositionMenuContributionRule;
import org.exbin.framework.editor.action.CloseAllFilesAction;
import org.exbin.framework.editor.action.CloseFileAction;
import org.exbin.framework.editor.action.CloseOtherFilesAction;
import org.exbin.framework.editor.action.EditorActions;
import org.exbin.framework.options.api.OptionsGroup;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPageManagement;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;

/**
 * Framework editor module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditorModule implements EditorModuleApi {

    private final List<EditorProvider> editors = new ArrayList<>();
    private final List<EditorProviderChangeListener> changeListeners = new ArrayList<>();
    private final List<EditorProviderComponentListener> componentListeners = new ArrayList<>();
    private final Map<String, List<EditorProvider>> pluginEditorsMap = new HashMap<>();
    private EditorProvider editorProvider = null;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener = null;
    private ResourceBundle resourceBundle;

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
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileModule.setFileOperations(editorProvider);

        this.editorProvider = editorProvider;

        for (EditorProviderChangeListener listener : changeListeners) {
            listener.providerChanged(editorProvider);
        }
        
        if (editorProvider instanceof DefaultMultiEditorProvider) {
            notifyEditorComponentCreated(editorProvider.getEditorComponent());
        }
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
        if (editorProvider instanceof ClipboardActionsHandler) {
            ((ClipboardActionsHandler) editorProvider).setUpdateListener(() -> {
                if (this.editorProvider == editorProvider) {
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
        return editorProvider.getEditorComponent();
    }

    @Override
    public void notifyEditorComponentCreated(JComponent component) {
        for (EditorProviderComponentListener listener : componentListeners) {
            listener.componentCreated(component);
        }
    }

    @Override
    public void registerMenuFileCloseActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(ActionConsts.FILE_SUBMENU_ID);
        MenuContribution contribution = mgmt.registerMenuGroup(FileModuleApi.FILE_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(createCloseFileAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(FileModuleApi.FILE_MENU_GROUP_ID));
    }

    @Nonnull
    @Override
    public CloseFileAction createCloseFileAction() {
        CloseFileAction closeFileAction = new CloseFileAction();
        ensureSetup();
        closeFileAction.setup(resourceBundle);
        return closeFileAction;
    }

    @Nonnull
    @Override
    public CloseAllFilesAction createCloseAllFilesAction() {
        CloseAllFilesAction closeAllFilesAction = new CloseAllFilesAction();
        ensureSetup();
        closeAllFilesAction.setup(resourceBundle);
        return closeAllFilesAction;
    }

    @Nonnull
    @Override
    public CloseOtherFilesAction createCloseOtherFilesAction() {
        CloseOtherFilesAction closeOtherFilesAction = new CloseOtherFilesAction();
        ensureSetup();
        closeOtherFilesAction.setup(resourceBundle);
        return closeOtherFilesAction;
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
    public void addEditorProviderChangeListener(EditorProviderChangeListener listener) {
        changeListeners.add(listener);
    }

    @Override
    public void removeEditorProviderChangeListener(EditorProviderChangeListener listener) {
        changeListeners.remove(listener);
    }

    @Override
    public void addEditorProviderComponentListener(EditorProviderComponentListener listener) {
        componentListeners.add(listener);
    }

    @Override
    public void removeEditorProviderComponentListener(EditorProviderComponentListener listener) {
        componentListeners.remove(listener);
    }

    @Override
    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(MODULE_ID);

        OptionsGroup editorOptionsGroup = optionsModule.createOptionsGroup("editor", getResourceBundle());
        optionsPageManagement.registerGroup(editorOptionsGroup);
    }
}
