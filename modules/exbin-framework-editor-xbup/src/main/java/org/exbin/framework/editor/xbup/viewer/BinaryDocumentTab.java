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

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.gui.BinaryStatusPanel;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.parser_tree.XBTTreeNode;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * Binary viewer of document.
 *
 * @version 0.2.1 2020/09/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryDocumentTab implements DocumentTab {

    private final BinEdComponentPanel binaryPanel;
    private final BinaryStatusPanel binaryStatusPanel;

    public BinaryDocumentTab() {
        binaryPanel = new BinEdComponentPanel();
        binaryStatusPanel = new BinaryStatusPanel();
//        binaryPanel.registerBinaryStatus(binaryStatusPanel);
        // binaryPanel.setNoBorder();
        init();
    }

    private void init() {
    }
    
    @Override
    public void setApplication(XBApplication application) {
        BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);
        CodeAreaPopupMenuHandler codeAreaPopupMenuHandler = binedModule.createCodeAreaPopupMenuHandler(BinedModule.PopupMenuVariant.BASIC);
        JPopupMenu popupMenu = new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                int clickedX = x;
                int clickedY = y;
                if (invoker instanceof JViewport) {
                    clickedX += ((JViewport) invoker).getParent().getX();
                    clickedY += ((JViewport) invoker).getParent().getY();
                }
                JPopupMenu popupMenu = binedModule.createBinEdComponentPopupMenu(codeAreaPopupMenuHandler, binaryPanel, clickedX, clickedY);
                popupMenu.addPopupMenuListener(new PopupMenuListener() {
                    @Override
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    }

                    @Override
                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        codeAreaPopupMenuHandler.dropPopupMenu(BinedModule.BINARY_POPUP_MENU_ID);
                    }

                    @Override
                    public void popupMenuCanceled(PopupMenuEvent e) {
                    }
                });
                popupMenu.show(invoker, x, y);
                binedModule.dropBinEdComponentPopupMenu();
            }
        };
        binaryPanel.setPopupMenu(popupMenu);
    }

    @Override
    public void setCatalog(XBACatalog catalog) {
    }

    @Override
    public void setPluginRepository(XBPluginRepository pluginRepository) {
    }

    @Override
    public void setSelectedItem(@Nullable XBTBlock item) {
        ByteArrayEditableData byteArrayData = null;
        if (item != null) {
            byteArrayData = new ByteArrayEditableData();
            try (OutputStream dataOutputStream = byteArrayData.getDataOutputStream()) {
                ((XBTTreeNode) item).toStreamUB(dataOutputStream);
            } catch (IOException ex) {
                Logger.getLogger(BinaryDocumentTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        binaryPanel.setContentData(byteArrayData);
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        return binaryPanel;
    }

    @Override
    public void performCut() {
        binaryPanel.getCodeArea().cut();
    }

    @Override
    public void performCopy() {
        binaryPanel.getCodeArea().copy();
    }

    @Override
    public void performPaste() {
        binaryPanel.getCodeArea().paste();
    }

    @Override
    public void performDelete() {
        binaryPanel.getCodeArea().delete();
    }

    @Override
    public void performSelectAll() {
        binaryPanel.getCodeArea().selectAll();
    }

    @Override
    public boolean isSelection() {
        return binaryPanel.getCodeArea().hasSelection();
    }

    @Override
    public boolean isEditable() {
        return binaryPanel.getCodeArea().isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return true;
    }

    @Override
    public boolean canPaste() {
        return binaryPanel.getCodeArea().canPaste();
    }

    @Override
    public boolean canDelete() {
        return isEditable();
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        // binaryPanel.setUpdateListener(updateListener);
    }

    @Override
    public void setActivationListener(final ActivationListener listener) {
        binaryPanel.addBinaryAreaFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                listener.activated();
            }
        });
    }
}
