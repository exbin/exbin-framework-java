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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.component.ComponentModule;
import org.exbin.framework.component.api.toolbar.MoveItemActions;
import org.exbin.framework.component.api.toolbar.MoveItemActionsHandler;
import org.exbin.framework.component.api.toolbar.SideToolBar;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Item movement default action set.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultMoveItemActions implements MoveItemActions {

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ComponentModule.class);

    public DefaultMoveItemActions() {
    }

    @Nonnull
    @Override
    public MoveUpAction createMoveUpAction() {
        MoveUpAction moveUpAction = new MoveUpAction();
        moveUpAction.setup(resourceBundle);
        return moveUpAction;
    }

    @Nonnull
    @Override
    public MoveDownAction createMoveDownAction() {
        MoveDownAction moveDownAction = new MoveDownAction();
        moveDownAction.setup(resourceBundle);
        return moveDownAction;
    }

    @Nonnull
    @Override
    public MoveTopAction createMoveTopAction() {
        MoveTopAction moveTopAction = new MoveTopAction();
        moveTopAction.setup(resourceBundle);
        return moveTopAction;
    }

    @Nonnull
    @Override
    public MoveBottomAction createMoveBottomAction() {
        MoveBottomAction moveBottomAction = new MoveBottomAction();
        moveBottomAction.setup(resourceBundle);
        return moveBottomAction;
    }

    @Override
    public void registerActions(SideToolBar sideToolBar) {
        sideToolBar.addAction(createMoveTopAction());
        sideToolBar.addAction(createMoveUpAction());
        sideToolBar.addAction(createMoveDownAction());
        sideToolBar.addAction(createMoveBottomAction());
    }

    @ParametersAreNonnullByDefault
    public class MoveUpAction extends AbstractAction {

        public static final String ACTION_ID = "moveItemUpAction";

        private MoveItemActionsHandler actionsHandler;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ActionContextChangeManager manager) -> {
                manager.registerUpdateListener(MoveItemActionsHandler.class, (MoveItemActionsHandler instance) -> {
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

    @ParametersAreNonnullByDefault
    public class MoveDownAction extends AbstractAction {

        public static final String ACTION_ID = "moveItemDownAction";

        private MoveItemActionsHandler actionsHandler;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ActionContextChangeManager manager) -> {
                manager.registerUpdateListener(MoveItemActionsHandler.class, (MoveItemActionsHandler instance) -> {
                    actionsHandler = instance;
                    setEnabled(actionsHandler.isEditable() && actionsHandler.isSelection());
                });
            });
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            actionsHandler.performMoveDown();
        }
    }

    @ParametersAreNonnullByDefault
    public class MoveTopAction extends AbstractAction {

        public static final String ACTION_ID = "moveItemTopAction";

        private MoveItemActionsHandler actionsHandler;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ActionContextChangeManager manager) -> {
                manager.registerUpdateListener(MoveItemActionsHandler.class, (MoveItemActionsHandler instance) -> {
                    actionsHandler = instance;
                    setEnabled(actionsHandler.isEditable() && actionsHandler.isSelection());
                });
            });
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            actionsHandler.performMoveTop();
        }
    }

    @ParametersAreNonnullByDefault
    public class MoveBottomAction extends AbstractAction {

        public static final String ACTION_ID = "moveItemBottomAction";

        private MoveItemActionsHandler actionsHandler;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ActionContextChangeManager manager) -> {
                manager.registerUpdateListener(MoveItemActionsHandler.class, (MoveItemActionsHandler instance) -> {
                    actionsHandler = instance;
                    setEnabled(actionsHandler.isEditable() && actionsHandler.isSelection());
                });
            });
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            actionsHandler.performMoveBottom();
        }
    }
}
