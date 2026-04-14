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
package org.exbin.jaguif.file;

import java.awt.Component;
import org.exbin.jaguif.file.api.FileDialogsProvider;
import java.io.File;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.exbin.jaguif.file.api.FileType;
import org.exbin.jaguif.file.api.FileTypes;
import org.exbin.jaguif.file.api.OpenFileResult;
import org.exbin.jaguif.file.api.UsedDirectoryApi;

/**
 * Swing file dialogs provider.
 */
@ParametersAreNonnullByDefault
public class SwingFileDialogsProvider implements FileDialogsProvider {

    public static final String ALL_FILES_FILTER = "AllFilesFilter";
    protected final ResourceBundle resourceBundle;

    public SwingFileDialogsProvider(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    @Override
    public String getProviderName() {
        return resourceBundle.getString("fileDialogs.swing");
    }

    @Nonnull
    @Override
    public OpenFileResult showOpenFileDialog(Component parentComponent, FileTypes fileTypes, @Nullable File selectedFile, @Nullable UsedDirectoryApi usedDirectory, @Nullable String dialogName) {
        JFileChooser openFileChooser = new JFileChooser();
        setupFileFilters(openFileChooser, fileTypes);
        if (usedDirectory != null) {
            openFileChooser.setCurrentDirectory(usedDirectory.getLastUsedDirectory().orElse(null));
        }
        if (selectedFile != null) {
            openFileChooser.setSelectedFile(selectedFile);
        }
        if (dialogName != null) {
            openFileChooser.setDialogTitle(dialogName);
        }
        int dialogResult = openFileChooser.showOpenDialog(parentComponent);
        FileFilter fileFilter = openFileChooser.getFileFilter();
        return new OpenFileResult(
                dialogResult, openFileChooser.getSelectedFile(),
                fileFilter instanceof FileType ? (FileType) fileFilter : null
        );
    }

    @Override
    public OpenFileResult showSaveFileDialog(Component parentComponent, FileTypes fileTypes, @Nullable File selectedFile, @Nullable UsedDirectoryApi usedDirectory, @Nullable String dialogName) {
        JFileChooser saveFileChooser = new JFileChooser();
        saveFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        setupFileFilters(saveFileChooser, fileTypes);
        if (usedDirectory != null) {
            saveFileChooser.setCurrentDirectory(usedDirectory.getLastUsedDirectory().orElse(null));
        }
        if (selectedFile != null) {
            saveFileChooser.setSelectedFile(selectedFile);
        }
        if (dialogName != null) {
            saveFileChooser.setDialogTitle(dialogName);
        }
        int dialogResult = saveFileChooser.showSaveDialog(parentComponent);
        FileFilter fileFilter = saveFileChooser.getFileFilter();
        return new OpenFileResult(
                dialogResult, saveFileChooser.getSelectedFile(),
                fileFilter instanceof FileType ? (FileType) fileFilter : null
        );
    }

    public void setupFileFilters(JFileChooser fileChooser, FileTypes fileTypes) {
        fileChooser.setAcceptAllFileFilterUsed(false);
        for (FileType fileType : fileTypes.getFileTypes()) {
            fileChooser.addChoosableFileFilter((FileFilter) fileType);
        }

        if (fileTypes.allowAllFiles()) {
            fileChooser.addChoosableFileFilter(new AllFilesFilter());
        }
    }

    @ParametersAreNonnullByDefault
    public class AllFilesFilter extends FileFilter implements FileType {

        @Override
        public boolean accept(File file) {
            return true;
        }

        @Nonnull
        @Override
        public String getDescription() {
            return resourceBundle.getString("AllFilesFilter.description");
        }

        @Nonnull
        @Override
        public String getFileTypeId() {
            return ALL_FILES_FILTER;
        }
    }
}
