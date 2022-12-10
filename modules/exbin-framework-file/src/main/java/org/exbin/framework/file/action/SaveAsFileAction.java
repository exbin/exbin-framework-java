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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.file.api.FileOperations;
import org.exbin.framework.file.api.FileOperationsProvider;
import org.exbin.framework.utils.ActionUtils;

/**
 * Save as file action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SaveAsFileAction extends AbstractAction {

    public static final String ACTION_ID = "saveAsFileAction";

    private ResourceBundle resourceBundle;
    private XBApplication application;
    private FileOperationsProvider fileOperationsProvider;

    public SaveAsFileAction() {
    }

    public void setup(XBApplication application, ResourceBundle resourceBundle, FileOperationsProvider fileOperationsProvider) {
        this.application = application;
        this.fileOperationsProvider = fileOperationsProvider;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, ActionUtils.getMetaMask() | KeyEvent.SHIFT_DOWN_MASK));
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        updateForFileOperations();
    }

    public void updateForFileOperations() {
        FileOperations fileOperations = fileOperationsProvider.getFileOperations();
        setEnabled(fileOperations != null ? fileOperations.canSave() : false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FileOperations fileOperations = fileOperationsProvider.getFileOperations();
        if (fileOperations != null) {
            fileOperations.saveAsFile();
        }
    }
}
