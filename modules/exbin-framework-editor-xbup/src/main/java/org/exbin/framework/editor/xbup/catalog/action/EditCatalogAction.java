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
package org.exbin.framework.editor.xbup.catalog.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.catalog.gui.CatalogEditorWrapperPanel;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.service.ServiceManagerModule;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCRoot;

/**
 * Edit catalog root action.
 *
 * @version 0.2.2 2021/12/25
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditCatalogAction extends AbstractAction {

    public static final String ACTION_ID = "editCatalogAction";

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(EditCatalogAction.class);

    private XBApplication application;
    private XBACatalog catalog;

    private Component parentComponent;
    private XBCRoot activeItem;

    public EditCatalogAction() {
    }

    public void setup(XBApplication application) {
        this.application = application;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    public void setActiveItem(XBCRoot activeItem) {
        this.activeItem = activeItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO catalogsManagerPanel.getSelectedItem();
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
        dialog.showCentered(parentComponent);
    }
}
