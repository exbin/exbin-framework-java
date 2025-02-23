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
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.text.font.service.TextFontService;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.text.font.options.TextFontOptionsPage;
import org.exbin.framework.options.api.OptionsPageManagement;
import org.exbin.framework.utils.ObjectUtils;

/**
 * Text editor module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(TextFontModule.class);

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

    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(MODULE_ID);

        TextFontOptionsPage textFontOptionsPage = new TextFontOptionsPage();
        textFontOptionsPage.setTextFontService(textFontService);
        optionsPageManagement.registerPage(textFontOptionsPage);
    }

    @Nonnull
    public TextFontAction createTextFontAction() {
        ensureSetup();
        TextFontAction textFontAction = new TextFontAction();
        textFontAction.setup(resourceBundle);
        return textFontAction;
    }
}
