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
package org.exbin.framework.editor.xbup.viewer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.operation.undo.api.UndoFileHandler;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * XBUP editor provider.
 *
 * @version 0.2.5 2021/12/05
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface XbupEditorProvider extends EditorProvider, UndoFileHandler {

    @Nonnull
    XBACatalog getCatalog();

    void setCatalog(XBACatalog catalog);

    @Nonnull
    XBApplication getApplication();

    @Nonnull
    XBPluginRepository getPluginRepository();

    void setPluginRepository(XBPluginRepository pluginRepository);

    void addItemSelectionListener(DocumentItemSelectionListener listener);

    void removeItemSelectionListener(DocumentItemSelectionListener listener);
}
