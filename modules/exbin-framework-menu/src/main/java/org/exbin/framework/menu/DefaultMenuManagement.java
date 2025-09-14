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
import javax.swing.JMenuItem;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.contribution.api.SubSequenceContributionRule;
import org.exbin.framework.menu.api.ActionMenuContribution;
import org.exbin.framework.menu.api.DirectMenuContribution;
import org.exbin.framework.menu.api.MenuItemProvider;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.menu.api.SubMenuContribution;

/**
 * Default menu management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultMenuManagement implements MenuManagement {

    private final DefaultMenuManager menuManager;
    private final String menuId;
    private final String moduleId;
    @Nullable
    private final String currentSubMenuId;

    public DefaultMenuManagement(DefaultMenuManager menuManager, String menuId, String moduleId) {
        this(menuManager, menuId, moduleId, null);
    }

    public DefaultMenuManagement(DefaultMenuManager menuManager, String menuId, String moduleId, @Nullable String currentSubMenuId) {
        this.menuManager = menuManager;
        this.menuId = menuId;
        this.moduleId = moduleId;
        this.currentSubMenuId = currentSubMenuId;
    }

    @Nonnull
    @Override
    public DirectMenuContribution registerMenuItem(MenuItemProvider menuItemProvider) {
        DirectMenuContribution contribution = menuManager.registerMenuItem(menuId, moduleId, menuItemProvider);
        if (currentSubMenuId != null) {
            menuManager.registerMenuRule(contribution, new SubSequenceContributionRule(currentSubMenuId));
        }
        return contribution;
    }

    @Nonnull
    @Override
    public ActionMenuContribution registerMenuItem(Action action) {
        ActionMenuContribution contribution = menuManager.registerMenuItem(menuId, moduleId, action);
        if (currentSubMenuId != null) {
            menuManager.registerMenuRule(contribution, new SubSequenceContributionRule(currentSubMenuId));
        }
        return contribution;
    }

    @Nonnull
    @Override
    public SubMenuContribution registerMenuItem(String subMenuId, Action subMenuAction) {
        SubMenuContribution contribution = menuManager.registerMenuItem(menuId, moduleId, subMenuId, subMenuAction);
        if (currentSubMenuId != null) {
            menuManager.registerMenuRule(contribution, new SubSequenceContributionRule(currentSubMenuId));
        }
        return contribution;
    }

    @Nonnull
    @Override
    public SubMenuContribution registerMenuItem(String subMenuId, String subMenuName) {
        SubMenuContribution contribution = menuManager.registerMenuItem(menuId, moduleId, subMenuId, subMenuName);
        if (currentSubMenuId != null) {
            menuManager.registerMenuRule(contribution, new SubSequenceContributionRule(currentSubMenuId));
        }
        return contribution;
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerMenuGroup(String groupId) {
        GroupSequenceContribution contribution = menuManager.registerMenuGroup(menuId, moduleId, groupId);
        if (currentSubMenuId != null) {
            menuManager.registerMenuRule(contribution, new SubSequenceContributionRule(currentSubMenuId));
        }
        return contribution;
    }

    @Override
    public boolean menuGroupExists(String groupId) {
        return menuManager.menuGroupExists(menuId, groupId);
    }

    @Override
    public void registerMenuRule(SequenceContribution contribution, SequenceContributionRule rule) {
        menuManager.registerMenuRule(contribution, rule);
    }

    @Nonnull
    @Override
    public MenuManagement getSubMenu(String subMenuId) {
        return new DefaultMenuManagement(menuManager, menuId, moduleId, subMenuId);
    }
}
