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
package org.exbin.jaguif.document.recent;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.RelativeSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.document.api.DocumentModuleApi;
import org.exbin.jaguif.document.recent.action.RecentFilesActions;
import org.exbin.jaguif.document.recent.settings.RecentFilesOptions;
import org.exbin.jaguif.file.api.FileModuleApi;
import org.exbin.jaguif.file.api.FileType;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;

/**
 * Recent documents module.
 */
@ParametersAreNonnullByDefault
public class DocumentRecentModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(DocumentRecentModule.class);

    private ResourceBundle resourceBundle;
    private RecentFilesActions recentFilesActions;

    public DocumentRecentModule() {
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(DocumentRecentModule.class);
        }

        return resourceBundle;
    }

    public void registerRecentFilesUpdate() {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileModule.addFileUsageListener(this::updateRecentFilesList);
    }

    /**
     * Registers list of last opened files into file menu.
     */
    public void registerRecentFilesMenuActions() {
        getRecentFilesActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(() -> recentFilesActions.createOpenRecentMenu());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_MENU_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(RelativeSequenceContributionRule.NextToMode.AFTER, "openFile")); // OpenFileAction.ACTION_ID
    }

    @Nonnull
    public RecentFilesActions getRecentFilesActions() {
        if (recentFilesActions == null) {
            ensureSetup();
            recentFilesActions = new RecentFilesActions();
            recentFilesActions.init(resourceBundle, new RecentFilesActions.FilesController() {
                @Override
                public void openRecentFile(URI fileUri, @Nullable FileType fileType) {
                    FileModuleApi fileModule = App.getModule(FileModuleApi.class);
                    fileModule.openFile(fileUri);
                }

                @Nonnull
                @Override
                public List<FileType> getRegisteredFileTypes() {
                    FileModuleApi fileModule = App.getModule(FileModuleApi.class);
                    List<FileType> fileTypes = new ArrayList<>();
                    fileTypes.addAll(fileModule.getFileTypes());
                    return fileTypes;
                }
            });
            OptionsModuleApi preferencesModule = App.getModule(OptionsModuleApi.class);
            recentFilesActions.setOptionsStorage(preferencesModule.getAppOptions());
        }
        return recentFilesActions;
    }

    public void updateRecentFilesList(URI fileUri, @Nullable FileType fileType) {
        if (recentFilesActions != null) {
            recentFilesActions.updateRecentFilesList(fileUri, fileType);
        }
    }

    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerSettingsOptions(RecentFilesOptions.class, (optionsStorage) -> new RecentFilesOptions(optionsStorage));
    }
}
