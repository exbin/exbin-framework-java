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
import org.exbin.bined.CodeAreaViewMode;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.panel.BinaryPanel;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * View mode handler.
 *
 * @version 0.2.0 2016/07/18
 * @author ExBin Project (http://exbin.org)
 */
public class ViewModeHandler {

    public static String VIEW_MODE_RADIO_GROUP_ID = "viewModeRadioGroup";

    private final BinaryEditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action dualModeAction;
    private Action codeMatrixModeAction;
    private Action textPreviewModeAction;

    private CodeAreaViewMode viewMode = CodeAreaViewMode.DUAL;

    public ViewModeHandler(XBApplication application, BinaryEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        dualModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    setViewMode(CodeAreaViewMode.DUAL);
                }
            }
        };
        ActionUtils.setupAction(dualModeAction, resourceBundle, "dualViewModeAction");
        dualModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        dualModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        dualModeAction.putValue(Action.SELECTED_KEY, viewMode == CodeAreaViewMode.DUAL);

        codeMatrixModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    setViewMode(CodeAreaViewMode.CODE_MATRIX);
                }
            }
        };
        ActionUtils.setupAction(codeMatrixModeAction, resourceBundle, "codeMatrixViewModeAction");
        codeMatrixModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        codeMatrixModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        codeMatrixModeAction.putValue(Action.SELECTED_KEY, viewMode == CodeAreaViewMode.CODE_MATRIX);

        textPreviewModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    setViewMode(CodeAreaViewMode.TEXT_PREVIEW);
                }
            }
        };
        ActionUtils.setupAction(textPreviewModeAction, resourceBundle, "textPreviewViewModeAction");
        textPreviewModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        textPreviewModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        textPreviewModeAction.putValue(Action.SELECTED_KEY, viewMode == CodeAreaViewMode.TEXT_PREVIEW);
    }

    public void setViewMode(CodeAreaViewMode viewMode) {
        this.viewMode = viewMode;
        BinaryPanel activePanel = ((BinaryEditorProvider) editorProvider).getDocument();
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