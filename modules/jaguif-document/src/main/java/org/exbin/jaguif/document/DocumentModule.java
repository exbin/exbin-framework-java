/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.document;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.document.api.DocumentManagement;
import org.exbin.jaguif.document.api.DocumentModuleApi;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.document.api.MemoryDocumentSource;
import org.exbin.jaguif.document.settings.StartupOptions;
import org.exbin.jaguif.document.settings.StartupSettingsComponent;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;

/**
 * Implementation of the document module.
 */
@ParametersAreNonnullByDefault
public class DocumentModule implements DocumentModuleApi {

    private ResourceBundle resourceBundle;
    private DocumentManager mainDocumentManager;

    public DocumentModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(DocumentModule.class);
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
    public DocumentManagement getMainDocumentManager() {
        if (mainDocumentManager == null) {
            mainDocumentManager = new DocumentManager();
        }

        return mainDocumentManager;
    }
    
    @Nonnull
    @Override
    public MemoryDocumentSource createMemoryDocumentSource() {
        return getMainDocumentManager().createMemoryDocumentSource();
    }

    @Nonnull
    @Override
    public String getNewDocumentNamePrefix() {
        return getResourceBundle().getString("newFileTitlePrefix");
    }

    @Override
    public void registerSettings() {
        getResourceBundle();
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManager = settingsModule.getMainSettingsManager();

        settingsManager.registerSettingsOptions(StartupOptions.class, (optionsStorage) -> new StartupOptions(optionsStorage));
        settingsManager.registerComponent(StartupSettingsComponent.COMPONENT_ID, new StartupSettingsComponent());

        SettingsPageContribution pageContribution = new SettingsPageContribution(SETTINGS_PAGE_ID, resourceBundle);
        settingsManager.registerPage(pageContribution);
    }

}
