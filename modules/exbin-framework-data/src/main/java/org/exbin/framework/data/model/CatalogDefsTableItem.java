/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.data.model;

import org.exbin.xbup.catalog.entity.XBERev;
import org.exbin.xbup.core.block.definition.XBParamType;
import org.exbin.xbup.core.catalog.base.XBCRev;
import org.exbin.xbup.core.catalog.base.XBCSpecDef;

/**
 * Table model for catalog specification definition.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class CatalogDefsTableItem {

    private XBCSpecDef specDef;
    private Long xbIndex;
    private XBParamType defType;
    private XBCRev target;
    private String stringId;
    private String name;
    private String description;

    /**
     * Chached values.
     */
    private Long revision;
    private String operation;
    private String type;

    public XBCSpecDef getSpecDef() {
        return specDef;
    }

    public void setSpecDef(XBCSpecDef specDef) {
        this.specDef = specDef;
    }

    public Long getXbIndex() {
        return xbIndex;
    }

    public void setXbIndex(Long xbIndex) {
        this.xbIndex = xbIndex;
    }

    public Long getRevision() {
        return revision;
    }

    public void setRevision(Long revision) {
        this.revision = revision;
    }

    public String getStringId() {
        return stringId;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String target) {
        this.operation = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public XBCRev getTarget() {
        return target;
    }

    public void setTarget(XBCRev target) {
        this.target = target;
    }

    public Long getTargetRevision() {
        return target != null ? target.getXBIndex() : null;
    }

    public void setTargetRevision(Long revision) {
        if (target != null) {
            ((XBERev) target).setXBIndex(revision);
        }
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

    public XBParamType getDefType() {
        return defType;
    }

    public void setDefType(XBParamType defType) {
        this.defType = defType;
    }
}
