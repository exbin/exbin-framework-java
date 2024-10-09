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
package org.exbin.framework.operation.manager.gui;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractListModel;
import org.exbin.framework.operation.api.Command;
import org.exbin.framework.operation.undo.api.UndoRedo;

/**
 * List model for undo manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UndoManagerModel extends AbstractListModel<String> {

    private UndoRedo undoRedo = null;

    public UndoManagerModel() {
    }

    @Nullable
    public UndoRedo getUndoRedo() {
        return undoRedo;
    }

    public void setUndoRedo(UndoRedo undoRedo) {
        if (this.undoRedo != null) {
            fireIntervalRemoved(this, 0, getList().size());
        }

        this.undoRedo = undoRedo;
        fireIntervalAdded(this, 0, getList().size());
    }

    @Override
    public int getSize() {
        return undoRedo == null ? 0 : getList().size() + 1;
    }

    @Nullable
    @Override
    public String getElementAt(int index) {
        return undoRedo == null ? null : (index == 0 ? "Initial" : getList().get(index - 1).getType().toString())
                + (undoRedo.getCommandPosition() == index ? " (current)" : "")
                + (undoRedo.getSyncPosition() == index ? " (saved)" : "");
    }

    @Nullable
    public Command getItem(int index) {
        return undoRedo == null || index == 0 ? null : getList().get(index - 1);
    }

    public int getCurrentPosition() {
        return (int) undoRedo.getCommandPosition();
    }

    @Nonnull
    private List<Command> getList() {
        return Objects.requireNonNull(undoRedo.getCommandList());
    }
}
