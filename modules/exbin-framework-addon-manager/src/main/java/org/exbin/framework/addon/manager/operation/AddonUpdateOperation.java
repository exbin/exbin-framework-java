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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.model.AddonUpdateChanges;
import org.exbin.framework.addon.manager.model.DependencyRecord;
import org.exbin.framework.addon.manager.model.ItemRecord;
import org.exbin.framework.addon.manager.model.UpdateRecord;
import org.exbin.framework.addon.manager.operation.model.DownloadItemRecord;
import org.exbin.framework.addon.manager.operation.model.LicenseItemRecord;
import org.exbin.framework.addon.manager.service.AddonCatalogService;
import org.exbin.framework.basic.BasicModuleProvider;
import org.exbin.framework.basic.ModuleRecord;

/**
 * Addon update operation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonUpdateOperation {

    private final AddonCatalogService addonCatalogService;
    private final AddonUpdateChanges addonUpdateChanges;
    private final UpdateOperations updateOperations = new UpdateOperations();
    private final LocalModules localModules;
    private final Map<String, UpdateRecord> availableUpdates = new HashMap<>();

    private final List<LicenseItemRecord> licenseRecords = new ArrayList<>();
    private final List<DownloadItemRecord> downloadRecords = new ArrayList<>();

    public AddonUpdateOperation(AddonCatalogService addonCatalogService, LocalModules localModules, AddonUpdateChanges addonUpdateChanges) {
        this.addonCatalogService = addonCatalogService;
        this.localModules = localModules;
        this.addonUpdateChanges = addonUpdateChanges;
    }

    @Nonnull
    public AddonUpdateChanges getAddonUpdateChanges() {
        return addonUpdateChanges;
    }

    @Nonnull
    public List<String> getOperations() {
        List<String> operations = new ArrayList<>();
        return operations;
    }

    @Nonnull
    public List<LicenseItemRecord> getLicenseRecords() {
        return licenseRecords;
    }

    @Nonnull
    public List<DownloadItemRecord> getDownloadRecords() {
        return downloadRecords;
    }

    public boolean hasLicenseRecords() {
        return !licenseRecords.isEmpty();
    }

    public boolean hasDownloadRecords() {
        return !downloadRecords.isEmpty();
    }

    public void installItem(ItemRecord item) {
        if (item instanceof AddonRecord) {
            String addonId = item.getId();
            if (addonUpdateChanges.hasInstallAddon(addonId)) {
                throw new IllegalStateException("Addon already queued for installation: " + addonId);
            }
            updateOperations.installAddons.add(addonId);
            addAddonFile((AddonRecord) item);
            addAddonDependencies((AddonRecord) item);
        } else {
            throw new IllegalStateException("Unable to install non-addon item");
        }
    }

    public void updateItem(ItemRecord item) {
        if (item instanceof AddonRecord) {
            String addonId = item.getId();
            if (addonUpdateChanges.hasInstallAddon(addonId)) {
                throw new IllegalStateException("Addon already queued for installation: " + addonId);
            }
            updateOperations.installAddons.add(addonId);
            addAddonFile((AddonRecord) item);
            addAddonDependencies((AddonRecord) item);
        } else {
            throw new IllegalStateException("Unable to install non-addon item");
        }
    }

    public void removeItem(ItemRecord item) {
        if (item instanceof AddonRecord) {
            String addonId = item.getId();
            if (addonUpdateChanges.hasRemoveAddon(addonId)) {
                throw new IllegalStateException("Addon already queued for removal: " + addonId);
            }
            updateOperations.removeAddons.add(addonId);
        } else {
            throw new IllegalStateException("Unable to install non-addon item");
        }
    }

    private void addAddonDependencies(AddonRecord record) {
        List<DependencyRecord> dependencies = new ArrayList<>();
        dependencies.addAll(record.getDependencies());
        while (!dependencies.isEmpty()) {
            DependencyRecord dependency = dependencies.remove(0);
            DependencyRecord.Type dependencyType = dependency.getType();
            String dependencyId = dependency.getId();
            switch (dependencyType) {
                case MODULE:
                case PLUGIN:
                    boolean include = true;

                    if (updateOperations.installAddons.contains(dependencyId)) {
                        include = false;
                    } else if (localModules.hasModule(dependencyId) && !availableUpdates.containsKey(dependencyId)) {
                        include = false;
                    }

                    if (include) {
                        Optional<AddonRecord> optRecord = addonCatalogService.getAddon(dependencyId);
                        if (!optRecord.isPresent()) {
                            throw new IllegalStateException("Missing dependecy: " + dependencyId);
                        }
                        AddonRecord addonRecord = optRecord.get();
                        updateOperations.installAddons.add(addonRecord.getId());
                        addAddonFile(addonRecord);
                        dependencies.addAll(addonRecord.getDependencies());
                    }
                    break;
                case JAR_LIBRARY:
                    if (!localModules.hasLibrary(dependencyId) && !updateOperations.downloadLibrary.contains(dependencyId)) {
                        updateOperations.downloadLibrary.add(dependencyId);
                    }
                    break;
                case MAVEN_LIBRARY:
                    if (!localModules.hasLibrary(BasicModuleProvider.mavenCodeToFileName(dependencyId)) && !updateOperations.mavenLibrary.contains(dependencyId)) {
                        updateOperations.mavenLibrary.add(dependencyId);
                    }
                    break;
            }
        }
    }

    private void addAddonFile(AddonRecord addonRecord) {

    }

    private static class UpdateOperations {

        final List<String> installAddons = new ArrayList<>();
        final List<String> dependencyAddons = new ArrayList<>();
        final List<String> removeAddons = new ArrayList<>();
        final List<String> downloadLibrary = new ArrayList<>();
        final List<String> mavenLibrary = new ArrayList<>();
        final List<String> removeLibrary = new ArrayList<>();
    }

    public interface LocalModules {

        boolean hasModule(String moduleId);

        boolean hasLibrary(String libraryFileName);
    }
}
