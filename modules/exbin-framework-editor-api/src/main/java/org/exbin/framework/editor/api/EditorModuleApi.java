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
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;

/**
 * XBUP framework editor module api interface.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface EditorModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(EditorModuleApi.class);

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
    AbstractAction getCloseFileAction();

    @Nonnull
    AbstractAction getCloseAllFileAction();

    @Nonnull
    AbstractAction getCloseOtherFileAction();

    @Nonnull
    EditorActionsApi getEditorActions();

    void registerMenuFileCloseActions();

    void updateActionStatus();
}
