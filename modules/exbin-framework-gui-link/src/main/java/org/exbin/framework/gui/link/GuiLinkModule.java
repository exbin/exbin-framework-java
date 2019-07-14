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
package org.exbin.framework.gui.link;

import java.awt.event.ActionEvent;
import java.net.URL;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.link.api.GuiLinkModuleApi;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.menu.api.MenuPosition;
import org.exbin.framework.gui.menu.api.PositionMode;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.BareBonesBrowserLaunch;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Implementation of XBUP framework link support module.
 *
 * @version 0.2.0 2016/07/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiLinkModule implements GuiLinkModuleApi {

    private XBApplication application;
    private final java.util.ResourceBundle bundle = LanguageUtils.getResourceBundleByClass(GuiLinkModule.class);
    private URL helpUrl;

    private Action onlineHelpAction;

    public GuiLinkModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    @Override
    public Action getOnlineHelpAction() {
        if (onlineHelpAction == null) {
            onlineHelpAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BareBonesBrowserLaunch.openURL(helpUrl.toExternalForm());
                }
            };
            ActionUtils.setupAction(onlineHelpAction, bundle, "onlineHelpAction");
            onlineHelpAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        }

        return onlineHelpAction;
    }

    @Override
    public void registerOnlineHelpMenu() {
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.HELP_MENU_ID, MODULE_ID, getOnlineHelpAction(), new MenuPosition(PositionMode.TOP));
    }

    @Override
    public void setOnlineHelpUrl(URL helpUrl) {
        this.helpUrl = helpUrl;
    }
}
