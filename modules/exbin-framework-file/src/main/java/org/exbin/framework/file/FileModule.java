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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.file.settings.FileOptions;
import org.exbin.framework.file.settings.FileSettingsApplier;
import org.exbin.framework.file.settings.FileSettingsComponent;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.ApplySettingsContribution;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsPageContribution;
import org.exbin.framework.options.settings.api.SettingsPageContributionRule;
import org.exbin.framework.frame.api.ComponentFrame;

/**
 * Framework file module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FileModule implements FileModuleApi {

    public static final String SETTINGS_PAGE_ID = "file";
    private java.util.ResourceBundle resourceBundle = null;

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

    @Override
    public void addFileType(FileType fileType) {
        registeredFileTypes.add(fileType);
    }

    @Nonnull
    @Override
    public Collection<FileType> getFileTypes() {
        return Collections.unmodifiableCollection(registeredFileTypes);
    }

    boolean useAwtDialogs = false;

    public boolean isUseAwtDialogs() {
        return useAwtDialogs;
    }

    public void setUseAwtDialogs(boolean useAwtDialogs) {
        this.useAwtDialogs = useAwtDialogs;
    }

    @Override
    public void registerCloseListener() {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        frameModule.addExitListener((ComponentFrame frameHandler) -> {
            // TODO
//            if (fileOperations != null) {
//                return fileOperations.releaseAllFiles();
//            }

            return true;
        });
    }

    /* @Nonnull
    @Override
    public FileActions getFileActions() {
        if (fileActions == null) {
            ensureSetup();
            fileActions = new FileActions();
            fileActions.init(resourceBundle);
        }

        return fileActions;
    } */

    @Override
    public void loadFromFile(String filename) {
        throw new IllegalStateException();
        /*if (fileOperations == null) {
            return;
        }
        try {
            fileOperations.loadFromFile(filename);
        } catch (URISyntaxException ex) {
            Logger.getLogger(FileModule.class.getName()).log(Level.SEVERE, null, ex);
        } */
    }

    @Override
    public void loadFromFile(URI fileUri) {
        throw new IllegalStateException();
        /* if (fileOperations == null) {
            return;
        }
        fileOperations.loadFromFile(fileUri, null); */
    }

    @Override
    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerOptionsSettings(FileOptions.class, (optionsStorage) -> new FileOptions(optionsStorage));

        settingsManagement.registerApplySetting(Object.class, new ApplySettingsContribution(SETTINGS_PAGE_ID, new FileSettingsApplier()));

        SettingsPageContribution pageContribution = new SettingsPageContribution(SETTINGS_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(pageContribution);
        SettingsComponentContribution settingsComponent = settingsManagement.registerComponent(FileSettingsComponent.COMPONENT_ID, new FileSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(pageContribution));
    }
}
