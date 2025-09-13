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
package org.exbin.framework.sidebar;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JToolBar;
import org.exbin.framework.App;
import org.exbin.framework.sidebar.api.SideBarManagement;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.action.api.ActionContextService;
import org.exbin.framework.sidebar.api.SideBarManager;
import org.exbin.framework.sidebar.api.SideBarModuleApi;

/**
 * Implementation of side bar module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SideBarModule implements SideBarModuleApi {

    private DefaultSideBarManager sideBarManager = null;
    private ResourceBundle resourceBundle;

    public SideBarModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(SideBarModule.class);
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
    public List<Action> getSideBarManagedActions() {
        List<Action> actions = new ArrayList<>();
        getSideBarManager();
        actions.addAll(sideBarManager.getAllManagedActions());

        return actions;
    }

    @Nonnull
    private DefaultSideBarManager getSideBarManager() {
        if (sideBarManager == null) {
            sideBarManager = new DefaultSideBarManager();
        }

        return sideBarManager;
    }

    @Nonnull
    @Override
    public SideBarManager createSideBarManager() {
        return new DefaultSideBarManager();
    }

    @Override
    public void buildSideBar(JToolBar targetSideBar, String sideBarId, ActionContextService activationUpdateService) {
        getSideBarManager().buildSideBar(targetSideBar, sideBarId, activationUpdateService);
    }

    @Override
    public void registerSideBar(String sideBarId, String moduleId) {
        getSideBarManager().registerSideBar(sideBarId, moduleId);
    }

    @Nonnull
    @Override
    public SideBarManagement getMainSideBarManagement(String moduleId) {
        return getSideBarManagement(MAIN_SIDE_BAR_ID, moduleId);
    }

    @Nonnull
    @Override
    public SideBarManagement getSideBarManagement(String sideBarId, String moduleId) {
        return new DefaultSideBarManagement(getSideBarManager(), sideBarId, moduleId);
    }

    @Override
    public void registerSideBarClipboardActions() {
        /*        getClipboardActions();
        ToolBarManagement mgmt = getToolBarManagement(MODULE_ID);
        ToolBarContribution contribution = mgmt.registerSideBarGroup(ActionConsts.MAIN_TOOL_BAR_ID, CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID);
        mgmt.registerSideBarRule(contribution, new PositionToolBarContributionRule(PositionMode.TOP));
        contribution = mgmt.registerSideBarItem(ActionConsts.MAIN_TOOL_BAR_ID, clipboardActions.createCutAction());
        mgmt.registerSideBarRule(contribution, new GroupToolBarContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerSideBarItem(ActionConsts.MAIN_TOOL_BAR_ID, clipboardActions.createCopyAction());
        mgmt.registerSideBarRule(contribution, new GroupToolBarContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerSideBarItem(ActionConsts.MAIN_TOOL_BAR_ID, clipboardActions.createPasteAction());
        mgmt.registerSideBarRule(contribution, new GroupToolBarContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerSideBarItem(ActionConsts.MAIN_TOOL_BAR_ID, clipboardActions.createDeleteAction());
        mgmt.registerSideBarRule(contribution, new GroupToolBarContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID)); */
    }
}
