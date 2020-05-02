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

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.picture.gui.ImagePanel;
import org.exbin.framework.editor.picture.gui.ImageResizePanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.gui.utils.gui.DefaultControlPanel;

/**
 * Picture operation handler.
 *
 * @version 0.2.0 2016/02/06
 * @author ExBin Project (http://exbin.org)
 */
public class PictureOperationHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action imageResizeAction;

    public PictureOperationHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorPictureModule.class);
    }

    public void init() {
        imageResizeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof ImagePanel) {
                    final ImageResizePanel imageResizePanel = new ImageResizePanel();
                    imageResizePanel.setResolution(((ImagePanel) editorProvider).getImageSize());
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
                            ((ImagePanel) editorProvider).performResize(width, height);
                        }

                        dialog.close();
                    });
                    dialog.showCentered((Component) e.getSource());
                    dialog.dispose();
                }
            }
        };
        ActionUtils.setupAction(imageResizeAction, resourceBundle, "imageResizeAction");
        imageResizeAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public Action getRevertAction() {
        return imageResizeAction;
    }
}
