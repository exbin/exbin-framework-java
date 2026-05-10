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
package org.exbin.jaguif.license.page;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.jaguif.App;
import org.exbin.jaguif.license.page.gui.BasicInfoPanel;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.tabpages.api.AbstractTabPagesComponent;
import org.exbin.jaguif.tabpages.api.ComponentTabPagesContribution;
import org.exbin.jaguif.tabpages.api.TabPagesComponent;

/**
 * Basic application information page.
 */
@ParametersAreNonnullByDefault
public class BasicInfoPage extends AbstractTabPagesComponent {

    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BasicInfoPage.class);
    protected final BasicInfoPanel component = new BasicInfoPanel();
    protected boolean variablesLoaded = false;

    public BasicInfoPage() {
        putValue(KEY_NAME, resourceBundle.getString("pageName"));
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        if (!variablesLoaded) {
            component.loadFromApplication();
            variablesLoaded = true;
        }
        return component;
    }

    public static class Contribution implements ComponentTabPagesContribution {

        @Nonnull
        @Override
        public TabPagesComponent createComponent() {
            return new BasicInfoPage();
        }

        @Nonnull
        @Override
        public String getContributionId() {
            return KEY_ID;
        }
    };
}
