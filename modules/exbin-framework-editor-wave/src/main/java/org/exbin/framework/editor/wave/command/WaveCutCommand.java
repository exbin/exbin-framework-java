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
package org.exbin.framework.editor.wave.command;

import java.util.Date;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.xbup.audio.swing.XBWavePanel;
import org.exbin.xbup.operation.AbstractCommand;

/**
 * Wave delete command.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WaveCutCommand extends AbstractCommand {

    private WaveCopyCommand copyCommand;
    private WaveDeleteCommand deleteCommand;

    public WaveCutCommand(XBWavePanel wave, int startPosition, int endPosition) {
        copyCommand = new WaveCopyCommand(wave, startPosition, endPosition);
        deleteCommand = new WaveDeleteCommand(wave, startPosition, endPosition);
    }

    @Nonnull
    @Override
    public String getCaption() {
        return "Wave section cut out";
    }

    @Override
    public void execute() throws Exception {
        copyCommand.execute();
        deleteCommand.execute();
    }

    @Override
    public void redo() throws Exception {
        deleteCommand.redo();
    }

    @Override
    public void undo() throws Exception {
        deleteCommand.undo();
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public void dispose() throws Exception {
    }

    @Nonnull
    @Override
    public Optional<Date> getExecutionTime() {
        return Optional.empty();
    }
}
