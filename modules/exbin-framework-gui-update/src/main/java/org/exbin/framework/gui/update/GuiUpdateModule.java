/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.gui.update;

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
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.menu.api.MenuPosition;
import org.exbin.framework.gui.menu.api.PositionMode;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.options.api.OptionsCapable;
import org.exbin.framework.gui.update.api.GuiUpdateModuleApi;
import org.exbin.framework.gui.update.options.CheckForUpdateOptions;
import org.exbin.framework.gui.update.options.panel.ApplicationUpdateOptionsPanel;
import org.exbin.framework.gui.update.panel.CheckForUpdatePanel;
import org.exbin.framework.gui.update.preferences.CheckForUpdatePreferences;
import org.exbin.framework.gui.update.service.CheckForUpdateService;
import org.exbin.framework.gui.update.service.impl.CheckForUpdateServiceImpl;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.panel.CloseControlPanel;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.gui.options.api.DefaultOptionsPage;

/**
 * Implementation of framework check update module.
 *
 * @version 0.2.1 2019/07/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiUpdateModule implements GuiUpdateModuleApi {

    private XBApplication application;
    private java.util.ResourceBundle resourceBundle = null;
    private Action checkUpdateAction;

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
    private ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(GuiUpdateModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    @Override
    public Action getCheckUpdateAction() {
        if (checkUpdateAction == null) {
            getResourceBundle();
            checkUpdateAction = new AbstractAction() {
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
            };
            ActionUtils.setupAction(checkUpdateAction, resourceBundle, "checkUpdateAction");
            checkUpdateAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        }

        return checkUpdateAction;
    }

    @Override
    public void registerDefaultMenuItem() {
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
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
