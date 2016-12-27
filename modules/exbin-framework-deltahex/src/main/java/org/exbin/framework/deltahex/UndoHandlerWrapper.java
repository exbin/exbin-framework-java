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
package org.exbin.framework.deltahex;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.exbin.deltahex.operation.BinaryDataCommand;
import org.exbin.deltahex.operation.BinaryDataOperationException;
import org.exbin.deltahex.operation.undo.BinaryDataUndoHandler;
import org.exbin.deltahex.operation.undo.BinaryDataUndoUpdateListener;
import org.exbin.xbup.operation.Command;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.operation.undo.XBUndoUpdateListener;

/**
 * Undo handler wrapper.
 *
 * @version 0.2.0 2016/12/20
 * @author ExBin Project (http://exbin.org)
 */
public class UndoHandlerWrapper implements XBUndoHandler {

    private final BinaryDataUndoHandler handler;
    private final Map<XBUndoUpdateListener, BinaryDataUndoUpdateListener> listenersMap = new HashMap<>();

    public UndoHandlerWrapper(BinaryDataUndoHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean canRedo() {
        return handler.canRedo();
    }

    @Override
    public boolean canUndo() {
        return handler.canUndo();
    }

    @Override
    public void clear() {
        handler.clear();
    }

    @Override
    public void doSync() throws Exception {
        handler.doSync();
    }

    @Override
    public void execute(Command cmnd) throws Exception {
        handler.execute(new BinaryCommandWrapper(cmnd));
    }

    @Override
    public void addCommand(Command cmnd) {
        handler.addCommand(new BinaryCommandWrapper(cmnd));
    }

    @Override
    public List<Command> getCommandList() {
        List<Command> result = new ArrayList<>();
        for (BinaryDataCommand command : handler.getCommandList()) {
            result.add(new CommandWrapper(command));
        }

        return result;
    }

    @Override
    public long getCommandPosition() {
        return handler.getCommandPosition();
    }

    @Override
    public long getMaximumUndo() {
        return handler.getMaximumUndo();
    }

    @Override
    public long getSyncPoint() {
        return handler.getSyncPoint();
    }

    @Override
    public long getUndoMaximumSize() {
        return handler.getUndoMaximumSize();
    }

    @Override
    public long getUsedSize() {
        return handler.getUsedSize();
    }

    @Override
    public void performRedo() throws Exception {
        handler.performRedo();
    }

    @Override
    public void performRedo(int i) throws Exception {
        handler.performRedo(i);
    }

    @Override
    public void performUndo() throws Exception {
        handler.performUndo();
    }

    @Override
    public void performUndo(int i) throws Exception {
        handler.performUndo(i);
    }

    @Override
    public void setCommandPosition(long l) throws Exception {
        handler.setCommandPosition(l);
    }

    @Override
    public void setSyncPoint(long l) {
        handler.setSyncPoint(l);
    }

    @Override
    public void setSyncPoint() {
        handler.setSyncPoint();
    }

    @Override
    public void addUndoUpdateListener(final XBUndoUpdateListener xl) {
        BinaryDataUndoUpdateListener binaryListener = new BinaryDataUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                xl.undoCommandPositionChanged();
            }

            @Override
            public void undoCommandAdded(BinaryDataCommand bdc) {
                xl.undoCommandAdded(new CommandWrapper(bdc));
            }
        };
        listenersMap.put(xl, binaryListener);
        handler.addUndoUpdateListener(binaryListener);
    }

    @Override
    public void removeUndoUpdateListener(XBUndoUpdateListener xl) {
        BinaryDataUndoUpdateListener binaryListener = listenersMap.remove(xl);
        handler.removeUndoUpdateListener(binaryListener);
    }

    private static class CommandWrapper implements Command {

        private final BinaryDataCommand command;

        public CommandWrapper(BinaryDataCommand command) {
            this.command = command;
        }

        @Override
        public String getCaption() {
            return command.getCaption();
        }

        @Override
        public void execute() throws Exception {
            command.execute();
        }

        @Override
        public void use() {
            command.use();
        }

        @Override
        public void redo() throws Exception {
            command.redo();
        }

        @Override
        public void undo() throws Exception {
            command.undo();
        }

        @Override
        public boolean canUndo() {
            return command.canUndo();
        }

        @Override
        public void dispose() throws Exception {
            command.dispose();
        }

        @Override
        public Date getExecutionTime() {
            return command.getExecutionTime();
        }
    }

    private static class BinaryCommandWrapper implements BinaryDataCommand {

        private final Command command;

        public BinaryCommandWrapper(Command command) {
            this.command = command;
        }

        @Override
        public String getCaption() {
            return command.getCaption();
        }

        @Override
        public void execute() throws BinaryDataOperationException {
            try {
                command.execute();
            } catch (Exception ex) {
                throw new BinaryDataOperationException(ex);
            }
        }

        @Override
        public void use() {
            command.use();
        }

        @Override
        public void redo() throws BinaryDataOperationException {
            try {
                command.redo();
            } catch (Exception ex) {
                throw new BinaryDataOperationException(ex);
            }
        }

        @Override
        public void undo() throws BinaryDataOperationException {
            try {
                command.undo();
            } catch (Exception ex) {
                throw new BinaryDataOperationException(ex);
            }
        }

        @Override
        public boolean canUndo() {
            return command.canUndo();
        }

        @Override
        public void dispose() throws BinaryDataOperationException {
            try {
                command.dispose();
            } catch (Exception ex) {
                throw new BinaryDataOperationException(ex);
            }
        }

        @Override
        public Date getExecutionTime() {
            return command.getExecutionTime();
        }
    }
}
