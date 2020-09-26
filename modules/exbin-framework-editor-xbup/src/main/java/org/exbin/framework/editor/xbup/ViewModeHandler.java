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
package org.exbin.framework.editor.xbup;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.viewer.DocumentViewerProvider;
import org.exbin.framework.editor.xbup.viewer.DocumentViewerProvider.ViewerTab;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * View mode handler.
 *
 * @version 0.2.1 2020/03/03
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ViewModeHandler {

    public static String VIEW_MODE_RADIO_GROUP_ID = "viewTabRadioGroup";

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action showViewTabAction;
    private Action showPropertiesTabAction;
    private Action showTextTabAction;
    private Action showBinaryTabAction;

    private ViewerTab viewTab = ViewerTab.VIEW;

    public ViewModeHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
    }

    public void init() {
        showViewTabAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof DocumentViewerProvider) {
                    setViewerTab(ViewerTab.VIEW);
                }
            }
        };
        ActionUtils.setupAction(showViewTabAction, resourceBundle, "showViewTabAction");
        showViewTabAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        showViewTabAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        showViewTabAction.putValue(Action.SELECTED_KEY, viewTab == ViewerTab.VIEW);

        showPropertiesTabAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof DocumentViewerProvider) {
                    setViewerTab(ViewerTab.PROPERTIES);
                }
            }
        };
        ActionUtils.setupAction(showPropertiesTabAction, resourceBundle, "showPropertiesTabAction");
        showPropertiesTabAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        showPropertiesTabAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        showPropertiesTabAction.putValue(Action.SELECTED_KEY, viewTab == ViewerTab.VIEW);

        showTextTabAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof DocumentViewerProvider) {
                    setViewerTab(ViewerTab.TEXT);
                }
            }
        };
        ActionUtils.setupAction(showTextTabAction, resourceBundle, "showTextTabAction");
        showTextTabAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        showTextTabAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        showTextTabAction.putValue(Action.SELECTED_KEY, viewTab == ViewerTab.TEXT);

        showBinaryTabAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof DocumentViewerProvider) {
                    setViewerTab(ViewerTab.BINARY);
                }
            }
        };
        ActionUtils.setupAction(showBinaryTabAction, resourceBundle, "showBinaryTabAction");
        showBinaryTabAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        showBinaryTabAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        showBinaryTabAction.putValue(Action.SELECTED_KEY, viewTab == ViewerTab.BINARY);
    }

    public void setViewerTab(ViewerTab viewTab) {
        this.viewTab = viewTab;
        DocumentViewerProvider viewerProvider = (DocumentViewerProvider) editorProvider;
        viewerProvider.switchToTab(viewTab);
    }

    @Nonnull
    public ViewerTab getViewTab() {
        return viewTab;
    }

    @Nonnull
    public Action getShowViewTabAction() {
        return showViewTabAction;
    }

    @Nonnull
    public Action getShowPropertiesTabAction() {
        return showPropertiesTabAction;
    }

    @Nonnull
    public Action getShowTextTabAction() {
        return showTextTabAction;
    }

    @Nonnull
    public Action getShowBinaryTabAction() {
        return showBinaryTabAction;
    }
}
