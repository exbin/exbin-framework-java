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
package org.exbin.framework.addon.manager.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;

/**
 * Addon update changes.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonUpdateChanges {

    private final List<String> installAddons = new ArrayList<>();
    private final List<String> removeAddons = new ArrayList<>();
    private final List<String> updateFiles = new ArrayList<>();
    private final List<String> removeFiles = new ArrayList<>();

    @Nonnull
    public List<String> getInstallAddons() {
        return installAddons;
    }

    @Nonnull
    public List<String> getRemoveAddons() {
        return removeAddons;
    }

    @Nonnull
    public List<String> getUpdateFiles() {
        return updateFiles;
    }

    @Nonnull
    public List<String> getRemoveFiles() {
        return removeFiles;
    }

    public void addInstallAddon(String addonId) {
        installAddons.add(addonId);
    }

    public void removeInstallAddon(String addonId) {
        installAddons.remove(addonId);
    }

    public boolean hasInstallAddon(String addonId) {
        return installAddons.contains(addonId);
    }

    public void addRemoveAddon(String addonId) {
        removeAddons.add(addonId);
    }

    public void removeRemoveAddon(String addonId) {
        removeAddons.remove(addonId);
    }

    public boolean hasRemoveAddon(String addonId) {
        return removeAddons.contains(addonId);
    }

    public void addUpdateFile(String fileName) {
        updateFiles.add(fileName);
    }

    public void removeUpdateFile(String fileName) {
        updateFiles.remove(fileName);
    }

    public boolean hasUpdateFile(String fileName) {
        return updateFiles.contains(fileName);
    }

    public void addRemoveFile(String fileName) {
        removeFiles.add(fileName);
    }

    public void removeRemoveFile(String fileName) {
        removeFiles.remove(fileName);
    }

    public boolean hasremoveFile(String fileName) {
        return removeFiles.contains(fileName);
    }

    public void readConfigFile() {
        File targetDirectory = new File(App.getConfigDirectory(), "addons_update");
        File changesConfigFile = new File(targetDirectory, "changes.cfg");
        installAddons.clear();
        removeAddons.clear();
        updateFiles.clear();
        removeFiles.clear();
        if (changesConfigFile.exists()) {
            String line = null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(changesConfigFile)))) {
                do {
                    if (line != null && !line.isEmpty()) {
                        line = reader.readLine();
                        int prefixEnd = line.indexOf(":");
                        String prefix = line.substring(0, prefixEnd);
                        String value = line.substring(prefixEnd + 1);
                        if (ChangeType.INSTALL_ADDON.name().equals(prefix)) {
                            installAddons.add(value);
                        } else if (ChangeType.REMOVE_ADDON.name().equals(prefix)) {
                            removeAddons.add(value);
                        } else if (ChangeType.UPDATE_FILE.name().equals(prefix)) {
                            updateFiles.add(value);
                        } else if (ChangeType.REMOVE_FILE.name().equals(prefix)) {
                            removeFiles.add(value);
                        }
                    }
                } while (line != null);
            } catch (IOException ex) {
                Logger.getLogger(AddonUpdateChanges.class.getName()).log(Level.SEVERE, "Failed to move file " + line, ex);
            }
        }
    }

    public void writeConfigFile() {
        File targetDirectory = new File(App.getConfigDirectory(), "addons_update");
        File changesConfigFile = new File(targetDirectory, "changes.cfg");
        try (OutputStreamWriter writer = new FileWriter(changesConfigFile)) {
            String prefix = ChangeType.INSTALL_ADDON.name() + ":";
            for (String line : installAddons) {
                writer.write(prefix + line + "\r\n");
            }
            prefix = ChangeType.REMOVE_ADDON.name() + ":";
            for (String line : removeAddons) {
                writer.write(prefix + line + "\r\n");
            }
            prefix = ChangeType.UPDATE_FILE.name() + ":";
            for (String line : updateFiles) {
                writer.write(prefix + line + "\r\n");
            }
            prefix = ChangeType.REMOVE_FILE.name() + ":";
            for (String line : removeFiles) {
                writer.write(prefix + line + "\r\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(AddonUpdateChanges.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public enum ChangeType {
        INSTALL_ADDON,
        REMOVE_ADDON,
        UPDATE_FILE,
        REMOVE_FILE
    }
}
