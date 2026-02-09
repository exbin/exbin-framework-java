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
package org.exbin.framework.addon.manager.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JToggleButton;

/**
 * Addons manager cart button.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CartButton extends JToggleButton {

    protected int changesCount = 0;
    protected final Color changesCountFg = new Color(255, 255, 201);
    protected final Color noChangesFg = Color.BLACK;
    protected final Color changesCountBg = new Color(16, 163, 16);
    protected final Color noChangesBg = Color.LIGHT_GRAY;

    public CartButton() {
        super();
    }

    @Override
    public void setText(String text) {
        super.setText(text + "      ");
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        if (ui != null) {
            Graphics g = (graphics == null) ? null : graphics.create();
            try {
                ui.update(g, this);
                int x = getWidth() - 34;
                int y = getHeight() / 2 - 10;
                g.setColor(changesCount == 0 ? noChangesBg : changesCountBg);
                g.fillOval(x, y, 24, 20);
                g.setColor(changesCount == 0 ? noChangesFg : changesCountFg);
                Font font = getFont().deriveFont(Font.BOLD);
                g.setFont(font);
                FontMetrics fontMetrics = g.getFontMetrics(font);
                String text = changesCount > 99 ? "+" : String.valueOf(changesCount);
                char[] changesCharArray = text.toCharArray();
                int textWidth = fontMetrics.charsWidth(changesCharArray, 0, changesCharArray.length);
                g.drawString(text, x + 13 - textWidth / 2, y + 15);
            } finally {
                g.dispose();
            }
        }
    }

    public void setChangesCount(int changesCount) {
        this.changesCount = changesCount;
        repaint();
    }
}
