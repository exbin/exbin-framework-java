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
import org.exbin.bined.extended.layout.ExtendedCodeAreaLayoutProfile;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Code area theme handler.
 *
 * @version 0.2.1 2019/07/06
 * @author ExBin Project (http://exbin.org)
 */
public class LayoutHandler {

    private final BinaryEditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action showHeaderAction;
    private Action showRowPositionAction;

    public LayoutHandler(XBApplication application, BinaryEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        showHeaderAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtendedCodeAreaLayoutProfile layoutProfile = editorProvider.getCodeArea().getLayoutProfile();
                layoutProfile.setShowHeader(!editorProvider.getCodeArea().getLayoutProfile().isShowHeader());
                editorProvider.getCodeArea().setLayoutProfile(layoutProfile);
            }
        };
        ActionUtils.setupAction(showHeaderAction, resourceBundle, "showHeaderAction");
        showHeaderAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
        showHeaderAction.putValue(Action.SELECTED_KEY, editorProvider.getCodeArea().getLayoutProfile().isShowHeader());

        showRowPositionAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtendedCodeAreaLayoutProfile layoutProfile = editorProvider.getCodeArea().getLayoutProfile();
                layoutProfile.setShowRowPosition(!editorProvider.getCodeArea().getLayoutProfile().isShowRowPosition());
                editorProvider.getCodeArea().setLayoutProfile(layoutProfile);
            }
        };
        ActionUtils.setupAction(showRowPositionAction, resourceBundle, "showRowPositionAction");
        showRowPositionAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
        showRowPositionAction.putValue(Action.SELECTED_KEY, editorProvider.getCodeArea().getLayoutProfile().isShowRowPosition());
    }

    public Action getShowHeaderAction() {
        return showHeaderAction;
    }

    public Action getShowRowPositionAction() {
        return showRowPositionAction;
    }
}
