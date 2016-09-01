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
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.deltahex.swing.ColorsGroup;

/**
 * Enumeration of hexadecimal editor color types.
 *
 * @version 0.1.0 2016/08/31
 * @author ExBin Project (http://exbin.org)
 */
public enum HexColorType {
    HEADER_TEXT("Header Text", "hexColor.headerText"),
    HEADER_BACKGROUND("Header Background", "hexColor.headerBackground"),
    MAIN_AREA_TEXT("Main Area Text", "hexColor.mainAreaText"),
    MAIN_AREA_BACKGROUND("Main Area Background", "hexColor.mainAreaBackground"),
    MAIN_AREA_UNPRINTABLES("Main Area Unprintables", "hexColor.mainAreaUnprintables"),
    MAIN_AREA_UNPRINTABLES_BACKGROUND("Main Area Unprintables Background", "hexColor.mainAreaUnprintablesBackground"),
    ALTERNATE_TEXT("Alternate Text", "hexColor.alternateText"),
    ALTERNATE_BACKGROUND("Alternate Background", "hexColor.alternateBackground"),
    ALTERNATE_UNPRINTABLES("Alternate Unprintables", "hexColor.alternateUnprintables"),
    ALTERNATE_UNPRINTABLES_BACKGROUND("Alternate Unprintables Background", "hexColor.alternateUnprintablesBackground"),
    SELECTION("Selection Text", "hexColor.selection"),
    SELECTION_BACKGROUND("Selection Background", "hexColor.selectionBackground"),
    SELECTION_UNPRINTABLES("Selection Nonprintables", "hexColor.selectionUnprintables"),
    SELECTION_UNPRINTABLES_BACKGROUND("Selection Nonprintables Background", "hexColor.selectionUnprintablesBackground"),
    MIRROR_SELECTION("Mirror Selection Text", "hexColor.mirrorSelection"),
    MIRROR_SELECTION_BACKGROUND("Mirror Selection Background", "hexColor.mirrorSelectionBackground"),
    MIRROR_SELECTION_UNPRINTABLES("Mirror Selection Nonprintables", "hexColor.mirrorSelectionUnprintables"),
    MIRROR_SELECTION_UNPRINTABLES_BACKGROUND("Mirror Selection Nonprintables Background", "hexColor.mirrorSelectionUnprintablesBackground"),
    CURSOR("Cursor", "hexColor.cursor"),
    DECORATION_LINE("Decoration Line", "hexColor.decorationLine"),
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
                return codeArea.getAlternateColors().getUnprintablesBackgroundColor();
            case ALTERNATE_TEXT:
                return codeArea.getAlternateColors().getTextColor();
            case ALTERNATE_BACKGROUND:
                return codeArea.getAlternateColors().getBackgroundColor();
            case ALTERNATE_UNPRINTABLES:
                return codeArea.getAlternateColors().getUnprintablesColor();
            case ALTERNATE_UNPRINTABLES_BACKGROUND:
                return codeArea.getAlternateColors().getUnprintablesBackgroundColor();
            case SELECTION:
                return codeArea.getSelectionColors().getTextColor();
            case SELECTION_BACKGROUND:
                return codeArea.getSelectionColors().getBackgroundColor();
            case SELECTION_UNPRINTABLES:
                return codeArea.getSelectionColors().getUnprintablesColor();
            case SELECTION_UNPRINTABLES_BACKGROUND:
                return codeArea.getSelectionColors().getUnprintablesBackgroundColor();
            case MIRROR_SELECTION:
                return codeArea.getMirrorSelectionColors().getTextColor();
            case MIRROR_SELECTION_BACKGROUND:
                return codeArea.getMirrorSelectionColors().getBackgroundColor();
            case MIRROR_SELECTION_UNPRINTABLES:
                return codeArea.getMirrorSelectionColors().getUnprintablesColor();
            case MIRROR_SELECTION_UNPRINTABLES_BACKGROUND:
                return codeArea.getMirrorSelectionColors().getUnprintablesBackgroundColor();
            case CURSOR:
                return codeArea.getCursorColor();
            case DECORATION_LINE:
                return codeArea.getDecorationLineColor();
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
                ColorsGroup mainColors = codeArea.getMainColors();
                mainColors.setTextColor(color);
                codeArea.setMainColors(mainColors);
                break;
            }
            case MAIN_AREA_BACKGROUND: {
                ColorsGroup mainColors = codeArea.getMainColors();
                mainColors.setBackgroundColor(color);
                codeArea.setMainColors(mainColors);
                break;
            }
            case MAIN_AREA_UNPRINTABLES: {
                ColorsGroup mainColors = codeArea.getMainColors();
                mainColors.setUnprintablesColor(color);
                codeArea.setMainColors(mainColors);
                break;
            }
            case MAIN_AREA_UNPRINTABLES_BACKGROUND: {
                ColorsGroup mainColors = codeArea.getMainColors();
                mainColors.setUnprintablesBackgroundColor(color);
                codeArea.setMainColors(mainColors);
                break;
            }
            case ALTERNATE_TEXT: {
                ColorsGroup alternateColors = codeArea.getAlternateColors();
                alternateColors.setTextColor(color);
                codeArea.setAlternateColors(alternateColors);
                break;
            }
            case ALTERNATE_BACKGROUND: {
                ColorsGroup alternateColors = codeArea.getAlternateColors();
                alternateColors.setBackgroundColor(color);
                codeArea.setAlternateColors(alternateColors);
                break;
            }
            case ALTERNATE_UNPRINTABLES: {
                ColorsGroup alternateColors = codeArea.getAlternateColors();
                alternateColors.setUnprintablesColor(color);
                codeArea.setAlternateColors(alternateColors);
                break;
            }
            case ALTERNATE_UNPRINTABLES_BACKGROUND: {
                ColorsGroup alternateColors = codeArea.getAlternateColors();
                alternateColors.setUnprintablesBackgroundColor(color);
                codeArea.setAlternateColors(alternateColors);
                break;
            }
            case SELECTION: {
                ColorsGroup selectionColors = codeArea.getSelectionColors();
                selectionColors.setTextColor(color);
                codeArea.setSelectionColors(selectionColors);
                break;
            }
            case SELECTION_BACKGROUND: {
                ColorsGroup selectionColors = codeArea.getSelectionColors();
                selectionColors.setBackgroundColor(color);
                codeArea.setSelectionColors(selectionColors);
                break;
            }
            case SELECTION_UNPRINTABLES: {
                ColorsGroup selectionColors = codeArea.getSelectionColors();
                selectionColors.setUnprintablesColor(color);
                codeArea.setSelectionColors(selectionColors);
                break;
            }
            case SELECTION_UNPRINTABLES_BACKGROUND: {
                ColorsGroup selectionColors = codeArea.getSelectionColors();
                selectionColors.setUnprintablesBackgroundColor(color);
                codeArea.setSelectionColors(selectionColors);
                break;
            }
            case MIRROR_SELECTION: {
                ColorsGroup selectionColors = codeArea.getMirrorSelectionColors();
                selectionColors.setTextColor(color);
                codeArea.setMirrorSelectionColors(selectionColors);
                break;
            }
            case MIRROR_SELECTION_BACKGROUND: {
                ColorsGroup selectionColors = codeArea.getMirrorSelectionColors();
                selectionColors.setBackgroundColor(color);
                codeArea.setMirrorSelectionColors(selectionColors);
                break;
            }
            case MIRROR_SELECTION_UNPRINTABLES: {
                ColorsGroup selectionColors = codeArea.getMirrorSelectionColors();
                selectionColors.setUnprintablesColor(color);
                codeArea.setMirrorSelectionColors(selectionColors);
                break;
            }
            case MIRROR_SELECTION_UNPRINTABLES_BACKGROUND: {
                ColorsGroup selectionColors = codeArea.getMirrorSelectionColors();
                selectionColors.setUnprintablesBackgroundColor(color);
                codeArea.setMirrorSelectionColors(selectionColors);
                break;
            }
            case CURSOR: {
                codeArea.setCursorColor(color);
                break;
            }
            case DECORATION_LINE: {
                codeArea.setDecorationLineColor(color);
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
