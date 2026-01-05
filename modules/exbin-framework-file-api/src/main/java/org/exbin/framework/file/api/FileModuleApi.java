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

import java.awt.Component;
import java.net.URI;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
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
     * Registers file providers.
     */
    void registerFileProviders();

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
    boolean showSaveModified(Component parentComponent);

    /**
     * Asks whether it's allowed to overwrite file.
     *
     * @param parentComponent parent component
     * @return true if allowed
     */
    boolean showAskToOverwrite(Component parentComponent);

    /**
     * Shows unable to save message.
     *
     * @param parentComponent parent component
     * @param ex exception
     */
    void showUnableToSave(Component parentComponent, Exception ex);

}
