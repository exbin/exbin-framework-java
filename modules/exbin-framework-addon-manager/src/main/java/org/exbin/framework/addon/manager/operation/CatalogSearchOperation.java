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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.ModuleProvider;
import org.exbin.framework.addon.manager.AddonManager;
import org.exbin.framework.addon.manager.api.AddonCatalogService;
import org.exbin.framework.addon.manager.api.AddonCatalogServiceException;
import org.exbin.framework.addon.manager.api.AddonRecord;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.basic.BasicModuleProvider;
import org.exbin.framework.operation.api.CancellableOperation;
import org.exbin.framework.operation.api.TitledOperation;

/**
 * Search in catalog operation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogSearchOperation implements Runnable, CancellableOperation, TitledOperation {

    protected AddonManager addonManager;
    protected AddonCatalogService addonCatalogService;
    protected boolean cancelled = false;
    protected final String searchCondition;
    protected List<AddonRecord> searchResult;

    public CatalogSearchOperation(AddonCatalogService addonCatalogService, AddonManager addonManager, String searchCondition) {
        this.addonCatalogService = addonCatalogService;
        this.addonManager = addonManager;
        this.searchCondition = searchCondition;
    }

    @Override
    public void run() {
        try {
            searchResult = addonCatalogService.searchForAddons(searchCondition);
            for (int i = searchResult.size() - 1; i >= 0; i--) {
                AddonRecord record = searchResult.get(i);
                ModuleProvider moduleProvider = App.getModuleProvider();
                if (((BasicModuleProvider) moduleProvider).hasModule(record.getId()) && !addonManager.isModuleRemoved(record.getId())) {
                    searchResult.remove(i);
                } else {
                    AvailableModuleUpdates availableModuleUpdates = addonManager.getAvailableModuleUpdates();
                    if (availableModuleUpdates.getStatus() != -1) {
                        record.setUpdateAvailable(availableModuleUpdates.isUpdateAvailable(record.getId(), record.getVersion()));
                    }
                }
            }
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

    @Override
    public String getTitle() {
        return "Searching";
    }
}
