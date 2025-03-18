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
package org.exbin.framework.menu;

import org.exbin.framework.menu.action.DeleteAction;
import org.exbin.framework.menu.action.SelectAllAction;
import org.exbin.framework.menu.action.CutAction;
import org.exbin.framework.menu.action.PasteAction;
import org.exbin.framework.menu.action.CopyAction;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.utils.ClipboardActionsApi;

/**
 * Clipboard actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClipboardActions implements ClipboardActionsApi {

    private ResourceBundle resourceBundle;

    public ClipboardActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    @Override
    public Action createCutAction() {
        CutAction cutAction = new CutAction();
        cutAction.setup(resourceBundle);
        return cutAction;
    }

    @Nonnull
    @Override
    public Action createCopyAction() {
        CopyAction copyAction = new CopyAction();
        copyAction.setup(resourceBundle);
        return copyAction;
    }

    @Nonnull
    @Override
    public Action createPasteAction() {
        PasteAction pasteAction = new PasteAction();
        pasteAction.setup(resourceBundle);
        return pasteAction;
    }

    @Nonnull
    @Override
    public Action createDeleteAction() {
        DeleteAction deleteAction = new DeleteAction();
        deleteAction.setup(resourceBundle);
        return deleteAction;
    }

    @Nonnull
    @Override
    public Action createSelectAllAction() {
        SelectAllAction selectAllAction = new SelectAllAction();
        selectAllAction.setup(resourceBundle);
        return selectAllAction;
    }
}
