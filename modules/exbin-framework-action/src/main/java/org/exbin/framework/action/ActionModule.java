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
package org.exbin.framework.action;

import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionManager;
import org.exbin.framework.utils.ClipboardActionsApi;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ComponentActivationManager;
import org.exbin.framework.action.api.ComponentActivationService;
import org.exbin.framework.action.api.GroupMenuContributionRule;
import org.exbin.framework.action.api.GroupToolBarContributionRule;
import org.exbin.framework.action.api.MenuContribution;
import org.exbin.framework.action.api.MenuContributionRule;
import org.exbin.framework.action.api.MenuManagement;
import org.exbin.framework.action.api.PositionMenuContributionRule;
import org.exbin.framework.action.api.PositionToolBarContributionRule;
import org.exbin.framework.action.api.SeparationMenuContributionRule;
import org.exbin.framework.action.api.ToolBarContribution;
import org.exbin.framework.action.api.ToolBarContributionRule;
import org.exbin.framework.action.api.ToolBarManagement;
import org.exbin.framework.action.popup.api.ComponentPopupEventDispatcher;
import org.exbin.framework.action.popup.api.ActionPopupModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.ClipboardUtils;
import org.exbin.framework.utils.UiUtils;

/**
 * Implementation of action module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionModule implements ActionModuleApi {

    private ClipboardActions clipboardActions = null;
    private ClipboardTextActions clipboardTextActions = null;
    private MenuManager menuManager = null;
    private ToolBarManager toolBarManager = null;
    private ResourceBundle resourceBundle;

    public ActionModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ActionModule.class);
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
    public ClipboardActions getClipboardActions() {
        if (clipboardActions == null) {
            clipboardActions = new ClipboardActions();
            ensureSetup();
            clipboardActions.setup(resourceBundle);
        }

        return clipboardActions;
    }

    @Nonnull
    @Override
    public ClipboardTextActions getClipboardTextActions() {
        if (clipboardTextActions == null) {
            clipboardTextActions = new ClipboardTextActions();
            ensureSetup();
            clipboardTextActions.setup(resourceBundle);
        }

        return clipboardTextActions;
    }

    @Nonnull
    @Override
    public ActionManager createActionManager() {
        return new DefaultActionManager();
    }

    @Override
    public void addComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        ActionPopupModuleApi popupModule = App.getModule(ActionPopupModuleApi.class);
        popupModule.addComponentPopupEventDispatcher(dispatcher);
    }

    @Override
    public void removeComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        ActionPopupModuleApi popupModule = App.getModule(ActionPopupModuleApi.class);
        popupModule.removeComponentPopupEventDispatcher(dispatcher);
    }

    @Override
    public void fillDefaultEditPopupMenu(JPopupMenu popupMenu, int position) {
        ActionPopupModuleApi popupModule = App.getModule(ActionPopupModuleApi.class);
        popupModule.fillDefaultEditPopupMenu(popupMenu, position);
    }

    @Override
    public void initAction(Action action, ResourceBundle bundle, String actionId) {
        initAction(action, bundle, action.getClass(), actionId);
    }

    @Override
    public void initAction(Action action, ResourceBundle bundle, Class<?> resourceClass, String actionId) {
        action.putValue(Action.NAME, bundle.getString(actionId + ActionConsts.ACTION_NAME_POSTFIX));
        action.putValue(ActionConsts.ACTION_ID, actionId);

        // TODO keystroke from string with meta mask translation
        if (bundle.containsKey(actionId + ActionConsts.ACTION_SHORT_DESCRIPTION_POSTFIX)) {
            action.putValue(Action.SHORT_DESCRIPTION, bundle.getString(actionId + ActionConsts.ACTION_SHORT_DESCRIPTION_POSTFIX));
        }
        if (bundle.containsKey(actionId + ActionConsts.ACTION_SMALL_ICON_POSTFIX)) {
            action.putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(resourceClass.getResource(bundle.getString(actionId + ActionConsts.ACTION_SMALL_ICON_POSTFIX))));
        }
        if (bundle.containsKey(actionId + ActionConsts.ACTION_SMALL_LARGE_POSTFIX)) {
            action.putValue(Action.LARGE_ICON_KEY, new javax.swing.ImageIcon(resourceClass.getResource(bundle.getString(actionId + ActionConsts.ACTION_SMALL_LARGE_POSTFIX))));
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
    @Override
    public List<Action> getToolBarManagedActions() {
        List<Action> actions = new ArrayList<>();
        getToolBarManager();
        actions.addAll(toolBarManager.getAllManagedActions());

        return actions;
    }

    @Nonnull
    private MenuManager getMenuManager() {
        if (menuManager == null) {
            menuManager = new MenuManager();
        }

        return menuManager;
    }

    @Nonnull
    private ToolBarManager getToolBarManager() {
        if (toolBarManager == null) {
            toolBarManager = new ToolBarManager();
        }

        return toolBarManager;
    }

    @Nonnull
    @Override
    public MenuManagement getMenuManagement(String moduleId) {
        return new MenuManagement() {
            @Override
            public void buildMenu(JPopupMenu targetMenu, String menuId, ComponentActivationService activationUpdateService) {
                getMenuManager().buildMenu(targetMenu, menuId, activationUpdateService);
            }

            @Override
            public void buildMenu(JMenuBar targetMenuBar, String menuId, ComponentActivationService activationUpdateService) {
                getMenuManager().buildMenu(targetMenuBar, menuId, activationUpdateService);
            }

            @Override
            public void registerMenu(String menuId) {
                getMenuManager().registerMenu(menuId, moduleId);
            }

            @Override
            public void unregisterMenu(String menuId) {
                getMenuManager().unregisterMenu(menuId);
            }

            @Nonnull
            @Override
            public MenuContribution registerMenuItem(String menuId, JMenu item) {
                return getMenuManager().registerMenuItem(menuId, moduleId, item);
            }

            @Nonnull
            @Override
            public MenuContribution registerMenuItem(String menuId, JMenuItem item) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Nonnull
            @Override
            public MenuContribution registerMenuItem(String menuId, Action action) {
                return getMenuManager().registerMenuItem(menuId, moduleId, action);
            }

            @Nonnull
            @Override
            public MenuContribution registerMenuItem(String menuId, String subMenuId, Action subMenuAction) {
                return getMenuManager().registerMenuItem(menuId, moduleId, subMenuId, subMenuAction);
            }

            @Nonnull
            @Override
            public MenuContribution registerMenuItem(String menuId, String subMenuId, String subMenuName) {
                return getMenuManager().registerMenuItem(menuId, moduleId, subMenuId, subMenuName);
            }

            @Nonnull
            @Override
            public MenuContribution registerMenuGroup(String menuId, String groupId) {
                return getMenuManager().registerMenuGroup(menuId, moduleId, groupId);
            }

            @Override
            public boolean menuGroupExists(String menuId, String groupId) {
                return menuManager.menuGroupExists(menuId, groupId);
            }

            @Override
            public void registerMenuRule(MenuContribution menuContribution, MenuContributionRule rule) {
                getMenuManager().registerMenuRule(menuContribution, rule);
            }
        };
    }

    @Nonnull
    @Override
    public ToolBarManagement getToolBarManagement(String moduleId) {
        return new ToolBarManagement() {
            @Override
            public void buildToolBar(JToolBar targetToolBar, String toolBarId, ComponentActivationService activationUpdateService) {
                getToolBarManager().buildToolBar(targetToolBar, toolBarId, activationUpdateService);
            }

            @Override
            public void registerToolBar(String toolBarId) {
                getToolBarManager().registerToolBar(toolBarId, moduleId);
            }

            @Nonnull
            @Override
            public ToolBarContribution registerToolBarItem(String toolBarId, Action action) {
                return getToolBarManager().registerToolBarItem(toolBarId, moduleId, action);
            }

            @Nonnull
            @Override
            public ToolBarContribution registerToolBarGroup(String toolBarId, String groupId) {
                return getToolBarManager().registerToolBarGroup(toolBarId, moduleId, groupId);
            }

            @Override
            public void registerToolBarRule(ToolBarContribution toolBarContribution, ToolBarContributionRule rule) {
                getToolBarManager().registerToolBarRule(toolBarContribution, rule);
            }
        };
    }

    @Override
    public void registerClipboardMenuItems(String menuId, String moduleId, SeparationMode separationMode) {
        registerClipboardMenuItems(getClipboardActions(), menuId, moduleId, separationMode);
    }

    @Override
    public void registerClipboardMenuItems(ClipboardActionsApi actions, String menuId, String moduleId, SeparationMode separationMode) {
        MenuManagement mgmt = getMenuManagement(moduleId);
        MenuContribution contribution = mgmt.registerMenuGroup(menuId, CLIPBOARD_ACTIONS_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.TOP));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(separationMode));
        contribution = mgmt.registerMenuItem(menuId, actions.createCutAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(menuId, actions.createCopyAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(menuId, actions.createPasteAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(menuId, actions.createDeleteAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(menuId, actions.createSelectAllAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
    }

    @Override
    public void registerClipboardTextActions() {
        getClipboardTextActions();
        ActionPopupModuleApi popupModule = App.getModule(ActionPopupModuleApi.class);
        popupModule.registerDefaultClipboardPopupMenu(resourceBundle, ActionModule.class);
    }

    @Override
    public void registerMenuClipboardActions() {
        registerClipboardMenuItems(ActionConsts.EDIT_MENU_ID, MODULE_ID, SeparationMode.NONE);
    }

    @Override
    public void registerToolBarClipboardActions() {
        getClipboardActions();
        ToolBarManagement mgmt = getToolBarManagement(MODULE_ID);
        ToolBarContribution contribution = mgmt.registerToolBarGroup(ActionConsts.MAIN_TOOL_BAR_ID, CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionToolBarContributionRule(PositionMode.TOP));
        contribution = mgmt.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, clipboardActions.createCutAction());
        mgmt.registerToolBarRule(contribution, new GroupToolBarContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, clipboardActions.createCopyAction());
        mgmt.registerToolBarRule(contribution, new GroupToolBarContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, clipboardActions.createPasteAction());
        mgmt.registerToolBarRule(contribution, new GroupToolBarContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, clipboardActions.createDeleteAction());
        mgmt.registerToolBarRule(contribution, new GroupToolBarContributionRule(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
    }

    @Override
    public void registerClipboardHandler(ClipboardActionsHandler clipboardHandler) {
//        getClipboardActions().setClipboardActionsHandler(clipboardHandler);
    }

    /*
    @Nonnull
    @Override
    public ClipboardActionsApi createClipboardActions(ClipboardActionsHandler clipboardActionsHandler) {
        ClipboardActions customClipboardActions = new ClipboardActions();
        customClipboardActions.setup(resourceBundle);
        customClipboardActions.setClipboardActionsHandler(clipboardActionsHandler);
        return customClipboardActions;
    }
     */
    public void registerClipboardFlavorListener(ComponentActivationManager activationManager) {
        ClipboardUtils.getClipboard().addFlavorListener(new FlavorListener() {

            private final ClipboardFlavorState clipboardFlavorState = new ClipboardFlavorState();

            @Override
            public void flavorsChanged(FlavorEvent fe) {
                activationManager.updateActionsForComponent(ClipboardFlavorState.class, clipboardFlavorState);
            }
        });
    }
}
