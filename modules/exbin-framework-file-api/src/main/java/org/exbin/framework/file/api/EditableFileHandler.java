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
package org.exbin.framework.file.api;

import java.net.URI;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for editable file handling.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface EditableFileHandler extends FileHandler {

    /**
     * Clears content of the file.
     */
    void clearFile();

    /**
     * Returns flag if file in this panel was modified since last saving.
     *
     * @return true if file was modified
     */
    boolean isModified();

    /**
     * Returns true if save operation is possible.
     *
     * @return id
     */
    boolean canSave();

    /**
     * Performs saving of the file.
     */
    void saveFile();

    /**
     * Saves file to given filename.
     *
     * @param fileUri file Uri
     * @param fileType file type
     */
    void saveToFile(URI fileUri, @Nullable FileType fileType);
}
