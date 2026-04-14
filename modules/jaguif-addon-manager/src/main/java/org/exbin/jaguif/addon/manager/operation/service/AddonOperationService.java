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
package org.exbin.jaguif.addon.manager.operation.service;

import java.awt.Component;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.addon.manager.api.AddonRecord;
import org.exbin.jaguif.addon.manager.api.ItemRecord;
import org.exbin.jaguif.addon.manager.operation.AddonModificationsOperation;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.addon.manager.api.AddonCatalogService;
import org.exbin.jaguif.addon.manager.api.AddonCatalogServiceException;
import org.exbin.jaguif.addon.manager.AddonManager;
import org.exbin.jaguif.addon.manager.AddonOperation;
import org.exbin.jaguif.addon.manager.model.AddonUpdateChanges;
import org.exbin.jaguif.addon.manager.ApplicationModulesUsage;

/**
 * Addon operation service.
 */
@ParametersAreNonnullByDefault
public class AddonOperationService {

    protected java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonOperationService.class);

    protected AddonManager addonManager;
    protected AddonCatalogService addonCatalogService;

    public AddonOperationService(AddonManager addonManager) {
        this.addonManager = addonManager;
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setAddonCatalogService(AddonCatalogService addonCatalogService) {
        this.addonCatalogService = addonCatalogService;
    }

    @Nonnull
    public AddonModificationsOperation performAddonOperations(List<AddonOperation> operations) {
        AddonModificationsOperation modifications = createOperation();
        for (AddonOperation operation : operations) {
            switch (operation.getVariant()) {
                case INSTALL:
                    modifications.installItem(operation.getItem());
                    break;
                case UPDATE:
                    ItemRecord item = operation.getItem();
                    modifications.updateItem(item, item);
                    break;
                case REMOVE:
                    modifications.removeItem(operation.getItem());
                    break;
            }
        }
        return modifications;
    }

    @Nonnull
    public AddonModificationsOperation installItem(ItemRecord item, Component parentComponent) {
        AddonModificationsOperation operation = createOperation();
        operation.installItem(item);
        return operation;
    }

    @Nonnull
    public AddonModificationsOperation updateItem(ItemRecord item) {
        AddonModificationsOperation operation = createOperation();
        AddonRecord addonRecord;
        try {
            addonRecord = addonCatalogService.getAddonDependency(item.getId());
            operation.updateItem(addonRecord, item);
        } catch (AddonCatalogServiceException ex) {
            Logger.getLogger(AddonOperationService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return operation;
    }

    @Nonnull
    public AddonModificationsOperation removeItem(ItemRecord item) {
        AddonModificationsOperation operation = createOperation();
        operation.removeItem(item);
        return operation;
    }

    @Nonnull
    public AddonModificationsOperation installAddons(Set<String> toInstall) {
        List<ItemRecord> installedAddons = addonManager.getInstalledAddons();
        AddonModificationsOperation operation = createOperation();
        if (toInstall.isEmpty()) {
            for (ItemRecord addon : installedAddons) {
                if (addon.isUpdateAvailable()) {
                    AddonRecord addonRecord;
                    try {
                        addonRecord = addonCatalogService.getAddonDependency(addon.getId());
                        operation.updateItem(addonRecord, addon);
                    } catch (AddonCatalogServiceException ex) {
                        Logger.getLogger(AddonOperationService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            for (String addonId : toInstall) {
                AddonRecord addonRecord;
                try {
                    addonRecord = addonCatalogService.getAddonDependency(addonId);
                    operation.installItem(addonRecord);
                } catch (AddonCatalogServiceException ex) {
                    Logger.getLogger(AddonOperationService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return operation;
    }

    @Nonnull
    public AddonModificationsOperation updateAddons(Set<String> toUpdate) {
        List<ItemRecord> installedAddons = addonManager.getInstalledAddons();
        AddonModificationsOperation operation = createOperation();
        if (toUpdate.isEmpty()) {
            for (ItemRecord addon : installedAddons) {
                if (addon.isUpdateAvailable()) {
                    AddonRecord addonRecord;
                    try {
                        addonRecord = addonCatalogService.getAddonDependency(addon.getId());
                        operation.updateItem(addonRecord, addon);
                    } catch (AddonCatalogServiceException ex) {
                        Logger.getLogger(AddonOperationService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            for (ItemRecord addon : installedAddons) {
                if (toUpdate.contains(addon.getId())) {
                    AddonRecord addonRecord;
                    try {
                        addonRecord = addonCatalogService.getAddonDependency(addon.getId());
                        operation.updateItem(addonRecord, addon);
                    } catch (AddonCatalogServiceException ex) {
                        Logger.getLogger(AddonOperationService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return operation;
    }

    @Nonnull
    private AddonModificationsOperation createOperation() {
        AddonUpdateChanges addonUpdateChanges = addonManager.getAddonUpdateChanges();
        ApplicationModulesUsage applicationModulesUsage = addonManager.getApplicationModulesUsage();
        return new AddonModificationsOperation(addonCatalogService, applicationModulesUsage, addonUpdateChanges);
    }
}
