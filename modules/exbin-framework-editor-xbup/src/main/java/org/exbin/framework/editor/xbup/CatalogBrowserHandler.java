/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.editor.xbup;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.panel.CatalogEditorWrapperPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.service.ServiceManagerModule;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.panel.CloseControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;

/**
 * Catalog browser handler.
 *
 * @version 0.2.1 2019/06/23
 * @author ExBin Project (http://exbin.org)
 */
public class CatalogBrowserHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;
    private XBACatalog catalog;

    private int metaMask;

    private Action catalogBrowserAction;

    public CatalogBrowserHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
    }

    public void init() {
        metaMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

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
                    WindowUtils.closeWindow(dialog.getWindow());
                });
                WindowUtils.assignGlobalKeyListener(dialog.getWindow(), controlPanel.createOkCancelListener());
                dialog.center(dialog.getParent());
                dialog.show();
            }
        };
        ActionUtils.setupAction(catalogBrowserAction, resourceBundle, "catalogBrowserAction");
        catalogBrowserAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public Action getCatalogBrowserAction() {
        return catalogBrowserAction;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }
}
