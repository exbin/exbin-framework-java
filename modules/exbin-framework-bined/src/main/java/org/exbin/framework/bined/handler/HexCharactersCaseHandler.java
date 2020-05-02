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
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.capability.CodeCharactersCaseCapable;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Hex characters case handler.
 *
 * @version 0.2.1 2018/08/12
 * @author ExBin Project (http://exbin.org)
 */
public class HexCharactersCaseHandler {

    public static String HEX_CHARACTERS_CASE_RADIO_GROUP_ID = "hexCharactersCaseRadioGroup";

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action upperHexCharsAction;
    private Action lowerHexCharsAction;

    private CodeCharactersCase hexCharactersCase = CodeCharactersCase.UPPER;

    public HexCharactersCaseHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        upperHexCharsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    setHexCharactersCase(CodeCharactersCase.UPPER);
                }
            }
        };
        ActionUtils.setupAction(upperHexCharsAction, resourceBundle, "upperHexCharactersAction");
        upperHexCharsAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        upperHexCharsAction.putValue(ActionUtils.ACTION_RADIO_GROUP, HEX_CHARACTERS_CASE_RADIO_GROUP_ID);
        upperHexCharsAction.putValue(Action.SELECTED_KEY, hexCharactersCase == CodeCharactersCase.UPPER);

        lowerHexCharsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof BinaryEditorProvider) {
                    setHexCharactersCase(CodeCharactersCase.LOWER);
                }
            }
        };
        ActionUtils.setupAction(lowerHexCharsAction, resourceBundle, "lowerHexCharactersAction");
        lowerHexCharsAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        lowerHexCharsAction.putValue(ActionUtils.ACTION_RADIO_GROUP, HEX_CHARACTERS_CASE_RADIO_GROUP_ID);
        lowerHexCharsAction.putValue(Action.SELECTED_KEY, hexCharactersCase == CodeCharactersCase.LOWER);
    }

    public void setHexCharactersCase(CodeCharactersCase hexCharactersCase) {
        this.hexCharactersCase = hexCharactersCase;
        BinEdComponentPanel activePanel = ((BinaryEditorProvider) editorProvider).getComponentPanel();
        ((CodeCharactersCaseCapable) activePanel.getCodeArea()).setCodeCharactersCase(hexCharactersCase);
    }

    public Action getUpperHexCharsAction() {
        return upperHexCharsAction;
    }

    public Action getLowerHexCharsAction() {
        return lowerHexCharsAction;
    }
}
