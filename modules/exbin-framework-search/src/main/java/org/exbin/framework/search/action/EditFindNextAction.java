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
package org.exbin.framework.search.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.search.api.ContextSearch;
import org.exbin.framework.context.api.ContextChangeRegistration;
import org.exbin.framework.search.api.FindSearchState;

/**
 * Search find next action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditFindNextAction extends AbstractAction implements ActionContextChange {

    public static final String ACTION_ID = "searchFindNextAction";
    protected FindSearchState findSearchState;

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        findSearchState.performFindNext();
    }

    @Override
    public void register(ContextChangeRegistration registrar) {
        registrar.registerUpdateListener(ContextSearch.class, (instance) -> {
            updateByContext(instance);
        });
        registrar.registerStateChangeListener(ContextSearch.class, (instance, changeType) -> {
            if (FindSearchState.ChangeType.FIND_AVAILABILITY.equals(changeType)) {
                updateByContext(instance);
            }
        });
    }

    protected void updateByContext(ContextSearch context) {
        findSearchState = context instanceof FindSearchState ? (FindSearchState) context : null;
        setEnabled(findSearchState != null && findSearchState.isFindNextAvailable());
    }
}
