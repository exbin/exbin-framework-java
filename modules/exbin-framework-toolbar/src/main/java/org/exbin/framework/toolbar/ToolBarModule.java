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
import org.exbin.framework.toolbar.api.ToolBarManagement;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.action.api.ActionContextService;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;
import org.exbin.framework.action.api.clipboard.ClipboardActionsApi;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule.PositionMode;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.toolbar.api.ToolBarManager;

/**
 * Implementation of tool bar module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ToolBarModule implements ToolBarModuleApi {

    private DefaultToolBarManager toolBarManager = null;
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
    private DefaultToolBarManager getToolBarManager() {
        if (toolBarManager == null) {
            toolBarManager = new DefaultToolBarManager();
        }

        return toolBarManager;
    }

    @Nonnull
    @Override
    public ToolBarManager createToolBarManager() {
        return new DefaultToolBarManager();
    }

    @Override
    public void buildToolBar(JToolBar targetToolBar, String toolBarId, ActionContextService activationUpdateService) {
        getToolBarManager().buildToolBar(targetToolBar, toolBarId, activationUpdateService);
    }

    @Override
    public void registerToolBar(String toolBarId, String moduleId) {
        getToolBarManager().registerToolBar(toolBarId, moduleId);
    }

    @Nonnull
    @Override
    public ToolBarManagement getToolBarManagement(String toolBarId, String moduleId) {
        return new DefaultToolBarManagement(getToolBarManager(), toolBarId, moduleId);
    }

    @Nonnull
    @Override
    public ToolBarManagement getMainToolBarManagement(String moduleId) {
        return getToolBarManagement(MAIN_TOOL_BAR_ID, moduleId);
    }

    @Override
    public void registerToolBarClipboardActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        ClipboardActionsApi clipboardActions = actionModule.getClipboardActions();
        ToolBarManagement mgmt = getMainToolBarManagement(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionMode.TOP));
        contribution = mgmt.registerToolBarItem(clipboardActions.createCutAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(clipboardActions.createCopyAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(clipboardActions.createPasteAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(clipboardActions.createDeleteAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
    }
}
