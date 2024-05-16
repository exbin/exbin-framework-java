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
package org.exbin.framework.operation.undo.api;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.operation.api.Command;

/**
 * Empty implementation of undo handling.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EmptyUndoRedo implements UndoRedo {

    @Override
    public boolean canRedo() {
        return false;
    }

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public void execute(Command command) {
        command.execute();
    }

    @Nonnull
    @Override
    public List<Command> getCommandList() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Command> getTopUndoCommand() {
        return Optional.empty();
    }

    @Override
    public long getCommandPosition() {
        return 0;
    }

    @Override
    public long getCommandsCount() {
        return 0;
    }

    @Override
    public long getSyncPosition() {
        return 0;
    }

    @Override
    public void performUndo() {
        throw new IllegalStateException();
    }

    @Override
    public void performUndo(int count) {
        throw new IllegalStateException();
    }

    @Override
    public void performRedo() {
        throw new IllegalStateException();
    }

    @Override
    public void performRedo(int count) {
        throw new IllegalStateException();
    }

    @Override
    public void performSync() {
    }

    @Override
    public void setSyncPosition(long syncPoint) {
        throw new IllegalStateException();
    }

    @Override
    public void setSyncPosition() {
    }

    @Override
    public void addChangeListener(UndoRedoChangeListener listener) {
    }

    @Override
    public void removeChangeListener(UndoRedoChangeListener listener) {
    }
}
