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
package org.exbin.framework.editor.xbup.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.exbin.xbup.core.block.XBBlockDataMode;
import org.exbin.xbup.core.block.declaration.XBBlockDecl;
import org.exbin.xbup.core.block.declaration.XBDeclBlockType;
import org.exbin.xbup.core.block.declaration.catalog.XBCBlockDecl;
import org.exbin.xbup.core.block.declaration.local.XBLBlockDecl;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCBlockSpec;
import org.exbin.xbup.core.catalog.base.service.XBCXIconService;
import org.exbin.xbup.core.catalog.base.service.XBCXNameService;
import org.exbin.xbup.parser_tree.XBTTreeNode;

/**
 * Tree Cell Renderer for XBUP Document Tree.
 *
 * @version 0.2.1 2020/09/22
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class XBDocTreeCellRenderer extends DefaultTreeCellRenderer {

    private XBACatalog catalog;
    private final Map<Long, String> captionCache;
    private final Map<Long, ImageIcon> iconCache;
    private final ImageIcon dataBlockIcon;

    public XBDocTreeCellRenderer() {
        super();
        captionCache = new HashMap<>();
        iconCache = new HashMap<>();
        dataBlockIcon = new ImageIcon(getClass().getResource("/org/exbin/framework/editor/xbup/resources/icons/data-block-16x16.png"));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
//        setLeafIcon(leafIcon);
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof XBTTreeNode) {
            XBTTreeNode node = ((XBTTreeNode) value);
            ImageIcon icon = null;
            String caption = null;
            if (node.getDataMode() == XBBlockDataMode.DATA_BLOCK) {
                if (node.getDataSize() == 0) {
                    caption = "Empty Block";
                } else {
                    caption = "Data Block";
                }
                icon = dataBlockIcon;
            } else {
                if (catalog != null) {
                    caption = getCaption(node.getBlockDecl());
                }

                if (catalog != null) {
                    icon = getIcon(node.getBlockDecl());
                }
            }

            if (caption != null) {
                setText(caption);
            } else {
                setText("Undefined");
                setForeground(Color.RED);
            }

            if (icon != null) {
                setIcon(icon);
            }
        }
        return this;
    }

    /**
     * @return the catalog
     */
    public XBACatalog getCatalog() {
        return catalog;
    }

    /**
     * @param catalog the catalog to set
     */
    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }

    /**
     * Gets caption for given block type.
     *
     * Use cache if available.
     *
     * @param blockDecl block declaration
     * @return caption
     */
    @Nullable
    public String getCaption(XBBlockDecl blockDecl) {
        if (blockDecl instanceof XBCBlockDecl) {
            XBCBlockSpec blockSpec = (XBCBlockSpec) ((XBCBlockDecl) blockDecl).getBlockSpecRev().getParent();
            if (captionCache.containsKey(blockSpec.getId())) {
                return captionCache.get(blockSpec.getId());
            }

            XBCXNameService nameService = catalog.getCatalogService(XBCXNameService.class);
            String caption = nameService.getDefaultText(blockSpec);
            captionCache.put(blockSpec.getId(), caption);
            return caption;
        } else if (blockDecl instanceof XBLBlockDecl) {
            // TOOD
            /* XBCBlockDecl blockDecl = (XBCBlockDecl) ((XBLBlockDecl) blockDecl).getBlockDecl();
             if (blockDecl != null) {
             XBCBlockSpec blockSpec = blockDecl.getBlockSpecRev().getParent();
             if (captionCache.containsKey(blockSpec.getId())) {
             return captionCache.get(blockSpec.getId());
             }

             XBCXNameService nameService = (XBCXNameService) catalog.getCatalogService(XBCXNameService.class);
             String caption = nameService.getDefaultText(blockSpec);
             captionCache.put(blockSpec.getId(), caption);
             return caption;
             } */
        }

        return null;
    }

    /**
     * Gets icon for given block type.
     *
     * Use cache if available.
     *
     * @param blockDecl block declaration
     * @return icon
     */
    @Nullable
    private ImageIcon getIcon(XBBlockDecl blockDecl) {
        if (blockDecl instanceof XBCBlockDecl) {
            XBCBlockSpec blockSpec = (XBCBlockSpec) ((XBCBlockDecl) blockDecl).getBlockSpecRev().getParent();
            if (iconCache.containsKey(blockSpec.getId())) {
                return iconCache.get(blockSpec.getId());
            }
            XBCXIconService iconService = catalog.getCatalogService(XBCXIconService.class);
            ImageIcon icon = iconService.getDefaultImageIcon(blockSpec);
            if (icon == null) {
                iconCache.put(blockSpec.getId(), icon);
                return null;
            }
            if (icon.getImage() == null) {
                return null;
            }
            icon = new ImageIcon(icon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH));
            iconCache.put(blockSpec.getId(), icon);
            return icon;
        } else if (blockDecl instanceof XBDeclBlockType) {
            // TODO
            /* XBCBlockDecl blockDecl = (XBCBlockDecl) ((XBDBlockType) blockDecl).getBlockDecl();
             if (blockDecl != null) {
             XBCBlockSpec blockSpec = blockDecl.getBlockSpecRev().getParent();
             if (iconCache.containsKey(blockSpec.getId())) {
             return iconCache.get(blockSpec.getId());
             }
             XBCXIconService iconService = (XBCXIconService) catalog.getCatalogService(XBCXIconService.class);
             ImageIcon icon = iconService.getDefaultImageIcon(blockSpec);
             if (icon == null) {
             iconCache.put(blockSpec.getId(), icon);
             return null;
             }
             icon = new ImageIcon(icon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH));
             iconCache.put(blockSpec.getId(), icon);
             return icon;
             } */
        }

        return null;
    }
}
