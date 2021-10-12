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
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.picture.gui.ImagePanel;
import org.exbin.framework.editor.picture.gui.ImageResizePanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileHandlerApi;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.gui.utils.gui.DefaultControlPanel;

/**
 * Picture operation actions.
 *
 * @version 0.2.1 2021/09/25
 * @author ExBin Project (http://exbin.org)
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
                    Optional<FileHandlerApi> activeFile = editorProvider.getActiveFile();
                    if (activeFile.isEmpty()) {
                        throw new IllegalStateException();
                    }

                    ImagePanel imagePanel = (ImagePanel) activeFile.get().getComponent();

                    final ImageResizePanel imageResizePanel = new ImageResizePanel();
                    imageResizePanel.setResolution(imagePanel.getImageSize());
                    DefaultControlPanel controlPanel = new DefaultControlPanel(imageResizePanel.getResourceBundle());
                    JPanel dialogPanel = WindowUtils.createDialogPanel(imageResizePanel, controlPanel);
                    GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                    final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
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
