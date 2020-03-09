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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.exbin.framework.bined.panel.PropertiesPanel;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.core.block.XBTBlock;

/**
 * Properties viewer of document.
 *
 * @version 0.2.1 2020/02/29
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DocumentPropertiesViewer implements DocumentViewer {
    
    private final JPanel panel = new JPanel();
    private final PropertiesPanel propertiesPanel;

    public DocumentPropertiesViewer() {
        propertiesPanel = new PropertiesPanel();
    }

    @Override
    public void setSelectedItem(XBTBlock item) {
        // TODO
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
}
