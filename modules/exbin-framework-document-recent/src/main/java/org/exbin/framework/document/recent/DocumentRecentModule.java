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
package org.exbin.framework.document.recent;

import java.net.URI;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.RelativeSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.document.api.DocumentModuleApi;
import org.exbin.framework.document.recent.action.RecentFilesActions;
import org.exbin.framework.document.recent.settings.RecentFilesOptions;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuDefinitionManagement;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;

/**
 * Recent documents module.
 *
 * @author ExBin Project (https://exbin.org)
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

    /**
     * Registers list of last opened files into file menu.
     */
    public void registerRecenFilesMenuActions() {
        getRecentFilesActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(() -> recentFilesActions.getOpenRecentMenu());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_MENU_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(RelativeSequenceContributionRule.NextToMode.AFTER, "openFileAction")); // OpenFileAction.ACTION_ID
    }

    @Nonnull
    public RecentFilesActions getRecentFilesActions() {
        if (recentFilesActions == null) {
            ensureSetup();
            recentFilesActions = new RecentFilesActions();
            recentFilesActions.init(resourceBundle, new RecentFilesActions.FilesController() {
                @Override
                public void loadFromFile(URI fileUri, @Nullable FileType fileType) {
                    // TODO fileOperations.loadFromFile(fileUri, fileType);
                }

                @Nonnull
                @Override
                public List<FileType> getRegisteredFileTypes() {
                    throw new IllegalStateException();
                    // TODO return registeredFileTypes;
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

        settingsManagement.registerOptionsSettings(RecentFilesOptions.class, (optionsStorage) -> new RecentFilesOptions(optionsStorage));
    }
}
