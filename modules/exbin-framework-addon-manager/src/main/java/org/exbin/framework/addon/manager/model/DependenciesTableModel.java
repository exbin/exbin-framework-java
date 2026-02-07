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
package org.exbin.framework.addon.manager.model;

import org.exbin.framework.addon.manager.api.DependencyRecord;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.table.AbstractTableModel;
import org.exbin.framework.App;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Addon dependency record.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DependenciesTableModel extends AbstractTableModel {

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(DependenciesTableModel.class);

    private List<DependencyRecord> dependencies;

    public DependenciesTableModel() {
    }

    public void setDependencies(@Nullable List<DependencyRecord> dependencies) {
        this.dependencies = dependencies;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return dependencies == null ? 0 : dependencies.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Nonnull
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 1:
                return String.class;
            case 2:
                return Boolean.class;
            default:
                throw new AssertionError();
        }
    }

    @Nonnull
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return resourceBundle.getString("typeColumn.name");
            case 1:
                return resourceBundle.getString("valueColumn.name");
            case 2:
                return resourceBundle.getString("optionalColumn.name");
            default:
                throw new AssertionError();
        }
    }

    @Nonnull
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DependencyRecord record = dependencies.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return resourceBundle.getString("dependencyType." + record.getType().name().toLowerCase());
            case 1:
                return record.getId();
            case 2:
                return record.isOptional();
            default:
                throw new AssertionError();
        }
    }
}
