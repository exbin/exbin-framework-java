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
package org.exbin.framework.editor.xbup.preferences;

import java.util.Optional;
import javax.annotation.Nonnull;
import org.exbin.framework.api.Preferences;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.xbup.options.CatalogConnectionOptions;

/**
 * Wave editor color preferences.
 *
 * @version 0.2.1 2019/07/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ServiceConnectionPreferences implements CatalogConnectionOptions {

    public static final String PREFERENCES_SERVICE_CONNECTION_ALLOWED = "serviceConnectionAllowed";
    public static final String PREFERENCES_SERVICE_CONNECTION_URL = "serviceConnectionURL";
    public static final String PREFERENCES_CATALOG_UPDATE_ALLOWED = "catalogUpdateAllowed";
    public static final String PREFERENCES_CATALOG_UPDATE_URL = "catalogUpdateURL";

    private final Preferences preferences;

    public ServiceConnectionPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean isServiceConnectionAllowed() {
        return preferences.getBoolean(PREFERENCES_SERVICE_CONNECTION_ALLOWED, true);
    }

    @Override
    public void setServiceConnectionAllowed(boolean allowed) {
        preferences.putBoolean(PREFERENCES_SERVICE_CONNECTION_ALLOWED, allowed);
    }

    @Nonnull
    @Override
    public Optional<String> getServiceConnectionUrl() {
        return preferences.get(PREFERENCES_SERVICE_CONNECTION_URL);
    }

    @Override
    public void setServiceConnectionUrl(String connectionUrl) {
        preferences.put(PREFERENCES_SERVICE_CONNECTION_URL, connectionUrl);
    }

    @Override
    public boolean isCatalogUpdateAllowed() {
        return preferences.getBoolean(PREFERENCES_CATALOG_UPDATE_ALLOWED, true);
    }

    @Override
    public void setCatalogUpdateAllowed(boolean allowed) {
        preferences.putBoolean(PREFERENCES_CATALOG_UPDATE_ALLOWED, allowed);
    }

    @Nonnull
    @Override
    public Optional<String> getCatalogUpdateUrl() {
        return preferences.get(PREFERENCES_CATALOG_UPDATE_URL);
    }

    @Override
    public void setCatalogUpdateUrl(String updateUrl) {
        preferences.put(PREFERENCES_CATALOG_UPDATE_URL, updateUrl);
    }
}
