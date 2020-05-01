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
package org.exbin.framework.gui.file.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;

/**
 * Interface for file handling actions.
 *
 * @version 0.2.0 2017/01/05
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface FileHandlingActionsApi {

    @Nonnull
    Action getNewFileAction();

    @Nonnull
    Action getOpenFileAction();

    @Nonnull
    Action getSaveFileAction();

    @Nonnull
    Action getSaveAsFileAction();

    @Nonnull
    FileHandlerApi getFileHandler();

    void setFileHandler(FileHandlerApi fileHandler);

    boolean releaseFile();
}
