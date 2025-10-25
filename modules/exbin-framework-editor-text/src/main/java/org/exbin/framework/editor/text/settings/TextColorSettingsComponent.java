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
package org.exbin.framework.editor.text.settings;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.text.settings.gui.TextColorSettingsPanel;
import org.exbin.framework.editor.text.service.TextColorService;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;

/**
 * Text color settings component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextColorSettingsComponent implements SettingsComponentProvider {

    private TextColorSettingsPanel panel;
    private TextColorService textColorService;

    public void setTextColorService(TextColorService textColorService) {
        this.textColorService = textColorService;
    }

    @Nonnull
    @Override
    public SettingsComponent createComponent() {
        if (panel == null) {
            panel = new TextColorSettingsPanel();
            panel.setTextColorService(textColorService);
        }
        return panel;
    }
}
