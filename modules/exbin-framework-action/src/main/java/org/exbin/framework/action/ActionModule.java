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

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.exbin.framework.App;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.ClipboardActionsApi;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.action.api.ToolBarGroup;
import org.exbin.framework.action.api.ToolBarPosition;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.popup.api.ComponentPopupEventDispatcher;
import org.exbin.framework.popup.api.PopupModuleApi;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.ClipboardActionsUpdater;

/**
 * Implementation of framework menu module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionModule implements ActionModuleApi {

    private ClipboardActions clipboardActions = null;
    private ClipboardTextActions clipboardTextActions = null;
    private MenuHandler menuHandler = null;
    private ToolBarHandler toolBarHandler = null;
    private ResourceBundle resourceBundle;

    public ActionModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(ActionModule.class);
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

    @Override
    public void addComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        PopupModuleApi popupModule = App.getModule(PopupModuleApi.class);
        popupModule.addComponentPopupEventDispatcher(dispatcher);
    }

    @Override
    public void removeComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        PopupModuleApi popupModule = App.getModule(PopupModuleApi.class);
        popupModule.removeComponentPopupEventDispatcher(dispatcher);
    }

    @Override
    public void fillPopupMenu(JPopupMenu popupMenu, int position) {
        PopupModuleApi popupModule = App.getModule(PopupModuleApi.class);
        popupModule.fillDefaultEditPopupMenu(popupMenu, position);
    }

    @Nonnull
    private MenuHandler getMenuHandler() {
        if (menuHandler == null) {
            menuHandler = new MenuHandler();
        }

        return menuHandler;
    }

    @Nonnull
    private ToolBarHandler getToolBarHandler() {
        if (toolBarHandler == null) {
            toolBarHandler = new ToolBarHandler();
        }

        return toolBarHandler;
    }

    @Override
    public void buildMenu(JPopupMenu targetMenu, String menuId) {
        getMenuHandler().buildMenu(targetMenu, menuId);
    }

    @Override
    public void buildMenu(JMenuBar targetMenuBar, String menuId) {
        getMenuHandler().buildMenu(targetMenuBar, menuId);
    }

    @Override
    public void registerMenu(String menuId, String pluginId) {
        getMenuHandler().registerMenu(menuId, pluginId);
    }

    @Override
    public void registerMenuGroup(String menuId, MenuGroup menuGroup) {
        getMenuHandler().registerMenuGroup(menuId, menuGroup);
    }

    @Override
    public void registerMenuItem(String menuId, String pluginId, JMenu menu, MenuPosition position) {
        getMenuHandler().registerMenuItem(menuId, pluginId, menu, position);
    }

    @Override
    public void registerMenuItem(String menuId, String pluginId, JMenuItem item, MenuPosition position) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerMenuItem(String menuId, String pluginId, Action action, MenuPosition position) {
        getMenuHandler().registerMenuItem(menuId, pluginId, action, position);
    }

    @Override
    public void registerMenuItem(String menuId, String pluginId, String subMenuId, Action subMenuAction, MenuPosition position) {
        getMenuHandler().registerMenuItem(menuId, pluginId, subMenuId, subMenuAction, position);
    }

    @Override
    public void registerMenuItem(String menuId, String pluginId, String subMenuId, String subMenuName, MenuPosition position) {
        getMenuHandler().registerMenuItem(menuId, pluginId, subMenuId, subMenuName, position);
    }

    @Override
    public void registerClipboardMenuItems(String menuId, String moduleId, SeparationMode separationMode) {
        registerClipboardMenuItems(getClipboardActions(), menuId, moduleId, separationMode);
    }

    @Override
    public void registerClipboardMenuItems(ClipboardActionsApi actions, String menuId, String moduleId, SeparationMode separationMode) {
        registerMenuGroup(menuId, new MenuGroup(CLIPBOARD_ACTIONS_MENU_GROUP_ID, new MenuPosition(PositionMode.TOP), separationMode));
        registerMenuItem(menuId, moduleId, actions.getCutAction(), new MenuPosition(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        registerMenuItem(menuId, moduleId, actions.getCopyAction(), new MenuPosition(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        registerMenuItem(menuId, moduleId, actions.getPasteAction(), new MenuPosition(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        registerMenuItem(menuId, moduleId, actions.getDeleteAction(), new MenuPosition(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        registerMenuItem(menuId, moduleId, actions.getSelectAllAction(), new MenuPosition(CLIPBOARD_ACTIONS_MENU_GROUP_ID));
    }

    @Override
    public void registerClipboardTextActions() {
        getClipboardTextActions();
        PopupModuleApi popupModule = App.getModule(PopupModuleApi.class);
        popupModule.registerDefaultClipboardPopupMenu(resourceBundle, ActionModule.class);
    }

    @Override
    public void buildToolBar(JToolBar targetToolBar, String toolBarId) {
        getToolBarHandler().buildToolBar(targetToolBar, toolBarId);
    }

    @Override
    public void registerToolBar(String toolBarId, String pluginId) {
        getToolBarHandler().registerToolBar(toolBarId, pluginId);
    }

    @Override
    public void registerToolBarGroup(String toolBarId, ToolBarGroup toolBarGroup) {
        getToolBarHandler().registerToolBarGroup(toolBarId, toolBarGroup);
    }

    @Override
    public void registerToolBarItem(String toolBarId, String pluginId, Action action, ToolBarPosition position) {
        getToolBarHandler().registerToolBarItem(toolBarId, pluginId, action, position);
    }

    @Override
    public void registerMenuClipboardActions() {
        registerClipboardMenuItems(FrameModuleApi.EDIT_MENU_ID, MODULE_ID, SeparationMode.NONE);
    }

    @Override
    public void registerToolBarClipboardActions() {
        getClipboardActions();
        registerToolBarGroup(FrameModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.TOP)));
        registerToolBarItem(FrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, clipboardActions.getCutAction(), new ToolBarPosition(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        registerToolBarItem(FrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, clipboardActions.getCopyAction(), new ToolBarPosition(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        registerToolBarItem(FrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, clipboardActions.getPasteAction(), new ToolBarPosition(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        registerToolBarItem(FrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, clipboardActions.getDeleteAction(), new ToolBarPosition(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
    }

    @Override
    public void registerClipboardHandler(ClipboardActionsHandler clipboardHandler) {
        getClipboardActions().setClipboardActionsHandler(clipboardHandler);
    }

    @Override
    public boolean menuGroupExists(String menuId, String groupId) {
        return menuHandler.menuGroupExists(menuId, groupId);
    }

    @Nonnull
    @Override
    public ClipboardActionsUpdater createClipboardActions(ClipboardActionsHandler clipboardActionsHandler) {
        ClipboardActions customClipboardActions = new ClipboardActions();
        customClipboardActions.setup(resourceBundle);
        customClipboardActions.setClipboardActionsHandler(clipboardActionsHandler);
        return customClipboardActions;
    }

    @Override
    public void unregisterMenu(String menuId) {
        getMenuHandler().unregisterMenu(menuId);
    }

    public void updateClipboardActionsState() {
        clipboardTextActions.updateClipboardActionsState();
    }
}
