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
package org.exbin.framework.docking.api;

import java.awt.Component;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;

/**
 * Interface for XBUP framework docking module.
 *
 * @version 0.2.0 2016/08/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface DockingModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(DockingModuleApi.class);

    /**
     * Returns docking viewport panel.
     *
     * @return panel component
     */
    @Nonnull
    Component getDockingPanel();

    /**
     * Adds new view for given component.
     *
     * @param component component
     */
    void addDockingView(Component component);

    /**
     * Returns editor view handling.
     *
     * @return editor view handling
     */
    @Nonnull
    EditorViewHandling getEditorViewHandling();
}
