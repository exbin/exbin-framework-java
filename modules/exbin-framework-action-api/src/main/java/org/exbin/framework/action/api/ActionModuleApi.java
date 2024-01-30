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
package org.exbin.framework.action.api;

import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.exbin.framework.action.popup.api.ComponentPopupEventDispatcher;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ClipboardActionsApi;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.utils.ClipboardActionsUpdater;

/**
 * Interface for XBUP framework action module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ActionModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(ActionModuleApi.class);
    public static final String CLIPBOARD_ACTIONS_MENU_GROUP_ID = MODULE_ID + ".clipboardActionsMenuGroup";
    public static final String CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID = MODULE_ID + ".clipboardActionsToolBarGroup";

    void setupAction(Action action, ResourceBundle bundle, String actionId);

    /**
     * Sets action values according to values specified by resource bundle.
     *
     * @param action modified action
     * @param bundle source bundle
     * @param resourceClass resourceClass
     * @param actionId action identifier and bundle key prefix
     */
    void setupAction(Action action, ResourceBundle bundle, Class<?> resourceClass, String actionId);

    /**
     * Requests update of actions with specific instance.
     *
     * @param component component instance
     */
    void updateActionsForComponent(Object component);

    @Nonnull
    JMenuItem actionToMenuItem(Action action);

    @Nonnull
    JMenuItem actionToMenuItem(Action action, @Nullable Map<String, ButtonGroup> buttonGroups);

    /**
     * Returns menu using given identificator.
     *
     * @param targetMenu target menu
     * @param menuId menu identificator
     */
    void buildMenu(JPopupMenu targetMenu, String menuId);

    /**
     * Returns menu using given identificator.
     *
     * @param targetMenuBar target menu bar
     * @param menuId menu identificator
     */
    void buildMenu(JMenuBar targetMenuBar, String menuId);

    /**
     * Registers menu associating it with given identificator.
     *
     * @param menuId menu identificator
     * @param pluginId plugin identificator
     */
    void registerMenu(String menuId, String pluginId);

    /**
     * Registers menu group for particular menu.
     *
     * @param menuId menu identificator
     * @param menuGroup menu group
     */
    void registerMenuGroup(String menuId, MenuGroup menuGroup);

    /**
     * Registers menu as a child item for given menu.
     *
     * @param menuId menu Id
     * @param pluginId plugin Id
     * @param item menu item
     * @param position menu position
     */
    void registerMenuItem(String menuId, String pluginId, JMenu item, MenuPosition position);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param menuId menu Id
     * @param pluginId plugin Id
     * @param item menu item
     * @param position menu position
     */
    void registerMenuItem(String menuId, String pluginId, JMenuItem item, MenuPosition position);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param menuId menu Id
     * @param pluginId plugin Id
     * @param action action
     * @param position menu position
     */
    void registerMenuItem(String menuId, String pluginId, Action action, MenuPosition position);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param menuId menu Id
     * @param pluginId plugin Id
     * @param subMenuId sub-menu id
     * @param subMenuAction sub-menu action
     * @param position menu position
     */
    void registerMenuItem(String menuId, String pluginId, String subMenuId, Action subMenuAction, MenuPosition position);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param menuId menu Id
     * @param pluginId plugin Id
     * @param subMenuId sub-menu id
     * @param subMenuName sub-menu name
     * @param position menu position
     */
    void registerMenuItem(String menuId, String pluginId, String subMenuId, String subMenuName, MenuPosition position);

    /**
     * Returns clipboard/editing actions.
     *
     * @return clipboard editing actions
     */
    @Nonnull
    ClipboardActionsApi getClipboardActions();

    @Nonnull
    ClipboardActionsApi getClipboardTextActions();

    /**
     * Creates new instance of the clipboard actions set.
     *
     * @param clipboardHandler clipboard handler
     * @return clipboard actions set
     */
    @Nonnull
    ClipboardActionsUpdater createClipboardActions(ClipboardActionsHandler clipboardHandler);

    /**
     * Registers menu clipboard actions.
     */
    void registerMenuClipboardActions();

    /**
     * Returns tool bar using given identificator.
     *
     * @param targetToolBar target toolbar
     * @param toolBarId toolbar id
     */
    void buildToolBar(JToolBar targetToolBar, String toolBarId);

    /**
     * Fills given popup menu with default clipboard actions.
     *
     * @param popupMenu popup menu
     * @param position target index position or -1 for adding at the end
     */
    void fillPopupMenu(JPopupMenu popupMenu, int position);

    /**
     * Registers tool bar associating it with given identificator.
     *
     * @param toolBarId toolbar id
     * @param pluginId plugin id
     */
    void registerToolBar(String toolBarId, String pluginId);

    /**
     * Registers tool bar group for particular tool bar.
     *
     * @param toolBarId toolbar id
     * @param toolBarGroup toolbar group
     */
    void registerToolBarGroup(String toolBarId, ToolBarGroup toolBarGroup);

    /**
     * Registers item as a child item for given tool bar.
     *
     * @param toolBarId toolbar id
     * @param pluginId plugin id
     * @param action action
     * @param position toolbar position
     */
    void registerToolBarItem(String toolBarId, String pluginId, Action action, ToolBarPosition position);

    /**
     * Registers tool bar clipboard actions.
     */
    void registerToolBarClipboardActions();

    void registerClipboardMenuItems(String menuId, String moduleId, SeparationMode separationMode);

    void registerClipboardMenuItems(ClipboardActionsApi actions, String menuId, String moduleId, SeparationMode separationMode);

    /**
     * Registers clipboard handler for main clipboard actions.
     *
     * @param clipboardHandler clipboard handler
     */
    void registerClipboardHandler(ClipboardActionsHandler clipboardHandler);

    /**
     * Registers default clipboard actions in default popup menu.
     */
    void registerClipboardTextActions();

    /**
     * Unregisters menu and all it's items.
     *
     * @param menuId menu id
     */
    void unregisterMenu(String menuId);

    /**
     * Returns true if given menu group exists.
     *
     * @param menuId menu id
     * @param groupId group id
     * @return true if group exists
     */
    boolean menuGroupExists(String menuId, String groupId);

    /**
     * Adds component popup menu event dispatcher.
     *
     * @param dispatcher event dispatcher
     */
    void addComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher);

    /**
     * Removes component popup menu event dispatcher.
     *
     * @param dispatcher event dispatcher
     */
    void removeComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher);
}
