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
package org.exbin.framework.docking.multi.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.docking.api.DocumentDocking;

/**
 * Interface for docking module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface DockingMultiModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(DockingMultiModuleApi.class);

    /**
     * Creates default multiple document docking.
     *
     * @return document docking
     */
    @Nonnull
    DocumentDocking createDefaultDocking();

    /**
     * Registers menu file close actions.
     */
    void registerMenuFileCloseActions();

    /**
     * Creates close file action.
     *
     * @return close file action
     */
    @Nonnull
    AbstractAction createCloseFileAction();

    /**
     * Creates close all files action.
     *
     * @return close all files action
     */
    @Nonnull
    AbstractAction createCloseAllFilesAction();

    /**
     * Creates close other files action.
     *
     * @return close other files action
     */
    @Nonnull
    AbstractAction createCloseOtherFilesAction();
}
