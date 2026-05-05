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
package org.exbin.jaguif.tabpages.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;

/**
 * Interface for tab pages.
 */
@ParametersAreNonnullByDefault
public interface TabPages {

    /**
     * Returns tab pages component.
     *
     * @return component
     */
    @Nonnull
    JComponent getComponent();

    /**
     * Adds tab page component.
     *
     * @param page tab page component
     */
    void addPage(TabPagesComponent page);

    /**
     * Changes active page.
     *
     * @param index active page index
     */
    void changeActivePageIndex(int index);

    /**
     * Returns count of registered pages.
     *
     * @return number of pages
     */
    int getPagesCount();
}
