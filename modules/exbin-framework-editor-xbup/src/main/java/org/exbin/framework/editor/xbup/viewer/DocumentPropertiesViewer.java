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
package org.exbin.framework.editor.xbup.viewer;

import java.awt.BorderLayout;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.panel.DocumentViewerPanel;
import org.exbin.framework.editor.xbup.panel.XBPropertyPanel;
import org.exbin.framework.gui.service.catalog.panel.CatalogItemPanel;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.parser_tree.XBTTreeNode;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * Properties viewer of document.
 *
 * @version 0.2.1 2020/03/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DocumentPropertiesViewer implements DocumentViewer {

    private final JPanel panel = new JPanel();
    private final DocumentViewerPanel viewerPanel = new DocumentViewerPanel();
    private JSplitPane viewSplitPane;
    private final XBPropertyPanel propertiesPanel;
    private final CatalogItemPanel typePanel;

    public DocumentPropertiesViewer() {
        propertiesPanel = new XBPropertyPanel();

        viewSplitPane = new JSplitPane();
        viewSplitPane.setDividerLocation(250);
        viewSplitPane.setResizeWeight(1.0);
        viewSplitPane.setLeftComponent(viewerPanel);
        viewSplitPane.setRightComponent(propertiesPanel);

        panel.setLayout(new BorderLayout());
        panel.add(viewSplitPane, BorderLayout.CENTER);

        typePanel = new CatalogItemPanel();
    }

    @Override
    public void setSelectedItem(@Nullable XBTBlock item) {
        propertiesPanel.setActiveNode((XBTTreeNode) item);
        viewerPanel.removeAllViews();
        if (item != null) {
            // TODO custom viewers

            viewerPanel.addView("Type", typePanel);
            // typePanel.setItem(item);
        }
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public void performCut() {
        // textPanel.performCut();
    }

    @Override
    public void performCopy() {
        // textPanel.performCopy();
    }

    @Override
    public void performPaste() {
        // textPanel.performPaste();
    }

    @Override
    public void performDelete() {
        // textPanel.performDelete();
    }

    @Override
    public void performSelectAll() {
        // textPanel.performSelectAll();
    }

    @Override
    public boolean isSelection() {
        return false;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean canSelectAll() {
        return false;
    }

    @Override
    public boolean canPaste() {
        return false;
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
    }

    public void setCatalog(XBACatalog catalog) {
        propertiesPanel.setCatalog(catalog);
        typePanel.setCatalog(catalog);
    }

    public void setApplication(XBApplication application) {
        propertiesPanel.setApplication(application);
    }

    public void setPluginRepository(XBPluginRepository pluginRepository) {
        propertiesPanel.setPluginRepository(pluginRepository);
    }
}
