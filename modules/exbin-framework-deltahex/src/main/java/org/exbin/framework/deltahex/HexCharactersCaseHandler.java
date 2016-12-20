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
import org.exbin.deltahex.HexCharactersCase;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Hex characters case handler.
 *
 * @version 0.2.0 2016/07/18
 * @author ExBin Project (http://exbin.org)
 */
public class HexCharactersCaseHandler {

    public static String HEX_CHARACTERS_CASE_RADIO_GROUP_ID = "hexCharactersCaseRadioGroup";

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private int metaMask;

    private Action upperHexCharsAction;
    private Action lowerHexCharsAction;

    private HexCharactersCase hexCharactersCase = HexCharactersCase.UPPER;

    public HexCharactersCaseHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(DeltaHexModule.class);
    }

    public void init() {
        metaMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        upperHexCharsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof HexEditorProvider) {
                    setHexCharactersCase(HexCharactersCase.UPPER);
                }
            }
        };
        ActionUtils.setupAction(upperHexCharsAction, resourceBundle, "upperHexCharactersAction");
        upperHexCharsAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        upperHexCharsAction.putValue(ActionUtils.ACTION_RADIO_GROUP, HEX_CHARACTERS_CASE_RADIO_GROUP_ID);
        upperHexCharsAction.putValue(Action.SELECTED_KEY, hexCharactersCase == HexCharactersCase.UPPER);

        lowerHexCharsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof HexEditorProvider) {
                    setHexCharactersCase(HexCharactersCase.LOWER);
                }
            }
        };
        ActionUtils.setupAction(lowerHexCharsAction, resourceBundle, "lowerHexCharactersAction");
        lowerHexCharsAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        lowerHexCharsAction.putValue(ActionUtils.ACTION_RADIO_GROUP, HEX_CHARACTERS_CASE_RADIO_GROUP_ID);
        lowerHexCharsAction.putValue(Action.SELECTED_KEY, hexCharactersCase == HexCharactersCase.LOWER);
    }

    public void setHexCharactersCase(HexCharactersCase hexCharactersCase) {
        this.hexCharactersCase = hexCharactersCase;
        HexPanel activePanel = ((HexEditorProvider) editorProvider).getDocument();
        activePanel.getCodeArea().setHexCharactersCase(hexCharactersCase);
    }

    public Action getUpperHexCharsAction() {
        return upperHexCharsAction;
    }

    public Action getLowerHexCharsAction() {
        return lowerHexCharsAction;
    }
}
