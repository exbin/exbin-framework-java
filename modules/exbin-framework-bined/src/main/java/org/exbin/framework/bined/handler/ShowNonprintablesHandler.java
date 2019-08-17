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
package org.exbin.framework.bined.handler;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * View nonprintables handler.
 *
 * @version 0.2.1 2019/07/15
 * @author ExBin Project (http://exbin.org)
 */
public class ShowNonprintablesHandler {

    private final BinaryEditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action viewNonprintablesAction;
    private Action viewNonprintablesToolbarAction;

    public ShowNonprintablesHandler(XBApplication application, BinaryEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        viewNonprintablesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean showUnprintables = editorProvider.changeShowNonprintables();
                viewNonprintablesAction.putValue(Action.SELECTED_KEY, showUnprintables);
            }
        };
        ActionUtils.setupAction(viewNonprintablesAction, resourceBundle, "viewNonprintablesAction");
        viewNonprintablesAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
        viewNonprintablesAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, ActionUtils.getMetaMask()));

        viewNonprintablesToolbarAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean showUnprintables = editorProvider.changeShowNonprintables();
                viewNonprintablesAction.putValue(Action.SELECTED_KEY, showUnprintables);
            }
        };
        ActionUtils.setupAction(viewNonprintablesToolbarAction, resourceBundle, "viewNonprintablesToolbarAction");
        viewNonprintablesToolbarAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
    }

    public Action getViewNonprintablesAction() {
        return viewNonprintablesAction;
    }

    public Action getViewNonprintablesToolbarAction() {
        return viewNonprintablesToolbarAction;
    }
}
