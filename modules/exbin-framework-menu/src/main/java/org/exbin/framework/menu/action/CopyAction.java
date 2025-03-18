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
package org.exbin.framework.menu.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.ClipboardActionsHandler;

/**
 * Clipboard copy action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CopyAction extends AbstractAction implements ActionContextChange {

    public static final String COPY_ACTION_ID = "popupCopyAction";

    private ClipboardActionsHandler clipboardActionsHandler;

    public CopyAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, COPY_ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, ActionUtils.getMetaMask()));
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (clipboardActionsHandler != null) {
            clipboardActionsHandler.performCopy();
        }
    }

    @Override
    public void register(ActionContextChangeManager manager) {
        manager.registerUpdateListener(ClipboardActionsHandler.class, instance -> {
            clipboardActionsHandler = instance;
            update();
        });
    }

    public void setClipboardActionsHandler(@Nullable ClipboardActionsHandler clipboardActionsHandler) {
        this.clipboardActionsHandler = clipboardActionsHandler;
        update();
    }

    public void update() {
        setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.isSelection());
    }

}
