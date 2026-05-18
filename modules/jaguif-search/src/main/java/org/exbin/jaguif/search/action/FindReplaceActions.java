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
package org.exbin.jaguif.search.action;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.search.SearchModule;
import org.exbin.jaguif.search.api.SearchModuleApi;
import org.exbin.jaguif.toolbar.api.ToolBarDefinitionManagement;
import org.exbin.jaguif.toolbar.api.ToolBarModuleApi;

/**
 * Find/replace actions for searching.
 */
@ParametersAreNonnullByDefault
public class FindReplaceActions {

    private ResourceBundle resourceBundle;

    public FindReplaceActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action createEditFindAction() {
        EditFindAction editFindAction = new EditFindAction();
        editFindAction.init(resourceBundle);
        return editFindAction;
    }

    @Nonnull
    public Action createEditFindNextAction() {
        EditFindNextAction editFindNextAction = new EditFindNextAction();
        editFindNextAction.init(resourceBundle);
        return editFindNextAction;
    }

    @Nonnull
    public Action createEditFindPreviousAction() {
        EditFindPreviousAction editFindPreviousAction = new EditFindPreviousAction();
        editFindPreviousAction.init(resourceBundle);
        return editFindPreviousAction;
    }

    @Nonnull
    public Action createEditReplaceAction() {
        EditReplaceAction editReplaceAction = new EditReplaceAction();
        editReplaceAction.init(resourceBundle);
        return editReplaceAction;
    }

    public void registerEditFindMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        String groupId = SearchModuleApi.EDIT_FIND_MENU_GROUP_ID;
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(SearchModule.MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = new EditFindContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupId));
        contribution = new EditFindNextContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupId));
        contribution = new EditReplaceContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupId));
    }

    public void registerEditFindPopupMenuActions(String menuId) {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(menuId, SearchModule.MODULE_ID);
        String groupId = SearchModuleApi.EDIT_FIND_MENU_GROUP_ID;
        SequenceContribution contribution = new EditFindContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupId));
        contribution = new EditReplaceContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupId));
    }

    public void registerEditFindToolBarActions() {
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarDefinitionManagement mgmt = toolBarModule.getMainToolBarDefinition(SearchModule.MODULE_ID);
        SequenceContribution contribution = new EditFindContribution();
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(SearchModuleApi.EDIT_FIND_TOOL_BAR_GROUP_ID));
    }

    public class EditFindContribution implements ActionSequenceContribution {

        public static final String CONTRIBUTION_ID = "editFind";

        @Nonnull
        @Override
        public Action createAction() {
            return createEditFindAction();
        }

        @Nonnull
        @Override
        public String getContributionId() {
            return CONTRIBUTION_ID;
        }
    }

    public class EditFindNextContribution implements ActionSequenceContribution {

        public static final String CONTRIBUTION_ID = "editFindNext";

        @Nonnull
        @Override
        public Action createAction() {
            return createEditFindNextAction();
        }

        @Nonnull
        @Override
        public String getContributionId() {
            return CONTRIBUTION_ID;
        }
    }

    public class EditFindPreviousContribution implements ActionSequenceContribution {

        public static final String CONTRIBUTION_ID = "editFindPrevious";

        @Nonnull
        @Override
        public Action createAction() {
            return createEditFindPreviousAction();
        }

        @Nonnull
        @Override
        public String getContributionId() {
            return CONTRIBUTION_ID;
        }
    }

    public class EditReplaceContribution implements ActionSequenceContribution {

        public static final String CONTRIBUTION_ID = "editReplace";

        @Nonnull
        @Override
        public Action createAction() {
            return createEditReplaceAction();
        }

        @Nonnull
        @Override
        public String getContributionId() {
            return CONTRIBUTION_ID;
        }
    }
}
