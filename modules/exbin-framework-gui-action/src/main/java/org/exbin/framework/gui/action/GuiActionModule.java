/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.gui.action;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ClipboardActions;
import org.exbin.framework.gui.utils.ClipboardActionsApi;
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ComponentPopupEventDispatcher;
import org.exbin.framework.gui.action.api.MenuGroup;
import org.exbin.framework.gui.action.api.MenuPosition;
import org.exbin.framework.gui.action.api.PositionMode;
import org.exbin.framework.gui.action.api.SeparationMode;
import org.exbin.framework.gui.action.api.ToolBarGroup;
import org.exbin.framework.gui.action.api.ToolBarPosition;
import org.exbin.framework.gui.utils.ClipboardUtils;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.gui.action.api.GuiActionModuleApi;

/**
 * Implementation of framework menu module.
 *
 * @version 0.2.1 2019/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiActionModule implements GuiActionModuleApi {

    private XBApplication application;
    private ClipboardActionsImpl clipboardActions = null;
    private MenuHandler menuHandler = null;
    private ToolBarHandler toolBarHandler = null;

    public GuiActionModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    @Override
    public ClipboardActionsImpl getClipboardActions() {
        if (clipboardActions == null) {
            clipboardActions = new ClipboardActionsImpl();
            clipboardActions.init();
        }

        return clipboardActions;
    }

    @Override
    public void addComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        ClipboardUtils.addComponentPopupEventDispatcher(dispatcher);
    }

    @Override
    public void removeComponentPopupEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        ClipboardUtils.removeComponentPopupEventDispatcher(dispatcher);
    }

    @Override
    public void fillPopupMenu(JPopupMenu popupMenu, int position) {
        ClipboardUtils.fillDefaultEditPopupMenu(popupMenu, position);
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
    public void registerMenuItem(String menuId, String pluginId, String subMenuId, String subMenuName, MenuPosition position) {
        getMenuHandler().registerMenuItem(menuId, pluginId, subMenuId, subMenuName, position);
    }

    @Override
    public void registerClipboardMenuItems(String menuId, String moduleId, SeparationMode separationMode) {
        getClipboardActions();
        registerClipboardMenuItems(clipboardActions, menuId, moduleId, separationMode);
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
        registerClipboardMenuItems(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, SeparationMode.NONE);
    }

    @Override
    public void registerToolBarClipboardActions() {
        getClipboardActions();
        registerToolBarGroup(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.TOP)));
        registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, clipboardActions.getCutAction(), new ToolBarPosition(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, clipboardActions.getCopyAction(), new ToolBarPosition(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, clipboardActions.getPasteAction(), new ToolBarPosition(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
        registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, clipboardActions.getDeleteAction(), new ToolBarPosition(CLIPBOARD_ACTIONS_TOOL_BAR_GROUP_ID));
    }

    @Override
    public void registerClipboardHandler(ClipboardActionsHandler clipboardHandler) {
        getClipboardActions().setClipboardHandler(clipboardHandler);
    }

    @Override
    public boolean menuGroupExists(String menuId, String groupId) {
        return menuHandler.menuGroupExists(menuId, groupId);
    }

    @Nonnull
    @Override
    public ClipboardActions createClipboardActions(ClipboardActionsHandler clipboardActionsHandler) {
        return new BasicClipboardActions(clipboardActionsHandler);
    }

    @Override
    public void unregisterMenu(String menuId) {
        getMenuHandler().unregisterMenu(menuId);
    }
}
