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
package org.exbin.jaguif.action.manager.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.manager.settings.gui.KeyMapSettingsPanel;
import org.exbin.jaguif.action.manager.model.KeyMapRecord;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsComponent;
import org.exbin.jaguif.options.settings.api.SettingsComponentProvider;
import org.exbin.jaguif.toolbar.api.ToolBarDefinitionManagement;
import org.exbin.jaguif.toolbar.api.ToolBarModuleApi;

/**
 * Action manager settings component.
 */
@ParametersAreNonnullByDefault
public class KeyMapSettingsComponent implements SettingsComponentProvider {

    public static final String COMPONENT_ID = "keymap";

    @Nonnull
    @Override
    public SettingsComponent createComponent() {
        KeyMapSettingsPanel panel = new KeyMapSettingsPanel();
        ResourceBundle resourceBundle = panel.getResourceBundle();
        List<KeyMapRecord> records = new ArrayList<>();

        {
            String type = resourceBundle.getString("actionType.menu");
            MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
            MenuDefinitionManagement menuManager = menuModule.getMainMenuManager(MenuModuleApi.MODULE_ID);
            List<SequenceContribution> contributions = menuManager.getContributions();
            for (SequenceContribution contribution : contributions) {
                if (contribution instanceof ActionSequenceContribution) {
                    Action action = ((ActionSequenceContribution) contribution).createAction();
                    String name = (String) action.getValue(Action.NAME);
                    ImageIcon icon = (ImageIcon) action.getValue(Action.SMALL_ICON);
                    KeyStroke keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
                    records.add(new KeyMapRecord(name, icon, type, keyStroke));
                }
            }
        }

        {
            String type = resourceBundle.getString("actionType.toolBar");
            ToolBarModuleApi toolbarModule = App.getModule(ToolBarModuleApi.class);
            ToolBarDefinitionManagement toolBarManager = toolbarModule.getMainToolBarManager(ToolBarModuleApi.MODULE_ID);
            List<SequenceContribution> contributions = toolBarManager.getContributions();
            for (SequenceContribution contribution : contributions) {
                if (contribution instanceof ActionSequenceContribution) {
                    Action action = ((ActionSequenceContribution) contribution).createAction();
                    String name = (String) action.getValue(Action.NAME);
                    ImageIcon icon = (ImageIcon) action.getValue(Action.SMALL_ICON);
                    KeyStroke keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
                    records.add(new KeyMapRecord(name, icon, type, keyStroke));
                }
            }
        }
        panel.setRecords(records);
        return panel;
    }
}
