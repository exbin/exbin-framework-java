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
package org.exbin.framework.gui.file.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;

/**
 * Interface for XBUP framework file module.
 *
 * @version 0.2.2 2021/10/05
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface GuiFileModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(GuiFileModuleApi.class);

    @Nonnull
    FileHandlingActionsApi getFileHandlingActions();

    void addFileType(FileType fileType);

    /**
     * Sets file operations handler.
     *
     * @param fileOperations file operations handler
     */
    void setFileOperations(@Nullable FileOperations fileOperations);

    /**
     * Registers file handling operations to main frame menu.
     */
    void registerMenuFileHandlingActions();

    /**
     * Registers file handling operations to main frame tool bar.
     */
    void registerToolBarFileHandlingActions();

    /**
     * Registers close listener.
     */
    void registerCloseListener();

    /**
     * Attempts to load given filename to active panel.
     *
     * @param filename filename
     */
    void loadFromFile(String filename);

    /**
     * Registers list of last opened files into file menu.
     */
    void registerLastOpenedMenuActions();
}
