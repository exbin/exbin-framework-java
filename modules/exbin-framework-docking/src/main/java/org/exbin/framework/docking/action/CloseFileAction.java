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
package org.exbin.framework.docking.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.context.api.ContextChangeRegistration;
import org.exbin.framework.docking.DefaultMultiDocking;
import org.exbin.framework.docking.api.ContextDocking;
import org.exbin.framework.document.api.ContextDocument;
import org.exbin.framework.document.api.Document;

/**
 * Close file action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CloseFileAction extends AbstractAction {

    public static final String ACTION_ID = "fileCloseAction";

    protected DefaultMultiDocking multiDocking;
    protected Document document;

    public CloseFileAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, ActionUtils.getMetaMask()));
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerUpdateListener(ContextDocking.class, (instance) -> {
                    multiDocking = instance instanceof DefaultMultiDocking ? (DefaultMultiDocking) instance : null;
                    updateByContext();
                });
                registrar.registerUpdateListener(ContextDocument.class, (instance) -> {
                    document = instance instanceof Document ? (Document) instance : null;
                    updateByContext();
                });
            }
        });
    }

    protected void updateByContext() {
        setEnabled(multiDocking != null && document != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        multiDocking.closeDocument(document);
    }
}
