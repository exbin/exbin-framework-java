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
package org.exbin.jaguif.addon.update.action;

import org.exbin.jaguif.addon.update.api.VersionNumbers;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.DialogParentComponent;
import org.exbin.jaguif.window.api.WindowModuleApi;
import org.exbin.jaguif.addon.update.gui.CheckForUpdatePanel;
import org.exbin.jaguif.addon.update.settings.CheckForUpdateOptions;
import org.exbin.jaguif.addon.update.service.CheckForUpdateService;
import org.exbin.jaguif.ApplicationBundleKeys;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.window.api.WindowHandler;
import org.exbin.jaguif.window.api.gui.CloseControlPanel;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.context.api.ContextChangeRegistration;

/**
 * Check for update action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CheckForUpdateAction extends AbstractAction {

    public static final String ACTION_ID = "checkUpdateAction";

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(CheckForUpdateAction.class);

    private URL checkUpdateUrl;
    private VersionNumbers updateVersion;
    private URL downloadUrl;
    private DialogParentComponent dialogParentComponent;

    private CheckForUpdateService checkForUpdateService;

    public CheckForUpdateAction() {
        init();
    }

    private void init() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ContextChangeRegistration registrar) -> {
            registrar.registerChangeListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                dialogParentComponent = instance;
                setEnabled(instance != null);
            });
        });
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        CheckForUpdatePanel checkForUpdatePanel = new CheckForUpdatePanel();
        CloseControlPanel controlPanel = new CloseControlPanel();
        final WindowHandler dialog = windowModule.createDialog(checkForUpdatePanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), checkForUpdatePanel.getClass(), checkForUpdatePanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, checkForUpdatePanel.getResourceBundle());
        controlPanel.setController(dialog::close);
        checkForUpdatePanel.setCheckForUpdateService(getCheckForUpdateService());
        checkForUpdatePanel.performCheckForUpdate();
        dialog.showCentered(dialogParentComponent.getComponent());
        dialog.dispose();
    }

    @Nonnull
    public VersionNumbers getCurrentVersion() {
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        ResourceBundle appBundle = languageModule.getAppBundle();
        String releaseString = appBundle.getString(ApplicationBundleKeys.APPLICATION_RELEASE);
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
        OptionsModuleApi preferencesModule = App.getModule(OptionsModuleApi.class);
        CheckForUpdateOptions checkForUpdateParameters = new CheckForUpdateOptions(preferencesModule.getAppOptions());
        boolean checkOnStart = checkForUpdateParameters.isShouldCheckForUpdate();

        if (!checkOnStart) {
            return;
        }

        getCheckForUpdateService();
        final CheckForUpdateService.CheckForUpdateResult checkForUpdateResult = checkForUpdateService.checkForUpdate();
        if (checkForUpdateResult == CheckForUpdateService.CheckForUpdateResult.UPDATE_FOUND) {
            WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
            CheckForUpdatePanel checkForUpdatePanel = new CheckForUpdatePanel();
            CloseControlPanel controlPanel = new CloseControlPanel();
            final WindowHandler dialog = windowModule.createDialog(checkForUpdatePanel, controlPanel);
            windowModule.addHeaderPanel(dialog.getWindow(), checkForUpdatePanel.getClass(), checkForUpdatePanel.getResourceBundle());
            windowModule.setWindowTitle(dialog, checkForUpdatePanel.getResourceBundle());
            controlPanel.setController(dialog::close);
            checkForUpdatePanel.setCheckForUpdateService(checkForUpdateService);
            checkForUpdatePanel.setCheckUpdatesResult(checkForUpdateResult);
            dialog.showCentered(frame);
            dialog.dispose();
        }
    }
}
