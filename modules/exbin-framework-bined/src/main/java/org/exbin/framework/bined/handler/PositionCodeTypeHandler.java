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
import org.exbin.bined.PositionCodeType;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.panel.BinEdComponentPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Position code type handler.
 *
 * @version 0.2.0 2016/07/18
 * @author ExBin Project (http://exbin.org)
 */
public class PositionCodeTypeHandler {

    public static String POSITION_CODE_TYPE_RADIO_GROUP_ID = "positionCodeTypeRadioGroup";

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action octalPositionCodeTypeAction;
    private Action decimalPositionCodeTypeAction;
    private Action hexadecimalPositionCodeTypeAction;

    private PositionCodeType positionCodeType = PositionCodeType.HEXADECIMAL;

    public PositionCodeTypeHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        octalPositionCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    setCodeType(PositionCodeType.OCTAL);
                }
            }
        };
        ActionUtils.setupAction(octalPositionCodeTypeAction, resourceBundle, "octalPositionCodeTypeAction");
        octalPositionCodeTypeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        octalPositionCodeTypeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, POSITION_CODE_TYPE_RADIO_GROUP_ID);
        octalPositionCodeTypeAction.putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.OCTAL);

        decimalPositionCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    setCodeType(PositionCodeType.DECIMAL);
                }
            }
        };
        ActionUtils.setupAction(decimalPositionCodeTypeAction, resourceBundle, "decimalPositionCodeTypeAction");
        decimalPositionCodeTypeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, POSITION_CODE_TYPE_RADIO_GROUP_ID);
        decimalPositionCodeTypeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        decimalPositionCodeTypeAction.putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.DECIMAL);

        hexadecimalPositionCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    setCodeType(PositionCodeType.HEXADECIMAL);
                }
            }
        };
        ActionUtils.setupAction(hexadecimalPositionCodeTypeAction, resourceBundle, "hexadecimalPositionCodeTypeAction");
        hexadecimalPositionCodeTypeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        hexadecimalPositionCodeTypeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, POSITION_CODE_TYPE_RADIO_GROUP_ID);
        hexadecimalPositionCodeTypeAction.putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.HEXADECIMAL);

    }

    public void setCodeType(PositionCodeType codeType) {
        this.positionCodeType = codeType;
        BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
        activePanel.getCodeArea().setPositionCodeType(codeType);
    }

    public Action getOctalCodeTypeAction() {
        return octalPositionCodeTypeAction;
    }

    public Action getDecimalCodeTypeAction() {
        return decimalPositionCodeTypeAction;
    }

    public Action getHexadecimalCodeTypeAction() {
        return hexadecimalPositionCodeTypeAction;
    }
}
