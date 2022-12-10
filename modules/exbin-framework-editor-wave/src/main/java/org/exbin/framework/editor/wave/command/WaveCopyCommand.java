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
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.xbup.audio.swing.XBWavePanel;
import org.exbin.xbup.audio.wave.XBWave;

/**
 * Wave copy command.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WaveCopyCommand {

    private final Clipboard clipboard;
    private final XBWavePanel wave;
    private final int startPosition;
    private final int endPosition;

    public WaveCopyCommand(XBWavePanel wave, int startPosition, int endPosition) {
        this.wave = wave;
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public void execute() {
        XBWave copiedWave = wave.getWave().copy(startPosition, endPosition - startPosition);
        WaveClipboardData clipboardData = new WaveClipboardData(copiedWave);
        clipboard.setContents(clipboardData, clipboardData);
    }
}
