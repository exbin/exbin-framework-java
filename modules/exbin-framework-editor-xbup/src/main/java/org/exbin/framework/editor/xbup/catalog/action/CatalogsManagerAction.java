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
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.catalog.CatalogsManager;
import org.exbin.framework.editor.xbup.gui.AddBlockPanel;
import org.exbin.framework.editor.xbup.catalog.gui.CatalogsManagerPanel;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.gui.CloseControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;

/**
 * Catalogs manager action.
 *
 * @version 0.2.2 2020/12/23
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogsManagerAction extends AbstractAction {

    public static final String ACTION_ID = "catalogsManagerAction";

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(CatalogsManagerAction.class);

    private XBApplication application;
    private XBACatalog catalog;

    public CatalogsManagerAction() {
    }

    public void setup(XBApplication application) {
        this.application = application;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CatalogsManager catalogsBrowser = new CatalogsManager();
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        catalogsBrowser.setApplication(application);
        catalogsBrowser.setCatalog(catalog);
        CatalogsManagerPanel panel = catalogsBrowser.getCatalogsManagerPanel();
        CloseControlPanel controlPanel = new CloseControlPanel();
        final DialogWrapper dialog = frameModule.createDialog(panel, controlPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), AddBlockPanel.class, panel.getResourceBundle());
        controlPanel.setHandler(() -> {
            dialog.close();
            dialog.dispose();
        });
        frameModule.setDialogTitle(dialog, panel.getResourceBundle());
        dialog.showCentered((Component) e.getSource());
    }
}
