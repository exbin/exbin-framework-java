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
package org.exbin.framework.document.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for document support module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface DocumentModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(DocumentModuleApi.class);
    public static final String SETTINGS_PAGE_ID = "document";
    public static final String FILE_MENU_GROUP_ID = MODULE_ID + ".fileMenuGroup";
    public static final String FILE_TOOL_BAR_GROUP_ID = MODULE_ID + ".fileToolBarGroup";

    /**
     * Returns main document manager.
     *
     * @return document manager
     */
    @Nonnull
    DocumentManagement getMainDocumentManager();

    /**
     * Creates instance of memory document source.
     *
     * @return memory document source
     */
    @Nonnull
    MemoryDocumentSource createMemoryDocumentSource();

    /**
     * Returns new document name prefix.
     * @return name prefix
     */
    @Nonnull
    String getNewDocumentNamePrefix();
    
    /**
     * Registers settings options and panels.
     */
    void registerSettings();
}
