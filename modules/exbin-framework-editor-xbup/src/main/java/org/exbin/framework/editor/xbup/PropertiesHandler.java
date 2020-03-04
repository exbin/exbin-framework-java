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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.panel.BlockPropertiesPanel;
import org.exbin.framework.editor.xbup.panel.DocumentPropertiesPanel;
import org.exbin.framework.editor.xbup.viewer.DocumentViewerProvider;
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
 * @version 0.2.1 2020/03/02
 * @author ExBin Project (http://exbin.org)
 */
public class PropertiesHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action propertiesAction;
    private Action itemPropertiesAction;
    private boolean devMode;

    public PropertiesHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
    }

    public void init() {
        propertiesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof DocumentViewerProvider) {
                    DocumentViewerProvider provider = (DocumentViewerProvider) editorProvider;
                    GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                    DocumentPropertiesPanel propertiesPanel = new DocumentPropertiesPanel();
                    propertiesPanel.setDocument(provider.getDoc());
                    propertiesPanel.setDocumentUri(provider.getFileUri());
                    CloseControlPanel controlPanel = new CloseControlPanel();
                    JPanel dialogPanel = WindowUtils.createDialogPanel(propertiesPanel, controlPanel);
                    final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                    WindowUtils.addHeaderPanel(dialog.getWindow(), propertiesPanel.getClass(), propertiesPanel.getResourceBundle());
                    frameModule.setDialogTitle(dialog, propertiesPanel.getResourceBundle());
                    controlPanel.setHandler(() -> {
                        dialog.close();
                        dialog.dispose();
                    });
                    dialog.showCentered((Component) e.getSource());
                }
            }
        };
        ActionUtils.setupAction(propertiesAction, resourceBundle, "propertiesAction");
        propertiesAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        itemPropertiesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider instanceof DocumentViewerProvider) {
                    DocumentViewerProvider provider = (DocumentViewerProvider) editorProvider;
                    GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                    BlockPropertiesPanel panel = new BlockPropertiesPanel();
                    panel.setCatalog(provider.getCatalog());
                    panel.setDevMode(devMode);
                    panel.setTreeNode(provider.getSelectedItem());
                    CloseControlPanel controlPanel = new CloseControlPanel();
                    JPanel dialogPanel = WindowUtils.createDialogPanel(panel, controlPanel);
                    final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                    controlPanel.setHandler(() -> {
                        dialog.close();
                        dialog.dispose();
                    });
                    dialog.showCentered((Component) e.getSource());
                }
            }
        };
        ActionUtils.setupAction(itemPropertiesAction, resourceBundle, "itemPropertiesAction");
        itemPropertiesAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        itemPropertiesAction.setEnabled(false);
    }

    public Action getPropertiesAction() {
        return propertiesAction;
    }

    public Action getItemPropertiesAction() {
        return itemPropertiesAction;
    }

    void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    void setEditEnabled(boolean editEnabled) {
        itemPropertiesAction.setEnabled(editEnabled);
    }
}
