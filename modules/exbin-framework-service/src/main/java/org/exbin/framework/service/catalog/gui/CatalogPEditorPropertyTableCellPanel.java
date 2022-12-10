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
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.utils.handler.DefaultControlHandler;
import org.exbin.xbup.catalog.XBECatalog;
import org.exbin.xbup.catalog.entity.XBEBlockRev;
import org.exbin.xbup.catalog.entity.XBEXBlockUi;
import org.exbin.xbup.catalog.entity.XBEXPlugUi;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.XBPlugUiType;
import org.exbin.xbup.core.catalog.base.XBCBlockRev;
import org.exbin.xbup.core.catalog.base.XBCBlockSpec;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.XBCXBlockUi;
import org.exbin.xbup.core.catalog.base.XBCXPlugUi;
import org.exbin.xbup.core.catalog.base.service.XBCRevService;
import org.exbin.xbup.core.catalog.base.service.XBCXUiService;

/**
 * Catalog panel editor property cell panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class CatalogPEditorPropertyTableCellPanel extends CatalogPropertyTableCellPanel {

    private XBApplication application;
    private XBACatalog catalog;
    private long paneId;
    private XBCBlockRev blockRev;
    private XBCXPlugUi plugUi;

    public CatalogPEditorPropertyTableCellPanel(XBACatalog catalog) {
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
        CatalogSelectComponentEditorPanel panelSelectPanel = new CatalogSelectComponentEditorPanel();
        panelSelectPanel.setApplication(application);
        panelSelectPanel.setCatalog(catalog);
        panelSelectPanel.setPlugUi(plugUi);
        DefaultControlPanel controlPanel = new DefaultControlPanel();
        final DialogWrapper dialog = frameModule.createDialog(panelSelectPanel, controlPanel);
//        frameModule.setDialogTitle(dialog, paneSelectPanel.getResourceBundle());
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            switch (actionType) {
                case OK: {
                    plugUi = panelSelectPanel.getPlugUi();

                    XBEXBlockUi blockPane = new XBEXBlockUi();
                    blockPane.setBlockRev((XBEBlockRev) blockRev);
                    blockPane.setUi((XBEXPlugUi) plugUi);
                    blockPane.setPriority(0L);

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
        XBCXUiService uiService = catalog.getCatalogService(XBCXUiService.class);
        XBCRevService revService = catalog.getCatalogService(XBCRevService.class);
        long maxRev = revService.findMaxRevXB((XBCBlockSpec) catalogItem);
        blockRev = (XBCBlockRev) revService.findRevByXB((XBCBlockSpec) catalogItem, maxRev);
        XBCXBlockUi blockUi = uiService.findUiByPR(blockRev, XBPlugUiType.PANEL_EDITOR, 0);
        plugUi = blockUi == null ? null : blockUi.getUi();
        paneId = blockUi == null ? 0 : blockUi.getId();

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
