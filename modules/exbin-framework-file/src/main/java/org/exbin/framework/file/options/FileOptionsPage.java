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
package org.exbin.framework.file.options;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.file.FileDialogsType;
import org.exbin.framework.file.FileModule;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.file.options.gui.FileOptionsPanel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * File options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FileOptionsPage implements DefaultOptionsPage<FileOptions> {

    public static final String PAGE_ID = "file";

    private java.util.ResourceBundle resourceBundle;

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    @Override
    public String getId() {
        return PAGE_ID;
    }

    @Nonnull
    @Override
    public OptionsComponent<FileOptions> createPanel() {
        FileOptionsPanel fileOptionsPanel = new FileOptionsPanel();
        List<String> fileDialogsKeys = new ArrayList<>();
        fileDialogsKeys.add(FileDialogsType.SWING.name());
        fileDialogsKeys.add(FileDialogsType.AWT.name());
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
    public FileOptions createOptions() {
        return new FileOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, FileOptions options) {
        FileOptions prefs = new FileOptions(preferences);
        options.setFileDialogs(prefs.getFileDialogs());
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, FileOptions options) {
        FileOptions prefs = new FileOptions(preferences);
        prefs.setFileDialogs(options.getFileDialogs());
    }

    @Override
    public void applyPreferencesChanges(FileOptions options) {
        // TODO Support for other file dialogs
        String fileDialogs = options.getFileDialogs();
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        ((FileModule) fileModule).setUseAwtDialogs(FileDialogsType.AWT.name().equals(fileDialogs));
    }
}
