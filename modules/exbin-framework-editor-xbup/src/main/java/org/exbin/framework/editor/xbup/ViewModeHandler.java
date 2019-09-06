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
package org.exbin.framework.editor.xbup;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.panel.XBDocumentPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * View mode handler.
 *
 * @version 0.2.1 2019/09/06
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

    private XBDocumentPanel.PanelMode viewMode = XBDocumentPanel.PanelMode.PREVIEW;

    public ViewModeHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
    }

    public void init() {
        viewPreviewModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof XBDocumentPanel) {
                    setViewMode(XBDocumentPanel.PanelMode.PREVIEW);
                }
            }
        };
        ActionUtils.setupAction(viewPreviewModeAction, resourceBundle, "viewPreviewModeAction");
        viewPreviewModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        viewPreviewModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        viewPreviewModeAction.putValue(Action.SELECTED_KEY, viewMode == XBDocumentPanel.PanelMode.PREVIEW);

        viewTextModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof XBDocumentPanel) {
                    setViewMode(XBDocumentPanel.PanelMode.TEXT);
                }
            }
        };
        ActionUtils.setupAction(viewTextModeAction, resourceBundle, "viewTextModeAction");
        viewTextModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        viewTextModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        viewTextModeAction.putValue(Action.SELECTED_KEY, viewMode == XBDocumentPanel.PanelMode.TEXT);

        viewBinaryModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof XBDocumentPanel) {
                    setViewMode(XBDocumentPanel.PanelMode.BINARY);
                }
            }
        };
        ActionUtils.setupAction(viewBinaryModeAction, resourceBundle, "viewBinaryModeAction");
        viewBinaryModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
        viewBinaryModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        viewBinaryModeAction.putValue(Action.SELECTED_KEY, viewMode == XBDocumentPanel.PanelMode.BINARY);
    }

    public void setViewMode(XBDocumentPanel.PanelMode viewMode) {
        this.viewMode = viewMode;
        XBDocumentPanel activePanel = (XBDocumentPanel) editorProvider;
        activePanel.setMode(viewMode);
    }

    public XBDocumentPanel.PanelMode getViewMode() {
        return viewMode;
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
