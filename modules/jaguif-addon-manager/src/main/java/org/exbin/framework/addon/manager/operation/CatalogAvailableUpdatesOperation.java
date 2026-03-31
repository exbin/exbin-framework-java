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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.addon.manager.AddonManager;
import org.exbin.framework.addon.manager.api.AddonCatalogService;
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
    protected final Output output;
    protected boolean cancelled = false;

    public CatalogAvailableUpdatesOperation(AddonCatalogService addonCatalogService, AddonManager addonManager, int catalogRevision, Output output) {
        this.addonCatalogService = addonCatalogService;
        this.addonManager = addonManager;
        this.catalogRevision = catalogRevision;
        this.output = output;
    }

    @Override
    public void run() {
        AvailableModuleUpdates availableModuleUpdates = addonManager.getAvailableModuleUpdates();
        if (catalogRevision > availableModuleUpdates.getRevision()) {
            UpdateAvailabilityOperation availabilityOperation = new UpdateAvailabilityOperation(addonCatalogService);
            availabilityOperation.run();
            availableModuleUpdates.setLatestVersion(catalogRevision, availabilityOperation.getLatestVersions());
            availableModuleUpdates.writeConfigFile();
            output.latestVersionsChanged();
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

    @Nonnull
    @Override
    public String getTitle() {
        return addonManager.getResourceBundle().getString("catalogAvailableUpdatesOperation");
    }

    @ParametersAreNonnullByDefault
    public interface Output {

        void latestVersionsChanged();
    }
}
