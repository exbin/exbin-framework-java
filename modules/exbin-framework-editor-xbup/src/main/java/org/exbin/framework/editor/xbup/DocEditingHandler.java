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
package org.exbin.framework.editor.xbup;

import java.util.ResourceBundle;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.action.AddItemAction;
import org.exbin.framework.editor.xbup.action.EditItemAction;
import org.exbin.framework.editor.xbup.viewer.DocumentViewerProvider;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Document editing handler.
 *
 * @version 0.2.1 2020/03/09
 * @author ExBin Project (http://exbin.org)
 */
public class DocEditingHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action addItemAction;
    private Action editItemAction;

    public DocEditingHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
    }

    public void init() {
        addItemAction = new AddItemAction((DocumentViewerProvider) editorProvider);
        ActionUtils.setupAction(addItemAction, resourceBundle, "addItemAction");
        addItemAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PLUS, 0));
        addItemAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        editItemAction = new EditItemAction((DocumentViewerProvider) editorProvider);
        ActionUtils.setupAction(editItemAction, resourceBundle, "editItemAction");
        editItemAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        editItemAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public Action getAddItemAction() {
        return addItemAction;
    }

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
