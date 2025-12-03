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
package org.exbin.framework.file.api.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.file.api.FileDialogsProvider;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.file.api.FileType;

/**
 * Test implementation of file module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TestFileModule implements FileModuleApi {

    @Override
    public void addFileType(FileType fileType) {
    }

    @Nonnull
    @Override
    public Collection<FileType> getFileTypes() {
        return new ArrayList<>();
    }

    @Override
    public void openFile(URI fileUri) {
    }

    @Override
    public void openFile(String filename) {
    }

    @Override
    public void registerSettings() {
    }

    @Override
    public void registerFileDialogsProvider(String providerId, FileDialogsProvider provider) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nonnull
    @Override
    public String getFileDialogProviderId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setFileDialogProviderId(String fileDialogProviderId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nonnull
    @Override
    public FileDialogsProvider getFileDialogsProvider() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerFileProviders() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
