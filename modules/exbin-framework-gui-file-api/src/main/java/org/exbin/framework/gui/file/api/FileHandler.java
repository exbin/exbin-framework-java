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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;

/**
 * Interface for file handling.
 *
 * @version 0.2.2 2021/10/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface FileHandler {

    /**
     * Returns unique identifier of the file.
     *
     * @return id
     */
    int getId();

    /**
     * Returns component for the file.
     *
     * @return component
     */
    @Nonnull
    JComponent getComponent();

    /**
     * Loads file from given filename.
     *
     * @param fileUri file Uri
     * @param fileType file type
     */
    void loadFromFile(URI fileUri, @Nullable FileType fileType);

    /**
     * Saves file to given filename.
     *
     * @param fileUri file Uri
     * @param fileType file type
     */
    void saveToFile(URI fileUri, @Nullable FileType fileType);

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
     * Sets currently used file type.
     *
     * @param fileType file type
     */
    void setFileType(@Nullable FileType fileType);

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