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
package org.exbin.framework.gui.service.catalog.gui;

import java.awt.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.gui.DefaultControlPanel;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.xbup.catalog.XBECatalog;
import org.exbin.xbup.catalog.entity.XBEBlockRev;
import org.exbin.xbup.catalog.entity.XBEXBlockPane;
import org.exbin.xbup.catalog.entity.XBEXPlugPane;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCBlockRev;
import org.exbin.xbup.core.catalog.base.XBCBlockSpec;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.XBCXBlockPane;
import org.exbin.xbup.core.catalog.base.XBCXPlugPane;
import org.exbin.xbup.core.catalog.base.service.XBCRevService;
import org.exbin.xbup.core.catalog.base.service.XBCXPaneService;

/**
 * Catalog component editor property cell panel.
 *
 * @version 0.2.1 2020/07/21
 * @author ExBin Project (http://exbin.org)
 */
public class CatalogCEditorPropertyTableCellPanel extends CatalogPropertyTableCellPanel {

    private XBApplication application;
    private XBACatalog catalog;
    private long paneId;
    private XBCBlockRev blockRev;
    private XBCXPlugPane plugPane;

    public CatalogCEditorPropertyTableCellPanel(XBACatalog catalog) {
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
        CatalogSelectComponentEditorPanel paneSelectPanel = new CatalogSelectComponentEditorPanel();
        paneSelectPanel.setApplication(application);
        paneSelectPanel.setCatalog(catalog);
        paneSelectPanel.setPlugPane(plugPane);
        DefaultControlPanel controlPanel = new DefaultControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(paneSelectPanel, controlPanel);
        final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
//        frameModule.setDialogTitle(dialog, paneSelectPanel.getResourceBundle());
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            switch (actionType) {
                case OK: {
                    plugPane = paneSelectPanel.getPlugPane();

                    XBEXBlockPane blockPane = new XBEXBlockPane();
                    blockPane.setBlockRev((XBEBlockRev) blockRev);
                    blockPane.setPane((XBEXPlugPane) plugPane);
                    blockPane.setPriority(0l);
                    
                    EntityManager em = ((XBECatalog) catalog).getEntityManager();
                    EntityTransaction transaction = em.getTransaction();
                    transaction.begin();
                    em.persist(blockPane);

                    em.flush();
                    transaction.commit();

                    paneId = blockPane.getId();
                    setPropertyLabel();
                    break;
                }
                case CANCEL: {
                    break;
                }
            }
            dialog.close();
        });
        dialog.showCentered(this);
        dialog.dispose();
    }

    public void setCatalogItem(XBCItem catalogItem) {
        XBCXPaneService paneService = catalog.getCatalogService(XBCXPaneService.class);
        XBCRevService revService = catalog.getCatalogService(XBCRevService.class);
        long maxRev = revService.findMaxRevXB((XBCBlockSpec) catalogItem);
        blockRev = (XBCBlockRev) revService.findRevByXB((XBCBlockSpec) catalogItem, maxRev);
        XBCXBlockPane blockPane = paneService.findPaneByPR(blockRev, 0);
        plugPane = blockPane == null ? null : blockPane.getPane();
        paneId = blockPane == null ? 0 : blockPane.getId();

        setPropertyLabel();
    }

    private void setPropertyLabel() {
        setPropertyText(paneId > 0 ? String.valueOf(paneId) : "");
    }

    public long getPaneId() {
        return paneId;
    }

    public XBACatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }
}
