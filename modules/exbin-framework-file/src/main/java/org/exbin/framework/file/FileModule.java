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
import org.exbin.framework.App;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.RelativeSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.toolbar.api.ToolBarManagement;
import org.exbin.framework.file.action.FileActions;
import org.exbin.framework.file.action.NewFileAction;
import org.exbin.framework.file.action.OpenFileAction;
import org.exbin.framework.file.action.RecentFilesActions;
import org.exbin.framework.file.action.SaveAsFileAction;
import org.exbin.framework.file.action.SaveFileAction;
import org.exbin.framework.file.api.FileOperations;
import org.exbin.framework.file.api.FileOperationsProvider;
import org.exbin.framework.file.settings.FileSettingsComponent;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsPageContribution;
import org.exbin.framework.options.settings.api.SettingsPageContributionRule;

/**
 * Framework file module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FileModule implements FileModuleApi, FileOperationsProvider {

    public static final String SETTINGS_PAGE_ID = "file";
    private java.util.ResourceBundle resourceBundle = null;
    private FileOperations fileOperations;

    private RecentFilesActions recentFilesActions;
    private FileActions fileActions;
    private final List<FileType> registeredFileTypes = new ArrayList<>();
    private boolean useAwtDialogs = false;

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

    public boolean isUseAwtDialogs() {
        return useAwtDialogs;
    }

    public void setUseAwtDialogs(boolean useAwtDialogs) {
        this.useAwtDialogs = useAwtDialogs;
    }

    @Override
    public void registerMenuFileHandlingActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(FILE_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(createNewFileAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(FILE_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(createOpenFileAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(FILE_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(createSaveFileAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(FILE_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(createSaveAsFileAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(FILE_MENU_GROUP_ID));
    }

    @Override
    public void registerToolBarFileHandlingActions() {
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarManagement mgmt = toolBarModule.getMainToolBarManagement(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(FILE_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerToolBarItem(createNewFileAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(FILE_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(createOpenFileAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(FILE_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(createSaveFileAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(FILE_TOOL_BAR_GROUP_ID));
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
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(() -> recentFilesActions.getOpenRecentMenu());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(FILE_MENU_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(RelativeSequenceContributionRule.NextToMode.AFTER, OpenFileAction.ACTION_ID));
    }

    @Nonnull
    @Override
    public NewFileAction createNewFileAction() {
        ensureSetup();
        NewFileAction newFileAction = new NewFileAction();
        newFileAction.init(resourceBundle);
        return newFileAction;
    }

    @Nonnull
    @Override
    public OpenFileAction createOpenFileAction() {
        ensureSetup();
        OpenFileAction openFileAction = new OpenFileAction();
        openFileAction.init(resourceBundle);
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
            recentFilesActions.init(resourceBundle, new RecentFilesActions.FilesController() {
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
            OptionsModuleApi preferencesModule = App.getModule(OptionsModuleApi.class);
            recentFilesActions.setPreferences(preferencesModule.getAppOptions());
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

    @Override
    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement setingsManagement = settingsModule.getMainSettingsManager();
        SettingsPageContribution settingsPage = setingsManagement.registerPage(SETTINGS_PAGE_ID);
        SettingsComponentContribution settingsComponent = setingsManagement.registerComponent(new FileSettingsComponent());
        setingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(settingsPage.getContributionId()));
    }
}
