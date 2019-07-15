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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.wave.panel.AudioPanel;
import org.exbin.framework.editor.wave.panel.PropertiesPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.panel.CloseControlPanel;

/**
 * Properties handler.
 *
 * @version 0.2.1 2019/07/14
 * @author ExBin Project (http://exbin.org)
 */
public class PropertiesHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action propertiesAction;

    public PropertiesHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorWaveModule.class);
    }

    public void init() {
        propertiesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof AudioPanel) {
                    AudioPanel activePanel = (AudioPanel) editorProvider;
                    GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);

                    PropertiesPanel propertiesPanel = new PropertiesPanel();
                    propertiesPanel.setDocument(activePanel);
                    CloseControlPanel controlPanel = new CloseControlPanel();
                    JPanel dialogPanel = WindowUtils.createDialogPanel(propertiesPanel, controlPanel);

                    final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                    WindowUtils.addHeaderPanel(dialog.getWindow(), propertiesPanel.getClass(), propertiesPanel.getResourceBundle(), controlPanel);
                    frameModule.setDialogTitle(dialog, propertiesPanel.getResourceBundle());
                    controlPanel.setHandler(dialog::close);
                    dialog.showCentered((Component) e.getSource());
                    dialog.dispose();
                }
            }
        };
        ActionUtils.setupAction(propertiesAction, resourceBundle, "propertiesAction");
        propertiesAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Nonnull
    public Action getPropertiesAction() {
        return propertiesAction;
    }
}
