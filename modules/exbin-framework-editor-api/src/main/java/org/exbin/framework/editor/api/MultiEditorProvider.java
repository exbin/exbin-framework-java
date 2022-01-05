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
package org.exbin.framework.editor.api;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.file.api.FileHandler;

/**
 * Framework multi-file editor interface.
 *
 * @version 0.2.2 2021/10/23
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface MultiEditorProvider extends EditorProvider {

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
     * Returns all file handlers.
     *
     * @return list of file handlers
     */
    @Nonnull
    List<FileHandler> getFileHandlers();

    /**
     * Returns name for the given file handler.
     *
     * @param fileHandler file handler
     * @return name
     */
    @Nonnull
    String getName(FileHandler fileHandler);

    /**
     * Calls file saving operation.
     *
     * @param fileHandler file handler
     */
    void saveFile(FileHandler fileHandler);

    /**
     * Calls file saving as operation.
     *
     * @param fileHandler file handler
     */
    void saveAsFile(FileHandler fileHandler);

    /**
     * Closes active file.
     */
    void closeFile();

    /**
     * Closes specified file.
     *
     * @param fileHandler file handler
     */
    void closeFile(FileHandler fileHandler);

    /**
     * Closes other opened files except given file.
     *
     * @param fileHandler file handler
     */
    void closeOtherFiles(FileHandler fileHandler);

    /**
     * Closes all opened files.
     */
    void closeAllFiles();

    /**
     * Saves all opened files.
     */
    void saveAllFiles();

    /**
     * Interface for changes of active file in editor listener.
     */
    public static interface ActiveFileChangeListener {

        void activeFileChanged(@Nullable FileHandler fileHandler);
    }
}
