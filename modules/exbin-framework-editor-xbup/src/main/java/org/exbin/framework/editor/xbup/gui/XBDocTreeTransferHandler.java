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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.COPY;
import static javax.swing.TransferHandler.COPY_OR_MOVE;
import static javax.swing.TransferHandler.MOVE;
import org.exbin.xbup.core.parser.basic.XBHead;
import org.exbin.xbup.parser_tree.XBTTreeNode;

/**
 * Document tree transfer handler.
 *
 * @version 0.2.1 2020/03/06
 * @author ExBin Project (http://exbin.org)
 */
public class XBDocTreeTransferHandler extends TransferHandler {

    public static final DataFlavor XB_DATA_FLAVOR = new DataFlavor(XBHead.XBUP_MIME_TYPE, "XBUP Document");

    private final XBDocTreePanel docTreePanel;
    private XBTTreeNode sourceNode;

    public XBDocTreeTransferHandler(XBDocTreePanel docTreePanel) {
        super();
        this.docTreePanel = docTreePanel;
        sourceNode = null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Nullable
    @Override
    public Transferable createTransferable(JComponent c) {
        if (c instanceof JTree) {
            sourceNode = docTreePanel.getSelectedItem();
            return new XBTSelection(sourceNode);
            //java.awt.datatransfer.StringSelection("Test");
        } else {
            return null;
        }
    }

    @Override
    public void exportDone(JComponent c, Transferable t, int action) {
        if (action == MOVE) {
            throw new UnsupportedOperationException("Not supported yet.");
            // docTreePanel.deleteNode(sourceNode);
        } else if (action == COPY) {

        }
        sourceNode = null;
    }

    @Override
    public boolean canImport(TransferSupport supp) {
        return false;
//        // Check for String flavor
//        if (!supp.isDataFlavorSupported(XB_DATA_FLAVOR)) {
//            return false;
//        }
//
//        // Fetch the drop location
//        DropLocation loc = supp.getDropLocation();
//
//        // Return whether we accept the location
//        TreePath treePath = docTreePanel.mainTree.getPathForLocation(loc.getDropPoint().x, loc.getDropPoint().y);
//        if (treePath == null) {
//            return false;
//        }
//        Object nodeObject = treePath.getLastPathComponent();
//        if (!(nodeObject instanceof XBTTreeNode)) {
//            return false;
//        }
//        XBTTreeNode node = (XBTTreeNode) nodeObject;
//        if (node.getDataMode() == XBBlockDataMode.DATA_BLOCK) {
//            return false;
//        }
//        if ((sourceNode != null) && (supp.getDropAction() == MOVE)) {
//            if (node == sourceNode) {
//                return false;
//            }
//            if (node == sourceNode.getParent()) {
//                return false;
//            }
//            XBTTreeNode parent = node.getParent();
//            while (parent != null) {
//                if (parent == sourceNode) {
//                    return false;
//                }
//                parent = parent.getParent();
//            }
//        }
//        return true;
    }

    @Override
    public boolean importData(TransferSupport supp) {
        return false;

//        if (!canImport(supp)) {
//            return false;
//        }
//
//        // Fetch the Transferable and its data
//        Transferable t = supp.getTransferable();
////            String data = t.getTransferData(stringFlavor);
//
//        // Fetch the drop location
//        DropLocation loc = supp.getDropLocation();
//
//        // Return whether we accept the location
//        TreePath treePath = docTreePanel.mainTree.getPathForLocation(loc.getDropPoint().x, loc.getDropPoint().y);
//        if (treePath == null) {
//            return false;
//        }
//        Object nodeObject = treePath.getLastPathComponent();
//        if (!(nodeObject instanceof XBTTreeNode)) {
//            return false;
//        }
//        XBTTreeNode node = (XBTTreeNode) nodeObject;
//
//        // Insert the data at this location
//        try {
//            ByteArrayOutputStream stream = (ByteArrayOutputStream) t.getTransferData(XB_DATA_FLAVOR);
//            XBTTreeNode newNode = new XBTTreeNode(node);
//            try {
//                newNode.fromStreamUB(new ByteArrayInputStream(stream.toByteArray()));
//                try {
//                    XBTDocCommand step = new XBTAddBlockCommand(docTreePanel.mainDoc, node.getBlockIndex(), node.getChildCount(), newNode);
//                    docTreePanel.getUndoHandler().execute(step);
//                    docTreePanel.reportStructureChange(node);
//                    docTreePanel.mainDoc.processSpec();
//                    docTreePanel.updateItemStatus();
//                } catch (Exception ex) {
//                    Logger.getLogger(XBDocTreePanel.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            } catch (IOException | XBProcessingException ex) {
//                Logger.getLogger(XBDocTreePanel.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } catch (UnsupportedFlavorException | IOException ex) {
//            Logger.getLogger(XBDocTreePanel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return true;
    }

    public static class XBTSelection implements Transferable, ClipboardOwner {

        private ByteArrayOutputStream data;

        public XBTSelection(XBTTreeNode node) {
            if (node != null) {
                data = new ByteArrayOutputStream();
                try {
                    node.toStreamUB(data);
                } catch (IOException ex) {
                    Logger.getLogger(XBDocTreePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            // TODO: Later also as text
            DataFlavor[] result = new DataFlavor[1];
            result[0] = XB_DATA_FLAVOR;
            return result;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(XB_DATA_FLAVOR);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.equals(XB_DATA_FLAVOR)) {
                return data;
            }
            return null;
        }

        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
            // do nothing
        }
    }
}
