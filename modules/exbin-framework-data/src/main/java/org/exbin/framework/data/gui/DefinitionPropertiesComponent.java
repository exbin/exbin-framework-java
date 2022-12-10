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
package org.exbin.framework.data.gui;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.data.model.CatalogDefsTableItem;
import org.exbin.framework.data.model.CatalogDefsDetailTableModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Definition properties table component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefinitionPropertiesComponent extends JScrollPane {

    private final JTable propertiesTable;
    private final CatalogDefsDetailTableModel model = new CatalogDefsDetailTableModel();

    public DefinitionPropertiesComponent() {
        propertiesTable = new JTable();
        init();
    }

    private void init() {
        propertiesTable.setModel(model);
        setViewportView(propertiesTable);
    }

    public void setItem(CatalogDefsTableItem item) {
        model.setItem(item);
    }
}
