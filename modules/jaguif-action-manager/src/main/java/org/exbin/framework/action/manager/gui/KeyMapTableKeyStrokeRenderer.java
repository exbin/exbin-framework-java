/*
 * Copyright (C) ExBin Project
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
package org.exbin.framework.action.manager.gui;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Keymap keytroke table renderer.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class KeyMapTableKeyStrokeRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String keyStrokeText;
        KeyStroke keyStroke = ((KeyStroke) value);
        if (keyStroke != null) {
            StringBuilder builder = new StringBuilder();
            int modifiers = keyStroke.getModifiers();
            // Or use KeyEvent.getModifiersExText() ?
            if ((modifiers & InputEvent.CTRL_DOWN_MASK) > 0) {
                builder.append("Ctrl + ");
            }
            if ((modifiers & InputEvent.ALT_DOWN_MASK) > 0) {
                builder.append("Alt + ");
            }
            if ((modifiers & InputEvent.META_DOWN_MASK) > 0) {
                builder.append("Meta + ");
            }
            if ((modifiers & InputEvent.SHIFT_DOWN_MASK) > 0) {
                builder.append("Shift + ");
            }
            if (keyStroke.getKeyCode() > 0) {
                builder.append(KeyEvent.getKeyText(keyStroke.getKeyCode()).toUpperCase());
            } else {
                builder.append(Character.toUpperCase(keyStroke.getKeyChar()));
            }

            keyStrokeText = builder.toString();
        } else {
            keyStrokeText = "";
        }

        return super.getTableCellRendererComponent(table, keyStrokeText, isSelected, hasFocus, row, column);
    }
}
