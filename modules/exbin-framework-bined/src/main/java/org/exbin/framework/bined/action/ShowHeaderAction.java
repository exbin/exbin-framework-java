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
package org.exbin.framework.bined.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.extended.layout.ExtendedCodeAreaLayoutProfile;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;

/**
 * Show header action.
 *
 * @version 0.2.1 2021/09/24
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ShowHeaderAction extends AbstractAction {

    public static final String ACTION_ID = "showHeaderAction";

    private BinaryEditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    public ShowHeaderAction() {
    }

    public void setup(XBApplication application, BinaryEditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
        putValue(Action.SELECTED_KEY, editorProvider.getCodeArea().getLayoutProfile().isShowHeader());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ExtendedCodeAreaLayoutProfile layoutProfile = editorProvider.getCodeArea().getLayoutProfile();
        layoutProfile.setShowHeader(!editorProvider.getCodeArea().getLayoutProfile().isShowHeader());
        editorProvider.getCodeArea().setLayoutProfile(layoutProfile);
    }
}