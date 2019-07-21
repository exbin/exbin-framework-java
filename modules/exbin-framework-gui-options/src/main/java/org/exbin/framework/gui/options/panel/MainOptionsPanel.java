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
package org.exbin.framework.gui.options.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.UIManager;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.options.api.OptionsCapable;
import org.exbin.framework.gui.options.api.OptionsModifiedListener;
import org.exbin.framework.gui.options.options.FrameworkOptions;

/**
 * Main options panel.
 *
 * @version 0.2.1 2019/07/20
 * @author ExBin Project (http://exbin.org)
 */
public class MainOptionsPanel extends javax.swing.JPanel implements OptionsCapable<FrameworkOptions> {

    private java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(MainOptionsPanel.class);
    private OptionsModifiedListener optionsModifiedListener;
    private OptionsCapable extendedOptionsPanel = null;

    private DefaultComboBoxModel<String> themesComboBoxModel;
    private DefaultComboBoxModel<Locale> languageComboBoxModel;
    private final DefaultListCellRenderer languageComboBoxCellRenderer;

    private List<String> themes;
    private List<String> themeNames;
    private List<Locale> languageLocales = null;

    public MainOptionsPanel() {
        themesComboBoxModel = new DefaultComboBoxModel<>();
        languageComboBoxModel = new DefaultComboBoxModel<>();
        languageComboBoxCellRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                DefaultListCellRenderer retValue = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Locale) {
                    if (value.equals(Locale.ROOT)) {
                        retValue.setText("<" + resourceBundle.getString("defaultLanguage") + ">");
                    } else {
                        retValue.setText(((Locale) value).getDisplayLanguage((Locale) value));
                    }
                }
                return retValue;
            }
        };

        initComponents();
        init();
    }

    private void init() {
        themes = new ArrayList<>();
        themes.add("");
        boolean extraCrossPlatformLAF = !"javax.swing.plaf.metal.MetalLookAndFeel".equals(UIManager.getCrossPlatformLookAndFeelClassName());
        if (extraCrossPlatformLAF) {
            themes.add(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        themes.add("javax.swing.plaf.metal.MetalLookAndFeel");
        themes.add("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        themeNames = new ArrayList<>();
        themeNames.add(resourceBundle.getString("defaultTheme"));
        if (extraCrossPlatformLAF) {
            themeNames.add(resourceBundle.getString("crossPlatformTheme"));
        }
        themeNames.add("Metal");
        themeNames.add("Motif");
        UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo lookAndFeelInfo : infos) {
            if (!themes.contains(lookAndFeelInfo.getClassName())) {
                themes.add(lookAndFeelInfo.getClassName());
                themeNames.add(lookAndFeelInfo.getName());
            }
        }

        themeNames.forEach((themeName) -> {
            themesComboBoxModel.addElement(themeName);
        });

        languageLocales = new ArrayList<>();
        languageLocales.add(Locale.ROOT);
        languageLocales.add(new Locale("en", "US"));
        languageLocales.forEach((language) -> {
            languageComboBoxModel.addElement(language);
        });
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Override
    public void loadFromOptions(FrameworkOptions options) {
        Locale languageLocale = options.getLanguageLocale();
        languageComboBox.setSelectedItem(languageLocale);

        String laf = options.getLookAndFeel();
        themeComboBox.setSelectedIndex(themes.indexOf(laf));
    }

    @Override
    public void saveToOptions(FrameworkOptions options) {
        options.setLookAndFeel(themes.get(themeComboBox.getSelectedIndex()));
        Locale languageLocale = (Locale) languageComboBox.getSelectedItem();
        options.setLanguageLocale(languageLocale);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainOptionsBasicPanel = new javax.swing.JPanel();
        visualThemeLabel = new javax.swing.JLabel();
        themeComboBox = new javax.swing.JComboBox<>();
        languageComboBox = new javax.swing.JComboBox<>();
        languageLabel = new javax.swing.JLabel();
        mainOptionsNotePanel = new javax.swing.JPanel();
        requireRestartLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        visualThemeLabel.setText(resourceBundle.getString("visualThemeLabel.text")); // NOI18N

        themeComboBox.setModel(themesComboBoxModel);
        themeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                themeComboBoxjComboBoxItemStateChanged(evt);
            }
        });

        languageComboBox.setModel(languageComboBoxModel);
        languageComboBox.setRenderer(languageComboBoxCellRenderer);
        languageComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                languageComboBoxjComboBoxItemStateChanged(evt);
            }
        });

        languageLabel.setText(resourceBundle.getString("languageLabel.text")); // NOI18N

        javax.swing.GroupLayout mainOptionsBasicPanelLayout = new javax.swing.GroupLayout(mainOptionsBasicPanel);
        mainOptionsBasicPanel.setLayout(mainOptionsBasicPanelLayout);
        mainOptionsBasicPanelLayout.setHorizontalGroup(
            mainOptionsBasicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainOptionsBasicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainOptionsBasicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(themeComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 376, Short.MAX_VALUE)
                    .addComponent(visualThemeLabel)
                    .addComponent(languageLabel)
                    .addComponent(languageComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 376, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainOptionsBasicPanelLayout.setVerticalGroup(
            mainOptionsBasicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainOptionsBasicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(visualThemeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(themeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(languageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(languageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(mainOptionsBasicPanel, java.awt.BorderLayout.NORTH);

        requireRestartLabel.setText(resourceBundle.getString("requireRestartLabel.text")); // NOI18N

        javax.swing.GroupLayout mainOptionsNotePanelLayout = new javax.swing.GroupLayout(mainOptionsNotePanel);
        mainOptionsNotePanel.setLayout(mainOptionsNotePanelLayout);
        mainOptionsNotePanelLayout.setHorizontalGroup(
            mainOptionsNotePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainOptionsNotePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(requireRestartLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainOptionsNotePanelLayout.setVerticalGroup(
            mainOptionsNotePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainOptionsNotePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(requireRestartLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(mainOptionsNotePanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void themeComboBoxjComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_themeComboBoxjComboBoxItemStateChanged
        setModified(true);
    }//GEN-LAST:event_themeComboBoxjComboBoxItemStateChanged

    private void languageComboBoxjComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_languageComboBoxjComboBoxItemStateChanged
        setModified(true);
    }//GEN-LAST:event_languageComboBoxjComboBoxItemStateChanged

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new MainOptionsPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Locale> languageComboBox;
    private javax.swing.JLabel languageLabel;
    private javax.swing.JPanel mainOptionsBasicPanel;
    private javax.swing.JPanel mainOptionsNotePanel;
    private javax.swing.JLabel requireRestartLabel;
    private javax.swing.JComboBox<String> themeComboBox;
    private javax.swing.JLabel visualThemeLabel;
    // End of variables declaration//GEN-END:variables

    private void setModified(boolean modified) {
        if (optionsModifiedListener != null) {
            optionsModifiedListener.wasModified();
        }
    }

    @Override
    public void setOptionsModifiedListener(OptionsModifiedListener listener) {
        optionsModifiedListener = listener;
    }

    public void addExtendedPanel(OptionsCapable<?> optionsPanel) {
        if (extendedOptionsPanel != null) {
            remove((Component) extendedOptionsPanel);
        }
        extendedOptionsPanel = optionsPanel;
        add((Component) extendedOptionsPanel, BorderLayout.CENTER);
        extendedOptionsPanel.setOptionsModifiedListener(optionsModifiedListener);
    }

    public void setLanguageLocales(Collection<Locale> locales) {
        languageLocales.clear();
        languageLocales.add(Locale.ROOT);
        languageLocales.add(new Locale("en", "US"));
        languageLocales.addAll(locales);
        languageComboBoxModel.removeAllElements();
        languageLocales.forEach((language) -> {
            languageComboBoxModel.addElement(language);
        });
        languageComboBox.revalidate();
    }
}
