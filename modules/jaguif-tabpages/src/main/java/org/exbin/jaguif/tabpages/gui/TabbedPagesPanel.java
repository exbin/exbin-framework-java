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
package org.exbin.jaguif.tabpages.gui;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import org.exbin.jaguif.tabpages.api.TabPages;
import org.exbin.jaguif.tabpages.api.TabPagesComponent;

/**
 * Panel for multiple pages organized by tabs.
 */
@ParametersAreNonnullByDefault
public class TabbedPagesPanel extends JTabbedPane implements TabPages {

    protected Controller controller;
    protected int activeIndex = -1;

    public TabbedPagesPanel() {
        addChangeListener((e) -> {
            int selectedIndex = getSelectedIndex();
            changeActivePageIndex(selectedIndex);
        });
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void addPage(TabPagesComponent page) {
        String tabTitle = (String) page.getValue(TabPagesComponent.KEY_NAME);
        Icon tabIcon = (Icon) page.getValue(TabPagesComponent.KEY_ICON);
        String tabTooltip = (String) page.getValue(TabPagesComponent.KEY_TOOLTIP);
        addTab(tabTitle, tabIcon, page.getComponent(), tabTooltip);
    }

    @Override
    public void changeActivePageIndex(int index) {
        if (activeIndex != index) {
            activeIndex = index;
            notifyActiveIndexChanged();
        }
    }

    @Override
    public int getPagesCount() {
        return getComponentCount();
    }

    private void notifyActiveIndexChanged() {
        if (controller != null) {
            controller.activeIndexChanged(activeIndex);
        }
    }

    public interface Controller {

        void activeIndexChanged(int index);
    }
}
