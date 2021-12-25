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
package org.exbin.framework.gui.service.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.EntityManagerFactory;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.action.api.MenuManagement;
import org.exbin.framework.gui.service.XBDbServiceClient;
import org.exbin.framework.gui.service.catalog.action.AddItemAction;
import org.exbin.framework.gui.service.catalog.action.EditItemAction;
import org.exbin.framework.gui.service.catalog.action.ExportItemAction;
import org.exbin.framework.gui.service.catalog.action.ImportItemAction;
import org.exbin.framework.gui.service.catalog.gui.CatalogBrowserPanel;
import org.exbin.framework.gui.service.catalog.gui.CatalogEditorPanel;
import org.exbin.framework.gui.service.catalog.gui.CatalogSearchPanel;
import org.exbin.framework.gui.service.catalog.gui.CatalogStatusPanel;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.xbup.catalog.XBAECatalog;
import org.exbin.xbup.catalog.entity.service.XBEXDescService;
import org.exbin.xbup.catalog.entity.service.XBEXFileService;
import org.exbin.xbup.catalog.entity.service.XBEXHDocService;
import org.exbin.xbup.catalog.entity.service.XBEXIconService;
import org.exbin.xbup.catalog.entity.service.XBEXLangService;
import org.exbin.xbup.catalog.entity.service.XBEXUiService;
import org.exbin.xbup.catalog.entity.service.XBEXNameService;
import org.exbin.xbup.catalog.entity.service.XBEXPlugService;
import org.exbin.xbup.catalog.entity.service.XBEXStriService;
import org.exbin.xbup.client.XBCatalogServiceClient;
import org.exbin.xbup.client.catalog.XBARCatalog;
import org.exbin.xbup.client.catalog.remote.service.XBRXDescService;
import org.exbin.xbup.client.catalog.remote.service.XBRXFileService;
import org.exbin.xbup.client.catalog.remote.service.XBRXHDocService;
import org.exbin.xbup.client.catalog.remote.service.XBRXIconService;
import org.exbin.xbup.client.catalog.remote.service.XBRXLangService;
import org.exbin.xbup.client.catalog.remote.service.XBRXUiService;
import org.exbin.xbup.client.catalog.remote.service.XBRXNameService;
import org.exbin.xbup.client.catalog.remote.service.XBRXPlugService;
import org.exbin.xbup.client.catalog.remote.service.XBRXStriService;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.XBCNode;
import org.exbin.xbup.core.catalog.base.service.XBCXDescService;
import org.exbin.xbup.core.catalog.base.service.XBCXFileService;
import org.exbin.xbup.core.catalog.base.service.XBCXHDocService;
import org.exbin.xbup.core.catalog.base.service.XBCXIconService;
import org.exbin.xbup.core.catalog.base.service.XBCXLangService;
import org.exbin.xbup.core.catalog.base.service.XBCXUiService;
import org.exbin.xbup.core.catalog.base.service.XBCXNameService;
import org.exbin.xbup.core.catalog.base.service.XBCXPlugService;
import org.exbin.xbup.core.catalog.base.service.XBCXStriService;

/**
 * XBManager service management panel.
 *
 * @version 0.2.1 2019/06/28
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ServiceManagerPanel extends javax.swing.JPanel {

    private XBApplication application;
    private XBCatalogServiceClient service;
    private final CatalogStatusPanel catalogStatusPanel;
    private final CatalogAvailabilityPanel catalogAvailabilityPanel;
    private final CatalogBrowserPanel catalogBrowserPanel;
    private final CatalogEditorPanel catalogEditorPanel;
    private final CatalogSearchPanel catalogSearchPanel;
    private String currentPanelCode;

    private final ServiceInfoPanel serviceInfoPanel;
    private final ServiceStartupPanel serviceStartupPanel;
    private final ServiceControlPanel serviceControlPanel;

    private final Map<String, Component> panelMap = new HashMap<>();
    private XBACatalog catalog = null;
    private MenuManagement menuManagement;

    public ServiceManagerPanel() {
        initComponents();

        serviceInfoPanel = new ServiceInfoPanel();
        serviceStartupPanel = new ServiceStartupPanel();
        serviceControlPanel = new ServiceControlPanel();

        serviceInfoScrollPane.setViewportView(serviceInfoPanel);
        panelMap.put("info", serviceInfoPanel);
        panelMap.put("startup", serviceStartupPanel);
        panelMap.put("empty", emptyPanel);
        panelMap.put("control", serviceControlPanel);

        catalogStatusPanel = new CatalogStatusPanel();
        catalogAvailabilityPanel = new CatalogAvailabilityPanel();
        catalogBrowserPanel = new CatalogBrowserPanel();
        catalogEditorPanel = new CatalogEditorPanel();
        catalogEditorPanel.setControl(new CatalogEditorPanel.Control() {
            @Override
            public void exportItem(Component parentComponent, XBCItem currentItem) {
                ExportItemAction exportItemAction = new ExportItemAction();
                exportItemAction.setCatalog(catalog);
                exportItemAction.setParentComponent(parentComponent);
                exportItemAction.setCurrentItem(currentItem);
                exportItemAction.actionPerformed(null);
            }

            @Override
            public void importItem(Component parentComponent, XBCItem currentItem) {
                ImportItemAction importItemAction = new ImportItemAction();
                importItemAction.setCatalog(catalog);
                importItemAction.setParentComponent(parentComponent);
                importItemAction.setCurrentItem(currentItem);
                importItemAction.actionPerformed(null);
            }

            @Override
            public void addItem(Component parentComponent, XBCItem currentItem) {
                AddItemAction addItemAction = new AddItemAction();
                addItemAction.setApplication(application);
                addItemAction.setCatalog(catalog);
                addItemAction.setParentComponent(parentComponent);
                addItemAction.setCurrentItem(currentItem);
                addItemAction.actionPerformed(null);
                XBCItem resultItem = addItemAction.getResultItem();
                if (resultItem != null) {
                    catalogEditorPanel.reloadNodesTree();
                    catalogEditorPanel.setNode(resultItem instanceof XBCNode ? (XBCNode) resultItem : catalogEditorPanel.getSpecsNode());
                    catalogEditorPanel.selectSpecTableRow(resultItem);
                }
            }

            @Override
            public void editItem(Component parentComponent, XBCItem currentItem) {
                EditItemAction editItemAction = new EditItemAction();
                editItemAction.setApplication(application);
                editItemAction.setCatalog(catalog);
                editItemAction.setMenuManagement(menuManagement);
                editItemAction.setParentComponent(parentComponent);
                editItemAction.setCurrentItem(currentItem);
                editItemAction.actionPerformed(null);
                XBCItem resultItem = editItemAction.getResultItem();
                if (resultItem != null) {
                    catalogEditorPanel.setItem(currentItem);
                    catalogEditorPanel.setSpecsNode(catalogEditorPanel.getSpecsNode());
                    catalogEditorPanel.selectSpecTableRow(currentItem);
                }
            }
        });
        catalogSearchPanel = new CatalogSearchPanel();

        panelMap.put("catalog", catalogStatusPanel);
        panelMap.put("catalog_availability", catalogAvailabilityPanel);
        panelMap.put("catalog_browse", catalogBrowserPanel);
        panelMap.put("catalog_editor", catalogEditorPanel);
        panelMap.put("catalog_search", catalogSearchPanel);
        panelMap.put("update", new CatalogUpdateManagerPanel());
        panelMap.put("transplug", new TransformationPluginsManagerPanel());
        managerTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        MutableTreeNode top = new MutableTreeNode("Root", "");
        createNodes(top);
        managerTree.setModel(new DefaultTreeModel(top, true));
        managerTree.getSelectionModel().addTreeSelectionListener(new MySelectionListener());
        managerTree.setSelectionRow(0);
    }

    public void setApplication(XBApplication application) {
        this.application = application;
        catalogBrowserPanel.setApplication(application);
        catalogEditorPanel.setApplication(application);
        catalogSearchPanel.setApplication(application);
    }

    private void createNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode item = new MutableTreeNode("Server Information", "info");
//        item.add(new MutableTreeNode("Connection","empty"));
        top.add(item);
        item = new MutableTreeNode("Service Control", "control");
        top.add(item);
        item = new MutableTreeNode("Service Startup", "startup");
        top.add(item);
        item = new MutableTreeNode("Catalog", "catalog");
        item.add(new MutableTreeNode("Browse", "catalog_browse"));
        item.add(new MutableTreeNode("Editor", "catalog_editor"));
        item.add(new MutableTreeNode("Search", "catalog_search"));
        item.add(new MutableTreeNode("Update Service", "update"));
        top.add(item);
        item = new MutableTreeNode("Plugin", "empty");
        item.add(new MutableTreeNode("Transformations", "transplug"));
        top.add(item);
    }

    public XBCatalogServiceClient getService() {
        return service;
    }

    public void setService(XBCatalogServiceClient service) {
        this.service = service;
        if (service == null) {
            serviceInfoPanel.setService(service);
            return;
        }

        XBACatalog connectedCatalog = null;
        if (service instanceof XBDbServiceClient) {
            serviceInfoPanel.setService(service);
            EntityManagerFactory emf = ((XBDbServiceClient) service).getEntityManagerFactory();
            try {
                connectedCatalog = new XBAECatalog(emf.createEntityManager()); // TODO: Kill on failure
                if (((XBAECatalog) connectedCatalog).isShallInit()) {
                    ((XBAECatalog) connectedCatalog).initCatalog();
                }
                ((XBAECatalog) connectedCatalog).addCatalogService(XBCXLangService.class, new XBEXLangService((XBAECatalog) connectedCatalog));
                ((XBAECatalog) connectedCatalog).addCatalogService(XBCXStriService.class, new XBEXStriService((XBAECatalog) connectedCatalog));
                ((XBAECatalog) connectedCatalog).addCatalogService(XBCXNameService.class, new XBEXNameService((XBAECatalog) connectedCatalog));
                ((XBAECatalog) connectedCatalog).addCatalogService(XBCXDescService.class, new XBEXDescService((XBAECatalog) connectedCatalog));

                ((XBAECatalog) connectedCatalog).addCatalogService(XBCXFileService.class, new XBEXFileService((XBAECatalog) connectedCatalog));
                ((XBAECatalog) connectedCatalog).addCatalogService(XBCXIconService.class, new XBEXIconService((XBAECatalog) connectedCatalog));
                ((XBAECatalog) connectedCatalog).addCatalogService(XBCXPlugService.class, new XBEXPlugService((XBAECatalog) connectedCatalog));
                ((XBAECatalog) connectedCatalog).addCatalogService(XBCXUiService.class, new XBEXUiService((XBAECatalog) connectedCatalog));
                ((XBAECatalog) connectedCatalog).addCatalogService(XBCXHDocService.class, new XBEXHDocService((XBAECatalog) connectedCatalog));
            } catch (Exception ex) {
                Logger.getLogger(ServiceManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                connectedCatalog = null;
            }
        } else if (service.validate()) {
            serviceInfoPanel.setService(service);
            serviceControlPanel.setStopButtonEnabled(true);

            connectedCatalog = new XBARCatalog(service);
            ((XBARCatalog) connectedCatalog).addCatalogService(XBCXLangService.class, new XBRXLangService((XBARCatalog) connectedCatalog));
            ((XBARCatalog) connectedCatalog).addCatalogService(XBCXStriService.class, new XBRXStriService((XBARCatalog) connectedCatalog));
            ((XBARCatalog) connectedCatalog).addCatalogService(XBCXNameService.class, new XBRXNameService((XBARCatalog) connectedCatalog));
            ((XBARCatalog) connectedCatalog).addCatalogService(XBCXDescService.class, new XBRXDescService((XBARCatalog) connectedCatalog));
            ((XBARCatalog) connectedCatalog).addCatalogService(XBCXFileService.class, new XBRXFileService((XBARCatalog) connectedCatalog));
            ((XBARCatalog) connectedCatalog).addCatalogService(XBCXIconService.class, new XBRXIconService((XBARCatalog) connectedCatalog));
            ((XBARCatalog) connectedCatalog).addCatalogService(XBCXPlugService.class, new XBRXPlugService((XBARCatalog) connectedCatalog));
            ((XBARCatalog) connectedCatalog).addCatalogService(XBCXUiService.class, new XBRXUiService((XBARCatalog) connectedCatalog));
            ((XBARCatalog) connectedCatalog).addCatalogService(XBCXHDocService.class, new XBRXHDocService((XBARCatalog) connectedCatalog));
        }

        setCatalog(connectedCatalog);
    }

//    public boolean updateActionStatus(Component component) {
//        // String test = ((CardLayout) cardPanel.getLayout()).toString();
//        // if ("catalog".equals(test)) {
//        catalogEditorPanel.updateActionStatus(component);
//        // }
//
//        return false;
//    }
//    public void releaseActionStatus() {
//        // if ("catalog".equals(((CardLayout) cardPanel.getLayout()).toString())) {
//        catalogEditorPanel.releaseActionStatus();
//        // }
//    }
//    public boolean performAction(String eventName, ActionEvent event) {
//        // if ("catalog".equals(((CardLayout) cardPanel.getLayout()).toString())) {
//        return catalogEditorPanel.performAction(eventName, event);
//        // }
//
//        // return false;
//    }
    public void setMenuManagement(MenuManagement menuManagement) {
        this.menuManagement = menuManagement;
        catalogBrowserPanel.setMenuManagement(menuManagement);
        catalogEditorPanel.setMenuManagement(menuManagement);
        catalogSearchPanel.setMenuManagement(menuManagement);
    }

//    public void setMainFrameManagement(MainFrameManagement mainFramenManagement) {
//        catalogBrowserPanel.setMainFrameManagement(mainFramenManagement);
//        catalogEditorPanel.setMainFrameManagement(mainFramenManagement);
//        catalogSearchPanel.setMainFrameManagement(mainFramenManagement);
//    }
    public void setCatalog(XBACatalog catalog) {
        catalogAvailabilityPanel.setCatalog(catalog);
        catalogStatusPanel.setCatalog(catalog);
        catalogBrowserPanel.setCatalog(catalog);
        catalogEditorPanel.setCatalog(catalog);
        catalogSearchPanel.setCatalog(catalog);

        if (this.catalog == null && catalog != null) {
            Component panel = panelMap.get(currentPanelCode);
            serviceInfoScrollPane.setViewportView(panel);
            panel.revalidate();
        }

        this.catalog = catalog;
    }

    private class MutableTreeNode extends DefaultMutableTreeNode {

        private final String caption;

        public MutableTreeNode(Object userObject, String caption) {
            super(userObject);
            this.caption = caption;
        }

        public String getCaption() {
            return caption;
        }
    }

    private class MySelectionListener implements TreeSelectionListener {

        private TreePath lastPath;

        public MySelectionListener() {
            super();
            lastPath = null;
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            TreePath itemPath = e.getPath();
            if (lastPath != null) { // Colapse last relevant node
                if (!lastPath.equals(itemPath)) {
                    if (itemPath.getPathCount() <= lastPath.getPathCount()) {
                        ArrayList<Object> collapsePath = new ArrayList<>();
                        int i;
                        for (i = 0; i < itemPath.getPathCount(); i++) {
                            if (!itemPath.getPathComponent(i).equals(lastPath.getPathComponent(i))) {
                                break;
                            }
                            collapsePath.add(itemPath.getPathComponent(i));
                        }
                        if (i <= itemPath.getPathCount()) {
                            collapsePath.add(lastPath.getPathComponent(i));
                            managerTree.collapsePath(new TreePath(collapsePath.toArray()));
                        }
                    }
                }
            }
            if (itemPath != null) {
                lastPath = itemPath;
                managerTree.expandPath(itemPath);
                String panelStringCode = ((MutableTreeNode) managerTree.getLastSelectedPathComponent()).getCaption();
                if (panelStringCode != null) {
                    currentPanelCode = panelStringCode;
                    Component panel = panelMap.get(panelStringCode);
                    if (panel instanceof CatalogManagementAware && catalog == null) {
                        panel = panelMap.get("catalog_availability");

                    }

                    serviceInfoScrollPane.setViewportView(panel);
                    panel.revalidate();
                } else {
                    serviceInfoScrollPane.setViewportView(panelMap.get("empty"));
                }
                serviceInfoScrollPane.revalidate();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        emptyPanel = new javax.swing.JPanel();
        mainSplitPane = new javax.swing.JSplitPane();
        managerTreeScrollPane = new javax.swing.JScrollPane();
        managerTree = new javax.swing.JTree();
        serviceInfoScrollPane = new javax.swing.JScrollPane();

        emptyPanel.setEnabled(false);
        emptyPanel.setName("emptyPanel"); // NOI18N
        emptyPanel.setLayout(new java.awt.BorderLayout());

        mainSplitPane.setDividerLocation(150);
        mainSplitPane.setName("mainSplitPane"); // NOI18N

        managerTreeScrollPane.setName("managerTreeScrollPane"); // NOI18N

        managerTree.setName("managerTree"); // NOI18N
        managerTree.setRootVisible(false);
        managerTreeScrollPane.setViewportView(managerTree);

        mainSplitPane.setLeftComponent(managerTreeScrollPane);

        serviceInfoScrollPane.setName("serviceInfoScrollPane"); // NOI18N
        mainSplitPane.setRightComponent(serviceInfoScrollPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 314, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(mainSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(mainSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new ServiceManagerPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel emptyPanel;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JTree managerTree;
    private javax.swing.JScrollPane managerTreeScrollPane;
    private javax.swing.JScrollPane serviceInfoScrollPane;
    // End of variables declaration//GEN-END:variables
}
