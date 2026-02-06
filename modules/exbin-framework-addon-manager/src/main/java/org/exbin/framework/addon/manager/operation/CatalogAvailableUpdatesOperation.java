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
package org.exbin.framework.addon.manager.operation;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.ApplicationBundleKeys;
import org.exbin.framework.addon.manager.AddonManager;
import org.exbin.framework.addon.manager.api.AddonCatalogService;
import org.exbin.framework.addon.manager.api.AddonCatalogServiceException;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.operation.api.CancellableOperation;
import org.exbin.framework.operation.api.TitledOperation;

/**
 * Operation to get available updates from catalog.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogAvailableUpdatesOperation implements Runnable, CancellableOperation, TitledOperation {

    protected final AddonCatalogService addonCatalogService;
    protected final AddonManager addonManager;
    protected final int catalogRevision;
    protected boolean cancelled = false;

    public CatalogAvailableUpdatesOperation(AddonCatalogService addonCatalogService, AddonManager addonManager, int catalogRevision) {
        this.addonCatalogService = addonCatalogService;
        this.addonManager = addonManager;
        this.catalogRevision = catalogRevision;
    }

    @Override
    public void run() {
        try {
            AvailableModuleUpdates availableModuleUpdates = addonManager.getAvailableModuleUpdates();
            if (catalogRevision > availableModuleUpdates.getRevision()) {
                UpdateAvailabilityOperation availabilityOperation = new UpdateAvailabilityOperation(addonCatalogService);
                availabilityOperation.run();
                availableModuleUpdates.setLatestVersion(catalogRevision, availabilityOperation.getLatestVersions());
                availableModuleUpdates.writeConfigFile();
            }
            ResourceBundle appBundle = App.getAppBundle();
            String releaseString = appBundle.getString(ApplicationBundleKeys.APPLICATION_RELEASE);
            addonCatalogService.checkStatus(releaseString);
        } catch (AddonCatalogServiceException ex) {
            Logger.getLogger(CatalogAvailableUpdatesOperation.class.getName()).log(Level.SEVERE, "Status check failed", ex);
        }
    }

    @Override
    public void cancelOperation() {
        cancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public String getTitle() {
        return "Getting updates";
    }
}
