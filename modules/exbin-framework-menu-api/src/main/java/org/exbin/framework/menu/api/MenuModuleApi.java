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
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionContextService;

/**
 * Interface for menu support module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface MenuModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(MenuModuleApi.class);
    public static final String MAIN_MENU_ID = "mainMenu";
    public static final String FILE_SUBMENU_ID = MAIN_MENU_ID + "/File";
    public static final String EDIT_SUBMENU_ID = MAIN_MENU_ID + "/Edit";
    public static final String VIEW_SUBMENU_ID = MAIN_MENU_ID + "/View";
    public static final String TOOLS_SUBMENU_ID = MAIN_MENU_ID + "/Tools";
    public static final String OPTIONS_SUBMENU_ID = MAIN_MENU_ID + "/Options";
    public static final String HELP_SUBMENU_ID = MAIN_MENU_ID + "/Help";

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
    MenuManagement getMainMenuManagement(String moduleId);

    /**
     * Returns menu management interface.
     *
     * @param menuId menu id
     * @param moduleId module id
     * @return menu management interface
     */
    @Nonnull
    MenuManagement getMenuManagement(String menuId, String moduleId);

    /**
     * Registers menu associating it with given identificator.
     *
     * @param menuId menu identificator
     * @param moduleId module ID
     */
    void registerMenu(String menuId, String moduleId);

    /**
     * Unregisters menu and all it's items.
     *
     * @param menuId menu id
     */
    void unregisterMenu(String menuId);

    /**
     * Returns menu using given identificator.
     *
     * @param targetMenu target menu
     * @param menuId menu identificator
     * @param actionContextService action context service
     */
    void buildMenu(JPopupMenu targetMenu, String menuId, ActionContextService actionContextService);

    /**
     * Returns menu using given identificator.
     *
     * @param targetMenuBar target menu bar
     * @param menuId menu identificator
     * @param actionContextService action context service
     */
    void buildMenu(JMenuBar targetMenuBar, String menuId, ActionContextService actionContextService);

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
     * @param subMenuId optional sub menu id
     * @param moduleId module id
     * @param separationMode separation mode
     */
    void registerClipboardMenuItems(String menuId, @Nullable String subMenuId, String moduleId, SeparationMenuContributionRule.SeparationMode separationMode);

    /**
     * Registers clipboard menu items.
     *
     * @param actions clipboard actions
     * @param menuId menu id
     * @param subMenuId optional sub menu id
     * @param moduleId module id
     * @param separationMode separation mode
     */
    void registerClipboardMenuItems(ClipboardActionsApi actions, String menuId, @Nullable String subMenuId, String moduleId, SeparationMenuContributionRule.SeparationMode separationMode);

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
