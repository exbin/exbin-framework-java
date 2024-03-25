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
package org.exbin.framework.client.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * Interface for XBUP framework client module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ClientModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(ClientModuleApi.class);

    /**
     * Attempts to connect to running service.
     *
     * @return true if connection was established
     */
    boolean connectToService();

    /**
     * Attempts to connect to fallback service using database connection.
     *
     * @return true if connection was established
     */
    boolean runLocalCatalog();

    boolean startLocalCatalog();

    boolean updateLocalCatalog();

    /**
     * Run internal service.
     */
    void useBuildInCatalog();

    @Nonnull
    XBACatalog getCatalog();

    @Nonnull
    XBPluginRepository getPluginRepository();

    boolean isDevMode();

    void setDevMode(boolean devMode);

    void addClientConnectionListener(ClientConnectionListener listener);

    void removeClientConnectionListener(ClientConnectionListener listener);

    void addPluginRepositoryListener(PluginRepositoryListener listener);

    void removePluginRepositoryListener(PluginRepositoryListener listener);
}
