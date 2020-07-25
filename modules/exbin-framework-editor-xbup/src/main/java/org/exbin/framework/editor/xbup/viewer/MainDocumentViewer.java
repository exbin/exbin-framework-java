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
package org.exbin.framework.editor.xbup.viewer;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.framework.editor.xbup.gui.BlockPropertiesPanel;
import org.exbin.framework.editor.xbup.gui.DocumentViewerPanel;
import org.exbin.framework.editor.xbup.gui.SimpleMessagePanel;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.core.block.declaration.XBBlockDecl;
import org.exbin.xbup.core.block.declaration.catalog.XBCBlockDecl;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCBlockRev;
import org.exbin.xbup.core.catalog.base.XBCXBlockPane;
import org.exbin.xbup.core.catalog.base.XBCXPlugPane;
import org.exbin.xbup.core.catalog.base.service.XBCXPaneService;
import org.exbin.xbup.parser_tree.XBTTreeNode;
import org.exbin.xbup.plugin.XBCatalogPlugin;
import org.exbin.xbup.plugin.XBComponentEditor;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * Custom viewer of document.
 *
 * @version 0.2.1 2020/07/23
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MainDocumentViewer implements DocumentViewer {

    private XBPluginRepository pluginRepository;

    private DocumentViewerPanel viewerPanel = new DocumentViewerPanel();
    private BlockPropertiesPanel propertiesPanel = new BlockPropertiesPanel();
    private JComponent customPanel = null;
    private XBTBlock selectedItem = null;
    private XBACatalog catalog;
    private ClipboardActionsUpdateListener updateListener;

    public MainDocumentViewer() {

//        customPanel = new JPanel();
//        customPanel.setBackground(Color.RED);
//        viewerPanel.addView("Test", customPanel);
        SimpleMessagePanel messagePanel = new SimpleMessagePanel();
        viewerPanel.setBorderComponent(messagePanel);
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        return viewerPanel;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        propertiesPanel.setCatalog(catalog);
    }

    public void setPluginRepository(XBPluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
    }

    @Override
    public void setSelectedItem(@Nullable XBTBlock block) {
        viewerPanel.removeAllViews();
        if (block != null) {
            XBCXPaneService paneService = catalog.getCatalogService(XBCXPaneService.class);
            XBBlockDecl decl = block instanceof XBTTreeNode ? ((XBTTreeNode) block).getBlockDecl() : null;
            if (decl instanceof XBCBlockDecl) {
                XBCBlockRev blockSpecRev = ((XBCBlockDecl) decl).getBlockSpecRev();

                XBCXBlockPane blockPane = paneService.findPaneByPR(blockSpecRev, 0);
                if (blockPane != null) {
                    XBCXPlugPane pane = blockPane.getPane();
                    Long paneIndex = pane.getPaneIndex();
                    //pane.getPlugin().getPluginFile();

                    try {
                        XBCatalogPlugin pluginHandler = pluginRepository.getPluginHandler(pane.getPlugin());
                        if (pluginHandler != null) {
                            XBComponentEditor panelEditor = pluginHandler.getComponentEditor(paneIndex);

                            if (panelEditor != null) {
                                viewerPanel.addView("Plugin " + String.valueOf(pane.getId()), panelEditor.getEditor());
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(MainDocumentViewer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            viewerPanel.addView("Information", propertiesPanel);
            propertiesPanel.setBlock(block);
        }
        selectedItem = block;
        viewerPanel.revalidate();
        viewerPanel.repaint();

    }

    @Override
    public void performCut() {
    }

    @Override
    public void performCopy() {
    }

    @Override
    public void performPaste() {
    }

    @Override
    public void performDelete() {
    }

    @Override
    public void performSelectAll() {
    }

    @Override
    public boolean isSelection() {
        return false;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean canSelectAll() {
        return false;
    }

    @Override
    public boolean canPaste() {
        return false;
    }

    @Override
    public boolean canDelete() {
        return false;
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        this.updateListener = updateListener;
    }
}
