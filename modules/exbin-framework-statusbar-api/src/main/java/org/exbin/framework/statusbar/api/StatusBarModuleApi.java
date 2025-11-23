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
package org.exbin.framework.statusbar.api;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JToolBar;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionContextRegistration;

/**
 * Support for status bar.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface StatusBarModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(StatusBarModuleApi.class);
    public static final String MAIN_STATUS_BAR_ID = "mainStatusBar";

    /**
     * Returns main status bar management interface.
     *
     * @param moduleId module id
     * @return status bar management interface
     */
    @Nonnull
    StatusBarDefinitionManagement getMainStatusBarManager(String moduleId);

    /**
     * Returns status bar management interface.
     *
     * @param statusBarId status bar id
     * @param moduleId module id
     * @return status bar management interface
     */
    @Nonnull
    StatusBarDefinitionManagement getStatusBarManager(String statusBarId, String moduleId);

    /**
     * Registers status bar associating it with given identificator.
     *
     * @param statusBarId status bar id
     * @param moduleId module id
     */
    void registerStatusBar(String statusBarId, String moduleId);

    /**
     * Creates status bar manager.
     *
     * @return status bar manager
     */
    @Nonnull
    StatusBarManagement createStatusBarManager();

    /**
     * Returns status bar using given identificator.
     *
     * @param targetStatusBar target status bar
     * @param statusBarId status bar id
     * @param actionContextRegistration action context registration
     */
    void buildStatusBar(JToolBar targetStatusBar, String statusBarId, ActionContextRegistration actionContextRegistration);

    /**
     * Returns list of action managed by status bar managers.
     *
     * @return list of actions
     */
    @Nonnull
    List<Action> getStatusBarManagedActions();

    /**
     * Returns status bar into main frame.
     */
    void registerFrameStatusBar();
}
