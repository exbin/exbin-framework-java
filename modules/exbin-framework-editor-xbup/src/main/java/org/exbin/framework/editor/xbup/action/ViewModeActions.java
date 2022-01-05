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
package org.exbin.framework.editor.xbup.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.viewer.XbupSingleEditorProvider;
import org.exbin.framework.editor.xbup.viewer.ViewerTab;
import org.exbin.framework.editor.xbup.viewer.XbupFileHandler;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.utils.ActionUtils;

/**
 * View mode actions.
 *
 * @version 0.2.1 2021/09/25
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ViewModeActions {

    public static final String SHOW_VIEW_TAB_ACTION_ID = "showViewTabAction";
    public static final String SHOW_PROPERTIES_TAB_ACTION_ID = "showPropertiesTabAction";
    public static final String SHOW_TEXT_TAB_ACTION_ID = "showTextTabAction";
    public static final String SHOW_BINARY_TAB_ACTION_ID = "showBinaryTabAction";
    public static final String VIEW_MODE_RADIO_GROUP_ID = "viewTabRadioGroup";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action showViewTabAction;
    private Action showPropertiesTabAction;
    private Action showTextTabAction;
    private Action showBinaryTabAction;

    private ViewerTab viewTab = ViewerTab.VIEW;

    public ViewModeActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    public void setViewerTab(ViewerTab viewTab) {
        this.viewTab = viewTab;
        XbupFileHandler xbupFile = (XbupFileHandler) editorProvider.getActiveFile().get();
        xbupFile.switchToTab(viewTab);
    }

    @Nonnull
    public ViewerTab getViewTab() {
        return viewTab;
    }

    @Nonnull
    public Action getShowViewTabAction() {
        if (showViewTabAction == null) {
            showViewTabAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof XbupSingleEditorProvider) {
                        setViewerTab(ViewerTab.VIEW);
                    }
                }
            };
            ActionUtils.setupAction(showViewTabAction, resourceBundle, SHOW_VIEW_TAB_ACTION_ID);
            showViewTabAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            showViewTabAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
            showViewTabAction.putValue(Action.SELECTED_KEY, viewTab == ViewerTab.VIEW);
        }
        return showViewTabAction;
    }

    @Nonnull
    public Action getShowPropertiesTabAction() {
        if (showPropertiesTabAction == null) {
            showPropertiesTabAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof XbupSingleEditorProvider) {
                        setViewerTab(ViewerTab.PROPERTIES);
                    }
                }
            };
            ActionUtils.setupAction(showPropertiesTabAction, resourceBundle, SHOW_PROPERTIES_TAB_ACTION_ID);
            showPropertiesTabAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            showPropertiesTabAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
            showPropertiesTabAction.putValue(Action.SELECTED_KEY, viewTab == ViewerTab.VIEW);
        }
        return showPropertiesTabAction;
    }

    @Nonnull
    public Action getShowTextTabAction() {
        if (showTextTabAction == null) {
            showTextTabAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof XbupSingleEditorProvider) {
                        setViewerTab(ViewerTab.TEXT);
                    }
                }
            };
            ActionUtils.setupAction(showTextTabAction, resourceBundle, SHOW_TEXT_TAB_ACTION_ID);
            showTextTabAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            showTextTabAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
            showTextTabAction.putValue(Action.SELECTED_KEY, viewTab == ViewerTab.TEXT);

        }
        return showTextTabAction;
    }

    @Nonnull
    public Action getShowBinaryTabAction() {
        if (showBinaryTabAction == null) {
            showBinaryTabAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof XbupSingleEditorProvider) {
                        setViewerTab(ViewerTab.BINARY);
                    }
                }
            };
            ActionUtils.setupAction(showBinaryTabAction, resourceBundle, SHOW_BINARY_TAB_ACTION_ID);
            showBinaryTabAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
            showBinaryTabAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            showBinaryTabAction.putValue(Action.SELECTED_KEY, viewTab == ViewerTab.BINARY);
        }
        return showBinaryTabAction;
    }
}
