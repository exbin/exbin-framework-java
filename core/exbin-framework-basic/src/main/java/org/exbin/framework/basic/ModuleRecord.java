/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exbin.framework.basic;

import java.util.List;
import javax.annotation.Nonnull;
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
     * Returns module name.
     *
     * @return module name
     */
    @Nonnull
    String getName();

    /**
     * Returns module description.
     *
     * @return description text
     */
    @Nonnull
    String getDescription();

    /**
     * Returns list of required dependency modules.
     *
     * @return list of dependecy module identifiers
     */
    @Nonnull
    List<String> getDependencyModuleIds();

    /**
     * Returns list of optional modules.
     *
     * @return list of module identifiers
     */
    @Nonnull
    List<String> getOptionalModuleIds();

    /**
     * Returns instance of the module.
     *
     * @return module instance
     */
    @Nonnull
    Module getModule();
}
