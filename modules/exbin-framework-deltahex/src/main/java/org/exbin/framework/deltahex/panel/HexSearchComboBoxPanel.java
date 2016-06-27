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
package org.exbin.framework.deltahex.panel;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.KeyListener;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.exbin.deltahex.CodeArea;
import org.exbin.utils.binary_data.ByteArrayEditableData;

/**
 * Combo box panel supporting both binary and text values.
 *
 * @version 0.1.0 2016/06/27
 * @author ExBin Project (http://exbin.org)
 */
public class HexSearchComboBoxPanel extends JPanel {

    public static final String TEXT_MODE = "text";
    public static final String BINARY_MODE = "binary";

    private final JTextField textField;
    private final CodeArea hexadecimalEditor = new CodeArea();

    private SearchCondition item;

    private ValueChangedListener valueChangedListener = null;

    public HexSearchComboBoxPanel() {
        super.setLayout(new CardLayout());
        Border comboBoxBorder = ((JComponent) (new JComboBox<>().getEditor().getEditorComponent())).getBorder();
        textField = new JTextField();
        textField.setBorder(comboBoxBorder);
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                comboBoxValueChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                comboBoxValueChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                comboBoxValueChanged();
            }
        });

        super.add(textField, TEXT_MODE);

        hexadecimalEditor.setShowHeader(false);
        hexadecimalEditor.setShowLineNumbers(false);
        hexadecimalEditor.setWrapMode(true);
        hexadecimalEditor.setBackgroundMode(CodeArea.BackgroundMode.PLAIN);
        hexadecimalEditor.setVerticalScrollBarVisibility(CodeArea.ScrollBarVisibility.NEVER);
        hexadecimalEditor.setHorizontalScrollBarVisibility(CodeArea.ScrollBarVisibility.NEVER);
        hexadecimalEditor.setData(new ByteArrayEditableData());
        hexadecimalEditor.setBorder(comboBoxBorder);
        hexadecimalEditor.addDataChangedListener(new CodeArea.DataChangedListener() {
            @Override
            public void dataChanged() {
                comboBoxValueChanged();
            }
        });
        super.add(hexadecimalEditor, BINARY_MODE);
    }

    public SearchCondition getItem() {
        return item;
    }

    public void setItem(SearchCondition item) {
        this.item = item;
        switch (item.getSearchMode()) {
            case TEXT: {
                CardLayout layout = (CardLayout) getLayout();
                layout.show(this, TEXT_MODE);
                revalidate();
                break;
            }
            case BINARY: {
                CardLayout layout = (CardLayout) getLayout();
                layout.show(this, BINARY_MODE);
                revalidate();
                break;
            }
        }
    }

    public void selectAll() {
        switch (item.getSearchMode()) {
            case TEXT: {
                textField.selectAll();
                break;
            }
            case BINARY: {
                hexadecimalEditor.selectAll();
                break;
            }
        }
    }

    private void comboBoxValueChanged() {
        if (valueChangedListener != null) {
            valueChangedListener.valueChanged();
        }
    }

    public void addValueKeyListener(KeyListener editorKeyListener) {
        textField.addKeyListener(editorKeyListener);
        hexadecimalEditor.addKeyListener(editorKeyListener);
    }

    public void setValueChangedListener(ValueChangedListener valueChangedListener) {
        this.valueChangedListener = valueChangedListener;
    }

    /**
     * Listener for value change.
     */
    public static interface ValueChangedListener {

        void valueChanged();
    }
}
