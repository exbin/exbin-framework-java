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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Interface for registered menus management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface MenuManagement {

    /**
     * Returns menu using given identificator.
     *
     * @param targetMenu target menu
     * @param menuId menu identificator
     * @param activationUpdateService activation update service
     */
    void buildMenu(JPopupMenu targetMenu, String menuId, ComponentActivationService activationUpdateService);

    /**
     * Returns menu using given identificator.
     *
     * @param targetMenuBar target menu bar
     * @param menuId menu identificator
     * @param activationUpdateService activation update service
     */
    void buildMenu(JMenuBar targetMenuBar, String menuId, ComponentActivationService activationUpdateService);

    /**
     * Registers menu associating it with given identificator.
     *
     * @param menuId menu identificator
     */
    void registerMenu(String menuId);

    /**
     * Unregisters menu and all it's items.
     *
     * @param menuId menu id
     */
    void unregisterMenu(String menuId);

    /**
     * Registers menu as a child item for given menu.
     *
     * @param menuId menu Id
     * @param item menu item
     * @return menu contribution
     */
    @Nonnull
    MenuContribution registerMenuItem(String menuId, JMenu item);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param menuId menu Id
     * @param item menu item
     * @return menu contribution
     */
    @Nonnull
    MenuContribution registerMenuItem(String menuId, JMenuItem item);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param menuId menu Id
     * @param action action
     * @return menu contribution
     */
    @Nonnull
    MenuContribution registerMenuItem(String menuId, Action action);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param menuId menu Id
     * @param subMenuId sub-menu id
     * @param subMenuAction sub-menu action
     * @return menu contribution
     */
    @Nonnull
    MenuContribution registerMenuItem(String menuId, String subMenuId, Action subMenuAction);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param menuId menu Id
     * @param subMenuId sub-menu id
     * @param subMenuName sub-menu name
     * @return menu contribution
     */
    @Nonnull
    MenuContribution registerMenuItem(String menuId, String subMenuId, String subMenuName);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param menuId menu Id
     * @param groupId group id
     * @return menu contribution
     */
    @Nonnull
    MenuContribution registerMenuGroup(String menuId, String groupId);

    /**
     * Returns true if given menu group exists.
     *
     * @param menuId menu id
     * @param groupId group id
     * @return true if group exists
     */
    boolean menuGroupExists(String menuId, String groupId);

    /**
     * Registers menu contribution rule.
     *
     * @param menuContribution menu contribution
     * @param rule menu contribution rule
     */
    void registerMenuRule(MenuContribution menuContribution, MenuContributionRule rule);
}
