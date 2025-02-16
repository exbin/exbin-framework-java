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
package org.exbin.framework.addon.manager;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.menu.MenuContribution;
import org.exbin.framework.action.api.menu.MenuManagement;
import org.exbin.framework.action.api.menu.PositionMenuContributionRule;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.addon.manager.action.AddonManagerAction;
import org.exbin.framework.addon.manager.api.AddonManagerModuleApi;
import org.exbin.framework.addon.manager.options.page.AddonManagerOptionsPage;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPageManagement;

/**
 * Implementation of addon manager module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManagerModule implements AddonManagerModuleApi {

    private static boolean devMode = false;

    public AddonManagerModule() {
    }

    @Nonnull
    @Override
    public Action createAddonManagerAction() {
        return new AddonManagerAction();
    }

    @Override
    public void registerAddonManagerMenuItem() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(ActionConsts.TOOLS_MENU_ID, createAddonManagerAction());
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.MIDDLE_LAST));
    }

    @Override
    public boolean isDevMode() {
        return devMode;
    }

    @Override
    public void setDevMode(boolean devMode) {
        AddonManagerModule.devMode = devMode;
    }

    @Override
    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(MODULE_ID);
        AddonManagerOptionsPage addonManagerOptionsPage = new AddonManagerOptionsPage();
        optionsPageManagement.registerPage(addonManagerOptionsPage);
    }

/*
    @Override
    public void setUpdateUrl(URL updateUrl) {
        this.checkUpdateUrl = updateUrl;
        if (checkUpdateAction != null) {
            checkUpdateAction.setUpdateUrl(updateUrl);
        }
    }

    @Nullable
    @Override
    public URL getUpdateUrl() {
        return checkUpdateUrl;
    }
     */
}
