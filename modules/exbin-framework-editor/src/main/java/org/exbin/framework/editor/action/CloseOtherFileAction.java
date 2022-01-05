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

import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.api.MultiEditorPopupMenu;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.file.api.FileDependentAction;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.utils.ActionUtils;

/**
 * Close file action.
 *
 * @version 0.2.2 2021/09/30
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CloseOtherFileAction extends AbstractAction implements FileDependentAction {

    public static final String ACTION_ID = "fileCloseOtherAction";

    private ResourceBundle resourceBundle;
    private XBApplication application;
    private MultiEditorProvider editorProvider;

    public CloseOtherFileAction() {
    }

    public void setup(XBApplication application, ResourceBundle resourceBundle, MultiEditorProvider editorProvider) {
        this.application = application;
        this.resourceBundle = resourceBundle;
        this.editorProvider = editorProvider;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, ActionUtils.getMetaMask()));
    }

    @Override
    public void updateForActiveFile() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        setEnabled(activeFile.isPresent());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        MultiEditorPopupMenu popupMenu = source instanceof JMenuItem && ((JMenuItem) source).getParent() instanceof MultiEditorPopupMenu ? (MultiEditorPopupMenu) ((JMenuItem) source).getParent() : null;
        if (popupMenu != null) {
            Optional<FileHandler> selectedFile = popupMenu.getSelectedFile();
            if (selectedFile.isPresent()) {
                editorProvider.closeOtherFiles(selectedFile.get());
            }
        } else {
            Optional<FileHandler> activeFile = editorProvider.getActiveFile();
            editorProvider.closeOtherFiles(activeFile.get());
        }
    }
}
