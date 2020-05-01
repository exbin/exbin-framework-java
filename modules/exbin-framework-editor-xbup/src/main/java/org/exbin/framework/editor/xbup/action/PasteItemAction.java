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
