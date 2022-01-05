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
package org.exbin.framework.service.gui;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.action.api.MenuManagement;
import org.exbin.xbup.core.catalog.XBACatalog;

/**
 * Interface for catalog management panels.
 *
 * @version 0.2.0 2016/02/01
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface CatalogManagementAware {

    /**
     * Passes menu management.
     *
     * @param menuManagement menu management
     */
    void setMenuManagement(MenuManagement menuManagement);

    /**
     * Passes access to catalog.
     *
     * @param catalog catalog
     */
    void setCatalog(XBACatalog catalog);
}
