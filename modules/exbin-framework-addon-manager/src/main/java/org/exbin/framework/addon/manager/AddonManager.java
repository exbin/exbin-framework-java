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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.ModuleProvider;
import org.exbin.framework.addon.AddonModuleFileLocation;
import org.exbin.framework.addon.manager.api.AddonRecord;
import org.exbin.framework.addon.manager.model.AddonUpdateChanges;
import org.exbin.framework.addon.manager.api.DependencyRecord;
import org.exbin.framework.addon.manager.api.ItemRecord;
import org.exbin.framework.addon.manager.operation.ApplicationModulesUsage;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.addon.manager.api.AddonCatalogService;
import org.exbin.framework.basic.BasicModuleProvider;
import org.exbin.framework.basic.ModuleRecord;
import org.exbin.framework.addon.manager.gui.AddonsCartPanel;
import org.exbin.framework.addon.manager.gui.AddonsManagerPanel;
import org.exbin.framework.addon.manager.api.AddonManagerPage;

/**
 * Addon manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManager {

    protected java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonManager.class);

    protected AddonsManagerPanel addonsManagerPanel = new AddonsManagerPanel();
    protected AvailableModuleUpdates availableModuleUpdates = new AvailableModuleUpdates();
    protected AddonUpdateChanges addonUpdateChanges = new AddonUpdateChanges();
    protected ApplicationModulesUsage applicationModulesUsage;
    protected List<ItemRecord> installedAddons = new ArrayList<>();

    protected final List<AddonOperation> cartOperations = new ArrayList<>();
    protected final List<AddonManagerPage> managerPages = new ArrayList<>();

    public AddonManager() {
    }

    public void init() {
        availableModuleUpdates.readConfigFile();
        addonUpdateChanges.readConfigFile();

        ModuleProvider moduleProvider = App.getModuleProvider();
        if (moduleProvider instanceof BasicModuleProvider) {
            List<ModuleRecord> basicModulesList = ((BasicModuleProvider) moduleProvider).getModulesList();
            for (ModuleRecord moduleRecord : basicModulesList) {
                AddonRecord itemRecord = new AddonRecord(moduleRecord.getModuleId(), moduleRecord.getName());
                itemRecord.setInstalled(true);
                itemRecord.setAddon(moduleRecord.getFileLocation() == AddonModuleFileLocation.ADDON);
                itemRecord.setVersion(moduleRecord.getVersion());
                itemRecord.setProvider(moduleRecord.getProvider().orElse(null));
                itemRecord.setHomepage(moduleRecord.getHomepage().orElse(null));
                itemRecord.setDescription(moduleRecord.getDescription().orElse(null));
                itemRecord.setIcon(moduleRecord.getIcon().orElse(null));
                List<DependencyRecord> dependencyRecords = new ArrayList<>();
                for (String dependencyModuleId : moduleRecord.getDependencyModuleIds()) {
                    dependencyRecords.add(new DependencyRecord(dependencyModuleId));
                }
                for (String dependencyLibraryId : moduleRecord.getDependencyLibraries()) {
                    dependencyRecords.add(new DependencyRecord(DependencyRecord.Type.JAR_LIBRARY, dependencyLibraryId));
                }
                itemRecord.setDependencies(dependencyRecords);
                installedAddons.add(itemRecord);
                /*System.out.println(moduleRecord.getModuleId() + "," + moduleRecord.getName() + "," + moduleRecord.getDescription().orElse("") + "," + moduleRecord.getVersion() + "," + moduleRecord.getHomepage().orElse(""));
                for (DependencyRecord dependency : dependencyRecords) {
                    System.out.println("- " + dependency.getType().name() + ", " + dependency.getId());
                } */
            }
            applicationModulesUsage = new ApplicationModulesUsage() {
                @Override
                public boolean hasModule(String moduleId) {
                    return ((BasicModuleProvider) moduleProvider).hasModule(moduleId);
                }

                @Override
                public boolean hasLibrary(String libraryFileName) {
                    return ((BasicModuleProvider) moduleProvider).hasLibrary(libraryFileName);
                }
            };
        }

        AvailableModuleUpdates.AvailableModulesChangeListener availableModulesChangeListener = (AvailableModuleUpdates checker) -> {
            int availableUpdates = 0;
            for (ItemRecord installedAddon : installedAddons) {
                if (checker.isUpdateAvailable(installedAddon.getId(), installedAddon.getVersion())) {
                    availableUpdates++;
                }
            }
            // TODO controlPanel.setAvailableUpdates(availableUpdates);
        };

        availableModuleUpdates.addChangeListener(availableModulesChangeListener);
        availableModuleUpdates.notifyChanged();

        AddonsCartPanel cartPanel = new AddonsCartPanel();
        addonsManagerPanel.setPreferredSize(new Dimension(800, 500));
        addonsManagerPanel.setCartComponent(cartPanel);
        addonsManagerPanel.setController(new AddonsManagerPanel.Controller() {
            @Override
            public void tabSwitched() {
                AddonManagerPage managerTab = addonsManagerPanel.getActiveTab();
                if (managerTab instanceof AddonsCatalogPage) {
                    // controlPanel.setOperationCount(((AddonsCatalogPage) managerTab).getToInstallCount());
                } else if (managerTab instanceof AddonsInstalledPage) {
                    // controlPanel.setOperationCount(((AddonsInstalledPage) managerTab).getToUpdateCount());
                } else {
                    throw new IllegalStateException();
                }
            }

            @Override
            public void openCatalog() {
                AddonManagerPage managerTab = addonsManagerPanel.getActiveTab();
            }

            @Override
            public void openCart() {
                cartPanel.setCartItems(getCartOperations());
            }

            @Override
            public void setFilter(String filter, Runnable finished) {
                for (AddonManagerPage managerPage : managerPages) {
                    managerPage.setFilter(filter, finished);
                }
            }

            @Override
            public void setSearch(String search, Runnable finished) {
                for (AddonManagerPage managerPage : managerPages) {
                    managerPage.setSearch(search, finished);
                }
            }
        });

        cartPanel.setController(new AddonsCartPanel.Controller() {
            @Override
            public void runOperations() {
                // addonManager.performAddonsOperation(addonManagerPanel);

                AddonManagerPage managerTab = addonsManagerPanel.getActiveTab();
                managerTab.notifyChanged();

//                if (managerTab instanceof AddonsCatalogPage) {
//                    ((AddonsCatalogPage) managerTab).installAddons();
//                } else if (managerTab instanceof AddonsInstalledPage) {
//                    ((AddonsInstalledPage) managerTab).updateAddons();
//                } else {
//                    throw new IllegalStateException();
//                }
            }
        });

        AddonsCatalogPage catalogPage = new AddonsCatalogPage();
        catalogPage.setAddonManager(this);
        addManagerPage(catalogPage);

        AddonsInstalledPage installedPage = new AddonsInstalledPage();
        installedPage.setAddonManager(this);
        addManagerPage(installedPage);
    }

    public void addManagerPage(AddonManagerPage page) {
        managerPages.add(page);
        addonsManagerPanel.addManagerPage(page);
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Nonnull
    public AddonsManagerPanel getAddonsManagerPanel() {
        return addonsManagerPanel;
    }

    public void setAddonCatalogService(AddonCatalogService addonCatalogService) {
        addonsManagerPanel.setCatalogUrl(addonCatalogService.getCatalogPageUrl());

        for (AddonManagerPage managerPage : managerPages) {
            if (managerPage instanceof AddonsCatalogPage) {
                ((AddonsCatalogPage) managerPage).setAddonCatalogService(addonCatalogService);
            }
        }
    }

    @Nonnull
    public AvailableModuleUpdates getAvailableModuleUpdates() {
        return availableModuleUpdates;
    }

    public boolean isModuleInstalled(String moduleId) {
        return addonUpdateChanges.hasInstallAddon(moduleId) && !addonUpdateChanges.hasRemoveAddon(moduleId);
    }

    public boolean isModuleRemoved(String moduleId) {
        return addonUpdateChanges.hasRemoveAddon(moduleId) && !addonUpdateChanges.hasInstallAddon(moduleId);
    }

    public void addCartOperation(AddonOperation operation) {
        cartOperations.add(operation);
        addonsManagerPanel.setCartItemsCount(cartOperations.size());
    }

    public boolean isInCart(String moduleId, AddonOperationVariant variant) {
        for (AddonOperation cartOperation : cartOperations) {
            if (moduleId.equals(cartOperation.getItem().getId()) && variant == cartOperation.getVariant()) {
                return true;
            }
        }

        return false;
    }

    @Nonnull
    public List<ItemRecord> getInstalledAddons() {
        return installedAddons;
    }

    public void addUpdateAvailabilityListener(AvailableModuleUpdates.AvailableModulesChangeListener listener) {
        availableModuleUpdates.addChangeListener(listener);
    }

    @Nonnull
    public List<AddonOperation> getCartOperations() {
        return cartOperations;
    }
}
