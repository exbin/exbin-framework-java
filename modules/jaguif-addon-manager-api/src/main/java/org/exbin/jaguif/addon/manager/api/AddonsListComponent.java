/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.addon.manager.api;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;

/**
 * Addons list component.
 */
@ParametersAreNonnullByDefault
public interface AddonsListComponent {

    /**
     * Returns addons list component.
     *
     * @return addons list component
     */
    JComponent getComponent();

    /**
     * Sets component controller.
     *
     * @param controller component controller
     */
    void setController(AddonsListComponentController controller);

    /**
     * Sets remove addon catalog URL.
     *
     * @param addonCatalogUrl addon catalog URL
     */
    void setCatalogUrl(String addonCatalogUrl);

    /**
     * Notifies item changed.
     */
    void notifyItemChanged();

    /**
     * Notifies items changed.
     */
    void notifyItemsChanged();
}
