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
package org.exbin.framework.toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JToolBar;
import org.exbin.framework.App;
import org.exbin.framework.toolbar.api.ToolBarContribution;
import org.exbin.framework.toolbar.api.ToolBarContributionRule;
import org.exbin.framework.toolbar.api.ToolBarManagement;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.action.api.ActionContextService;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;

/**
 * Implementation of tool bar module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ToolBarModule implements ToolBarModuleApi {

    private ToolBarManager toolBarManager = null;
    private ResourceBundle resourceBundle;

    public ToolBarModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ToolBarModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    @Override
    public List<Action> getToolBarManagedActions() {
        List<Action> actions = new ArrayList<>();
        getToolBarManager();
        actions.addAll(toolBarManager.getAllManagedActions());

        return actions;
    }

    @Nonnull
    private ToolBarManager getToolBarManager() {
        if (toolBarManager == null) {
            toolBarManager = new ToolBarManager();
        }

        return toolBarManager;
    }

    @Nonnull
    @Override
    public ToolBarManagement getToolBarManagement(String moduleId) {
        return new ToolBarManagement() {
            @Override
            public void buildToolBar(JToolBar targetToolBar, String toolBarId, ActionContextService activationUpdateService) {
                getToolBarManager().buildToolBar(targetToolBar, toolBarId, activationUpdateService);
            }

            @Override
            public void registerToolBar(String toolBarId) {
                getToolBarManager().registerToolBar(toolBarId, moduleId);
            }

            @Nonnull
            @Override
            public ToolBarContribution registerToolBarItem(String toolBarId, Action action) {
                return getToolBarManager().registerToolBarItem(toolBarId, moduleId, action);
            }

            @Nonnull
            @Override
            public ToolBarContribution registerToolBarGroup(String toolBarId, String groupId) {
                return getToolBarManager().registerToolBarGroup(toolBarId, moduleId, groupId);
            }

            @Override
            public void registerToolBarRule(ToolBarContribution toolBarContribution, ToolBarContributionRule rule) {
                getToolBarManager().registerToolBarRule(toolBarContribution, rule);
            }
        };
    }

    @Override
    public void registerToolBarClipboardActions() {
/*        getClipboardActions();
        ToolBarManagement mgmt = getToolBarManagement(MODULE_ID);
        ToolBarContribution contribution = mgmt.registerToolBarGroup(ActionConsts.MAIN_TOOL_BAR_ID, CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionToolBarContributionRule(PositionMode.TOP));
        contribution = mgmt.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, clipboardActions.createCutAction());
        mgmt.registerToolBarRule(contribution, new GroupToolBarContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, clipboardActions.createCopyAction());
        mgmt.registerToolBarRule(contribution, new GroupToolBarContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, clipboardActions.createPasteAction());
        mgmt.registerToolBarRule(contribution, new GroupToolBarContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, clipboardActions.createDeleteAction());
        mgmt.registerToolBarRule(contribution, new GroupToolBarContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID)); */
    }
}
