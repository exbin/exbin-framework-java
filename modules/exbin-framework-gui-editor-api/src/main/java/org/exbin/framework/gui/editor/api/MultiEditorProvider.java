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
package org.exbin.framework.gui.editor.api;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.gui.file.api.MultiFileHandlerApi;

/**
 * Framework multitab editor interface.
 *
 * @version 0.2.0 2016/08/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface MultiEditorProvider extends EditorProvider, MultiFileHandlerApi {

    /**
     * Sets currently active editor provider.
     *
     * @param editorProvider editor provider
     */
    void setActiveEditor(EditorProvider editorProvider);
}
