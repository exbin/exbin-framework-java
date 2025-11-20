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
package org.exbin.framework.docking;

import java.awt.Component;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.docking.api.DockingModuleApi;
import org.exbin.framework.docking.api.EditorViewHandling;

/**
 * Interface for docking module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DockingModule implements DockingModuleApi {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(DockingModule.class);

    @Override
    public Component getDockingPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addDockingView(Component component) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EditorViewHandling getEditorViewHandling() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
