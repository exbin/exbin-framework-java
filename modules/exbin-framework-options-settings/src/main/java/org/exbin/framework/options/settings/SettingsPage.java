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
package org.exbin.framework.options.settings;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import org.exbin.framework.context.api.ApplicationContextProvider;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsModifiedListener;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.settings.api.VerticallyExpandable;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;

/**
 * Options settings page record.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SettingsPage {

    protected final String pageId;
    protected final List<SettingsComponent> components = new ArrayList<>();
    protected final JPanel panel = new JPanel();
    protected final GroupLayout.ParallelGroup horizontalGroup;
    protected final GroupLayout.SequentialGroup verticalGroup;

    public SettingsPage(String pageId) {
        this.pageId = pageId;
        GroupLayout groupLayout = new GroupLayout(panel);
        horizontalGroup = groupLayout.createParallelGroup();
        groupLayout.setHorizontalGroup(horizontalGroup);
        verticalGroup = groupLayout.createSequentialGroup();
        groupLayout.setVerticalGroup(verticalGroup);
        panel.setLayout(groupLayout);
    }

    @Nonnull
    public JPanel getPanel() {
        return panel;
    }

    @Nonnull
    public String getPageId() {
        return pageId;
    }
    
    public int getComponentsCount() {
        return components.size();
    }
    
    public void addComponent(SettingsComponent settingsComponent) {
        if (!components.isEmpty()) {
            appendLast(false);
        }

        components.add(settingsComponent);
    }
    
    public void finish() {
        if (!components.isEmpty()) {
            appendLast(true);
        }
        
        panel.revalidate();
        panel.repaint();
    }
    
    private void appendLast(boolean last) {
        SettingsComponent settingsComponent = components.get(components.size() - 1);
        panel.add((Component) settingsComponent);
        horizontalGroup.addComponent((Component) settingsComponent);
        if (last && settingsComponent instanceof VerticallyExpandable) {
            verticalGroup.addComponent((Component) settingsComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        } else {
            verticalGroup.addComponent((Component) settingsComponent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
        }
    }

    public void loadFromOptions(SettingsOptionsProvider settingsProvider, @Nullable ApplicationContextProvider applicationContextProvider) {
        for (SettingsComponent component : components) {
            component.loadFromOptions(settingsProvider, applicationContextProvider);
        }
    }

    public void saveToOptions(SettingsOptionsProvider settingsProvider, @Nullable ApplicationContextProvider applicationContextProvider) {
        for (SettingsComponent component : components) {
            component.saveToOptions(settingsProvider, applicationContextProvider);
        }
    }

    @SuppressWarnings("unchecked")
    public void saveAndApply(SettingsOptionsProvider settingsProvider, @Nullable ApplicationContextProvider applicationContextProvider) {
//        for (int i = 0; i < pages.size(); i++) {
//            SettingsPage page = pages.get(i);
//            SettingsOptions options = page.createOptions();
//            SettingsComponent component = components.get(i);
//            component.saveToOptions(options);
////            page.saveToPreferences(preferences, options);
////            page.applyPreferencesChanges(options);
//        }
    }

    @SuppressWarnings("unchecked")
    public void applyPreferencesChanges(SettingsOptionsProvider settingsProvider, @Nullable ApplicationContextProvider applicationContextProvider) {
//        for (int i = 0; i < pages.size(); i++) {
//            SettingsPage page = pages.get(i);
//            SettingsOptions options = page.createOptions();
//            SettingsComponent component = components.get(i);
//            component.saveToOptions(options);
////            page.applyPreferencesChanges(options);
//        }
    }

    public void setSettingsModifiedListener(SettingsModifiedListener listener) {
        for (SettingsComponent component : components) {
            component.setSettingsModifiedListener(listener);
        }
    }
}
