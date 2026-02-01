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
package org.exbin.framework.addon.manager.api;

import java.awt.Component;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Addon manager page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface AddonManagerPage {

    /**
     * Returns tab title.
     *
     * @return title
     */
    @Nonnull
    String getTitle();

    /**
     * Returns tab component.
     *
     * @return component
     */
    @Nonnull
    Component getComponent();

    /**
     * Notifies content changed.
     */
    void notifyChanged();

    /**
     * Sets catalog base url.
     *
     * @param addonCatalogUrl addon catalog url
     */
    void setCatalogUrl(String addonCatalogUrl);

    /**
     * Sets filter condition.
     *
     * @param filter filter condition
     * @param finished finished method
     */
    void setFilter(Object filter, Runnable finished);

    /**
     * Sets search condition.
     *
     * @param search search condition
     * @param finished finished method
     */
    void setSearch(String search, Runnable finished);
}
