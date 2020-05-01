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
package org.exbin.framework.gui.file;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JMenu;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.ApplicationFrameHandler;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.menu.api.MenuGroup;
import org.exbin.framework.gui.menu.api.MenuPosition;
import org.exbin.framework.gui.menu.api.NextToMode;
import org.exbin.framework.gui.menu.api.PositionMode;
import org.exbin.framework.gui.menu.api.ToolBarGroup;
import org.exbin.framework.gui.menu.api.ToolBarPosition;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Implementation of framework file module.
 *
 * @version 0.2.1 2019/07/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiFileModule implements GuiFileModuleApi {

    private static final String FILE_MENU_GROUP_ID = MODULE_ID + ".fileMenuGroup";
    private static final String FILE_TOOL_BAR_GROUP_ID = MODULE_ID + ".fileToolBarGroup";

    private FileHandlingActions fileHandlingActions = null;
    private XBApplication application;

    public GuiFileModule() {
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
    public FileHandlingActions getFileHandlingActions() {
        if (fileHandlingActions == null) {
            fileHandlingActions = new FileHandlingActions();
            fileHandlingActions.init(application);
            fileHandlingActions.setPreferences(application.getAppPreferences());
        }

        return fileHandlingActions;
    }

    @Override
    public void addFileType(FileType fileType) {
        FileHandlingActions handle = getFileHandlingActions();
        handle.addFileType(fileType);
    }

    @Override
    public void registerMenuFileHandlingActions() {
        getFileHandlingActions();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuGroup(GuiFrameModuleApi.FILE_MENU_ID, new MenuGroup(FILE_MENU_GROUP_ID, new MenuPosition(PositionMode.TOP)));
        menuModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, fileHandlingActions.getNewFileAction(), new MenuPosition(FILE_MENU_GROUP_ID));
        menuModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, fileHandlingActions.getOpenFileAction(), new MenuPosition(FILE_MENU_GROUP_ID));
        menuModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, fileHandlingActions.getSaveFileAction(), new MenuPosition(FILE_MENU_GROUP_ID));
        menuModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, fileHandlingActions.getSaveAsFileAction(), new MenuPosition(FILE_MENU_GROUP_ID));
    }

    @Override
    public void registerToolBarFileHandlingActions() {
        getFileHandlingActions();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerToolBarGroup(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(FILE_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.TOP)));
        menuModule.registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, fileHandlingActions.getNewFileAction(), new ToolBarPosition(FILE_TOOL_BAR_GROUP_ID));
        menuModule.registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, fileHandlingActions.getOpenFileAction(), new ToolBarPosition(FILE_TOOL_BAR_GROUP_ID));
        menuModule.registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, fileHandlingActions.getSaveFileAction(), new ToolBarPosition(FILE_TOOL_BAR_GROUP_ID));
    }

    @Override
    public void registerCloseListener() {
        getFileHandlingActions();
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        frameModule.addExitListener((ApplicationFrameHandler frameHandler) -> fileHandlingActions.releaseFile());
    }

    @Override
    public void registerLastOpenedMenuActions() {
        getFileHandlingActions();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        JMenu recentFileMenu = fileHandlingActions.getOpenRecentMenu();
        menuModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, recentFileMenu, new MenuPosition(NextToMode.AFTER, "Open..."));
    }

    @Override
    public void loadFromFile(String filename) {
        try {
            getFileHandlingActions().loadFromFile(filename);
        } catch (URISyntaxException ex) {
            Logger.getLogger(GuiFileModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
