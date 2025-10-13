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
package org.exbin.framework.file.api;

import java.net.URI;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for file support module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface FileModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(FileModuleApi.class);

    public static final String FILE_MENU_GROUP_ID = MODULE_ID + ".fileMenuGroup";
    public static final String FILE_TOOL_BAR_GROUP_ID = MODULE_ID + ".fileToolBarGroup";

    /**
     * Adds file type.
     *
     * @param fileType file type
     */
    void addFileType(FileType fileType);

    /**
     * Returns file types.
     *
     * @return file types
     */
    @Nonnull
    Collection<FileType> getFileTypes();

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
     * @param fileUri file URI
     */
    void loadFromFile(URI fileUri);

    /**
     * Attempts to load given filename to active panel.
     *
     * @param filename filename
     */
    void loadFromFile(String filename);

    /**
     * Registers list of last opened files into file menu.
     */
    void registerRecenFilesMenuActions();

    @Nonnull
    AbstractAction createNewFileAction();

    @Nonnull
    AbstractAction createOpenFileAction();

    @Nonnull
    AbstractAction createSaveFileAction();

    @Nonnull
    AbstractAction createSaveAsFileAction();

    @Nonnull
    FileActionsApi getFileActions();

    void updateRecentFilesList(URI fileUri, @Nullable FileType fileType);

    void registerSettings();
}
