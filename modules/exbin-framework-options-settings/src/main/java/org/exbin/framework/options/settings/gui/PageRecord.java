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
package org.exbin.framework.options.settings.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsModifiedListener;
import org.exbin.framework.options.settings.api.SettingsPage;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.SettingsOptions;

/**
 * Options settings page record.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PageRecord {

    private final List<SettingsPage<?>> pages = new ArrayList<>();
    private final List<SettingsComponent<?>> components = new ArrayList<>();
    private final JPanel panel = new JPanel();
    private final GroupLayout.ParallelGroup horizontalGroup;
    private final GroupLayout.SequentialGroup verticalGroup;

    public PageRecord(SettingsPage<?> page) {
        GroupLayout groupLayout = new GroupLayout(panel);
        horizontalGroup = groupLayout.createParallelGroup();
        groupLayout.setHorizontalGroup(horizontalGroup);
        verticalGroup = groupLayout.createSequentialGroup();
        groupLayout.setVerticalGroup(verticalGroup);
        panel.setLayout(groupLayout);
        PageRecord.this.addOptionsPage(page, null);
    }

    public void addOptionsPage(SettingsPage<?> page, @Nullable SettingsModifiedListener listener) {
        pages.add(page);
        SettingsComponent<?> optionsComponent = page.createComponent();
        if (listener != null) {
            optionsComponent.setSettingsModifiedListener(listener);
        }
        components.add(optionsComponent);
        panel.add((Component) optionsComponent);
        horizontalGroup.addComponent((Component) optionsComponent);
//        if (visualParams != null && visualParams.isExpand()) {
//            verticalGroup.addComponent((Component) optionsComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
//        } else {
            verticalGroup.addComponent((Component) optionsComponent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
//        }

        panel.revalidate();
        panel.repaint();
    }

    @Nonnull
    public JPanel getPanel() {
        return panel;
    }

    @SuppressWarnings("unchecked")
    public void loadFromPreferences(OptionsStorage preferences) {
        for (int i = 0; i < pages.size(); i++) {
            SettingsPage page = pages.get(i);
            SettingsOptions options = page.createOptions();
//            page.loadFromPreferences(preferences, options);
            SettingsComponent component = components.get(i);
            component.loadFromOptions(options);
        }
    }

    @SuppressWarnings("unchecked")
    public void saveToPreferences(OptionsStorage preferences) {
        for (int i = 0; i < pages.size(); i++) {
            SettingsPage page = pages.get(i);
            SettingsOptions options = page.createOptions();
            SettingsComponent component = components.get(i);
            component.saveToOptions(options);
//            page.saveToPreferences(preferences, options);
        }
    }

    @SuppressWarnings("unchecked")
    public void saveAndApply(OptionsStorage preferences) {
        for (int i = 0; i < pages.size(); i++) {
            SettingsPage page = pages.get(i);
            SettingsOptions options = page.createOptions();
            SettingsComponent component = components.get(i);
            component.saveToOptions(options);
//            page.saveToPreferences(preferences, options);
//            page.applyPreferencesChanges(options);
        }
    }

    @SuppressWarnings("unchecked")
    public void applyPreferencesChanges(OptionsStorage preferences) {
        for (int i = 0; i < pages.size(); i++) {
            SettingsPage page = pages.get(i);
            SettingsOptions options = page.createOptions();
            SettingsComponent component = components.get(i);
            component.saveToOptions(options);
//            page.applyPreferencesChanges(options);
        }
    }

    public void setSettingsModifiedListener(SettingsModifiedListener listener) {
        for (SettingsComponent<?> component : components) {
            component.setSettingsModifiedListener(listener);
        }
    }
}
