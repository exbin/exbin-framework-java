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

import org.exbin.xbup.core.catalog.base.XBCRev;

/**
 * Table model for catalog specification revisions.
 *
 * @version 0.1.24 2014/11/13
 * @author ExBin Project (http://exbin.org)
 */
public class CatalogRevsTableItem {

    private XBCRev rev;
    private Long xbIndex;
    private Long limit;
    private String name;
    private String description;

    public XBCRev getRev() {
        return rev;
    }

    public void setRev(XBCRev rev) {
        this.rev = rev;
    }

    public Long getXbIndex() {
        return xbIndex;
    }

    public void setXbIndex(Long xbIndex) {
        this.xbIndex = xbIndex;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
