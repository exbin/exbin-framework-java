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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.exbin.framework.menu.api.DirectSubMenuContribution;
import org.exbin.framework.menu.api.GroupMenuContribution;
import org.exbin.framework.menu.api.GroupMenuContributionRule;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuContributionRule;
import org.exbin.framework.menu.api.PositionMenuContributionRule;
import org.exbin.framework.menu.api.RelativeMenuContributionRule;
import org.exbin.framework.menu.api.SeparationMenuContributionRule;
import org.exbin.framework.menu.api.SubMenuContributionRule;
import org.exbin.framework.menu.api.SubMenuContribution;
import org.exbin.framework.utils.ObjectUtils;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.action.api.ActionContextService;
import org.exbin.framework.menu.api.MenuItemProvider;

/**
 * Menu manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MenuManager {

    private final Map<String, MenuDefinition> menus = new HashMap<>();

    public MenuManager() {
    }

    public void buildMenu(JMenu outputMenu, String menuId, ActionContextService activationUpdateService) {
        buildMenu(new MenuWrapper(outputMenu), menuId, activationUpdateService);
        activationUpdateService.requestUpdate();
    }

    public void buildMenu(JPopupMenu outputMenu, String menuId, ActionContextService activationUpdateService) {
        buildMenu(new PopupMenuWrapper(outputMenu), menuId, activationUpdateService);
        activationUpdateService.requestUpdate();
    }

    public void buildMenu(JMenuBar outputMenuBar, String menuId, ActionContextService activationUpdateService) {
        buildMenu(new MenuBarWrapper(outputMenuBar), menuId, activationUpdateService);
        activationUpdateService.requestUpdate();
    }

    private void buildMenu(MenuOutput outputMenu, String menuId, ActionContextService activationUpdateService) {
        MenuDefinition menuDef = menus.get(menuId);

        if (menuDef == null) {
            return;
        }

        BuilderRecord builderRecord = new BuilderRecord();
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        BuilderContributionRecord lastContributionRecord = null;

        // Build contributions tree
        for (MenuContribution contribution : menuDef.getContributions()) {
            String parentGroupId = null;
            String parentSubMenuId = null;
            PositionMenuContributionRule.PositionMode positionHint = null;
            SeparationMenuContributionRule.SeparationMode separationMode = null;
            List<String> afterIds = new ArrayList<>();
            List<String> beforeIds = new ArrayList<>();
            List<MenuContributionRule> rules = menuDef.getRules().get(contribution);
            for (MenuContributionRule rule : rules) {
                if (rule instanceof PositionMenuContributionRule) {
                    positionHint = ((PositionMenuContributionRule) rule).getPositionMode();
                } else if (rule instanceof SeparationMenuContributionRule) {
                    separationMode = ((SeparationMenuContributionRule) rule).getSeparationMode();
                } else if (rule instanceof RelativeMenuContributionRule) {
                    RelativeMenuContributionRule.NextToMode nextToMode = ((RelativeMenuContributionRule) rule).getNextToMode();
                    String contributionId = ((RelativeMenuContributionRule) rule).getContributionId();
                    switch (nextToMode) {
                        case AFTER:
                            afterIds.add(contributionId);
                            break;
                        case BEFORE:
                            beforeIds.add(contributionId);
                            break;
                        default:
                            throw new AssertionError();
                    }
                } else if (rule instanceof GroupMenuContributionRule) {
                    parentGroupId = ((GroupMenuContributionRule) rule).getGroupId();
                } else if (rule instanceof SubMenuContributionRule) {
                    parentSubMenuId = ((SubMenuContributionRule) rule).getSubMenuId();
                }
            }

            BuilderGroupRecord groupRecord = createGroup(builderRecord, parentSubMenuId, parentGroupId);
            BuilderMenuRecord menuRecord = builderRecord.subMenusMap.get((String) (parentSubMenuId == null ? "" : parentSubMenuId));

            BuilderContributionRecord contributionRecord;
            String contributionId = null;
            if (contribution instanceof GroupMenuContribution) {
                String groupId = ((GroupMenuContribution) contribution).getGroupId();
                contributionRecord = menuRecord.groupsMap.get(groupId);
                if (contributionRecord == null) {
                    contributionRecord = new BuilderGroupRecord(groupId);
                    menuRecord.groupsMap.put(groupId, (BuilderGroupRecord) contributionRecord);
                }
            } else if (contribution instanceof SubMenuContribution) {
                contributionRecord = new BuilderMenuContributionRecord((SubMenuContribution) contribution);
            } else if (contribution instanceof DirectSubMenuContribution) {
                contributionRecord = new BuilderDirectSubMenuContributionRecord(((DirectSubMenuContribution) contribution));
            } else if (contribution instanceof ActionMenuContribution) {
                contributionRecord = new BuilderActionContributionRecord((ActionMenuContribution) contribution);
            } else {
                throw new IllegalStateException("Unsupported contribution type: " + (contribution == null ? "null" : contribution.getClass().getName()));
            }

            if (contributionId != null && menuRecord.contributionsMap.containsKey(contributionId)) {
                throw new IllegalStateException("Contribution with id " + contributionId + " already exists");
            }

            contributionRecord.separationMode = separationMode;
            contributionRecord.placeAfter.addAll(afterIds);
            if (positionHint != null) {
                contributionRecord.positionHint = positionHint;
            }
            contributionRecord.previousHint = lastContributionRecord;
            lastContributionRecord = contributionRecord;

            // Convert before rules to after rules
            List<String> defferedAfterIds = menuRecord.afterMap.remove(contributionId);
            if (defferedAfterIds != null) {
                contributionRecord.placeAfter.addAll(defferedAfterIds);
            }
            for (String itemId : beforeIds) {
                BuilderContributionRecord itemRecord = menuRecord.contributionsMap.get(itemId);
                if (itemRecord != null) {
                    itemRecord.placeAfter.add(contributionId);
                } else {
                    List<String> itemAfterIds = menuRecord.afterMap.get(itemId);
                    if (itemAfterIds == null) {
                        itemAfterIds = new ArrayList<>();
                        itemAfterIds.add(contributionId);
                        menuRecord.afterMap.put(itemId, itemAfterIds);
                    } else {
                        itemAfterIds.add(contributionId);
                    }
                }
            }

            groupRecord.contributions.add(contributionRecord);
            if (contributionId != null) {
                menuRecord.contributionsMap.put(contributionId, contributionRecord);
            }
        }

        // Generate menu
        List<BuilderProcessingRecord> processing = new ArrayList<>();
        BuilderContributionMatch contributionMatch = new BuilderContributionMatch();
        BuilderProcessingRecord rootRecord = new BuilderProcessingRecord();
        rootRecord.menu = builderRecord.subMenusMap.get("");
        rootRecord.menu.outputMenu = outputMenu;
        rootRecord.group = rootRecord.menu.groupsMap.get("");
        processing.add(rootRecord);
        while (!processing.isEmpty()) {
            BuilderProcessingRecord processingRecord = processing.get(processing.size() - 1);
            BuilderMenuRecord menuRecord = processingRecord.menu;
            BuilderGroupRecord groupRecord = processingRecord.group;

            if (groupRecord.processingState == SectionProcessingState.START) {
                if (groupRecord.separationMode == SeparationMenuContributionRule.SeparationMode.ABOVE || groupRecord.separationMode == SeparationMenuContributionRule.SeparationMode.AROUND) {
                    menuRecord.separatorQueued = true;
                }
                groupRecord.processingState = SectionProcessingState.CONTRIBUTION;
            }

            if (groupRecord.processingState == SectionProcessingState.CONTRIBUTION) {
                if (!groupRecord.contributions.isEmpty()) {
                    contributionMatch.clear();
                    BuilderContributionRecord contribution;
                    while (contributionMatch.nextMatch == -1) {
                        int index = 0;
                        while (index < groupRecord.contributions.size()) {
                            contribution = groupRecord.contributions.get(index);
                            boolean noAfterItems = contribution.placeAfter == null || contribution.placeAfter.isEmpty();
                            boolean directPlaceMatch = noAfterItems ? false : menuRecord.processedContributions.containsAll(contribution.placeAfter);
                            if (noAfterItems || directPlaceMatch) {
                                if (contributionMatch.fallbackMatch == -1) {
                                    contributionMatch.fallbackMatch = index;
                                }
                                if (contribution.positionHint == groupRecord.processingPosition) {
                                    if (contributionMatch.positionMatch == -1) {
                                        contributionMatch.positionMatch = index;
                                    }

                                    if (contributionMatch.nextMatch == -1 && directPlaceMatch) {
                                        contributionMatch.nextMatch = index;
                                    }

                                    if (contributionMatch.nextHintMatch == -1 && contribution.previousHint == menuRecord.previousContribution) {
                                        contributionMatch.nextHintMatch = index;
                                    }
                                }
                            }
                            index++;
                        }

                        if (contributionMatch.positionMatch >= 0 || groupRecord.processingPosition == PositionMenuContributionRule.PositionMode.BOTTOM_LAST) {
                            break;
                        }

                        groupRecord.processingPosition = PositionMenuContributionRule.PositionMode.values()[groupRecord.processingPosition.ordinal() + 1];
                    }
                    if (contributionMatch.hasFoundMatch()) {
                        int index = contributionMatch.bestMatch();
                        contribution = groupRecord.contributions.remove(index);

                        if (contribution.separationMode == SeparationMenuContributionRule.SeparationMode.ABOVE || contribution.separationMode == SeparationMenuContributionRule.SeparationMode.AROUND) {
                            menuRecord.separatorQueued = true;
                        }
                        boolean isPopup = outputMenu.isPopup();
                        if (contribution instanceof BuilderGroupRecord) {
                            BuilderProcessingRecord groupProcessingRecord = new BuilderProcessingRecord();
                            groupProcessingRecord.menu = menuRecord;
                            groupProcessingRecord.group = (BuilderGroupRecord) contribution;
                            processing.add(groupProcessingRecord);
                        } else if (contribution instanceof BuilderActionContributionRecord) {
                            BuilderActionContributionRecord contributionRecord = (BuilderActionContributionRecord) contribution;
                            if (contributionRecord.shouldCreate(isPopup, menuId, menuRecord.subMenuId)) {
                                JMenuItem menuItem = contributionRecord.createItem(isPopup, menuId, menuRecord.subMenuId, buttonGroups);
                                if (menuRecord.separatorQueued) {
                                    if (!menuRecord.outputMenu.isEmpty()) {
                                        menuRecord.outputMenu.addSeparator();
                                    }
                                    menuRecord.separatorQueued = false;
                                }
                                menuRecord.outputMenu.add(menuItem);
                                contributionRecord.finishItem(menuItem, activationUpdateService);
                                menuRecord.previousContribution = contributionRecord;
                            }
                        } else if (contribution instanceof BuilderDirectSubMenuContributionRecord) {
                            BuilderDirectSubMenuContributionRecord contributionRecord = (BuilderDirectSubMenuContributionRecord) contribution;
                            if (contributionRecord.shouldCreate(isPopup, menuId, menuRecord.subMenuId)) {
                                JMenuItem menuItem = contributionRecord.createItem(isPopup, menuId, menuRecord.subMenuId, buttonGroups);
                                if (menuRecord.separatorQueued) {
                                    if (!menuRecord.outputMenu.isEmpty()) {
                                        menuRecord.outputMenu.addSeparator();
                                    }
                                    menuRecord.separatorQueued = false;
                                }
                                menuRecord.outputMenu.add(menuItem);
                                contributionRecord.finishItem(menuItem, activationUpdateService);
                                menuRecord.previousContribution = contributionRecord;
                            }
                        } else if (contribution instanceof BuilderMenuContributionRecord) {
                            BuilderMenuContributionRecord contributionRecord = (BuilderMenuContributionRecord) contribution;
                            if (contributionRecord.shouldCreate(isPopup, menuId, menuRecord.subMenuId)) {
                                BuilderMenuRecord subSection = builderRecord.subMenusMap.get(contributionRecord.contributionId);
                                if (subSection != null) {
                                    JMenu subMenu = contributionRecord.createItem(isPopup, menuId, menuRecord.subMenuId, buttonGroups);
                                    Action action = contributionRecord.contribution.getAction();
                                    subMenu.setAction(action);
                                    contributionRecord.finishItem(subMenu, activationUpdateService);
                                    subSection.outputMenu = new MenuWrapper(subMenu);
                                    BuilderProcessingRecord subProcessingRecord = new BuilderProcessingRecord();
                                    subProcessingRecord.menu = subSection;
                                    subProcessingRecord.group = subSection.groupsMap.get("");
                                    subProcessingRecord.isSubMenu = true;
                                    processing.add(subProcessingRecord);
                                }
                                menuRecord.previousContribution = contributionRecord;
                            }
                        }

                        if (contribution.separationMode == SeparationMenuContributionRule.SeparationMode.BELOW || contribution.separationMode == SeparationMenuContributionRule.SeparationMode.AROUND) {
                            menuRecord.separatorQueued = true;
                        }
                        menuRecord.processedContributions.add(contribution.contributionId);
                    } else {
                        Logger.getLogger(MenuManager.class.getName()).log(Level.SEVERE, "Skipping items");
                        groupRecord.contributions.clear();
                    }
                    continue;
                } else {
                    groupRecord.processingState = SectionProcessingState.END;
                }
            }

            if (groupRecord.processingState == SectionProcessingState.END) {
                if (groupRecord.separationMode == SeparationMenuContributionRule.SeparationMode.BELOW || groupRecord.separationMode == SeparationMenuContributionRule.SeparationMode.AROUND) {
                    menuRecord.separatorQueued = true;
                }
                processing.remove(processing.size() - 1);
                if (processingRecord.isSubMenu && !menuRecord.outputMenu.isEmpty()) {
                    BuilderMenuRecord parentMenuRecord = processing.get(processing.size() - 1).menu;
                    if (parentMenuRecord.separatorQueued) {
                        if (!parentMenuRecord.outputMenu.isEmpty()) {
                            parentMenuRecord.outputMenu.addSeparator();
                        }
                        parentMenuRecord.separatorQueued = false;
                    }
                    parentMenuRecord.outputMenu.add(((MenuWrapper) menuRecord.outputMenu).getMenu());
                }
            }
        }
    }

    @Nonnull
    private BuilderGroupRecord createGroup(BuilderRecord builderRecord, @Nullable String subMenuId, @Nullable String groupId) {
        if (subMenuId == null) {
            subMenuId = "";
        }
        if (groupId == null) {
            groupId = "";
        }

        BuilderMenuRecord menuRecord = builderRecord.subMenusMap.get(subMenuId);
        if (menuRecord == null) {
            menuRecord = new BuilderMenuRecord(subMenuId, builderRecord.menuOutput);
            builderRecord.subMenusMap.put(subMenuId, menuRecord);
        }

        BuilderGroupRecord groupRecord = menuRecord.groupsMap.get(groupId);
        if (groupRecord == null) {
            groupRecord = new BuilderGroupRecord(groupId);
            menuRecord.groupsMap.put(groupId, groupRecord);
        }

        return groupRecord;
    }

    private static void finishMenuAction(@Nullable Action action, ActionContextService activationUpdateService) {
        if (action == null) {
            return;
        }

        activationUpdateService.requestUpdate(action);
    }

    private static void finishMenuItem(JMenuItem menuItem, ActionContextService activationUpdateService) {
        if (menuItem == null) {
            return;
        }

        if (menuItem instanceof JMenu) {
            finishMenu((JMenu) menuItem, activationUpdateService);
        } else {
            Action action = menuItem.getAction();
            if (action != null) {
                finishMenuAction(action, activationUpdateService);
            }
        }
    }

    private static void finishMenu(JMenu menu, ActionContextService activationUpdateService) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem menuItem = menu.getItem(i);
            if (menuItem == null) {
                continue;
            }
            Action action = menuItem.getAction();
            if (action != null) {
                finishMenuAction(action, activationUpdateService);
            }
            if (menuItem instanceof JMenu) {
                finishMenu((JMenu) menuItem, activationUpdateService);
            }
        }
    }

    public boolean menuGroupExists(String menuId, String groupId) {
        MenuDefinition menuDefs = menus.get(menuId);
        if (menuDefs == null) {
            return false;
        }

        for (MenuContribution contribution : menuDefs.getContributions()) {
            if (contribution instanceof GroupMenuContribution && ((GroupMenuContribution) contribution).getGroupId().equals(groupId)) {
                return true;
            }
        }
                
        return false;
    }

    public void unregisterMenu(String menuId) {
        MenuDefinition definition = menus.get(menuId);
        if (definition != null) {
            // TODO clear pointers to improve garbage collection performance?
//            List<MenuContribution> contributions = definition.getContributions();
//            for (MenuContribution contribution : contributions) {
//                contribution.
//            }

            /*            for (Map.Entry<String, String> usage : pluginsUsage.entrySet()) {
                if (menuId.equals(usage.getValue())) {
                    pluginsUsage.remove(usage.getKey());
                    break;
                }
            } */
            menus.remove(menuId);
        }
    }

    @Nonnull
    public List<Action> getAllManagedActions() {
        List<Action> actions = new ArrayList<>();
        for (MenuDefinition menuDef : menus.values()) {
            for (MenuContribution contribution : menuDef.getContributions()) {
                if (contribution instanceof ActionMenuContribution) {
                    actions.add(((ActionMenuContribution) contribution).getAction());
                }
            }
        }
        return actions;
    }

    public void registerMenu(String menuId, String moduleId) {
        ObjectUtils.requireNonNull(menuId);
        ObjectUtils.requireNonNull(moduleId);

        MenuDefinition menu = menus.get(menuId);
        if (menu != null) {
            if (menu.getModuleId().isPresent()) {
                throw new IllegalStateException("Menu with ID " + menuId + " already exists.");
            } else {
                menu.setModuleId(moduleId);
                return;
            }
        }

        MenuDefinition menuDefinition = new MenuDefinition(moduleId);
        menus.put(menuId, menuDefinition);
    }

    @Nonnull
    public MenuContribution registerMenuItem(String menuId, String pluginId, Action action) {
        MenuDefinition menuDef = menus.get(menuId);
        if (menuDef == null) {
            menuDef = new MenuDefinition(null);
            menus.put(menuId, menuDef);
        }

        ActionMenuContribution menuContribution = new ActionMenuContribution(action);
        menuDef.getContributions().add(menuContribution);
        return menuContribution;
    }

    @Nonnull
    public MenuContribution registerMenuItem(String menuId, String pluginId, String subMenuId, String subMenuName) {
        Action subMenuAction = new AbstractAction(subMenuName) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        return registerMenuItem(menuId, pluginId, subMenuId, subMenuAction);
    }

    @Nonnull
    public MenuContribution registerMenuItem(String menuId, String pluginId, String subMenuId, Action subMenuAction) {
        MenuDefinition menuDef = menus.get(menuId);
        if (menuDef == null) {
            menuDef = new MenuDefinition(null);
            menus.put(menuId, menuDef);
        }

        SubMenuContribution menuContribution = new SubMenuContribution(subMenuId, subMenuAction);
        menuDef.getContributions().add(menuContribution);
        return menuContribution;
    }

    @Nonnull
    public MenuContribution registerMenuItem(String menuId, String pluginId, MenuItemProvider menuProvider) {
        MenuDefinition menuDef = menus.get(menuId);
        if (menuDef == null) {
            menuDef = new MenuDefinition(null);
            menus.put(menuId, menuDef);
        }

        DirectSubMenuContribution menuContribution = new DirectSubMenuContribution(menuProvider);
        menuDef.getContributions().add(menuContribution);
        return menuContribution;
    }

    @Nonnull
    public MenuContribution registerMenuGroup(String menuId, String pluginId, String groupId) {
        MenuDefinition menuDef = menus.get(menuId);
        if (menuDef == null) {
            menuDef = new MenuDefinition(null);
            menus.put(menuId, menuDef);
        }

        GroupMenuContribution groupContribution = new GroupMenuContribution(groupId);
        menuDef.getContributions().add(groupContribution);
        return groupContribution;
    }

    public void registerMenuRule(MenuContribution contribution, MenuContributionRule rule) {
        MenuDefinition match = null;
        for (MenuDefinition menuDef : menus.values()) {
            if (menuDef.getContributions().contains(contribution)) {
                match = menuDef;
                break;
            }
        }
        if (match == null) {
            throw new IllegalStateException("Invalid menu contribution rule");
        }

        List<MenuContributionRule> rules = match.getRules().get(contribution);
        if (rules == null) {
            rules = new ArrayList<>();
            match.getRules().put(contribution, rules);
        }
        rules.add(rule);
    }

    private static class BuilderRecord {

        MenuOutput menuOutput;
        Map<String, BuilderMenuRecord> subMenusMap = new HashMap<>();
    }

    @ParametersAreNonnullByDefault
    private static class BuilderMenuRecord {

        String subMenuId;
        MenuOutput outputMenu;

        Map<String, BuilderGroupRecord> groupsMap = new HashMap<>();
        Map<String, BuilderContributionRecord> contributionsMap = new HashMap<>();

        boolean separatorQueued = false;
        BuilderContributionRecord previousContribution = null;
        Map<String, List<String>> afterMap = new HashMap<>();
        Set<String> processedContributions = new HashSet<>();

        public BuilderMenuRecord(String subMenuId, MenuOutput outputMenu) {
            this.subMenuId = subMenuId;
            this.outputMenu = outputMenu;
        }
    }

    @ParametersAreNonnullByDefault
    private static class BuilderGroupRecord extends BuilderContributionRecord {

        SectionProcessingState processingState = SectionProcessingState.START;
        PositionMenuContributionRule.PositionMode processingPosition = PositionMenuContributionRule.PositionMode.TOP;
        List<BuilderContributionRecord> contributions = new ArrayList<>();

        public BuilderGroupRecord(String groupId) {
            contributionId = groupId;
        }
    }

    private static class BuilderProcessingRecord {

        BuilderMenuRecord menu;
        BuilderGroupRecord group;
        boolean isSubMenu = false;
    }

    private enum SectionProcessingState {
        START,
        CONTRIBUTION,
        END
    }

    @ParametersAreNonnullByDefault
    private static class BuilderActionContributionRecord extends BuilderContributionRecord {

        final ActionMenuContribution contribution;

        public BuilderActionContributionRecord(ActionMenuContribution contribution) {
            this.contribution = contribution;
            contributionId = (String) contribution.getAction().getValue(ActionConsts.ACTION_ID);
        }

        public boolean shouldCreate(boolean isPopup, String menuId, String subMenuId) {
            if (isPopup) {
                Action action = ((ActionMenuContribution) contribution).getAction();
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    return menuCreation.shouldCreate(menuId, subMenuId);
                }
            }

            return true;
        }

        @Nonnull
        public JMenuItem createItem(boolean isPopup, String menuId, String subMenuId, Map<String, ButtonGroup> buttonGroups) {
            Action action = contribution.getAction();
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            JMenuItem menuItem = actionModule.actionToMenuItem(action, buttonGroups);

            if (isPopup) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(menuItem, menuId, subMenuId);
                }
            }

            return menuItem;
        }

        public void finishItem(JMenuItem menuItem, ActionContextService activationUpdateService) {
            Action menuItemAction = menuItem.getAction();
            if (menuItemAction != null) {
                MenuManager.finishMenuAction(menuItemAction, activationUpdateService);
            }
        }
    }

    @ParametersAreNonnullByDefault
    private static class BuilderDirectSubMenuContributionRecord extends BuilderContributionRecord {

        DirectSubMenuContribution contribution;
        JMenuItem menuItem;

        public BuilderDirectSubMenuContributionRecord(DirectSubMenuContribution contribution) {
            this.contribution = contribution;
        }

        public boolean shouldCreate(boolean isPopup, String menuId, String subMenuId) {
            if (menuItem == null) {
                menuItem = contribution.getMenuItemProvider().createMenuItem();
                Action action = menuItem.getAction();
                if (action != null) {
                    contributionId = (String) action.getValue(ActionConsts.ACTION_ID);
                }
            }

            if (isPopup) {
                Action action = menuItem.getAction();
                if (action != null) {
                    ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                    if (menuCreation != null) {
                        return menuCreation.shouldCreate(menuId, subMenuId);
                    }
                }
            }

            return true;
        }

        @Nonnull
        public JMenuItem createItem(boolean isPopup, String menuId, String subMenuId, Map<String, ButtonGroup> buttonGroups) {
            if (menuItem == null) {
                menuItem = contribution.getMenuItemProvider().createMenuItem();
                Action action = menuItem.getAction();
                if (action != null) {
                    contributionId = (String) action.getValue(ActionConsts.ACTION_ID);
                }
            }

            Action action = menuItem.getAction();
            if (isPopup && action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(menuItem, menuId, subMenuId);
                }
            }

            return menuItem;
        }

        public void finishItem(JMenuItem menuItem, ActionContextService activationUpdateService) {
            MenuManager.finishMenuItem(menuItem, activationUpdateService);
            MenuManager.finishMenuAction(menuItem.getAction(), activationUpdateService);
        }
    }

    @ParametersAreNonnullByDefault
    private static class BuilderMenuContributionRecord extends BuilderContributionRecord {

        SubMenuContribution contribution;

        public BuilderMenuContributionRecord(SubMenuContribution contribution) {
            this.contribution = contribution;
            this.contributionId = contribution.getSubMenuId();
        }

        public boolean shouldCreate(boolean isPopup, String menuId, String subMenuId) {
            if (isPopup) {
                Action action = contribution.getAction();
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    return menuCreation.shouldCreate(menuId, subMenuId);
                }
            }

            return true;
        }

        @Nonnull
        public JMenu createItem(boolean isPopup, String menuId, String subMenuId, Map<String, ButtonGroup> buttonGroups) {
            JMenu subMenu = UiUtils.createMenu();

            Action action = contribution.getAction();
            subMenu.setAction(action);
            if (isPopup) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(subMenu, menuId, subMenuId);
                }
            }

            return subMenu;
        }

        public void finishItem(JMenu menu, ActionContextService activationUpdateService) {
            MenuManager.finishMenuAction(menu.getAction(), activationUpdateService);
        }
    }

    private static class BuilderContributionRecord {

        String contributionId;

        SeparationMenuContributionRule.SeparationMode separationMode;
        PositionMenuContributionRule.PositionMode positionHint = PositionMenuContributionRule.PositionMode.DEFAULT;
        BuilderContributionRecord previousHint = null;
        final Set<String> placeAfter = new HashSet<>();
    }

    @ParametersAreNonnullByDefault
    private static interface MenuOutput {

        void add(JMenu menuItem);

        void add(JMenuItem menuItem);

        void addSeparator();

        boolean isPopup();

        boolean isEmpty();
    }

    private static class BuilderContributionMatch {

        int fallbackMatch = -1;
        int positionMatch = -1;
        int nextMatch = -1;
        int nextHintMatch = -1;

        void clear() {
            fallbackMatch = -1;
            positionMatch = -1;
            nextMatch = -1;
            nextHintMatch = -1;
        }

        boolean hasFoundMatch() {
            return fallbackMatch >= 0 || positionMatch >= 0 || nextMatch >= 0 || nextHintMatch >= 0;
        }

        int bestMatch() {
            if (nextMatch >= 0) {
                return nextMatch;
            }
            if (nextHintMatch >= 0) {
                return nextHintMatch;
            }
            if (positionMatch >= 0) {
                return positionMatch;
            }

            return fallbackMatch;
        }
    }

    @ParametersAreNonnullByDefault
    private static class MenuBarWrapper implements MenuOutput {

        private final JMenuBar menuBar;

        public MenuBarWrapper(JMenuBar menuBar) {
            this.menuBar = menuBar;
        }

        @Override
        public void add(JMenu menuItem) {
            menuBar.add(menuItem);
        }

        @Override
        public void add(JMenuItem menuItem) {
            menuBar.add(menuItem);
        }

        @Override
        public void addSeparator() {
        }

        @Override
        public boolean isPopup() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return menuBar.getMenuCount() == 0;
        }
    }

    @ParametersAreNonnullByDefault
    private static class PopupMenuWrapper implements MenuOutput {

        private final JPopupMenu menu;

        public PopupMenuWrapper(JPopupMenu menu) {
            this.menu = menu;
        }

        @Override
        public void add(JMenu menuItem) {
            menu.add(menuItem);
        }

        @Override
        public void add(JMenuItem menuItem) {
            menu.add(menuItem);
        }

        @Override
        public void addSeparator() {
            menu.addSeparator();
        }

        @Override
        public boolean isPopup() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return menu.getComponentCount() == 0;
        }
    }

    @ParametersAreNonnullByDefault
    private static class MenuWrapper implements MenuOutput {

        private final JMenu menu;
        private final boolean popup;

        public MenuWrapper(JMenu menu, boolean popup) {
            this.menu = menu;
            this.popup = popup;
        }

        public MenuWrapper(JMenu menu) {
            this.menu = menu;
            this.popup = false;
        }

        @Override
        public void add(JMenu menuItem) {
            menu.add(menuItem);
        }

        @Override
        public void add(JMenuItem menuItem) {
            menu.add(menuItem);
        }

        @Override
        public void addSeparator() {
            menu.addSeparator();
        }

        @Override
        public boolean isPopup() {
            return popup;
        }

        @Nonnull
        public JMenu getMenu() {
            return menu;
        }

        @Override
        public boolean isEmpty() {
            return menu.getItemCount() == 0;
        }
    }
}
