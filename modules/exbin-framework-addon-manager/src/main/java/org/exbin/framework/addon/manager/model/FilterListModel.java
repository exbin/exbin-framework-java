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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractListModel;
import org.exbin.framework.addon.manager.gui.FilterListPanel;

/**
 * Filter list model.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FilterListModel extends AbstractListModel<ItemRecord> {

    private FilterListPanel.Controller controller;
    private int size = 0;

    public void setController(FilterListPanel.Controller controller) {
        int newSize = controller.getItemsCount();
        if (this.controller != null && size > 0) {
            fireIntervalRemoved(this, 0, size - 1);
        }
        this.controller = controller;
        if (newSize > 0) {
            fireIntervalAdded(this, 0, newSize - 1);
        }
        this.size = newSize;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Nonnull
    @Override
    public ItemRecord getElementAt(int index) {
        try {
            return controller.getItem(index);
        } catch (IndexOutOfBoundsException ex) {
            return new ItemRecord();
        }
    }
}
