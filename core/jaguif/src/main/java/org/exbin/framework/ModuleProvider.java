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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Module provider interface.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ModuleProvider {

    /**
     * Returns app manifest class.
     *
     * @return manifest class
     */
    @Nonnull
    Class getManifestClass();

    /**
     * Launches application via launcher module.
     *
     * @param launcherModuleId launcher module id
     * @param args command line arguments
     */
    void launch(String launcherModuleId, String[] args);

    /**
     * Launches application.
     *
     * @param runnable runnable
     */
    void launch(Runnable runnable);

    /**
     * Returns module for given module class or interface.
     *
     * @param <T> module class
     * @param interfaceClass module class or interface
     * @return module instance
     */
    @Nonnull
    <T extends Module> T getModule(Class<T> interfaceClass);
}
