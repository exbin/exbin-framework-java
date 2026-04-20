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
package org.exbin.jaguif.toolbar;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JToolBar;
import org.exbin.jaguif.App;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.toolbar.api.ToolBarModuleApi;
import org.exbin.jaguif.action.api.clipboard.ClipboardActionsApi;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule.PositionMode;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.toolbar.api.ToolBarDefinitionManagement;
import org.exbin.jaguif.toolbar.api.ToolBarManagement;
import org.exbin.jaguif.context.api.ContextRegistration;

/**
 * Implementation of tool bar module.
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

    @Nonnull
    private ToolBarManager getToolBarManager() {
        if (toolBarManager == null) {
            toolBarManager = new ToolBarManager();
        }

        return toolBarManager;
    }

    @Nonnull
    @Override
    public ToolBarManagement createToolBarManager() {
        return new ToolBarManager();
    }

    @Override
    public void buildToolBar(JToolBar targetToolBar, String toolBarId, ContextRegistration contextRegistration) {
        ToolBarModule.this.getToolBarManager().buildToolBar(targetToolBar, toolBarId, contextRegistration);
    }

    @Override
    public void registerToolBar(String toolBarId, String moduleId) {
        ToolBarModule.this.getToolBarManager().registerToolBar(toolBarId, moduleId);
    }

    @Nonnull
    @Override
    public ToolBarDefinitionManagement getToolBarManager(String toolBarId, String moduleId) {
        return new ToolBarDefinitionManager(ToolBarModule.this.getToolBarManager(), toolBarId, moduleId);
    }

    @Nonnull
    @Override
    public ToolBarDefinitionManagement getMainToolBarManager(String moduleId) {
        return getToolBarManager(MAIN_TOOL_BAR_ID, moduleId);
    }

    @Override
    public void registerToolBarClipboardActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        ClipboardActionsApi clipboardActions = actionModule.getClipboardActions();
        ToolBarDefinitionManagement mgmt = getMainToolBarManager(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionMode.TOP));
        contribution = clipboardActions.createCutContribution();
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = clipboardActions.createCopyContribution();
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = clipboardActions.createPasteContribution();
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = clipboardActions.createDeleteContribution();
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
    }
}
