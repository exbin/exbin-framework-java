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
package org.exbin.framework.file;

import java.io.File;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JFileChooser;
import org.exbin.framework.App;
import org.exbin.framework.document.api.DocumentProvider;
import org.exbin.framework.file.api.DefaultFileTypes;
import org.exbin.framework.file.api.FileDialogsProvider;
import org.exbin.framework.file.api.FileDocumentSource;
import org.exbin.framework.file.api.FileSourceIdentifier;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.file.api.OpenFileResult;
import org.exbin.framework.document.api.SourceIdentifier;
import org.exbin.framework.document.api.DocumentSource;

/**
 * File document provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FileDocumentProvider implements DocumentProvider {

    @Nonnull
    @Override
    public Optional<DocumentSource> openDefaultDocument() {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        FileDialogsProvider fileDialogsProvider = fileModule.getFileDialogsProvider();
        OpenFileResult openFileResult = fileDialogsProvider.showOpenFileDialog(new DefaultFileTypes(fileModule.getFileTypes()), null, null);
        if (openFileResult.getDialogResult() == JFileChooser.APPROVE_OPTION) {
            return Optional.of(new FileDocumentSource(openFileResult.getSelectedFile().get()));
        }

        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<DocumentSource> openDocument(SourceIdentifier source) {
        if (source instanceof FileSourceIdentifier) {
            return Optional.of(new FileDocumentSource(new File(((FileSourceIdentifier) source).getFileUri())));
        }

        return Optional.empty();
    }
}
