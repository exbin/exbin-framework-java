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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.utils.VersionUtils;

/**
 * Available module updates.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AvailableModuleUpdates {

    private static final String MODULE_UPDATES_FILE = "available-updates.cfg";
    private final Map<String, String> latestVersions = new HashMap<>();
    private int status = -1;
    private final List<AvailableModulesChangeListener> changeListeners = new ArrayList<>();

    public int getStatus() {
        return status;
    }

    /**
     * Checks whether never version of module is available for update.
     *
     * @param moduleId module id
     * @param version current module version
     * @return true if never version is available
     */
    public boolean isUpdateAvailable(String moduleId, String version) {
        String latestVersion = latestVersions.get(moduleId);
        if (latestVersion != null) {
            return VersionUtils.isGreaterThan(latestVersion, version);
        }
        return false;
    }

    /**
     * Applies availability state to records.
     *
     * @param record target record
     */
    public void applyTo(ItemRecord record) {
        record.setUpdateAvailable(isUpdateAvailable(record.getId(), record.getVersion()));
    }

    public void setLatestVersion(int status, Map<String, String> latestVersions) {
        this.status = status;
        this.latestVersions.clear();
        this.latestVersions.putAll(latestVersions);
        notifyChanged();
    }

    public void readConfigFile() {
        File changesConfigFile = new File(App.getConfigDirectory(), MODULE_UPDATES_FILE);
        latestVersions.clear();
        status = -1;
        if (changesConfigFile.exists()) {
            String line;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(changesConfigFile)))) {
                line = reader.readLine();
                status = Integer.parseInt(line);
                do {
                    line = reader.readLine();
                    if (line != null && !line.isEmpty()) {
                        int versionPos = line.indexOf(":");
                        latestVersions.put(line.substring(0, versionPos), line.substring(versionPos + 1));
                    }
                } while (line != null);
            } catch (NumberFormatException | IOException ex) {
                Logger.getLogger(AvailableModuleUpdates.class.getName()).log(Level.SEVERE, "Failed to read modules update cache", ex);
            }
        }
    }

    public void writeConfigFile() {
        File configDirectory = App.getConfigDirectory();
        if (!configDirectory.isDirectory()) {
            configDirectory.mkdirs();
        }
        File changesConfigFile = new File(configDirectory, MODULE_UPDATES_FILE);
        try (OutputStreamWriter writer = new FileWriter(changesConfigFile)) {
            writer.write(status + "\r\n");
            for (Map.Entry<String, String> entry : latestVersions.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\r\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(AvailableModuleUpdates.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addChangeListener(AvailableModulesChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(AvailableModulesChangeListener listener) {
        changeListeners.remove(listener);
    }

    public void notifyChanged() {
        for (AvailableModulesChangeListener changeListener : changeListeners) {
            changeListener.changed(this);
        }
    }

    public interface AvailableModulesChangeListener {

        void changed(AvailableModuleUpdates availableModuleUpdates);
    }
}
