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
package org.exbin.framework.editor.wave.command;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.Date;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.xbup.audio.swing.XBWavePanel;
import org.exbin.xbup.audio.wave.XBWave;
import org.exbin.xbup.operation.AbstractCommand;

/**
 * Wave delete command.
 *
 * @version 0.2.0 2016/01/28
 * @author ExBin Project (http://exbin.org)
 */
public class WavePasteCommand extends AbstractCommand {

    private XBWave pastedWave;
    private final XBWavePanel wave;
    private final int startPosition;
    private int endPosition;

    private BinaryData deletedData;

    public WavePasteCommand(XBWavePanel wave, int startPosition) {
        this.wave = wave;
        this.startPosition = startPosition;
    }

    @Override
    public String getCaption() {
        return "Wave section pasted";
    }

    @Override
    public void execute() throws Exception {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (clipboard.isDataFlavorAvailable(WaveClipboardData.WAVE_FLAVOR)) {
            pastedWave = (XBWave) clipboard.getData(WaveClipboardData.WAVE_FLAVOR);
        }

        endPosition = startPosition + wave.getWave().getLengthInTicks();
        wave.getWave().insertWave(pastedWave, startPosition);
        wave.rebuildZoomCache();
    }

    @Override
    public void redo() throws Exception {
        wave.getWave().insertData(deletedData, startPosition);
        wave.rebuildZoomCache();
        deletedData = null;
    }

    @Override
    public void undo() throws Exception {
        deletedData = wave.getWave().cutData(startPosition, endPosition - startPosition);
        wave.rebuildZoomCache();
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
