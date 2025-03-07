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
package org.exbin.framework.action.manager.options;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.exbin.framework.App;
import org.exbin.framework.action.manager.gui.KeyMapOptionsPanel;
import org.exbin.framework.action.manager.model.KeyMapRecord;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;

/**
 * Action manager options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionManagerOptionsPage implements DefaultOptionsPage<ActionManagerOptions> {

    public static final String PAGE_ID = "actionManager";

    @Nonnull
    @Override
    public String getId() {
        return PAGE_ID;
    }

    @Nonnull
    @Override
    public OptionsComponent<ActionManagerOptions> createComponent() {
        KeyMapOptionsPanel panel = new KeyMapOptionsPanel();
        List<KeyMapRecord> records = new ArrayList<>();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        List<Action> actions = menuModule.getMenuManagedActions();
        for (Action action : actions) {
            String name = (String) action.getValue(Action.NAME);
            ImageIcon icon = (ImageIcon) action.getValue(Action.SMALL_ICON);
            String type = getResourceBundle().getString("actionType.menu");
            KeyStroke keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
            records.add(new KeyMapRecord(name, icon, type, keyStroke));
        }

        ToolBarModuleApi toolbarModule = App.getModule(ToolBarModuleApi.class);
        actions = toolbarModule.getToolBarManagedActions();
        for (Action action : actions) {
            String name = (String) action.getValue(Action.NAME);
            ImageIcon icon = (ImageIcon) action.getValue(Action.SMALL_ICON);
            String type = getResourceBundle().getString("actionType.toolBar");
            KeyStroke keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
            records.add(new KeyMapRecord(name, icon, type, keyStroke));
        }
        panel.setRecords(records);
        return panel;
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(KeyMapOptionsPanel.class);
    }

    @Nonnull
    @Override
    public ActionManagerOptions createOptions() {
        return new ActionManagerOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, ActionManagerOptions options) {
        ActionManagerOptions prefs = new ActionManagerOptions(preferences);
        prefs.copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, ActionManagerOptions options) {
        ActionManagerOptions prefs = new ActionManagerOptions(preferences);
        options.copyTo(prefs);
    }

    @Override
    public void applyPreferencesChanges(ActionManagerOptions options) {
    }
}
