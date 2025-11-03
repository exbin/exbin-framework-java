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
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;

/**
 * Interface for registered menu management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface MenuDefinitionManagement {

    /**
     * Registers menu as a child item for given menu.
     *
     * @param menuProvider menu provider
     * @return menu contribution
     */
    @Nonnull
    DirectMenuContribution registerMenuItem(MenuItemProvider menuProvider);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param action action
     * @return menu contribution
     */
    @Nonnull
    ActionMenuContribution registerMenuItem(Action action);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param subMenuId sub-menu id
     * @param subMenuAction sub-menu action
     * @return menu contribution
     */
    @Nonnull
    SubMenuContribution registerMenuItem(String subMenuId, Action subMenuAction);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param subMenuId sub-menu id
     * @param subMenuName sub-menu name
     * @return menu contribution
     */
    @Nonnull
    SubMenuContribution registerMenuItem(String subMenuId, String subMenuName);

    /**
     * Registers menu item as a child item for given menu.
     *
     * @param groupId group id
     * @return menu contribution
     */
    @Nonnull
    GroupSequenceContribution registerMenuGroup(String groupId);

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
     * @param contribution menu contribution
     * @param rule menu contribution rule
     */
    void registerMenuRule(SequenceContribution contribution, SequenceContributionRule rule);

    /**
     * Returns submenu management.
     *
     * @param subMenuId submenu id
     * @return menu management
     */
    @Nonnull
    MenuDefinitionManagement getSubMenu(String subMenuId);
}
