/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.gui.menu.api;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;

/**
 * Interface for XBUP framework menu module.
 *
 * @version 0.2.0 2016/07/22
 * @author ExBin Project (http://exbin.org)
 */
public interface GuiMenuModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(GuiMenuModuleApi.class);
    public static final String CLIPBOARD_ACTIONS_MENU_GROUP_ID = MODULE_ID + ".clipboardActionsMenuGroup";
    public static final String CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID = MODULE_ID + ".clipboardActionsToolBarGroup";

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
     * @param subMenuName sub-menu name
     * @param position menu position
     */
    void registerMenuItem(String menuId, String pluginId, String subMenuId, String subMenuName, MenuPosition position);

    /**
     * Returns clipboard/editing actions.
     *
     * @return clipboard editing actions
     */
    ClipboardActionsApi getClipboardActions();

    /**
     * Creates new instance of the clipboard actions set.
     *
     * @param clipboardHandler clipboard handler
     * @return clipboard actions set
     */
    ClipboardActions createClipboardActions(ClipboardActionsHandler clipboardHandler);

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
    public boolean menuGroupExists(String menuId, String groupId);

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
