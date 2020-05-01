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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Show values panel handler.
 *
 * @version 0.2.0 2019/08/18
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ShowValuesPanelHandler {

    private final BinaryEditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action showValuesPanelAction;

    public ShowValuesPanelHandler(XBApplication application, BinaryEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        showValuesPanelAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setShowValuesPanel(!editorProvider.isShowValuesPanel());
            }
        };
        ActionUtils.setupAction(showValuesPanelAction, resourceBundle, "showValuesPanelAction");
        showValuesPanelAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
    }

    public void setShowValuesPanel(boolean show) {
        editorProvider.setShowValuesPanel(show);
        showValuesPanelAction.putValue(Action.SELECTED_KEY, show);
    }

    @Nonnull
    public Action getShowValuesPanelAction() {
        return showValuesPanelAction;
    }
}
