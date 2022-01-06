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
package org.exbin.framework.tabeditor.api;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.MultiEditorProvider;

/**
 * Interface for editor view handling.
 *
 * @version 0.2.0 2016/08/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface EditorViewHandling {

    /**
     * Adds new editor view.
     *
     * @param editorProvider editor provider
     */
    void addEditorView(EditorProvider editorProvider);

    /**
     * Removes and drops editor view.
     *
     * @param editorProvider editor provider
     */
    void removeEditorView(EditorProvider editorProvider);

    /**
     * Updates editor view. Specificaly name and modified state.
     *
     * @param editorProvider editor provider
     */
    void updateEditorView(EditorProvider editorProvider);

    /**
     * Sets multiple tabs editor handler.
     *
     * @param multiEditor multi editor provider
     */
    void setMultiEditorProvider(MultiEditorProvider multiEditor);
}
