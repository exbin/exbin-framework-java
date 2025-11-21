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
import org.exbin.framework.editor.api.EditorProviderChangeListener;
import org.exbin.framework.editor.api.EditorProviderComponentListener;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.action.api.clipboard.ClipboardStateListener;
import org.exbin.framework.action.api.clipboard.TextClipboardController;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.SettingsPageContribution;

/**
 * Framework editor module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditorModule implements EditorModuleApi {

    public static final String SETTINGS_PAGE_ID = "editor";
    private final List<EditorProvider> editors = new ArrayList<>();
    private final List<EditorProviderChangeListener> changeListeners = new ArrayList<>();
    private final List<EditorProviderComponentListener> componentListeners = new ArrayList<>();
    private final Map<String, List<EditorProvider>> pluginEditorsMap = new HashMap<>();
    private EditorProvider editorProvider = null;
    private ClipboardStateListener clipboardActionsUpdateListener = null;
    private ResourceBundle resourceBundle;

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
            actionModule.registerClipboardHandler(new TextClipboardController() {
                @Override
                public void performCut() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof TextClipboardController) {
                        ((TextClipboardController) fileHandler).performCut();
                    }
                }

                @Override
                public void performCopy() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof TextClipboardController) {
                        ((TextClipboardController) fileHandler).performCopy();
                    }
                }

                @Override
                public void performPaste() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof TextClipboardController) {
                        ((TextClipboardController) fileHandler).performPaste();
                    }
                }

                @Override
                public void performDelete() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof TextClipboardController) {
                        ((TextClipboardController) fileHandler).performDelete();
                    }
                }

                @Override
                public void performSelectAll() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof TextClipboardController) {
                        ((TextClipboardController) fileHandler).performSelectAll();
                    }
                }

                @Override
                public boolean hasSelection() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof TextClipboardController) {
                        return ((TextClipboardController) fileHandler).hasSelection();
                    }

                    return false;
                }

                @Override
                public boolean isEditable() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof TextClipboardController) {
                        return ((TextClipboardController) fileHandler).isEditable();
                    }

                    return false;
                }

                @Override
                public boolean canSelectAll() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof TextClipboardController) {
                        return ((TextClipboardController) fileHandler).canSelectAll();
                    }

                    return false;
                }

                @Override
                public void setUpdateListener(ClipboardStateListener updateListener) {
                    clipboardActionsUpdateListener = updateListener;
                }

                @Override
                public boolean canPaste() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof TextClipboardController) {
                        return ((TextClipboardController) fileHandler).canPaste();
                    }

                    return true;
                }

                @Override
                public boolean canDelete() {
                    FileHandler fileHandler = editorProvider.getActiveFile().orElse(null);
                    if (fileHandler instanceof TextClipboardController) {
                        return ((TextClipboardController) fileHandler).canDelete();
                    }

                    return isEditable();
                }

                @Override
                public boolean hasDataToCopy() {
                    return hasSelection();
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
        if (editorProvider instanceof TextClipboardController) {
            ((TextClipboardController) editorProvider).setUpdateListener(() -> {
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
    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();
        
        SettingsPageContribution pageContribution = new SettingsPageContribution(SETTINGS_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(pageContribution);
    }
}
