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
import javax.swing.JMenu;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.GroupMenuContributionRule;
import org.exbin.framework.action.api.GroupToolBarContributionRule;
import org.exbin.framework.action.api.MenuContribution;
import org.exbin.framework.action.api.MenuManagement;
import org.exbin.framework.action.api.NextToMode;
import org.exbin.framework.action.api.PositionMenuContributionRule;
import org.exbin.framework.action.api.PositionToolBarContributionRule;
import org.exbin.framework.action.api.RelativeMenuContributionRule;
import org.exbin.framework.action.api.ToolBarContribution;
import org.exbin.framework.action.api.ToolBarManagement;
import org.exbin.framework.file.action.FileActions;
import org.exbin.framework.file.action.NewFileAction;
import org.exbin.framework.file.action.OpenFileAction;
import org.exbin.framework.file.action.RecentFilesActions;
import org.exbin.framework.file.action.SaveAsFileAction;
import org.exbin.framework.file.action.SaveFileAction;
import org.exbin.framework.file.api.FileOperations;
import org.exbin.framework.file.api.FileOperationsProvider;
import org.exbin.framework.file.options.gui.FileOptionsPanel;
import org.exbin.framework.file.options.impl.FileOptionsImpl;
import org.exbin.framework.file.preferences.FilePreferences;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPage;
import org.exbin.framework.options.api.OptionsPathItem;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.utils.ComponentResourceProvider;

/**
 * Framework file module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FileModule implements FileModuleApi, FileOperationsProvider {

    private java.util.ResourceBundle resourceBundle = null;
    private FileOperations fileOperations;

    private RecentFilesActions recentFilesActions;
    private FileActions fileActions;
    private final List<FileType> registeredFileTypes = new ArrayList<>();

    public FileModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(FileModule.class);
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
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuGroup(ActionConsts.FILE_MENU_ID, FILE_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.TOP));
        contribution = mgmt.registerMenuItem(ActionConsts.FILE_MENU_ID, createNewFileAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(FILE_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(ActionConsts.FILE_MENU_ID, createOpenFileAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(FILE_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(ActionConsts.FILE_MENU_ID, createSaveFileAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(FILE_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(ActionConsts.FILE_MENU_ID, createSaveAsFileAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(FILE_MENU_GROUP_ID));
    }

    @Override
    public void registerToolBarFileHandlingActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        ToolBarManagement mgmt = actionModule.getToolBarManagement(MODULE_ID);
        ToolBarContribution contribution = mgmt.registerToolBarGroup(ActionConsts.MAIN_TOOL_BAR_ID, FILE_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionToolBarContributionRule(PositionMode.TOP));
        contribution = mgmt.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, createNewFileAction());
        mgmt.registerToolBarRule(contribution, new GroupToolBarContributionRule(FILE_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, createOpenFileAction());
        mgmt.registerToolBarRule(contribution, new GroupToolBarContributionRule(FILE_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, createSaveFileAction());
        mgmt.registerToolBarRule(contribution, new GroupToolBarContributionRule(FILE_TOOL_BAR_GROUP_ID));
    }

    @Override
    public void registerCloseListener() {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
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
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        JMenu recentFileMenu = recentFilesActions.getOpenRecentMenu();
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(ActionConsts.FILE_MENU_ID, recentFileMenu);
        mgmt.registerMenuRule(contribution, new RelativeMenuContributionRule(NextToMode.AFTER, OpenFileAction.ACTION_ID));
    }

    @Nonnull
    @Override
    public NewFileAction createNewFileAction() {
        ensureSetup();
        NewFileAction newFileAction = new NewFileAction();
        newFileAction.init(resourceBundle, this);
        return newFileAction;
    }

    @Nonnull
    @Override
    public OpenFileAction createOpenFileAction() {
        ensureSetup();
        OpenFileAction openFileAction = new OpenFileAction();
        openFileAction.init(resourceBundle, this);
        return openFileAction;
    }

    @Nonnull
    @Override
    public SaveFileAction createSaveFileAction() {
        ensureSetup();
        SaveFileAction saveFileAction = new SaveFileAction();
        saveFileAction.init(resourceBundle);
        return saveFileAction;
    }

    @Nonnull
    @Override
    public SaveAsFileAction createSaveAsFileAction() {
        ensureSetup();
        SaveAsFileAction saveAsFileAction = new SaveAsFileAction();
        saveAsFileAction.init(resourceBundle);
        return saveAsFileAction;
    }

    @Nonnull
    public RecentFilesActions getRecentFilesActions() {
        if (recentFilesActions == null) {
            recentFilesActions = new RecentFilesActions();
            recentFilesActions.init(resourceBundle, new RecentFilesActions.FilesControl() {
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
            PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
            recentFilesActions.setPreferences(preferencesModule.getAppPreferences());
        }
        return recentFilesActions;
    }

    @Override
    public void updateRecentFilesList(URI fileUri, @Nullable FileType fileType) {
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
            fileActions.init(resourceBundle);
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
    public void loadFromFile(URI fileUri) {
        if (fileOperations == null) {
            return;
        }
        fileOperations.loadFromFile(fileUri, null);
    }

    @Nonnull
    public OptionsPage<FileOptionsImpl> getFileOptionsPage() {
        return new DefaultOptionsPage<FileOptionsImpl>() {

            @Nonnull
            @Override
            public OptionsComponent<FileOptionsImpl> createPanel() {
                FileOptionsPanel fileOptionsPanel = new FileOptionsPanel();
                List<String> fileDialogsKeys = new ArrayList<>();
                fileDialogsKeys.add("SWING");
                fileDialogsKeys.add("AWT");
                List<String> fileDialogsNames = new ArrayList<>();
                fileDialogsNames.add(resourceBundle.getString("fileDialogs.swing"));
                fileDialogsNames.add(resourceBundle.getString("fileDialogs.swt"));
                fileOptionsPanel.setFileDialogs(fileDialogsKeys, fileDialogsNames);
                return fileOptionsPanel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return App.getModule(LanguageModuleApi.class).getBundle(FileOptionsPanel.class);
            }

            @Nonnull
            @Override
            public FileOptionsImpl createOptions() {
                return new FileOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, FileOptionsImpl options) {
                FilePreferences prefs = new FilePreferences(preferences);
                options.setFileDialogs(prefs.getFileDialogs());
            }

            @Override
            public void saveToPreferences(Preferences preferences, FileOptionsImpl options) {
                FilePreferences prefs = new FilePreferences(preferences);
                prefs.setFileDialogs(options.getFileDialogs());
            }

            @Override
            public void applyPreferencesChanges(FileOptionsImpl options) {
                String fileDialogs = options.getFileDialogs();
                getFileActions().setUseAwtDialogs("AWT".equals(fileDialogs));
            }
        };
    }

    @Override
    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPage<FileOptionsImpl> fileOptionsPage = getFileOptionsPage();
        ResourceBundle optionsResourceBundle = ((ComponentResourceProvider) fileOptionsPage).getResourceBundle();
        List<OptionsPathItem> optionsPath = new ArrayList<>();
        optionsPath.add(new OptionsPathItem(optionsResourceBundle.getString("options.name"), optionsResourceBundle.getString("options.caption")));
        optionsModule.addOptionsPage(fileOptionsPage, optionsPath);
    }
}
