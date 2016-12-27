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
package org.exbin.framework.deltahex;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Line wrapping handler.
 *
 * @version 0.2.0 2016/08/14
 * @author ExBin Project (http://exbin.org)
 */
public class LineWrappingHandler {

    private final HexEditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action viewLineWrapAction;

    public LineWrappingHandler(XBApplication application, HexEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(DeltaHexModule.class);
    }

    public void init() {
        viewLineWrapAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean lineWraping = editorProvider.changeLineWrap();
                viewLineWrapAction.putValue(Action.SELECTED_KEY, lineWraping);
            }
        };
        ActionUtils.setupAction(viewLineWrapAction, resourceBundle, "viewLineWrapAction");
        viewLineWrapAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
    }

    public Action getViewLineWrapAction() {
        return viewLineWrapAction;
    }
}
