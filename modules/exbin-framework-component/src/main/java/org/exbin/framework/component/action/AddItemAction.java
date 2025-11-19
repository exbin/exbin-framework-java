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
package org.exbin.framework.component.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.component.api.ContextEditItem;
import org.exbin.framework.context.api.ContextChangeRegistration;

/**
 * Add item action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddItemAction extends AbstractAction {

    public static final String ACTION_ID = "addItemAction";

    protected final EditItemMode mode;
    protected ContextEditItem actionsHandler;

    public AddItemAction(EditItemMode mode) {
        this.mode = mode;
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        if (mode == EditItemMode.DIALOG) {
            putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        }
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ContextChangeRegistration registrar) -> {
            registrar.registerUpdateListener(ContextEditItem.class, (ContextEditItem instance) -> {
                actionsHandler = instance;
                setEnabled(actionsHandler.canEditItem());
            });
        });
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        actionsHandler.performAddItem();
    }
}
