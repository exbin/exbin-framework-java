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
package org.exbin.framework.bined.action;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.gui.CompareFilesPanel;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.file.api.FileHandler;

/**
 * Compare files action.
 *
 * @version 0.2.1 2021/10/12
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CompareFilesAction extends AbstractAction {

    public static final String ACTION_ID = "compareFilesAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    public CompareFilesAction() {

    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final CompareFilesPanel compareFilesPanel = new CompareFilesPanel();
        CloseControlPanel controlPanel = new CloseControlPanel(compareFilesPanel.getResourceBundle());
        JPanel dialogPanel = WindowUtils.createDialogPanel(compareFilesPanel, controlPanel);
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        final WindowUtils.DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, editorProvider.getEditorComponent(), "Compare two files", Dialog.ModalityType.APPLICATION_MODAL);
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (activeFile.isPresent()) {
            compareFilesPanel.setLeftFile(((BinEdFileHandler) activeFile.get()).getCodeArea().getContentData());
        }

        List<FileHandler> fileHandlers;
        if (editorProvider instanceof MultiEditorProvider) {
            fileHandlers = ((MultiEditorProvider) editorProvider).getFileHandlers();
            List<String> availableFiles = new ArrayList<>();
            for (FileHandler fileHandler : fileHandlers) {
                Optional<URI> fileUri = fileHandler.getFileUri();
                availableFiles.add(fileUri.isPresent() ? fileUri.get().toString() : "Unsaved file");
            }
            compareFilesPanel.setAvailableFiles(availableFiles);
        } else {
            fileHandlers = new ArrayList<>();
            Optional<URI> fileUri = editorProvider.getActiveFile().get().getFileUri();
            List<String> availableFiles = new ArrayList<>();
            availableFiles.add(fileUri.isPresent() ? fileUri.get().toString() : "Unsaved file");
            compareFilesPanel.setAvailableFiles(availableFiles);
        }

        compareFilesPanel.setControl(new CompareFilesPanel.Control() {
            @Nullable
            @Override
            public File openFile() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Nonnull
            @Override
            public FileHandler getFileHandler(int index) {
                return fileHandlers.get(index);
            }
        });
        dialog.showCentered(editorProvider.getEditorComponent());
    }
}
