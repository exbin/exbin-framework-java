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
package org.exbin.jaguif.operation.undo;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SeparationSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.operation.undo.api.OperationUndoModuleApi;
import org.exbin.jaguif.operation.undo.api.UndoActions;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.operation.undo.action.RedoAction;
import org.exbin.jaguif.operation.undo.action.UndoAction;
import org.exbin.jaguif.toolbar.api.ToolBarModuleApi;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.operation.undo.contribution.RedoContribution;
import org.exbin.jaguif.operation.undo.contribution.UndoContribution;
import org.exbin.jaguif.toolbar.api.ToolBarDefinitionManagement;

/**
 * Implementation of undo/redo support module.
 */
@ParametersAreNonnullByDefault
public class OperationUndoModule implements OperationUndoModuleApi {

    private java.util.ResourceBundle resourceBundle = null;

    public OperationUndoModule() {
    }

    @Nonnull
    @Override
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
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(OperationUndoModuleApi.UNDO_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.BELOW));
        contribution = new UndoContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(OperationUndoModuleApi.UNDO_MENU_GROUP_ID));
        contribution = new RedoContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(OperationUndoModuleApi.UNDO_MENU_GROUP_ID));
    }

    @Override
    public void registerMainToolBar() {
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarDefinitionManagement mgmt = toolBarModule.getMainToolBarManager(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(OperationUndoModuleApi.UNDO_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        mgmt.registerToolBarRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = new UndoContribution();
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(OperationUndoModuleApi.UNDO_TOOL_BAR_GROUP_ID));
        contribution = new RedoContribution();
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(OperationUndoModuleApi.UNDO_TOOL_BAR_GROUP_ID));
    }

    @Nonnull
    @Override
    public UndoActions createUndoActions() {
        return new UndoActions() {
            @Nonnull
            @Override
            public Action createUndoAction() {
                return OperationUndoModule.this.createUndoAction();
            }

            @Nonnull
            @Override
            public Action createRedoAction() {
                return OperationUndoModule.this.createRedoAction();
            }
        };
    }

    @Nonnull
    public UndoAction createUndoAction() {
        return new UndoAction();
    }

    @Nonnull
    public RedoAction createRedoAction() {
        return new RedoAction();
    }
}
