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
package org.exbin.framework.text.font;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.text.font.action.TextFontAction;
import org.exbin.framework.text.font.service.TextFontService;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.ApplySettingsContribution;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.text.font.settings.TextFontSettingsComponent;
import org.exbin.framework.utils.ObjectUtils;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsPageContribution;
import org.exbin.framework.options.settings.api.SettingsPageContributionRule;
import org.exbin.framework.text.font.settings.TextFontOptions;
import org.exbin.framework.text.font.settings.TextFontSettingsApplier;

/**
 * Text font module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(TextFontModule.class);
    public static final String SETTINGS_PAGE_ID = "textFont";

    private ResourceBundle resourceBundle;

    private TextFontService textFontService;

    public TextFontModule() {
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(TextFontModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    public TextFontService getTextFontService() {
        return ObjectUtils.requireNonNull(textFontService);
    }

    public void setTextFontService(TextFontService textFontService) {
        this.textFontService = textFontService;
    }

    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();
        
        settingsManagement.registerOptionsSettings(TextFontOptions.class, (optionsStorage) -> new TextFontOptions(optionsStorage));
        
        settingsManagement.registerApplySetting(Object.class, new ApplySettingsContribution(TextFontSettingsApplier.APPLIER_ID, new TextFontSettingsApplier()));

        SettingsPageContribution pageContribution = new SettingsPageContribution(SETTINGS_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(pageContribution);

        TextFontSettingsComponent textFontSettingsComponent = new TextFontSettingsComponent();
        textFontSettingsComponent.setTextFontService(textFontService);
        SettingsComponentContribution settingsComponent = settingsManagement.registerComponent(textFontSettingsComponent.COMPONENT_ID, textFontSettingsComponent);
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(pageContribution));
    }

    @Nonnull
    public TextFontAction createTextFontAction() {
        ensureSetup();
        TextFontAction textFontAction = new TextFontAction();
        textFontAction.setup(resourceBundle);
        return textFontAction;
    }
}
