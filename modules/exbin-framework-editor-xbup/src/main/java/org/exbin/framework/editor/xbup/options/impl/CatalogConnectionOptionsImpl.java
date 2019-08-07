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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.xbup.preferences.ServiceConnectionPreferences;
import org.exbin.framework.gui.options.api.OptionsData;
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

    @Nullable
    @Override
    public String getServiceConnectionUrl() {
        return serviceConnectionUrl;
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

    @Nullable
    @Override
    public String getCatalogUpdateUrl() {
        return catalogUpdateUrl;
    }

    @Override
    public void setCatalogUpdateUrl(@Nullable String catalogUpdateUrl) {
        this.catalogUpdateUrl = catalogUpdateUrl;
    }

    public void loadFromParameters(ServiceConnectionPreferences preferences) {
        serviceConnectionAllowed = preferences.isServiceConnectionAllowed();
        serviceConnectionUrl = preferences.getServiceConnectionUrl();
        catalogUpdateAllowed = preferences.isCatalogUpdateAllowed();
        catalogUpdateUrl = preferences.getCatalogUpdateUrl();
    }

    public void saveToParameters(ServiceConnectionPreferences preferences) {
        preferences.setServiceConnectionAllowed(serviceConnectionAllowed);
        preferences.setServiceConnectionUrl(serviceConnectionUrl);
        preferences.setCatalogUpdateAllowed(catalogUpdateAllowed);
        preferences.setCatalogUpdateUrl(catalogUpdateUrl);
    }
}
