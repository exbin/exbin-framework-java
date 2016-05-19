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

import java.awt.Component;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

/**
 * Interface for application's menus.
 *
 * @version 0.2.0 2016/05/19
 * @author ExBin Project (http://exbin.org)
 */
public interface MenuManagement {

    /**
     * Adds all items from given menu instance into menu manager.
     *
     * @param menu menu
     * @param pluginId plugin id
     * @param menuId menu id
     * @param positionMode position mode
     */
    public void extendMenu(JMenu menu, Integer pluginId, String menuId, PositionMode positionMode);

    /**
     * Adds given menu component into menu manager.
     *
     * @param menuItem menu item
     * @param pluginId plugin id
     * @param positionMode position mode
     * @param menuId menu id
     */
    public void addMenuItem(Component menuItem, Integer pluginId, String menuId, PositionMode positionMode);

    /**
     * Insert menu into menubar into main menu manager.
     *
     * @param menu menu
     * @param pluginId plugin id
     * @param positionMode position mode
     */
    public void insertMenu(JMenu menu, Integer pluginId, PositionMode positionMode);

    /**
     * Adds all items from given toolbar instance into menu manager.
     *
     * @param toolBar toolbar
     */
    public void extendToolBar(JToolBar toolBar);

    /**
     * Copy and insert main popup menu into given popup menu.
     *
     * @param popupMenu popup menu
     * @param position position
     */
    public void insertMainPopupMenu(JPopupMenu popupMenu, int position);
}
