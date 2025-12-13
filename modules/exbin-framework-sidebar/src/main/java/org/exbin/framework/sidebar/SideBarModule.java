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

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;
import org.exbin.framework.App;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.sidebar.api.SideBarModuleApi;
import org.exbin.framework.sidebar.api.SideBarDefinitionManagement;
import org.exbin.framework.sidebar.api.SideBarManagement;
import org.exbin.framework.action.api.ActionContextRegistration;
import org.exbin.framework.docking.api.SidePanelDocking;
import org.exbin.framework.sidebar.api.SideBar;
import org.exbin.framework.utils.UiUtils;

/**
 * Implementation of side bar module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SideBarModule implements SideBarModuleApi {

    private SideBarManager mainSideBarManager = null;
    private boolean autoShow = false;
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
        SideBarModule.this.getMainSideBarManager();
        actions.addAll(mainSideBarManager.getAllManagedActions());

        return actions;
    }

    @Nonnull
    private SideBarManager getMainSideBarManager() {
        if (mainSideBarManager == null) {
            mainSideBarManager = new SideBarManager();
            mainSideBarManager.registerSideBar(MAIN_SIDE_BAR_ID, MODULE_ID);
        }
        return mainSideBarManager;
    }

    @Override
    public void setAutoShow(boolean autoShow) {
        this.autoShow = autoShow;
    }

    @Nonnull
    @Override
    public SideBarManagement createSideBarManager() {
        return new SideBarManager();
    }

    @Override
    public void buildSideBar(SideBar targetSideBar, String sideBarId, ActionContextRegistration actionContextRegistration) {
        SideBarModule.this.getMainSideBarManager().buildSideBar(targetSideBar, sideBarId, actionContextRegistration);
    }

    @Override
    public void registerSideBar(String sideBarId, String moduleId) {
        SideBarModule.this.getMainSideBarManager().registerSideBar(sideBarId, moduleId);
    }

    @Nonnull
    @Override
    public SideBarDefinitionManagement getMainSideBarManager(String moduleId) {
        return getSideBarManager(MAIN_SIDE_BAR_ID, moduleId);
    }

    @Nonnull
    @Override
    public SideBarDefinitionManagement getSideBarManager(String sideBarId, String moduleId) {
        return new SideBarDefinitionManager(SideBarModule.this.getMainSideBarManager(), sideBarId, moduleId);
    }

    @Nonnull
    @Override
    public void registerDockingSideBar(SidePanelDocking docking) {
        registerDockingSideBar(getMainSideBarManager().createSideToolBar(docking), docking);
    }

    @Nonnull
    @Override
    public void registerDockingSideBar(SideBar sideBar, SidePanelDocking docking) {
        JToolBar toolBar = sideBar.getToolBar();
        docking.setSideToolBar(toolBar);
        docking.setSideComponent(sideBar.getSideBarPanel());
        if (autoShow) {
            JButton component = (JButton) toolBar.getComponentAtIndex(0);
            UiUtils.doButtonClick(component);
        }
    }
}
