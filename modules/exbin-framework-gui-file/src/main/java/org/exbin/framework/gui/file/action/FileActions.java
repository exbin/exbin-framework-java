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
package org.exbin.framework.gui.file.action;

import java.io.File;
import java.net.URI;
import java.util.List;
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
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.file.api.FileHandlerApi;
import org.exbin.framework.gui.file.api.FileOperationsProvider;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;

/**
 * File actions.
 *
 * @version 0.2.2 2021/10/08
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FileActions {

    public static final String ALL_FILES_FILTER = "AllFilesFilter";

    private ResourceBundle resourceBundle;
    private XBApplication application;
    private FileOperationsProvider fileOperationsProvider;

    public FileActions() {
    }

    public void setup(XBApplication application, ResourceBundle resourceBundle, FileOperationsProvider fileOperationsProvider) {
        this.application = application;
        this.fileOperationsProvider = fileOperationsProvider;
        this.resourceBundle = resourceBundle;
    }

    public void setupFileFilters(JFileChooser fileChooser, FileTypes fileTypes) {
        for (FileType fileType : fileTypes.getFileTypes()) {
            fileChooser.addChoosableFileFilter((FileFilter) fileType);
        }

        if (fileTypes.allowAllFiles()) {
            fileChooser.addChoosableFileFilter(new AllFilesFilter());
        }
    }

    public void openFile(@Nullable FileHandlerApi fileHandler, FileTypes fileTypes) {
        if (fileHandler != null) {
            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
            JFileChooser openFileChooser = new JFileChooser();
            setupFileFilters(openFileChooser, fileTypes);
            if (openFileChooser.showOpenDialog(frameModule.getFrame()) == JFileChooser.APPROVE_OPTION) {
//                ((CardLayout) statusPanel.getLayout()).show(statusPanel, "busy");
//                statusPanel.repaint();
                URI fileUri = openFileChooser.getSelectedFile().toURI();

                FileType fileType = null;
                FileFilter fileFilter = openFileChooser.getFileFilter();
                if (fileFilter instanceof FileType) {
                    fileType = fileTypes.getFileType(((FileType) fileFilter).getFileTypeId()).orElse(null);
                }
                fileHandler.loadFromFile(fileUri, fileType);

//                updateRecentFilesList(fileUri);
            }
        }
    }

    public void saveFile(@Nullable FileHandlerApi fileHandler, FileTypes fileTypes) {
        if (fileHandler != null) {
            Optional<URI> fileUri = fileHandler.getFileUri();
            if (fileUri.isPresent()) {
                fileHandler.saveToFile(fileUri.get(), fileHandler.getFileType().get());
            } else {
                saveAsFile(fileHandler, fileTypes);
            }
        }
    }

    public void saveAsFile(@Nullable FileHandlerApi fileHandler, FileTypes fileTypes) {
        if (fileHandler != null) {
            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
            JFileChooser saveFileChooser = new JFileChooser();
            setupFileFilters(saveFileChooser, fileTypes);
            if (saveFileChooser.showSaveDialog(frameModule.getFrame()) == JFileChooser.APPROVE_OPTION) {
                if (new File(saveFileChooser.getSelectedFile().getAbsolutePath()).exists()) {
                    if (!overwriteFile()) {
                        return;
                    }
                }

                try {
                    URI fileUri = saveFileChooser.getSelectedFile().toURI();
                    fileHandler.saveToFile(fileUri, (FileType) saveFileChooser.getFileFilter());
//                    updateRecentFilesList(fileUri);
                } catch (Exception ex) {
                    Logger.getLogger(FileActions.class.getName()).log(Level.SEVERE, null, ex);
                    String errorMessage = ex.getLocalizedMessage();
                    JOptionPane.showMessageDialog(frameModule.getFrame(), "Unable to save file: " + ex.getClass().getCanonicalName() + (errorMessage == null || errorMessage.isEmpty() ? "" : errorMessage), "Unable to save file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Attempts to release current file and warn if document was modified.
     *
     * @param fileHandler file handler
     * @param fileTypes file types handler
     * @return true if successful
     */
    public boolean releaseFile(@Nullable FileHandlerApi fileHandler, FileTypes fileTypes) {
        if (fileHandler == null) {
            return true;
        }

        while (fileHandler.isModified()) {
            Object[] options = {
                resourceBundle.getString("Question.modified_save"),
                resourceBundle.getString("Question.modified_discard"),
                resourceBundle.getString("Question.modified_cancel")
            };
            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
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

            saveFile(fileHandler, fileTypes);
        }

        return true;
    }

    /**
     * Asks whether it's allowed to overwrite file.
     *
     * @return true if allowed
     */
    private boolean overwriteFile() {
        Object[] options = {
            resourceBundle.getString("Question.overwrite_save"),
            resourceBundle.getString("Question.modified_cancel")
        };

        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
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
            return "All files (*)";
        }

        @Nonnull
        @Override
        public String getFileTypeId() {
            return ALL_FILES_FILTER;
        }
    }

    @ParametersAreNonnullByDefault
    public interface FileTypes {

        boolean allowAllFiles();

        @Nonnull
        Optional<FileType> getFileType(String fileTypeId);

        @Nonnull
        List<FileType> getFileTypes();
    }
}