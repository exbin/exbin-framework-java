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
package org.exbin.framework.basic;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.ApplicationBundleKeys;

/**
 * Basic framework application.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BasicApplication {

    public static final String PLUGINS_DIRECTORY = "plugins";

    protected ResourceBundle appBundle;
    protected Preferences appPreferences;
    protected BasicModuleProvider moduleProvider;
//    private final List<URI> plugins = new ArrayList<>();
//    private String targetLaf = null;
    protected File appDirectory;
    protected File configDirectory;

    public BasicApplication(DynamicClassLoader dynamicClassLoader, Class manifestClass) {
        this(dynamicClassLoader, manifestClass, null);
    }

    public BasicApplication(DynamicClassLoader dynamicClassLoader, Class manifestClass, @Nullable ResourceBundle appBundle) {
        this.appBundle = appBundle;
        BasicApplication.this.setAppDirectory(manifestClass);
        moduleProvider = new BasicModuleProvider(dynamicClassLoader, manifestClass);
        File appsConfigDirectory;
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.startsWith("win")) {
                String appData = System.getenv("APPDATA");
                if (appData != null) {
                    appsConfigDirectory = Paths.get(appData).toFile();
                } else {
                    appsConfigDirectory = Paths.get(System.getProperty("user.home"), "AppData", "Local").toFile();
                }
            } else {
                appsConfigDirectory = new File(System.getProperty("user.home"), ".config");
            }
            if (appBundle == null) {
                configDirectory = new File(appsConfigDirectory, manifestClass.getName());
            } else {
                configDirectory = new File(appsConfigDirectory, appBundle.getString(ApplicationBundleKeys.APPLICATION_VENDOR_ID) + File.separator + appBundle.getString(ApplicationBundleKeys.APPLICATION_ID) + File.separator + appBundle.getString(ApplicationBundleKeys.APPLICATION_VERSION));
            }
        } catch (Throwable tw) {
            Logger.getLogger(BasicApplication.class.getName()).log(Level.SEVERE, "Unable to locate configuration directory", tw);
            configDirectory = new File("");
        }
    }

    @Nonnull
    public static BasicApplication createApplication(Class manifestClass) {
        return BasicApplication.createApplication(manifestClass, null);
    }

    @Nonnull
    public static BasicApplication createApplication(Class manifestClass, @Nullable ResourceBundle appBundle) {
        try {
            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(manifestClass);
            Class<?> applicationClass = dynamicClassLoader.loadClass(BasicApplication.class.getCanonicalName());
            if (appBundle == null) {
                Constructor<?> constructor = applicationClass.getConstructor(DynamicClassLoader.class, Class.class);
                return (BasicApplication) constructor.newInstance(dynamicClassLoader, manifestClass);
            }

            Constructor<?> constructor = applicationClass.getConstructor(DynamicClassLoader.class, Class.class, ResourceBundle.class);
            return (BasicApplication) constructor.newInstance(dynamicClassLoader, manifestClass, appBundle);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException("Unable to create application instance", ex);
        }
    }

    public void init() {
        App.setModuleProvider(moduleProvider);
        App.setConfigDirectory(configDirectory);
    }

    @Nonnull
    public File getAppDirectory() {
        if (appDirectory == null) {
            appDirectory = new File("");
        }
        return appDirectory;
    }

    public void setAppDirectory(File appDirectory) {
        this.appDirectory = appDirectory;
    }

    @Nonnull
    public File getConfigDirectory() {
        return configDirectory;
    }

    public void setConfigDirectory(File configDirectory) {
        this.configDirectory = configDirectory;
    }

    @Nonnull
    public ResourceBundle getAppBundle() {
        if (appBundle == null) {
            appBundle = new ResourceBundle() {
                @Nullable
                @Override
                protected Object handleGetObject(String key) {
                    return null;
                }

                @Nonnull
                @Override
                public Enumeration<String> getKeys() {
                    return Collections.emptyEnumeration();
                }
            };
        }
        return appBundle;
    }

    public void setAppBundle(ResourceBundle appBundle) {
        this.appBundle = appBundle;
    }

    @Nonnull
    public Preferences getAppPreferences() {
        return appPreferences;
    }

    public void setAppPreferences(Preferences appPreferences) {
        this.appPreferences = appPreferences;
    }

    public void setAppDirectory(Class classInstance) {
        URL classResourceUrl = classInstance.getResource(classInstance.getSimpleName() + ".class");
        if (!"jar".equals(classResourceUrl.getProtocol())) {
            return;
        }

        try {
            URL appDirectoryUrl = classInstance.getProtectionDomain().getCodeSource().getLocation();
            appDirectory = new File(appDirectoryUrl.toURI()).getParentFile();
            return;
        } catch (final SecurityException e) {
            // ignore: Cannot access protection domain.
        } catch (final NullPointerException e) {
            // ignore: Protection domain or code source is null.
        } catch (URISyntaxException ex) {
            // ignore: Invalid URL
        }

        String appDirectoryPath = classResourceUrl.toString();
        appDirectoryPath = appDirectoryPath.substring(4, appDirectoryPath.indexOf("!"));

        appDirectory = new File(appDirectoryPath).getParentFile();
    }

    public void addModulesFromPath(URI pathUri, ModuleFileLocation fileLocation) {
        moduleProvider.addModulesFromPath(pathUri, fileLocation);
    }

    public void addModulesFromPath(URL pathUrl, ModuleFileLocation fileLocation) {
        moduleProvider.addModulesFromPath(pathUrl, fileLocation);
    }

    public void addClassPathModules() {
        moduleProvider.addClassPathModules();
    }

    public void addModulesFromManifest(Class manifestClass) {
        moduleProvider.addModulesFromManifest(manifestClass);
    }

    public void addModulesFromManifest() {
        moduleProvider.addModulesFromManifest();
    }

    public void addPreloadedLibrary(String libraryFileName) {
        moduleProvider.addPreloadedLibrary(libraryFileName);
    }

    public void initModules() {
        moduleProvider.initModules();
    }
}
