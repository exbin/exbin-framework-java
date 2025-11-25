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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for docking module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface DockingModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(DockingModuleApi.class);

    /**
     * Registers file handling operations to main frame menu.
     */
    void registerMenuFileHandlingActions();

    /**
     * Registers file handling operations to main frame tool bar.
     */
    void registerToolBarFileHandlingActions();

    /**
     * Creates default document docking.
     *
     * @return document docking
     */
    @Nonnull
    DocumentDocking createDefaultDocking();

    /**
     * Creates new file action.
     *
     * @return action
     */
    @Nonnull
    AbstractAction createNewFileAction();

    /**
     * Creates open file action.
     *
     * @return action
     */
    @Nonnull
    AbstractAction createOpenFileAction();

    /**
     * Creates save file action.
     *
     * @return action
     */
    @Nonnull
    AbstractAction createSaveFileAction();

    /**
     * Creates save as file action.
     *
     * @return action
     */
    @Nonnull
    AbstractAction createSaveAsFileAction();
}
