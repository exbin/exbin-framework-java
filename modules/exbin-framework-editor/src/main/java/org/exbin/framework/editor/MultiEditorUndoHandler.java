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
package org.exbin.framework.editor;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.operation.undo.api.UndoFileHandler;
import org.exbin.xbup.operation.Command;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.operation.undo.XBUndoUpdateListener;

/**
 * Undo handler for multi editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MultiEditorUndoHandler implements XBUndoHandler {

    private List<XBUndoUpdateListener> updateListeners = new ArrayList<>();
    @Nullable
    private UndoFileHandler fileHandler;

    public void setActiveFile(@Nullable FileHandler fileHandler) {
        if (this.fileHandler != fileHandler) {
            this.fileHandler = fileHandler instanceof UndoFileHandler ? (UndoFileHandler) fileHandler : null;
            notifyUndoUpdate();
        }
    }

    public void notifyUndoUpdate() {
        for (XBUndoUpdateListener listener : updateListeners) {
            listener.undoCommandPositionChanged();
        }
    }

    public void notifyUndoCommandAdded(Command command) {
        for (XBUndoUpdateListener listener : updateListeners) {
            listener.undoCommandAdded(command);
        }
    }

    @Override
    public boolean canRedo() {
        if (fileHandler != null) {
            return fileHandler.getUndoHandler().canRedo();
        }

        return false;
    }

    @Override
    public boolean canUndo() {
        if (fileHandler != null) {
            return fileHandler.getUndoHandler().canUndo();
        }

        return false;
    }

    @Override
    public void clear() {
        if (fileHandler != null) {
            fileHandler.getUndoHandler().clear();
        }
    }

    @Override
    public void doSync() throws Exception {
        if (fileHandler != null) {
            fileHandler.getUndoHandler().doSync();
        }
    }

    @Override
    public void execute(Command command) throws Exception {
        if (fileHandler != null) {
            fileHandler.getUndoHandler().execute(command);
        }
    }

    @Override
    public void addCommand(Command cmnd) {
        if (fileHandler != null) {
            fileHandler.getUndoHandler().addCommand(cmnd);
        }
    }

    @Nullable
    @Override
    public List<Command> getCommandList() {
        if (fileHandler != null) {
            return fileHandler.getUndoHandler().getCommandList();
        }

        return new ArrayList<>();
    }

    @Override
    public long getCommandPosition() {
        if (fileHandler != null) {
            return fileHandler.getUndoHandler().getCommandPosition();
        }

        return 0;
    }

    @Override
    public long getMaximumUndo() {
        if (fileHandler != null) {
            return fileHandler.getUndoHandler().getMaximumUndo();
        }

        return 0;
    }

    @Override
    public long getSyncPoint() {
        if (fileHandler != null) {
            return fileHandler.getUndoHandler().getSyncPoint();
        }

        return 0;
    }

    @Override
    public long getUndoMaximumSize() {
        if (fileHandler != null) {
            return fileHandler.getUndoHandler().getUndoMaximumSize();
        }

        return 0;
    }

    @Override
    public long getUsedSize() {
        if (fileHandler != null) {
            return fileHandler.getUndoHandler().getUsedSize();
        }

        return 0;
    }

    @Override
    public void performRedo() throws Exception {
        if (fileHandler != null) {
            fileHandler.getUndoHandler().performRedo();
        }
    }

    @Override
    public void performRedo(int count) throws Exception {
        if (fileHandler != null) {
            fileHandler.getUndoHandler().performRedo(count);
        }
    }

    @Override
    public void performUndo() throws Exception {
        if (fileHandler != null) {
            fileHandler.getUndoHandler().performUndo();
        }
    }

    @Override
    public void performUndo(int count) throws Exception {
        if (fileHandler != null) {
            fileHandler.getUndoHandler().performUndo();
        }
    }

    @Override
    public void setCommandPosition(long targetPosition) throws Exception {
        if (fileHandler != null) {
            fileHandler.getUndoHandler().setCommandPosition(targetPosition);
        }
    }

    @Override
    public void setSyncPoint(long syncPoint) {
        if (fileHandler != null) {
            fileHandler.getUndoHandler().setSyncPoint(syncPoint);
        }
    }

    @Override
    public void setSyncPoint() {
        if (fileHandler != null) {
            fileHandler.getUndoHandler().setSyncPoint();
        }
    }

    @Override
    public void addUndoUpdateListener(XBUndoUpdateListener listener) {
        updateListeners.add(listener);
    }

    @Override
    public void removeUndoUpdateListener(XBUndoUpdateListener listener) {
        updateListeners.remove(listener);
    }
}
