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
package org.exbin.framework.addon.manager;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.ModuleProvider;
import org.exbin.framework.addon.AddonModuleFileLocation;
import org.exbin.framework.addon.manager.api.AddonRecord;
import org.exbin.framework.addon.manager.api.DependencyRecord;
import org.exbin.framework.addon.manager.model.AddonUpdateChanges;
import org.exbin.framework.addon.manager.api.ItemRecord;
import org.exbin.framework.addon.manager.model.AvailableModuleUpdates;
import org.exbin.framework.basic.BasicModuleProvider;
import org.exbin.framework.basic.ModuleRecord;

/**
 * Addons target state including qeued changes.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonsState {

    protected final List<ItemRecord> installedAddons = new ArrayList<>();
    protected final AvailableModuleUpdates availableModuleUpdates = new AvailableModuleUpdates();
    protected final AddonUpdateChanges addonUpdateChanges = new AddonUpdateChanges();
    protected ApplicationModulesUsage applicationModulesUsage;

    public AddonsState() {
    }

    public void init() {
        availableModuleUpdates.readConfigFile();
        addonUpdateChanges.readConfigFile();

        ModuleProvider moduleProvider = App.getModuleProvider();
        if (moduleProvider instanceof BasicModuleProvider) {
            List<ModuleRecord> basicModulesList = ((BasicModuleProvider) moduleProvider).getModulesList();
            for (ModuleRecord moduleRecord : basicModulesList) {
                AddonRecord itemRecord = new AddonRecord(moduleRecord.getModuleId(), moduleRecord.getName());
                itemRecord.setInstalled(true);
                itemRecord.setAddon(moduleRecord.getFileLocation() == AddonModuleFileLocation.ADDON);
                itemRecord.setVersion(moduleRecord.getVersion());
                itemRecord.setProvider(moduleRecord.getProvider().orElse(null));
                itemRecord.setHomepage(moduleRecord.getHomepage().orElse(null));
                itemRecord.setDescription(moduleRecord.getDescription().orElse(null));
                itemRecord.setIcon(moduleRecord.getIcon().orElse(null));
                List<DependencyRecord> dependencyRecords = new ArrayList<>();
                for (String dependencyModuleId : moduleRecord.getDependencyModuleIds()) {
                    dependencyRecords.add(new DependencyRecord(dependencyModuleId));
                }
                for (String dependencyLibraryId : moduleRecord.getDependencyLibraries()) {
                    dependencyRecords.add(new DependencyRecord(DependencyRecord.Type.JAR_LIBRARY, dependencyLibraryId));
                }
                itemRecord.setDependencies(dependencyRecords);
                installedAddons.add(itemRecord);
                /*System.out.println(moduleRecord.getModuleId() + "," + moduleRecord.getName() + "," + moduleRecord.getDescription().orElse("") + "," + moduleRecord.getVersion() + "," + moduleRecord.getHomepage().orElse(""));
                for (DependencyRecord dependency : dependencyRecords) {
                    System.out.println("- " + dependency.getType().name() + ", " + dependency.getId());
                } */
            }
            applicationModulesUsage = new ApplicationModulesUsage() {
                @Override
                public boolean hasModule(String moduleId) {
                    return ((BasicModuleProvider) moduleProvider).hasModule(moduleId);
                }

                @Override
                public boolean hasLibrary(String libraryFileName) {
                    return ((BasicModuleProvider) moduleProvider).hasLibrary(libraryFileName);
                }
            };
        }

        AvailableModuleUpdates.AvailableModulesChangeListener availableModulesChangeListener = (AvailableModuleUpdates checker) -> {
            int availableUpdates = 0;
            for (ItemRecord installedAddon : installedAddons) {
                if (checker.isUpdateAvailable(installedAddon.getId(), installedAddon.getVersion())) {
                    availableUpdates++;
                }
            }
            // TODO controlPanel.setAvailableUpdates(availableUpdates);
        };

        availableModuleUpdates.addChangeListener(availableModulesChangeListener);
        availableModuleUpdates.notifyChanged();
    }

    @Nonnull
    public List<ItemRecord> getInstalledAddons() {
        return installedAddons;
    }

    @Nonnull
    public AvailableModuleUpdates getAvailableModuleUpdates() {
        return availableModuleUpdates;
    }

    @Nonnull
    public AddonUpdateChanges getAddonUpdateChanges() {
        return addonUpdateChanges;
    }

    @Nonnull
    public ApplicationModulesUsage getApplicationModulesUsage() {
        return applicationModulesUsage;
    }

    public boolean isModuleInstalled(String moduleId) {
        return addonUpdateChanges.hasInstallAddon(moduleId) && !addonUpdateChanges.hasRemoveAddon(moduleId);
    }

    public boolean isModuleRemoved(String moduleId) {
        return addonUpdateChanges.hasRemoveAddon(moduleId) && !addonUpdateChanges.hasInstallAddon(moduleId);
    }

    public void addUpdateAvailabilityListener(AvailableModuleUpdates.AvailableModulesChangeListener listener) {
        availableModuleUpdates.addChangeListener(listener);
    }
}
