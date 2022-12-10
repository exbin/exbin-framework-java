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

import java.awt.Component;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

/**
 * Interface for application's menus.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface MenuManagement {

    /**
     * Adds all items from given menu instance into menu manager.
     *
     * @param menu menu
     * @param pluginId plugin id
     * @param menuId menu id
     * @param positionMode position mode
     */
    void extendMenu(JMenu menu, Integer pluginId, String menuId, PositionMode positionMode);

    /**
     * Adds given menu component into menu manager.
     *
     * @param menuItem menu item
     * @param pluginId plugin id
     * @param positionMode position mode
     * @param menuId menu id
     */
    void addMenuItem(Component menuItem, Integer pluginId, String menuId, PositionMode positionMode);

    /**
     * Insert menu into menubar into main menu manager.
     *
     * @param menu menu
     * @param pluginId plugin id
     * @param positionMode position mode
     */
    void insertMenu(JMenu menu, Integer pluginId, PositionMode positionMode);

    /**
     * Adds all items from given toolbar instance into menu manager.
     *
     * @param toolBar toolbar
     */
    void extendToolBar(JToolBar toolBar);

    /**
     * Copy and insert main popup menu into given popup menu.
     *
     * @param popupMenu popup menu
     * @param position position
     */
    void insertMainPopupMenu(JPopupMenu popupMenu, int position);
}
