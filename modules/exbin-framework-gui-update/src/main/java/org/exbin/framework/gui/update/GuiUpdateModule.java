/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.gui.update;

import org.exbin.framework.gui.update.api.VersionNumbers;
import java.awt.Frame;
import java.net.URL;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.action.api.MenuPosition;
import org.exbin.framework.gui.action.api.PositionMode;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.options.api.OptionsCapable;
import org.exbin.framework.gui.update.api.GuiUpdateModuleApi;
import org.exbin.framework.gui.update.options.CheckForUpdateOptions;
import org.exbin.framework.gui.update.options.gui.ApplicationUpdateOptionsPanel;
import org.exbin.framework.gui.update.gui.CheckForUpdatePanel;
import org.exbin.framework.gui.update.preferences.CheckForUpdatePreferences;
import org.exbin.framework.gui.update.service.CheckForUpdateService;
import org.exbin.framework.gui.update.service.impl.CheckForUpdateServiceImpl;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.gui.options.api.DefaultOptionsPage;
import org.exbin.framework.gui.update.action.CheckForUpdateAction;
import org.exbin.framework.gui.action.api.GuiActionModuleApi;

/**
 * Implementation of framework check update module.
 *
 * @version 0.2.1 2020/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiUpdateModule implements GuiUpdateModuleApi {

    private XBApplication application;
    private CheckForUpdateAction checkUpdateAction;

    private URL checkUpdateUrl;
    private VersionNumbers updateVersion;
    private URL downloadUrl;

    private CheckForUpdateService checkForUpdateService;

    public GuiUpdateModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    @Override
    public Action getCheckUpdateAction() {
        if (checkUpdateAction == null) {
            checkUpdateAction = new CheckForUpdateAction();
            checkUpdateAction.setCheckForUpdateService(getCheckForUpdateService());
            checkUpdateAction.setApplication(application);
            checkUpdateAction.setUpdateUrl(checkUpdateUrl);
            checkUpdateAction.setUpdateVersion(updateVersion);
            checkUpdateAction.setUpdateDownloadUrl(downloadUrl);
        }

        return checkUpdateAction;
    }

    @Override
    public void registerDefaultMenuItem() {
        GuiActionModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.HELP_MENU_ID, MODULE_ID, getCheckUpdateAction(), new MenuPosition(PositionMode.MIDDLE_LAST));
    }

    @Override
    public void registerOptionsPanels() {
        GuiOptionsModuleApi optionsModule = application.getModuleRepository().getModuleByInterface(GuiOptionsModuleApi.class);
        optionsModule.addOptionsPage(new DefaultOptionsPage<CheckForUpdateOptions>() {
            @Override
            public OptionsCapable<CheckForUpdateOptions> createPanel() {
                return new ApplicationUpdateOptionsPanel();
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return LanguageUtils.getResourceBundleByClass(ApplicationUpdateOptionsPanel.class);
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
        ResourceBundle appBundle = application.getAppBundle();
        String releaseString = appBundle.getString("Application.release");
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
        CheckForUpdatePreferences checkForUpdateParameters = new CheckForUpdatePreferences(application.getAppPreferences());
        boolean checkOnStart = checkForUpdateParameters.isShouldCheckForUpdate();

        if (!checkOnStart) {
            return;
        }

        getCheckForUpdateService();
        final CheckForUpdateService.CheckForUpdateResult checkForUpdates = checkForUpdateService.checkForUpdate();
        if (checkForUpdates == CheckForUpdateService.CheckForUpdateResult.UPDATE_FOUND) {
            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
            CheckForUpdatePanel checkForUpdatePanel = new CheckForUpdatePanel();
            CloseControlPanel controlPanel = new CloseControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(checkForUpdatePanel, controlPanel);

            final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
            WindowUtils.addHeaderPanel(dialog.getWindow(), checkForUpdatePanel.getClass(), checkForUpdatePanel.getResourceBundle());
            frameModule.setDialogTitle(dialog, checkForUpdatePanel.getResourceBundle());
            controlPanel.setHandler(dialog::close);
            checkForUpdatePanel.setCheckForUpdateService(checkForUpdateService);
            dialog.showCentered(frame);
            dialog.dispose();
        }
    }
}
