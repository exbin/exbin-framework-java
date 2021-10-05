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
package org.exbin.framework.gui.editor.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.utils.ActionUtils;

/**
 * Close all files action.
 *
 * @version 0.2.2 2021/10/04
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CloseAllFileAction extends AbstractAction {

    public static final String ACTION_ID = "fileCloseAllAction";

    private ResourceBundle resourceBundle;
    private XBApplication application;

    public CloseAllFileAction() {
    }

    public void setup(XBApplication application, ResourceBundle resourceBundle) {
        this.application = application;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, ActionUtils.getMetaMask()));
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
