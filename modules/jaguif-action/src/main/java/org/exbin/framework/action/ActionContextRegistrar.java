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
package org.exbin.framework.action;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.action.api.ActionContextRegistration;
import org.exbin.framework.action.api.ActionManagement;

/**
 * Default action context registrar.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionContextRegistrar implements ActionContextRegistration {
    
    protected final ActionManagement actionManager;
    protected final List<Action> registeredActions = new ArrayList<>();

    public ActionContextRegistrar(ActionManagement actionManager) {
        this.actionManager = actionManager;
    }

    @Override
    public void registerActionContext(Action action) {
        registeredActions.add(action);
        actionManager.registerAction(action);
        actionManager.initAction(action);
    }

    @Override
    public void finish() {
        for (Action action : registeredActions) {
            actionManager.requestUpdateForAction(action);
        }
    }
}
