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
package org.exbin.framework.action.clipboard;

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
 * Clipboard select all action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SelectAllAction extends AbstractAction implements ActionContextChange {

    public static final String ACTION_ID = "selectAllAction";

    private ClipboardActionsHandler clipboardActionsHandler;

    public SelectAllAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, ActionUtils.getMetaMask()));
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (clipboardActionsHandler != null) {
            clipboardActionsHandler.performSelectAll();
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
        setEnabled(clipboardActionsHandler != null && clipboardActionsHandler.canSelectAll());
    }

}
