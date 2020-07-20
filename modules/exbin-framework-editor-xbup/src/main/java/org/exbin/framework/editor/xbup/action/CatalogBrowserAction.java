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
package org.exbin.framework.editor.xbup.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.CatalogsBrowser;
import org.exbin.framework.editor.xbup.gui.CatalogsBrowserPanel;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;

/**
 * Catalog browser action.
 *
 * @version 0.2.1 2020/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogBrowserAction extends AbstractAction {

    public static final String ACTION_ID = "catalogBrowserAction";

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(CatalogBrowserAction.class);

    private XBApplication application;
    private XBACatalog catalog;

    public CatalogBrowserAction() {
        init();
    }

    private void init() {
        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CatalogsBrowser catalogsBrowser = new CatalogsBrowser();
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        catalogsBrowser.setApplication(application);
        catalogsBrowser.setCatalog(catalog);
        CatalogsBrowserPanel panel = catalogsBrowser.getBrowserPanel();
        CloseControlPanel controlPanel = new CloseControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(panel, controlPanel);
        final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        controlPanel.setHandler(() -> {
            dialog.close();
            dialog.dispose();
        });
        frameModule.setDialogTitle(dialog, panel.getResourceBundle());
        dialog.showCentered((Component) e.getSource());
    }
}
