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
package org.exbin.framework.help.local;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.help.local.action.HelpLocalAction;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.MenuContribution;
import org.exbin.framework.action.api.MenuManagement;
import org.exbin.framework.action.api.PositionMenuContributionRule;

/**
 * Local help module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class HelpLocalModule {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(HelpLocalModule.class);

    public HelpLocalModule() {
    }

    @Nonnull
    public HelpLocalAction createHelpLocalAction() {
        HelpLocalAction helpAction = new HelpLocalAction();
        helpAction.setup();
        return helpAction;
    }

    public void registerMainMenu() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(ActionConsts.HELP_MENU_ID, createHelpLocalAction());
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.TOP));
    }
}
