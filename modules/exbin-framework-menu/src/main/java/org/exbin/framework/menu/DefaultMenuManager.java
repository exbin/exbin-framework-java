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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.menu.api.ActionMenuContribution;
import org.exbin.framework.menu.api.ActionMenuCreation;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.menu.api.SubMenuContribution;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.contribution.ContributionDefinition;
import org.exbin.framework.contribution.TreeContributionsManager;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.contribution.api.SubSequenceContribution;
import org.exbin.framework.contribution.api.TreeContributionSequenceOutput;
import org.exbin.framework.menu.api.DirectMenuContribution;
import org.exbin.framework.menu.api.MenuItemProvider;
import org.exbin.framework.menu.api.MenuManager;
import org.exbin.framework.action.api.ActionContextManager;

/**
 * Default menu manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultMenuManager extends TreeContributionsManager implements MenuManager {

    public DefaultMenuManager() {
    }

    @Override
    public void buildMenu(JMenu outputMenu, String menuId, ActionContextManager actionUpdateService) {
        ContributionDefinition definition = definitions.get(menuId);
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        buildSequence(new MenuWrapper(outputMenu, actionUpdateService, buttonGroups), menuId, definition);
    }

    @Override
    public void buildMenu(JPopupMenu outputMenu, String menuId, ActionContextManager actionUpdateService) {
        ContributionDefinition definition = definitions.get(menuId);
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        buildSequence(new PopupMenuWrapper(outputMenu, actionUpdateService, buttonGroups), menuId, definition);
    }

    @Override
    public void buildMenu(JMenuBar outputMenuBar, String menuId, ActionContextManager actionUpdateService) {
        ContributionDefinition definition = definitions.get(menuId);
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        buildSequence(new MenuBarWrapper(outputMenuBar, actionUpdateService, buttonGroups), menuId, definition);
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

    @Nonnull
    private static JMenuItem createMenuItem(Action action, Map<String, ButtonGroup> buttonGroups) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        JMenuItem menuItem = actionModule.actionToMenuItem(action, buttonGroups);
        return menuItem;
    }

    private static void finishMenuAction(@Nullable Action action, ActionContextManager actionUpdateService) {
        if (action == null) {
            return;
        }

        actionUpdateService.registerActionContext(action);
    }

    private static void finishMenuItem(@Nullable JMenuItem menuItem, ActionContextManager actionUpdateService) {
        if (menuItem == null) {
            return;
        }

        if (menuItem instanceof JMenu) {
            finishMenu((JMenu) menuItem, actionUpdateService);
        } else {
            Action action = menuItem.getAction();
            if (action != null) {
                finishMenuAction(action, actionUpdateService);
            }
        }
    }

    private static void finishMenu(JMenu menu, ActionContextManager actionUpdateService) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem menuItem = menu.getItem(i);
            if (menuItem == null) {
                continue;
            }
            Action action = menuItem.getAction();
            if (action != null) {
                finishMenuAction(action, actionUpdateService);
            }
            if (menuItem instanceof JMenu) {
                finishMenu((JMenu) menuItem, actionUpdateService);
            }
        }
    }

    @ParametersAreNonnullByDefault
    private static class MenuWrapper implements TreeContributionSequenceOutput {

        private final JMenu menu;
        private final ActionContextManager actionUpdateService;
        private final Map<String, ButtonGroup> buttonGroups;
        private final boolean isPopup;

        public MenuWrapper(JMenu menu, ActionContextManager actionUpdateService, Map<String, ButtonGroup> buttonGroups, boolean isPopup) {
            this.menu = menu;
            this.actionUpdateService = actionUpdateService;
            this.buttonGroups = buttonGroups;
            this.isPopup = isPopup;
        }

        public MenuWrapper(JMenu menu, ActionContextManager actionUpdateService, Map<String, ButtonGroup> buttonGroups) {
            this(menu, actionUpdateService, buttonGroups, false);
        }

        @Override
        public boolean initItem(SequenceContribution contribution, String definitionId, String subId) {
            if (contribution instanceof SubSequenceContribution) {
                Action action = ((SubMenuContribution) contribution).getAction();
                if (isPopup && action != null) {
                    ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                    if (menuCreation != null) {
                        if (!menuCreation.shouldCreate(definitionId, subId)) {
                            return false;
                        }
                    }
                }

                JMenu subMenu = UiUtils.createMenu();
                subMenu.setAction(action);
                ((SubMenuContribution) contribution).setSubMenu(subMenu);

                if (isPopup && action != null) {
                    ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                    if (menuCreation != null) {
                        menuCreation.onCreate(subMenu, definitionId, subId);
                    }
                }

                return true;
            }

            Action action;
            JMenuItem menuItem;
            if (contribution instanceof ActionMenuContribution) {
                menuItem = null;
                action = ((ActionMenuContribution) contribution).getAction();
            } else {
                menuItem = ((DirectMenuContribution) contribution).getMenuItem();
                action = menuItem.getAction();
            }
            if (isPopup && action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    if (!menuCreation.shouldCreate(definitionId, subId)) {
                        return false;
                    }
                }
            }

            if (contribution instanceof ActionMenuContribution) {
                menuItem = DefaultMenuManager.createMenuItem(action, buttonGroups);
                ((ActionMenuContribution) contribution).setMenuItem(menuItem);
            }

            if (isPopup && action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(menuItem, definitionId, subId);
                }
            }

            return true;
        }

        @Override
        public void add(SequenceContribution contribution) {
            if (contribution instanceof SubSequenceContribution) {
                JMenu subMenu = ((SubMenuContribution) contribution).getSubMenu().get();
                menu.add(subMenu);
                DefaultMenuManager.finishMenuItem(subMenu, actionUpdateService);
                return;
            }

            if (contribution instanceof DirectMenuContribution) {
                menu.add(((DirectMenuContribution) contribution).getMenuItem());
                return;
            }

            JMenuItem menuItem = ((ActionMenuContribution) contribution).getMenuItem();
            menu.add(menuItem);
            DefaultMenuManager.finishMenuItem(menuItem, actionUpdateService);
        }

        @Override
        public void addSeparator() {
            menu.addSeparator();
        }

        @Nonnull
        public JMenu getMenu() {
            return menu;
        }

        @Nonnull
        @Override
        public TreeContributionSequenceOutput createSubOutput(SubSequenceContribution subContribution) {
            return new MenuWrapper(((SubMenuContribution) subContribution).getSubMenu().get(), actionUpdateService, buttonGroups, isPopup);
        }

        @Override
        public boolean isEmpty() {
            return menu.getItemCount() == 0;
        }
    }

    @ParametersAreNonnullByDefault
    private static class MenuBarWrapper implements TreeContributionSequenceOutput {

        private final JMenuBar menuBar;
        private final ActionContextManager actionUpdateService;
        private final Map<String, ButtonGroup> buttonGroups;

        public MenuBarWrapper(JMenuBar menuBar, ActionContextManager actionUpdateService, Map<String, ButtonGroup> buttonGroups) {
            this.menuBar = menuBar;
            this.actionUpdateService = actionUpdateService;
            this.buttonGroups = buttonGroups;
        }

        @Override
        public boolean initItem(SequenceContribution contribution, String definitionId, String subId) {
            if (contribution instanceof SubMenuContribution) {
                Action action = ((SubMenuContribution) contribution).getAction();

                JMenu subMenu = UiUtils.createMenu();
                subMenu.setAction(action);
                ((SubMenuContribution) contribution).setSubMenu(subMenu);
                return true;
            }

            if (contribution instanceof ActionMenuContribution) {
                Action action = ((ActionMenuContribution) contribution).getAction();
                JMenuItem menuItem = DefaultMenuManager.createMenuItem(action, buttonGroups);
                ((ActionMenuContribution) contribution).setMenuItem(menuItem);
            }

            return true;
        }

        @Override
        public void add(SequenceContribution contribution) {
            if (contribution instanceof SubSequenceContribution) {
                JMenu subMenu = ((SubMenuContribution) contribution).getSubMenu().get();
                menuBar.add(subMenu);
                DefaultMenuManager.finishMenuItem(subMenu, actionUpdateService);
                return;
            }

            if (contribution instanceof DirectMenuContribution) {
                menuBar.add(((DirectMenuContribution) contribution).getMenuItem());
                return;
            }

            JMenuItem menuItem = ((ActionMenuContribution) contribution).getMenuItem();
            menuBar.add(menuItem);
            DefaultMenuManager.finishMenuItem(menuItem, actionUpdateService);
        }

        @Override
        public void addSeparator() {
        }

        @Nonnull
        @Override
        public TreeContributionSequenceOutput createSubOutput(SubSequenceContribution subContribution) {
            return new MenuWrapper(((SubMenuContribution) subContribution).getSubMenu().get(), actionUpdateService, buttonGroups);
        }

        @Override
        public boolean isEmpty() {
            return menuBar.getMenuCount() == 0;
        }
    }

    @ParametersAreNonnullByDefault
    private static class PopupMenuWrapper implements TreeContributionSequenceOutput {

        private final JPopupMenu menu;
        private final ActionContextManager actionUpdateService;
        private final Map<String, ButtonGroup> buttonGroups;

        public PopupMenuWrapper(JPopupMenu menu, ActionContextManager actionUpdateService, Map<String, ButtonGroup> buttonGroups) {
            this.menu = menu;
            this.actionUpdateService = actionUpdateService;
            this.buttonGroups = buttonGroups;
        }

        @Override
        public boolean initItem(SequenceContribution contribution, String definitionId, String subId) {
            if (contribution instanceof SubMenuContribution) {
                Action action = ((SubMenuContribution) contribution).getAction();

                if (action != null) {
                    ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                    if (menuCreation != null) {
                        if (!menuCreation.shouldCreate(definitionId, subId)) {
                            return false;
                        }
                    }
                }

                JMenu subMenu = UiUtils.createMenu();
                subMenu.setAction(action);
                ((SubMenuContribution) contribution).setSubMenu(subMenu);

                if (action != null) {
                    ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                    if (menuCreation != null) {
                        menuCreation.onCreate(subMenu, definitionId, subId);
                    }
                }

                return true;
            }

            Action action;
            JMenuItem menuItem;
            if (contribution instanceof ActionMenuContribution) {
                menuItem = null;
                action = ((ActionMenuContribution) contribution).getAction();
            } else {
                menuItem = ((DirectMenuContribution) contribution).getMenuItem();
                action = menuItem.getAction();
            }
            if (action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    if (!menuCreation.shouldCreate(definitionId, subId)) {
                        return false;
                    }
                }
            }

            if (contribution instanceof ActionMenuContribution) {
                menuItem = DefaultMenuManager.createMenuItem(action, buttonGroups);
                ((ActionMenuContribution) contribution).setMenuItem(menuItem);
            }

            if (action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(menuItem, definitionId, subId);
                }
            }

            return true;
        }

        @Override
        public void add(SequenceContribution contribution) {
            if (contribution instanceof SubMenuContribution) {
                JMenu subMenu = ((SubMenuContribution) contribution).getSubMenu().get();
                menu.add(subMenu);
                DefaultMenuManager.finishMenuItem(subMenu, actionUpdateService);
                return;
            }

            if (contribution instanceof DirectMenuContribution) {
                menu.add(((DirectMenuContribution) contribution).getMenuItem());
                return;
            }

            JMenuItem menuItem = ((ActionMenuContribution) contribution).getMenuItem();
            menu.add(menuItem);
            DefaultMenuManager.finishMenuItem(menuItem, actionUpdateService);
        }

        @Override
        public void addSeparator() {
            menu.addSeparator();
        }

        @Nonnull
        @Override
        public TreeContributionSequenceOutput createSubOutput(SubSequenceContribution subContribution) {
            return new MenuWrapper(((SubMenuContribution) subContribution).getSubMenu().get(), actionUpdateService, buttonGroups, true);
        }

        @Override
        public boolean isEmpty() {
            return menu.getComponentCount() == 0;
        }
    }
}
