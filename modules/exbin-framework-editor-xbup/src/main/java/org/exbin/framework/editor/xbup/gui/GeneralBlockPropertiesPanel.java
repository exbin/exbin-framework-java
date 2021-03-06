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
package org.exbin.framework.editor.xbup.gui;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.service.catalog.gui.CatalogItemPanel;
import org.exbin.framework.gui.utils.BareBonesBrowserLaunch;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;
import org.exbin.xbup.core.block.XBBlockDataMode;
import org.exbin.xbup.core.block.XBBlockTerminationMode;
import org.exbin.xbup.core.block.XBBlockType;
import org.exbin.xbup.core.block.XBFBlockType;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.core.block.declaration.XBBlockDecl;
import org.exbin.xbup.core.block.declaration.catalog.XBCBlockDecl;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCBlockSpec;
import org.exbin.xbup.core.catalog.base.XBCXIcon;
import org.exbin.xbup.core.catalog.base.service.XBCSpecService;
import org.exbin.xbup.core.catalog.base.service.XBCXIconService;
import org.exbin.xbup.core.catalog.base.service.XBCXNameService;
import org.exbin.xbup.parser_tree.XBTTreeNode;

/**
 * General block properties panel.
 *
 * @version 0.2.1 2020/09/22
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GeneralBlockPropertiesPanel extends javax.swing.JPanel {

    private boolean devMode = false;
    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(GeneralBlockPropertiesPanel.class);
    private XBACatalog catalog;
    private XBTBlock block;
    private XBApplication application;

    public GeneralBlockPropertiesPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        linkPopupMenu = new javax.swing.JPopupMenu();
        copyLinkMenuItem = new javax.swing.JMenuItem();
        iconPanel = new javax.swing.JPanel();
        itemIconLabel = new javax.swing.JLabel();
        itemTitleLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        dataModeLabel = new javax.swing.JLabel();
        dataModeTextField = new javax.swing.JTextField();
        terminationModeLabel = new javax.swing.JLabel();
        terminationModeTextField = new javax.swing.JTextField();
        nodeSizeLabel = new javax.swing.JLabel();
        nodeSizeTextField = new javax.swing.JTextField();
        attributesCountLabel = new javax.swing.JLabel();
        attributesCountTextField = new javax.swing.JTextField();
        childrenCountLabel = new javax.swing.JLabel();
        childrenCountTextField = new javax.swing.JTextField();
        webCatalogLabel = new javax.swing.JLabel();
        webCatalogLinkScrollPane = new javax.swing.JScrollPane();
        webCatalogLinkLabel = new javax.swing.JLabel();
        webCatalogLinkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        catalogButton = new javax.swing.JButton();

        copyLinkMenuItem.setText(resourceBundle.getString("copyLinkMenuItem.text")); // NOI18N
        copyLinkMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyLinkMenuItemActionPerformed(evt);
            }
        });
        linkPopupMenu.add(copyLinkMenuItem);

        iconPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        iconPanel.setLayout(new java.awt.BorderLayout());

        itemIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/gui/service/resources/images/empty.png"))); // NOI18N
        iconPanel.add(itemIconLabel, java.awt.BorderLayout.CENTER);

        itemTitleLabel.setText("Unknown item");

        dataModeLabel.setText(resourceBundle.getString("dataModeLabel.text")); // NOI18N

        dataModeTextField.setEditable(false);

        terminationModeLabel.setText(resourceBundle.getString("terminationModeLabel.text")); // NOI18N

        terminationModeTextField.setEditable(false);

        nodeSizeLabel.setText(resourceBundle.getString("nodeSizeLabel.text")); // NOI18N

        nodeSizeTextField.setEditable(false);

        attributesCountLabel.setText(resourceBundle.getString("attributesCountLabel.text")); // NOI18N

        attributesCountTextField.setEditable(false);

        childrenCountLabel.setText(resourceBundle.getString("childrenCountLabel.text")); // NOI18N

        childrenCountTextField.setEditable(false);

        webCatalogLabel.setText(resourceBundle.getString("webCatalogLabell.text")); // NOI18N

        webCatalogLinkScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        webCatalogLinkScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        webCatalogLinkLabel.setForeground(java.awt.Color.blue);
        webCatalogLinkLabel.setText(resourceBundle.getString("webCatalogLabel.text")); // NOI18N
        HashMap<TextAttribute, Object> attribs = new HashMap<TextAttribute, Object>();
        attribs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        webCatalogLinkLabel.setFont(webCatalogLinkLabel.getFont().deriveFont(attribs));
        webCatalogLinkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                webCatalogLinkLabelMouseClicked(evt);
            }
        });
        webCatalogLinkScrollPane.setViewportView(webCatalogLinkLabel);

        catalogButton.setText("Catalog...");
        catalogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                catalogButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(terminationModeLabel)
                            .addComponent(nodeSizeLabel)
                            .addComponent(attributesCountLabel)
                            .addComponent(childrenCountLabel)
                            .addComponent(webCatalogLabel)
                            .addComponent(dataModeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dataModeTextField)
                            .addComponent(childrenCountTextField)
                            .addComponent(attributesCountTextField)
                            .addComponent(nodeSizeTextField)
                            .addComponent(terminationModeTextField)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(webCatalogLinkScrollPane)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(catalogButton))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(iconPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(itemTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(itemTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(iconPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dataModeLabel)
                    .addComponent(dataModeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(terminationModeLabel)
                    .addComponent(terminationModeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nodeSizeLabel)
                    .addComponent(nodeSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(attributesCountLabel)
                    .addComponent(attributesCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(childrenCountLabel)
                    .addComponent(childrenCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(webCatalogLabel)
                    .addComponent(webCatalogLinkScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(catalogButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public void setBlock(XBTBlock block) {
        this.block = block;
        XBCSpecService specService = catalog.getCatalogService(XBCSpecService.class);
        XBCXIconService iconService = catalog.getCatalogService(XBCXIconService.class);

        itemTitleLabel.setText(getCaption(block));
        dataModeTextField.setText(block.getDataMode() == XBBlockDataMode.DATA_BLOCK ? "DATA_BLOCK" : "NODE_BLOCK");
        terminationModeTextField.setText(block.getTerminationMode() == XBBlockTerminationMode.TERMINATED_BY_ZERO ? "TERMINATED_BY_ZERO" : "SIZE_SPECIFIED");
        nodeSizeTextField.setText(block instanceof XBTTreeNode ? Integer.toString(((XBTTreeNode) block).getSizeUB()) : "Unknown");
        attributesCountTextField.setText(String.valueOf(block.getAttributesCount()));
        childrenCountTextField.setText(String.valueOf(block.getChildrenCount()));

        String catalogLink = devMode ? "https://catalog-dev.exbin.org/" : "https://catalog.exbin.org/";
        XBBlockDecl decl = block instanceof XBTTreeNode ? ((XBTTreeNode) block).getBlockDecl() : null;
        if (decl instanceof XBCBlockDecl) {
            XBCBlockSpec spec = ((XBCBlockDecl) decl).getBlockSpecRev().getParent();

            XBCXIcon itemIcon = iconService.getDefaultIcon(spec);
            if (itemIcon != null) {
                ImageIcon icon = iconService.getDefaultImageIcon(spec);
                itemIconLabel.setIcon(icon);
            } else {
                itemIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/gui/service/resources/images/empty.png")));
            }

            Long[] path = specService.getSpecXBPath(spec);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < path.length; i++) {
                if (i > 0) {
                    builder.append("/");
                }
                builder.append(path[i]);
            }
            catalogLink += "?block=" + builder.toString();
        } else if (block.getDataMode() == XBBlockDataMode.DATA_BLOCK) {
            itemIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/editor/xbup/resources/icons/data-block.png")));
        } else {
            itemIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/gui/service/resources/images/empty.png")));
        }

        webCatalogLinkLabel.setText(catalogLink);
        webCatalogLinkLabel.setToolTipText("Link to: " + catalogLink);
    }

    private void webCatalogLinkLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_webCatalogLinkLabelMouseClicked
        if (!evt.isPopupTrigger()) {
            String targetURL = ((JLabel) evt.getSource()).getText();
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                try {
                    java.net.URI uri = new java.net.URI(targetURL);
                    desktop.browse(uri);
                } catch (IOException | URISyntaxException ex) {
                    Logger.getLogger(GeneralBlockPropertiesPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                BareBonesBrowserLaunch.openURL(targetURL);
            }
        }
    }//GEN-LAST:event_webCatalogLinkLabelMouseClicked

    private void copyLinkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyLinkMenuItemActionPerformed
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(webCatalogLinkLabel.getText()), null);
    }//GEN-LAST:event_copyLinkMenuItemActionPerformed

    private void catalogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_catalogButtonActionPerformed
        CatalogItemPanel itemPanel = new CatalogItemPanel();
        itemPanel.setCatalog(catalog);

        XBBlockDecl decl = block instanceof XBTTreeNode ? ((XBTTreeNode) block).getBlockDecl() : null;
        if (decl instanceof XBCBlockDecl) {
            XBCBlockSpec spec = ((XBCBlockDecl) decl).getBlockSpecRev().getParent();
            itemPanel.setItem(spec);
        }

        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        CloseControlPanel controlPanel = new CloseControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(itemPanel, controlPanel);
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        controlPanel.setHandler(() -> {
            dialog.close();
            dialog.dispose();
        });

        dialog.showCentered(this);
    }//GEN-LAST:event_catalogButtonActionPerformed

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new GeneralBlockPropertiesPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel attributesCountLabel;
    private javax.swing.JTextField attributesCountTextField;
    private javax.swing.JButton catalogButton;
    private javax.swing.JLabel childrenCountLabel;
    private javax.swing.JTextField childrenCountTextField;
    private javax.swing.JMenuItem copyLinkMenuItem;
    private javax.swing.JLabel dataModeLabel;
    private javax.swing.JTextField dataModeTextField;
    private javax.swing.JPanel iconPanel;
    private javax.swing.JLabel itemIconLabel;
    private javax.swing.JLabel itemTitleLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu linkPopupMenu;
    private javax.swing.JLabel nodeSizeLabel;
    private javax.swing.JTextField nodeSizeTextField;
    private javax.swing.JLabel terminationModeLabel;
    private javax.swing.JTextField terminationModeTextField;
    private javax.swing.JLabel webCatalogLabel;
    private javax.swing.JLabel webCatalogLinkLabel;
    private javax.swing.JScrollPane webCatalogLinkScrollPane;
    // End of variables declaration//GEN-END:variables

    @Nonnull
    public String getCaption(XBTBlock node) {
        if (node.getDataMode() == XBBlockDataMode.DATA_BLOCK) {
            return resourceBundle.getString("node_caption_data");
        }

        XBBlockType blockType = node.getBlockType();
        if (catalog != null) {
            XBCXNameService nameService = catalog.getCatalogService(XBCXNameService.class);

            XBCBlockDecl blockDecl = node instanceof XBTTreeNode ? (XBCBlockDecl) ((XBTTreeNode) node).getBlockDecl() : null;
            if (blockDecl == null) {
                return resourceBundle.getString("node_caption_undefined");
            }
            XBCBlockSpec blockSpec = blockDecl.getBlockSpecRev().getParent();
            return nameService.getDefaultText(blockSpec);
        }

        return resourceBundle.getString("node_caption_unknown") + " (" + Integer.toString(((XBFBlockType) blockType).getGroupID().getInt()) + ", " + Integer.toString(((XBFBlockType) blockType).getBlockID().getInt()) + ")";
    }
}
