/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.bined;

import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.framework.bined.panel.BinaryPanel;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.gui.editor.api.EditorProvider;

/**
 * Hexadecimal editor provider interface.
 *
 * @version 0.2.0 2019/06/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface BinaryEditorProvider extends EditorProvider {

    /**
     * Registers hex status method.
     *
     * @param binaryStatus hex status
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

    Charset getCharset();

    void setCharset(Charset forName);

    void performFind(SearchParameters searchParameters);

    void performReplace(SearchParameters searchParameters, ReplaceParameters replaceParameters);

    boolean changeShowNonprintables();

    void showValuesPanel();

    void hideValuesPanel();

    boolean isValuesPanelVisible();

    boolean changeLineWrap();

    BinaryPanel getDocument();

    void printFile();

    BinaryDataUndoHandler getBinaryUndoHandler();

    ExtCodeArea getCodeArea();

    void setFileHandlingMode(FileHandlingMode valueOf);
}
