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

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.picture.gui.ImagePanel;
import org.exbin.framework.editor.picture.gui.ImageResizePanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.handler.DefaultControlHandler;
import org.exbin.framework.utils.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.file.api.FileHandler;

/**
 * Picture operation actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PictureOperationActions {

    public static final String IMAGE_RESIZE_ACTION_ID = "imageResizeAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action imageResizeAction;

    public PictureOperationActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action getRevertAction() {
        if (imageResizeAction == null) {
            imageResizeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (!activeFile.isPresent()) {
                        throw new IllegalStateException();
                    }

                    ImagePanel imagePanel = (ImagePanel) activeFile.get().getComponent();

                    final ImageResizePanel imageResizePanel = new ImageResizePanel();
                    imageResizePanel.setResolution(imagePanel.getImageSize());
                    DefaultControlPanel controlPanel = new DefaultControlPanel(imageResizePanel.getResourceBundle());
                    FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
                    final DialogWrapper dialog = frameModule.createDialog(imageResizePanel, controlPanel);
                    WindowUtils.addHeaderPanel(dialog.getWindow(), imageResizePanel.getClass(), imageResizePanel.getResourceBundle());
                    frameModule.setDialogTitle(dialog, imageResizePanel.getResourceBundle());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType == ControlActionType.OK) {
                            Point point = imageResizePanel.getResolution();
                            int width = (int) (point.getX());
                            int height = (int) (point.getY());
                            imagePanel.performResize(width, height);
                        }

                        dialog.close();
                    });
                    dialog.showCentered((Component) e.getSource());
                    dialog.dispose();
                }
            };

            ActionUtils.setupAction(imageResizeAction, resourceBundle, IMAGE_RESIZE_ACTION_ID);
            imageResizeAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        }
        return imageResizeAction;
    }
}
