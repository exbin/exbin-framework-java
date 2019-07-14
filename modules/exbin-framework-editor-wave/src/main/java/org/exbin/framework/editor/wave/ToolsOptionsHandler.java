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
package org.exbin.framework.editor.wave;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.wave.panel.AudioPanel;
import org.exbin.framework.editor.wave.options.panel.WaveColorPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.gui.utils.panel.DefaultControlPanel;
import org.exbin.framework.editor.wave.service.WaveColorService;
import org.exbin.framework.editor.wave.service.impl.WaveColorServiceImpl;

/**
 * Tools options action handler.
 *
 * @version 0.2.1 2019/07/13
 * @author ExBin Project (http://exbin.org)
 */
public class ToolsOptionsHandler {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(EditorWaveModule.class);

    private Action toolsSetColorAction;

    private final EditorProvider editorProvider;
    private final XBApplication application;

    public ToolsOptionsHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
    }

    public void init() {
        toolsSetColorAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);

                WaveColorService textColorPanelApi = new WaveColorServiceImpl(editorProvider);

                final WaveColorPanel waveColorPanel = new WaveColorPanel(textColorPanelApi);
                waveColorPanel.setWaveColorsFromArray(((AudioPanel) editorProvider).getAudioPanelColors());
                DefaultControlPanel controlPanel = new DefaultControlPanel(waveColorPanel.getResourceBundle());
                JPanel dialogPanel = WindowUtils.createDialogPanel(waveColorPanel, controlPanel);
                final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                WindowUtils.addHeaderPanel(dialog.getWindow(), waveColorPanel.getClass(), waveColorPanel.getResourceBundle());
                frameModule.setDialogTitle(dialog, waveColorPanel.getResourceBundle());
                controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                    if (actionType == ControlActionType.OK) {
                        ((AudioPanel) editorProvider).setAudioPanelColors(waveColorPanel.getWaveColorsAsArray());
                    }

                    dialog.close();
                });
                WindowUtils.assignGlobalKeyListener(dialog.getWindow(), controlPanel.createOkCancelListener());
                dialog.center(dialog.getParent());
                dialog.show();
            }
        };
        ActionUtils.setupAction(toolsSetColorAction, resourceBundle, "toolsSetColorAction");
        toolsSetColorAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public Action getToolsSetColorAction() {
        return toolsSetColorAction;
    }
}
