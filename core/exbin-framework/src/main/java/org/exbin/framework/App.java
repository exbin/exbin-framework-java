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
package org.exbin.framework;

import java.io.File;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Framework application.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public final class App {

    private static ModuleProvider moduleProvider = null;
    private static ResourceBundle appBundle = null;
    private static File configDirectory = null;

    private App() {
        // No instance
    }

    public static void launch(Runnable runnable) {
        if (moduleProvider == null) {
            throw createNotInitializedException();
        }

        moduleProvider.launch(runnable);
    }

    public static void launch(String launcherModuleId, String[] args) {
        if (moduleProvider == null) {
            throw createNotInitializedException();
        }

        moduleProvider.launch(launcherModuleId, args);
    }

    @Nonnull
    public static <T extends Module> T getModule(Class<T> interfaceClass) {
        if (moduleProvider != null) {
            return moduleProvider.getModule(interfaceClass);
        }

        throw createNotInitializedException();
    }

    @Nonnull
    public static ModuleProvider getModuleProvider() {
        return Objects.requireNonNull(moduleProvider);
    }

    @Nonnull
    public static File getConfigDirectory() {
        return Objects.requireNonNull(configDirectory);
    }

    @Nonnull
    public static ResourceBundle getAppBundle() {
        return Objects.requireNonNull(appBundle);
    }

    public static void setModuleProvider(ModuleProvider moduleProvider) {
        if (App.moduleProvider != null) {
            throw createAlreadyInitializedException();
        }

        App.moduleProvider = moduleProvider;
    }

    public static void setConfigDirectory(File configDirectory) {
        if (App.configDirectory != null) {
            throw createAlreadyInitializedException();
        }

        App.configDirectory = configDirectory;
    }

    public static void setAppBundle(ResourceBundle appBundle) {
        if (App.appBundle != null) {
            throw createAlreadyInitializedException();
        }

        App.appBundle = appBundle;
    }

    private static IllegalStateException createNotInitializedException() {
        return new IllegalStateException("Module provider not initialized");
    }

    private static IllegalStateException createAlreadyInitializedException() {
        return new IllegalStateException("Value was already initialized");
    }
}
