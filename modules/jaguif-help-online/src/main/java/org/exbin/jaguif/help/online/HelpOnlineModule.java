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
package org.exbin.jaguif.help.online;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.help.api.HelpLink;
import org.exbin.jaguif.help.api.HelpModuleApi;
import org.exbin.jaguif.help.online.action.OnlineHelpAction;
import org.exbin.jaguif.help.online.api.HelpOnlineModuleApi;
import org.exbin.jaguif.help.online.contribution.OnlineHelpContribution;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.utils.DesktopUtils;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;

/**
 * Implementation of the online help support module.
 */
@ParametersAreNonnullByDefault
public class HelpOnlineModule implements HelpOnlineModuleApi {

    private URL helpUrl;

    public HelpOnlineModule() {
    }

    @Nonnull
    @Override
    public OnlineHelpAction createOnlineHelpAction() {
        OnlineHelpAction onlineHelpAction = new OnlineHelpAction();
        onlineHelpAction.setOnlineHelpUrl(helpUrl);
        return onlineHelpAction;
    }

    @Override
    public void registerOnlineHelpMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.HELP_SUBMENU_ID);
        SequenceContribution contribution = new OnlineHelpContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    @Override
    public void setOnlineHelpUrl(URL helpUrl) {
        this.helpUrl = helpUrl;
    }

    @Override
    public void openHelpLink(@Nullable HelpLink helpLink) {
        URL targetUrl = helpUrl;
        if (helpLink != null) {
            try {
                targetUrl = helpUrl.toURI().resolve("#" + helpLink.getHelpId()).toURL();
            } catch (MalformedURLException | URISyntaxException ex) {
                Logger.getLogger(HelpOnlineModule.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        DesktopUtils.openDesktopURL(targetUrl.toExternalForm());
    }

    @Override
    public void registerOpeningHandler() {
        HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
        helpModule.setFallbackOpeningHandler(this::openHelpLink);
    }
}
