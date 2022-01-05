/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.editor.xbup.options.impl;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.xbup.preferences.ServiceConnectionPreferences;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.editor.xbup.options.CatalogConnectionOptions;

/**
 * Catalog connection options.
 *
 * @version 0.2.1 2019/07/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogConnectionOptionsImpl implements OptionsData, CatalogConnectionOptions {

    private boolean serviceConnectionAllowed;
    private String serviceConnectionUrl;
    private boolean catalogUpdateAllowed;
    private String catalogUpdateUrl;

    @Override
    public boolean isServiceConnectionAllowed() {
        return serviceConnectionAllowed;
    }

    @Override
    public void setServiceConnectionAllowed(boolean serviceConnectionAllowed) {
        this.serviceConnectionAllowed = serviceConnectionAllowed;
    }

    @Nonnull
    @Override
    public Optional<String> getServiceConnectionUrl() {
        return Optional.ofNullable(serviceConnectionUrl);
    }

    @Override
    public void setServiceConnectionUrl(@Nullable String serviceConnectionUrl) {
        this.serviceConnectionUrl = serviceConnectionUrl;
    }

    @Override
    public boolean isCatalogUpdateAllowed() {
        return catalogUpdateAllowed;
    }

    @Override
    public void setCatalogUpdateAllowed(boolean catalogUpdateAllowed) {
        this.catalogUpdateAllowed = catalogUpdateAllowed;
    }

    @Nonnull
    @Override
    public Optional<String> getCatalogUpdateUrl() {
        return Optional.ofNullable(catalogUpdateUrl);
    }

    @Override
    public void setCatalogUpdateUrl(@Nullable String catalogUpdateUrl) {
        this.catalogUpdateUrl = catalogUpdateUrl;
    }

    public void loadFromPreferences(ServiceConnectionPreferences preferences) {
        serviceConnectionAllowed = preferences.isServiceConnectionAllowed();
        serviceConnectionUrl = preferences.getServiceConnectionUrl().orElse(null);
        catalogUpdateAllowed = preferences.isCatalogUpdateAllowed();
        catalogUpdateUrl = preferences.getCatalogUpdateUrl().orElse(null);
    }

    public void saveToPreferences(ServiceConnectionPreferences preferences) {
        preferences.setServiceConnectionAllowed(serviceConnectionAllowed);
        preferences.setServiceConnectionUrl(serviceConnectionUrl);
        preferences.setCatalogUpdateAllowed(catalogUpdateAllowed);
        preferences.setCatalogUpdateUrl(catalogUpdateUrl);
    }
}
