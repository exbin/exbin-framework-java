/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.addon.packs;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.addon.manager.api.AddonManagerModuleApi;
import org.exbin.jaguif.addon.packs.page.AddonPacksPage;
import org.exbin.jaguif.contribution.api.RelativeSequenceContributionRule;
import org.exbin.jaguif.tabpages.api.TabPagesDefinitionManagement;
import org.exbin.jaguif.tabpages.api.TabPagesModuleApi;

/**
 * Addon manager with support for packs.
 */
@ParametersAreNonnullByDefault
public class AddonPacksModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(AddonPacksModule.class);

    public AddonPacksModule() {
    }

    public void registerAddonManagerPages() {
        TabPagesModuleApi tabPagesModule = App.getModule(TabPagesModuleApi.class);
        TabPagesDefinitionManagement pagesDefinitions = tabPagesModule.getMainTabPagesDefinition(AddonManagerModuleApi.ADDON_MANAGER_TABPAGES_ID, AddonManagerModuleApi.MODULE_ID);
        AddonPacksPage.Contribution pageContribution = new AddonPacksPage.Contribution();
        pagesDefinitions.registerTabPagesContribution(pageContribution);
        pagesDefinitions.registerTabPagesRule(pageContribution, new RelativeSequenceContributionRule(RelativeSequenceContributionRule.NextToMode.AFTER, "addonsCatalog" /*AddonsCatalogPage.PAGE_ID */));
    }
}
