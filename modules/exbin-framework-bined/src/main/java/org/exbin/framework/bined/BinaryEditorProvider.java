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
package org.exbin.framework.bined;

import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.gui.editor.api.EditorProvider;

/**
 * Binary editor provider interface.
 *
 * @version 0.2.0 2020/03/05
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface BinaryEditorProvider extends EditorProvider {

    /**
     * Registers binary status method.
     *
     * @param binaryStatus binarystatus
     */
    void registerBinaryStatus(BinaryStatusApi binaryStatus);

    /**
     * Registers encoding status method.
     *
     * @param encodingStatus encoding status
     */
    void registerEncodingStatus(TextEncodingStatusApi encodingStatus);

    @Nonnull
    ExtendedCodeAreaColorProfile getCurrentColors();

    @Nonnull
    ExtendedCodeAreaColorProfile getDefaultColors();

    void setCurrentColors(ExtendedCodeAreaColorProfile colorsProfile);

    boolean isWordWrapMode();

    void setWordWrapMode(boolean mode);

    @Nonnull
    Charset getCharset();

    int getId();

    void setCharset(Charset charset);

    boolean isShowNonprintables();

    void setShowNonprintables(boolean show);

    boolean isShowValuesPanel();

    void setShowValuesPanel(boolean show);

    boolean changeLineWrap();

    @Nonnull
    BinEdComponentPanel getComponentPanel();

    void printFile();

    @Nonnull
    BinaryDataUndoHandler getBinaryUndoHandler();

    @Nonnull
    ExtCodeArea getCodeArea();

    void setFileHandlingMode(FileHandlingMode fileHandlingMode);
}
