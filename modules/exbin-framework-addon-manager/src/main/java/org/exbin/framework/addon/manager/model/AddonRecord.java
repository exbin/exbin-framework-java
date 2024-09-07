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
package org.exbin.framework.addon.manager.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Addon record.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonRecord extends ItemRecord {

    private String description = null;
    private RepositoryRecord repository;
    private List<DependencyRecord> dependencies = new ArrayList<>();

    public AddonRecord(String id, String name) {
        super(id, name);
    }

    @Nonnull
    @Override
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @Override
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nonnull
    public RepositoryRecord getRepository() {
        return repository;
    }

    public void setRepository(RepositoryRecord repository) {
        this.repository = repository;
    }

    @Nonnull
    public List<DependencyRecord> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<DependencyRecord> dependencies) {
        this.dependencies = dependencies;
    }
}
