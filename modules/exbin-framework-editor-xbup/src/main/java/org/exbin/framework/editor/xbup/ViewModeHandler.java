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

    public static String VIEW_MODE_RADIO_GROUP_ID = "viewModeRadioGroup";

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action viewPreviewModeAction;
    private Action viewTextModeAction;
    private Action viewBinaryModeAction;

    private ViewerTab viewTab = ViewerTab.VIEW;

    public ViewModeHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
    }

    public void init() {
        viewPreviewModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof DocumentViewerProvider) {
                    setViewerTab(ViewerTab.VIEW);
                }
            }
        };
        ActionUtils.setupAction(viewPreviewModeAction, resourceBundle, "viewPreviewModeAction");
        viewPreviewModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        viewPreviewModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        viewPreviewModeAction.putValue(Action.SELECTED_KEY, viewTab == ViewerTab.VIEW);

        viewTextModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof DocumentViewerProvider) {
                    setViewerTab(ViewerTab.TEXT);
                }
            }
        };
        ActionUtils.setupAction(viewTextModeAction, resourceBundle, "viewTextModeAction");
        viewTextModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        viewTextModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        viewTextModeAction.putValue(Action.SELECTED_KEY, viewTab == ViewerTab.TEXT);

        viewBinaryModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof DocumentViewerProvider) {
                    setViewerTab(ViewerTab.BINARY);
                }
            }
        };
        ActionUtils.setupAction(viewBinaryModeAction, resourceBundle, "viewBinaryModeAction");
        viewBinaryModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        viewBinaryModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        viewBinaryModeAction.putValue(Action.SELECTED_KEY, viewTab == ViewerTab.BINARY);
    }

    public void setViewerTab(ViewerTab viewTab) {
        this.viewTab = viewTab;
        DocumentViewerProvider viewerProvider = (DocumentViewerProvider) editorProvider;
        viewerProvider.setViewerTab(viewTab);
    }

    public ViewerTab getViewTab() {
        return viewTab;
    }

    @Nonnull
    public Action getPreviewModeAction() {
        return viewPreviewModeAction;
    }

    @Nonnull
    public Action getTextModeAction() {
        return viewTextModeAction;
    }

    @Nonnull
    public Action getBinaryModeAction() {
        return viewBinaryModeAction;
    }
}
