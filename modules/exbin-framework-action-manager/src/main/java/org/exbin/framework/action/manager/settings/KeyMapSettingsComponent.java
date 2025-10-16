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
package org.exbin.framework.action.manager.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.exbin.framework.App;
import org.exbin.framework.action.manager.settings.gui.KeyMapSettingsPanel;
import org.exbin.framework.action.manager.model.KeyMapRecord;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;

/**
 * Action manager settings component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class KeyMapSettingsComponent implements SettingsComponentProvider<ActionManagerOptions> {

    public static final String COMPONENT_ID = "keymap";

    @Nonnull
    @Override
    public SettingsComponent<ActionManagerOptions> createComponent() {
        KeyMapSettingsPanel panel = new KeyMapSettingsPanel();
        ResourceBundle resourceBundle = panel.getResourceBundle();
        List<KeyMapRecord> records = new ArrayList<>();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        List<Action> actions = menuModule.getMenuManagedActions();
        for (Action action : actions) {
            String name = (String) action.getValue(Action.NAME);
            ImageIcon icon = (ImageIcon) action.getValue(Action.SMALL_ICON);
            String type = resourceBundle.getString("actionType.menu");
            KeyStroke keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
            records.add(new KeyMapRecord(name, icon, type, keyStroke));
        }

        ToolBarModuleApi toolbarModule = App.getModule(ToolBarModuleApi.class);
        actions = toolbarModule.getToolBarManagedActions();
        for (Action action : actions) {
            String name = (String) action.getValue(Action.NAME);
            ImageIcon icon = (ImageIcon) action.getValue(Action.SMALL_ICON);
            String type = resourceBundle.getString("actionType.toolBar");
            KeyStroke keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
            records.add(new KeyMapRecord(name, icon, type, keyStroke));
        }
        panel.setRecords(records);
        return panel;
    }
}
