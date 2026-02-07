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
import org.exbin.framework.addon.manager.api.AddonCatalogService;
import org.exbin.framework.addon.manager.api.AddonCatalogServiceException;
import org.exbin.framework.operation.api.CancellableOperation;
import org.exbin.framework.operation.api.TitledOperation;

/**
 * Operation to check status of catalog.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogCheckStatusOperation implements Runnable, CancellableOperation, TitledOperation {

    protected final AddonCatalogService addonCatalogService;
    protected final Output output;
    protected boolean cancelled = false;

    public CatalogCheckStatusOperation(AddonCatalogService addonCatalogService, Output output) {
        this.addonCatalogService = addonCatalogService;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            ResourceBundle appBundle = App.getAppBundle();
            String releaseString = appBundle.getString(ApplicationBundleKeys.APPLICATION_RELEASE);
            int catalogRevision = addonCatalogService.checkStatus(releaseString);
            output.outputStatus(catalogRevision);
        } catch (AddonCatalogServiceException ex) {
            Logger.getLogger(CatalogCheckStatusOperation.class.getName()).log(Level.SEVERE, "Status check failed", ex);
            output.outputStatus(-1);
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
        return "Checking catalog";
    }

    public interface Output {

        void outputStatus(int status);
    }
}
