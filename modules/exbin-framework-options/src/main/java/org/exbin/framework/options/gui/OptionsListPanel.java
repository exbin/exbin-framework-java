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
package org.exbin.framework.options.gui;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import org.exbin.framework.App;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsModifiedListener;
import org.exbin.framework.options.api.OptionsPathItem;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.options.api.OptionsPage;
import org.exbin.framework.utils.LazyComponentListener;
import org.exbin.framework.utils.LazyComponentsIssuable;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;
import org.exbin.framework.options.api.OptionsPageReceiver;
import org.exbin.framework.options.api.VisualOptionsPageParams;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Panel for application options and preferences setting.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsListPanel extends javax.swing.JPanel implements OptionsPageReceiver, LazyComponentsIssuable {

    public static final String OPTIONS_PANEL_KEY = "options";

    private OptionsStorage preferences = null;
    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(OptionsListPanel.class);
    private final List<PageRecord> optionPages = new ArrayList<>();
    private final Map<String, Integer> optionPageKeys = new HashMap<>();
    private PageRecord currentOptionsPanel = null;
    private OptionsModifiedListener optionsModifiedListener;
    private final List<LazyComponentListener> listeners = new ArrayList<>();
    private String rootCaption;

    private boolean modified;

    public OptionsListPanel() {
        initComponents();
        init();
    }

    private void init() {
        modified = false;
        optionsModifiedListener = () -> {
            setModified(true);
        };

        addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if ("modified".equals(evt.getPropertyName())) {
                modified = true;
            }
        });

        // Actions on change of look&feel
        UIManager.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            SwingUtilities.updateComponentTreeUI(OptionsListPanel.this);
        });

        // Create menu tree
        categoriesList.setModel(new DefaultListModel<>());
        categoriesList.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            int selectedIndex = categoriesList.getSelectedIndex();

            // optionsAreaTitleLabel.setText(selectedIndex >= 0 ? " " + categoriesList.getModel().getElementAt(selectedIndex) : "");
            if (currentOptionsPanel != null) {
                optionsAreaScrollPane.remove(currentOptionsPanel.getPanel());
            }
            if (selectedIndex >= 0) {
                currentOptionsPanel = optionPages.get(selectedIndex);
                if (currentOptionsPanel != null) {
                    optionsAreaScrollPane.setViewportView(currentOptionsPanel.getPanel());
                } else {
                    optionsAreaScrollPane.setViewportView(null);
                }
            } else {
                currentOptionsPanel = null;
                optionsAreaScrollPane.setViewportView(null);
            }
        });
        rootCaption = resourceBundle.getString("options.root.caption");
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void pagesFinished() {
        categoriesList.setSelectedIndex(0);
    }

    public void setRootCaption(String rootCaption) {
        this.rootCaption = rootCaption;
        DefaultListModel<String> model = ((DefaultListModel<String>) categoriesList.getModel());
        if (!model.isEmpty()) {
            model.remove(0);
            model.add(0, rootCaption);
            categoriesList.setSelectedIndex(0);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        categoryLabel = new javax.swing.JLabel();
        categoriesScrollPane = new javax.swing.JScrollPane();
        categoriesList = new javax.swing.JList<>();
        optionsAreaScrollPane = new javax.swing.JScrollPane();
        separator = new javax.swing.JSeparator();

        categoryLabel.setText(resourceBundle.getString("categoryLabel.text")); // NOI18N

        categoriesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        categoriesScrollPane.setViewportView(categoriesList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(categoriesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(optionsAreaScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 16, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(categoryLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(separator)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(categoryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(optionsAreaScrollPane)
                    .addComponent(categoriesScrollPane))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            WindowUtils.invokeWindow(new OptionsListPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> categoriesList;
    private javax.swing.JScrollPane categoriesScrollPane;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JScrollPane optionsAreaScrollPane;
    private javax.swing.JSeparator separator;
    // End of variables declaration//GEN-END:variables

    /**
     * @param modified the modified to set
     */
    public void setModified(boolean modified) {
        this.modified = modified;
        // applyButton.setEnabled(modified);
    }

    @Override
    public void addOptionsPage(OptionsPage<?> optionPage, @Nullable List<OptionsPathItem> path, @Nullable VisualOptionsPageParams visualParams) {
        String panelKey;
        if (path == null) {
            panelKey = OPTIONS_PANEL_KEY;
        } else {
            panelKey = path.get(path.size() - 1).getGroupId();
        }

        Integer pageRecordIndex = optionPageKeys.get(panelKey);
        if (pageRecordIndex != null) {
            PageRecord pageRecord = optionPages.get(pageRecordIndex);
            pageRecord.addOptionsPage(optionPage, optionsModifiedListener, visualParams);
        } else {
            PageRecord pageRecord = new PageRecord(optionPage, visualParams);
            if (path == null) {
                optionPages.add(0, pageRecord);
                ((DefaultListModel<String>) categoriesList.getModel()).insertElementAt(rootCaption, 0);
                optionPageKeys.put(panelKey, 0);
            } else {
                optionPages.add(pageRecord);
                ((DefaultListModel<String>) categoriesList.getModel()).addElement(path.get(path.size() - 1).getName());
                optionPageKeys.put(panelKey, optionPages.size() - 1);
            }
            pageRecord.setOptionsModifiedListener(optionsModifiedListener);
        }
    }

    public void loadAllFromPreferences() {
        optionPages.forEach((pageRecord) -> {
            try {
                pageRecord.loadFromPreferences(preferences);
            } catch (Exception ex) {
                Logger.getLogger(OptionsListPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void saveAndApplyAll() {
        optionPages.forEach((pageRecord) -> {
            try {
                pageRecord.saveAndApply(preferences);
            } catch (Exception ex) {
                Logger.getLogger(OptionsListPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        optionsModule.notifyOptionsChanged();
    }

    public void applyPreferencesChanges() {
        optionPages.forEach((pageRecord) -> {
            try {
                pageRecord.applyPreferencesChanges(preferences);
            } catch (Exception ex) {
                Logger.getLogger(OptionsListPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        OptionsModuleApi windowModule = App.getModule(OptionsModuleApi.class);
        windowModule.notifyOptionsChanged();
    }

    public void setPreferences(OptionsStorage preferences) {
        this.preferences = preferences;
    }

    @Override
    public void addChildComponentListener(LazyComponentListener listener) {
        listeners.add(listener);
        for (PageRecord pageRecord : optionPages) {
            listener.componentCreated(pageRecord.getPanel());
        }
    }

    @Override
    public void removeChildComponentListener(LazyComponentListener listener) {
        listeners.remove(listener);
    }
}
