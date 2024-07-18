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
package org.exbin.framework.file.api.utils;

import java.awt.event.ActionEvent;
import java.io.File;
import org.exbin.framework.file.api.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;

/**
 * Test implementation of file module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TestFileModule implements FileModuleApi {

    @Override
    public void addFileType(FileType fileType) {
    }

    @Override
    public Collection<FileType> getFileTypes() {
        return new ArrayList<>();
    }

    @Override
    public void setFileOperations(FileOperations fileOperations) {
    }

    @Override
    public void registerMenuFileHandlingActions() {
    }

    @Override
    public void registerToolBarFileHandlingActions() {
    }

    @Override
    public void registerCloseListener() {
    }

    @Override
    public void loadFromFile(URI fileUri) {
    }

    @Override
    public void loadFromFile(String filename) {
    }

    @Override
    public void registerRecenFilesMenuActions() {
    }

    @Nonnull
    @Override
    public AbstractAction createNewFileAction() {
        return new AbstractAction("New") {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new IllegalStateException();
            }
        };
    }

    @Nonnull
    @Override
    public AbstractAction createOpenFileAction() {
        return new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new IllegalStateException();
            }
        };
    }

    @Nonnull
    @Override
    public AbstractAction createSaveFileAction() {
        return new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new IllegalStateException();
            }
        };
    }

    @Nonnull
    @Override
    public AbstractAction createSaveAsFileAction() {
        return new AbstractAction("Save As") {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new IllegalStateException();
            }
        };
    }

    @Nonnull
    @Override
    public FileActionsApi getFileActions() {
        return new FileActionsApi() {
            @Override
            public void openFile(LoadableFileHandler fileHandler, FileTypes fileTypes, UsedDirectoryApi usedDirectory) {
                throw new IllegalStateException();
            }

            @Override
            public void saveFile(FileHandler fileHandler, FileTypes fileTypes, UsedDirectoryApi usedDirectory) {
                throw new IllegalStateException();
            }

            @Override
            public void saveAsFile(FileHandler fileHandler, FileTypes fileTypes, UsedDirectoryApi usedDirectory) {
                throw new IllegalStateException();
            }

            @Override
            public boolean showAskForSaveDialog(FileHandler fileHandler, FileTypes fileTypes, UsedDirectoryApi usedDirectory) {
                throw new IllegalStateException();
            }

            @Override
            public boolean showAskToOverwrite() {
                throw new IllegalStateException();
            }

            @Override
            public FileActionsApi.OpenFileResult showOpenFileDialog(FileTypes fileTypes, UsedDirectoryApi usedDirectory) {
                throw new IllegalStateException();
            }

            @Override
            public FileActionsApi.OpenFileResult showOpenFileDialog(FileTypes fileTypes, File selectedFile, UsedDirectoryApi usedDirectory) {
                throw new IllegalStateException();
            }

            @Override
            public FileActionsApi.OpenFileResult showSaveFileDialog(FileTypes fileTypes, UsedDirectoryApi usedDirectory) {
                throw new IllegalStateException();
            }

            @Override
            public FileActionsApi.OpenFileResult showSaveFileDialog(FileTypes fileTypes, File selectedFile, UsedDirectoryApi usedDirectory) {
                throw new IllegalStateException();
            }
        };
    }

    @Override
    public void updateRecentFilesList(URI fileUri, FileType fileType) {
    }
}
