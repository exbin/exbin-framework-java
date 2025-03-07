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
package org.exbin.framework.menu.api;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ClipboardActionsApi;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for menu support module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface MenuModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(MenuModuleApi.class);
    public static final String CLIPBOARD_ACTIONS_MENU_GROUP_ID = MODULE_ID + ".clipboardActionsMenuGroup";

    /**
     * Converts action to menu item.
     *
     * @param action action
     * @return menu item
     */
    @Nonnull
    JMenuItem actionToMenuItem(Action action);

    /**
     * Converts action to menu item.
     *
     * @param action action
     * @param buttonGroups button groups
     * @return menu item
     */
    @Nonnull
    JMenuItem actionToMenuItem(Action action, @Nullable Map<String, ButtonGroup> buttonGroups);

    /**
     * Returns menu management interface.
     *
     * @param moduleId module id
     * @return menu management interface
     */
    @Nonnull
    MenuManagement getMenuManagement(String moduleId);

    /**
     * Returns clipboard/editing actions.
     *
     * @return clipboard editing actions
     */
    @Nonnull
    ClipboardActionsApi getClipboardActions();

    /**
     * Returns clipboard/editing text actions.
     *
     * @return clipboard/editing text actions.
     */
    @Nonnull
    ClipboardActionsApi getClipboardTextActions();

    /**
     * Registers menu clipboard actions.
     */
    void registerMenuClipboardActions();

    /**
     * Registers default clipboard menu items.
     *
     * @param menuId menu id
     * @param moduleId module id
     * @param separationMode separation mode
     */
    void registerClipboardMenuItems(String menuId, String moduleId, SeparationMenuContributionRule.SeparationMode separationMode);

    /**
     * Registers clipboard menu items.
     *
     * @param actions clipboard actions
     * @param menuId menu id
     * @param moduleId module id
     * @param separationMode separation mode
     */
    void registerClipboardMenuItems(ClipboardActionsApi actions, String menuId, String moduleId, SeparationMenuContributionRule.SeparationMode separationMode);

    /**
     * Registers clipboard handler for main clipboard actions.
     *
     * @param clipboardHandler clipboard handler
     */
    void registerClipboardHandler(ClipboardActionsHandler clipboardHandler);

    /**
     * Returns list of action managed by menu managers.
     *
     * @return list of actions
     */
    @Nonnull
    List<Action> getMenuManagedActions();
}
