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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.xbup.audio.swing.XBWavePanel;
import org.exbin.xbup.operation.AbstractCommand;

/**
 * Wave delete command.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WaveDeleteCommand extends AbstractCommand {

    private final XBWavePanel wave;
    private final int startPosition;
    private final int endPosition;

    private BinaryData deletedData;

    public WaveDeleteCommand(XBWavePanel wave, int startPosition, int endPosition) {
        this.wave = wave;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    @Nonnull
    @Override
    public String getName() {
        return "Wave section deleted";
    }

    @Override
    public void execute() {
        deletedData = wave.getWave().cutData(startPosition, endPosition - startPosition);
        wave.rebuildZoomCache();
    }

    @Override
    public void redo() {
        execute();
    }

    @Override
    public void undo() {
        wave.getWave().insertData(deletedData, startPosition);
        wave.rebuildZoomCache();
        deletedData = null;
    }

    @Override
    public void dispose() {
    }
}
