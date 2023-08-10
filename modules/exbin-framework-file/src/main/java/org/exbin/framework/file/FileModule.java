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
package org.exbin.framework.file;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JMenu;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.NextToMode;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.ToolBarGroup;
import org.exbin.framework.action.api.ToolBarPosition;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.file.action.FileActions;
import org.exbin.framework.file.action.NewFileAction;
import org.exbin.framework.file.action.OpenFileAction;
import org.exbin.framework.file.action.RecentFilesActions;
import org.exbin.framework.file.action.SaveAsFileAction;
import org.exbin.framework.file.action.SaveFileAction;
import org.exbin.framework.file.api.FileOperations;
import org.exbin.framework.file.api.FileOperationsProvider;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.LanguageUtils;

/**
 * Implementation of framework file module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FileModule implements FileModuleApi, FileOperationsProvider {

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

    public FileModule() {
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
            resourceBundle = LanguageUtils.getResourceBundleByClass(FileModule.class);
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

    @Nonnull
    @Override
    public Collection<FileType> getFileTypes() {
        return Collections.unmodifiableCollection(registeredFileTypes);
    }

    @Override
    public void registerMenuFileHandlingActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuGroup(FrameModuleApi.FILE_MENU_ID, new MenuGroup(FILE_MENU_GROUP_ID, new MenuPosition(PositionMode.TOP)));
        actionModule.registerMenuItem(FrameModuleApi.FILE_MENU_ID, MODULE_ID, getNewFileAction(), new MenuPosition(FILE_MENU_GROUP_ID));
        actionModule.registerMenuItem(FrameModuleApi.FILE_MENU_ID, MODULE_ID, getOpenFileAction(), new MenuPosition(FILE_MENU_GROUP_ID));
        actionModule.registerMenuItem(FrameModuleApi.FILE_MENU_ID, MODULE_ID, getSaveFileAction(), new MenuPosition(FILE_MENU_GROUP_ID));
        actionModule.registerMenuItem(FrameModuleApi.FILE_MENU_ID, MODULE_ID, getSaveAsFileAction(), new MenuPosition(FILE_MENU_GROUP_ID));
    }

    @Override
    public void registerToolBarFileHandlingActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerToolBarGroup(FrameModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(FILE_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.TOP)));
        actionModule.registerToolBarItem(FrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, getNewFileAction(), new ToolBarPosition(FILE_TOOL_BAR_GROUP_ID));
        actionModule.registerToolBarItem(FrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, getOpenFileAction(), new ToolBarPosition(FILE_TOOL_BAR_GROUP_ID));
        actionModule.registerToolBarItem(FrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, getSaveFileAction(), new ToolBarPosition(FILE_TOOL_BAR_GROUP_ID));
    }

    @Override
    public void registerCloseListener() {
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
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
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        JMenu recentFileMenu = recentFilesActions.getOpenRecentMenu();
        actionModule.registerMenuItem(FrameModuleApi.FILE_MENU_ID, MODULE_ID, recentFileMenu, new MenuPosition(NextToMode.AFTER, (String) getOpenFileAction().getValue(Action.NAME) + ActionUtils.DIALOG_MENUITEM_EXT));
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

    @Override
    public void updateRecentFilesList(URI fileUri, FileType fileType) {
        if (recentFilesActions != null) {
            recentFilesActions.updateRecentFilesList(fileUri, fileType);
        }
    }

    @Nonnull
    @Override
    public FileActions getFileActions() {
        if (fileActions == null) {
            ensureSetup();
            fileActions = new FileActions();
            fileActions.setup(application, resourceBundle);
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
            Logger.getLogger(FileModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void updateForFileOperations() {
        if (saveFileAction != null) {
            saveFileAction.updateForFileOperations();
        }
        if (saveAsFileAction != null) {
            saveAsFileAction.updateForFileOperations();
        }
    }
}
