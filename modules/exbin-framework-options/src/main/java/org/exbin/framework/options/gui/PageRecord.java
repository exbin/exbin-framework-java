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
package org.exbin.framework.options.gui;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.options.api.OptionsPage;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Options page record.
 *
 * @author ExBin Project (https://exbin.org)
 * @param <T> options data type
 */
@ParametersAreNonnullByDefault
public class PageRecord<T extends OptionsData> {

    private final OptionsPage<T> page;
    private final OptionsComponent<T> panel;

    public PageRecord(OptionsPage<T> page) {
        this.page = page;
        this.panel = page.createPanel();
    }

    @Nonnull
    public OptionsPage<T> getPage() {
        return page;
    }

    @Nonnull
    public OptionsComponent<T> getPanel() {
        return panel;
    }

    public void loadFromPreferences(OptionsStorage preferences) {
        T options = page.createOptions();
        page.loadFromPreferences(preferences, options);
        panel.loadFromOptions(options);
    }

    public void saveToPreferences(OptionsStorage preferences) {
        T options = page.createOptions();
        panel.saveToOptions(options);
        page.saveToPreferences(preferences, options);
    }

    public void saveAndApply(OptionsStorage preferences) {
        T options = page.createOptions();
        panel.saveToOptions(options);
        page.saveToPreferences(preferences, options);
        page.applyPreferencesChanges(options);
    }

    public void applyPreferencesChanges(OptionsStorage preferences) {
        T options = page.createOptions();
        panel.saveToOptions(options);
        page.applyPreferencesChanges(options);
    }
}
