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
package org.exbin.framework.operation.undo;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SeparationSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.operation.undo.api.OperationUndoModuleApi;
import org.exbin.framework.operation.undo.api.UndoActions;
import org.exbin.framework.menu.api.GroupMenuContributionRule;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.menu.api.PositionMenuContributionRule;
import org.exbin.framework.menu.api.SeparationMenuContributionRule;
import org.exbin.framework.toolbar.api.ToolBarManagement;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.operation.undo.action.RedoAction;
import org.exbin.framework.operation.undo.action.UndoAction;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;

/**
 * Implementation of undo/redo support module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OperationUndoModule implements OperationUndoModuleApi {

    private UndoActions defaultUndoActions = null;
    private java.util.ResourceBundle resourceBundle = null;

    public OperationUndoModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(OperationUndoModule.class);
        }

        return resourceBundle;
    }

    public void unregisterModule(String moduleId) {
    }

    @Override
    public void registerMainMenu() {
        getDefaultUndoActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        MenuContribution contribution = mgmt.registerMenuGroup(OperationUndoModuleApi.UNDO_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMenuContributionRule.SeparationMode.BELOW));
        contribution = mgmt.registerMenuItem(defaultUndoActions.createUndoAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(OperationUndoModuleApi.UNDO_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(defaultUndoActions.createRedoAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(OperationUndoModuleApi.UNDO_MENU_GROUP_ID));
    }

    @Override
    public void registerMainToolBar() {
        getDefaultUndoActions();
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarManagement mgmt = toolBarModule.getMainToolBarManagement(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(OperationUndoModuleApi.UNDO_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        mgmt.registerToolBarRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerToolBarItem(defaultUndoActions.createUndoAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(OperationUndoModuleApi.UNDO_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(defaultUndoActions.createRedoAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(OperationUndoModuleApi.UNDO_TOOL_BAR_GROUP_ID));
    }

    @Nonnull
    @Override
    public UndoActions createUndoActions() {
        return new UndoActions() {
            @Override
            public Action createUndoAction() {
                return OperationUndoModule.this.createUndoAction();
            }

            @Override
            public Action createRedoAction() {
                return OperationUndoModule.this.createRedoAction();
            }
        };
    }

    @Nonnull
    public UndoActions getDefaultUndoActions() {
        if (defaultUndoActions == null) {
            defaultUndoActions = createUndoActions();
        }

        return defaultUndoActions;
    }

    @Nonnull
    public UndoAction createUndoAction() {
        UndoAction undoAction = new UndoAction();
        undoAction.setup(getResourceBundle());
        return undoAction;
    }

    @Nonnull
    public RedoAction createRedoAction() {
        RedoAction redoAction = new RedoAction();
        redoAction.setup(getResourceBundle());
        return redoAction;
    }
}
