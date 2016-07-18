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
import org.exbin.deltahex.CodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.gui.editor.api.XBEditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;

/**
 * View mode handler.
 *
 * @version 0.2.0 2016/07/18
 * @author ExBin Project (http://exbin.org)
 */
public class ViewModeHandler {

    public static String VIEW_MODE_RADIO_GROUP_ID = "viewModeRadioGroup";

    private final XBEditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private int metaMask;

    private Action dualModeAction;
    private Action codeMatrixModeAction;
    private Action textPreviewModeAction;

    private CodeArea.ViewMode viewMode = CodeArea.ViewMode.DUAL;

    public ViewModeHandler(XBApplication application, XBEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = ActionUtils.getResourceBundleByClass(DeltaHexModule.class);
    }

    public void init() {
        metaMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        dualModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof HexPanel) {
                    setViewMode(CodeArea.ViewMode.DUAL);
                }
            }
        };
        ActionUtils.setupAction(dualModeAction, resourceBundle, "dualViewModeAction");
        dualModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        dualModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        dualModeAction.putValue(Action.SELECTED_KEY, viewMode == CodeArea.ViewMode.DUAL);

        codeMatrixModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof HexPanel) {
                    setViewMode(CodeArea.ViewMode.CODE_MATRIX);
                }
            }
        };
        ActionUtils.setupAction(codeMatrixModeAction, resourceBundle, "codeMatrixViewModeAction");
        codeMatrixModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        codeMatrixModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        codeMatrixModeAction.putValue(Action.SELECTED_KEY, viewMode == CodeArea.ViewMode.CODE_MATRIX);

        textPreviewModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof HexPanel) {
                    setViewMode(CodeArea.ViewMode.TEXT_PREVIEW);
                }
            }
        };
        ActionUtils.setupAction(textPreviewModeAction, resourceBundle, "textPreviewViewModeAction");
        textPreviewModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        textPreviewModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        textPreviewModeAction.putValue(Action.SELECTED_KEY, viewMode == CodeArea.ViewMode.TEXT_PREVIEW);
    }

    public void setViewMode(CodeArea.ViewMode viewMode) {
        this.viewMode = viewMode;
        HexPanel activePanel = (HexPanel) editorProvider;
        activePanel.getCodeArea().setViewMode(viewMode);
    }

    public Action getDualModeAction() {
        return dualModeAction;
    }

    public Action getCodeMatrixModeAction() {
        return codeMatrixModeAction;
    }

    public Action getTextPreviewModeAction() {
        return textPreviewModeAction;
    }
}
