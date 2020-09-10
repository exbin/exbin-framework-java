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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.gui.AddBlockPanel;
import org.exbin.framework.editor.xbup.viewer.DocumentViewerProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.MultiStepControlHandler;
import org.exbin.framework.gui.utils.gui.MultiStepControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.operation.XBTDocCommand;
import org.exbin.xbup.operation.basic.command.XBTAddBlockCommand;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.parser_tree.XBTTreeNode;

/**
 * Add item action.
 *
 * @version 0.2.1 2020/09/10
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddItemAction extends AbstractAction {

    public static final String ACTION_ID = "addItemAction";

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(AddItemAction.class);

    private final DocumentViewerProvider viewerProvider;
    private AddBlockPanel addItemPanel = null;

    public AddItemAction(DocumentViewerProvider viewerProvider) {
        this.viewerProvider = viewerProvider;
        init();
    }

    private void init() {
        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
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
