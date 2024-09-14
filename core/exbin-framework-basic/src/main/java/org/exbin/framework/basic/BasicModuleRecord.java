/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exbin.framework.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    private String version = "";
    private String description = null;
    private String provider = null;
    private String homepage = null;
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
    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    @Override
    public Optional<String> getProvider() {
        return Optional.ofNullable(provider);
    }

    public void setProvider(@Nullable String provider) {
        this.provider = provider;
    }

    @Nonnull
    @Override
    public Optional<String> getHomepage() {
        return Optional.ofNullable(homepage);
    }

    public void setHomepage(@Nullable String homepage) {
        this.homepage = homepage;
    }

    @Nonnull
    @Override
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public void setDescription(@Nullable String description) {
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
