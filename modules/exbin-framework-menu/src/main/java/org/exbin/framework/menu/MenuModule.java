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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.clipboard.ClipboardActionsApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.action.api.ActionContextService;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SeparationSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.menu.api.MenuManager;
import org.exbin.framework.menu.api.MenuModuleApi;

/**
 * Implementation of menu module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MenuModule implements MenuModuleApi {

    private DefaultMenuManager menuManager = null;
    private ResourceBundle resourceBundle;

    public MenuModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(MenuModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    @Override
    public JMenuItem actionToMenuItem(Action action) {
        return actionToMenuItem(action, null);
    }

    @Nonnull
    @Override
    public JMenuItem actionToMenuItem(Action action, @Nullable Map<String, ButtonGroup> buttonGroups) {
        return actionToMenuItemInt(action, buttonGroups);
    }

    @Nonnull
    private JMenuItem actionToMenuItemInt(Action action, @Nullable Map<String, ButtonGroup> buttonGroups) {
        JMenuItem menuItem;
        ActionType actionType = (ActionType) action.getValue(ActionConsts.ACTION_TYPE);
        if (actionType != null) {
            switch (actionType) {
                case CHECK: {
                    menuItem = UiUtils.createCheckBoxMenuItem();
                    menuItem.setAction(action);
                    break;
                }
                case RADIO: {
                    menuItem = UiUtils.createRadioButtonMenuItem();
                    menuItem.setAction(action);
                    String radioGroup = (String) action.getValue(ActionConsts.ACTION_RADIO_GROUP);
                    if (buttonGroups != null) {
                        ButtonGroup buttonGroup = buttonGroups.get(radioGroup);
                        if (buttonGroup == null) {
                            buttonGroup = new ButtonGroup();
                            buttonGroups.put(radioGroup, buttonGroup);
                        }
                        buttonGroup.add(menuItem);
                    }
                    break;
                }
                default: {
                    menuItem = UiUtils.createMenuItem();
                    menuItem.setAction(action);
                }
            }
        } else {
            menuItem = UiUtils.createMenuItem();
            menuItem.setAction(action);
        }

        Object dialogMode = action.getValue(ActionConsts.ACTION_DIALOG_MODE);
        if (dialogMode instanceof Boolean && ((Boolean) dialogMode)) {
            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            menuItem.setText(languageModule.getActionWithDialogText(menuItem.getText()));
        }
        return menuItem;
    }

    @Nonnull
    @Override
    public List<Action> getMenuManagedActions() {
        List<Action> actions = new ArrayList<>();
        getMenuManager();
        actions.addAll(menuManager.getAllManagedActions());

        return actions;
    }

    @Nonnull
    private DefaultMenuManager getMenuManager() {
        if (menuManager == null) {
            menuManager = new DefaultMenuManager();
        }

        return menuManager;
    }

    @Nonnull
    @Override
    public MenuManager createMenuManager() {
        return new DefaultMenuManager();
    }

    @Nonnull
    @Override
    public MenuManagement getMenuManagement(String menuId, String moduleId) {
        return new DefaultMenuManagement(getMenuManager(), menuId, moduleId);
    }

    @Override
    public void registerMenu(String menuId, String moduleId) {
        getMenuManager().registerMenu(menuId, moduleId);
    }

    @Override
    public void unregisterMenu(String menuId) {
        getMenuManager().unregisterMenu(menuId);
    }

    @Nonnull
    @Override
    public MenuManagement getMainMenuManagement(String moduleId) {
        return getMenuManagement(MAIN_MENU_ID, moduleId);
    }

    @Override
    public void buildMenu(JPopupMenu targetMenu, String menuId, ActionContextService activationUpdateService) {
        getMenuManager().buildMenu(targetMenu, menuId, activationUpdateService);
    }

    @Override
    public void buildMenu(JMenuBar targetMenuBar, String menuId, ActionContextService activationUpdateService) {
        getMenuManager().buildMenu(targetMenuBar, menuId, activationUpdateService);
    }

    @Override
    public void registerClipboardMenuItems(String menuId, @Nullable String subMenuId, String moduleId, SeparationSequenceContributionRule.SeparationMode separationMode) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        registerClipboardMenuItems(actionModule.getClipboardActions(), menuId, subMenuId, moduleId, separationMode);
    }

    @Override
    public void registerClipboardMenuItems(ClipboardActionsApi actions, String menuId, @Nullable String subMenuId, String moduleId, SeparationSequenceContributionRule.SeparationMode separationMode) {
        MenuManagement mgmt = getMenuManagement(menuId, moduleId);
        if (subMenuId != null) {
            mgmt = mgmt.getSubMenu(subMenuId);
        }
        SequenceContribution contribution = mgmt.registerMenuGroup(CLIPBOARD_ACTIONS_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(separationMode));
        contribution = mgmt.registerMenuItem(actions.createCutAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(actions.createCopyAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(actions.createPasteAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(actions.createDeleteAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(actions.createSelectAllAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
    }

    @Override
    public void registerMenuClipboardActions() {
        registerClipboardMenuItems(MenuModuleApi.MAIN_MENU_ID, EDIT_SUBMENU_ID, MODULE_ID, SeparationSequenceContributionRule.SeparationMode.NONE);
    }
}
