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
package org.exbin.framework.file;

import org.exbin.framework.file.api.FileDialogsProvider;
import java.io.File;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.App;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileTypes;
import org.exbin.framework.file.api.OpenFileResult;
import org.exbin.framework.file.api.UsedDirectoryApi;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * Swing file dialogs provider.
 *
 * @author ExBin Project (https://exbin.org)
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
    public OpenFileResult showOpenFileDialog(FileTypes fileTypes, @Nullable File selectedFile, @Nullable UsedDirectoryApi usedDirectory) {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        JFileChooser openFileChooser = new JFileChooser();
        setupFileFilters(openFileChooser, fileTypes);
        if (usedDirectory != null) {
            openFileChooser.setCurrentDirectory(usedDirectory.getLastUsedDirectory().orElse(null));
        }
        if (selectedFile != null) {
            openFileChooser.setSelectedFile(selectedFile);
        }
        int dialogResult = openFileChooser.showOpenDialog(frameModule.getFrame());
        FileFilter fileFilter = openFileChooser.getFileFilter();
        return new OpenFileResult(
                dialogResult, openFileChooser.getSelectedFile(),
                fileFilter instanceof FileType ? (FileType) fileFilter : null
        );
    }

    @Override
    public OpenFileResult showSaveFileDialog(FileTypes fileTypes, @Nullable File selectedFile, @Nullable UsedDirectoryApi usedDirectory, @Nullable String dialogName) {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
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
        int dialogResult = saveFileChooser.showSaveDialog(frameModule.getFrame());
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
