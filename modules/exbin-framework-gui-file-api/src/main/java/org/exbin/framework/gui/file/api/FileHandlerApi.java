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
package org.exbin.framework.gui.file.api;

import java.net.URI;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for file handling actions.
 *
 * @version 0.2.0 2021/06/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface FileHandlerApi {

    /**
     * Loads file from given filename.
     *
     * @param fileUri file Uri
     * @param fileType file type
     */
    void loadFromFile(URI fileUri, FileType fileType);

    /**
     * Saves file to given filename.
     *
     * @param fileUri file Uri
     * @param fileType file type
     */
    void saveToFile(URI fileUri, FileType fileType);

    /**
     * Returns currect file URI.
     *
     * @return URI
     */
    @Nonnull
    Optional<URI> getFileUri();

    /**
     * Returns current filename.
     *
     * Typically file name with extension is returned.
     *
     * @return filename
     */
    @Nonnull
    Optional<String> getFileName();

    /**
     * Returns currently used filetype.
     *
     * @return fileType file type
     */
    @Nonnull
    Optional<FileType> getFileType();

    /**
     * Sets current used filetype.
     *
     * @param fileType file type
     */
    void setFileType(FileType fileType);

    /**
     * Creates new file.
     */
    void newFile();

    /**
     * Returns flag if file in this panel was modified since last saving.
     *
     * @return true if file was modified
     */
    boolean isModified();
}
