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
package org.exbin.framework.project.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.utils.ActionUtils;

/**
 * Save project action.
 *
 * @version 0.2.2 2022/01/10
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SaveProjectAction extends AbstractAction {

    public static final String ACTION_ID = "saveProjectAction";

    private ResourceBundle resourceBundle;
    private XBApplication application;

    public SaveProjectAction() {
    }

    public void setup(XBApplication application, ResourceBundle resourceBundle) {
        this.application = application;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, ActionUtils.getMetaMask() | InputEvent.SHIFT_DOWN_MASK));
    }

    public void updateForFileOperations() {
//        FileOperations fileOperations = fileOperationsProvider.getFileOperations();
//        setEnabled(fileOperations != null ? fileOperations.canSave() : false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}