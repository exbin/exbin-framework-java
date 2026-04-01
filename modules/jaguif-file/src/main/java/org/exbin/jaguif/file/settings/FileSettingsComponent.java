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
package org.exbin.jaguif.file.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.file.api.FileDialogsProvider;
import org.exbin.jaguif.file.api.FileModuleApi;
import org.exbin.jaguif.file.settings.gui.FileSettingsPanel;
import org.exbin.jaguif.options.settings.api.SettingsComponent;
import org.exbin.jaguif.options.settings.api.SettingsComponentProvider;

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
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        Map<String, FileDialogsProvider> fileDialogsProviders = fileModule.getFileDialogsProviders();
        List<String> fileDialogsKeys = new ArrayList<>();
        List<String> fileDialogsNames = new ArrayList<>();
        for (Map.Entry<String, FileDialogsProvider> entry : fileDialogsProviders.entrySet()) {
            String providerId = entry.getKey();
            FileDialogsProvider provider = entry.getValue();
            fileDialogsKeys.add(providerId);
            fileDialogsNames.add(provider.getProviderName());
        }
        fileSettingsPanel.setFileDialogs(fileDialogsKeys, fileDialogsNames);
        return fileSettingsPanel;
    }
}
