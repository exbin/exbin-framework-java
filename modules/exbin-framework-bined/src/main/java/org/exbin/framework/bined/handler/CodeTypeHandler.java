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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;
import org.exbin.bined.CodeType;
import org.exbin.bined.capability.CodeTypeCapable;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Code type handler.
 *
 * @version 0.2.0 2016/08/17
 * @author ExBin Project (http://exbin.org)
 */
public class CodeTypeHandler {

    public static String CODE_TYPE_RADIO_GROUP_ID = "codeTypeRadioGroup";

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action binaryCodeTypeAction;
    private Action octalCodeTypeAction;
    private Action decimalCodeTypeAction;
    private Action hexadecimalCodeTypeAction;
    private Action cycleCodeTypesAction;

    private CodeType codeType = CodeType.HEXADECIMAL;

    public CodeTypeHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        binaryCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    setCodeType(CodeType.BINARY);
                }
            }
        };
        ActionUtils.setupAction(binaryCodeTypeAction, resourceBundle, "binaryCodeTypeAction");
        binaryCodeTypeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        binaryCodeTypeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
        binaryCodeTypeAction.putValue(Action.SELECTED_KEY, codeType == CodeType.BINARY);

        octalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    setCodeType(CodeType.OCTAL);
                }
            }
        };
        ActionUtils.setupAction(octalCodeTypeAction, resourceBundle, "octalCodeTypeAction");
        octalCodeTypeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        octalCodeTypeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
        octalCodeTypeAction.putValue(Action.SELECTED_KEY, codeType == CodeType.OCTAL);

        decimalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    setCodeType(CodeType.DECIMAL);
                }
            }
        };
        ActionUtils.setupAction(decimalCodeTypeAction, resourceBundle, "decimalCodeTypeAction");
        decimalCodeTypeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
        decimalCodeTypeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        decimalCodeTypeAction.putValue(Action.SELECTED_KEY, codeType == CodeType.DECIMAL);

        hexadecimalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    setCodeType(CodeType.HEXADECIMAL);
                }
            }
        };
        ActionUtils.setupAction(hexadecimalCodeTypeAction, resourceBundle, "hexadecimalCodeTypeAction");
        hexadecimalCodeTypeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        hexadecimalCodeTypeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
        hexadecimalCodeTypeAction.putValue(Action.SELECTED_KEY, codeType == CodeType.HEXADECIMAL);

        cycleCodeTypesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    int codeTypePos = codeType.ordinal();
                    CodeType[] values = CodeType.values();
                    CodeType next = codeTypePos + 1 >= values.length ? values[0] : values[codeTypePos + 1];
                    setCodeType(next);
                }
            }
        };
        ActionUtils.setupAction(cycleCodeTypesAction, resourceBundle, "cycleCodeTypesAction");
        cycleCodeTypesAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CYCLE);
        ButtonGroup cycleButtonGroup = new ButtonGroup();
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        buttonGroups.put(CODE_TYPE_RADIO_GROUP_ID, cycleButtonGroup);
        JPopupMenu cycleCodeTypesPopupMenu = new JPopupMenu();
        cycleCodeTypesPopupMenu.add(ActionUtils.actionToMenuItem(binaryCodeTypeAction, buttonGroups));
        cycleCodeTypesPopupMenu.add(ActionUtils.actionToMenuItem(octalCodeTypeAction, buttonGroups));
        cycleCodeTypesPopupMenu.add(ActionUtils.actionToMenuItem(decimalCodeTypeAction, buttonGroups));
        cycleCodeTypesPopupMenu.add(ActionUtils.actionToMenuItem(hexadecimalCodeTypeAction, buttonGroups));
        cycleCodeTypesAction.putValue(ActionUtils.CYCLE_POPUP_MENU, cycleCodeTypesPopupMenu);
        updateCycleButtonName();
    }

    public void setCodeType(CodeType codeType) {
        this.codeType = codeType;
        switch (codeType) {
            case BINARY: {
                binaryCodeTypeAction.putValue(Action.SELECTED_KEY, Boolean.TRUE);
                break;
            }
            case OCTAL: {
                octalCodeTypeAction.putValue(Action.SELECTED_KEY, Boolean.TRUE);
                break;
            }
            case DECIMAL: {
                decimalCodeTypeAction.putValue(Action.SELECTED_KEY, Boolean.TRUE);
                break;
            }
            case HEXADECIMAL: {
                hexadecimalCodeTypeAction.putValue(Action.SELECTED_KEY, Boolean.TRUE);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected code tyoe " + codeType.name());
        }
        BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
        ((CodeTypeCapable) activePanel.getCodeArea()).setCodeType(codeType);
        updateCycleButtonName();
    }

    private void updateCycleButtonName() {
        cycleCodeTypesAction.putValue(Action.NAME, codeType.name().substring(0, 3));
    }

    public Action getBinaryCodeTypeAction() {
        return binaryCodeTypeAction;
    }

    public Action getOctalCodeTypeAction() {
        return octalCodeTypeAction;
    }

    public Action getDecimalCodeTypeAction() {
        return decimalCodeTypeAction;
    }

    public Action getHexadecimalCodeTypeAction() {
        return hexadecimalCodeTypeAction;
    }

    public Action getCycleCodeTypesAction() {
        return cycleCodeTypesAction;
    }
}
