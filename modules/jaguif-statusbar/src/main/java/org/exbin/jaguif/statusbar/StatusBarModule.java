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
package org.exbin.jaguif.statusbar;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.statusbar.api.StatusBar;
import org.exbin.jaguif.statusbar.api.StatusBarDefinitionManagement;
import org.exbin.jaguif.statusbar.api.StatusBarManagement;
import org.exbin.jaguif.statusbar.api.StatusBarModuleApi;
import org.exbin.jaguif.statusbar.gui.DefaultStatusBar;

/**
 * Support for status bar module.
 */
@ParametersAreNonnullByDefault
public class StatusBarModule implements StatusBarModuleApi {

    private StatusBarManager mainStatusBarManager = null;
    private ResourceBundle resourceBundle;

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(StatusBarModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    @Override
    public StatusBarManager getMainStatusBarManager() {
        if (mainStatusBarManager == null) {
            mainStatusBarManager = new StatusBarManager();
        }

        return mainStatusBarManager;
    }

    @Nonnull
    @Override
    public StatusBarManagement createStatusBarManager() {
        return new StatusBarManager();
    }

    @Nonnull
    @Override
    public StatusBarDefinitionManagement getMainStatusBarDefinition(String moduleId) {
        return new StatusBarDefinitionManager(StatusBarModule.this.getMainStatusBarManager(), MAIN_STATUS_BAR_ID, moduleId);
    }

    @Nonnull
    @Override
    public StatusBarDefinitionManagement getMainStatusBarDefinition(String statusBarId, String moduleId) {
        return new StatusBarDefinitionManager(StatusBarModule.this.getMainStatusBarManager(), statusBarId, moduleId);
    }

    @Nonnull
    @Override
    public StatusBarDefinitionManagement createStatusBarDefinition(StatusBarManagement statusBarManagement, String statusBarId, String moduleId) {
        return new StatusBarDefinitionManager(statusBarManagement, statusBarId, moduleId);
    }

    @Override
    public void registerStatusBar(String statusBarId, String moduleId) {
        StatusBarModule.this.getMainStatusBarManager().registerStatusBar(statusBarId, moduleId);
    }

    @Override
    public void buildStatusBar(StatusBar targetStatusBar, String statusBarId, ContextRegistration contextRegistration) {
        StatusBarModule.this.getMainStatusBarManager().buildStatusBar(targetStatusBar, statusBarId, contextRegistration);
    }

    @Nonnull
    @Override
    public StatusBar createStatusBar(String statusBarId, ContextRegistration contextRegistration) {
        StatusBar statusBar = new DefaultStatusBar();
        buildStatusBar(statusBar, statusBarId, contextRegistration);
        return statusBar;
    }
}
