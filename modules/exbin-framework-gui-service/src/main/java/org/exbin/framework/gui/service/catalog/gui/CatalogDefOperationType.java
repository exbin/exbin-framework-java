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
package org.exbin.framework.gui.service.catalog.gui;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of specification definition's operation types.
 *
 * @version 0.2.0 2016/02/01
 * @author ExBin Project (http://exbin.org)
 */
public enum CatalogDefOperationType {

    CONSIST(0, "Consist"),
    JOIN(1, "Join"),
    ANY(2, "Any"),
    ATTRIBUTE(3, "Attribute"),
    CONSIST_LIST(4, "Consist[]"),
    JOIN_LIST(5, "Join[]"),
    ANY_LIST(6, "Any[]"),
    ATTRIBUTE_LIST(7, "Attribute[]");

    final int rowIndex;
    private final String caption;
    private static final Map<Integer, CatalogDefOperationType> map = new HashMap<>();

    static {
        for (CatalogDefOperationType operationType : CatalogDefOperationType.values()) {
            map.put(operationType.rowIndex, operationType);
        }
    }

    CatalogDefOperationType(int rowIndex, String caption) {
        this.rowIndex = rowIndex;
        this.caption = caption;
    }

    public static CatalogDefOperationType valueOf(int rowIndex) {
        return map.get(rowIndex);
    }

    public static String[] getAsArray() {
        return getAsArray(map.size());
    }

    public static String[] getAsArray(int rowsCount) {
        String[] rows = new String[rowsCount];
        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            rows[rowIndex] = valueOf(rowIndex).caption;
        }
        return rows;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public String getCaption() {
        return caption;
    }
}
