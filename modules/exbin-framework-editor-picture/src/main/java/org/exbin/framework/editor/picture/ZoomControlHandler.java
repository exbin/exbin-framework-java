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
package org.exbin.framework.editor.picture;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.picture.gui.ImagePanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Zoom mode control handler.
 *
 * @version 0.2.0 2016/02/06
 * @author ExBin Project (http://exbin.org)
 */
public class ZoomControlHandler {

    public static String ZOOM_RADIO_GROUP_ID = "zoomRadioGroup";

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action normalZoomAction;
    private Action zoomUpAction;
    private Action zoomDownAction;

    public ZoomControlHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorPictureModule.class);
    }

    public void init() {
        normalZoomAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof ImagePanel) {
                    ImagePanel activePanel = (ImagePanel) editorProvider;
                    activePanel.setScale(1);
                }
            }
        };
        ActionUtils.setupAction(normalZoomAction, resourceBundle, "normalZoomAction");

        zoomUpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof ImagePanel) {
                    ImagePanel activePanel = (ImagePanel) editorProvider;
                    activePanel.setScale(activePanel.getScale() / 2);
                }
            }
        };
        ActionUtils.setupAction(zoomUpAction, resourceBundle, "zoomUpAction");

        zoomDownAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof ImagePanel) {
                    ImagePanel activePanel = (ImagePanel) editorProvider;
                    activePanel.setScale(activePanel.getScale() * 2);
                }
            }
        };
        ActionUtils.setupAction(zoomDownAction, resourceBundle, "zoomDownAction");
    }

    public Action getNormalZoomAction() {
        return normalZoomAction;
    }

    public Action getZoomUpAction() {
        return zoomUpAction;
    }

    public Action getZoomDownAction() {
        return zoomDownAction;
    }
}
