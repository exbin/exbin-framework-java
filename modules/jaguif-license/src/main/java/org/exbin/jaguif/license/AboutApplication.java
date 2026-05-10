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
package org.exbin.jaguif.license;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.jaguif.App;
import org.exbin.jaguif.license.action.AboutAction;
import org.exbin.jaguif.license.api.LicenseModuleApi;
import org.exbin.jaguif.license.gui.AboutPanel;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.tabpages.api.TabPages;
import org.exbin.jaguif.tabpages.api.TabPagesModuleApi;

/**
 * Manager for data about the application.
 */
@ParametersAreNonnullByDefault
public class AboutApplication {

    protected JComponent sideComponent = null;

    @Nonnull
    public AboutAction createAboutAction() {
        AboutAction aboutAction = new AboutAction();
        if (sideComponent != null) {
            aboutAction.setAboutDialogSideComponent(sideComponent);
        }
        return aboutAction;
    }

    public void setAboutDialogSideComponent(JComponent sideComponent) {
        this.sideComponent = sideComponent;
    }

    @Nonnull
    public JComponent createAboutPanel() {
        AboutPanel aboutPanel = new AboutPanel();
        ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
        TabPagesModuleApi tabPagesModule = App.getModule(TabPagesModuleApi.class);
        TabPages tabbedPagesPanel = tabPagesModule.createTabbedPagesPanel();
        ContextRegistration contextRegistration = contextModule.createContextRegistrator();
        tabPagesModule.buildTabPages(tabbedPagesPanel, LicenseModuleApi.ABOUT_PAGES_ID, contextRegistration);
        aboutPanel.loadFields();
        aboutPanel.setSideComponent(sideComponent);
        aboutPanel.setCenterComponent(tabbedPagesPanel.getComponent());
        return aboutPanel;
    }
}
