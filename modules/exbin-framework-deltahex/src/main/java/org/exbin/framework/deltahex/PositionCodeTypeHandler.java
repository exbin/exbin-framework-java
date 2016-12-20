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
import org.exbin.deltahex.PositionCodeType;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.deltahex.panel.HexPanel;
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

    private int metaMask;

    private Action octalPositionCodeTypeAction;
    private Action decimalPositionCodeTypeAction;
    private Action hexadecimalPositionCodeTypeAction;

    private PositionCodeType positionCodeType = PositionCodeType.HEXADECIMAL;

    public PositionCodeTypeHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(DeltaHexModule.class);
    }

    public void init() {
        metaMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        octalPositionCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof HexEditorProvider) {
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
                if (editorProvider instanceof HexEditorProvider) {
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
                if (editorProvider instanceof HexEditorProvider) {
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
        HexPanel activePanel = ((HexEditorProvider) editorProvider).getDocument();
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
