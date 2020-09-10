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
package org.exbin.framework.editor.xbup;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.action.AddItemAction;
import org.exbin.framework.editor.xbup.action.EditItemAction;
import org.exbin.framework.editor.xbup.viewer.DocumentViewerProvider;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;

/**
 * Document editing handler.
 *
 * @version 0.2.1 2020/09/10
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DocEditingHandler {

    private final EditorProvider editorProvider;

    private final XBApplication application;

    private Action addItemAction;
    private Action editItemAction;

    public DocEditingHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
    }

    public void init() {
        addItemAction = new AddItemAction((DocumentViewerProvider) editorProvider);
        addItemAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PLUS, 0));
        addItemAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        editItemAction = new EditItemAction((DocumentViewerProvider) editorProvider);
        editItemAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        editItemAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Nonnull
    public Action getAddItemAction() {
        return addItemAction;
    }

    @Nonnull
    public Action getEditItemAction() {
        return editItemAction;
    }

    void setAddEnabled(boolean addEnabled) {
        addItemAction.setEnabled(addEnabled);
    }

    void setEditEnabled(boolean editEnabled) {
        editItemAction.setEnabled(editEnabled);
    }
}
