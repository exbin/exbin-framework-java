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
package org.exbin.framework.bined.handler;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.basic.CodeAreaViewMode;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
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
        BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
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
