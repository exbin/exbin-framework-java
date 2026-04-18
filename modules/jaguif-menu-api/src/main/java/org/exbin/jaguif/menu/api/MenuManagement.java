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
package org.exbin.jaguif.menu.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;
import org.exbin.jaguif.context.api.ContextRegistration;

/**
 * Interface for menus management.
 */
@ParametersAreNonnullByDefault
public interface MenuManagement {

    /**
     * Builds menu from given definition id.
     *
     * @param outputMenu output menu
     * @param menuId menu definition id
     * @param contextRegistration context registration
     */
    void buildMenu(JMenu outputMenu, String menuId, ContextRegistration contextRegistration);

    /**
     * Builds menu from given definition id.
     *
     * @param outputMenu output popup menu
     * @param menuId menu definition id
     * @param contextRegistration context registration
     */
    void buildMenu(JPopupMenu outputMenu, String menuId, ContextRegistration contextRegistration);

    /**
     * Builds menu from given definition id.
     *
     * @param outputMenuBar output menu bar
     * @param menuId menu definition id
     * @param contextRegistration context registration
     */
    void buildMenu(JMenuBar outputMenuBar, String menuId, ContextRegistration contextRegistration);

    /**
     * Checks whether menu group exists.
     *
     * @param menuId menu id
     * @param groupId group id
     * @return true if group exists
     */
    boolean menuGroupExists(String menuId, String groupId);

    /**
     * Registers menu definition.
     *
     * @param menuId menu id
     * @param moduleId module id
     */
    void registerMenu(String menuId, String moduleId);

    /**
     * Unregisters menu definition
     *
     * @param menuId menu id
     */
    void unregisterMenu(String menuId);

    /**
     * Registers menu contribution.
     *
     * @param menuId menu id
     * @param moduleId module id
     * @param contribution menu contribution
     */
    void registerMenuContribution(String menuId, String moduleId, SequenceContribution contribution);

    /**
     * Registers sub menu contribution.
     *
     * @param menuId menu id
     * @param moduleId module id
     * @param subMenuId sub menu id
     * @param subMenuName sub menu name
     * @return sub menu contribution
     */
    @Nonnull
    SubMenuContribution registerMenuItem(String menuId, String moduleId, String subMenuId, String subMenuName);

    /**
     * Registers sub menu contribution.
     *
     * @param menuId menu id
     * @param moduleId module id
     * @param subMenuId sub menu id
     * @param subMenuAction sub menu action
     * @return sub menu contribution
     */
    @Nonnull
    SubMenuContribution registerMenuItem(String menuId, String moduleId, String subMenuId, Action subMenuAction);

    /**
     * Registers direct menu contribution.
     *
     * @param menuId menu id
     * @param moduleId module id
     * @param menuProvider menu provider
     * @return sub menu contribution
     */
    @Nonnull
    DirectMenuContribution registerMenuItem(String menuId, String moduleId, MenuItemProvider menuProvider);

    /**
     * Registers menu group.
     *
     * @param menuId menu id
     * @param moduleId module id
     * @param groupId group id
     * @return group contribution
     */
    @Nonnull
    GroupSequenceContribution registerMenuGroup(String menuId, String moduleId, String groupId);

    /**
     * Registers menu contribution rule.
     *
     * @param contribution menu contribution
     * @param rule menu contribution rule
     */
    void registerMenuRule(SequenceContribution contribution, SequenceContributionRule rule);
}
