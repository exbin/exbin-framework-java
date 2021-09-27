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
package org.exbin.framework.gui.editor.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;

/**
 * XBUP framework editor module api interface.
 *
 * @version 0.2.2 2021/09/27
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface GuiEditorModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(GuiEditorModuleApi.class);

    /**
     * Registers new editor.
     *
     * @param pluginId plugin identifier
     * @param editorProvider editor provider
     */
    void registerEditor(String pluginId, EditorProvider editorProvider);

    /**
     * Registers multi-file editor.
     *
     * @param pluginId plugin identifier
     * @param editorProvider editor provider
     */
    void registerMultiEditor(String pluginId, final MultiEditorProvider editorProvider);

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
}
