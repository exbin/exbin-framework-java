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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.exbin.xbup.audio.wave.XBWave;

/**
 * Wave clipboard data.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class WaveClipboardData implements Transferable, ClipboardOwner {

    public static final String MIME_XBUP_WAVE = "others/x-xbup-wave";
    public static final DataFlavor WAVE_FLAVOR = new DataFlavor(MIME_XBUP_WAVE, "Wave Data");

    private XBWave wave;

    public WaveClipboardData(XBWave wave) {
        this.wave = wave;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] result = new DataFlavor[1];
        result[0] = WAVE_FLAVOR;
        return result;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(WAVE_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(WAVE_FLAVOR)) {
            return wave;
        }
        return null;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // do nothing
    }
}
