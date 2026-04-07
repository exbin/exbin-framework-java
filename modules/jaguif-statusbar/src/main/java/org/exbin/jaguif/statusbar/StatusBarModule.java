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
package org.exbin.jaguif.statusbar;

import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
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
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class StatusBarModule implements StatusBarModuleApi {

    private StatusBarManager statusBarManager = null;
    private ResourceBundle resourceBundle;

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(StatusBarModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    public StatusBarManager getStatusBarManager() {
        if (statusBarManager == null) {
            statusBarManager = new StatusBarManager();
        }

        return statusBarManager;
    }

    @Nonnull
    @Override
    public StatusBarManagement createStatusBarManager() {
        return new StatusBarManager();
    }

    @Nonnull
    @Override
    public StatusBarDefinitionManagement getStatusBarManager(String statusBarId, String moduleId) {
        return new StatusBarDefinitionManager(StatusBarModule.this.getStatusBarManager(), statusBarId, moduleId);
    }

    @Nonnull
    @Override
    public StatusBarDefinitionManagement getMainStatusBarManager() {
        return getStatusBarManager(MAIN_STATUS_BAR_ID, MODULE_ID);
    }

    @Override
    public void registerStatusBar(String statusBarId, String moduleId) {
        StatusBarModule.this.getStatusBarManager().registerStatusBar(statusBarId, moduleId);
    }

    @Override
    public void buildStatusBar(StatusBar targetStatusBar, String statusBarId, ContextRegistration contextRegistration) {
        StatusBarModule.this.getStatusBarManager().buildStatusBar(targetStatusBar, statusBarId, contextRegistration);
    }
    
    @Nonnull
    @Override
    public StatusBar createStatusBar(String statusBarId, ContextRegistration contextRegistration) {
        StatusBar statusBar = new DefaultStatusBar();
        buildStatusBar(statusBar, statusBarId, contextRegistration);
        return statusBar;
    }

    @Nonnull
    @Override
    public List<Action> getStatusBarManagedActions() {
        throw new UnsupportedOperationException();
    }
}
