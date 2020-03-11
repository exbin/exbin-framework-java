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

import javax.swing.JComponent;
import org.exbin.auxiliary.paged_data.ByteArrayData;
import org.exbin.framework.bined.panel.BinEdComponentPanel;
import org.exbin.framework.bined.panel.BinaryStatusPanel;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.core.block.XBTBlock;

/**
 * Binary viewer of document.
 *
 * @version 0.2.1 2020/03/07
 * @author ExBin Project (http://exbin.org)
 */
public class DocumentBinaryViewer implements DocumentViewer {

    private final BinEdComponentPanel binaryPanel;
    private final BinaryStatusPanel binaryStatusPanel;

    public DocumentBinaryViewer() {
        binaryPanel = new BinEdComponentPanel();
        binaryStatusPanel = new BinaryStatusPanel();
        binaryPanel.registerBinaryStatus(binaryStatusPanel);
        // binaryPanel.setNoBorder();
        init();
    }
    
    public void init() {
        
    }

    @Override
    public void setSelectedItem(XBTBlock item) {
        binaryPanel.setContentData(new ByteArrayData(new byte[] { 64 }));
    }

    @Override
    public JComponent getComponent() {
        return binaryPanel;
    }

    @Override
    public void performCut() {
        binaryPanel.performCut();
    }

    @Override
    public void performCopy() {
        binaryPanel.performCopy();
    }

    @Override
    public void performPaste() {
        binaryPanel.performPaste();
    }

    @Override
    public void performDelete() {
        binaryPanel.performDelete();
    }

    @Override
    public void performSelectAll() {
        binaryPanel.performSelectAll();
    }

    @Override
    public boolean isSelection() {
        return binaryPanel.isSelection();
    }

    @Override
    public boolean isEditable() {
        return binaryPanel.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return binaryPanel.canSelectAll();
    }

    @Override
    public boolean canPaste() {
        return binaryPanel.canPaste();
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        binaryPanel.setUpdateListener(updateListener);
    }
}
