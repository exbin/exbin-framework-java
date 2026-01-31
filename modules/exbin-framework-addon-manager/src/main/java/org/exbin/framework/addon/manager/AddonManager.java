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
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.exbin.framework.addon.manager.operation.UpdateAvailabilityOperation;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.addon.manager.api.AddonCatalogService;
import org.exbin.framework.addon.manager.api.AddonCatalogServiceException;
import org.exbin.framework.basic.BasicModuleProvider;
import org.exbin.framework.basic.ModuleRecord;
import org.exbin.framework.ApplicationBundleKeys;
import org.exbin.framework.addon.manager.api.AddonManagerTab;
import org.exbin.framework.addon.manager.gui.AddonsCartPanel;
import org.exbin.framework.addon.manager.gui.AddonsManagerPanel;

/**
 * Addon manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManager {

    protected java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonManager.class);

    protected AddonCatalogService addonCatalogService;
    protected AddonsManagerPanel addonsManagerPanel = new AddonsManagerPanel();
    protected CatalogOperation catalogOperation = CatalogOperation.IDLE;
    protected ApplicationModulesUsage applicationModulesUsage;
    protected AvailableModuleUpdates availableModuleUpdates = new AvailableModuleUpdates();
    protected AddonUpdateChanges addonUpdateChanges = new AddonUpdateChanges();
    protected List<ItemRecord> installedAddons = new ArrayList<>();
    protected int serviceStatus = -1;
    protected final List<AddonOperation> cartOperations = new ArrayList<>();

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
                AddonManagerTab managerTab = addonsManagerPanel.getActiveTab();
                if (managerTab instanceof AddonsCatalogTab) {
                    // controlPanel.setOperationCount(((AddonsCatalogTab) managerTab).getToInstallCount());
                } else if (managerTab instanceof AddonsInstalledTab) {
                    // controlPanel.setOperationCount(((AddonsInstalledTab) managerTab).getToUpdateCount());
                } else {
                    throw new IllegalStateException();
                }
            }

            @Override
            public void openCatalog() {
                AddonManagerTab managerTab = addonsManagerPanel.getActiveTab();
            }

            @Override
            public void openCart() {
                cartPanel.setCartItems(getCartOperations());
            }

            @Override
            public void setFilter(String filter, Runnable finished) {
                // TODO
            }

            @Override
            public void setSearch(String search, Runnable finished) {
                // TODO
            }
        });

        cartPanel.setController(new AddonsCartPanel.Controller() {
            @Override
            public void runOperations() {
                // addonManager.performAddonsOperation(addonManagerPanel);

                AddonManagerTab managerTab = addonsManagerPanel.getActiveTab();
                managerTab.notifyChanged();

//                if (managerTab instanceof AddonsCatalogTab) {
//                    ((AddonsCatalogTab) managerTab).installAddons();
//                } else if (managerTab instanceof AddonsInstalledTab) {
//                    ((AddonsInstalledTab) managerTab).updateAddons();
//                } else {
//                    throw new IllegalStateException();
//                }
            }
        });

        AddonsCatalogTab addonsManagerTab = new AddonsCatalogTab();
        addonsManagerTab.setAddonManager(this);
        addonsManagerPanel.addManagerTab(addonsManagerTab);

        AddonsInstalledTab installedManagerTab = new AddonsInstalledTab();
        installedManagerTab.setAddonManager(this);
        addonsManagerPanel.addManagerTab(installedManagerTab);
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
        this.addonCatalogService = addonCatalogService;
        addonsManagerPanel.setCatalogUrl(getServiceUrl());

        Thread thread = new Thread(() -> {
            try {
                ResourceBundle appBundle = App.getAppBundle();
                String releaseString = appBundle.getString(ApplicationBundleKeys.APPLICATION_RELEASE);
                serviceStatus = addonCatalogService.checkStatus(releaseString);
            } catch (AddonCatalogServiceException ex) {
                Logger.getLogger(AddonManager.class.getName()).log(Level.SEVERE, "Status check failed", ex);
                serviceStatus = -1;
            }
            // TODO
            // controlPanel.showLegacyWarning();
            if (serviceStatus == -1) {
                // TODO controlPanel.showManualOnlyWarning();
            } else {
                if (serviceStatus > availableModuleUpdates.getStatus()) {
                    UpdateAvailabilityOperation availabilityOperation = new UpdateAvailabilityOperation(addonCatalogService);
                    availabilityOperation.run();
                    availableModuleUpdates.setLatestVersion(serviceStatus, availabilityOperation.getLatestVersions());
                    availableModuleUpdates.writeConfigFile();
                }
            }
        });
        thread.start();
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
    public String getModuleDetails(ItemRecord itemRecord) {
        if (itemRecord.isAddon()) {
            try {
                return addonCatalogService.getModuleDetails(itemRecord.getId());
            } catch (AddonCatalogServiceException ex) {
                Logger.getLogger(AddonManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return "";
    }

    @Nonnull
    public List<AddonRecord> searchForAddons() throws AddonCatalogServiceException {
        if (serviceStatus == -1) {
            return new ArrayList<>();
        }

        List<AddonRecord> searchResult = addonCatalogService.searchForAddons("");
        for (int i = searchResult.size() - 1; i >= 0; i--) {
            AddonRecord record = searchResult.get(i);
            ModuleProvider moduleProvider = App.getModuleProvider();
            if (((BasicModuleProvider) moduleProvider).hasModule(record.getId()) && !addonUpdateChanges.hasRemoveAddon(record.getId())) {
                searchResult.remove(i);
            } else {
                if (availableModuleUpdates.getStatus() != -1) {
                    record.setUpdateAvailable(availableModuleUpdates.isUpdateAvailable(record.getId(), record.getVersion()));
                }
            }
        }
        return searchResult;
    }

    private void invokeCatalogOperation(CatalogOperation operation) {

    }

    @Nonnull
    public String getServiceUrl() {
        return addonCatalogService.getCatalogPageUrl();
    }

    @Nonnull
    public List<AddonOperation> getCartOperations() {
        return cartOperations;
    }

    private class CatalogThread extends Thread {

        public CatalogThread() {
            super("AddonCatalogThread");
        }

        @Override
        public void run() {
            switch (catalogOperation) {
                case CHECK:
                    try {
                        ResourceBundle appBundle = App.getAppBundle();
                        String releaseString = appBundle.getString(ApplicationBundleKeys.APPLICATION_RELEASE);
                        addonCatalogService.checkStatus(releaseString);
                    } catch (AddonCatalogServiceException ex) {
                        Logger.getLogger(AddonManager.class.getName()).log(Level.SEVERE, "Status check failed", ex);
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Not supported yet.");
            }
        }
    }

    private enum CatalogOperation {
        IDLE,
        CHECK
    }
}
