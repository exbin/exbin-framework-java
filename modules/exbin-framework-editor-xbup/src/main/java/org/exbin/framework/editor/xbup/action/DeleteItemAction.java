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

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.editor.xbup.panel.XBDocTreePanel;
import org.exbin.xbup.operation.XBTDocCommand;
import org.exbin.xbup.operation.basic.command.XBTDeleteBlockCommand;
import org.exbin.xbup.parser_tree.XBTTreeNode;

/**
 * Delete item action.
 *
 * @version 0.2.0 2016/03/06
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DeleteItemAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
//        XBTTreeNode parent = (XBTTreeNode) node.getParent();
//        try {
//            XBTDocCommand command = new XBTDeleteBlockCommand(mainDoc, node);
//            undoHandler.execute(command);
//        } catch (Exception ex) {
//            Logger.getLogger(XBDocTreePanel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        if (parent == null) {
//            mainDocModel.fireTreeChanged();
//        } else {
//            mainDocModel.fireTreeStructureChanged(parent);
//        }
//        mainDoc.setModified(true);
    }
}
