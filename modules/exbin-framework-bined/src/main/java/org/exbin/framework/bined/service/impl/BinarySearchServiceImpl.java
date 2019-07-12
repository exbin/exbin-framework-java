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
package org.exbin.framework.bined.service.impl;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightNonAsciiCodeAreaPainter;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.bined.ReplaceParameters;
import org.exbin.framework.bined.SearchParameters;
import org.exbin.framework.bined.service.BinarySearchService;

/**
 * Binary search service.
 *
 * @version 0.2.1 2019/07/12
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinarySearchServiceImpl implements BinarySearchService {

    private final ExtCodeArea codeArea;

    public BinarySearchServiceImpl(ExtCodeArea codeArea) {
        this.codeArea = codeArea;
    }

    @Override
    public void performFind(SearchParameters dialogSearchParameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMatchPosition(int matchPosition) {
        ExtendedHighlightNonAsciiCodeAreaPainter painter = (ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter();
        painter.setCurrentMatchIndex(matchPosition);
        ExtendedHighlightNonAsciiCodeAreaPainter.SearchMatch currentMatch = painter.getCurrentMatch();
        codeArea.revealPosition(currentMatch.getPosition(), 0, codeArea.getActiveSection());
        codeArea.repaint();
    }

    @Override
    public void updatePosition() {
        throw new UnsupportedOperationException("Not supported yet.");
        // TODO binarySearchPanel.updatePosition(codeArea.getCaretPosition().getDataPosition(), codeArea.getDataSize());
    }

    @Override
    public void performReplace(SearchParameters searchParameters, ReplaceParameters replaceParameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearMatches() {
        ExtendedHighlightNonAsciiCodeAreaPainter painter = (ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter();
        painter.clearMatches();
    }
}
