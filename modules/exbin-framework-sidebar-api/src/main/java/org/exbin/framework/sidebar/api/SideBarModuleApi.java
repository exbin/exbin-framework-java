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
package org.exbin.framework.sidebar.api;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionContextRegistration;
import org.exbin.framework.docking.api.SidePanelDocking;

/**
 * Interface for side bar support module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface SideBarModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(SideBarModuleApi.class);
    public static final String MAIN_SIDE_BAR_ID = "mainSideBar";

    /**
     * Returns main side bar management interface.
     *
     * @param moduleId module id
     * @return side bar management interface
     */
    @Nonnull
    SideBarDefinitionManagement getMainSideBarManager(String moduleId);

    /**
     * Returns side bar management interface.
     *
     * @param sideBarId side bar id
     * @param moduleId module id
     * @return side bar management interface
     */
    @Nonnull
    SideBarDefinitionManagement getSideBarManager(String sideBarId, String moduleId);

    /**
     * Registers side bar associating it with given identificator.
     *
     * @param sideBarId sidebar id
     * @param moduleId module id
     */
    void registerSideBar(String sideBarId, String moduleId);

    /**
     * Creates side bar manager.
     *
     * @return side bar manager
     */
    @Nonnull
    SideBarManagement createSideBarManager();

    /**
     * Returns side bar using given identificator.
     *
     * @param targetSideBar target sidebar
     * @param sideBarId sidebar id
     * @param actionContextRegistration action context registration
     */
    void buildSideBar(SideBar targetSideBar, String sideBarId, ActionContextRegistration actionContextRegistration);

    /**
     * Returns list of action managed by sidebar managers.
     *
     * @return list of actions
     */
    @Nonnull
    List<Action> getSideBarManagedActions();

    /**
     * Registers side bar to docking.
     *
     * @param docking docking
     */
    void registerDockingSideBar(SidePanelDocking docking);

    /**
     * Registers side bar to docking.
     *
     * @param sideBarPanelProvider side bar panel provider
     * @param docking docking
     */
    void registerDockingSideBar(SideBar sideBarPanelProvider, SidePanelDocking docking);
}
