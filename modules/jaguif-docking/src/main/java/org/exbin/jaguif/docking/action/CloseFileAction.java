/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.docking.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.utils.ActionUtils;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.docking.api.ContextDocking;
import org.exbin.jaguif.docking.api.DocumentDocking;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.jaguif.document.api.Document;

/**
 * Close file action.
 */
@ParametersAreNonnullByDefault
public class CloseFileAction extends AbstractAction {

    public static final String ACTION_ID = "fileClose";

    protected DocumentDocking docking;
    protected Document document;

    public CloseFileAction() {
    }

    public void init(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, ActionUtils.getMetaMask()));
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextDocking.class, (instance) -> {
                    docking = instance instanceof DocumentDocking ? (DocumentDocking) instance : null;
                    updateByContext();
                });
                registrar.registerChangeListener(ContextDocument.class, (instance) -> {
                    document = instance instanceof Document ? (Document) instance : null;
                    updateByContext();
                });
            }
        });
    }

    protected void updateByContext() {
        setEnabled(docking != null && document != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (docking.releaseDocument(document)) {
            docking.closeDocument(document);
        }
    }
}
