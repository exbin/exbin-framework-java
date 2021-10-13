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

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.gui.file.api.FileHandler;

/**
 * Framework multitab editor interface.
 *
 * @version 0.2.2 2021/10/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface MultiEditorProvider extends EditorProvider {

    /**
     * Sets currently active editor provider.
     *
     * @param editorProvider editor provider
     */
    void setActiveEditor(EditorProvider editorProvider);

    /**
     * Adds active file change listener.
     *
     * @param listener listener
     */
    void addActiveFileChangeListener(ActiveFileChangeListener listener);

    /**
     * Removes active file change listener.
     *
     * @param listener listener
     */
    void removeActiveFileChangeListener(ActiveFileChangeListener listener);

    /**
     * Closes active file.
     */
    void closeFile();

    /**
     * Closes specified file.
     *
     * @param file file handler
     */
    void closeFile(FileHandler file);
}
