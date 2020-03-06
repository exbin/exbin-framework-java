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
package org.exbin.framework.editor.xbup.action;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.editor.xbup.panel.XBDocTreePanel;
import org.exbin.xbup.core.parser.XBProcessingException;
import org.exbin.xbup.operation.XBTDocCommand;
import org.exbin.xbup.operation.basic.command.XBTAddBlockCommand;
import org.exbin.xbup.parser_tree.XBTTreeNode;

/**
 * Paste item from clipboard action.
 *
 * @version 0.2.0 2016/03/06
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PasteItemAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
//        if (clipboard.isDataFlavorAvailable(XB_DATA_FLAVOR)) {
//            try {
//                ByteArrayOutputStream stream = (ByteArrayOutputStream) clipboard.getData(XB_DATA_FLAVOR);
//                XBTTreeNode node = getSelectedItem();
//                XBTTreeNode newNode = new XBTTreeNode(node);
//                try {
//                    newNode.fromStreamUB(new ByteArrayInputStream(stream.toByteArray()));
//                    try {
//                        long parentPosition = node == null ? -1 : node.getBlockIndex();
//                        int childIndex = node == null ? 0 : node.getChildCount();
//                        XBTDocCommand step = new XBTAddBlockCommand(mainDoc, parentPosition, childIndex, newNode);
//                        getUndoHandler().execute(step);
//                        reportStructureChange(node);
//                        updateItemStatus();
//                    } catch (Exception ex) {
//                        Logger.getLogger(XBDocTreePanel.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                } catch (IOException | XBProcessingException ex) {
//                    Logger.getLogger(XBDocTreePanel.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            } catch (UnsupportedFlavorException | IOException ex) {
//                Logger.getLogger(XBDocTreePanel.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }
}
