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

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import org.exbin.jaguif.App;
import org.exbin.jaguif.language.api.ApplicationInfoKeys;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.license.gui.AboutPanel;
import org.exbin.jaguif.tabpages.api.AbstractTabPagesComponent;
import org.exbin.jaguif.tabpages.api.ComponentTabPagesContribution;
import org.exbin.jaguif.tabpages.api.TabPagesComponent;
import org.exbin.jaguif.utils.DesktopUtils;

/**
 * Single license page.
 */
@ParametersAreNonnullByDefault
public class SingleLicensePage extends AbstractTabPagesComponent {

    public static final String PAGE_ID = "singleLicense";
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(SingleLicensePage.class);
    protected final JScrollPane scrollPane = new JScrollPane();
    protected final JEditorPane editorPane = new JEditorPane();
    protected boolean contentLoaded = false;

    public SingleLicensePage() {
        putValue(KEY_NAME, resourceBundle.getString("pageName"));
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        if (!contentLoaded) {
            editorPane.setEditable(false);
            editorPane.setContentType("text/html"); // NOI18N
            scrollPane.setViewportView(editorPane);

            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            ResourceBundle langBundle = languageModule.getAppBundle();
            try {
                if (langBundle.containsKey(ApplicationInfoKeys.APPLICATION_LICENSE_FILE)) {
                    String licenseFilePath = langBundle.getString(ApplicationInfoKeys.APPLICATION_LICENSE_FILE);
                    editorPane.setPage(getClass().getResource(licenseFilePath));
                }
                editorPane.addHyperlinkListener((HyperlinkEvent event) -> {
                    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        DesktopUtils.openDesktopURL(event.getURL().toExternalForm());
                    }
                });
            } catch (IOException ex) {
                Logger.getLogger(AboutPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

            contentLoaded = true;
        }
        return scrollPane;
    }

    public static class Contribution implements ComponentTabPagesContribution {

        @Nonnull
        @Override
        public TabPagesComponent createComponent() {
            return new SingleLicensePage();
        }

        @Nonnull
        @Override
        public String getContributionId() {
            return PAGE_ID;
        }
    };
}
