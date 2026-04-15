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
package org.exbin.jaguif.action;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.jaguif.action.clipboard.CopyAction;
import org.exbin.jaguif.action.clipboard.CutAction;
import org.exbin.jaguif.action.clipboard.DeleteAction;
import org.exbin.jaguif.action.clipboard.PasteAction;
import org.exbin.jaguif.action.clipboard.SelectAllAction;
import org.exbin.jaguif.action.api.clipboard.ClipboardActionsApi;

/**
 * Clipboard actions.
 */
@ParametersAreNonnullByDefault
public class ClipboardActions implements ClipboardActionsApi {

    protected ResourceBundle resourceBundle;

    public ClipboardActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    @Override
    public Action createCutAction() {
        CutAction cutAction = new CutAction();
        cutAction.init(resourceBundle);
        return cutAction;
    }

    @Nonnull
    @Override
    public Action createCopyAction() {
        CopyAction copyAction = new CopyAction();
        copyAction.init(resourceBundle);
        return copyAction;
    }

    @Nonnull
    @Override
    public Action createPasteAction() {
        PasteAction pasteAction = new PasteAction();
        pasteAction.init(resourceBundle);
        return pasteAction;
    }

    @Nonnull
    @Override
    public Action createDeleteAction() {
        DeleteAction deleteAction = new DeleteAction();
        deleteAction.init(resourceBundle);
        return deleteAction;
    }

    @Nonnull
    @Override
    public Action createSelectAllAction() {
        SelectAllAction selectAllAction = new SelectAllAction();
        selectAllAction.init(resourceBundle);
        return selectAllAction;
    }
}
