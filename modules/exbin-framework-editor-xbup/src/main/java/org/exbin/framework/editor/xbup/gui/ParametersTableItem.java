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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.xbup.core.block.definition.XBParamType;
import org.exbin.xbup.core.catalog.base.XBCSpecDef;
import org.exbin.xbup.plugin.XBRowEditor;

/**
 * Parameters list table item record.
 *
 * @version 0.1.24 2015/01/10
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ParametersTableItem {

    private XBCSpecDef specDef;
    private String valueName;
    private String typeName;
    private XBRowEditor rowEditor;

    public ParametersTableItem(XBCSpecDef specDef, String valueName, String typeName, @Nullable XBRowEditor rowEditor) {
        this.specDef = specDef;
        this.valueName = valueName;
        this.typeName = typeName;
        this.rowEditor = rowEditor;
    }

    public ParametersTableItem(XBCSpecDef specDef, String name, String type) {
        this(specDef, name, type, null);
    }

    @Nonnull
    public XBCSpecDef getSpecDef() {
        return specDef;
    }

    public void setSpecDef(XBCSpecDef spec) {
        this.specDef = spec;
    }

    @Nonnull
    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    @Nonnull
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Nullable
    public XBRowEditor getRowEditor() {
        return rowEditor;
    }

    public void setRowEditor(@Nullable XBRowEditor rowEditor) {
        this.rowEditor = rowEditor;
    }

    @Nonnull
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
