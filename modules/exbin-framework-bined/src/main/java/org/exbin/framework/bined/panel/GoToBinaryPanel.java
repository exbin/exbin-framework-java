/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.bined.panel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.PositionCodeType;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;

/**
 * Go-to position panel for hexadecimal editor.
 *
 * @version 0.2.1 2019/06/21
 * @author ExBin Project (http://exbin.org)
 */
public class GoToBinaryPanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(GoToBinaryPanel.class);

    private long cursorPosition;
    private long maxPosition;
    private GoToMode goToMode = GoToMode.FROM_START;
    private final PositionSpinnerEditor positionSpinnerEditor;

    public GoToBinaryPanel() {
        initComponents();

        positionSpinnerEditor = new PositionSpinnerEditor(positionSpinner);
        positionSpinner.setEditor(positionSpinnerEditor);

        // Spinner selection workaround from http://forums.sun.com/thread.jspa?threadID=409748&forumID=57
        positionSpinnerEditor.getTextField().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (e.getSource() instanceof JTextComponent) {
                    final JTextComponent textComponent = ((JTextComponent) e.getSource());
                    SwingUtilities.invokeLater(textComponent::selectAll);
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        positionTypeButtonGroup = new javax.swing.ButtonGroup();
        positionTypePopupMenu = new javax.swing.JPopupMenu();
        octalMenuItem = new javax.swing.JMenuItem();
        decimalMenuItem = new javax.swing.JMenuItem();
        hexadecimalMenuItem = new javax.swing.JMenuItem();
        currentPositionLabel = new javax.swing.JLabel();
        currentPositionTextField = new javax.swing.JTextField();
        targetPositionLabel = new javax.swing.JLabel();
        targetPositionTextField = new javax.swing.JTextField();
        goToPanel = new javax.swing.JPanel();
        fromStartRadioButton = new javax.swing.JRadioButton();
        fromEndRadioButton = new javax.swing.JRadioButton();
        relativeRadioButton = new javax.swing.JRadioButton();
        positionLabel = new javax.swing.JLabel();
        positionSpinner = new javax.swing.JSpinner();
        positionTypeButton = new javax.swing.JButton();

        octalMenuItem.setText("OCT");
        octalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                octalMenuItemActionPerformed(evt);
            }
        });
        positionTypePopupMenu.add(octalMenuItem);

        decimalMenuItem.setText("DEC");
        decimalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decimalMenuItemActionPerformed(evt);
            }
        });
        positionTypePopupMenu.add(decimalMenuItem);

        hexadecimalMenuItem.setText("HEX");
        hexadecimalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hexadecimalMenuItemActionPerformed(evt);
            }
        });
        positionTypePopupMenu.add(hexadecimalMenuItem);

        currentPositionLabel.setText(resourceBundle.getString("currentPositionLabel.text")); // NOI18N

        currentPositionTextField.setEditable(false);
        currentPositionTextField.setText("0"); // NOI18N

        targetPositionLabel.setText(resourceBundle.getString("targetPositionLabel.text")); // NOI18N

        targetPositionTextField.setEditable(false);
        targetPositionTextField.setText("0"); // NOI18N

        goToPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle.getString("goToPanel.border.title"))); // NOI18N

        positionTypeButtonGroup.add(fromStartRadioButton);
        fromStartRadioButton.setSelected(true);
        fromStartRadioButton.setText(resourceBundle.getString("fromStartRadioButton.text")); // NOI18N
        fromStartRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromStartRadioButtonActionPerformed(evt);
            }
        });

        positionTypeButtonGroup.add(fromEndRadioButton);
        fromEndRadioButton.setText(resourceBundle.getString("fromEndRadioButton.text")); // NOI18N
        fromEndRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromEndRadioButtonActionPerformed(evt);
            }
        });

        positionTypeButtonGroup.add(relativeRadioButton);
        relativeRadioButton.setText(resourceBundle.getString("relativeRadioButton.text")); // NOI18N
        relativeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relativeRadioButtonActionPerformed(evt);
            }
        });

        positionLabel.setText(resourceBundle.getString("positionLabel.text")); // NOI18N

        positionSpinner.setModel(new javax.swing.SpinnerNumberModel(0L, null, null, 1L));
        positionSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                positionSpinnerStateChanged(evt);
            }
        });

        positionTypeButton.setText("DEC");
        positionTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                positionTypeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout goToPanelLayout = new javax.swing.GroupLayout(goToPanel);
        goToPanel.setLayout(goToPanelLayout);
        goToPanelLayout.setHorizontalGroup(
            goToPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fromStartRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(relativeRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
            .addComponent(fromEndRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(goToPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(goToPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(positionTypeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(positionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(positionSpinner)
                .addContainerGap())
        );
        goToPanelLayout.setVerticalGroup(
            goToPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(goToPanelLayout.createSequentialGroup()
                .addComponent(fromStartRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fromEndRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(relativeRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(positionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(goToPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(positionSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(positionTypeButton)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(currentPositionTextField)
                    .addComponent(goToPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(targetPositionTextField)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(currentPositionLabel)
                            .addComponent(targetPositionLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(currentPositionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currentPositionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(goToPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(targetPositionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(targetPositionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fromStartRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromStartRadioButtonActionPerformed
        if (goToMode != GoToMode.FROM_START && fromStartRadioButton.isSelected()) {
            goToMode = GoToMode.FROM_START;
            long currentValue = getPositionValue();
            setPositionValue(0l);
            ((SpinnerNumberModel) positionSpinner.getModel()).setMinimum(0l);
            ((SpinnerNumberModel) positionSpinner.getModel()).setMaximum(maxPosition);
            setPositionValue(cursorPosition + currentValue);
            positionSpinner.revalidate();
            updateTargetPosition();
        }
    }//GEN-LAST:event_fromStartRadioButtonActionPerformed

    private void relativeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relativeRadioButtonActionPerformed
        if (goToMode != GoToMode.RELATIVE && relativeRadioButton.isSelected()) {
            goToMode = GoToMode.RELATIVE;
            long currentValue = getPositionValue();
            setPositionValue(0l);
            ((SpinnerNumberModel) positionSpinner.getModel()).setMinimum(-cursorPosition);
            ((SpinnerNumberModel) positionSpinner.getModel()).setMaximum(maxPosition - cursorPosition);
            setPositionValue(currentValue - cursorPosition);
            positionSpinner.revalidate();
            updateTargetPosition();
        }
    }//GEN-LAST:event_relativeRadioButtonActionPerformed

    private void positionSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_positionSpinnerStateChanged
        updateTargetPosition();
    }//GEN-LAST:event_positionSpinnerStateChanged

    private void fromEndRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromEndRadioButtonActionPerformed
        if (goToMode == GoToMode.FROM_END && fromEndRadioButton.isSelected()) {
            goToMode = GoToMode.FROM_END;
            long currentValue = getPositionValue();
            positionSpinner.setValue(0l);
            ((SpinnerNumberModel) positionSpinner.getModel()).setMinimum(0l);
            ((SpinnerNumberModel) positionSpinner.getModel()).setMaximum(maxPosition);
            setPositionValue(maxPosition - currentValue);
            positionSpinner.revalidate();
            updateTargetPosition();
        }
    }//GEN-LAST:event_fromEndRadioButtonActionPerformed

    private void octalMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_octalMenuItemActionPerformed
        switchNumBase(PositionCodeType.OCTAL);
    }//GEN-LAST:event_octalMenuItemActionPerformed

    private void decimalMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decimalMenuItemActionPerformed
        switchNumBase(PositionCodeType.DECIMAL);
    }//GEN-LAST:event_decimalMenuItemActionPerformed

    private void hexadecimalMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hexadecimalMenuItemActionPerformed
        switchNumBase(PositionCodeType.HEXADECIMAL);
    }//GEN-LAST:event_hexadecimalMenuItemActionPerformed

    private void positionTypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_positionTypeButtonActionPerformed
        PositionCodeType positionCodeType = positionSpinnerEditor.getPositionCodeType();
        switch (positionCodeType) {
            case OCTAL: {
                switchNumBase(PositionCodeType.DECIMAL);
                break;
            }
            case DECIMAL: {
                switchNumBase(PositionCodeType.HEXADECIMAL);
                break;
            }
            case HEXADECIMAL: {
                switchNumBase(PositionCodeType.OCTAL);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected position type " + positionCodeType.name());
        }
    }//GEN-LAST:event_positionTypeButtonActionPerformed

    private void updateTargetPosition() {
        targetPositionTextField.setText(String.valueOf(getGoToPosition()));
    }

    public void initFocus() {
        /* ((JSpinner.DefaultEditor) positionSpinner.getEditor()) */
        positionSpinnerEditor.getTextField().requestFocusInWindow();
    }

    public long getGoToPosition() {
        long position = getPositionValue();
        switch (goToMode) {
            case FROM_START:
                return position;
            case FROM_END:
                return maxPosition - position;
            case RELATIVE:
                return cursorPosition + position;
            default:
                throw new IllegalStateException("Unexpected go to mode " + goToMode.name());
        }
    }

    public long getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(long cursorPosition) {
        this.cursorPosition = cursorPosition;
        setPositionValue(cursorPosition);
        currentPositionTextField.setText(String.valueOf(cursorPosition));
    }

    public void setMaxPosition(long maxPosition) {
        this.maxPosition = maxPosition;
        ((SpinnerNumberModel) positionSpinner.getModel()).setMaximum(maxPosition);
        positionSpinner.revalidate();
        updateTargetPosition();
    }

    public void setSelected() {
        positionSpinner.requestFocusInWindow();
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    private void switchNumBase(PositionCodeType codeType) {
        long positionValue = getPositionValue();
        positionTypeButton.setText(codeType.name().substring(0, 3));
        positionSpinnerEditor.setPositionCodeType(codeType);
        setPositionValue(positionValue);
    }

    private long getPositionValue() {
        return (Long) positionSpinner.getValue();
    }

    private void setPositionValue(long value) {
        positionSpinner.setValue(value);
        positionSpinner.firePropertyChange("value", value, value);
    }

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new GoToBinaryPanel());
    }

    public void acceptInput() {
        try {
            positionSpinner.commitEdit();
        } catch (ParseException ex) {
            // Ignore parse exception
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel currentPositionLabel;
    private javax.swing.JTextField currentPositionTextField;
    private javax.swing.JMenuItem decimalMenuItem;
    private javax.swing.JRadioButton fromEndRadioButton;
    private javax.swing.JRadioButton fromStartRadioButton;
    private javax.swing.JPanel goToPanel;
    private javax.swing.JMenuItem hexadecimalMenuItem;
    private javax.swing.JMenuItem octalMenuItem;
    private javax.swing.JLabel positionLabel;
    private javax.swing.JSpinner positionSpinner;
    private javax.swing.JButton positionTypeButton;
    private javax.swing.ButtonGroup positionTypeButtonGroup;
    private javax.swing.JPopupMenu positionTypePopupMenu;
    private javax.swing.JRadioButton relativeRadioButton;
    private javax.swing.JLabel targetPositionLabel;
    private javax.swing.JTextField targetPositionTextField;
    // End of variables declaration//GEN-END:variables

    public enum GoToMode {
        FROM_START, FROM_END, RELATIVE
    }

    @ParametersAreNonnullByDefault
    private static class PositionSpinnerEditor extends JPanel implements ChangeListener, PropertyChangeListener, LayoutManager {

        private static final int LENGTH_LIMIT = 21;

        private PositionCodeType positionCodeType = PositionCodeType.DECIMAL;

        private final char[] cache = new char[LENGTH_LIMIT];

        private final JTextField textField;
        private final JSpinner spinner;

        public PositionSpinnerEditor(JSpinner spinner) {
            this.spinner = spinner;
            textField = new JTextField();

            init();
        }

        private void init() {
            textField.setName("Spinner.textField");
            textField.setText(getPositionAsString((Long) spinner.getValue()));
            textField.addPropertyChangeListener(this);
            textField.getDocument().addDocumentListener(new DocumentListener() {
                private final PropertyChangeEvent changeEvent = new PropertyChangeEvent(spinner, "text", null, null);

                @Override
                public void changedUpdate(DocumentEvent e) {
                    notifyChanged();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    notifyChanged();
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    notifyChanged();
                }

                public void notifyChanged() {
                    propertyChange(changeEvent);
                }
            });
            textField.setEditable(true);
            textField.setInheritsPopupMenu(true);

            String toolTipText = spinner.getToolTipText();
            if (toolTipText != null) {
                textField.setToolTipText(toolTipText);
            }

            add(textField);

            setLayout(this);
            spinner.addChangeListener(this);
        }

        @Nonnull
        private JTextField getTextField() {
            return textField;
        }

        @Nonnull
        private JSpinner getSpinner() {
            return spinner;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            JSpinner sourceSpinner = (JSpinner) (e.getSource());
            getTextField().setText(getPositionAsString((Long) sourceSpinner.getValue()));
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            JSpinner sourceSpinner = getSpinner();

            Object source = e.getSource();
            String name = e.getPropertyName();
            if ((source instanceof JTextField) && "text".equals(name)) {
                Long lastValue = (Long) sourceSpinner.getValue();

                // Try to set the new value
                try {
                    sourceSpinner.setValue(valueOfPosition(getTextField().getText()));
                } catch (IllegalArgumentException iae) {
                    // SpinnerModel didn't like new value, reset
                    try {
                        ((JTextField) source).setText(getPositionAsString(lastValue));
                    } catch (IllegalArgumentException iae2) {
                        // Still bogus, nothing else we can do, the
                        // SpinnerModel and JFormattedTextField are now out
                        // of sync.
                    }
                }
            }
        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        /**
         * Returns the size of the parents insets.
         */
        @Nonnull
        private Dimension insetSize(Container parent) {
            Insets insets = parent.getInsets();
            int width = insets.left + insets.right;
            int height = insets.top + insets.bottom;
            return new Dimension(width, height);
        }

        @Nonnull
        @Override
        public Dimension preferredLayoutSize(Container parent) {
            Dimension preferredSize = insetSize(parent);
            if (parent.getComponentCount() > 0) {
                Dimension childSize = getComponent(0).getPreferredSize();
                preferredSize.width += childSize.width;
                preferredSize.height += childSize.height;
            }
            return preferredSize;
        }

        @Nonnull
        @Override
        public Dimension minimumLayoutSize(Container parent) {
            Dimension minimumSize = insetSize(parent);
            if (parent.getComponentCount() > 0) {
                Dimension childSize = getComponent(0).getMinimumSize();
                minimumSize.width += childSize.width;
                minimumSize.height += childSize.height;
            }
            return minimumSize;
        }

        @Override
        public void layoutContainer(Container parent) {
            if (parent.getComponentCount() > 0) {
                Insets insets = parent.getInsets();
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                getComponent(0).setBounds(insets.left, insets.top, width, height);
            }
        }

        @Nonnull
        public PositionCodeType getPositionCodeType() {
            return positionCodeType;
        }

        public void setPositionCodeType(PositionCodeType positionCodeType) {
            this.positionCodeType = positionCodeType;
        }

        @Nonnull
        private String getPositionAsString(long position) {
            if (position < 0) {
                return "-" + getNonNegativePostionAsString(-position);
            }
            return getNonNegativePostionAsString(position);
        }

        @Nonnull
        private String getNonNegativePostionAsString(long position) {
            Arrays.fill(cache, ' ');
            CodeAreaUtils.longToBaseCode(cache, 0, position, positionCodeType.getBase(), LENGTH_LIMIT, false, CodeCharactersCase.LOWER);
            return new String(cache).trim();
        }

        private long valueOfPosition(String position) {
            return Long.parseLong(position, positionCodeType.getBase());
        }
    }
}