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
package org.exbin.framework.deltahex;

import java.awt.Color;
import java.nio.charset.Charset;
import java.util.Map;
import org.exbin.deltahex.operation.undo.BinaryDataUndoHandler;
import org.exbin.framework.deltahex.panel.HexColorType;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.deltahex.panel.ReplaceParameters;
import org.exbin.framework.deltahex.panel.SearchParameters;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.editor.text.dialog.TextFontDialog;
import org.exbin.framework.gui.editor.api.EditorProvider;

/**
 * Hexadecimal editor provider interface.
 *
 * @version 0.2.0 2016/12/21
 * @author ExBin Project (http://exbin.org)
 */
public interface HexEditorProvider extends EditorProvider {

    /**
     * Registers hex status method.
     *
     * @param hexStatus hex status
     */
    void registerHexStatus(HexStatusApi hexStatus);

    /**
     * Registers encoding status method.
     *
     * @param encodingStatus hex status
     */
    void registerEncodingStatus(TextEncodingStatusApi encodingStatus);

    Map<HexColorType, Color> getCurrentColors();

    Map<HexColorType, Color> getDefaultColors();

    void setCurrentColors(Map<HexColorType, Color> colors);

    boolean isWordWrapMode();

    void setWordWrapMode(boolean mode);

    Charset getCharset();

    void setCharset(Charset forName);

    void performFind(SearchParameters searchParameters);

    void performReplace(SearchParameters searchParameters, ReplaceParameters replaceParameters);

    boolean changeShowNonprintables();

    void showFontDialog(TextFontDialog dialog);

    boolean changeLineWrap();

    HexPanel getDocument();

    void printFile();

    BinaryDataUndoHandler getHexUndoHandler();
}
