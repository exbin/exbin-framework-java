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
package org.exbin.framework.editor.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for editor module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface EditorModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(EditorModuleApi.class);

    /**
     * Registers new editor.
     *
     * @param pluginId plugin identifier
     * @param editorProvider editor provider
     */
    void registerEditor(String pluginId, EditorProvider editorProvider);

    /**
     * Returns main component for editors handling.
     *
     * @return panel component
     */
    @Nonnull
    JComponent getEditorComponent();

    /**
     * Notifies editor component created.
     *
     * @param component editor component
     */
    void notifyEditorComponentCreated(JComponent component);

    /**
     * Creates close file action.
     *
     * @return close file action
     */
    @Nonnull
    AbstractAction createCloseFileAction();

    /**
     * Creates close all files action.
     *
     * @return close all files action
     */
    @Nonnull
    AbstractAction createCloseAllFilesAction();

    /**
     * Creates close other files action.
     *
     * @return close other files action
     */
    @Nonnull
    AbstractAction createCloseOtherFilesAction();

    /**
     * Return editor actions.
     *
     * @return editor actions
     */
    @Nonnull
    EditorActionsApi getEditorActions();

    /**
     * Registers menu file close actions.
     */
    void registerMenuFileCloseActions();

    /**
     * Adds change listener.
     *
     * @param listener listener
     */
    void addEditorProviderChangeListener(EditorProviderChangeListener listener);

    /**
     * Removes change listener.
     *
     * @param listener listener
     */
    void removeEditorProviderChangeListener(EditorProviderChangeListener listener);

    /**
     * Adds component listener.
     *
     * @param listener listener
     */
    void addEditorProviderComponentListener(EditorProviderComponentListener listener);

    /**
     * Removes component listener.
     *
     * @param listener listener
     */
    void removeEditorProviderComponentListener(EditorProviderComponentListener listener);
}
