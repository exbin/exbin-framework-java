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
package org.exbin.framework.file.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.file.FileDialogsType;
import org.exbin.framework.file.FileModule;
import org.exbin.framework.file.settings.gui.FileSettingsPanel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;

/**
 * File settings component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FileSettingsComponent implements SettingsComponentProvider {

    public static final String COMPONENT_ID = "file";

    @Nonnull
    @Override
    public SettingsComponent createComponent() {
        FileSettingsPanel fileSettingsPanel = new FileSettingsPanel();
        // TODO Move resources
        ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(FileModule.class);
        List<String> fileDialogsKeys = new ArrayList<>();
        fileDialogsKeys.add(FileDialogsType.SWING.name());
        fileDialogsKeys.add(FileDialogsType.AWT.name());
        List<String> fileDialogsNames = new ArrayList<>();
        fileDialogsNames.add(resourceBundle.getString("fileDialogs.swing"));
        fileDialogsNames.add(resourceBundle.getString("fileDialogs.swt"));
        fileSettingsPanel.setFileDialogs(fileDialogsKeys, fileDialogsNames);
        return fileSettingsPanel;
    }
}
