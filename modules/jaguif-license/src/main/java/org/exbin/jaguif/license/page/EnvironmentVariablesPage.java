/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.license.page;

import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.exbin.jaguif.App;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.tabpages.api.AbstractTabPagesComponent;

/**
 * Environment variables page.
 */
@ParametersAreNonnullByDefault
public class EnvironmentVariablesPage extends AbstractTabPagesComponent {

    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(EnvironmentVariablesPage.class);
    protected final JScrollPane environmentScrollPane = new JScrollPane();
    protected final JTable environmentTable = new JTable();
    protected boolean variablesLoaded = false;

    public EnvironmentVariablesPage() {
        putValue(KEY_NAME, resourceBundle.getString("pageName"));
        environmentScrollPane.setViewportView(environmentTable);

        environmentTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    resourceBundle.getString("environmentTable.propertyColumn"), resourceBundle.getString("environmentTable.valueColumn")
                }
        ) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        if (!variablesLoaded) {
            // Fill system properties tab
            Properties systemProperties = System.getProperties();
            DefaultTableModel tableModel = (DefaultTableModel) environmentTable.getModel();
            Set<java.util.Map.Entry<Object, Object>> items = systemProperties.entrySet();
            items.stream().map((entry) -> {
                Object[] line = new Object[2];
                line[0] = entry.getKey();
                line[1] = entry.getValue();
                return line;
            }).forEachOrdered((line) -> {
                tableModel.addRow(line);
            });

            variablesLoaded = true;
        }
        return environmentScrollPane;
    }
}
