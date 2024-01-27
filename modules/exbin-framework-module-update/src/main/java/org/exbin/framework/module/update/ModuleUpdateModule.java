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
package org.exbin.framework.module.update;

import org.exbin.framework.update.api.VersionNumbers;
import java.awt.Frame;
import java.net.URL;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.update.api.UpdateModuleApi;
import org.exbin.framework.module.update.options.CheckForUpdateOptions;
import org.exbin.framework.module.update.options.gui.ApplicationUpdateOptionsPanel;
import org.exbin.framework.module.update.preferences.CheckForUpdatePreferences;
import org.exbin.framework.module.update.service.CheckForUpdateService;
import org.exbin.framework.module.update.service.impl.CheckForUpdateServiceImpl;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.module.update.action.CheckForUpdateAction;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.options.api.OptionsComponent;

/**
 * Implementation of framework check update module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ModuleUpdateModule implements UpdateModuleApi {

    private CheckForUpdateAction checkUpdateAction;

    private URL checkUpdateUrl;
    private VersionNumbers updateVersion;
    private URL downloadUrl;

    private CheckForUpdateService checkForUpdateService;

    public ModuleUpdateModule() {
    }

    @Nonnull
    @Override
    public Action getCheckUpdateAction() {
        if (checkUpdateAction == null) {
            checkUpdateAction = new CheckForUpdateAction();
            checkUpdateAction.setCheckForUpdateService(getCheckForUpdateService());
            checkUpdateAction.setUpdateUrl(checkUpdateUrl);
            checkUpdateAction.setUpdateVersion(updateVersion);
            checkUpdateAction.setUpdateDownloadUrl(downloadUrl);
        }

        return checkUpdateAction;
    }

    @Override
    public void registerDefaultMenuItem() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(WindowModuleApi.HELP_MENU_ID, MODULE_ID, getCheckUpdateAction(), new MenuPosition(PositionMode.MIDDLE_LAST));
    }

    @Override
    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        optionsModule.addOptionsPage(new DefaultOptionsPage<CheckForUpdateOptions>() {
            @Override
            public OptionsComponent<CheckForUpdateOptions> createPanel() {
                return new ApplicationUpdateOptionsPanel();
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return App.getModule(LanguageModuleApi.class).getBundle(ApplicationUpdateOptionsPanel.class);
            }

            @Nonnull
            @Override
            public CheckForUpdateOptions createOptions() {
                return new CheckForUpdateOptions();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, CheckForUpdateOptions options) {
                options.loadFromPreferences(new CheckForUpdatePreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, CheckForUpdateOptions options) {
                options.saveToPreferences(new CheckForUpdatePreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(CheckForUpdateOptions options) {
            }
        });
    }

    @Nonnull
    public VersionNumbers getCurrentVersion() {
        String releaseString = ""; // TODO appBundle.getString(XBApplicationBundle.APPLICATION_RELEASE);
        VersionNumbers versionNumbers = new VersionNumbers();
        versionNumbers.versionFromString(releaseString);
        return versionNumbers;
    }

    @Override
    public void setUpdateUrl(URL updateUrl) {
        this.checkUpdateUrl = updateUrl;
        if (checkUpdateAction != null) {
            checkUpdateAction.setUpdateUrl(updateUrl);
        }
    }

    @Nullable
    @Override
    public URL getUpdateUrl() {
        return checkUpdateUrl;
    }

    @Nonnull
    public CheckForUpdateService getCheckForUpdateService() {
        if (checkForUpdateService == null) {
            checkForUpdateService = new CheckForUpdateServiceImpl(this);
        }

        return checkForUpdateService;
    }

    @Override
    public void setUpdateVersion(VersionNumbers updateVersion) {
        this.updateVersion = updateVersion;

        if (checkUpdateAction != null) {
            checkUpdateAction.setUpdateVersion(updateVersion);
        }
    }

    @Nullable
    @Override
    public VersionNumbers getUpdateVersion() {
        return updateVersion;
    }

    @Nullable
    @Override
    public URL getUpdateDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public void setUpdateDownloadUrl(URL downloadUrl) {
        this.downloadUrl = downloadUrl;

        if (checkUpdateAction != null) {
            checkUpdateAction.setUpdateDownloadUrl(downloadUrl);
        }
    }

    @Override
    public void checkOnStart(Frame frame) {
        checkUpdateAction.checkOnStart(frame);
    }
}
