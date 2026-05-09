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
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.context.api.ContextRegistration;

/**
 * Interface for tab pages support module.
 */
@ParametersAreNonnullByDefault
public interface TabPagesModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(TabPagesModuleApi.class);
    public static final String MAIN_TAB_PAGES_ID = "mainTabPages";

    /**
     * Returns main tab pages management interface.
     *
     * @return tab pages management interface
     */
    @Nonnull
    TabPagesManagement getMainTabPagesManager();

    /**
     * Creates tab pages manager.
     *
     * @return tab pages manager
     */
    @Nonnull
    TabPagesManagement createTabPagesManager();

    /**
     * Returns main tab pages management definition.
     *
     * @param moduleId module id
     * @return tab pages management interface
     */
    @Nonnull
    TabPagesDefinitionManagement getMainTabPagesDefinition(String moduleId);

    /**
     * Returns main tab pages management definition.
     *
     * @param tabPagesId tab pages id
     * @param moduleId module id
     * @return tab pages management interface
     */
    @Nonnull
    TabPagesDefinitionManagement getMainTabPagesDefinition(String tabPagesId, String moduleId);

    /**
     * Returns tab pages management definition.
     *
     * @param tabPagesManagement tab pages management
     * @param tabPagesId tab pages id
     * @param moduleId module id
     * @return tab pages management interface
     */
    @Nonnull
    TabPagesDefinitionManagement createTabPagesDefinition(TabPagesManagement tabPagesManagement, String tabPagesId, String moduleId);

    /**
     * Registers tab pages associating it with given identificator.
     *
     * @param tabPagesId tab pages id
     * @param moduleId module id
     */
    void registerTabPages(String tabPagesId, String moduleId);

    /**
     * Returns tab pages using given identificator.
     *
     * @param targetTabPages target tab pages
     * @param tabPagesId tab pages id
     * @param contextRegistration context registration
     */
    void buildTabPages(TabPages targetTabPages, String tabPagesId, ContextRegistration contextRegistration);

    /**
     * Creates new tabbed pages panel.
     *
     * @return tabbed pages panel
     */
    @Nonnull
    TabPages createTabbedPagesPanel();

    /**
     * Creates new tabbed pages panel optionally showing tabs when more than one
     * is registered.
     *
     * @return tabbed pages panel
     */
    @Nonnull
    TabPages createOptTabbedPagesPanel();
}
