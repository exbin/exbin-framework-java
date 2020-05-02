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

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.gui.BinaryStatusPanel;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.parser_tree.XBTTreeNode;

/**
 * Binary viewer of document.
 *
 * @version 0.2.1 2020/03/12
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

    private void init() {

    }

    @Override
    public void setSelectedItem(@Nullable XBTBlock item) {
        ByteArrayEditableData byteArrayData = null;
        if (item != null) {
            byteArrayData = new ByteArrayEditableData();
            try (OutputStream dataOutputStream = byteArrayData.getDataOutputStream()) {
                ((XBTTreeNode) item).toStreamUB(dataOutputStream);
            } catch (IOException ex) {
                Logger.getLogger(DocumentBinaryViewer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        binaryPanel.setContentData(byteArrayData);
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
