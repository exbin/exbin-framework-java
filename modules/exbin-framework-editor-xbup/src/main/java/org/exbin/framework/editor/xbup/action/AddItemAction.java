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
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.exbin.framework.editor.xbup.panel.AddBlockPanel;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.MultiStepControlHandler;
import org.exbin.framework.gui.utils.panel.MultiStepControlPanel;
import org.exbin.xbup.operation.XBTDocCommand;
import org.exbin.xbup.operation.basic.command.XBTAddBlockCommand;
import org.exbin.xbup.parser_tree.XBTTreeNode;

/**
 * Add item action.
 *
 * @version 0.2.0 2016/03/02
 * @author ExBin Project (http://exbin.org)
 */
public class AddItemAction extends AbstractAction {

    private AddBlockPanel addItemPanel = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
//        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
//        XBTTreeNode node = getSelectedItem();
//
//        addItemPanel = new AddBlockPanel();
//        addItemPanel.setApplication(application);
//        addItemPanel.setCatalog(catalog);
//        addItemPanel.setParentNode(node);
//        MultiStepControlPanel controlPanel = new MultiStepControlPanel();
//        JPanel dialogPanel = WindowUtils.createDialogPanel(addItemPanel, controlPanel);
//        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
//        WindowUtils.addHeaderPanel(dialog.getWindow(), AddBlockPanel.class, addItemPanel.getResourceBundle());
//        controlPanel.setHandler((MultiStepControlHandler.ControlActionType actionType) -> {
//            switch (actionType) {
//                case FINISH: {
//                    XBTTreeNode newNode = addItemPanel.getWorkNode();
//                    try {
//                        long parentPosition = node == null ? -1 : node.getBlockIndex();
//                        int childIndex = node == null ? 0 : node.getChildCount();
//                        XBTDocCommand step = new XBTAddBlockCommand(mainDoc, parentPosition, childIndex, newNode);
//                        getUndoHandler().execute(step);
//                    } catch (Exception ex) {
//                        Logger.getLogger(AddItemAction.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//
//                    reportStructureChange(newNode);
//                    mainDoc.setModified(true);
//                    updateItemStatus();
//
//                    dialog.close();
//                    dialog.dispose();
//                    break;
//                }
//                case CANCEL: {
//                    dialog.close();
//                    dialog.dispose();
//                    break;
//                }
//                case NEXT: {
//                    break;
//                }
//                case PREVIOUS: {
//                    break;
//                }
//            }
//        });
//        dialog.showCentered(this);
//
//        addItemPanel.setLocationRelativeTo(addItemPanel.getParent());
//        addItemPanel.setParentNode(node);
//        XBTTreeNode newNode = addItemPanel.showDialog();
//        if (addItemPanel.getDialogOption() == JOptionPane.OK_OPTION) {
//            try {
//                long parentPosition = node == null ? -1 : node.getBlockIndex();
//                int childIndex = node == null ? 0 : node.getChildCount();
//                XBTDocCommand step = new XBTAddBlockCommand(mainDoc, parentPosition, childIndex, newNode);
//                getUndoHandler().execute(step);
//            } catch (Exception ex) {
//                Logger.getLogger(AddItemAction.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            reportStructureChange(newNode);
//            mainDoc.setModified(true);
//            updateItemStatus();
//        }
//        addItemPanel = null;
    }
}
