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
import org.exbin.framework.deltahex.panel.HexColorType;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.deltahex.panel.HexStatusPanel;
import org.exbin.framework.deltahex.panel.SearchParameters;
import org.exbin.framework.editor.text.dialog.TextFontDialog;
import org.exbin.framework.gui.editor.api.EditorProvider;

/**
 * Hexadecimal editor provider interface.
 *
 * @version 0.2.0 2016/08/14
 * @author ExBin Project (http://exbin.org)
 */
public interface HexEditorProvider extends EditorProvider {

    /**
     * Registers text status method.
     *
     * @param hexStatusPanel hex status panel
     */
    void registerTextStatus(HexStatusPanel hexStatusPanel);

    public Map<HexColorType, Color> getCurrentColors();

    public Map<HexColorType, Color> getDefaultColors();

    public void setCurrentColors(Map<HexColorType, Color> colors);

    public boolean isWordWrapMode();

    public void setWordWrapMode(boolean mode);

    public Charset getCharset();

    public void setCharset(Charset forName);

    public void findText(SearchParameters searchParameters);

    public boolean changeShowNonprintables();

    public void showFontDialog(TextFontDialog dialog);

    public boolean changeLineWrap();

    public HexPanel getDocument();

    public void printFile();
}
