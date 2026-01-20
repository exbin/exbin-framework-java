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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.addon.manager.api.UpdateRecord;
import org.exbin.framework.addon.manager.api.AddonCatalogService;
import org.exbin.framework.addon.manager.api.AddonCatalogServiceException;
import org.exbin.framework.operation.api.CancellableOperation;

/**
 * Update availability operation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UpdateAvailabilityOperation implements Runnable, CancellableOperation {

    private AddonCatalogService addonCatalogService;
    private boolean cancelled = false;
    private List<UpdateRecord> updateRecords;

    public UpdateAvailabilityOperation(AddonCatalogService addonCatalogService) {
        this.addonCatalogService = addonCatalogService;
    }

    @Override
    public void run() {
        try {
            updateRecords = addonCatalogService.getUpdateRecords();
        } catch (AddonCatalogServiceException ex) {
            Logger.getLogger(UpdateAvailabilityOperation.class.getName()).log(Level.SEVERE, null, ex);
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
    public List<UpdateRecord> getUpdateRecords() {
        return updateRecords;
    }

    @Nonnull
    public Map<String, String> getLatestVersions() {
        Map<String, String> latestVersions = new HashMap<>();
        if (updateRecords != null) {
            for (UpdateRecord record : updateRecords) {
                latestVersions.put(record.getModuleId(), record.getVersion());
            }
        }
        return latestVersions;
    }
}
