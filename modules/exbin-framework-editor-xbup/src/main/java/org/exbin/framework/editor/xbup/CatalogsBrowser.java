/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.editor.xbup;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.gui.CatalogEditorWrapperPanel;
import org.exbin.framework.editor.xbup.gui.CatalogsBrowserPanel;
import org.exbin.framework.gui.component.api.ActionsProvider;
import org.exbin.framework.gui.component.api.toolbar.SideToolBar;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.service.ServiceManagerModule;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;

/**
 * Catalog browser.
 *
 * @version 0.2.1 2020/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogsBrowser {

    private final CatalogsBrowserPanel browserPanel;
    private final BrowserActions actions;
    private XBApplication application;
    private XBACatalog catalog;

    public CatalogsBrowser() {
        browserPanel = new CatalogsBrowserPanel();
        actions = new BrowserActions();
        init();
    }

    private void init() {
        browserPanel.addActions(actions);
    }

    @Nonnull
    public CatalogsBrowserPanel getBrowserPanel() {
        return browserPanel;
    }

    public void setApplication(XBApplication application) {
        this.application = application;
        browserPanel.setApplication(application);
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        browserPanel.setCatalog(catalog);
    }

    private final class BrowserActions implements ActionsProvider {

        private final Action createCatalog;
        private final Action editCatalog;
        private final Action updateCatalog;

        public BrowserActions() {
            createCatalog = new AbstractAction("Create") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
            createCatalog.setEnabled(false);

            editCatalog = new AbstractAction("Edit") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    browserPanel.getSelectedItem();
                    GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                    ServiceManagerModule managerModule = application.getModuleRepository().getModuleByInterface(ServiceManagerModule.class);
                    CatalogEditorWrapperPanel panel = new CatalogEditorWrapperPanel();
                    panel.setApplication(application);
                    panel.setMenuManagement(managerModule.getDefaultMenuManagement());
                    panel.setCatalog(catalog);
                    CloseControlPanel controlPanel = new CloseControlPanel();
                    JPanel dialogPanel = WindowUtils.createDialogPanel(panel, controlPanel);
                    final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                    controlPanel.setHandler(() -> {
                        dialog.close();
                        dialog.dispose();
                    });
                    frameModule.setDialogTitle(dialog, panel.getResourceBundle());
                    dialog.showCentered((Component) e.getSource());
                }
            };
            
            updateCatalog = new AbstractAction("Update") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
            updateCatalog.setEnabled(false);
        }

        @Override
        public void registerActions(SideToolBar sideToolBar) {
            sideToolBar.addAction(createCatalog);
            sideToolBar.addAction(editCatalog);
            sideToolBar.addAction(updateCatalog);
        }
    }
}
