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
