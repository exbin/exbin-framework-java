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
package org.exbin.framework.file.action;

import java.awt.Dialog;
import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.App;
import org.exbin.framework.file.FileModule;
import org.exbin.framework.file.api.FileActionsApi;
import org.exbin.framework.file.api.FileActionsApi.OpenFileResult;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileTypes;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.UsedDirectoryApi;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.file.api.EditableFileHandler;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.file.api.LoadableFileHandler;

/**
 * File actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FileActions implements FileActionsApi {

    public static final String ALL_FILES_FILTER = "AllFilesFilter";

    private ResourceBundle resourceBundle;

    public FileActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
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

    @Override
    public void openFile(@Nullable LoadableFileHandler fileHandler, FileTypes fileTypes, @Nullable UsedDirectoryApi usedDirectory) {
        if (fileHandler != null) {
            OpenFileResult openFileResult = FileActions.this.showOpenFileDialog(fileTypes, usedDirectory);
            if (openFileResult.dialogResult == JFileChooser.APPROVE_OPTION) {
                File selectedFile = Objects.requireNonNull(openFileResult.selectedFile);
                URI fileUri = selectedFile.toURI();
                fileHandler.loadFromFile(fileUri, openFileResult.fileType);
                if (usedDirectory != null) {
                    usedDirectory.setLastUsedDirectory(selectedFile.getParentFile());
                    usedDirectory.updateRecentFilesList(fileUri, openFileResult.fileType);
                }
            }
        }
    }

    @Nonnull
    @Override
    public OpenFileResult showOpenFileDialog(FileTypes fileTypes, @Nullable UsedDirectoryApi usedDirectory) {
        return showOpenFileDialog(fileTypes, null, usedDirectory);
    }

    @Nonnull
    @Override
    public OpenFileResult showOpenFileDialog(FileTypes fileTypes, @Nullable File selectedFile, @Nullable UsedDirectoryApi usedDirectory) {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        if (((FileModule) fileModule).isUseAwtDialogs()) {
            FileDialog fileDialog = new FileDialog(frameModule.getFrame());
            fileDialog.setMode(FileDialog.LOAD);
            fileDialog.setMultipleMode(false);
            FilenameFilter filter = (File file, String string) -> true;
            fileDialog.setFilenameFilter(filter);
            if (usedDirectory != null) {
                File lastUsedDirectory = usedDirectory.getLastUsedDirectory().orElse(null);
                if (lastUsedDirectory != null) {
                    fileDialog.setDirectory(lastUsedDirectory.getAbsolutePath());
                }
            }
            if (selectedFile != null) {
                fileDialog.setFile(selectedFile.getAbsolutePath());
            }
            fileDialog.setModal(true);
            fileDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            fileDialog.setLocationByPlatform(true);
            fileDialog.setLocationRelativeTo(frameModule.getFrame());
            fileDialog.setVisible(true);
            String file = fileDialog.getFile();
            OpenFileResult result = new OpenFileResult();
            result.dialogResult = file != null ? JFileChooser.APPROVE_OPTION : JFileChooser.CANCEL_OPTION;
            result.selectedFile = file != null ? new File(fileDialog.getDirectory(), file) : null;
            result.fileType = null;
            return result;
        }

        JFileChooser openFileChooser = new JFileChooser();
        setupFileFilters(openFileChooser, fileTypes);
        if (usedDirectory != null) {
            openFileChooser.setCurrentDirectory(usedDirectory.getLastUsedDirectory().orElse(null));
        }
        if (selectedFile != null) {
            openFileChooser.setSelectedFile(selectedFile);
        }
        int dialogResult = openFileChooser.showOpenDialog(frameModule.getFrame());
        OpenFileResult result = new OpenFileResult();
        result.dialogResult = dialogResult;
        result.selectedFile = openFileChooser.getSelectedFile();
        FileFilter fileFilter = openFileChooser.getFileFilter();
        result.fileType = fileFilter instanceof FileType ? (FileType) fileFilter : null;

        return result;
    }

    @Nonnull
    @Override
    public OpenFileResult showSaveFileDialog(FileTypes fileTypes, @Nullable UsedDirectoryApi usedDirectory) {
        return showSaveFileDialog(fileTypes, null, usedDirectory);
    }

    @Nonnull
    @Override
    public OpenFileResult showSaveFileDialog(FileTypes fileTypes, @Nullable File selectedFile, @Nullable UsedDirectoryApi usedDirectory) {
        return showSaveFileDialog(fileTypes, selectedFile, usedDirectory, null);
    }

    @Nonnull
    private OpenFileResult showSaveFileDialog(FileTypes fileTypes, @Nullable File selectedFile, @Nullable UsedDirectoryApi usedDirectory, @Nullable String dialogName) {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        if (((FileModule) fileModule).isUseAwtDialogs()) {
            FileDialog fileDialog = new FileDialog(frameModule.getFrame());
            fileDialog.setMode(FileDialog.SAVE);
            fileDialog.setMultipleMode(false);
            FilenameFilter filter = (File file, String string) -> true;
            fileDialog.setFilenameFilter(filter);
            if (usedDirectory != null) {
                File lastUsedDirectory = usedDirectory.getLastUsedDirectory().orElse(null);
                if (lastUsedDirectory != null) {
                    fileDialog.setDirectory(lastUsedDirectory.getAbsolutePath());
                }
            }
            if (selectedFile != null) {
                fileDialog.setFile(selectedFile.getAbsolutePath());
            }
            fileDialog.setModal(true);
            fileDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            fileDialog.setLocationByPlatform(true);
            fileDialog.setLocationRelativeTo(frameModule.getFrame());
            fileDialog.setVisible(true);
            String file = fileDialog.getFile();
            OpenFileResult result = new OpenFileResult();
            result.dialogResult = file != null ? JFileChooser.APPROVE_OPTION : JFileChooser.CANCEL_OPTION;
            result.selectedFile = file != null ? new File(fileDialog.getDirectory(), file) : null;
            result.fileType = null;
            return result;
        }

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
        OpenFileResult result = new OpenFileResult();
        result.dialogResult = dialogResult;
        result.selectedFile = saveFileChooser.getSelectedFile();
        FileFilter fileFilter = saveFileChooser.getFileFilter();
        result.fileType = fileFilter instanceof FileType ? (FileType) fileFilter : null;
        return result;
    }

    @Override
    public void saveFile(@Nullable FileHandler fileHandler, FileTypes fileTypes, @Nullable UsedDirectoryApi usedDirectory) {
        if (!(fileHandler instanceof EditableFileHandler)) {
            throw new IllegalStateException("Unable to save file" + (fileHandler == null ? "" : " " + fileHandler.getTitle()));
        }

        Optional<URI> fileUri = fileHandler.getFileUri();
        if (fileUri.isPresent()) {
            ((EditableFileHandler) fileHandler).saveToFile(fileUri.get(), fileHandler.getFileType().orElse(null));
        } else {
            saveAsFile(fileHandler, fileTypes, usedDirectory);
        }
    }

    @Override
    public void saveAsFile(@Nullable FileHandler fileHandler, FileTypes fileTypes, @Nullable UsedDirectoryApi usedDirectory) {
        if (!(fileHandler instanceof EditableFileHandler)) {
            throw new IllegalStateException("Unable to save file" + (fileHandler == null ? "" : " " + fileHandler.getTitle()));
        }

        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        Optional<URI> currentFileUri = fileHandler.getFileUri();
        OpenFileResult saveFileResult = showSaveFileDialog(fileTypes, currentFileUri.isPresent() ? new File(currentFileUri.get()) : null, usedDirectory, resourceBundle.getString("SaveAsDialog.title"));
        if (saveFileResult.dialogResult == JFileChooser.APPROVE_OPTION) {
            File selectedFile = Objects.requireNonNull(saveFileResult.selectedFile);
            if (selectedFile.exists()) {
                if (!showAskToOverwrite()) {
                    return;
                }
            }

            try {
                URI fileUri = selectedFile.toURI();
                ((EditableFileHandler) fileHandler).saveToFile(fileUri, (FileType) saveFileResult.fileType);
                if (usedDirectory != null) {
                    usedDirectory.setLastUsedDirectory(selectedFile.getParentFile());
                    usedDirectory.updateRecentFilesList(fileUri, saveFileResult.fileType);
                }
            } catch (Exception ex) {
                Logger.getLogger(FileActions.class.getName()).log(Level.SEVERE, null, ex);
                String errorMessage = ex.getLocalizedMessage();
                JOptionPane.showMessageDialog(frameModule.getFrame(),
                        resourceBundle.getString("Question.unable_to_save") + ": " + ex.getClass().getCanonicalName() + (errorMessage == null || errorMessage.isEmpty() ? "" : errorMessage),
                        resourceBundle.getString("Question.unable_to_save"), JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    @Override
    public boolean showAskForSaveDialog(@Nullable FileHandler fileHandler, FileTypes fileTypes, @Nullable UsedDirectoryApi usedDirectory) {
        if (!(fileHandler instanceof EditableFileHandler)) {
            return true;
        }

        while (((EditableFileHandler) fileHandler).isModified()) {
            Object[] options = {
                resourceBundle.getString("Question.modified_save"),
                resourceBundle.getString("Question.modified_discard"),
                resourceBundle.getString("Question.modified_cancel")
            };
            FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
            int result = JOptionPane.showOptionDialog(frameModule.getFrame(),
                    resourceBundle.getString("Question.modified"),
                    resourceBundle.getString("Question.modified_title"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            if (result == JOptionPane.NO_OPTION) {
                return true;
            }
            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                return false;
            }

            saveFile(fileHandler, fileTypes, usedDirectory);
        }

        return true;
    }

    /**
     * Asks whether it's allowed to overwrite file.
     *
     * @return true if allowed
     */
    @Override
    public boolean showAskToOverwrite() {
        Object[] options = {
            resourceBundle.getString("Question.overwrite_save"),
            resourceBundle.getString("Question.modified_cancel")
        };

        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        int result = JOptionPane.showOptionDialog(
                frameModule.getFrame(),
                resourceBundle.getString("Question.overwrite"),
                resourceBundle.getString("Question.overwrite_title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (result == JOptionPane.YES_OPTION) {
            return true;
        }
        if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION) {
            return false;
        }

        return false;
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
