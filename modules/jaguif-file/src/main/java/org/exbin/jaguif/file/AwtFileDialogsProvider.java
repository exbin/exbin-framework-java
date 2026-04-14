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
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import org.exbin.jaguif.file.api.FileTypes;
import org.exbin.jaguif.file.api.OpenFileResult;
import org.exbin.jaguif.file.api.UsedDirectoryApi;

/**
 * AWT file dialogs provider.
 */
@ParametersAreNonnullByDefault
public class AwtFileDialogsProvider implements FileDialogsProvider {

    protected final ResourceBundle resourceBundle;

    public AwtFileDialogsProvider(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    @Override
    public String getProviderName() {
        return resourceBundle.getString("fileDialogs.awt");
    }

    @Nonnull
    @Override
    public OpenFileResult showOpenFileDialog(Component parentComponent, FileTypes fileTypes, @Nullable File selectedFile, @Nullable UsedDirectoryApi usedDirectory, @Nullable String dialogName) {
        Component rootComponent = SwingUtilities.getRoot(parentComponent);
        Frame frame = (Frame) rootComponent;
        FileDialog fileDialog = new FileDialog(frame);
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
        fileDialog.setLocationRelativeTo(parentComponent);
        fileDialog.setVisible(true);
        String file = fileDialog.getFile();
        return new OpenFileResult(
                file != null ? JFileChooser.APPROVE_OPTION : JFileChooser.CANCEL_OPTION,
                file != null ? new File(fileDialog.getDirectory(), file) : null,
                null
        );
    }

    @Override
    public OpenFileResult showSaveFileDialog(Component parentComponent, FileTypes fileTypes, @Nullable File selectedFile, @Nullable UsedDirectoryApi usedDirectory, @Nullable String dialogName) {
        Component rootComponent = SwingUtilities.getRoot(parentComponent);
        Frame frame = (Frame) rootComponent;
        FileDialog fileDialog = new FileDialog(frame);
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
        fileDialog.setLocationRelativeTo(parentComponent);
        fileDialog.setVisible(true);
        String file = fileDialog.getFile();
        return new OpenFileResult(
                file != null ? JFileChooser.APPROVE_OPTION : JFileChooser.CANCEL_OPTION,
                file != null ? new File(fileDialog.getDirectory(), file) : null,
                null
        );
    }
}
