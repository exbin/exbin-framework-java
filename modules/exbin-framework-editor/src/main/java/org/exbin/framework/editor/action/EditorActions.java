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
package org.exbin.framework.editor.action;

import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.editor.api.EditorActionsApi;
import org.exbin.framework.editor.gui.UnsavedFilesPanel;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;

/**
 * Editor actions.
 *
 * @version 0.2.2 2021/10/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditorActions implements EditorActionsApi {

    private ResourceBundle resourceBundle;
    private XBApplication application;
    private MultiEditorProvider editorProvider;

    public EditorActions() {
    }

    public void setup(XBApplication application, ResourceBundle resourceBundle, MultiEditorProvider editorProvider) {
        this.application = application;
        this.resourceBundle = resourceBundle;
        this.editorProvider = editorProvider;
    }

    @Override
    public boolean showAskForSaveDialog(List<FileHandler> fileHandlers) {
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        UnsavedFilesPanel unsavedFilesPanel = new UnsavedFilesPanel();
        unsavedFilesPanel.setUnsavedFiles(fileHandlers, editorProvider);
        final boolean[] result = new boolean[1];
        final DialogWrapper dialog = frameModule.createDialog(unsavedFilesPanel);
        unsavedFilesPanel.setController(new UnsavedFilesPanel.Controller() {
            @Override
            public boolean saveFile(FileHandler fileHandler) {
                editorProvider.saveFile(fileHandler);
                return !fileHandler.isModified();
            }

            @Override
            public void discardAll(List<FileHandler> fileHandlers) {
                result[0] = true;
                dialog.close();
            }

            @Override
            public void cancel() {
                result[0] = false;
                dialog.close();
            }
        });

        frameModule.setDialogTitle(dialog, unsavedFilesPanel.getResourceBundle());
        unsavedFilesPanel.assignGlobalKeys();
        dialog.showCentered(editorProvider.getEditorComponent());

        return result[0];
    }
}
