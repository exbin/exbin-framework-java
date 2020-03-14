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
package org.exbin.framework.editor.xbup.action;

import java.awt.event.ActionEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.panel.BlockPropertiesPanel;
import org.exbin.framework.editor.xbup.viewer.DocumentViewerProvider;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.panel.CloseControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;

/**
 * Item properties action.
 *
 * @version 0.2.0 2016/03/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ItemPropertiesAction extends AbstractAction {

    private final DocumentViewerProvider viewerProvider;
    private boolean devMode = false;

    public ItemPropertiesAction(DocumentViewerProvider viewerProvider) {
        this.viewerProvider = viewerProvider;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        XBApplication application = viewerProvider.getApplication();
        XBACatalog catalog = viewerProvider.getCatalog();
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        BlockPropertiesPanel panel = new BlockPropertiesPanel();
        panel.setCatalog(catalog);
        panel.setDevMode(devMode);
        panel.setTreeNode(viewerProvider.getSelectedItem());
        CloseControlPanel controlPanel = new CloseControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(panel, controlPanel);
        final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        controlPanel.setHandler(() -> {
            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(viewerProvider.getPanel());
    }
    
    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }
}
