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
package org.exbin.jaguif.file.api;

import java.awt.Component;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;

/**
 * Interface for file support module.
 */
@ParametersAreNonnullByDefault
public interface FileModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(FileModuleApi.class);

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
     * Registers file dialogs provider.
     *
     * @param providerId final provider ID
     * @param provider file dialog provider
     */
    void registerFileDialogsProvider(String providerId, FileDialogsProvider provider);

    /**
     * Returns file dialog providers.
     *
     * @return file dialogs providers
     */
    @Nonnull
    Map<String, FileDialogsProvider> getFileDialogsProviders();

    /**
     * Retuns preferred file dialog provider id.
     *
     * @return file dialog provider id
     */
    @Nonnull
    String getFileDialogProviderId();

    /**
     * Sets preferred file dialog provider.
     *
     * @param fileDialogProviderId file dialog provider id
     */
    void setFileDialogProviderId(String fileDialogProviderId);

    /**
     * Retuns preferred file dialog provider.
     *
     * @return file dialog provider
     */
    @Nonnull
    FileDialogsProvider getFileDialogsProvider();

    /**
     * Registers file providers.
     */
    void registerFileProviders();

    /**
     * Adds file usage listener.
     *
     * @param listener file usage listener
     */
    void addFileUsageListener(FileUsageListener listener);

    /**
     * Removes file usage listener.
     *
     * @param listener file usage listener
     */
    void removeFileUsageListener(FileUsageListener listener);

    /**
     * Notifies about file usage event.
     *
     * @param fileUri file Uri
     * @param fileType file type
     */
    void notifyFileUsed(URI fileUri, @Nullable FileType fileType);

    /**
     * Attempts to open given file URI to active panel.
     *
     * @param fileUri file URI
     */
    void openFile(URI fileUri);

    /**
     * Attempts to open given filename to active panel.
     *
     * @param filename filename
     */
    void openFile(String filename);

    /**
     * Registers settings.
     */
    void registerSettings();

    /**
     * Asks whether modified file should be saved.
     *
     * @param parentComponent
     * @return
     */
    @Nonnull
    SaveModifiedResult showSaveModified(Component parentComponent);

    /**
     * Asks whether it's allowed to overwrite file.
     *
     * @param parentComponent parent component
     * @return true if allowed
     */
    boolean showAskToOverwrite(Component parentComponent);

    /**
     * Shows file not found message.
     *
     * @param parentComponent parent component
     * @param filePath file path
     */
    void showFileNotFound(Component parentComponent, String filePath);

    /**
     * Shows unable to save message.
     *
     * @param parentComponent parent component
     * @param ex exception
     */
    void showUnableToSave(Component parentComponent, Exception ex);
}
