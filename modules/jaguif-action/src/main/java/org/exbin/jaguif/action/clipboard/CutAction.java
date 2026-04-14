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
package org.exbin.jaguif.action.clipboard;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.DeletionController;
import org.exbin.jaguif.utils.ActionUtils;
import org.exbin.jaguif.action.api.clipboard.ClipboardController;
import org.exbin.jaguif.action.api.ContextComponent;
import org.exbin.jaguif.context.api.ContextChangeRegistration;

/**
 * Cut to clipboard action.
 */
@ParametersAreNonnullByDefault
public class CutAction extends AbstractAction implements ActionContextChange {

    public static final String ACTION_ID = "cut";

    protected ClipboardController clipboardSupport;

    public CutAction() {
    }

    public void init(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, ActionUtils.getMetaMask()));
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (clipboardSupport != null) {
            clipboardSupport.performCut();
        }
    }

    @Override
    public void register(ContextChangeRegistration registrar) {
        registrar.registerChangeListener(ContextComponent.class, (instance) -> {
            updateByContext(instance);
        });
        registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
            if (ClipboardController.UpdateType.CONTENT_STATE.equals(updateType)) {
                updateByContext(instance);
            }
        });
    }

    public void setClipboardSupport(@Nullable ClipboardController clipboardSupport) {
        updateByContext(clipboardSupport);
    }

    public void updateByContext(Object context) {
        clipboardSupport = context instanceof ClipboardController && context instanceof DeletionController ? (ClipboardController) context : null;
        setEnabled(clipboardSupport != null && clipboardSupport.hasDataToCopy() && ((DeletionController) clipboardSupport).canDelete());
    }
}
