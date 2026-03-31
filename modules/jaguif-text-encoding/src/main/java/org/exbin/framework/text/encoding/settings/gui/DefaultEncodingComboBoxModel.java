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
package org.exbin.framework.text.encoding.settings.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.exbin.framework.text.encoding.EncodingsManager;

/**
 * Text editor encodings options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultEncodingComboBoxModel implements ComboBoxModel<String> {

    private List<String> availableEncodings = new ArrayList<>();
    private String selectedEncoding = null;
    private final List<ListDataListener> dataListeners = new ArrayList<>();

    public DefaultEncodingComboBoxModel() {
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selectedEncoding = (String) anItem;
    }

    @Nullable
    @Override
    public Object getSelectedItem() {
        return selectedEncoding;
    }

    @Override
    public int getSize() {
        return availableEncodings.size();
    }

    @Nonnull
    @Override
    public String getElementAt(int index) {
        return availableEncodings.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener listener) {
        dataListeners.add(listener);
    }

    @Override
    public void removeListDataListener(ListDataListener listener) {
        dataListeners.remove(listener);
    }

    @Nonnull
    public List<String> getAvailableEncodings() {
        return availableEncodings;
    }

    public void setAvailableEncodings(List<String> encodings) {
        availableEncodings = new ArrayList<>();
        if (encodings.isEmpty()) {
            availableEncodings.add(EncodingsManager.ENCODING_UTF8);
        } else {
            availableEncodings.addAll(encodings);
        }
        int position = availableEncodings.indexOf(selectedEncoding);
        selectedEncoding = availableEncodings.get(position > 0 ? position : 0);

        for (int index = 0; index < dataListeners.size(); index++) {
            ListDataListener listDataListener = dataListeners.get(index);
            listDataListener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, availableEncodings.size()));
        }
    }

    @Nonnull
    public String getSelectedEncoding() {
        return selectedEncoding;
    }

    public void setSelectedEncoding(String selectedEncoding) {
        this.selectedEncoding = selectedEncoding;
    }
}
