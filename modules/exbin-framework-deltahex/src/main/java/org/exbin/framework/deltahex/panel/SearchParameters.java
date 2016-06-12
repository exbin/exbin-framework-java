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

/**
 * Parameters for action to search for occurences of text or data.
 *
 * @version 0.1.0 2016/06/12
 * @author ExBin Project (http://exbin.org)
 */
public class SearchParameters {

    private SearchMode searchMode = SearchMode.TEXT;
    private String searchText;
    private boolean searchFromCursor;
    private boolean matchCase;
    private boolean multipleMatches;

    public SearchParameters() {
    }

    public SearchMode getSearchMode() {
        return searchMode;
    }

    public void setSearchMode(SearchMode searchMode) {
        this.searchMode = searchMode;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public boolean isSearchFromCursor() {
        return searchFromCursor;
    }

    public void setSearchFromCursor(boolean searchFromCursor) {
        this.searchFromCursor = searchFromCursor;
    }

    public boolean isMatchCase() {
        return matchCase;
    }

    public void setMatchCase(boolean matchCase) {
        this.matchCase = matchCase;
    }

    public boolean isMultipleMatches() {
        return multipleMatches;
    }

    public void setMultipleMatches(boolean multipleMatches) {
        this.multipleMatches = multipleMatches;
    }

    public static enum SearchMode {
        TEXT, BINARY
    }
}
