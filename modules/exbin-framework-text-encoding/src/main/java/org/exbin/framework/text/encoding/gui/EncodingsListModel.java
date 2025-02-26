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
package org.exbin.framework.text.encoding.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractListModel;

/**
 * List model for encoding / character sets.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EncodingsListModel extends AbstractListModel<String> {

    private final List<String> charsets = new ArrayList<>();

    @Override
    public int getSize() {
        return charsets.size();
    }

    @Nonnull
    @Override
    public String getElementAt(int index) {
        return charsets.get(index);
    }

    /**
     * @return the charsets
     */
    @Nonnull
    public List<String> getCharsets() {
        return charsets;
    }

    /**
     * @param charsets the charsets to set
     */
    public void setCharsets(@Nullable List<String> charsets) {
        this.charsets.clear();
        if (charsets != null) {
            this.charsets.addAll(charsets);
        }
        fireContentsChanged(this, 0, this.charsets.size());
    }

    public void addAll(List<String> list, int pos) {
        if (pos >= 0) {
            charsets.addAll(pos, list);
            fireIntervalAdded(this, pos, list.size() + pos);
        } else {
            charsets.addAll(list);
            fireIntervalAdded(this, charsets.size() - list.size(), charsets.size());
        }
    }

    public void removeIndices(int[] indices) {
        if (indices.length == 0) {
            return;
        }

        for (int i = indices.length - 1; i >= 0; i--) {
            charsets.remove(indices[i]);
            fireIntervalRemoved(this, indices[i], indices[i]);
        }
    }

    public void remove(int index) {
        charsets.remove(index);
        fireIntervalRemoved(this, index, index);
    }

    public void add(int index, String item) {
        charsets.add(index, item);
        fireIntervalAdded(this, index, index);
    }
}
