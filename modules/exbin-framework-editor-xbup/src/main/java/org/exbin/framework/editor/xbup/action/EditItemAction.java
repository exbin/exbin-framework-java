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
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.panel.ModifyBlockPanel;
import org.exbin.framework.editor.xbup.viewer.DocumentViewerProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.panel.DefaultControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.type.XBData;
import org.exbin.xbup.operation.XBTDocCommand;
import org.exbin.xbup.operation.basic.XBTModifyBlockOperation;
import org.exbin.xbup.operation.basic.XBTTailDataOperation;
import org.exbin.xbup.operation.basic.command.XBTChangeBlockCommand;
import org.exbin.xbup.operation.basic.command.XBTModifyBlockCommand;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.parser_tree.XBTTreeNode;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * Modify item action.
 *
 * @version 0.2.0 2016/03/09
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditItemAction extends AbstractAction {

    private final DocumentViewerProvider viewerProvider;

    public EditItemAction(DocumentViewerProvider viewerProvider) {
        this.viewerProvider = viewerProvider;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        XBApplication application = viewerProvider.getApplication();
        XBACatalog catalog = viewerProvider.getCatalog();
        XBUndoHandler undoHandler = viewerProvider.getUndoHandler();
        XBTTreeDocument mainDoc = viewerProvider.getDoc();
        XBPluginRepository pluginRepository = viewerProvider.getPluginRepository();
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        XBTTreeNode node = viewerProvider.getSelectedItem();

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
                    Logger.getLogger(EditItemAction.class.getName()).log(Level.SEVERE, null, ex);
                }

                viewerProvider.itemWasModified(node);
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(viewerProvider.getPanel());
    }
}
