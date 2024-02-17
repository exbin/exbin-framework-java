/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.editor.picture.action;

import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.editor.picture.gui.ImagePanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileHandler;

/**
 * Zoom mode control actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ZoomControlActions {

    public static final String NORMAL_ZOOM_ACTION_ID = "normalZoomAction";
    public static final String ZOOM_UP_ACTION_ID = "zoomUpAction";
    public static final String ZOOM_DOWN_ACTION_ID = "zoomDownAction";
    public static final String ZOOM_RADIO_GROUP_ID = "zoomRadioGroup";

    private EditorProvider editorProvider;
    private ResourceBundle resourceBundle;

    public ZoomControlActions() {
    }

    public void setup(EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

    }

    @Nonnull
    public Action createNormalZoomAction() {
        AbstractAction normalZoomAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (!activeFile.isPresent()) {
                    throw new IllegalStateException();
                }

                ImagePanel imagePanel = (ImagePanel) activeFile.get().getComponent();
                imagePanel.setScale(1);
            }
        };
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(normalZoomAction, resourceBundle, NORMAL_ZOOM_ACTION_ID);
        return normalZoomAction;
    }

    @Nonnull
    public Action createZoomUpAction() {
        AbstractAction zoomUpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (!activeFile.isPresent()) {
                    throw new IllegalStateException();
                }

                ImagePanel imagePanel = (ImagePanel) activeFile.get().getComponent();
                imagePanel.setScale(imagePanel.getScale() / 2);
            }
        };
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(zoomUpAction, resourceBundle, ZOOM_UP_ACTION_ID);
        return zoomUpAction;
    }

    @Nonnull
    public Action createZoomDownAction() {
        AbstractAction zoomDownAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (!activeFile.isPresent()) {
                    throw new IllegalStateException();
                }

                ImagePanel imagePanel = (ImagePanel) activeFile.get().getComponent();
                imagePanel.setScale(imagePanel.getScale() * 2);
            }
        };
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(zoomDownAction, resourceBundle, ZOOM_DOWN_ACTION_ID);
        return zoomDownAction;
    }
}
