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
package org.exbin.framework.gui.update.options.panel;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.gui.update.preferences.CheckForUpdateParameters;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.options.api.OptionsCapable;
import org.exbin.framework.gui.options.api.OptionsModifiedListener;
import org.exbin.framework.gui.utils.WindowUtils;

/**
 * Application update options panel.
 *
 * @version 0.2.1 2018/06/09
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ApplicationUpdateOptionsPanel extends javax.swing.JPanel implements OptionsCapable {

    private OptionsModifiedListener optionsModifiedListener;
    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(ApplicationUpdateOptionsPanel.class);

    public ApplicationUpdateOptionsPanel() {
        initComponents();
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        checkForUpdatesOnStartCheckBox = new javax.swing.JCheckBox();

        setName("Form"); // NOI18N

        checkForUpdatesOnStartCheckBox.setSelected(true);
        checkForUpdatesOnStartCheckBox.setText(resourceBundle.getString("checkForUpdatesOnStartCheckBox.text")); // NOI18N
        checkForUpdatesOnStartCheckBox.setName("checkForUpdatesOnStartCheckBox"); // NOI18N
        checkForUpdatesOnStartCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkForUpdatesOnStartCheckBoxjCheckBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkForUpdatesOnStartCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkForUpdatesOnStartCheckBox))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void checkForUpdatesOnStartCheckBoxjCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkForUpdatesOnStartCheckBoxjCheckBoxItemStateChanged
        setModified(true);
    }//GEN-LAST:event_checkForUpdatesOnStartCheckBoxjCheckBoxItemStateChanged

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new ApplicationUpdateOptionsPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkForUpdatesOnStartCheckBox;
    // End of variables declaration//GEN-END:variables

    @Override
    public void loadFromPreferences(Preferences preferences) {
        CheckForUpdateParameters checkForUpdateParameters = new CheckForUpdateParameters(preferences);
        checkForUpdatesOnStartCheckBox.setSelected(checkForUpdateParameters.isShouldCheckForUpdate());
    }

    @Override
    public void saveToPreferences(Preferences preferences) {
        CheckForUpdateParameters checkForUpdateParameters = new CheckForUpdateParameters(preferences);
        checkForUpdateParameters.setShouldCheckForUpdate(checkForUpdatesOnStartCheckBox.isSelected());
    }

    @Override
    public void applyPreferencesChanges() {
    }

    private void setModified(boolean b) {
        if (optionsModifiedListener != null) {
            optionsModifiedListener.wasModified();
        }
    }

    @Override
    public void setOptionsModifiedListener(OptionsModifiedListener listener) {
        optionsModifiedListener = listener;
    }
}