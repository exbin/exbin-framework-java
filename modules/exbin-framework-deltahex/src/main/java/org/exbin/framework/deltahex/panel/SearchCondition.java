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

import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.EditableBinaryData;

/**
 * Parameters for action to search for occurences of text or data.
 *
 * @version 0.1.0 2016/06/20
 * @author ExBin Project (http://exbin.org)
 */
public class SearchCondition {

    private SearchMode searchMode = SearchMode.TEXT;
    private String searchText = "";
    private EditableBinaryData binaryData;

    public SearchCondition() {
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

    public BinaryData getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(EditableBinaryData binaryData) {
        this.binaryData = binaryData;
    }

    public boolean isEmpty() {
        switch (searchMode) {
            case TEXT: {
                return searchText == null || searchText.isEmpty();
            }
            case BINARY: {
                return binaryData == null || binaryData.isEmpty();
            }
            default:
                throw new IllegalStateException("Unexpected search mode " + searchMode.name());
        }
    }

    public void clear() {
        searchText = "";
        if (binaryData != null) {
            binaryData.clear();
        }
    }

    public static enum SearchMode {
        TEXT, BINARY
    }
}
