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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.core.block.XBTBlock;

/**
 * Custom viewer of document.
 *
 * @version 0.2.1 2020/03/01
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DocumentCustomViewer implements DocumentViewer {

    private JComponent customPanel;

    public DocumentCustomViewer() {
        customPanel = new JPanel();
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        return customPanel;
    }

    @Override
    public void setSelectedItem(XBTBlock item) {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
    }
}
