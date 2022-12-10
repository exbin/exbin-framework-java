/*
 * Copyright (C) ExBin Project
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
package org.exbin.framework.service.catalog.gui;

import java.awt.event.ActionEvent;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.handler.RemovalControlHandler;
import org.exbin.framework.utils.gui.RemovalControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.service.XBCXIconService;

/**
 * Catalog big icon property cell panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class CatalogSIconPropertyTableCellPanel extends CatalogPropertyTableCellPanel {

    private XBApplication application;
    private XBACatalog catalog;
    private byte[] icon;

    public CatalogSIconPropertyTableCellPanel(XBACatalog catalog) {
        super();
        this.catalog = catalog;
        init();
    }

    private void init() {
        setEditorAction((ActionEvent e) -> {
            performEditorAction();
        });
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    public void performEditorAction() {
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        CatalogEditIconPanel iconPanel = new CatalogEditIconPanel(catalog, icon);
        RemovalControlPanel controlPanel = new RemovalControlPanel();
        final DialogWrapper dialog = frameModule.createDialog(iconPanel, controlPanel);
        frameModule.setDialogTitle(dialog, iconPanel.getResourceBundle());
        controlPanel.setHandler((RemovalControlHandler.ControlActionType actionType) -> {
            switch (actionType) {
                case OK: {
                    icon = iconPanel.getIcon();
                    setDocLabel();
                    break;
                }
                case CANCEL: {
                    break;
                }
                case REMOVE: {
                    icon = new byte[0];
                    setDocLabel();
                    break;
                }
            }
            dialog.close();
        });
        dialog.showCentered(this);
        dialog.dispose();
    }

    public void setCatalogItem(XBCItem catalogItem) {
        XBCXIconService iconService = catalog.getCatalogService(XBCXIconService.class);
        icon = iconService.getDefaultSmallIconData(catalogItem);
        setDocLabel();
    }

    private void setDocLabel() {
        setPropertyText(icon == null || icon.length == 0 ? "" : "[" + icon.length + " bytes]");
    }

    public byte[] getIcon() {
        return icon;
    }

    public XBACatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }
}
