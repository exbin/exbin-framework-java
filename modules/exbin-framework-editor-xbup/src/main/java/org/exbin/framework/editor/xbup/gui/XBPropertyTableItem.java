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
package org.exbin.framework.editor.xbup.gui;

import org.exbin.xbup.core.block.definition.XBParamType;
import org.exbin.xbup.core.catalog.base.XBCSpecDef;
import org.exbin.xbup.plugin.XBRowEditor;

/**
 * Parameters list table item record.
 *
 * @version 0.2.1 2020/09/20
 * @author ExBin Project (http://exbin.org)
 */
public class XBPropertyTableItem {

    private XBCSpecDef specDef;
    private String valueName;
    private String typeName;
    private XBRowEditor rowEditor;

    public XBPropertyTableItem(XBCSpecDef specDef, String valueName, String typeName, XBRowEditor rowEditor) {
        this.specDef = specDef;
        this.valueName = valueName;
        this.typeName = typeName;
        this.rowEditor = rowEditor;
    }

    public XBPropertyTableItem(XBCSpecDef specDef, String name, String type) {
        this(specDef, name, type, null);
    }

    public XBCSpecDef getSpecDef() {
        return specDef;
    }

    public void setSpecDef(XBCSpecDef specDef) {
        this.specDef = specDef;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public XBRowEditor getRowEditor() {
        return rowEditor;
    }

    public void setRowEditor(XBRowEditor rowEditor) {
        this.rowEditor = rowEditor;
    }

    public String getDefTypeName() {
        String defTypeName = "";
        if (!specDef.getTargetRev().isPresent()) {
            switch (specDef.getType()) {
                case CONSIST:
                case LIST_CONSIST: {
                    defTypeName = "Any";
                    break;
                }
                case JOIN:
                case LIST_JOIN: {
                    defTypeName = "Attribute";
                    break;
                }
            }
        } else {
            defTypeName = typeName;
        }
        if (specDef.getType() == XBParamType.LIST_CONSIST || specDef.getType() == XBParamType.LIST_JOIN) {
            defTypeName += "[]";
        }

        return defTypeName;
    }
}
