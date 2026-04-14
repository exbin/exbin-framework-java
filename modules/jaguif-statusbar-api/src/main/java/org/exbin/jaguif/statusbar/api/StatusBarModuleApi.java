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
package org.exbin.jaguif.statusbar.api;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.context.api.ContextRegistration;

/**
 * Support for status bar.
 */
@ParametersAreNonnullByDefault
public interface StatusBarModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(StatusBarModuleApi.class);
    public static final String MAIN_STATUS_BAR_ID = "mainStatusBar";

    /**
     * Returns main status bar management interface.
     *
     * @return status bar management interface
     */
    @Nonnull
    StatusBarDefinitionManagement getMainStatusBarManager();

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
     * @param contextRegistration context registration
     */
    void buildStatusBar(StatusBar targetStatusBar, String statusBarId, ContextRegistration contextRegistration);

    /**
     * Creates new status bar using given identification.
     *
     * @param statusBarId status bar id
     * @param contextRegistration context registration
     * @return status bar
     */
    @Nonnull
    StatusBar createStatusBar(String statusBarId, ContextRegistration contextRegistration);

    /**
     * Returns list of action managed by status bar managers.
     *
     * @return list of actions
     */
    @Nonnull
    List<Action> getStatusBarManagedActions();
}
