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
package org.exbin.framework.bined;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Tools options action handler.
 *
 * @version 0.2.1 2019/06/18
 * @author ExBin Project (http://exbin.org)
 */
public class ToolsOptionsHandler {

    private int metaMask;
    private final ResourceBundle resourceBundle;

    private Action toolsSetFontAction;

    private final BinaryEditorProvider editorProvider;
    private final XBApplication application;

    public ToolsOptionsHandler(XBApplication application, BinaryEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        toolsSetFontAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                org.exbin.framework.editor.text.ToolsOptionsHandler textOptionsHandler = new org.exbin.framework.editor.text.ToolsOptionsHandler(application, editorProvider);
                textOptionsHandler.init();
                Action textToolsSetFontAction = textOptionsHandler.getToolsSetFontAction();
                textToolsSetFontAction.actionPerformed(e);
            }
        };
        ActionUtils.setupAction(toolsSetFontAction, resourceBundle, "toolsSetFontAction");
        toolsSetFontAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public Action getToolsSetFontAction() {
        return toolsSetFontAction;
    }
}
