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
package org.exbin.jaguif.sidebar;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JButton;
import javax.swing.JToolBar;
import org.exbin.jaguif.App;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.sidebar.api.SideBarModuleApi;
import org.exbin.jaguif.sidebar.api.SideBarDefinitionManagement;
import org.exbin.jaguif.sidebar.api.SideBarManagement;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.docking.api.SidePanelDocking;
import org.exbin.jaguif.sidebar.api.SideBar;
import org.exbin.jaguif.utils.UiUtils;

/**
 * Implementation of side bar module.
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

    @Nonnull
    @Override
    public SideBarManagement getMainSideBarManager() {
        if (mainSideBarManager == null) {
            mainSideBarManager = new SideBarManager();
            mainSideBarManager.registerSideBar(MAIN_SIDE_BAR_ID, MODULE_ID);
        }
        return mainSideBarManager;
    }

    @Nonnull
    @Override
    public SideBarManagement createSideBarManager() {
        return new SideBarManager();
    }

    @Override
    public void setAutoShow(boolean autoShow) {
        this.autoShow = autoShow;
    }

    @Override
    public void buildSideBar(SideBar targetSideBar, String sideBarId, ContextRegistration contextRegistration) {
        SideBarModule.this.getMainSideBarManager().buildSideBar(targetSideBar, sideBarId, contextRegistration);
    }

    @Override
    public void registerSideBar(String sideBarId, String moduleId) {
        SideBarModule.this.getMainSideBarManager().registerSideBar(sideBarId, moduleId);
    }

    @Nonnull
    @Override
    public SideBarDefinitionManagement getMainSideBarDefinition(String moduleId) {
        return new SideBarDefinitionManager(getMainSideBarManager(), MAIN_SIDE_BAR_ID, moduleId);
    }

    @Nonnull
    @Override
    public SideBarDefinitionManagement createSideBarDefinition(SideBarManagement sideBarManagement, String sideBarId, String moduleId) {
        return new SideBarDefinitionManager(sideBarManagement, sideBarId, moduleId);
    }

    @Override
    public void registerDockingSideBar(SidePanelDocking docking) {
        registerDockingSideBar(((SideBarManager) getMainSideBarManager()).createSideToolBar(docking), docking);
    }

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
