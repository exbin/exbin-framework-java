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
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.gui.CatalogEditorWrapperPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.service.ServiceManagerModule;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;

/**
 * Catalog browser handler.
 *
 * @version 0.2.1 2019/07/15
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogBrowserHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;
    private XBACatalog catalog;

    private Action catalogBrowserAction;

    public CatalogBrowserHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
    }

    public void init() {
        catalogBrowserAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                ServiceManagerModule managerModule = application.getModuleRepository().getModuleByInterface(ServiceManagerModule.class);
                CatalogEditorWrapperPanel panel = new CatalogEditorWrapperPanel();
                panel.setApplication(application);
                panel.setMenuManagement(managerModule.getDefaultMenuManagement());
                panel.setCatalog(catalog);
                CloseControlPanel controlPanel = new CloseControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(panel, controlPanel);
                final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                controlPanel.setHandler(() -> {
                    dialog.close();
                    dialog.dispose();
                });
                dialog.showCentered((Component) e.getSource());
            }
        };
        ActionUtils.setupAction(catalogBrowserAction, resourceBundle, "catalogBrowserAction");
        catalogBrowserAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Nonnull
    public Action getCatalogBrowserAction() {
        return catalogBrowserAction;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }
}
