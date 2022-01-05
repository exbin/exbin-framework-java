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
package org.exbin.framework.editor.xbup.viewer;

import java.awt.BorderLayout;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.gui.DocumentViewerPanel;
import org.exbin.framework.editor.xbup.gui.GeneralPropertiesPanel;
import org.exbin.framework.editor.xbup.gui.SimpleMessagePanel;
import org.exbin.framework.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * Properties viewer of document.
 *
 * @version 0.2.1 2020/09/22
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PropertiesDocumentTab implements DocumentTab {

    private final JPanel panel = new JPanel();
    private final DocumentViewerPanel viewerPanel = new DocumentViewerPanel();
    private final GeneralPropertiesPanel generalPanel = new GeneralPropertiesPanel();
    private XBACatalog catalog;

    public PropertiesDocumentTab() {
        panel.setLayout(new BorderLayout());
        panel.add(viewerPanel, BorderLayout.CENTER);

        SimpleMessagePanel messagePanel = new SimpleMessagePanel();
        viewerPanel.setBorderComponent(messagePanel);
    }

    @Override
    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        generalPanel.setCatalog(catalog);
    }

    @Override
    public void setApplication(XBApplication application) {
        generalPanel.setApplication(application);
    }

    @Override
    public void setPluginRepository(XBPluginRepository pluginRepository) {
    }
    
    public void setDevMode(boolean devMode) {
        generalPanel.setDevMode(devMode);
    }

    @Override
    public void setSelectedItem(@Nullable XBTBlock block) {
        viewerPanel.removeAllViews();
        if (block != null) {
            viewerPanel.addView("General", generalPanel);
            viewerPanel.viewsAdded();
            generalPanel.setBlock(block);
        }

        viewerPanel.revalidate();
        viewerPanel.repaint();
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public void performCut() {
    }

    @Override
    public void performCopy() {
    }

    @Override
    public void performPaste() {
    }

    @Override
    public void performDelete() {
    }

    @Override
    public void performSelectAll() {
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
    public boolean canDelete() {
        return false;
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
    }

    @Override
    public void setActivationListener(ActivationListener listener) {
    }
}
