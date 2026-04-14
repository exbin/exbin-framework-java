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
package org.exbin.jaguif.addon.manager.operation;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.ModuleProvider;
import org.exbin.jaguif.addon.manager.AddonManager;
import org.exbin.jaguif.addon.manager.api.AddonCatalogService;
import org.exbin.jaguif.addon.manager.api.AddonCatalogServiceException;
import org.exbin.jaguif.addon.manager.api.AddonRecord;
import org.exbin.jaguif.addon.manager.model.AvailableModuleUpdates;
import org.exbin.jaguif.basic.BasicModuleProvider;
import org.exbin.jaguif.operation.api.CancellableOperation;
import org.exbin.jaguif.operation.api.ProgressOperation;
import org.exbin.jaguif.operation.api.TitledOperation;

/**
 * Operation to search in catalog.
 */
@ParametersAreNonnullByDefault
public class CatalogSearchOperation implements Runnable, CancellableOperation, ProgressOperation, TitledOperation {

    protected final AddonManager addonManager;
    protected final AddonCatalogService addonCatalogService;
    protected final Output output;
    protected boolean cancelled = false;
    protected final String searchCondition;

    public CatalogSearchOperation(AddonCatalogService addonCatalogService, AddonManager addonManager, String searchCondition, Output output) {
        this.addonCatalogService = addonCatalogService;
        this.addonManager = addonManager;
        this.searchCondition = searchCondition;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            List<AddonRecord> searchResult = addonCatalogService.searchForAddons(searchCondition);
            for (int i = searchResult.size() - 1; i >= 0; i--) {
                AddonRecord record = searchResult.get(i);
                ModuleProvider moduleProvider = App.getModuleProvider();
                if (((BasicModuleProvider) moduleProvider).hasModule(record.getId()) && !addonManager.isModuleRemoved(record.getId())) {
                    searchResult.remove(i);
                } else {
                    AvailableModuleUpdates availableModuleUpdates = addonManager.getAvailableModuleUpdates();
                    if (availableModuleUpdates.getRevision() != -1) {
                        record.setUpdateAvailable(availableModuleUpdates.isUpdateAvailable(record.getId(), record.getVersion()));
                    }
                }
            }
            output.outputItems(searchResult);
        } catch (AddonCatalogServiceException ex) {
            Logger.getLogger(CatalogSearchOperation.class.getName()).log(Level.SEVERE, null, ex);
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
        return addonManager.getResourceBundle().getString("catalogSearchOperation");
    }

    @Override
    public int getOperationProgress() {
        return -1;
    }

    public interface Output {

        void outputItems(List<AddonRecord> addonItems);
    }
}
