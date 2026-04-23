/*
 * Copyright (C) ExBin Project, https://exbin.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.jaguif.action.manager.gui;

import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.exbin.jaguif.action.manager.model.KeyMapRecord;

/**
 * Keymap table renderer.
 */
public class KeyMapTableTitleRenderer extends DefaultTableCellRenderer {

    private final ImageIcon emptyIcon;

    public KeyMapTableTitleRenderer() {
        emptyIcon = new ImageIcon(new BufferedImage(16, 16, BufferedImage.TRANSLUCENT));
    }

    @Override
    public Component getTableCellRendererComponent(@Nullable JTable table, @Nullable Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component;
        if (value instanceof KeyMapRecord) {
            component = super.getTableCellRendererComponent(table, ((KeyMapRecord) value).getName(), isSelected, hasFocus, row, column);
            ImageIcon icon = ((KeyMapRecord) value).getIcon();
            setIcon(icon == null ? emptyIcon : icon);
        } else {
            component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        return component;
    }
}
