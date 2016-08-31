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
import org.exbin.deltahex.CodeType;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Code type handler.
 *
 * @version 0.2.0 2016/07/18
 * @author ExBin Project (http://exbin.org)
 */
public class CodeTypeHandler {

    public static String CODE_TYPE_RADIO_GROUP_ID = "codeTypeRadioGroup";

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private int metaMask;

    private Action binaryCodeTypeAction;
    private Action octalCodeTypeAction;
    private Action decimalCodeTypeAction;
    private Action hexadecimalCodeTypeAction;

    private CodeType codeType = CodeType.HEXADECIMAL;

    public CodeTypeHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(DeltaHexModule.class);
    }

    public void init() {
        metaMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        binaryCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof HexEditorProvider) {
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
                if (editorProvider instanceof HexEditorProvider) {
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
                if (editorProvider instanceof HexEditorProvider) {
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
                if (editorProvider instanceof HexEditorProvider) {
                    setCodeType(CodeType.HEXADECIMAL);
                }
            }
        };
        ActionUtils.setupAction(hexadecimalCodeTypeAction, resourceBundle, "hexadecimalCodeTypeAction");
        hexadecimalCodeTypeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        hexadecimalCodeTypeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
        hexadecimalCodeTypeAction.putValue(Action.SELECTED_KEY, codeType == CodeType.HEXADECIMAL);

    }

    public void setCodeType(CodeType codeType) {
        this.codeType = codeType;
        HexPanel activePanel = ((HexEditorProvider) editorProvider).getDocument();
        activePanel.getCodeArea().setCodeType(codeType);
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
}
