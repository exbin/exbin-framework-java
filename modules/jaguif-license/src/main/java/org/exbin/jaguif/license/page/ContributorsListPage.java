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
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import org.exbin.jaguif.App;
import org.exbin.jaguif.language.api.ApplicationInfoKeys;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.tabpages.api.AbstractTabPagesComponent;

/**
 * Contributors list page.
 */
@ParametersAreNonnullByDefault
public class ContributorsListPage extends AbstractTabPagesComponent {

    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ContributorsListPage.class);
    protected final JScrollPane scrollPane = new JScrollPane();
    protected final JEditorPane editorPane = new JEditorPane();
    protected boolean contentLoaded = false;

    public ContributorsListPage() {
        putValue(KEY_NAME, resourceBundle.getString("pageName"));
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        if (!contentLoaded) {
            editorPane.setEditable(false);
            editorPane.setContentType("text/plain"); // NOI18N
            scrollPane.setViewportView(editorPane);

            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            ResourceBundle langBundle = languageModule.getAppBundle();
            editorPane.setText(langBundle.getString(ApplicationInfoKeys.APPLICATION_AUTHORS));

            contentLoaded = true;
        }
        return scrollPane;
    }
}
