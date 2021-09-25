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
package org.exbin.framework.editor.wave.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;

/**
 * Zoom mode control actions.
 *
 * @version 0.2.0 2021/09/25
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ZoomControlActions {

    public static final String NORMAL_ZOOM_ACTION_ID = "normalZoomAction";
    public static final String ZOOM_UP_ACTION_ID = "zoomUpAction";
    public static final String ZOOM_DOWN_ACTION_ID = "zoomDownAction";
    public static final String ZOOM_RADIO_GROUP_ID = "zoomRadioGroup";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action normalZoomAction;
    private Action zoomUpAction;
    private Action zoomDownAction;

    public ZoomControlActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action getNormalZoomAction() {
        if (normalZoomAction == null) {
            normalZoomAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof AudioPanel) {
                        AudioPanel activePanel = (AudioPanel) editorProvider;
                        activePanel.scaleAndSeek(1);
                    }
                }
            };
            ActionUtils.setupAction(normalZoomAction, resourceBundle, NORMAL_ZOOM_ACTION_ID);
        }
        return normalZoomAction;
    }

    @Nonnull
    public Action getZoomUpAction() {
        if (zoomUpAction == null) {
            zoomUpAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof AudioPanel) {
                        AudioPanel activePanel = (AudioPanel) editorProvider;
                        activePanel.scaleAndSeek(activePanel.getScale() / 2);
                    }
                }
            };
            ActionUtils.setupAction(zoomUpAction, resourceBundle, ZOOM_UP_ACTION_ID);

        }
        return zoomUpAction;
    }

    @Nonnull
    public Action getZoomDownAction() {
        if (zoomDownAction == null) {
            zoomDownAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof AudioPanel) {
                        AudioPanel activePanel = (AudioPanel) editorProvider;
                        activePanel.scaleAndSeek(activePanel.getScale() * 2);
                    }
                }
            };
            ActionUtils.setupAction(zoomDownAction, resourceBundle, ZOOM_DOWN_ACTION_ID);

        }
        return zoomDownAction;
    }
}
