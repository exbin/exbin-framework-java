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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.xbup.audio.swing.XBWavePanel;
import org.exbin.xbup.audio.wave.XBWave;
import org.exbin.xbup.operation.AbstractCommand;

/**
 * Wave delete command.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
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

    @Nonnull
    @Override
    public String getName() {
        return "Wave section pasted";
    }

    @Override
    public void execute() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (clipboard.isDataFlavorAvailable(WaveClipboardData.WAVE_FLAVOR)) {
            try {
                pastedWave = (XBWave) clipboard.getData(WaveClipboardData.WAVE_FLAVOR);
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(WavePasteCommand.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(WavePasteCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        endPosition = startPosition + wave.getWave().getLengthInTicks();
        wave.getWave().insertWave(pastedWave, startPosition);
        wave.rebuildZoomCache();
    }

    @Override
    public void redo() {
        wave.getWave().insertData(deletedData, startPosition);
        wave.rebuildZoomCache();
        deletedData = null;
    }

    @Override
    public void undo() {
        deletedData = wave.getWave().cutData(startPosition, endPosition - startPosition);
        wave.rebuildZoomCache();
    }

    @Override
    public void dispose() {
    }
}
