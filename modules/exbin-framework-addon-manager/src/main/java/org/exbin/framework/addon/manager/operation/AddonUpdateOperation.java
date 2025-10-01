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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.addon.manager.api.AddonManagerModuleApi;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.model.AddonUpdateChanges;
import org.exbin.framework.addon.manager.model.DependencyRecord;
import org.exbin.framework.addon.manager.model.ItemRecord;
import org.exbin.framework.addon.manager.operation.model.DownloadItemRecord;
import org.exbin.framework.addon.manager.operation.model.LicenseItemRecord;
import org.exbin.framework.addon.manager.options.AddonManagerOptions;
import org.exbin.framework.addon.manager.service.AddonCatalogService;
import org.exbin.framework.addon.manager.service.AddonCatalogServiceException;
import org.exbin.framework.basic.BasicModuleProvider;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.preferences.api.PreferencesModuleApi;

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
    private final List<LicenseItemRecord> licenseRecords = new ArrayList<>();
    private final Set<String> licenseCodes = new HashSet<>();
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
        AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
        String licenseDownloadPrefix = addonManagerModule.getAddonServiceUrl() + "license/";
        for (LicenseItemRecord record : licenseRecords) {
            try {
                record.setUrl(new URI(licenseDownloadPrefix + record.getRemoteFile()).toURL());
            } catch (MalformedURLException | URISyntaxException ex) {
                Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return licenseRecords;
    }

    @Nonnull
    public List<DownloadItemRecord> getDownloadRecords() {
        AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
        String libraryDownloadPrefix = addonManagerModule.getAddonServiceUrl() + "download/?f=";
        List<DownloadItemRecord> downloadRecords = new ArrayList<>();
        String downloadItemDescription = resourceBundle.getString("downloadItemDescription.module");
        for (String moduleFile : updateOperations.downloadModule) {
            DownloadItemRecord record = new DownloadItemRecord(String.format(downloadItemDescription, moduleFile), moduleFile);
            try {
                record.setUrl(new URI(libraryDownloadPrefix + moduleFile).toURL());
                downloadRecords.add(record);
            } catch (MalformedURLException | URISyntaxException ex) {
                Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        downloadItemDescription = resourceBundle.getString("downloadItemDescription.library");
        for (String library : updateOperations.downloadLibraries) {
            DownloadItemRecord record = new DownloadItemRecord(String.format(downloadItemDescription, library), library);
            try {
                record.setUrl(new URI(libraryDownloadPrefix + library).toURL());
                downloadRecords.add(record);
            } catch (MalformedURLException | URISyntaxException ex) {
                Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        downloadItemDescription = resourceBundle.getString("downloadItemDescription.mavenLibrary");
        for (String library : updateOperations.downloadMavenLibraries) {
            String libraryFile = BasicModuleProvider.mavenCodeToFileName(library);
            DownloadItemRecord record = new DownloadItemRecord(String.format(downloadItemDescription, library), libraryFile);
            try {
                record.setUrl(new URI(AddonUpdateOperation.mavenCodeToDownloadUrl(library)).toURL());
                downloadRecords.add(record);
            } catch (MalformedURLException | URISyntaxException ex) {
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
            processAddonLicense((AddonRecord) item);
            try {
                updateOperations.downloadModule.add(addonCatalogService.getAddonFile(addonId));
                updateOperations.installAddons.add(addonId);
            } catch (AddonCatalogServiceException ex) {
                Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
            addAddonDependencies((AddonRecord) item);
        } else {
            throw new IllegalStateException("Unable to install non-addon item");
        }
    }

    public void updateItem(ItemRecord item, ItemRecord previousItem) {
        if (item instanceof AddonRecord) {
            String addonId = item.getId();
            if (addonUpdateChanges.hasInstallAddon(addonId)) {
                throw new IllegalStateException("Addon already queued for installation: " + addonId);
            }
            updateOperations.installAddons.add(addonId);
            processAddonLicense((AddonRecord) item);
            if (previousItem.isAddon()) {
                String addonFile = findAddonFileName(item.getId());
                if (addonFile != null) {
                    updateOperations.removeLibraries.add(addonFile);
                }
            }
            try {
                updateOperations.downloadModule.add(addonCatalogService.getAddonFile(item.getId()));
            } catch (AddonCatalogServiceException ex) {
                Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
            addAddonDependencies((AddonRecord) item);
        } else {
            throw new IllegalStateException("Unable to update non-addon item");
        }
    }

    public void removeItem(ItemRecord item) {
        if (item instanceof AddonRecord) {
            String addonId = item.getId();
            if (addonUpdateChanges.hasRemoveAddon(addonId)) {
                throw new IllegalStateException("Addon already queued for removal: " + addonId);
            }
            updateOperations.removeAddons.add(addonId);
            String addonFile = findAddonFileName(item.getId());
            if (addonFile != null) {
                updateOperations.removeLibraries.add(addonFile);
            }
        } else {
            throw new IllegalStateException("Unable to install non-addon item");
        }
    }

    @Nullable
    private static String findAddonFileName(String moduleId) {
        // TODO Replace with including file name in module records
        File targetDirectory = new File(App.getConfigDirectory(), "addons");
        if (!targetDirectory.isDirectory()) {
            return null;
        }

        File[] addonFiles = targetDirectory.listFiles();
        if (addonFiles == null) {
            return null;
        }
        for (File addonFile : addonFiles) {
            if (addonFile.getName().endsWith(".jar")) {
                try {
                    URL moduleRecordUrl = new URI("jar:" + addonFile.toURI().toURL().toExternalForm() + "!/META-INF/module.xml").toURL();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(moduleRecordUrl.openStream()))) {
                        String line = reader.readLine();
                        do {
                            line = reader.readLine();
                            if (line != null && !line.isEmpty()) {
                                int idTag = line.indexOf("<id>");
                                if (idTag >= 0) {
                                    int end = line.indexOf("</id>");
                                    String fileModuleId = line.substring(idTag + 4, end);
                                    if (moduleId.equals(fileModuleId)) {
                                        return addonFile.getName();
                                    }
                                } else {
                                    int apiTag = line.indexOf("<api>");
                                    if (apiTag >= 0) {
                                        int end = line.indexOf("</api>");
                                        String fileModuleId = line.substring(apiTag + 5, end);
                                        if (moduleId.equals(fileModuleId)) {
                                            return addonFile.getName();
                                        }
                                    } else {
                                        int pluginTag = line.indexOf("<plugin>");
                                        if (pluginTag >= 0) {
                                            int end = line.indexOf("</plugin>");
                                            String fileModuleId = line.substring(pluginTag + 8, end);
                                            if (moduleId.equals(fileModuleId)) {
                                                return addonFile.getName();
                                            }
                                        }
                                    }
                                }
                            }
                        } while (line != null);
                    } catch (FileNotFoundException ex) {
                    } catch (NumberFormatException | IOException ex) {
                        Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, "Failed to read modules update cache", ex);
                    }
                } catch (MalformedURLException | URISyntaxException ex) {
                    Logger.getLogger(AddonUpdateOperation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return null;
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

                    if (updateOperations.installAddons.contains(dependencyId) || updateOperations.dependencyAddons.contains(dependencyId)) {
                        include = false;
                    } else if (applicationModulesUsage.hasModule(dependencyId) && !availableUpdates.contains(dependencyId)) {
                        include = false;
                    }

                    if (include) {
                        AddonRecord addonRecord;
                        try {
                            addonRecord = addonCatalogService.getAddonDependency(dependencyId);
                            updateOperations.dependencyAddons.add(addonRecord.getId());
                            processAddonLicense(addonRecord);
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

    public void processAddonLicense(AddonRecord addonRecord) {
        String remoteFile = addonRecord.getLicenseRemoteFile();
        if ("Apache-2.0".equals(addonRecord.getLicenseSpdx().orElse(null)) || remoteFile.isEmpty()) {
            return;
        }
        if (!licenseCodes.contains(remoteFile)) {
            licenseCodes.add(remoteFile);
            licenseRecords.add(new LicenseItemRecord(addonRecord.getLicense(), remoteFile));
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
            if ("org.exbin.framework.addon.manager.AddonManagerModule".equals(moduleId)) {
                PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
                AddonManagerOptions addonPreferences = new AddonManagerOptions(preferencesModule.getAppPreferences());
                addonPreferences.setActivatedVersion("0.3.0-SNAPSHOT");
            }
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
        if (namePos == -1) {
            throw new IllegalStateException("Maven library code is missing split characters: " + mavenCode);
        }
        int domainSegment = 0;
        while (domainSegment < namePos) {
            int segment = mavenCode.indexOf(".", domainSegment);
            if (segment == -1) {
                segment = namePos;
            } else if (segment > namePos) {
                segment = namePos;
            }
            builder.append(mavenCode.substring(domainSegment, segment)).append("/");
            domainSegment = segment + 1;
        }
        int versionPos = mavenCode.indexOf(":", namePos + 1);
        if (versionPos == -1) {
            throw new IllegalStateException("Maven library code is missing split characters: " + mavenCode);
        }
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
