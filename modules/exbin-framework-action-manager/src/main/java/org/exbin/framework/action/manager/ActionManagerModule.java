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
package org.exbin.framework.action.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.exbin.framework.App;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.manager.gui.KeyMapOptionsPanel;
import org.exbin.framework.action.manager.model.KeyMapRecord;
import org.exbin.framework.action.manager.options.impl.ActionOptionsImpl;
import org.exbin.framework.action.manager.preferences.ActionPreferences;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPage;
import org.exbin.framework.options.api.OptionsPathItem;
import org.exbin.framework.utils.ComponentResourceProvider;
import org.exbin.framework.preferences.api.Preferences;

/**
 * Action manager module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionManagerModule implements org.exbin.framework.Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(ActionManagerModule.class);

    private ResourceBundle resourceBundle;

    public ActionManagerModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ActionManagerModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);

        OptionsPage<ActionOptionsImpl> actionOptionsPage = new DefaultOptionsPage<ActionOptionsImpl>() {
            @Nonnull
            @Override
            public OptionsComponent<ActionOptionsImpl> createPanel() {
                KeyMapOptionsPanel panel = new KeyMapOptionsPanel();
                List<KeyMapRecord> records = new ArrayList<>();
                ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
                List<Action> actions = actionModule.getAllManagedActions();
                for (Action action : actions) {
                    String name = (String) action.getValue(Action.NAME);
                    String type = "";
                    KeyStroke keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
                    String shortcut = keyStroke == null ? "" : keyStroke.toString();
                    records.add(new KeyMapRecord(name, type, shortcut));
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
            public ActionOptionsImpl createOptions() {
                return new ActionOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, ActionOptionsImpl options) {
                ActionPreferences prefs = new ActionPreferences(preferences);
                options.loadFromPreferences(prefs);
            }

            @Override
            public void saveToPreferences(Preferences preferences, ActionOptionsImpl options) {
                ActionPreferences prefs = new ActionPreferences(preferences);
                options.saveToParameters(prefs);
            }

            @Override
            public void applyPreferencesChanges(ActionOptionsImpl options) {
            }
        };
        ResourceBundle optionsResourceBundle = ((ComponentResourceProvider) actionOptionsPage).getResourceBundle();
        List<OptionsPathItem> optionsPath = new ArrayList<>();
        optionsPath.add(new OptionsPathItem(optionsResourceBundle.getString("options.name"), optionsResourceBundle.getString("options.caption")));
        optionsModule.addOptionsPage(actionOptionsPage, optionsPath);
    }
}
