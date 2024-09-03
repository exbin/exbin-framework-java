/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exbin.framework.basic;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.Module;

/**
 * Basic module record.
 *
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BasicModuleRecord implements ModuleRecord {

    private String moduleId;
    private String name;
    private ClassLoader classLoader;
    private String description;
    private final List<String> dependencyModuleIds = new ArrayList<>();
    private final List<String> optionalModuleIds = new ArrayList<>();
    private Module module;

    public BasicModuleRecord() {
    }

    public BasicModuleRecord(String moduleId, Module module, ClassLoader classLoader) {
        this.moduleId = moduleId;
        this.module = module;
        this.classLoader = classLoader;
    }

    @Nonnull
    @Override
    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nonnull
    public ClassLoader getClassLoader() {
        if (classLoader == null) {
            throw new IllegalStateException("Attempt to use uninitialized module: " + moduleId);
        }
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nonnull
    @Override
    public List<String> getDependencyModuleIds() {
        return dependencyModuleIds;
    }

    @Nonnull
    @Override
    public List<String> getOptionalModuleIds() {
        return optionalModuleIds;
    }

    @Nonnull
    @Override
    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
}
