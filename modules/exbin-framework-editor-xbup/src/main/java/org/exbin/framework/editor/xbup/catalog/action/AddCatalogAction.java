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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.catalog.gui.AddCatalogPanel;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.utils.handler.DefaultControlHandler;
import org.exbin.xbup.catalog.modifiable.XBMRoot;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCRoot;
import org.exbin.xbup.core.catalog.base.manager.XBCRootManager;
import org.exbin.xbup.core.catalog.base.service.XBCNodeService;

/**
 * Add catalog root action.
 *
 * @version 0.2.2 2022/01/15
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddCatalogAction extends AbstractAction {

    public static final String ACTION_ID = "addCatalogAction";

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(AddCatalogAction.class);

    private XBApplication application;
    private XBACatalog catalog;
    private XBCNodeService nodeService;

    private XBCRoot resultRoot;

    private Component parentComponent;

    public AddCatalogAction() {
    }

    public void setup(XBApplication application) {
        this.application = application;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        nodeService = catalog == null ? null : catalog.getCatalogService(XBCNodeService.class);
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    @Nullable
    public XBCRoot getResultRoot() {
        return resultRoot;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        resultRoot = null;
        AddCatalogPanel panel = new AddCatalogPanel();
        panel.setApplication(application);
        panel.setCatalog(catalog);
        DefaultControlPanel controlPanel = new DefaultControlPanel();
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(panel, controlPanel);
        controlPanel.setHandler((actionType) -> {
            if (actionType == DefaultControlHandler.ControlActionType.OK) {
                XBCRootManager rootManager = catalog.getCatalogManager(XBCRootManager.class);
                resultRoot = (XBMRoot) rootManager.createEmptyRoot(panel.getCatalogUrl());
            }
            dialog.close();
            dialog.dispose();
        });
        frameModule.setDialogTitle(dialog, panel.getResourceBundle());
        dialog.showCentered(parentComponent);
    }
}
