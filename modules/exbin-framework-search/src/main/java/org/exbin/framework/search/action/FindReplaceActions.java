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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeRegistration;
import org.exbin.framework.search.api.ContextSearch;

/**
 * Find/replace actions for binary search.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FindReplaceActions {

    public static final String FIND_ACTION_ID = "searchFindAction";
    public static final String FIND_AGAIN_ACTION_ID = "searchFindAgainAction";
    public static final String REPLACE_ACTION_ID = "searchReplaceAction";

    private ResourceBundle resourceBundle;

    public FindReplaceActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action createEditFindAction() {
        EditFindAction editFindAction = new EditFindAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(editFindAction, resourceBundle, FIND_ACTION_ID);
        editFindAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, ActionUtils.getMetaMask()));
        editFindAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        editFindAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, editFindAction);
        return editFindAction;
    }

    @Nonnull
    public Action createEditFindAgainAction() {
        EditFindAgainAction editFindAgainAction = new EditFindAgainAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(editFindAgainAction, resourceBundle, FIND_AGAIN_ACTION_ID);
        editFindAgainAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        editFindAgainAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, editFindAgainAction);
        return editFindAgainAction;
    }

    @Nonnull
    public Action createEditReplaceAction() {
        EditFindAgainAction editReplaceAction = new EditFindAgainAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(editReplaceAction, resourceBundle, REPLACE_ACTION_ID);
        editReplaceAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, ActionUtils.getMetaMask()));
        editReplaceAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        editReplaceAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, editReplaceAction);
        return editReplaceAction;
    }

    @ParametersAreNonnullByDefault
    public class EditFindAction extends AbstractAction implements ActionContextChange {

        private ContextSearch contextSearch;

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void register(ActionContextChangeRegistration registrar) {
            registrar.registerUpdateListener(ContextSearch.class, (instance) -> {
                contextSearch = instance;
                setEnabled(instance != null);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public class EditFindAgainAction extends AbstractAction implements ActionContextChange {

        private ContextSearch contextSearch;

        @Override
        public void actionPerformed(ActionEvent e) {
        }

        @Override
        public void register(ActionContextChangeRegistration registrar) {
            registrar.registerUpdateListener(ContextSearch.class, (instance) -> {
                contextSearch = instance;
                setEnabled(instance != null);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public class EditReplaceAction extends AbstractAction implements ActionContextChange {

        private ContextSearch contextSearch;

        @Override
        public void actionPerformed(ActionEvent e) {
        }

        @Override
        public void register(ActionContextChangeRegistration registrar) {
            registrar.registerUpdateListener(ContextSearch.class, (instance) -> {
                contextSearch = instance;
                setEnabled(instance != null);
            });
        }
    }
}
