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
package org.exbin.framework.gui.service.catalog.panel;

import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.RemovalControlHandler;
import org.exbin.framework.gui.utils.panel.RemovalControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.service.XBCXHDocService;

/**
 * Catalog hDoc property cell panel.
 *
 * @version 0.2.1 2019/06/28
 * @author ExBin Project (http://exbin.org)
 */
public class CatalogDocPropertyTableCellPanel extends PropertyTableCellPanel {

    private XBApplication application;
    private XBACatalog catalog;
    private String doc;

    public CatalogDocPropertyTableCellPanel(XBACatalog catalog) {
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
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        CatalogEditDocumentationPanel docPanel = new CatalogEditDocumentationPanel();
        docPanel.setDocumentation(doc);
        RemovalControlPanel controlPanel = new RemovalControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(docPanel, controlPanel);
        final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        frameModule.setDialogTitle(dialog, docPanel.getResourceBundle());
        controlPanel.setHandler((RemovalControlHandler.ControlActionType actionType) -> {
            switch (actionType) {
                case OK: {
                    doc = docPanel.getDocumentation();
                    setDocLabel();
                    break;
                }
                case CANCEL: {
                    break;
                }
                case REMOVE: {
                    doc = "";
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
        XBCXHDocService hDocService = catalog.getCatalogService(XBCXHDocService.class);
        doc = hDocService.getDocumentationText(catalogItem);
        setDocLabel();
    }

    private void setDocLabel() {
        setPropertyText(doc == null || doc.isEmpty() ? "" : "[" + doc.length() + " bytes]");
    }

    public String getDocument() {
        return doc;
    }

    public XBACatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }
}
