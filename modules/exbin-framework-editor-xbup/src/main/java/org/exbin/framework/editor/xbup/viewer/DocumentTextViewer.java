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

import java.awt.Color;
import java.awt.Font;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.framework.editor.text.panel.TextPanel;
import org.exbin.framework.editor.text.service.TextSearchService;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.core.block.XBBlockDataMode;
import org.exbin.xbup.core.block.XBBlockType;
import org.exbin.xbup.core.block.XBFBlockType;
import org.exbin.xbup.core.block.XBTBlock;
import org.exbin.xbup.core.block.declaration.catalog.XBCBlockDecl;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCBlockSpec;
import org.exbin.xbup.core.catalog.base.service.XBCXNameService;
import org.exbin.xbup.core.parser.token.XBAttribute;
import org.exbin.xbup.parser_tree.XBTTreeNode;

/**
 * Text viewer of document.
 *
 * @version 0.2.1 2020/03/09
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DocumentTextViewer implements DocumentViewer {

    private final TextPanel textPanel;
    private XBACatalog catalog;

    public DocumentTextViewer() {
        textPanel = new TextPanel();
        textPanel.setNoBorder();
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        return textPanel;
    }

    @Override
    public void setSelectedItem(XBTBlock item) {
        String text = "<!XBUP version=\"0.1\">\n";
//        XBTBlock parent = item.getParent();
//        if (parent == null) {
//            text += nodeAsText((XBTTreeNode) parent, "").toString();
//        }
        text = nodeAsText((XBTTreeNode) item, "").toString();
        textPanel.setText(text);
    }

    @Override
    public void performCut() {
        textPanel.performCut();
    }

    @Override
    public void performCopy() {
        textPanel.performCopy();
    }

    @Override
    public void performPaste() {
        textPanel.performPaste();
    }

    @Override
    public void performDelete() {
        textPanel.performDelete();
    }

    @Override
    public void performSelectAll() {
        textPanel.performSelectAll();
    }

    @Override
    public boolean isSelection() {
        return textPanel.isSelection();
    }

    @Override
    public boolean isEditable() {
        return textPanel.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return textPanel.canSelectAll();
    }

    @Override
    public boolean canPaste() {
        return textPanel.canPaste();
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        textPanel.setUpdateListener(updateListener);
    }

    public Color[] getDefaultColors() {
        return textPanel.getDefaultColors();
    }

    public void setCurrentColors(Color[] colors) {
        textPanel.setCurrentColors(colors);
    }

    public Font getDefaultFont() {
        return textPanel.getDefaultFont();
    }

    public void setCurrentFont(Font deriveFont) {
        textPanel.setCurrentFont(deriveFont);
    }

    public boolean changeLineWrap() {
        return textPanel.changeLineWrap();
    }

    public int getLineCount() {
        return textPanel.getLineCount();
    }

    public void gotoRelative(int charPos) {
        textPanel.gotoRelative(charPos);
    }

    public void gotoLine(int line) {
        textPanel.gotoLine(line);
    }

    public void findText(TextSearchService.FindTextParameters findTextParameters) {
        textPanel.findText(findTextParameters);
    }

    public void setCharset(Charset charset) {
        textPanel.setCharset(charset);
    }

    public Color[] getCurrentColors() {
        return textPanel.getCurrentColors();
    }

    public void setFileMode(int i) {
        // TODO textPanel.setFileMode(getMode().ordinal());
    }

    public Font getCurrentFont() {
        return textPanel.getCurrentFont();
    }

    private StringBuffer nodeAsText(@Nullable XBTTreeNode node, String prefix) {
        StringBuffer result = new StringBuffer();
        result.append(prefix);
        if (node == null) {
            return result;
        }

        if (node.getDataMode() == XBBlockDataMode.DATA_BLOCK) {
            result.append("[");
            for (long i = 0; i < node.getDataSize(); i++) {
                byte b = node.getBlockData().getByte(i);
                result.append(getHex(b));
            }
            result.append("]\n");
        } else {
            result.append("<").append(getCaption(node));
            if (node.getAttributesCount() > 2) {
                XBAttribute[] attributes = node.getAttributes();
                for (int i = 0; i < attributes.length; i++) {
                    XBAttribute attribute = attributes[i];
                    result.append(" ").append(i + 1).append("=\"").append(attribute.getNaturalLong()).append("\"");
                }
            }

            if (node.getChildren() != null) {
                result.append(">\n");
                XBTBlock[] children = node.getChildren();
                for (XBTBlock child : children) {
                    result.append(nodeAsText((XBTTreeNode) child, prefix + "  "));
                }
                result.append(prefix);
                result.append("</").append(getCaption(node)).append(">\n");
            } else {
                result.append("/>\n");
            }
        }
        return result;
    }

    public String getHex(byte b) {
        byte low = (byte) (b & 0xf);
        byte hi = (byte) (b >> 0x8);
        return (Integer.toHexString(hi) + Integer.toHexString(low)).toUpperCase();
    }

    private String getCaption(XBTTreeNode node) {
        if (node.getDataMode() == XBBlockDataMode.DATA_BLOCK) {
            return "Data Block";
        }
        XBBlockType blockType = node.getBlockType();
        if (catalog != null) {
            XBCXNameService nameService = (XBCXNameService) catalog.getCatalogService(XBCXNameService.class);
            XBCBlockDecl blockDecl = (XBCBlockDecl) node.getBlockDecl();
            if (blockDecl != null) {
                XBCBlockSpec blockSpec = blockDecl.getBlockSpecRev().getParent();
                return nameService.getDefaultText(blockSpec);
            }
        }
        return "Unknown" + " (" + Integer.toString(((XBFBlockType) blockType).getGroupID().getInt()) + ", " + Integer.toString(((XBFBlockType) blockType).getBlockID().getInt()) + ")";
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }

}
