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
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.file.api.FileOperations;
import org.exbin.framework.file.api.FileOperationsProvider;
import org.exbin.framework.utils.ActionUtils;

/**
 * New file action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class NewFileAction extends AbstractAction {

    public static final String ACTION_ID = "newFileAction";

    private ResourceBundle resourceBundle;
    private FileOperationsProvider fileOperationsProvider;

    public NewFileAction() {
    }

    public void setup(ResourceBundle resourceBundle, FileOperationsProvider fileOperationsProvider) {
        this.fileOperationsProvider = fileOperationsProvider;
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.setupAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, ActionUtils.getMetaMask()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FileOperations fileOperations = fileOperationsProvider.getFileOperations();
        if (fileOperations != null) {
            fileOperations.newFile();
        }
    }
}
