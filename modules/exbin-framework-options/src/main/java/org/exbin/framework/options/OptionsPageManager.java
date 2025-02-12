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
package org.exbin.framework.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.options.api.OptionsPage;
import org.exbin.framework.options.api.OptionsPageManagement;
import org.exbin.framework.options.api.OptionsPageReceiver;
import org.exbin.framework.options.api.OptionsPageRule;
import org.exbin.framework.options.api.OptionsPathItem;
import org.exbin.framework.options.api.PathOptionsPageRule;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.utils.ComponentResourceProvider;

/**
 * Options page manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsPageManager implements OptionsPageManagement {

    private final List<OptionsPage<?>> optionsPages = new ArrayList<>();
    private final Map<OptionsPage<?>, List<OptionsPageRule>> optionsPagesRules = new HashMap<>();

    @Override
    public void registerOptionsPage(OptionsPage<?> optionsPage) {
        String optionsDefaultPath;
        if (optionsPage instanceof ComponentResourceProvider) {
            ResourceBundle componentResourceBundle = ((ComponentResourceProvider) optionsPage).getResourceBundle();
            optionsDefaultPath = componentResourceBundle.getString("options.path");
        } else {
            optionsDefaultPath = null;
        }

        optionsPages.add(optionsPage);
    }
    
    @Override
    public void registerOptionsPageRule(OptionsPage<?> optionsPage, OptionsPageRule optionsPageRule) {
        
    }

    public void passOptionsPages(OptionsPageReceiver optionsPageReceiver) {
        for (OptionsPage<?> optionsPage : optionsPages) {
            List<OptionsPathItem> path = null;
            List<OptionsPageRule> rules = optionsPagesRules.get(optionsPage);
            for (OptionsPageRule rule : rules) {
                if (rule instanceof PathOptionsPageRule) {
                    path = ((PathOptionsPageRule) rule).getPath();
                    break;
                }
            }
            optionsPageReceiver.addOptionsPage(optionsPage, path);
        }
    }

    public void initialLoadFromPreferences() {
        // TODO use preferences instead of options for initial apply
        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
        Preferences preferences = preferencesModule.getAppPreferences();
        for (OptionsPage<?> optionsPage : optionsPages) {
            OptionsData options = optionsPage.createOptions();
            ((OptionsPage) optionsPage).loadFromPreferences(preferences, options);
        }
    }
}
