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
import org.exbin.framework.editor.xbup.panel.AddBlockPanel;
import org.exbin.framework.editor.xbup.viewer.DocumentViewerProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.MultiStepControlHandler;
import org.exbin.framework.gui.utils.panel.MultiStepControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.operation.XBTDocCommand;
import org.exbin.xbup.operation.basic.command.XBTAddBlockCommand;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.parser_tree.XBTTreeNode;

/**
 * Add item action.
 *
 * @version 0.2.0 2016/03/09
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddItemAction extends AbstractAction {

    private AddBlockPanel addItemPanel = null;
    private final DocumentViewerProvider viewerProvider;

    public AddItemAction(DocumentViewerProvider viewerProvider) {
        this.viewerProvider = viewerProvider;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        XBApplication application = viewerProvider.getApplication();
        XBACatalog catalog = viewerProvider.getCatalog();
        XBUndoHandler undoHandler = viewerProvider.getUndoHandler();
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        XBTTreeNode node = viewerProvider.getSelectedItem();

        addItemPanel = new AddBlockPanel();
        addItemPanel.setApplication(application);
        addItemPanel.setCatalog(catalog);
        addItemPanel.setParentNode(node);
        MultiStepControlPanel controlPanel = new MultiStepControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(addItemPanel, controlPanel);
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), AddBlockPanel.class, addItemPanel.getResourceBundle());
        controlPanel.setHandler((MultiStepControlHandler.ControlActionType actionType) -> {
            switch (actionType) {
                case FINISH: {
                    XBTTreeNode newNode = addItemPanel.getWorkNode();
                    try {
                        XBTTreeDocument mainDoc = viewerProvider.getDoc();
                        long parentPosition = node == null ? -1 : node.getBlockIndex();
                        int childIndex = node == null ? 0 : node.getChildCount();
                        XBTDocCommand step = new XBTAddBlockCommand(mainDoc, parentPosition, childIndex, newNode);
                        undoHandler.execute(step);
                    } catch (Exception ex) {
                        Logger.getLogger(AddItemAction.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    viewerProvider.itemWasModified(newNode);

                    dialog.close();
                    dialog.dispose();
                    break;
                }
                case CANCEL: {
                    dialog.close();
                    dialog.dispose();
                    break;
                }
                case NEXT: {
                    break;
                }
                case PREVIOUS: {
                    break;
                }
            }
        });
        dialog.showCentered(viewerProvider.getPanel());
    }
}
