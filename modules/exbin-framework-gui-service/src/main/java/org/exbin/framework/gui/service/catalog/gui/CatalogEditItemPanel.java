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

import java.awt.Container;
import java.util.ResourceBundle;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.action.api.MenuManagement;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.XBCNode;
import org.exbin.xbup.core.catalog.base.XBCSpec;

/**
 * XBManager catalog item editation panel.
 *
 * @version 0.2.1 2020/07/22
 * @author ExBin Project (http://exbin.org)
 */
public class CatalogEditItemPanel extends javax.swing.JPanel {

    private XBApplication application;
    private XBACatalog catalog;

    private CatalogItemEditPanel propertiesPanel;
    private CatalogItemEditRevsPanel revisionsPanel;
    private CatalogItemEditDefinitionPanel definitionPanel;
    private CatalogItemEditFilesPanel filesPanel;
    private CatalogItemEditPluginsPanel pluginsPanel;
    private MenuManagement menuManagement;
    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(CatalogEditItemPanel.class);

    public CatalogEditItemPanel() {
        initComponents();
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setCatalogItem(XBCItem item) {
        mainTabbedPane.removeAll();

        propertiesPanel = new CatalogItemEditPanel();
        propertiesPanel.setApplication(application);
        propertiesPanel.setCatalog(catalog);
        propertiesPanel.setCatalogItem(item);
        initComponent(propertiesPanel);
        mainTabbedPane.add(propertiesPanel, "Basic");

        if (item instanceof XBCSpec) {
            revisionsPanel = new CatalogItemEditRevsPanel();
            revisionsPanel.setApplication(application);
            revisionsPanel.setCatalog(catalog);
            revisionsPanel.setCatalogItem(item);
            initComponent(revisionsPanel);
            mainTabbedPane.add(revisionsPanel, "Revisions");

            definitionPanel = new CatalogItemEditDefinitionPanel();
            definitionPanel.setApplication(application);
            definitionPanel.setCatalog(catalog);
            definitionPanel.setCatalogItem(item);
            revisionsPanel.setDefsModel(definitionPanel.getDefsModel());
            initComponent(definitionPanel);
            mainTabbedPane.add(definitionPanel, "Definition");
        } else if (item instanceof XBCNode) {
            filesPanel = new CatalogItemEditFilesPanel();
            filesPanel.setApplication(application);
            filesPanel.setCatalog(catalog);
            filesPanel.setNode((XBCNode) item);
            if (menuManagement != null) {
                filesPanel.setMenuManagement(menuManagement);
            }

            initComponent(filesPanel);
            mainTabbedPane.add(filesPanel, "Files");

            pluginsPanel = new CatalogItemEditPluginsPanel();
            pluginsPanel.setApplication(application);
            pluginsPanel.setCatalog(catalog);
            pluginsPanel.setNode((XBCNode) item);
            if (menuManagement != null) {
                pluginsPanel.setMenuManagement(menuManagement);
            }

            initComponent(pluginsPanel);
            mainTabbedPane.add(pluginsPanel, "Plugins");
        }
    }

    public void setApplication(XBApplication application) {
        this.application = application;
        if (propertiesPanel != null) {
            propertiesPanel.setApplication(application);
        }
        if (revisionsPanel != null) {
            revisionsPanel.setApplication(application);
        }
        if (definitionPanel != null) {
            definitionPanel.setApplication(application);
        }
        if (filesPanel != null) {
            filesPanel.setApplication(application);
        }
    }

    public void persist() {
        propertiesPanel.persist();
        if (definitionPanel != null) {
            definitionPanel.persist();
        }
        if (revisionsPanel != null) {
            revisionsPanel.persist();
        }
        if (filesPanel != null) {
            filesPanel.persist();
        }
    }

    public void setMenuManagement(MenuManagement menuManagement) {
        this.menuManagement = menuManagement;

        if (filesPanel != null) {
            filesPanel.setMenuManagement(menuManagement);
        }
    }

    public XBCItem getCatalogItem() {
        return propertiesPanel.getCatalogItem();
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        if (propertiesPanel != null) {
            propertiesPanel.setCatalog(catalog);
        }
        if (revisionsPanel != null) {
            revisionsPanel.setCatalog(catalog);
        }
        if (definitionPanel != null) {
            definitionPanel.setCatalog(catalog);
        }
        if (filesPanel != null) {
            filesPanel.setCatalog(catalog);
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

        mainTabbedPane = new javax.swing.JTabbedPane();

        setLayout(new java.awt.BorderLayout());
        add(mainTabbedPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new CatalogEditItemPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane mainTabbedPane;
    // End of variables declaration//GEN-END:variables

    private void initComponent(Container container) {
        // TODO WindowUtils.assignGlobalKeyListener(container, setButton, cancelButton);
    }
}
