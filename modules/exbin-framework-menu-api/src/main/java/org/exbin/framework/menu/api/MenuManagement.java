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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;

/**
 * Interface for registered menus management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface MenuManagement {

    /**
     * Registers menu as a child item for given menu.
     *
     * @param menuProvider menu provider
     * @return menu contribution
     */
    @Nonnull
    MenuContribution registerMenuItem(MenuItemProvider menuProvider);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param action action
     * @return menu contribution
     */
    @Nonnull
    MenuContribution registerMenuItem(Action action);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param subMenuId sub-menu id
     * @param subMenuAction sub-menu action
     * @return menu contribution
     */
    @Nonnull
    MenuContribution registerMenuItem(String subMenuId, Action subMenuAction);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param subMenuId sub-menu id
     * @param subMenuName sub-menu name
     * @return menu contribution
     */
    @Nonnull
    MenuContribution registerMenuItem(String subMenuId, String subMenuName);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param groupId group id
     * @return menu contribution
     */
    @Nonnull
    MenuContribution registerMenuGroup(String groupId);

    /**
     * Returns true if given menu group exists.
     *
     * @param groupId group id
     * @return true if group exists
     */
    boolean menuGroupExists(String groupId);

    /**
     * Registers menu contribution rule.
     *
     * @param menuContribution menu contribution
     * @param rule menu contribution rule
     */
    void registerMenuRule(MenuContribution menuContribution, MenuContributionRule rule);

    /**
     * Returns submenu management.
     *
     * @param subMenuId submenu id
     * @return menu management
     */
    @Nonnull
    MenuManagement getSubMenu(String subMenuId);
}
