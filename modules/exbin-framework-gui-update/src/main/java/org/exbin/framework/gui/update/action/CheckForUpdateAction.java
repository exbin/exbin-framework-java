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
package org.exbin.framework.gui.update.action;

import org.exbin.framework.gui.update.api.VersionNumbers;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.update.gui.CheckForUpdatePanel;
import org.exbin.framework.gui.update.preferences.CheckForUpdatePreferences;
import org.exbin.framework.gui.update.service.CheckForUpdateService;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;

/**
 * Implementation of framework check update module.
 *
 * @version 0.2.1 2020/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CheckForUpdateAction extends AbstractAction {

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
        ActionUtils.setupAction(this, resourceBundle, "checkUpdateAction");
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        CheckForUpdatePanel checkForUpdatePanel = new CheckForUpdatePanel();
        CloseControlPanel controlPanel = new CloseControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(checkForUpdatePanel, controlPanel);

        final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), checkForUpdatePanel.getClass(), checkForUpdatePanel.getResourceBundle());
        frameModule.setDialogTitle(dialog, checkForUpdatePanel.getResourceBundle());
        controlPanel.setHandler(dialog::close);
        checkForUpdatePanel.setCheckForUpdateService(getCheckForUpdateService());
        checkForUpdatePanel.checkForUpdate();
        dialog.showCentered((Component) e.getSource());
        dialog.dispose();
    }

    @Nonnull
    public VersionNumbers getCurrentVersion() {
        ResourceBundle appBundle = application.getAppBundle();
        String releaseString = appBundle.getString("Application.release");
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
