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
package org.exbin.framework.addon.update;

import org.exbin.framework.addon.update.api.VersionNumbers;
import java.awt.Frame;
import java.net.URL;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.addon.update.service.CheckForUpdateService;
import org.exbin.framework.addon.update.service.impl.CheckForUpdateServiceImpl;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.addon.update.action.CheckForUpdateAction;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.menu.api.PositionMenuContributionRule;
import org.exbin.framework.addon.update.api.AddonUpdateModuleApi;
import org.exbin.framework.addon.update.options.CheckForUpdateOptionsPage;
import org.exbin.framework.language.api.ApplicationInfoKeys;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.options.api.OptionsPageManagement;

/**
 * Implementation of framework check update module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonUpdateModule implements AddonUpdateModuleApi {

    private CheckForUpdateAction checkUpdateAction;

    private URL checkUpdateUrl;
    private VersionNumbers updateVersion;
    private URL downloadUrl;

    private CheckForUpdateService checkForUpdateService;

    public AddonUpdateModule() {
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
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.HELP_SUBMENU_ID);
        MenuContribution contribution = mgmt.registerMenuItem(getCheckUpdateAction());
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.MIDDLE_LAST));
    }

    @Override
    public void registerOptionsPanels() {
        CheckForUpdateOptionsPage optionsPage = new CheckForUpdateOptionsPage();
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(MODULE_ID);
        optionsPageManagement.registerPage(optionsPage);
    }

    @Nonnull
    public VersionNumbers getCurrentVersion() {
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        ResourceBundle appBundle = languageModule.getAppBundle();
        String releaseString = appBundle.getString(ApplicationInfoKeys.APPLICATION_RELEASE);
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
