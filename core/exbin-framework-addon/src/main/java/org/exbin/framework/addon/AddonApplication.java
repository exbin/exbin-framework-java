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
package org.exbin.framework.addon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.basic.BasicApplication;
import org.exbin.framework.basic.DynamicClassLoader;

/**
 * Addon supporting framework application.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonApplication extends BasicApplication {

    public static final String ADDONS_DIRECTORY = "addons";
    public static final String ADDONS_UPDATE_DIRECTORY = "addons_update";
    public static final String ADDONS_CONFIG_FILE = "changes.cfg";
    public static final String ADDONS_UPDATE_FILE = "UPDATE_FILE";
    public static final String ADDONS_REMOVE_FILE = "REMOVE_FILE";

    public AddonApplication(DynamicClassLoader dynamicClassLoader, Class manifestClass) {
        super(dynamicClassLoader, manifestClass);
    }

    public AddonApplication(DynamicClassLoader dynamicClassLoader, Class manifestClass, @Nullable ResourceBundle appBundle) {
        super(dynamicClassLoader, manifestClass, appBundle);
    }

    @Nonnull
    public static AddonApplication createApplication(Class manifestClass) {
        try {
            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(manifestClass);
            Class<?> applicationClass = dynamicClassLoader.loadClass(AddonApplication.class.getCanonicalName());
            Constructor<?> constructor = applicationClass.getConstructor(DynamicClassLoader.class, Class.class);
            return (AddonApplication) constructor.newInstance(dynamicClassLoader, manifestClass);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException("Unable to create application instance", ex);
        }
    }

    @Nonnull
    public static AddonApplication createApplication(Class manifestClass, ResourceBundle appBundle) {
        try {
            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(manifestClass);
            Class<?> applicationClass = dynamicClassLoader.loadClass(AddonApplication.class.getCanonicalName());
            Constructor<?> constructor = applicationClass.getConstructor(DynamicClassLoader.class, Class.class, ResourceBundle.class);
            return (AddonApplication) constructor.newInstance(dynamicClassLoader, manifestClass, appBundle);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException("Unable to create application instance", ex);
        }
    }

    public void setupAddons() {
        String configDirectoryPath = configDirectory.getAbsolutePath();
        File addonsDirectory = new File(configDirectoryPath, ADDONS_DIRECTORY);
        File updateDirectory = new File(configDirectoryPath, ADDONS_UPDATE_DIRECTORY);
        if (updateDirectory.exists()) {
            File changesConfig = new File(updateDirectory, ADDONS_CONFIG_FILE);

            if (changesConfig.exists()) {
                if (!addonsDirectory.exists()) {
                    addonsDirectory.mkdirs();
                }
                // Perform update
                Logger.getLogger(AddonApplication.class.getName()).log(Level.INFO, "Starting addons update");
                boolean success = false;
                String line = null;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(changesConfig)))) {
                    do {
                        line = reader.readLine();
                        if (line != null) {
                            if (line.startsWith(ADDONS_UPDATE_FILE)) {
                                String fileName = line.substring(12);
                                File replacedFile = new File(addonsDirectory, fileName);
                                File sourceFile = new File(updateDirectory, fileName);
                                if (sourceFile.exists()) {
                                    if (replacedFile.exists()) {
                                        replacedFile.delete();
                                    }
                                }
                                sourceFile.renameTo(replacedFile);
                            } else if (line.startsWith(ADDONS_REMOVE_FILE)) {
                                String fileName = line.substring(12);
                                File removedFile = new File(addonsDirectory, fileName);
                                if (removedFile.exists()) {
                                    removedFile.delete();
                                }
                            }
                        }
                    } while (line != null);
                    success = true;
                } catch (IOException ex) {
                    Logger.getLogger(AddonApplication.class.getName()).log(Level.SEVERE, "Failed to move file " + line, ex);
                }
                if (success) {
                    changesConfig.delete();
                }

                Logger.getLogger(AddonApplication.class.getName()).log(Level.INFO, "Finished addons update");
            }
        }

        // Load addons
        try {
            URL addonsPath = addonsDirectory.toURI().toURL();
            AddonApplication.this.addModulesFromPath(addonsPath, AddonModuleFileLocation.ADDON);
        } catch (MalformedURLException ex) {
            Logger.getLogger(AddonApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
