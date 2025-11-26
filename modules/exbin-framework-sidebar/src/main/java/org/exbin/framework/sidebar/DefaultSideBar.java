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
package org.exbin.framework.sidebar;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.exbin.framework.docking.api.SidePanelDocking;
import org.exbin.framework.sidebar.api.SideBar;
import org.exbin.framework.sidebar.api.SideBarComponent;

/**
 * Default docking sidebar.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultSideBar implements SideBar {

    protected final SidePanelDocking docking;
    protected final JToolBar toolBar;
    protected final JPanel sideBarPanel;
    protected String activeComponentId = null;
    protected Component activeComponent = null;

    public DefaultSideBar(SidePanelDocking docking) {
        this.docking = docking;
        sideBarPanel = new JPanel(new BorderLayout());
        toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(false);
        toolBar.setFocusable(false);
    }

    @Nonnull
    @Override
    public JToolBar getToolBar() {
        return toolBar;
    }

    @Override
    public void switchComponent(SideBarComponent component) {
        String componentId = (String) component.getValue(SideBarComponent.KEY_ID);
        if (componentId == null) {
            componentId = "";
        }
        if (componentId.equals(activeComponentId)) {
            docking.setSidePanelVisible(false);
            activeComponentId = null;
            setActiveComponent(null);
        } else {
            if (!docking.isSidePanelVisible()) {
                docking.setSidePanelVisible(true);
            }
            activeComponentId = componentId;
            setActiveComponent(component.createComponent());
        }
    }

    @Nonnull
    @Override
    public JPanel getSideBarPanel() {
        return sideBarPanel;
    }

    public void setActiveComponent(@Nullable Component component) {
        if (component == activeComponent) {
            return;
        }

        if (activeComponent != null) {
            sideBarPanel.remove(activeComponent);
        }
        if (component != null) {
            sideBarPanel.add(component, BorderLayout.CENTER);
        }
        sideBarPanel.revalidate();
        sideBarPanel.repaint();
        activeComponent = component;
    }
}
