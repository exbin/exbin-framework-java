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
import org.exbin.jaguif.action.api.ContextComponent;
import org.exbin.jaguif.context.api.ContextChangeRegistration;

/**
 * Delete in document action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DeleteAction extends AbstractAction implements ActionContextChange {

    public static final String ACTION_ID = "delete";

    protected DeletionController deletionSupport;

    public DeleteAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (deletionSupport != null) {
            deletionSupport.performDelete();
        }
    }

    @Override
    public void register(ContextChangeRegistration registrar) {
        registrar.registerChangeListener(ContextComponent.class, (instance) -> {
            updateByContext(instance);
        });
        registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
            if (DeletionController.UpdateType.CONTENT_STATE.equals(updateType)) {
                updateByContext(instance);
            }
        });
    }

    public void setClipboardSupport(@Nullable DeletionController deletionSupport) {
        updateByContext(deletionSupport);
    }

    public void updateByContext(Object context) {
        deletionSupport = context instanceof DeletionController ? (DeletionController) context : null;
        setEnabled(deletionSupport != null && deletionSupport.canDelete());
    }
}
