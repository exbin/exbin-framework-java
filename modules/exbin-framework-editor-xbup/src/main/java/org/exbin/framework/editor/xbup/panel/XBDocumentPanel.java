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
package org.exbin.framework.editor.xbup.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeSelectionEvent;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.panel.BinaryPanel;
import org.exbin.framework.bined.panel.BinaryStatusPanel;
import org.exbin.framework.editor.text.panel.TextPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
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
import org.exbin.framework.editor.text.service.TextSearchService;
import org.exbin.framework.editor.xbup.viewer.DocumentBinaryViewer;

/**
 * Panel with XBUP document visualization.
 *
 * @version 0.2.1 2020/02/26
 * @author ExBin Project (http://exbin.org)
 */
public class XBDocumentPanel extends javax.swing.JPanel implements EditorProvider, ClipboardActionsHandler {

    private XBApplication application;

    private final TreeDocument mainDoc;
    private URI fileUri = null;
    private FileType fileType = null;
    private XBACatalog catalog;
    private boolean showPropertiesPanel = false;

    private PanelMode mode = PanelMode.VIEW;
    private final DocumentBinaryViewer binaryViewer;

    private final JPanel previewPanel = new JPanel();
    private final XBDocTreePanel treePanel;
    private final TextPanel textPanel;

    private XBPropertyPanel propertyPanel;
    private XBPluginRepository pluginRepository;
    private PropertyChangeListener propertyChangeListener;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;
    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(XBDocumentPanel.class);

    public XBDocumentPanel(XBACatalog catalog, XBUndoHandler undoHandler) {
        this.catalog = catalog;
        mainDoc = new TreeDocument(catalog);

        initComponents();

        propertyPanel = new XBPropertyPanel(catalog);
        mainSplitPane.setRightComponent(propertyPanel);

        treePanel = new XBDocTreePanel(mainDoc, catalog, undoHandler, popupMenu);
        binaryViewer = new DocumentBinaryViewer();
        textPanel = new TextPanel();
        textPanel.setNoBorder();

        treePanel.setPopupMenu(popupMenu);
        textPanel.setPopupMenu(popupMenu);

        ((JPanel) mainTabbedPane.getComponentAt(0)).add(treePanel, java.awt.BorderLayout.CENTER);

        treePanel.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
            if (propertyChangeListener != null) {
                propertyChangeListener.propertyChange(evt);
            }
        });

        treePanel.addTreeSelectionListener((TreeSelectionEvent e) -> {
            if (propertyPanel.isEnabled()) {
                propertyPanel.setActiveNode(treePanel.getSelectedItem());
            }
        });

        super.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (propertyChangeListener != null) {
                propertyChangeListener.propertyChange(evt);
            }
        });

        mainSplitPane.setLeftComponent(treePanel);
        mainSplitPane.setRightComponent(mainTabbedPane);
        setShowPropertiesPanel(true);
        //updateItem();
    }

    public void postWindowOpened() {
        mainSplitPane.setDividerLocation(getWidth() - 300 > 0 ? getWidth() - 300 : getWidth() / 3);
    }

    /**
     * Updating selected item available operations status, like add, edit,
     * delete.
     */
    public void updateItem() {
        treePanel.updateItemStatus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu = new javax.swing.JPopupMenu();
        popupItemViewMenuItem = new javax.swing.JMenuItem();
        popupItemCopyMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        popupItemPropertiesMenuItem = new javax.swing.JMenuItem();
        viewSplitPane = new javax.swing.JSplitPane();
        mainTabbedPane = new javax.swing.JTabbedPane();
        propertiesTabPanel = new javax.swing.JPanel();
        textTabPanel = new javax.swing.JPanel();
        binaryTabPanel = new javax.swing.JPanel();
        mainSplitPane = new javax.swing.JSplitPane();

        popupItemViewMenuItem.setText(resourceBundle.getString("popupItemViewMenuItem.text")); // NOI18N
        popupItemViewMenuItem.setToolTipText(resourceBundle.getString("popupItemViewMenuItem.toolTipText")); // NOI18N
        popupItemViewMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupItemViewMenuItemActionPerformed(evt);
            }
        });
        popupMenu.add(popupItemViewMenuItem);

        popupItemCopyMenuItem.setText(resourceBundle.getString("popupItemCopyMenuItem.text")); // NOI18N
        popupItemCopyMenuItem.setToolTipText(resourceBundle.getString("popupItemCopyMenuItem.toolTipText")); // NOI18N
        popupItemCopyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupItemCopyMenuItemActionPerformed(evt);
            }
        });
        popupMenu.add(popupItemCopyMenuItem);
        popupMenu.add(jSeparator1);

        popupItemPropertiesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.ALT_MASK));
        popupItemPropertiesMenuItem.setText(resourceBundle.getString("popupItemPropertiesMenuItem.text")); // NOI18N
        popupItemPropertiesMenuItem.setToolTipText(resourceBundle.getString("popupItemPropertiesMenuItem.toolTipText")); // NOI18N
        popupItemPropertiesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupItemPropertiesMenuItemActionPerformed(evt);
            }
        });
        popupMenu.add(popupItemPropertiesMenuItem);

        viewSplitPane.setDividerLocation(250);
        viewSplitPane.setResizeWeight(1.0);

        mainTabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        mainTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mainTabbedPaneStateChanged(evt);
            }
        });

        propertiesTabPanel.setLayout(new java.awt.BorderLayout());
        mainTabbedPane.addTab("Properties", propertiesTabPanel);

        textTabPanel.setLayout(new java.awt.BorderLayout());
        mainTabbedPane.addTab("Text", textTabPanel);

        binaryTabPanel.setLayout(new java.awt.BorderLayout());
        mainTabbedPane.addTab("Binary", binaryTabPanel);

        setLayout(new java.awt.BorderLayout());

        mainSplitPane.setBorder(null);
        mainSplitPane.setDividerLocation(200);
        add(mainSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void popupItemPropertiesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupItemPropertiesMenuItemActionPerformed
        actionItemProperties();
    }//GEN-LAST:event_popupItemPropertiesMenuItemActionPerformed

    private void popupItemViewMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupItemViewMenuItemActionPerformed
        performModify();
    }//GEN-LAST:event_popupItemViewMenuItemActionPerformed

    private void popupItemCopyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupItemCopyMenuItemActionPerformed
        performCopy();
    }//GEN-LAST:event_popupItemCopyMenuItemActionPerformed

    private void mainTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mainTabbedPaneStateChanged
        setMode(PanelMode.values()[mainTabbedPane.getSelectedIndex()]);
    }//GEN-LAST:event_mainTabbedPaneStateChanged

    public XBTTreeNode getSelectedItem() {
        return treePanel.getSelectedItem();
    }

    public XBTTreeDocument getDoc() {
        return mainDoc;
    }

    public void reportStructureChange(XBTBlock block) {
        treePanel.reportStructureChange(block);
    }

    public boolean isEditEnabled() {
        switch (mode) {
            case VIEW:
                return treePanel.isEditEnabled();
            case TEXT:
                return false;
            case BINARY:
                return false;
            default:
                return false;
        }
    }

    public boolean isAddEnabled() {
        switch (mode) {
            case VIEW:
                return treePanel.isAddEnabled();
            case TEXT:
                return false;
            case BINARY:
                return false;
            default:
                return false;
        }
    }

    public boolean isPasteEnabled() {
        return treePanel.isPasteEnabled();
    }

    public void addUpdateListener(ActionListener tml) {
        treePanel.addUpdateListener(tml);
    }

    public void removeUpdateListener(ActionListener tml) {
        treePanel.removeUpdateListener(tml);
    }

    public XBACatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        treePanel.setCatalog(catalog);
        mainDoc.setCatalog(catalog);
        mainDoc.processSpec();
        propertyPanel.setCatalog(catalog);
    }

    public void setApplication(XBApplication application) {
        this.application = application;
        treePanel.setApplication(application);
        propertyPanel.setApplication(application);
    }

    @Override
    public void performCut() {
        switch (mode) {
            case VIEW:
                treePanel.performCut();
                break;
            case TEXT:
                textPanel.performCut();
                break;
            case BINARY:
                binaryViewer.performCut();
            default:
                break;
        }
    }

    @Override
    public void performCopy() {
        switch (mode) {
            case VIEW:
                treePanel.performCopy();
                break;
            case TEXT:
                textPanel.performCopy();
                break;
            case BINARY:
                binaryViewer.performCopy();
            default:
                break;
        }
    }

    @Override
    public void performPaste() {
        switch (mode) {
            case VIEW:
                treePanel.performPaste();
                break;
            case TEXT:
                textPanel.performPaste();
                break;
            case BINARY:
                binaryViewer.performPaste();
                break;
            default:
                break;
        }
    }

    @Override
    public void performSelectAll() {
        switch (mode) {
            case VIEW:
                treePanel.performSelectAll();
                break;
            case TEXT:
                textPanel.performSelectAll();
                break;
            case BINARY:
                binaryViewer.performSelectAll();
                break;
            default:
                break;
        }
    }

    public void performAdd() {
        treePanel.performAdd();
    }

    @Override
    public void performDelete() {
        switch (mode) {
            case VIEW:
                treePanel.performDelete();
                break;
            case TEXT:
                textPanel.performDelete();
                break;
            case BINARY:
                binaryViewer.performDelete();
                break;
            default:
                break;
        }
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
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    try {
                        binaryViewer.saveToStream(buffer);
                        mainDoc.fromStreamUB(new ByteArrayInputStream(buffer.toByteArray()));
                    } catch (XBProcessingException ex) {
                        Logger.getLogger(XBDocumentPanel.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(WindowUtils.getFrame(this), ex.getMessage(), "Parsing Exception", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        Logger.getLogger(XBDocumentPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
                    textPanel.setText(text);
                    break;
                }
                case BINARY: {
                    // TODO: Replace stupid buffer copy later
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    try {
                        mainDoc.toStreamUB(buffer);
                        binaryViewer.loadFromStream(new ByteArrayInputStream(buffer.toByteArray()), buffer.size());
                    } catch (IOException ex) {
                        Logger.getLogger(XBDocumentPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
            showPanel();
            updateItem();
            updateActionStatus(null);
            if (clipboardActionsUpdateListener != null) {
                clipboardActionsUpdateListener.stateChanged();
            }
        }
    }

    public void showPanel() {
        int index = getMode().ordinal();
        mainTabbedPane.setSelectedIndex(index);
        ((JPanel) mainTabbedPane.getComponentAt(index)).add(getPanel(index));
    }

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel binaryTabPanel;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JMenuItem popupItemCopyMenuItem;
    private javax.swing.JMenuItem popupItemPropertiesMenuItem;
    private javax.swing.JMenuItem popupItemViewMenuItem;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JPanel propertiesTabPanel;
    private javax.swing.JPanel textTabPanel;
    private javax.swing.JSplitPane viewSplitPane;
    // End of variables declaration//GEN-END:variables

    public void setEditEnabled(boolean editEnabled) {
        treePanel.setEditEnabled(editEnabled);
    }

    public void setAddEnabled(boolean addEnabled) {
        treePanel.setAddEnabled(addEnabled);
    }

    public void updateUndoAvailable() {
        firePropertyChange("undoAvailable", false, true);
        firePropertyChange("redoAvailable", false, true);
    }

    public XBUndoHandler getUndoHandler() {
        return treePanel.getUndoHandler();
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        File file = new File(fileUri);
        try (FileInputStream fileStream = new FileInputStream(file)) {
            getDoc().fromStreamUB(fileStream);
            getDoc().processSpec();
            reportStructureChange((XBTTreeNode) getDoc().getRootBlock());
            performSelectAll();
            getUndoHandler().clear();
            this.fileUri = fileUri;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XBDocumentPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XBDocumentPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XBProcessingException ex) {
            Logger.getLogger(XBDocumentPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(WindowUtils.getFrame(this), ex.getMessage(), "Parsing Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @return the popupMenu
     */
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public Color[] getDefaultColors() {
        return textPanel.getDefaultColors();
    }

    public void setCurrentColors(Color[] colors) {
        textPanel.setCurrentColors(colors);
    }

    public Font getDefaultFont() {
        return textPanel.getDefaultFont();
    }

    public void setCurrentFont(Font deriveFont) {
        textPanel.setCurrentFont(deriveFont);
    }

    @Override
    public boolean isModified() {
        return getDoc().wasModified();
    }

    public boolean changeLineWrap() {
        return textPanel.changeLineWrap();
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        File file = new File(fileUri);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            getDoc().toStreamUB(fileOutputStream);
            getUndoHandler().setSyncPoint();
            getDoc().setModified(false);
            this.fileUri = fileUri;
        } catch (IOException ex) {
            Logger.getLogger(XBDocumentPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    public URI getFileUri() {
        return fileUri;
    }

    @Override
    public FileType getFileType() {
        return fileType;
    }

    @Override
    public void newFile() {
        getUndoHandler().clear();
        getDoc().clear();
        reportStructureChange(null);
        updateItem();
    }

    public void printFile() {
        textPanel.printFile();
    }

    public int getLineCount() {
        return textPanel.getLineCount();
    }

    public void gotoRelative(int charPos) {
        textPanel.gotoRelative(charPos);
    }

    public void gotoLine(int line) {
        textPanel.gotoLine(line);
    }

    public void findText(TextSearchService.FindTextParameters findTextParameters) {
        textPanel.findText(findTextParameters);
    }

    public void setCharset(Charset charset) {
        textPanel.setCharset(charset);
    }

    public Color[] getCurrentColors() {
        return textPanel.getCurrentColors();
    }

    public void setFileMode(int i) {
        // TODO textPanel.setFileMode(getMode().ordinal());
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
        final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
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
                    getUndoHandler().execute(undoStep);
                } catch (Exception ex) {
                    Logger.getLogger(XBDocumentPanel.class.getName()).log(Level.SEVERE, null, ex);
                }

                mainDoc.processSpec();
                reportStructureChange(node);
                getDoc().setModified(true);
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(this);
    }

    public void setShowPropertiesPanel(boolean showPropertiesPanel) {
//        if (this.showPropertiesPanel != showPropertiesPanel) {
//            if (showPropertiesPanel) {
//                viewSplitPane.setLeftComponent(mainTabbedPane);
//                viewSplitPane.setRightComponent(propertyPanel);
//                mainSplitPane.setRightComponent(viewSplitPane);
//            } else {
//                mainSplitPane.setRightComponent(mainTabbedPane);
//            }
//
//            this.showPropertiesPanel = showPropertiesPanel;
//        }
    }

    private Component getPanel(int index) {
        switch (index) {
            case 0:
                return previewPanel;
            case 1:
                return textPanel;
            case 2:
                return binaryViewer.getComponent();
        }

        return null;
    }

//    public ActivePanelActionHandling getActivePanel() {
//        int selectedIndex = mainTabbedPane.getSelectedIndex();
//        return (ActivePanelActionHandling) getPanel(selectedIndex);
//    }
    public boolean isShowPropertiesPanel() {
        return showPropertiesPanel;
    }

    public PanelMode getMode() {
        return mode;
    }

    public XBPluginRepository getPluginRepository() {
        return pluginRepository;
    }

    public void setPluginRepository(XBPluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
        propertyPanel.setPluginRepository(pluginRepository);
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

    @Override
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propertyChangeListener = propertyChangeListener;
    }

    public boolean updateActionStatus(Component component) {
        switch (mode) {
            case VIEW:
//                return treePanel.updateActionStatus(component);
            case TEXT: {
                return false;
            }
            case BINARY: {
                return false;
            }
        }

        return false;
    }

    public void releaseActionStatus() {
        switch (mode) {
            case VIEW: {
//                treePanel.releaseActionStatus();
                break;
            }
        }
    }

    public boolean performAction(String eventName, ActionEvent event) {
        switch (mode) {
            case VIEW:
//                return treePanel.performAction(eventName, event);
        }

        return false;
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

    public Font getCurrentFont() {
        return textPanel.getCurrentFont();
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public boolean isSelection() {
        switch (mode) {
            case VIEW:
                return treePanel.isSelection();
            case TEXT:
                return textPanel.isSelection();
            case BINARY:
                // TODO
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isEditable() {
        switch (mode) {
            case VIEW:
                return treePanel.isEditEnabled();
            case TEXT:
                return true;
            case BINARY:
                return binaryViewer.isEditable();
            default:
                return false;
        }
    }

    @Override
    public boolean canSelectAll() {
        switch (mode) {
            case VIEW:
                // TODO Multiple selection in tree
                return true;
            case TEXT:
                return true;
            case BINARY:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean canPaste() {
        switch (mode) {
            case VIEW:
                return treePanel.isPasteEnabled();
            case TEXT:
                // TODO Allow to paste text only
                return true;
            case BINARY:
                return binaryViewer.canPaste();
            default:
                return false;
        }
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        clipboardActionsUpdateListener = updateListener;
        treePanel.setUpdateListener(updateListener);
        textPanel.setUpdateListener(updateListener);
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        // TODO
    }

    public enum PanelMode {
        VIEW,
        PROPERTIES,
        TEXT,
        BINARY
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
        treePanel.setPopupMenu(popupMenu);
        textPanel.setPopupMenu(popupMenu);
    }

    public void actionItemProperties() {
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        BlockPropertiesPanel panel = new BlockPropertiesPanel();
        panel.setCatalog(catalog);
        panel.setTreeNode(getSelectedItem());
        CloseControlPanel controlPanel = new CloseControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(panel, controlPanel);
        final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        controlPanel.setHandler(() -> {
            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(this);
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
            updateActionStatus(null);
            if (clipboardActionsUpdateListener != null) {
                clipboardActionsUpdateListener.stateChanged();
            }

            if (operation instanceof XBTDocOperation) {
                setMode(PanelMode.VIEW);
            } else {
                // TODO
            }
        }
    }
}
