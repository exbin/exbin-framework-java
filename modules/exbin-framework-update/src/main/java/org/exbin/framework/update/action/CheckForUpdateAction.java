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
package org.exbin.framework.update.action;

import org.exbin.framework.update.api.VersionNumbers;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationBundle;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.update.gui.CheckForUpdatePanel;
import org.exbin.framework.update.preferences.CheckForUpdatePreferences;
import org.exbin.framework.update.service.CheckForUpdateService;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.gui.CloseControlPanel;

/**
 * Implementation of framework check update module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CheckForUpdateAction extends AbstractAction {

    public static final String ACTION_ID = "checkUpdateAction";

    private XBApplication application;
    private java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(CheckForUpdateAction.class);

    private URL checkUpdateUrl;
    private VersionNumbers updateVersion;
    private URL downloadUrl;

    private CheckForUpdateService checkForUpdateService;

    public CheckForUpdateAction() {
        init();
    }

    private void init() {
        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        CheckForUpdatePanel checkForUpdatePanel = new CheckForUpdatePanel();
        CloseControlPanel controlPanel = new CloseControlPanel();
        final DialogWrapper dialog = frameModule.createDialog(checkForUpdatePanel, controlPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), checkForUpdatePanel.getClass(), checkForUpdatePanel.getResourceBundle());
        frameModule.setDialogTitle(dialog, checkForUpdatePanel.getResourceBundle());
        controlPanel.setHandler(dialog::close);
        checkForUpdatePanel.setCheckForUpdateService(getCheckForUpdateService());
        checkForUpdatePanel.performCheckForUpdate();
        dialog.showCentered((Component) e.getSource());
        dialog.dispose();
    }

    @Nonnull
    public VersionNumbers getCurrentVersion() {
        ResourceBundle appBundle = application.getAppBundle();
        String releaseString = appBundle.getString(XBApplicationBundle.APPLICATION_RELEASE);
        VersionNumbers versionNumbers = new VersionNumbers();
        versionNumbers.versionFromString(releaseString);
        return versionNumbers;
    }

    public void setUpdateUrl(URL updateUrl) {
        this.checkUpdateUrl = updateUrl;
    }

    @Nullable
    public URL getUpdateUrl() {
        return checkUpdateUrl;
    }

    public void setCheckForUpdateService(CheckForUpdateService checkForUpdateService) {
        this.checkForUpdateService = checkForUpdateService;
    }

    @Nonnull
    public CheckForUpdateService getCheckForUpdateService() {
        return checkForUpdateService;
    }

    public void setUpdateVersion(VersionNumbers updateVersion) {
        this.updateVersion = updateVersion;
    }

    @Nullable
    public VersionNumbers getUpdateVersion() {
        return updateVersion;
    }

    @Nullable
    public URL getUpdateDownloadUrl() {
        return downloadUrl;
    }

    public void setUpdateDownloadUrl(URL downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void checkOnStart(Frame frame) {
        CheckForUpdatePreferences checkForUpdateParameters = new CheckForUpdatePreferences(application.getAppPreferences());
        boolean checkOnStart = checkForUpdateParameters.isShouldCheckForUpdate();

        if (!checkOnStart) {
            return;
        }

        getCheckForUpdateService();
        final CheckForUpdateService.CheckForUpdateResult checkForUpdates = checkForUpdateService.checkForUpdate();
        if (checkForUpdates == CheckForUpdateService.CheckForUpdateResult.UPDATE_FOUND) {
            FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
            CheckForUpdatePanel checkForUpdatePanel = new CheckForUpdatePanel();
            CloseControlPanel controlPanel = new CloseControlPanel();
            final DialogWrapper dialog = frameModule.createDialog(checkForUpdatePanel, controlPanel);
            WindowUtils.addHeaderPanel(dialog.getWindow(), checkForUpdatePanel.getClass(), checkForUpdatePanel.getResourceBundle());
            frameModule.setDialogTitle(dialog, checkForUpdatePanel.getResourceBundle());
            controlPanel.setHandler(dialog::close);
            checkForUpdatePanel.setCheckForUpdateService(checkForUpdateService);
            dialog.showCentered(frame);
            dialog.dispose();
        }
    }
}
