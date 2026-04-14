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
package org.exbin.jaguif.component.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.component.api.ContextMoveItem;
import org.exbin.jaguif.context.api.ContextChangeRegistration;

/**
 * Move up action.
 */
@ParametersAreNonnullByDefault
public class MoveUpAction extends AbstractAction {

    public static final String ACTION_ID = "moveItemUp";

    protected ContextMoveItem actionsHandler;

    public void init(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ContextChangeRegistration registrar) -> {
            registrar.registerChangeListener(ContextMoveItem.class, (ContextMoveItem instance) -> {
                actionsHandler = instance;
                setEnabled(actionsHandler.isEditable() && actionsHandler.isSelection());
            });
        });
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        actionsHandler.performMoveUp();
    }
}
