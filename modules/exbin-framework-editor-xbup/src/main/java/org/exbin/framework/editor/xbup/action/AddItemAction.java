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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.editor.xbup.gui.AddBlockPanel;
import org.exbin.framework.editor.xbup.viewer.XbupEditorProvider;
import org.exbin.framework.editor.xbup.viewer.XbupFileHandler;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.handler.MultiStepControlHandler;
import org.exbin.framework.utils.gui.MultiStepControlPanel;
import org.exbin.xbup.core.block.XBTBlock;
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

    private XbupEditorProvider editorProvider;
    private AddBlockPanel addItemPanel = null;

    public AddItemAction() {
    }

    public void setup(XbupEditorProvider editorProvider) {
        this.editorProvider = editorProvider;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        if (editorProvider instanceof MultiEditorProvider) {
            setEnabled(editorProvider.getActiveFile().isPresent());
            ((MultiEditorProvider) editorProvider).addActiveFileChangeListener((@Nullable FileHandler fileHandler) -> {
                setEnabled(fileHandler != null);
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        XBApplication application = editorProvider.getApplication();
        XBACatalog catalog = editorProvider.getCatalog();
        XbupFileHandler xbupFile = (XbupFileHandler) editorProvider.getActiveFile().get();
        XBUndoHandler undoHandler = xbupFile.getUndoHandler();
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        XBTBlock block = xbupFile.getSelectedItem().orElse(null);
        if (!(block instanceof XBTTreeNode) && block != null) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        XBTTreeNode node = (XBTTreeNode) block;

        addItemPanel = new AddBlockPanel();
        addItemPanel.setApplication(application);
        addItemPanel.setCatalog(catalog);
        addItemPanel.setParentNode(node);
        MultiStepControlPanel controlPanel = new MultiStepControlPanel();
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(addItemPanel, controlPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), AddBlockPanel.class, addItemPanel.getResourceBundle());
        controlPanel.setHandler((MultiStepControlHandler.ControlActionType actionType) -> {
            switch (actionType) {
                case FINISH: {
                    XBTTreeNode newNode = addItemPanel.getWorkNode();
                    try {
                        XBTTreeDocument mainDoc = xbupFile.getDoc();
                        long parentPosition = node == null ? -1 : node.getBlockIndex();
                        int childIndex = node == null ? 0 : node.getChildCount();
                        XBTDocCommand step = new XBTAddBlockCommand(mainDoc, parentPosition, childIndex, newNode);
                        undoHandler.execute(step);
                    } catch (Exception ex) {
                        Logger.getLogger(AddItemAction.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    xbupFile.itemWasModified(newNode);

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
        dialog.showCentered(editorProvider.getEditorComponent());
    }
}
