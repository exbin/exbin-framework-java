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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import org.exbin.framework.menu.api.ActionMenuContribution;
import org.exbin.framework.menu.api.SubMenuContribution;
import org.exbin.framework.contribution.ContributionDefinition;
import org.exbin.framework.contribution.TreeContributionManager;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.menu.api.DirectMenuContribution;
import org.exbin.framework.menu.api.MenuItemProvider;
import org.exbin.framework.contribution.TreeContributionSequenceBuilder;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.action.api.ActionContextRegistration;

/**
 * Default menus manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MenuManager extends TreeContributionManager implements MenuManagement {

    protected final TreeContributionSequenceBuilder builder = new TreeContributionSequenceBuilder();

    public MenuManager() {
    }

    @Override
    public void buildMenu(JMenu outputMenu, String menuId, ActionContextRegistration actionContextRegistration) {
        ContributionDefinition definition = definitions.get(menuId);
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        builder.buildSequence(new MenuSequenceOutput(outputMenu, actionContextRegistration, buttonGroups), menuId, definition);
        actionContextRegistration.finish();
    }

    @Override
    public void buildMenu(JPopupMenu outputMenu, String menuId, ActionContextRegistration actionContextRegistration) {
        ContributionDefinition definition = definitions.get(menuId);
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        builder.buildSequence(new PopupMenuSequenceOutput(outputMenu, actionContextRegistration, buttonGroups), menuId, definition);
        actionContextRegistration.finish();
    }

    @Override
    public void buildMenu(JMenuBar outputMenuBar, String menuId, ActionContextRegistration actionContextRegistration) {
        ContributionDefinition definition = definitions.get(menuId);
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        builder.buildSequence(new MenuBarSequenceOutput(outputMenuBar, actionContextRegistration, buttonGroups), menuId, definition);
        actionContextRegistration.finish();
    }

    @Override
    public boolean menuGroupExists(String menuId, String groupId) {
        ContributionDefinition definition = definitions.get(menuId);
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
        unregisterDefinition(menuId);
    }

    @Override
    public void registerMenu(String menuId, String moduleId) {
        ContributionDefinition definition = definitions.get(menuId);
        if (definition != null) {
            return;
        }

        registerDefinition(menuId, moduleId);
    }

    @Nonnull
    @Override
    public ActionMenuContribution registerMenuItem(String menuId, String moduleId, Action action) {
        ContributionDefinition definition = definitions.get(menuId);
        if (definition == null) {
            definition = new ContributionDefinition();
            definitions.put(menuId, definition);
        }

        ActionMenuContribution menuContribution = new ActionMenuContribution(action);
        definition.addContribution(menuContribution);
        return menuContribution;
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
        ContributionDefinition definition = definitions.get(menuId);
        if (definition == null) {
            definition = new ContributionDefinition();
            definitions.put(menuId, definition);
        }

        SubMenuContribution menuContribution = new SubMenuContribution(subMenuId, subMenuAction);
        definition.addContribution(menuContribution);
        return menuContribution;
    }

    @Nonnull
    @Override
    public DirectMenuContribution registerMenuItem(String menuId, String moduleId, MenuItemProvider menuItemProvider) {
        ContributionDefinition definition = definitions.get(menuId);
        if (definition == null) {
            definition = new ContributionDefinition();
            definitions.put(menuId, definition);
        }

        DirectMenuContribution menuContribution = new DirectMenuContribution(menuItemProvider);
        definition.addContribution(menuContribution);
        return menuContribution;
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerMenuGroup(String menuId, String moduleId, String groupId) {
        return registerContributionGroup(menuId, moduleId, groupId);
    }

    @Override
    public void registerMenuRule(SequenceContribution contribution, SequenceContributionRule rule) {
        registerContributionRule(contribution, rule);
    }

    @Nonnull
    @Override
    public List<Action> getAllManagedActions() {
        List<Action> actions = new ArrayList<>();
        for (ContributionDefinition definition : definitions.values()) {
            for (SequenceContribution contribution : definition.getContributions()) {
                if (contribution instanceof ActionMenuContribution) {
                    actions.add(((ActionMenuContribution) contribution).getAction());
                }
            }
        }
        return actions;
    }
}
