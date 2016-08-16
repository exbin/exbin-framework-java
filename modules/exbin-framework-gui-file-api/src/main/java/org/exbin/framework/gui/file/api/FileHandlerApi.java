/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.gui.file.api;

import java.net.URI;

/**
 * Interface for file handling actions.
 *
 * @version 0.2.0 2016/08/16
 * @author ExBin Project (http://exbin.org)
 */
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
    URI getFileUri();

    /**
     * Returns current filename.
     *
     * Typically file name with extension is returned.
     *
     * @return filename
     */
    String getFileName();

    /**
     * Returns currently used filetype.
     *
     * @return fileType file type
     */
    FileType getFileType();

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
