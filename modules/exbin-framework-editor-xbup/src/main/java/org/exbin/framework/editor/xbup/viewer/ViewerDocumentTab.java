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

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.editor.xbup.gui.BlockDefinitionPanel;
import org.exbin.framework.editor.xbup.gui.DocumentViewerPanel;
import org.exbin.framework.editor.xbup.gui.ModifyBlockPanel;
import org.exbin.framework.editor.xbup.gui.SimpleMessagePanel;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.core.block.XBBlockDataMode;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.core.block.declaration.XBBlockDecl;
import org.exbin.xbup.core.block.declaration.catalog.XBCBlockDecl;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.XBPlugUiType;
import org.exbin.xbup.core.catalog.base.XBCBlockRev;
import org.exbin.xbup.core.catalog.base.XBCXBlockUi;
import org.exbin.xbup.core.catalog.base.XBCXPlugUi;
import org.exbin.xbup.core.catalog.base.service.XBCXUiService;
import org.exbin.xbup.core.parser.XBProcessingException;
import org.exbin.xbup.core.parser.token.pull.convert.XBTProviderToPullProvider;
import org.exbin.xbup.core.serial.XBPSerialReader;
import org.exbin.xbup.core.serial.XBSerializable;
import org.exbin.xbup.core.util.StreamUtils;
import org.exbin.xbup.parser_tree.XBTTreeNode;
import org.exbin.xbup.parser_tree.XBTTreeWriter;
import org.exbin.xbup.plugin.XBCatalogPlugin;
import org.exbin.xbup.plugin.XBComponentViewer;
import org.exbin.xbup.plugin.XBComponentViewerCatalogPlugin;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * Custom viewer of document.
 *
 * @version 0.2.1 2020/09/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ViewerDocumentTab implements DocumentTab {

    private XBPluginRepository pluginRepository;

    private DocumentViewerPanel viewerPanel = new DocumentViewerPanel();
    private final BlockDefinitionPanel definitionPanel = new BlockDefinitionPanel();
    private final BinEdComponentPanel dataPanel = new BinEdComponentPanel();
    private XBTBlock selectedItem = null;
    private XBACatalog catalog;
    private ClipboardActionsUpdateListener updateListener;

    public ViewerDocumentTab() {
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
        definitionPanel.setCatalog(catalog);
    }

    public void setPluginRepository(XBPluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
        definitionPanel.setPluginRepository(pluginRepository);
    }

    public void setApplication(XBApplication application) {
        definitionPanel.setApplication(application);
    }

    @Override
    public void setSelectedItem(@Nullable XBTBlock block) {
        viewerPanel.removeAllViews();
        if (block != null) {
            XBCXUiService uiService = catalog.getCatalogService(XBCXUiService.class);
            XBBlockDecl decl = block instanceof XBTTreeNode ? ((XBTTreeNode) block).getBlockDecl() : null;
            if (decl instanceof XBCBlockDecl) {
                XBCBlockRev blockSpecRev = ((XBCBlockDecl) decl).getBlockSpecRev();

                XBCXBlockUi blockUi = uiService.findUiByPR(blockSpecRev, XBPlugUiType.PANEL_VIEWER, 0);
                if (blockUi != null) {
                    XBCXPlugUi plugUi = blockUi.getUi();
                    Long methodIndex = plugUi.getMethodIndex();
                    //pane.getPlugin().getPluginFile();

                    try {
                        XBCatalogPlugin pluginHandler = pluginRepository.getPluginHandler(plugUi.getPlugin());
                        if (pluginHandler != null) {
                            XBComponentViewer panelViewer = ((XBComponentViewerCatalogPlugin) pluginHandler).getComponentViewer(methodIndex);
                            reloadCustomEditor(panelViewer, block);
                            viewerPanel.addView("Plugin " + String.valueOf(plugUi.getId()), panelViewer.getViewer());
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(ViewerDocumentTab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            if (block.getDataMode() == XBBlockDataMode.DATA_BLOCK) {
                ByteArrayEditableData byteArrayData = new ByteArrayEditableData();
                try (OutputStream dataOutputStream = byteArrayData.getDataOutputStream()) {
                    StreamUtils.copyInputStreamToOutputStream(block.getData(), dataOutputStream);
                } catch (IOException ex) {
                    Logger.getLogger(PropertiesDocumentTab.class.getName()).log(Level.SEVERE, null, ex);
                }
                dataPanel.setContentData(byteArrayData);
                viewerPanel.addView("Data", dataPanel);
            } else {
                definitionPanel.setActiveNode((XBTTreeNode) block);
                viewerPanel.addView("Definition", definitionPanel);
            }
        }

        selectedItem = block;
        viewerPanel.revalidate();
        viewerPanel.repaint();
    }

    private void reloadCustomEditor(XBComponentViewer panelViewer, XBTBlock block) {
        XBPSerialReader serialReader = new XBPSerialReader(new XBTProviderToPullProvider(new XBTTreeWriter(block)));
        try {
            serialReader.read((XBSerializable) panelViewer);
        } catch (XBProcessingException | IOException ex) {
            Logger.getLogger(ModifyBlockPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
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
