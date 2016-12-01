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

import java.awt.Frame;
import org.exbin.framework.gui.update.dialog.CheckUpdatesDialog;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.menu.api.MenuPosition;
import org.exbin.framework.gui.menu.api.PositionMode;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.update.api.GuiUpdateModuleApi;
import org.exbin.framework.gui.update.panel.ApplicationUpdateOptionsPanel;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Implementation of XBUP framework check updates module.
 *
 * @version 0.2.0 2016/08/18
 * @author ExBin Project (http://exbin.org)
 */
public class GuiUpdateModule implements GuiUpdateModuleApi {

    private XBApplication application;
    private java.util.ResourceBundle resourceBundle = null;
    private Action checkUpdateAction;
    private URL checkUpdateUrl;
    private VersionNumbers updateVersion;
    private URL downloadUrl;

    public GuiUpdateModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    private ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(GuiUpdateModule.class);
        }

        return resourceBundle;
    }

    @Override
    public Action getCheckUpdateAction() {
        if (checkUpdateAction == null) {
            getResourceBundle();
            checkUpdateAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);

                    CheckUpdatesDialog checkUpdatesDialog = new CheckUpdatesDialog(frameModule.getFrame(), true, application);
                    checkUpdatesDialog.setProjectResourceBundle(application.getAppBundle());
                    checkUpdatesDialog.setVersionNumbers(getVersionNumbers());
                    checkUpdatesDialog.setUpdateDownloadUrl(downloadUrl);
                    checkUpdatesDialog.setCheckUpdatesHandler(new CheckUpdatesDialog.CheckUpdatesHandler() {
                        @Override
                        public CheckUpdatesResult checkForUpdates() {
                            return GuiUpdateModule.this.checkForUpdates();
                        }

                        @Override
                        public VersionNumbers getUpdateVersion() {
                            return GuiUpdateModule.this.getUpdateVersion();
                        }
                    });
                    checkUpdatesDialog.setLocationRelativeTo(checkUpdatesDialog.getParent());
                    checkUpdatesDialog.setVisible(true);
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
        optionsModule.addOptionsPanel(new ApplicationUpdateOptionsPanel());
    }

    public VersionNumbers getVersionNumbers() {
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

    public CheckUpdatesResult checkForUpdates() {
        if (checkUpdateUrl == null) {
            return CheckUpdatesResult.UPDATE_URL_NOT_SET;
        }

        try {
            try (InputStream checkUpdateStream = checkUpdateUrl.openStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(checkUpdateStream))) {
                String line = reader.readLine();
                if (line == null) {
                    return CheckUpdatesResult.NOT_FOUND;
                }
                updateVersion = new VersionNumbers();
                updateVersion.versionFromString(line);
            }

            // Compare versions
            if (updateVersion.isGreaterThan(getVersionNumbers())) {
                return CheckUpdatesResult.UPDATE_FOUND;
            }

            return CheckUpdatesResult.NO_UPDATE_AVAILABLE;
        } catch (FileNotFoundException ex) {
            return CheckUpdatesResult.NOT_FOUND;
        } catch (IOException ex) {
            return CheckUpdatesResult.CONNECTION_ISSUE;
        } catch (Exception ex) {
            return CheckUpdatesResult.CONNECTION_ISSUE;
        }
    }

    public VersionNumbers getUpdateVersion() {
        return updateVersion;
    }

    @Override
    public void setUpdateDownloadUrl(URL downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public void checkOnStart(Frame frame) {
        boolean checkOnStart = application.getAppPreferences().getBoolean(ApplicationUpdateOptionsPanel.PREFERENCES_CHECK_FOR_UPDATE_ON_START, true);

        if (!checkOnStart) {
            return;
        }

        final CheckUpdatesResult checkForUpdates = checkForUpdates();
        if (checkForUpdates == CheckUpdatesResult.UPDATE_FOUND) {
            CheckUpdatesDialog checkUpdatesDialog = new CheckUpdatesDialog(frame, true, application);
            checkUpdatesDialog.setProjectResourceBundle(application.getAppBundle());
            checkUpdatesDialog.setVersionNumbers(getVersionNumbers());
            checkUpdatesDialog.setUpdateDownloadUrl(downloadUrl);
            checkUpdatesDialog.setCheckUpdatesHandler(new CheckUpdatesDialog.CheckUpdatesHandler() {
                boolean first = true;

                @Override
                public CheckUpdatesResult checkForUpdates() {
                    if (first) {
                        return checkForUpdates;
                    }
                    return GuiUpdateModule.this.checkForUpdates();
                }

                @Override
                public VersionNumbers getUpdateVersion() {
                    return GuiUpdateModule.this.getUpdateVersion();
                }
            });
            checkUpdatesDialog.setLocationRelativeTo(checkUpdatesDialog.getParent());
            checkUpdatesDialog.setVisible(true);
        }
    }

    /**
     * Enumeration of result types.
     */
    public static enum CheckUpdatesResult {
        UPDATE_URL_NOT_SET,
        NO_CONNECTION,
        CONNECTION_ISSUE,
        NOT_FOUND,
        NO_UPDATE_AVAILABLE,
        UPDATE_FOUND
    }
}
