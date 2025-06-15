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
package org.exbin.framework.addon.manager.service;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.model.UpdateRecord;
import org.exbin.framework.addon.manager.operation.DownloadOperation;
import org.exbin.framework.addon.manager.operation.model.DownloadItemRecord;

/**
 * Addon catalog service.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface AddonCatalogService {

    /**
     * Checks whether service supports specific catalog version.
     *
     * @param version checked version
     * @return -1 for failure, greater number for revisions
     * @throws AddonCatalogServiceException when service fails
     */
    int checkStatus(String version) throws AddonCatalogServiceException;

    /**
     * Request search for addons with option to specifi search condition for
     * name.
     *
     * @param searchCondition search condition
     * @return list of found addons
     * @throws AddonCatalogServiceException when service fails
     */
    @Nonnull
    List<AddonRecord> searchForAddons(String searchCondition) throws AddonCatalogServiceException;

    /**
     * Returns simplified record of specific addon with depedency / license info
     * only.
     *
     * @param moduleId module id
     * @return addon record
     * @throws AddonCatalogServiceException when service fails
     */
    @Nonnull
    AddonRecord getAddonDependency(String moduleId) throws AddonCatalogServiceException;

    /**
     * Returns module filename for specific addon.
     *
     * @param moduleId module id
     * @return addon filename
     * @throws AddonCatalogServiceException when service fails
     */
    @Nonnull
    String getAddonFile(String moduleId) throws AddonCatalogServiceException;

    /**
     * Returns update records for all addons.
     *
     * @return update records
     * @throws AddonCatalogServiceException when service fails
     */
    @Nonnull
    List<UpdateRecord> getUpdateRecords() throws AddonCatalogServiceException;

    /**
     * Returns module details text.
     *
     * @param id module id
     * @return details text
     * @throws AddonCatalogServiceException when service fails
     */
    @Nonnull
    String getModuleDetails(String id) throws AddonCatalogServiceException;

    /**
     * Creates download operation.
     *
     * @param records download records
     * @return operation handler
     */
    @Nonnull
    DownloadOperation createDownloadsOperation(List<DownloadItemRecord> records);

//    @Nonnull
//    CancellableOperation createIconsDownloadOperation(List<AddonRecord> records);
}
