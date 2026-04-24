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
package org.exbin.jaguif.file;

import java.io.File;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.document.api.Document;
import org.exbin.jaguif.document.api.DocumentProvider;
import org.exbin.jaguif.file.api.DefaultFileTypes;
import org.exbin.jaguif.file.api.FileDialogsProvider;
import org.exbin.jaguif.file.api.FileDocumentSource;
import org.exbin.jaguif.file.api.FileSourceIdentifier;
import org.exbin.jaguif.file.api.FileModuleApi;
import org.exbin.jaguif.file.api.OpenFileResult;
import org.exbin.jaguif.document.api.SourceIdentifier;
import org.exbin.jaguif.document.api.DocumentSource;
import org.exbin.jaguif.document.api.LoadableDocument;
import org.exbin.jaguif.document.api.MemoryDocumentSource;
import org.exbin.jaguif.file.api.UsedDirectoryApi;
import org.exbin.jaguif.frame.api.FrameModuleApi;

/**
 * File document provider.
 */
@ParametersAreNonnullByDefault
public class FileDocumentProvider implements DocumentProvider {
    
    private final UsedDirectoryApi usedDirectory = new DefaultLastUsedDirectory();

    @Nonnull
    @Override
    public Optional<DocumentSource> createDocumentSource(SourceIdentifier source) {
        if (source instanceof FileSourceIdentifier) {
            return Optional.of(new FileDocumentSource(new File(((FileSourceIdentifier) source).getFileUri())));
        }

        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<DocumentSource> performOpenDefaultDocument() {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);        
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        FileDialogsProvider fileDialogsProvider = fileModule.getFileDialogsProvider();
        OpenFileResult openFileResult = fileDialogsProvider.showOpenFileDialog(frameModule.getFrame(), new DefaultFileTypes(fileModule.getFileTypes()), null, usedDirectory, null);
        if (openFileResult.getResultType() == OpenFileResult.ResultType.APPROVED) {
            return Optional.of(new FileDocumentSource(openFileResult.getSelectedFile().get()));
        }

        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<DocumentSource> performSaveAsDefaultDocument(Document document) {
        File suggestedFile = null;
        if (document instanceof LoadableDocument) {
            Optional<DocumentSource> optDocumentSource = ((LoadableDocument) document).getDocumentSource();
            if (optDocumentSource.isPresent()) {
                DocumentSource documentSource = optDocumentSource.get();
                if (documentSource instanceof MemoryDocumentSource) {
                    suggestedFile = new File(((MemoryDocumentSource) documentSource).getDocumentTitle());
                }
            }
        }
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);        
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        FileDialogsProvider fileDialogsProvider = fileModule.getFileDialogsProvider();
        OpenFileResult openFileResult = fileDialogsProvider.showSaveFileDialog(frameModule.getFrame(), new DefaultFileTypes(fileModule.getFileTypes()), suggestedFile, usedDirectory, null);
        if (openFileResult.getResultType() == OpenFileResult.ResultType.APPROVED) {
            return Optional.of(new FileDocumentSource(openFileResult.getSelectedFile().get()));
        }

        return Optional.empty();
    }
}
