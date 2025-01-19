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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.addon.manager.api.AddonManagerModuleApi;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.model.AddonUpdateChanges;
import org.exbin.framework.addon.manager.model.DependencyRecord;
import org.exbin.framework.addon.manager.model.ItemRecord;
import org.exbin.framework.addon.manager.model.UpdateRecord;
import org.exbin.framework.addon.manager.operation.model.DownloadItemRecord;
import org.exbin.framework.addon.manager.operation.model.LicenseItemRecord;
import org.exbin.framework.addon.manager.service.AddonCatalogService;
import org.exbin.framework.addon.manager.service.AddonCatalogServiceException;
import org.exbin.framework.basic.BasicModuleProvider;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Addon update operation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonUpdateOperation {

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonUpdateOperation.class);

    private static final String MAVEN_CENTRAL_URL = "https://repo1.maven.org/maven2/";
    private final AddonCatalogService addonCatalogService;
    private final AddonUpdateChanges addonUpdateChanges;
    private final ApplicationModulesUsage applicationModulesUsage;
    private final List<UpdateRecord> updatesRecords = new ArrayList<>();
    private final Set<String> availableUpdates = new HashSet<>();

    private final UpdateOperations updateOperations = new UpdateOperations();

    public AddonUpdateOperation(AddonCatalogService addonCatalogService, ApplicationModulesUsage applicationModulesUsage, AddonUpdateChanges addonUpdateChanges) {
        this.addonCatalogService = addonCatalogService;
        this.applicationModulesUsage = applicationModulesUsage;
        this.addonUpdateChanges = addonUpdateChanges;
    }

    @Nonnull
    public AddonUpdateChanges getAddonUpdateChanges() {
        return addonUpdateChanges;
    }

    @Nonnull
    public List<String> getOperations() {
        List<String> operations = new ArrayList<>();
        String operationMessage = resourceBundle.getString("operationMessage.installModule");
        for (String moduleId : updateOperations.installAddons) {
            operations.add(String.format(operationMessage, moduleId));
        }
        operationMessage = resourceBundle.getString("operationMessage.removeModule");
        for (String moduleId : updateOperations.removeAddons) {
            operations.add(String.format(operationMessage, moduleId));
        }
        operationMessage = resourceBundle.getString("operationMessage.dependencyAddon");
        for (String moduleId : updateOperations.dependencyAddons) {
            operations.add(String.format(operationMessage, moduleId));
        }
        operationMessage = resourceBundle.getString("operationMessage.downloadLibrary");
        for (String libraryFile : updateOperations.downloadLibraries) {
            operations.add(String.format(operationMessage, libraryFile));
        }
        operationMessage = resourceBundle.getString("operationMessage.downloadMavenLibrary");
        for (String libraryFile : updateOperations.downloadMavenLibraries) {
            operations.add(String.format(operationMessage, libraryFile));
        }
        operationMessage = resourceBundle.getString("operationMessage.removeLibrary");
        for (String libraryFile : updateOperations.removeLibraries) {
            operations.add(String.format(operationMessage, libraryFile));
        }
        return operations;
    }

    @Nonnull
    public List<LicenseItemRecord> getLicenseRecords() {
        List<LicenseItemRecord> licenseRecords = new ArrayList<>();
        /*        AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
        String licenseDownloadPrefix = addonManagerModule.isDevMode() ? "https://bined.exbin.org/addon-dev/license/" : "https://bined.exbin.org/addon/license/";
        for (String moduleFile : updateOperations.downloadModule) {
            try {
                LicenseItemRecord record = new LicenseItemRecord("Apache License, Version 2.0", new URL(licenseDownloadPrefix + "Apache-2.0.html"));
                licenseRecords.add(record);
            } catch (MalformedURLException ex) {
                Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } */

        return licenseRecords;
    }

    @Nonnull
    public List<DownloadItemRecord> getDownloadRecords() {
        AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
        String libraryDownloadPrefix = addonManagerModule.isDevMode() ? "https://bined.exbin.org/addon-dev/download/" : "https://bined.exbin.org/addon/download/";
        List<DownloadItemRecord> downloadRecords = new ArrayList<>();
        String downloadItemDescription = resourceBundle.getString("downloadItemDescription.module");
        for (String moduleFile : updateOperations.downloadModule) {
            DownloadItemRecord record = new DownloadItemRecord(String.format(downloadItemDescription, moduleFile), moduleFile);
            try {
                record.setUrl(new URL(libraryDownloadPrefix + moduleFile));
                downloadRecords.add(record);
            } catch (MalformedURLException ex) {
                Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        downloadItemDescription = resourceBundle.getString("downloadItemDescription.library");
        for (String library : updateOperations.downloadLibraries) {
            DownloadItemRecord record = new DownloadItemRecord(String.format(downloadItemDescription, library), library);
            try {
                record.setUrl(new URL(libraryDownloadPrefix + library));
                downloadRecords.add(record);
            } catch (MalformedURLException ex) {
                Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        downloadItemDescription = resourceBundle.getString("downloadItemDescription.mavenLibrary");
        for (String library : updateOperations.downloadMavenLibraries) {
            DownloadItemRecord record = new DownloadItemRecord(String.format(downloadItemDescription, library), library);
            try {
                record.setUrl(new URL(AddonUpdateOperation.mavenCodeToDownloadUrl(library)));
                downloadRecords.add(record);
            } catch (MalformedURLException ex) {
                Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return downloadRecords;
    }

    public void installItem(ItemRecord item) {
        if (item instanceof AddonRecord) {
            String addonId = item.getId();
            if (addonUpdateChanges.hasInstallAddon(addonId)) {
                throw new IllegalStateException("Addon already queued for installation: " + addonId);
            }
            updateOperations.installAddons.add(addonId);
            try {
                updateOperations.downloadModule.add(addonCatalogService.getAddonFile(addonId));
            } catch (AddonCatalogServiceException ex) {
                Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            try {
                updateOperations.downloadModule.add(addonCatalogService.getAddonFile(item.getId()));
            } catch (AddonCatalogServiceException ex) {
                Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            try {
                // TODO Should be addon filename instead of filename from catalog
                updateOperations.removeLibraries.add(addonCatalogService.getAddonFile(item.getId()));
            } catch (AddonCatalogServiceException ex) {
                Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
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
                    } else if (applicationModulesUsage.hasModule(dependencyId) && !availableUpdates.contains(dependencyId)) {
                        include = false;
                    }

                    if (include) {
                        AddonRecord addonRecord;
                        try {
                            addonRecord = addonCatalogService.getAddonDependency(dependencyId);
                            updateOperations.installAddons.add(addonRecord.getId());
                            updateOperations.downloadModule.add(addonCatalogService.getAddonFile(addonRecord.getId()));
                            dependencies.addAll(addonRecord.getDependencies());
                        } catch (AddonCatalogServiceException ex) {
                            Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case JAR_LIBRARY:
                    if (!applicationModulesUsage.hasLibrary(dependencyId) && !updateOperations.downloadLibraries.contains(dependencyId)) {
                        updateOperations.downloadLibraries.add(dependencyId);
                    }
                    break;
                case MAVEN_LIBRARY:
                    if (!applicationModulesUsage.hasLibrary(BasicModuleProvider.mavenCodeToFileName(dependencyId)) && !updateOperations.downloadMavenLibraries.contains(dependencyId)) {
                        updateOperations.downloadMavenLibraries.add(dependencyId);
                    }
                    break;
            }
        }
    }

    public void finished() {
        for (String moduleId : updateOperations.installAddons) {
            addonUpdateChanges.removeRemoveAddon(moduleId);
            addonUpdateChanges.addInstallAddon(moduleId);
        }
        for (String moduleId : updateOperations.dependencyAddons) {
            addonUpdateChanges.removeRemoveAddon(moduleId);
            addonUpdateChanges.addInstallAddon(moduleId);
        }
        for (String moduleFile : updateOperations.downloadModule) {
            addonUpdateChanges.removeRemoveFile(moduleFile);
            addonUpdateChanges.addUpdateFile(moduleFile);
        }
        for (String libraryFile : updateOperations.downloadLibraries) {
            addonUpdateChanges.removeRemoveFile(libraryFile);
            addonUpdateChanges.addUpdateFile(libraryFile);
        }
        for (String mavenLibrary : updateOperations.downloadMavenLibraries) {
            String libraryFile = BasicModuleProvider.mavenCodeToFileName(mavenLibrary);
            addonUpdateChanges.removeRemoveFile(libraryFile);
            addonUpdateChanges.addUpdateFile(libraryFile);
        }
        for (String moduleId : updateOperations.removeAddons) {
            addonUpdateChanges.removeInstallAddon(moduleId);
            addonUpdateChanges.addRemoveAddon(moduleId);
        }
        for (String file : updateOperations.removeLibraries) {
            addonUpdateChanges.removeUpdateFile(file);
            // TODO delete file
            addonUpdateChanges.addRemoveFile(file);
        }
        addonUpdateChanges.writeConfigFile();
    }

    @Nonnull
    public static String mavenCodeToDownloadUrl(String mavenCode) {
        StringBuilder builder = new StringBuilder();
        builder.append(MAVEN_CENTRAL_URL);
        int namePos = mavenCode.indexOf(":");
        int domainSegment = 0;
        while (domainSegment < namePos) {
            int segment = mavenCode.indexOf(".", domainSegment);
            if (segment > namePos) {
                segment = namePos;
            }
            builder.append(mavenCode.substring(domainSegment, segment)).append("/");
            domainSegment = segment + 1;
        }
        int versionPos = mavenCode.indexOf(":", namePos + 1);
        String namePart = mavenCode.substring(namePos + 1, versionPos);
        String versionPart = mavenCode.substring(versionPos + 1);
        builder.append(namePart).append("/").append(versionPart).append("/");
        builder.append(namePart).append("-").append(versionPart).append(".jar");
        return builder.toString();
    }

    private static class UpdateOperations {

        final List<String> installAddons = new ArrayList<>();
        final List<String> dependencyAddons = new ArrayList<>();
        final List<String> removeAddons = new ArrayList<>();
        final List<String> downloadModule = new ArrayList<>();
        final List<String> downloadLibraries = new ArrayList<>();
        final List<String> downloadMavenLibraries = new ArrayList<>();
        final List<String> removeLibraries = new ArrayList<>();
    }
}
