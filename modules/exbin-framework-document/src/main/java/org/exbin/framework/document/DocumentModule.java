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
package org.exbin.framework.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.document.api.Document;
import org.exbin.framework.document.api.DocumentManagement;
import org.exbin.framework.document.api.DocumentModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.document.api.DocumentProvider;
import org.exbin.framework.document.api.MemoryDocumentSource;

/**
 * Implementation of the document module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DocumentModule implements DocumentModuleApi {

    private ResourceBundle resourceBundle;

    private DocumentManager mainDocumentManager;

    public DocumentModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(DocumentModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    @Override
    public DocumentManagement getMainDocumentManager() {
        if (mainDocumentManager == null) {
            mainDocumentManager = new DocumentManager();
        }

        return mainDocumentManager;
    }
    
    @Nonnull
    @Override
    public MemoryDocumentSource createMemoryDocumentSource() {
        String title = getNewFileTitlePrefix() + " " + 1;
        DefaultMemoryDocumentSource memoryDocumentSource = new DefaultMemoryDocumentSource();
        memoryDocumentSource.setDocumentTitle(title);
        return memoryDocumentSource;
    }

    @Nonnull
    public String getNewFileTitlePrefix() {
        return getResourceBundle().getString("newFileTitlePrefix");
    }
}
