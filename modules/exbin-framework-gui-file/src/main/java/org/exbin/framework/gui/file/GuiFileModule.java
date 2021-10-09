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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JMenu;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.ApplicationFrameHandler;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.action.api.MenuGroup;
import org.exbin.framework.gui.action.api.MenuPosition;
import org.exbin.framework.gui.action.api.NextToMode;
import org.exbin.framework.gui.action.api.PositionMode;
import org.exbin.framework.gui.action.api.ToolBarGroup;
import org.exbin.framework.gui.action.api.ToolBarPosition;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.gui.action.api.GuiActionModuleApi;
import org.exbin.framework.gui.file.action.FileActions;
import org.exbin.framework.gui.file.action.NewFileAction;
import org.exbin.framework.gui.file.action.OpenFileAction;
import org.exbin.framework.gui.file.action.RecentFilesActions;
import org.exbin.framework.gui.file.action.SaveAsFileAction;
import org.exbin.framework.gui.file.action.SaveFileAction;
import org.exbin.framework.gui.file.api.FileOperations;
import org.exbin.framework.gui.file.api.FileOperationsProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Implementation of framework file module.
 *
 * @version 0.2.2 2021/10/08
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiFileModule implements GuiFileModuleApi, FileOperationsProvider {

    private static final String FILE_MENU_GROUP_ID = MODULE_ID + ".fileMenuGroup";
    private static final String FILE_TOOL_BAR_GROUP_ID = MODULE_ID + ".fileToolBarGroup";

    private java.util.ResourceBundle resourceBundle = null;
    private XBApplication application;
    private FileOperations fileOperations;

    private NewFileAction newFileAction;
    private OpenFileAction openFileAction;
    private SaveFileAction saveFileAction;
    private SaveAsFileAction saveAsFileAction;
    private RecentFilesActions recentFilesActions;
    private FileActions fileActions;
    private final List<FileType> registeredFileTypes = new ArrayList<>();

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
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(GuiFileModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nullable
    @Override
    public FileOperations getFileOperations() {
        return fileOperations;
    }

    @Override
    public void setFileOperations(@Nullable FileOperations fileOperations) {
        this.fileOperations = fileOperations;
    }

    @Override
    public void addFileType(FileType fileType) {
        registeredFileTypes.add(fileType);
    }

    @Override
    public void registerMenuFileHandlingActions() {
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuGroup(GuiFrameModuleApi.FILE_MENU_ID, new MenuGroup(FILE_MENU_GROUP_ID, new MenuPosition(PositionMode.TOP)));
        actionModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, getNewFileAction(), new MenuPosition(FILE_MENU_GROUP_ID));
        actionModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, getOpenFileAction(), new MenuPosition(FILE_MENU_GROUP_ID));
        actionModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, getSaveFileAction(), new MenuPosition(FILE_MENU_GROUP_ID));
        actionModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, getSaveAsFileAction(), new MenuPosition(FILE_MENU_GROUP_ID));
    }

    @Override
    public void registerToolBarFileHandlingActions() {
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerToolBarGroup(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(FILE_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.TOP)));
        actionModule.registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, getNewFileAction(), new ToolBarPosition(FILE_TOOL_BAR_GROUP_ID));
        actionModule.registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, getOpenFileAction(), new ToolBarPosition(FILE_TOOL_BAR_GROUP_ID));
        actionModule.registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, getSaveFileAction(), new ToolBarPosition(FILE_TOOL_BAR_GROUP_ID));
    }

    @Override
    public void registerCloseListener() {
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        frameModule.addExitListener((ApplicationFrameHandler frameHandler) -> {
            if (fileOperations != null) {
                return fileOperations.releaseAllFiles();
            }
            
            return true;
        });
    }

    @Override
    public void registerRecenFilesMenuActions() {
        getRecentFilesActions();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        JMenu recentFileMenu = recentFilesActions.getOpenRecentMenu();
        actionModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, recentFileMenu, new MenuPosition(NextToMode.AFTER, "Open" + ActionUtils.DIALOG_MENUITEM_EXT));
    }

    @Nonnull
    @Override
    public NewFileAction getNewFileAction() {
        if (newFileAction == null) {
            ensureSetup();
            newFileAction = new NewFileAction();
            newFileAction.setup(application, resourceBundle, this);
        }
        return newFileAction;
    }

    @Nonnull
    @Override
    public OpenFileAction getOpenFileAction() {
        if (openFileAction == null) {
            ensureSetup();
            openFileAction = new OpenFileAction();
            openFileAction.setup(application, resourceBundle, this);
        }
        return openFileAction;
    }

    @Nonnull
    @Override
    public SaveFileAction getSaveFileAction() {
        if (saveFileAction == null) {
            ensureSetup();
            saveFileAction = new SaveFileAction();
            saveFileAction.setup(application, resourceBundle, this);
        }
        return saveFileAction;
    }

    @Nonnull
    @Override
    public SaveAsFileAction getSaveAsFileAction() {
        if (saveAsFileAction == null) {
            ensureSetup();
            saveAsFileAction = new SaveAsFileAction();
            saveAsFileAction.setup(application, resourceBundle, this);
        }
        return saveAsFileAction;
    }

    @Nonnull
    public RecentFilesActions getRecentFilesActions() {
        if (recentFilesActions == null) {
            recentFilesActions = new RecentFilesActions();
            recentFilesActions.setup(application, resourceBundle, new RecentFilesActions.FilesControl() {
                @Override
                public boolean releaseFile() {
                    return fileOperations.releaseAllFiles();
                }

                @Override
                public void loadFromFile(URI fileUri, @Nullable FileType fileType) {
                    fileOperations.loadFromFile(fileUri, fileType);
                }
                
                @Nonnull
                @Override
                public List<FileType> getRegisteredFileTypes() {
                    return registeredFileTypes;
                }
            });
            recentFilesActions.setPreferences(application.getAppPreferences());
        }
        return recentFilesActions;
    }
    
    @Nonnull
    public FileActions getFileActions() {
        if (fileActions == null) {
            ensureSetup();
            fileActions = new FileActions();
            fileActions.setup(application, resourceBundle, this);
        }
        
        return fileActions;
    }

    @Override
    public void loadFromFile(String filename) {
        if (fileOperations == null) {
            return;
        }
        try {
            fileOperations.loadFromFile(filename);
        } catch (URISyntaxException ex) {
            Logger.getLogger(GuiFileModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
