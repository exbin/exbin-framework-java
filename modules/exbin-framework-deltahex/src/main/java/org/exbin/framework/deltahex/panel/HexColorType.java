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
package org.exbin.framework.deltahex.panel;

import java.awt.Color;
import org.exbin.deltahex.CodeArea;

/**
 * Enumeration of hexadecimal editor color types.
 *
 * @version 0.1.0 2016/06/23
 * @author ExBin Project (http://exbin.org)
 */
public enum HexColorType {
    HEADER_TEXT("Header Text", "hexColor.headerText"),
    HEADER_BACKGROUND("Header Background", "hexColor.headerBackground"),
    MAIN_AREA_TEXT("Main Area Text", "hexColor.mainAreaText"),
    MAIN_AREA_BACKGROUND("Main Area Background", "hexColor.mainAreaBackground"),
    MAIN_AREA_UNPRINTABLES("Main Area Nonprintables", "hexColor.mainAreaUnprintables"),
    MAIN_AREA_UNPRINTABLES_BACKGROUND("Main Area Nonprintables Background", "hexColor.mainAreaUnprintablesBackground"),
    SELECTION("Selection Text", "hexColor.selection"),
    SELECTION_BACKGROUND("Selection Background", "hexColor.selectionBackground"),
    SELECTION_UNPRINTABLES("Selection Nonprintables", "hexColor.selectionUnprintables"),
    SELECTION_UNPRINTABLES_BACKGROUND("Selection Nonprintables Background", "hexColor.selectionUnprintablesBackground"),
    FOUND("Decoration Line", "hexColor.found");

    private final String preferencesString;
    private final String title;

    HexColorType(String title, String preferencesString) {
        this.title = title;
        this.preferencesString = preferencesString;
    }

    public String getPreferencesString() {
        return preferencesString;
    }

    public String getTitle() {
        return title;
    }

    public Color getColorFromCodeArea(CodeArea codeArea) {
        switch (this) {
            case HEADER_TEXT:
                return codeArea.getForeground();
            case HEADER_BACKGROUND:
                return codeArea.getBackground();
            case MAIN_AREA_TEXT:
                return codeArea.getMainColors().getTextColor();
            case MAIN_AREA_BACKGROUND:
                return codeArea.getMainColors().getBackgroundColor();
            case MAIN_AREA_UNPRINTABLES:
                return codeArea.getMainColors().getUnprintablesColor();
            case MAIN_AREA_UNPRINTABLES_BACKGROUND:
                return codeArea.getMainColors().getUnprintablesBackgroundColor();
            case SELECTION:
                return codeArea.getSelectionColors().getTextColor();
            case SELECTION_BACKGROUND:
                return codeArea.getSelectionColors().getBackgroundColor();
            case SELECTION_UNPRINTABLES:
                return codeArea.getSelectionColors().getUnprintablesColor();
            case SELECTION_UNPRINTABLES_BACKGROUND:
                return codeArea.getSelectionColors().getUnprintablesBackgroundColor();
            case FOUND:
                return Color.RED;
            default:
                throw new AssertionError();
        }
    }

    public void setColorToCodeArea(CodeArea codeArea, Color color) {
        switch (this) {
            case HEADER_TEXT: {
                codeArea.setForeground(color);
                break;
            }
            case HEADER_BACKGROUND: {
                codeArea.setBackground(color);
                break;
            }
            case MAIN_AREA_TEXT: {
                CodeArea.ColorsGroup mainColors = codeArea.getMainColors();
                mainColors.setTextColor(color);
                codeArea.setMainColors(mainColors);
                break;
            }
            case MAIN_AREA_BACKGROUND: {
                CodeArea.ColorsGroup mainColors = codeArea.getMainColors();
                mainColors.setBackgroundColor(color);
                codeArea.setMainColors(mainColors);
                break;
            }
            case MAIN_AREA_UNPRINTABLES: {
                CodeArea.ColorsGroup mainColors = codeArea.getMainColors();
                mainColors.setUnprintablesColor(color);
                codeArea.setMainColors(mainColors);
                break;
            }
            case MAIN_AREA_UNPRINTABLES_BACKGROUND: {
                CodeArea.ColorsGroup mainColors = codeArea.getMainColors();
                mainColors.setUnprintablesBackgroundColor(color);
                codeArea.setMainColors(mainColors);
                break;
            }
            case SELECTION: {
                CodeArea.ColorsGroup selectionColors = codeArea.getSelectionColors();
                selectionColors.setTextColor(color);
                codeArea.setSelectionColors(selectionColors);
                break;
            }
            case SELECTION_BACKGROUND: {
                CodeArea.ColorsGroup selectionColors = codeArea.getSelectionColors();
                selectionColors.setBackgroundColor(color);
                codeArea.setSelectionColors(selectionColors);
                break;
            }
            case SELECTION_UNPRINTABLES: {
                CodeArea.ColorsGroup selectionColors = codeArea.getSelectionColors();
                selectionColors.setUnprintablesColor(color);
                codeArea.setSelectionColors(selectionColors);
                break;
            }
            case SELECTION_UNPRINTABLES_BACKGROUND: {
                CodeArea.ColorsGroup selectionColors = codeArea.getSelectionColors();
                selectionColors.setUnprintablesBackgroundColor(color);
                codeArea.setSelectionColors(selectionColors);
                break;
            }
            case FOUND: {
                break;
            }

            default:
                throw new AssertionError();
        }
    }
}
