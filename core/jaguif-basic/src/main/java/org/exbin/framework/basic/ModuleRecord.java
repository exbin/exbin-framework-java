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

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.swing.ImageIcon;
import org.exbin.framework.Module;

/**
 * Interface for record about single module.
 *
 * @author ExBin Project (http://exbin.org)
 */
public interface ModuleRecord {

    /**
     * Returns module ID.
     *
     * @return module ID
     */
    @Nonnull
    String getModuleId();

    /**
     * Returns module type.
     *
     * @return module type
     */
    @Nonnull
    ModuleType getType();

    /**
     * Returns instance of the module.
     *
     * @return module instance
     */
    @Nonnull
    Module getModule();

    /**
     * Returns location of the module file.
     *
     * @return file location
     */
    @Nonnull
    ModuleFileLocation getFileLocation();

    /**
     * Returns module name.
     *
     * @return module name
     */
    @Nonnull
    String getName();

    /**
     * Returns module version.
     *
     * @return module version
     */
    @Nonnull
    String getVersion();

    /**
     * Returns module description.
     *
     * @return description text
     */
    @Nonnull
    Optional<String> getDescription();

    /**
     * Returns module icon.
     *
     * @return icon if present
     */
    @Nonnull
    Optional<ImageIcon> getIcon();

    /**
     * Returns module provider.
     *
     * @return module provider
     */
    @Nonnull
    Optional<String> getProvider();

    /**
     * Returns module homepage.
     *
     * @return module homepage
     */
    @Nonnull
    Optional<String> getHomepage();

    /**
     * Returns list of required dependency modules.
     *
     * @return list of dependecy module identifiers
     */
    @Nonnull
    List<String> getDependencyModuleIds();

    /**
     * Returns list of required dependency libraries.
     *
     * @return list of dependecy libraries
     */
    @Nonnull
    List<String> getDependencyLibraries();

    /**
     * Returns list of optional modules.
     *
     * @return list of module identifiers
     */
    @Nonnull
    List<String> getOptionalModuleIds();
}
