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
package org.exbin.framework.gui.service;

import java.awt.Component;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.menu.api.MenuManagement;
import org.exbin.framework.gui.menu.api.PositionMode;
import org.exbin.framework.gui.service.panel.ConnectionPanel;
import org.exbin.framework.gui.service.panel.ServiceManagerPanel;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * XBUP service manager module.
 *
 * @version 0.2.1 2019/07/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ServiceManagerModule implements XBApplicationModule {

    public static final String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(ServiceManagerModule.class);

    private XBApplication application;
    private ServiceManagerPanel servicePanel;
    private Preferences preferences;

    public ServiceManagerModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    public void openConnectionDialog(Component parentComponent) {
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        ConnectionPanel panel = new ConnectionPanel();
        panel.setApplication(application);
        panel.loadConnectionList(preferences);
        final DialogWrapper dialog = frameModule.createDialog(panel);
        WindowUtils.assignGlobalKeyListener(dialog.getWindow(), panel.getCloseButton());
        dialog.showCentered(parentComponent);
        dialog.dispose();
        panel.saveConnectionList(preferences);
        getServicePanel().setService(panel.getService());
    }

    public ServiceManagerPanel getServicePanel() {
        if (servicePanel == null) {
            servicePanel = new ServiceManagerPanel();
            servicePanel.setApplication(application);
            servicePanel.setMenuManagement(getDefaultMenuManagement());
        }

        return servicePanel;
    }

    public MenuManagement getDefaultMenuManagement() {
        return new MenuManagement() {
            @Override
            public void extendMenu(JMenu menu, Integer pluginId, String menuId, PositionMode positionMode) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void addMenuItem(Component menuItem, Integer pluginId, String menuId, PositionMode mode) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void insertMenu(JMenu menu, Integer pluginId, PositionMode positionMode) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void extendToolBar(JToolBar toolBar) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void insertMainPopupMenu(JPopupMenu popupMenu, int position) {
                // Temporary
                GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
                menuModule.fillPopupMenu(popupMenu, position);
            }
        };
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
}
