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

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.editor.xbup.gui.XBDocTreePanel;
import org.exbin.framework.editor.xbup.viewer.XbupEditorProvider;
import org.exbin.framework.editor.xbup.viewer.XbupFileHandler;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.operation.XBTDocCommand;
import org.exbin.xbup.operation.basic.command.XBTDeleteBlockCommand;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.parser_tree.XBTTreeNode;

/**
 * Delete item action.
 *
 * @version 0.2.1 2020/09/10
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DeleteItemAction extends AbstractAction {

    public static final String ACTION_ID = "deleteItemAction";

    private XbupEditorProvider editorProvider;

    public DeleteItemAction() {
    }

    public void setup(XbupEditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DeleteItemAction.performDelete(editorProvider);
    }

    static void performDelete(XbupEditorProvider viewerProvider) {
        XbupFileHandler xbupFile = (XbupFileHandler) viewerProvider.getActiveFile().get();
        XBTBlock block = xbupFile.getSelectedItem().get();
        if (!(block instanceof XBTTreeNode)) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        XBTTreeNode node = (XBTTreeNode) block;
        XBTTreeDocument mainDoc = xbupFile.getDoc();
        XBUndoHandler undoHandler = xbupFile.getUndoHandler();

        XBTTreeNode parent = (XBTTreeNode) node.getParent();
        try {
            XBTDocCommand command = new XBTDeleteBlockCommand(mainDoc, node);
            undoHandler.execute(command);
        } catch (Exception ex) {
            Logger.getLogger(XBDocTreePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

//        if (parent == null) {
//            mainDocModel.fireTreeChanged();
//        } else {
//            mainDocModel.fireTreeStructureChanged(parent);
//        }
        mainDoc.setModified(true);
    }
}
