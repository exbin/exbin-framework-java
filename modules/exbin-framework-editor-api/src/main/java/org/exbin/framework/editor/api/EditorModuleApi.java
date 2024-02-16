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
 * XBUP framework editor module api interface.
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
     * Registers undo handler for undo management to editor.
     */
    void registerUndoHandler();

    @Nonnull
    AbstractAction createCloseFileAction();

    @Nonnull
    AbstractAction createCloseAllFilesAction();

    @Nonnull
    AbstractAction createCloseOtherFilesAction();

    @Nonnull
    EditorActionsApi getEditorActions();

    void registerMenuFileCloseActions();
}
