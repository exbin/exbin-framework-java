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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JFileChooser;

/**
 * Interface for file types management.
 *
 * @version 0.2.1 2019/07/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface FileTypeManagement {

    /**
     * Adds file type into manager.
     *
     * FileType should extends javax.swing.filechooser.FileFilter.
     *
     * @param fileType file type
     */
    void addFileType(FileType fileType);

    /**
     * Opens file from given fileChooser with the respect to used file type.
     *
     * @param fileChooser file chooser
     * @return true if file opened successfuly
     */
    boolean openFile(JFileChooser fileChooser);

    /**
     * Opens file from given properties.
     *
     * @param path full path to file
     * @param fileTypeId file type ID
     * @return true if file opened successfuly
     */
    boolean openFile(String path, String fileTypeId);

    /**
     * Finishes last file operation.
     */
    void finish();

    /**
     * Saves file using given file chooser.
     *
     * @param saveFC file chooser
     * @return true if file saved successfuly
     */
    boolean saveFile(JFileChooser saveFC);

    /**
     * Saves file using last used filename.
     *
     * @return true if file saved successfuly
     */
    boolean saveFile();

    /**
     * Creates new file.
     */
    void newFile();

    /**
     * Gets window title.
     *
     * @return window title
     */
    @Nonnull
    String getWindowTitle();
}
