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
package org.exbin.framework.help.online;

import java.net.URL;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.help.online.action.OnlineHelpAction;
import org.exbin.framework.help.online.api.HelpOnlineModuleApi;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.ActionModuleApi;

/**
 * Implementation of XBUP framework online help support module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class HelpOnlineModule implements HelpOnlineModuleApi {

    private URL helpUrl;

    private OnlineHelpAction onlineHelpAction;

    public HelpOnlineModule() {
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
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(WindowModuleApi.HELP_MENU_ID, MODULE_ID, getOnlineHelpAction(), new MenuPosition(PositionMode.TOP));
    }

    @Override
    public void setOnlineHelpUrl(URL helpUrl) {
        this.helpUrl = helpUrl;
    }
}
