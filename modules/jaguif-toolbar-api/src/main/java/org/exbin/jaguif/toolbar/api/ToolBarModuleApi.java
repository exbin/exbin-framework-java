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
package org.exbin.jaguif.toolbar.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JToolBar;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.context.api.ContextRegistration;

/**
 * Interface for tool bar support module.
 */
@ParametersAreNonnullByDefault
public interface ToolBarModuleApi extends Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(ToolBarModuleApi.class);
    public static final String MAIN_TOOL_BAR_ID = "mainToolBar";
    public static final String CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID = MODULE_ID + ".clipboardActionsToolBarGroup";

    /**
     * Registers tool bar associating it with given identificator.
     *
     * @param toolBarId toolbar id
     * @param moduleId module id
     */
    void registerToolBar(String toolBarId, String moduleId);

    /**
     * Returns tool bar management interface.
     *
     * @param toolBarId tool bar id
     * @param moduleId module id
     * @return tool bar management interface
     */
    @Nonnull
    ToolBarDefinitionManagement getToolBarManager(String toolBarId, String moduleId);

    /**
     * Returns main tool bar management interface.
     *
     * @param moduleId module id
     * @return tool bar management interface
     */
    @Nonnull
    ToolBarDefinitionManagement getMainToolBarManager(String moduleId);

    /**
     * Creates tool bar manager.
     *
     * @return tool bar manager
     */
    @Nonnull
    ToolBarManagement createToolBarManager();

    /**
     * Returns tool bar using given identificator.
     *
     * @param targetToolBar target toolbar
     * @param toolBarId toolbar id
     * @param contextRegistration context registration
     */
    void buildToolBar(JToolBar targetToolBar, String toolBarId, ContextRegistration contextRegistration);

    /**
     * Registers tool bar clipboard actions.
     */
    void registerToolBarClipboardActions();
}
