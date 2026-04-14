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
package org.exbin.jaguif.help.local;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.help.local.action.HelpLocalAction;
import org.exbin.jaguif.help.local.contribution.HelpLocalContribution;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;

/**
 * Local help module.
 */
@ParametersAreNonnullByDefault
public class HelpLocalModule {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(HelpLocalModule.class);

    public HelpLocalModule() {
    }

    @Nonnull
    public HelpLocalAction createHelpLocalAction() {
        HelpLocalAction helpAction = new HelpLocalAction();
        helpAction.init();
        return helpAction;
    }

    public void registerMainMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.HELP_SUBMENU_ID);
        SequenceContribution contribution = new HelpLocalContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }
}
