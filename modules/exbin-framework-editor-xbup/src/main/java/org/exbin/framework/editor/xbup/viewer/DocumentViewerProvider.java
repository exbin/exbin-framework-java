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
package org.exbin.framework.editor.xbup.viewer;

import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.panel.BlockPropertiesPanel;
import org.exbin.framework.editor.xbup.panel.ModifyBlockPanel;
import org.exbin.framework.editor.xbup.panel.XBDocumentPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.panel.CloseControlPanel;
import org.exbin.framework.gui.utils.panel.DefaultControlPanel;
import org.exbin.xbup.core.block.XBBlockDataMode;
import org.exbin.xbup.core.block.XBBlockType;
import org.exbin.xbup.core.block.XBFBlockType;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.core.block.declaration.catalog.XBCBlockDecl;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.XBCatalog;
import org.exbin.xbup.core.catalog.base.XBCBlockSpec;
import org.exbin.xbup.core.catalog.base.service.XBCXNameService;
import org.exbin.xbup.core.parser.XBProcessingException;
import org.exbin.xbup.core.parser.token.XBAttribute;
import org.exbin.xbup.core.type.XBData;
import org.exbin.xbup.operation.Operation;
import org.exbin.xbup.operation.OperationEvent;
import org.exbin.xbup.operation.OperationListener;
import org.exbin.xbup.operation.XBTDocCommand;
import org.exbin.xbup.operation.XBTDocOperation;
import org.exbin.xbup.operation.basic.XBTModifyBlockOperation;
import org.exbin.xbup.operation.basic.XBTTailDataOperation;
import org.exbin.xbup.operation.basic.command.XBTChangeBlockCommand;
import org.exbin.xbup.operation.basic.command.XBTModifyBlockCommand;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.parser_tree.XBTTreeNode;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * Viewer provider.
 *
 * @version 0.2.1 2020/03/02
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DocumentViewerProvider implements EditorProvider {

    private URI fileUri = null;
    private FileType fileType = null;
    private XBACatalog catalog;
    private final TreeDocument mainDoc;
    private PropertyChangeListener propertyChangeListener = null;

    private final XBDocumentPanel documentPanel;

    private PanelMode mode = PanelMode.VIEW;
    private DocumentViewer activeViewer;
    private final DocumentBinaryViewer binaryViewer;
    private final DocumentTextViewer textViewer;
    private final DocumentPropertiesViewer propertiesViewer;

    private XBApplication application;
    private XBUndoHandler undoHandler;
    private XBPluginRepository pluginRepository;

    public DocumentViewerProvider(XBACatalog catalog, XBUndoHandler undoHandler) {
        this.catalog = catalog;
        this.undoHandler = undoHandler;
        propertiesViewer = new DocumentPropertiesViewer();
        binaryViewer = new DocumentBinaryViewer();
        textViewer = new DocumentTextViewer();

        documentPanel = new XBDocumentPanel();
        mainDoc = new TreeDocument(catalog);
        documentPanel.setMainDoc(mainDoc);
    }

    @Nonnull
    @Override
    public JPanel getPanel() {
        return documentPanel;
    }

    public XBTTreeDocument getDoc() {
        return mainDoc;
    }

    public XBACatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        documentPanel.setCatalog(catalog);
        mainDoc.setCatalog(catalog);
        mainDoc.processSpec();
    }

    public void setApplication(XBApplication application) {
        this.application = application;
        documentPanel.setApplication(application);
    }

    public void setPluginRepository(XBPluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
        documentPanel.setPluginRepository(pluginRepository);
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propertyChangeListener = propertyChangeListener;
        documentPanel.setPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public String getWindowTitle(String frameTitle) {
        if (!"".equals(getDoc().getFileName())) {
            int pos;
            int newpos = 0;
            do {
                pos = newpos;
                newpos = getDoc().getFileName().indexOf(File.separatorChar, pos) + 1;
            } while (newpos > 0);
            return getDoc().getFileName().substring(pos) + " - " + frameTitle;
        }

        return frameTitle;
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        File file = new File(fileUri);
        try (FileInputStream fileStream = new FileInputStream(file)) {
            getDoc().fromStreamUB(fileStream);
            getDoc().processSpec();
            reportStructureChange((XBTTreeNode) getDoc().getRootBlock());
            performSelectAll();
            undoHandler.clear();
            this.fileUri = fileUri;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XBProcessingException ex) {
            Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO JOptionPane.showMessageDialog(WindowUtils.getFrame(this), ex.getMessage(), "Parsing Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        File file = new File(fileUri);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            getDoc().toStreamUB(fileOutputStream);
            undoHandler.setSyncPoint();
            getDoc().setModified(false);
            this.fileUri = fileUri;
        } catch (IOException ex) {
            Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public URI getFileUri() {
        return fileUri;
    }

    @Override
    public void newFile() {
        undoHandler.clear();
        getDoc().clear();
        reportStructureChange(null);
        updateItem();
    }

    @Override
    public String getFileName() {
        if (fileUri != null) {
            String path = fileUri.getPath();
            int lastSegment = path.lastIndexOf("/");
            return lastSegment < 0 ? path : path.substring(lastSegment + 1);
        }

        return null;
    }

    @Override
    public FileType getFileType() {
        return fileType;
    }

    @Override
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public boolean isModified() {
        return getDoc().wasModified();
    }

    public void performCut() {
        activeViewer.performCut();
    }

    public void performCopy() {
        activeViewer.performCopy();
    }

    public void performPaste() {
        activeViewer.performPaste();
    }

    public void performDelete() {
        activeViewer.performDelete();
    }

    public void performSelectAll() {
        activeViewer.performSelectAll();
    }

    public boolean isSelection() {
        return activeViewer.isSelection();
    }

    public boolean isEditable() {
        return activeViewer.isEditable();
    }

    public boolean canSelectAll() {
        return activeViewer.canSelectAll();
    }

    public boolean canPaste() {
        return activeViewer.canPaste();
    }

    public boolean isAddEnabled() {
        return true;
//        switch (mode) {
//            case VIEW:
//                return treePanel.isAddEnabled();
//            case TEXT:
//                return false;
//            case BINARY:
//                return false;
//            default:
//                return false;
//        }
    }

    public boolean isPasteEnabled() {
        return documentPanel.isPasteEnabled();
    }

    public void postWindowOpened() {
        documentPanel.postWindowOpened();
    }

    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
//        clipboardActionsUpdateListener = updateListener;
        documentPanel.setUpdateListener(updateListener);
        binaryViewer.setUpdateListener(updateListener);
        textViewer.setUpdateListener(updateListener);
        propertiesViewer.setUpdateListener(updateListener);
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        // TODO
    }

    public void reportStructureChange(XBTBlock block) {
        documentPanel.reportStructureChange(block);
    }

    public XBTTreeNode getSelectedItem() {
        return documentPanel.getSelectedItem();
    }

    public void updateItem() {
        documentPanel.updateItem();
    }

    public void setMode(PanelMode mode) {
        if (this.mode != mode) {
            switch (this.mode) {
                case VIEW:
                    break;
                case PROPERTIES:
                    break;
                case TEXT: {
                    break;
                }
                case BINARY: {
                    // TODO: Replace stupid buffer copy later
//                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//                    try {
//                        binaryViewer.saveToStream(buffer);
//                        mainDoc.fromStreamUB(new ByteArrayInputStream(buffer.toByteArray()));
//                    } catch (XBProcessingException ex) {
//                        Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
//                        throw new UnsupportedOperationException("Not supported yet.");
//                        // JOptionPane.showMessageDialog(WindowUtils.getFrame(this), ex.getMessage(), "Parsing Exception", JOptionPane.ERROR_MESSAGE);
//                    } catch (IOException ex) {
//                        Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                    break;
                }
                default:
                    throw new InternalError("Unknown mode");
            }
            switch (mode) {
                case VIEW:
                    break;
                case PROPERTIES:
                    break;
                case TEXT: {
                    String text = "<!XBUP version=\"0.1\">\n";
                    if (mainDoc.getRootBlock() != null) {
                        text += nodeAsText((XBTTreeNode) mainDoc.getRootBlock(), "").toString();
                    }
                    throw new UnsupportedOperationException("Not supported yet.");
                    // TODO textPanel.setText(text);
                    // break;
                }
                case BINARY: {
                    // TODO: Replace stupid buffer copy later
//                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//                    try {
//                        mainDoc.toStreamUB(buffer);
//                        binaryViewer.loadFromStream(new ByteArrayInputStream(buffer.toByteArray()), buffer.size());
//                    } catch (IOException ex) {
//                        Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                    break;
                }
                default:
                    throw new InternalError("Unknown mode");
            }
            this.mode = mode;

//            mainFrame.getEditFindAction().setEnabled(mode != PanelMode.TREE);
//            mainFrame.getEditFindAgainAction().setEnabled(mode == PanelMode.TEXT);
//            mainFrame.getEditGotoAction().setEnabled(mode == PanelMode.TEXT);
//            mainFrame.getEditReplaceAction().setEnabled(mode == PanelMode.TEXT);
//            mainFrame.getItemAddAction().setEnabled(false);
//            mainFrame.getItemModifyAction().setEnabled(false);
//            showPanel();
//            updateItem();
//            updateActionStatus(null);
//            if (clipboardActionsUpdateListener != null) {
//                clipboardActionsUpdateListener.stateChanged();
//            }
        }
    }

    public void performModify() {
        XBTTreeNode node = getSelectedItem();
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        ModifyBlockPanel panel = new ModifyBlockPanel();
        panel.setApplication(application);
        panel.setCatalog(catalog);
        panel.setPluginRepository(pluginRepository);
        panel.setNode(node, mainDoc);
        DefaultControlPanel controlPanel = new DefaultControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(panel, controlPanel);
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), ModifyBlockPanel.class, panel.getResourceBundle());
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == DefaultControlHandler.ControlActionType.OK) {
                XBTTreeNode newNode = panel.getNode();
                XBTDocCommand undoStep;
                if (node.getParent() == null) {
                    undoStep = new XBTChangeBlockCommand(mainDoc);
                    long position = node.getBlockIndex();
                    XBTModifyBlockOperation modifyOperation = new XBTModifyBlockOperation(mainDoc, position, newNode);
                    ((XBTChangeBlockCommand) undoStep).appendOperation(modifyOperation);
                    XBData tailData = new XBData();
                    panel.saveTailData(tailData.getDataOutputStream());
                    XBTTailDataOperation extOperation = new XBTTailDataOperation(mainDoc, tailData);
                    ((XBTChangeBlockCommand) undoStep).appendOperation(extOperation);
                } else {
                    undoStep = new XBTModifyBlockCommand(mainDoc, node, newNode);
                }
                // TODO: Optimized diff command later
                //                if (node.getDataMode() == XBBlockDataMode.DATA_BLOCK) {
                //                    undoStep = new XBTModDataBlockCommand(node, newNode);
                //                } else if (newNode.getChildrenCount() > 0) {
                //                } else {
                //                    undoStep = new XBTModAttrBlockCommand(node, newNode);
                //                }
                try {
                    undoHandler.execute(undoStep);
                } catch (Exception ex) {
                    Logger.getLogger(DocumentViewerProvider.class.getName()).log(Level.SEVERE, null, ex);
                }

                mainDoc.processSpec();
                reportStructureChange(node);
                getDoc().setModified(true);
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(null);
    }

    public void actionItemProperties() {
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        BlockPropertiesPanel panel = new BlockPropertiesPanel();
        panel.setCatalog(catalog);
        panel.setTreeNode(getSelectedItem());
        CloseControlPanel controlPanel = new CloseControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(panel, controlPanel);
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        controlPanel.setHandler(() -> {
            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(null);
    }

//    private void updateActiveViewer() {
//        int selectedIndex = mainTabbedPane.getSelectedIndex();
//        switch (selectedIndex) {
//            case 0: {
//                activeViewer = propertiesViewer;
//                mode = PanelMode.PROPERTIES;
//                break;
//            }
//            case 1: {
//                activeViewer = textViewer;
//                mode = PanelMode.TEXT;
//                break;
//            }
//            case 2: {
//                activeViewer = binaryViewer;
//                mode = PanelMode.BINARY;
//                break;
//            }
//        }
//    }
    private StringBuffer nodeAsText(XBTTreeNode node, String prefix) {
        StringBuffer result = new StringBuffer();
        result.append(prefix);
        if (node.getDataMode() == XBBlockDataMode.DATA_BLOCK) {
            result.append("[");
            for (long i = 0; i < node.getDataSize(); i++) {
                byte b = node.getBlockData().getByte(i);
                result.append(getHex(b));
            }
            result.append("]\n");
        } else {
            result.append("<").append(getCaption(node));
            if (node.getAttributesCount() > 2) {
                XBAttribute[] attributes = node.getAttributes();
                for (int i = 0; i < attributes.length; i++) {
                    XBAttribute attribute = attributes[i];
                    result.append(" ").append(i + 1).append("=\"").append(attribute.getNaturalLong()).append("\"");
                }
            }

            if (node.getChildren() != null) {
                result.append(">\n");
                XBTBlock[] children = node.getChildren();
                for (XBTBlock child : children) {
                    result.append(nodeAsText((XBTTreeNode) child, prefix + "  "));
                }
                result.append(prefix);
                result.append("</").append(getCaption(node)).append(">\n");
            } else {
                result.append("/>\n");
            }
        }
        return result;
    }

    public String getHex(byte b) {
        byte low = (byte) (b & 0xf);
        byte hi = (byte) (b >> 0x8);
        return (Integer.toHexString(hi) + Integer.toHexString(low)).toUpperCase();
    }

    private String getCaption(XBTTreeNode node) {
        if (node.getDataMode() == XBBlockDataMode.DATA_BLOCK) {
            return "Data Block";
        }
        XBBlockType blockType = node.getBlockType();
        if (catalog != null) {
            XBCXNameService nameService = (XBCXNameService) catalog.getCatalogService(XBCXNameService.class);
            XBCBlockDecl blockDecl = (XBCBlockDecl) node.getBlockDecl();
            if (blockDecl != null) {
                XBCBlockSpec blockSpec = blockDecl.getBlockSpecRev().getParent();
                return nameService.getDefaultText(blockSpec);
            }
        }
        return "Unknown" + " (" + Integer.toString(((XBFBlockType) blockType).getGroupID().getInt()) + ", " + Integer.toString(((XBFBlockType) blockType).getBlockID().getInt()) + ")";
    }

    private class TreeDocument extends XBTTreeDocument implements OperationListener {

        public TreeDocument(XBCatalog catalog) {
            super(catalog);
        }

        @Override
        public void notifyChange(OperationEvent event) {
            Operation operation = event.getOperation();
            // TODO Consolidate
            mainDoc.processSpec();
            reportStructureChange(null);
            // getDoc().setModified(true);
            updateItem();
//            updateActionStatus(null);
//            if (clipboardActionsUpdateListener != null) {
//                clipboardActionsUpdateListener.stateChanged();
//            }

            if (operation instanceof XBTDocOperation) {
                setMode(PanelMode.VIEW);
            } else {
                // TODO
            }
        }
    }

    public enum PanelMode {
        VIEW,
        PROPERTIES,
        TEXT,
        BINARY
    }
}
