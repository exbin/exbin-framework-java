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
package org.exbin.framework.undo.api;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Empty implementation of undo handling.
 *
 * @version 0.2.1 2017/02/21
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UndoActionsHandlerEmpty implements UndoActionsHandler {

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public boolean canRedo() {
        return false;
    }

    @Override
    public void performUndo() {
    }

    @Override
    public void performRedo() {
    }

    @Override
    public void performUndoManager() {
    }

    @Override
    public void setUndoUpdateListener(UndoUpdateListener undoUpdateListener) {
    }
}