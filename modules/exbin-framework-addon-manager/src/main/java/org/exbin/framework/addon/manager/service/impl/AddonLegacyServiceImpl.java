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
package org.exbin.framework.addon.manager.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.operation.DownloadOperation;
import org.exbin.framework.addon.manager.operation.model.DownloadItemRecord;
import org.exbin.framework.addon.manager.service.AddonCatalogService;

/**
 * Addon legacy service implementation using fixed files.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonLegacyServiceImpl implements AddonCatalogService {

    private static final String CATALOG_URL = "https://bined.exbin.org/addon/";
    private static final String CATALOG_DEV_URL = "https://bined.exbin.org/addon-dev/";
    private final Map<AddonRecord, String> iconPaths = new HashMap<>();
    private final List<IconChangeListener> iconChangeListeners = new ArrayList<>();

    @Nonnull
    @Override
    public AddonsListResult searchForAddons(String searchCondition) {
        throw new IllegalStateException();
    }

    @Nonnull
    @Override
    public Optional<AddonRecord> getAddon(String addonId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nonnull
    @Override
    public DownloadOperation createDownloadsOperation(List<DownloadItemRecord> records) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addIconChangeListener(IconChangeListener listener) {
        iconChangeListeners.add(listener);
    }

    public void removeIconChangeListener(IconChangeListener listener) {
        iconChangeListeners.remove(listener);
    }

    public interface IconChangeListener {

        void iconsChanged();
    }
}
