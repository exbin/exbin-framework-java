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
package org.exbin.framework.gui.undo.gui;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractListModel;
import org.exbin.xbup.operation.Command;
import org.exbin.xbup.operation.undo.XBUndoHandler;

/**
 * List model for undo manager.
 *
 * @version 0.2.1 2019/07/15
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UndoManagerModel extends AbstractListModel<String> {

    private XBUndoHandler undoHandler = null;

    public UndoManagerModel() {
    }

    public XBUndoHandler getUndoHandler() {
        return undoHandler;
    }

    public void setUndoHandler(XBUndoHandler undoHandler) {
        if (this.undoHandler != null) {
            fireIntervalRemoved(this, 0, getList().size());
        }

        this.undoHandler = undoHandler;
        fireIntervalAdded(this, 0, getList().size());
    }

    @Override
    public int getSize() {
        return undoHandler == null ? 0 : getList().size() + 1;
    }

    @Override
    public String getElementAt(int index) {
        return undoHandler == null ? null : (index == 0 ? "Initial" : getList().get(index - 1).getCaption())
                + (undoHandler.getCommandPosition() == index ? " (current)" : "")
                + (undoHandler.getSyncPoint() == index ? " (saved)" : "");
    }

    public Command getItem(int index) {
        return undoHandler == null || index == 0 ? null : getList().get(index - 1);
    }

    public int getCurrentPosition() {
        return (int) undoHandler.getCommandPosition();
    }

    @Nonnull
    private List<Command> getList() {
        return Objects.requireNonNull(undoHandler.getCommandList());
    }
}
