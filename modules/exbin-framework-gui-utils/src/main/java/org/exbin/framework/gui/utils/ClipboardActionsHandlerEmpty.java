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
package org.exbin.framework.gui.utils;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Empty implementation for clipboard handler for visual component / context
 * menu.
 *
 * @version 0.2.1 2017/02/21
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClipboardActionsHandlerEmpty implements ClipboardActionsHandler {

    @Override
    public void performCut() {
    }

    @Override
    public void performCopy() {
    }

    @Override
    public void performPaste() {
    }

    @Override
    public void performDelete() {
    }

    @Override
    public void performSelectAll() {
    }

    @Override
    public boolean isSelection() {
        return false;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean canSelectAll() {
        return false;
    }

    @Override
    public boolean canPaste() {
        return false;
    }

    @Override
    public boolean canDelete() {
        return false;
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
    }
}
