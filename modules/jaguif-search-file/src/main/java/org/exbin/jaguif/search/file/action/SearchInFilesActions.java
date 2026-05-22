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
package org.exbin.jaguif.search.file.action;

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
import org.exbin.jaguif.search.file.SearchFileModule;
import org.exbin.jaguif.search.api.SearchModuleApi;
import org.exbin.jaguif.toolbar.api.ToolBarDefinitionManagement;
import org.exbin.jaguif.toolbar.api.ToolBarModuleApi;

/**
 * Find/replace actions for searching.
 */
@ParametersAreNonnullByDefault
public class SearchInFilesActions {

    private ResourceBundle resourceBundle;

    public SearchInFilesActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action createFindInFilesAction() {
        FindInFilesAction findInFilesAction = new FindInFilesAction();
        findInFilesAction.init(resourceBundle);
        return findInFilesAction;
    }

    @Nonnull
    public Action createReplaceInFilesAction() {
        ReplaceInFilesAction replaceInFilesAction = new ReplaceInFilesAction();
        replaceInFilesAction.init(resourceBundle);
        return replaceInFilesAction;
    }

    public void registerEditFindMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        String groupId = SearchModuleApi.EDIT_FIND_MENU_GROUP_ID;
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(SearchFileModule.MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = new FindInFilesContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupId));
        contribution = new ReplaceInFilesContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupId));
    }

    public void registerEditFindPopupMenuActions(String menuId) {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(menuId, SearchFileModule.MODULE_ID);
        String groupId = SearchModuleApi.EDIT_FIND_MENU_GROUP_ID;
        SequenceContribution contribution = new FindInFilesContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupId));
        contribution = new ReplaceInFilesContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupId));
    }

    public void registerEditFindToolBarActions() {
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarDefinitionManagement mgmt = toolBarModule.getMainToolBarDefinition(SearchFileModule.MODULE_ID);
        SequenceContribution contribution = new FindInFilesContribution();
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(SearchModuleApi.EDIT_FIND_TOOL_BAR_GROUP_ID));
    }

    public class FindInFilesContribution implements ActionSequenceContribution {

        public static final String CONTRIBUTION_ID = "findInFiles";

        @Nonnull
        @Override
        public Action createAction() {
            return createFindInFilesAction();
        }

        @Nonnull
        @Override
        public String getContributionId() {
            return CONTRIBUTION_ID;
        }
    }

    public class ReplaceInFilesContribution implements ActionSequenceContribution {

        public static final String CONTRIBUTION_ID = "replaceInFiles";

        @Nonnull
        @Override
        public Action createAction() {
            return createReplaceInFilesAction();
        }

        @Nonnull
        @Override
        public String getContributionId() {
            return CONTRIBUTION_ID;
        }
    }
}
