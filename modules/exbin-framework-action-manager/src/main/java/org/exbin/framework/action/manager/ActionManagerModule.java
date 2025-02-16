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

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.manager.options.ActionManagerOptionsPage;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.GroupOptionsPageRule;
import org.exbin.framework.options.api.OptionsGroup;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPageManagement;

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

        ActionManagerOptionsPage actionOptionsPage = new ActionManagerOptionsPage();
        
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(MODULE_ID);

        OptionsGroup keymapOptionsGroup = optionsModule.createOptionsGroup("keymap", getResourceBundle());
        optionsPageManagement.registerGroup(keymapOptionsGroup);

        optionsPageManagement.registerPage(actionOptionsPage);
        optionsPageManagement.registerPageRule(actionOptionsPage, new GroupOptionsPageRule(keymapOptionsGroup));
    }
}
