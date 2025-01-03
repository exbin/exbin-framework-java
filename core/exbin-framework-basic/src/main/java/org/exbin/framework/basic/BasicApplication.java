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

    private Preferences appPreferences;

    private BasicModuleProvider moduleProvider;
//    private final List<URI> plugins = new ArrayList<>();
//    private String targetLaf = null;
    private File appDirectory = new File("");

    public BasicApplication(DynamicClassLoader dynamicClassLoader, Class manifestClass) {
        moduleProvider = new BasicModuleProvider(dynamicClassLoader, manifestClass);
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
