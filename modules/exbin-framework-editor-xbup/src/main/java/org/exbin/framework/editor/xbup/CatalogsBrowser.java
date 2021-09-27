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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.gui.AddCatalogPanel;
import org.exbin.framework.editor.xbup.gui.CatalogEditorWrapperPanel;
import org.exbin.framework.editor.xbup.gui.CatalogsBrowserPanel;
import org.exbin.framework.gui.component.action.DefaultEditItemActions;
import org.exbin.framework.gui.component.api.toolbar.EditItemActionsHandler;
import org.exbin.framework.gui.component.api.toolbar.EditItemActionsUpdateListener;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.service.ServiceManagerModule;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;
import org.exbin.framework.gui.utils.gui.DefaultControlPanel;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.xbup.core.catalog.XBACatalog;

/**
 * Catalog browser.
 *
 * @version 0.2.1 2021/04/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogsBrowser {

    private final CatalogsBrowserPanel browserPanel;
    private final DefaultEditItemActions actions;
    private XBApplication application;
    private XBACatalog catalog;

    public CatalogsBrowser() {
        browserPanel = new CatalogsBrowserPanel();
        actions = new DefaultEditItemActions();
        actions.setEditItemActionsHandler(new EditItemActionsHandler() {
            @Override
            public void performAddItem() {
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                ServiceManagerModule managerModule = application.getModuleRepository().getModuleByInterface(ServiceManagerModule.class);
                AddCatalogPanel panel = new AddCatalogPanel();
                panel.setApplication(application);
                panel.setCatalog(catalog);
                DefaultControlPanel controlPanel = new DefaultControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(panel, controlPanel);
                final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                controlPanel.setHandler((actionType) -> {
                    if (actionType == DefaultControlHandler.ControlActionType.OK) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                    dialog.close();
                    dialog.dispose();
                });
                frameModule.setDialogTitle(dialog, panel.getResourceBundle());
                dialog.showCentered(browserPanel);
            }

            @Override
            public void performEditItem() {
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
                dialog.showCentered(browserPanel);
            }

            @Override
            public void performDeleteItem() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isSelection() {
                return true;
            }

            @Override
            public boolean isEditable() {
                return true;
            }

            @Override
            public void setUpdateListener(EditItemActionsUpdateListener updateListener) {
            }
        });
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
}
