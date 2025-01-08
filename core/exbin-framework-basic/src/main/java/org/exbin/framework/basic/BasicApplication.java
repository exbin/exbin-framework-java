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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;

/**
 * Basic framework application.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BasicApplication {

    public static final String PLUGINS_DIRECTORY = "plugins";
    public static final String ADDONS_DIRECTORY = "addons";

    private Preferences appPreferences;

    private BasicModuleProvider moduleProvider;
//    private final List<URI> plugins = new ArrayList<>();
//    private String targetLaf = null;
    private File appDirectory = new File("");
    private File configDirectory;

    public BasicApplication(DynamicClassLoader dynamicClassLoader, Class manifestClass) {
        moduleProvider = new BasicModuleProvider(dynamicClassLoader, manifestClass);
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.startsWith("win")) {
                String appData = System.getenv("APPDATA");
                if (appData != null) {
                    configDirectory = Paths.get(appData).toFile();
                } else {
                    configDirectory = Paths.get(System.getProperty("user.home"), "AppData", "Local").toFile();
                }
            } else {
                configDirectory = new File(System.getProperty("user.home"), ".config");
            }
            configDirectory = new File(configDirectory, manifestClass.getName());
        } catch (Throwable tw) {
            Logger.getLogger(BasicApplication.class.getName()).log(Level.SEVERE, "Unable to locate configuration directory", tw);
            configDirectory = new File("");
        }
    }

    @Nonnull
    public static BasicApplication createApplication(Class manifestClass) {
        try {
            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(manifestClass);
            Class<?> applicationClass = dynamicClassLoader.loadClass(BasicApplication.class.getCanonicalName());
            Constructor<?> constructor = applicationClass.getConstructor(DynamicClassLoader.class, Class.class);
            return (BasicApplication) constructor.newInstance(dynamicClassLoader, manifestClass);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException("Unable to create application instance", ex);
        }
    }

    public void init() {
        App.setModuleProvider(moduleProvider);
        App.setConfigDirectory(configDirectory);
    }

    @Nonnull
    public Preferences getAppPreferences() {
        return appPreferences;
    }

    public void setAppPreferences(Preferences appPreferences) {
        this.appPreferences = appPreferences;
    }

    @Nonnull
    public File getAppDirectory() {
        return appDirectory;
    }

    public void setAppDirectory(File appDirectory) {
        this.appDirectory = appDirectory;
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

    public void addAddonsModules() {
        try {
            URL addonsPath = new File(configDirectory.getAbsolutePath(), ADDONS_DIRECTORY).toURI().toURL();
            addModulesFrom(addonsPath);
        } catch (MalformedURLException ex) {
            Logger.getLogger(BasicApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addModulesFrom(URI moduleClassUri) {
        moduleProvider.addModulesFrom(moduleClassUri);
    }

    public void addModulesFrom(URL moduleClassUrl) {
        moduleProvider.addModulesFrom(moduleClassUrl);
    }

    public void loadModulesFromPath(URI pathUri) {
        moduleProvider.addModulesFromPath(pathUri);
    }

    public void addModulesFromPath(URL pathUrl) {
        moduleProvider.addModulesFromPath(pathUrl);
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

    public void initModules() {
        moduleProvider.initModules();
    }
}
