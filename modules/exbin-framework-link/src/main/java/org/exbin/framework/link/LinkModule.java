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
package org.exbin.framework.link;

import java.net.URL;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.link.action.OnlineHelpAction;
import org.exbin.framework.link.api.LinkModuleApi;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.action.api.ActionModuleApi;

/**
 * Implementation of XBUP framework link support module.
 *
 * @version 0.2.0 2020/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class LinkModule implements LinkModuleApi {

    private XBApplication application;
    private URL helpUrl;

    private OnlineHelpAction onlineHelpAction;

    public LinkModule() {
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
    public OnlineHelpAction getOnlineHelpAction() {
        if (onlineHelpAction == null) {
            onlineHelpAction = new OnlineHelpAction();
            onlineHelpAction.setOnlineHelpUrl(helpUrl);
        }

        return onlineHelpAction;
    }

    @Override
    public void registerOnlineHelpMenu() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(FrameModuleApi.HELP_MENU_ID, MODULE_ID, getOnlineHelpAction(), new MenuPosition(PositionMode.TOP));
    }

    @Override
    public void setOnlineHelpUrl(URL helpUrl) {
        this.helpUrl = helpUrl;
    }
}
