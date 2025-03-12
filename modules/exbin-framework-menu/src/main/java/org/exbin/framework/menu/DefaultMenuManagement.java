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
package org.exbin.framework.menu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuContributionRule;
import org.exbin.framework.menu.api.MenuItemProvider;
import org.exbin.framework.menu.api.MenuManagement;

/**
 * Default menu management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultMenuManagement implements MenuManagement {

    private final MenuManager menuManager;
    private final String menuId;
    private final String moduleId;
    @Nullable
    private final String currentSubMenuId;

    public DefaultMenuManagement(MenuManager menuManager, String menuId, String moduleId) {
        this(menuManager, menuId, moduleId, null);
    }

    public DefaultMenuManagement(MenuManager menuManager, String menuId, String moduleId, @Nullable String currentSubMenuId) {
        this.menuManager = menuManager;
        this.menuId = menuId;
        this.moduleId = moduleId;
        this.currentSubMenuId = currentSubMenuId;
    }

    @Nonnull
    @Override
    public MenuContribution registerMenuItem(MenuItemProvider menuItemProvider) {
        MenuContribution contribution = menuManager.registerMenuItem(menuId, moduleId, menuItemProvider);
        if (currentSubMenuId != null) {
            // TODO menuManager.registerMenuRule(contribution, new SubMenuContribution(currentSubMenuId, ;));
        }
        return contribution;
    }

    @Nonnull
    @Override
    public MenuContribution registerMenuItem(Action action) {
        return menuManager.registerMenuItem(menuId, moduleId, action);
    }

    @Nonnull
    @Override
    public MenuContribution registerMenuItem(String subMenuId, Action subMenuAction) {
        return menuManager.registerMenuItem(menuId, moduleId, subMenuId, subMenuAction);
    }

    @Nonnull
    @Override
    public MenuContribution registerMenuItem(String subMenuId, String subMenuName) {
        return menuManager.registerMenuItem(menuId, moduleId, subMenuId, subMenuName);
    }

    @Nonnull
    @Override
    public MenuContribution registerMenuGroup(String groupId) {
        return menuManager.registerMenuGroup(menuId, moduleId, groupId);
    }

    @Override
    public boolean menuGroupExists(String groupId) {
        return menuManager.menuGroupExists(menuId, groupId);
    }

    @Override
    public void registerMenuRule(MenuContribution menuContribution, MenuContributionRule rule) {
        menuManager.registerMenuRule(menuContribution, rule);
    }

    @Nonnull
    @Override
    public MenuManagement getSubMenu(String subMenuId) {
        return new DefaultMenuManagement(menuManager, menuId, moduleId, subMenuId);
    }
}
