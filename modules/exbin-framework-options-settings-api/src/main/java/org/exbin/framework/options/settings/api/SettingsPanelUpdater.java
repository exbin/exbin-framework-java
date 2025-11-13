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
package org.exbin.framework.options.settings.api;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextArea;

/**
 * Updater for settings panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SettingsPanelUpdater {

    protected final SettingsModifiedListener modifiedListener;

    public SettingsPanelUpdater(SettingsModifiedListener modifiedListener) {
        this.modifiedListener = modifiedListener;
    }

    public void setCheckBoxSelected(JCheckBox checkBox, boolean selected) {
        if (selected != checkBox.isSelected()) {
            checkBox.setSelected(selected);
            modifiedListener.notifyModified();
        }
    }

    public void setComboBoxValue(JComboBox<String> comboBox, String value) {
        if (!value.equals(comboBox.getSelectedItem())) {
            comboBox.setSelectedItem(value);
            modifiedListener.notifyModified();
        }
    }

    public void setComboBoxIndex(JComboBox<String> comboBox, int index) {
        if (index != comboBox.getSelectedIndex()) {
            comboBox.setSelectedIndex(index);
            modifiedListener.notifyModified();
        }
    }

    public void setTextAreaText(JTextArea textArea, String value) {
        if (!value.equals(textArea.getText())) {
            textArea.setText(value);
            modifiedListener.notifyModified();
        }
    }

    public void setSpinnerInteger(JSpinner spinner, Integer value) {
        if (!value.equals(spinner.getValue())) {
            spinner.setValue(value);
            modifiedListener.notifyModified();
        }
    }

    public void setSpinnerLong(JSpinner spinner, Long value) {
        if (!value.equals(spinner.getValue())) {
            spinner.setValue(value);
            modifiedListener.notifyModified();
        }
    }
}
