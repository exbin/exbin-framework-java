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
package org.exbin.framework.sidebar.api;

import java.awt.Component;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * Interface for sidebar.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface SideBar {

    /**
     * Returns side bar tool bar.
     *
     * @return tool bar
     */
    @Nonnull
    JToolBar getToolBar();

    /**
     * Returns side bar panel.
     *
     * @return side bar panel
     */
    @Nonnull
    JPanel getSideBarPanel();

    /**
     * Switch to specific component.
     *
     * @param component component
     */
    void switchComponent(SideBarComponent component);
}
