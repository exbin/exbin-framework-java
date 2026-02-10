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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.addon.manager.AddonManager;
import org.exbin.framework.addon.manager.api.AddonCatalogService;
import org.exbin.framework.addon.manager.api.AddonCatalogServiceException;
import org.exbin.framework.addon.manager.api.ItemRecord;
import org.exbin.framework.operation.api.CancellableOperation;
import org.exbin.framework.operation.api.ProgressOperation;
import org.exbin.framework.operation.api.TitledOperation;

/**
 * Operation to receive details about module from catalog.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogModuleDetailOperation implements Runnable, CancellableOperation, ProgressOperation, TitledOperation {

    protected final AddonManager addonManager;
    protected final AddonCatalogService addonCatalogService;
    protected final Output output;
    protected boolean cancelled = false;
    protected final ItemRecord itemRecord;

    public CatalogModuleDetailOperation(AddonCatalogService addonCatalogService, AddonManager addonManager, ItemRecord itemRecord, Output output) {
        this.addonCatalogService = addonCatalogService;
        this.addonManager = addonManager;
        this.itemRecord = itemRecord;
        this.output = output;
    }

    @Override
    public void run() {
        if (itemRecord.isAddon()) {
            try {
                String moduleDetail = addonCatalogService.getModuleDetails(itemRecord.getId());
                output.outputModuleDetail(moduleDetail);
            } catch (AddonCatalogServiceException ex) {
                Logger.getLogger(CatalogModuleDetailOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        return addonManager.getResourceBundle().getString("catalogModuleDetailOperation");
    }

    @Override
    public int getOperationProgress() {
        return -1;
    }

    public interface Output {

        void outputModuleDetail(String details);
    }
}
