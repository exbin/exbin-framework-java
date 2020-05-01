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
package org.exbin.framework.editor.wave.panel.command;

import java.util.Date;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.exbin.xbup.audio.swing.XBWavePanel;
import org.exbin.xbup.core.type.XBData;
import org.exbin.xbup.operation.AbstractCommand;

/**
 * Wave reverse command.
 *
 * @version 0.2.0 2016/01/24
 * @author ExBin Project (http://exbin.org)
 */
public class WaveReverseCommand extends AbstractCommand {

    private XBWavePanel wave;
    private int startPosition;
    private int endPosition;

    XBData deletedData;

    public WaveReverseCommand(XBWavePanel wave) {
        this(wave, -1, -1);
    }

    public WaveReverseCommand(XBWavePanel wave, int startPosition, int endPosition) {
        this.wave = wave;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    @Override
    public String getCaption() {
        return "Wave section reversed";
    }

    @Override
    public void execute() throws Exception {
        if (startPosition >= 0) {
            wave.getWave().performTransformReverse(startPosition, endPosition);
        } else {
            wave.getWave().performTransformReverse();
        }
        wave.rebuildZoomCache();
    }

    @Override
    public void redo() throws Exception {
        execute();
    }

    @Override
    public void undo() throws Exception {
        execute();
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
