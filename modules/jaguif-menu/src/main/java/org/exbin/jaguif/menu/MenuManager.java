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
package org.exbin.jaguif.menu;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import org.exbin.jaguif.App;
import org.exbin.jaguif.menu.api.SubMenuContribution;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;
import org.exbin.jaguif.menu.api.DirectMenuContribution;
import org.exbin.jaguif.menu.api.MenuItemProvider;
import org.exbin.jaguif.menu.api.MenuManagement;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.context.api.ContextStateProvider;
import org.exbin.jaguif.contribution.api.ContributionDefinition;
import org.exbin.jaguif.contribution.api.ContributionModuleApi;
import org.exbin.jaguif.contribution.api.TreeContributionManagement;
import org.exbin.jaguif.contribution.api.TreeContributionSequenceBuilder;

/**
 * Default menus manager.
 */
@ParametersAreNonnullByDefault
public class MenuManager implements MenuManagement {

    protected final TreeContributionSequenceBuilder builder;
    protected final TreeContributionManagement contributionManagement;

    public MenuManager() {
        ContributionModuleApi contributionModule = App.getModule(ContributionModuleApi.class);
        contributionManagement = contributionModule.createTreeContributionManager();
        builder = contributionModule.createTreeContributionSequenceBuilder();
    }

    @Override
    public void buildMenu(JMenu outputMenu, String menuId, ContextRegistration contextRegistration, @Nullable ContextStateProvider creationContext) {
        ContributionDefinition definition = contributionManagement.getDefinition(menuId);
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        builder.buildSequence(new MenuSequenceOutput(outputMenu, contextRegistration, creationContext, buttonGroups), menuId, definition);
        contextRegistration.finish();
    }

    @Override
    public void buildMenu(JPopupMenu outputMenu, String menuId, ContextRegistration contextRegistration, @Nullable ContextStateProvider creationContext) {
        ContributionDefinition definition = contributionManagement.getDefinition(menuId);
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        builder.buildSequence(new PopupMenuSequenceOutput(outputMenu, contextRegistration, creationContext, buttonGroups), menuId, definition);
        contextRegistration.finish();
    }

    @Override
    public void buildMenu(JMenuBar outputMenuBar, String menuId, ContextRegistration contextRegistration, @Nullable ContextStateProvider creationContext) {
        ContributionDefinition definition = contributionManagement.getDefinition(menuId);
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        builder.buildSequence(new MenuBarSequenceOutput(outputMenuBar, contextRegistration, creationContext, buttonGroups), menuId, definition);
        contextRegistration.finish();
    }

    @Override
    public boolean menuGroupExists(String menuId, String groupId) {
        ContributionDefinition definition = contributionManagement.getDefinition(menuId);
        if (definition == null) {
            return false;
        }

        for (SequenceContribution contribution : definition.getContributions()) {
            if (contribution instanceof GroupSequenceContribution && ((GroupSequenceContribution) contribution).getGroupId().equals(groupId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void unregisterMenu(String menuId) {
        contributionManagement.unregisterDefinition(menuId);
    }

    @Override
    public void registerMenu(String menuId, String moduleId) {
        ContributionDefinition definition = contributionManagement.getDefinition(menuId);
        if (definition != null) {
            return;
        }

        contributionManagement.registerDefinition(menuId, moduleId);
    }

    @Override
    public void registerMenuContribution(String menuId, String moduleId, SequenceContribution contribution) {
        ContributionDefinition definition = contributionManagement.getDefinition(menuId);
        if (definition == null) {
            definition = contributionManagement.registerDefinition(menuId, moduleId);
        }

        definition.addContribution(contribution);
    }

    @Nonnull
    @Override
    public SubMenuContribution registerMenuItem(String menuId, String moduleId, String subMenuId, String subMenuName) {
        Action subMenuAction = new AbstractAction(subMenuName) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        return registerMenuItem(menuId, moduleId, subMenuId, subMenuAction);
    }

    @Nonnull
    @Override
    public SubMenuContribution registerMenuItem(String menuId, String moduleId, String subMenuId, Action subMenuAction) {
        ContributionDefinition definition = contributionManagement.getDefinition(menuId);
        if (definition == null) {
            definition = contributionManagement.registerDefinition(menuId, moduleId);
        }

        SubMenuContribution menuContribution = new SubMenuContribution(subMenuId, subMenuAction);
        definition.addContribution(menuContribution);
        return menuContribution;
    }

    @Nonnull
    @Override
    public DirectMenuContribution registerMenuItem(String menuId, String moduleId, MenuItemProvider menuItemProvider) {
        ContributionDefinition definition = contributionManagement.getDefinition(menuId);
        if (definition == null) {
            definition = contributionManagement.registerDefinition(menuId, moduleId);
        }

        DirectMenuContribution menuContribution = new DirectMenuContribution(menuItemProvider);
        definition.addContribution(menuContribution);
        return menuContribution;
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerMenuGroup(String menuId, String moduleId, String groupId) {
        return contributionManagement.registerContributionGroup(menuId, moduleId, groupId);
    }

    @Override
    public void registerMenuRule(SequenceContribution contribution, SequenceContributionRule rule) {
        contributionManagement.registerContributionRule(contribution, rule);
    }

    @Nonnull
    @Override
    public List<SequenceContribution> getContributions() {
        List<ContributionDefinition> definitions = contributionManagement.getAllDefinitions();
        List<SequenceContribution> contributions = new ArrayList<>();
        for (ContributionDefinition definition : definitions) {
            contributions.addAll(definition.getContributions());
        }
        return contributions;
    }
}
