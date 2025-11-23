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
package org.exbin.framework.docking.api;

import java.awt.Component;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.context.api.ContextComponentProvider;

/**
 * Interface for side panel docking.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface SidePanelDocking extends ContextComponentProvider {

    /**
     * Sets side tool bar.
     *
     * @param sideToolBar side tool bar
     */
    void setSideToolBar(@Nullable Component sideToolBar);

    /**
     * Sets side component.
     *
     * @param sideComponent side component
     */
    void setSideComponent(@Nullable Component sideComponent);

    /**
     * Sets visibility of the side panel.
     *
     * @param visible true for visible
     */
    void setSidePanelVisible(boolean visible);
}
