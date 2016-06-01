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
 * Paramters for action to search for occurences of text or data.
 *
 * @version 0.1.0 2016/06/01
 * @author ExBin Project (http://exbin.org)
 */
public class SearchParameters {

    private String searchText;
    private boolean searchFromCursor;

    public SearchParameters() {
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
}
