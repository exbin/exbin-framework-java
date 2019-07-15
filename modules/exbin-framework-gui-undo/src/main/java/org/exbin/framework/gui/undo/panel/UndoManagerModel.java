/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.gui.undo.panel;

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
