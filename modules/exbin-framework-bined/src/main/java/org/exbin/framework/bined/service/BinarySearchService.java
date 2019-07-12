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
package org.exbin.framework.bined.service;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.ReplaceParameters;
import org.exbin.framework.bined.SearchParameters;

/**
 * Binary search service.
 *
 * @version 0.2.1 2019/07/12
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface BinarySearchService {

    void performFind(SearchParameters dialogSearchParameters);

    void setMatchPosition(int matchPosition);

    void updatePosition();

    void performReplace(SearchParameters searchParameters, ReplaceParameters replaceParameters);

    void clearMatches();
}
