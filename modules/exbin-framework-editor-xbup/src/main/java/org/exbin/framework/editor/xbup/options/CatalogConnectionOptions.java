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
package org.exbin.framework.editor.xbup.options;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.xbup.preferences.ServiceConnectionPreferences;
import org.exbin.framework.gui.options.api.OptionsData;

/**
 * Catalog connection options.
 *
 * @version 0.2.1 2019/07/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogConnectionOptions implements OptionsData {

    private boolean serviceConnectionAllowed;
    private String serviceConnectionUrl;
    private boolean catalogUpdateAllowed;
    private String catalogUpdateUrl;

    public boolean isServiceConnectionAllowed() {
        return serviceConnectionAllowed;
    }

    public void setServiceConnectionAllowed(boolean serviceConnectionAllowed) {
        this.serviceConnectionAllowed = serviceConnectionAllowed;
    }

    @Nullable
    public String getServiceConnectionUrl() {
        return serviceConnectionUrl;
    }

    public void setServiceConnectionUrl(@Nullable String serviceConnectionUrl) {
        this.serviceConnectionUrl = serviceConnectionUrl;
    }

    public boolean isCatalogUpdateAllowed() {
        return catalogUpdateAllowed;
    }

    public void setCatalogUpdateAllowed(boolean catalogUpdateAllowed) {
        this.catalogUpdateAllowed = catalogUpdateAllowed;
    }

    @Nullable
    public String getCatalogUpdateUrl() {
        return catalogUpdateUrl;
    }

    public void setCatalogUpdateUrl(@Nullable String catalogUpdateUrl) {
        this.catalogUpdateUrl = catalogUpdateUrl;
    }

    public void loadFromParameters(ServiceConnectionPreferences preferences) {
        serviceConnectionAllowed = preferences.getServiceConnectionAllowed();
        serviceConnectionUrl = preferences.getServiceConnectionUrl();
        catalogUpdateAllowed = preferences.getCatalogUpdateAllowed();
        catalogUpdateUrl = preferences.getCatalogUpdateUrl();
    }

    public void saveToParameters(ServiceConnectionPreferences preferences) {
        preferences.setServiceConnectionAllowed(serviceConnectionAllowed);
        preferences.setServiceConnectionUrl(serviceConnectionUrl);
        preferences.setCatalogUpdateAllowed(catalogUpdateAllowed);
        preferences.setCatalogUpdateUrl(catalogUpdateUrl);
    }
}
