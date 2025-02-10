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
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.file.api.FileOperations;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeManager;

/**
 * Save as file action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SaveAsFileAction extends AbstractAction {

    public static final String ACTION_ID = "saveAsFileAction";

    private ResourceBundle resourceBundle;
    private FileOperations fileOperations;

    public SaveAsFileAction() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, ActionUtils.getMetaMask() | KeyEvent.SHIFT_DOWN_MASK));
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeManager manager) {
                manager.registerUpdateListener(FileOperations.class, (instance) -> {
                    fileOperations = instance;
                    setEnabled(instance != null && instance.canSave());
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (fileOperations != null) {
            fileOperations.saveAsFile();
        }
    }
}
