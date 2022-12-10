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
package org.exbin.framework.service.catalog.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.service.catalog.gui.CatalogAddItemPanel;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.utils.handler.DefaultControlHandler;
import org.exbin.xbup.catalog.XBECatalog;
import org.exbin.xbup.catalog.entity.XBEItem;
import org.exbin.xbup.catalog.entity.XBENode;
import org.exbin.xbup.catalog.entity.XBESpec;
import org.exbin.xbup.catalog.entity.service.XBEXNameService;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.XBCNode;
import org.exbin.xbup.core.catalog.base.XBCSpec;
import org.exbin.xbup.core.catalog.base.service.XBCNodeService;
import org.exbin.xbup.core.catalog.base.service.XBCRevService;
import org.exbin.xbup.core.catalog.base.service.XBCSpecService;
import org.exbin.xbup.core.catalog.base.service.XBCXNameService;

/**
 * Add item to catalog action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddItemAction extends AbstractAction {

    private XBApplication application;
    private XBACatalog catalog;
    private XBCNodeService nodeService;
    private XBCSpecService specService;
    private XBCRevService revService;
    private XBCXNameService nameService;

    private Component parentComponent;
    private XBCItem currentItem;
    private XBCItem resultItem;

    public AddItemAction() {
    }

    @Nullable
    public XBCItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(@Nullable XBCItem currentItem) {
        this.currentItem = currentItem;
    }

    @Nullable
    public XBCItem getResultItem() {
        return resultItem;
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    @Override
    public void actionPerformed(@Nullable ActionEvent event) {
        resultItem = null;
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        final CatalogAddItemPanel panel = new CatalogAddItemPanel();
        DefaultControlPanel controlPanel = new DefaultControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(panel, controlPanel);
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            switch (actionType) {
                case OK: {
                    // TODO: Use different transaction management later
                    EntityManager em = ((XBECatalog) catalog).getEntityManager();
                    EntityTransaction transaction = em.getTransaction();
                    transaction.begin();

                    XBENode node = (XBENode) (currentItem instanceof XBCNode ? currentItem : currentItem.getParentItem().orElse(null));
                    XBEItem item = null;
                    switch (panel.getItemType()) {
                        case NODE: {
                            item = (XBENode) nodeService.createItem();
                            Long newXbIndex = nodeService.findMaxSubNodeXB(node);
                            item.setXBIndex(newXbIndex == null ? 0 : newXbIndex + 1);
                            break;
                        }
                        case BLOCK: {
                            item = (XBESpec) specService.createBlockSpec();
                            Long newXbIndex = specService.findMaxBlockSpecXB(node);
                            item.setXBIndex(newXbIndex == null ? 0 : newXbIndex + 1);
                            break;
                        }
                        case GROUP: {
                            item = (XBESpec) specService.createGroupSpec();
                            Long newXbIndex = specService.findMaxBlockSpecXB(node);
                            item.setXBIndex(newXbIndex == null ? 0 : newXbIndex + 1);
                            break;
                        }
                        case FORMAT: {
                            item = (XBESpec) specService.createFormatSpec();
                            Long newXbIndex = specService.findMaxBlockSpecXB(node);
                            item.setXBIndex(newXbIndex == null ? 0 : newXbIndex + 1);
                            break;
                        }
                        default: {
                            throw new IllegalStateException();
                        }
                    }

                    if (item == null) {
                        throw new IllegalStateException();
                    }
                    item.setParentItem(node);
                    if (item instanceof XBCNode) {
                        nodeService.persistItem((XBCNode) item);
                    } else {
                        specService.persistItem((XBCSpec) item);
                    }
                    ((XBEXNameService) nameService).setDefaultText(item, panel.getItemName());
                    em.flush();
                    transaction.commit();

                    resultItem = item;
                    break;
                }
                case CANCEL: {
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected action type " + actionType.name());
            }
            dialog.close();
        });
        dialog.showCentered(parentComponent);
        dialog.dispose();
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;

        nodeService = catalog == null ? null : catalog.getCatalogService(XBCNodeService.class);
        specService = catalog == null ? null : catalog.getCatalogService(XBCSpecService.class);
        revService = catalog == null ? null : catalog.getCatalogService(XBCRevService.class);
        nameService = catalog == null ? null : catalog.getCatalogService(XBCXNameService.class);
    }
}
