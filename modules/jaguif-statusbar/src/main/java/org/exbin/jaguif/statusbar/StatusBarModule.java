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
import org.exbin.jaguif.action.api.ActionContextRegistration;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.statusbar.api.StatusBar;
import org.exbin.jaguif.statusbar.api.StatusBarDefinitionManagement;
import org.exbin.jaguif.statusbar.api.StatusBarManagement;
import org.exbin.jaguif.statusbar.api.StatusBarModuleApi;

/**
 * Support for status bar module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class StatusBarModule implements StatusBarModuleApi {

    public static final String MAIN_STATUS_BAR_ID = "mainStatusBar";

    private StatusBarDefinitionManager mainStatusBarManager = null;
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
    public StatusBarDefinitionManagement getMainStatusBarManager(String moduleId) {
        if (mainStatusBarManager == null) {
            mainStatusBarManager = new StatusBarDefinitionManager(createStatusBarManager(), MAIN_STATUS_BAR_ID, MODULE_ID);
        }

        return mainStatusBarManager;
    }

    @Nonnull
    @Override
    public StatusBarDefinitionManagement getStatusBarManager(String statusBarId, String moduleId) {
        return new StatusBarDefinitionManager((StatusBarManagement) StatusBarModule.this.getMainStatusBarManager(moduleId), statusBarId, moduleId);
    }

    @Override
    public void registerStatusBar(String statusBarId, String moduleId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nonnull
    @Override
    public StatusBarManagement createStatusBarManager() {
        return new StatusBarManager();
    }

    @Override
    public void buildStatusBar(StatusBar targetStatusBar, String statusBarId, ActionContextRegistration actionContextRegistration) {
        // TODO StatusBarModule.this.getMainStatusBarManager(MODULE_ID).buildStatusBar(targetStatusBar, statusBarId, actionContextRegistration);
    }

    @Nonnull
    @Override
    public List<Action> getStatusBarManagedActions() {
        throw new UnsupportedOperationException();
    }
}
