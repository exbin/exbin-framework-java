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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.exbin.framework.App;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsModifiedListener;
import org.exbin.framework.options.api.OptionsPathItem;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.OptionsPageReceiver;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.options.api.OptionsPage;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.utils.LazyComponentListener;
import org.exbin.framework.utils.LazyComponentsIssuable;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;

/**
 * Panel for application options and preferences setting.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsTreePanel extends javax.swing.JPanel implements OptionsPageReceiver, LazyComponentsIssuable {

    public static final String OPTIONS_PANEL_KEY = "options";

    private OptionsStorage preferences = null;
    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(OptionsTreePanel.class);
    private final Map<String, PageRecord> optionPages = new HashMap<>();
    private PageRecord currentOptionsPanel = null;
    private OptionsModifiedListener optionsModifiedListener;
    private final List<LazyComponentListener> listeners = new ArrayList<>();

    private boolean modified;
    private OptionsMutableTreeNode top;

    public OptionsTreePanel() {
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
            SwingUtilities.updateComponentTreeUI(OptionsTreePanel.this);
        });

        // Create menu tree
        top = new OptionsMutableTreeNode(resourceBundle.getString("options.root.caption"), OPTIONS_PANEL_KEY);
        optionsTree.setModel(new DefaultTreeModel(top, true));
        optionsTree.getSelectionModel().addTreeSelectionListener((TreeSelectionEvent e) -> {
            if (e.getPath() != null) {
                String caption;
                OptionsMutableTreeNode node = ((OptionsMutableTreeNode) optionsTree.getLastSelectedPathComponent());
                if (node == null) {
                    caption = null;
                    optionsAreaTitleLabel.setText("");
                } else {
                    caption = node.getName();
                    optionsAreaTitleLabel.setText(" " + (String) node.getUserObject());
                }
                if (currentOptionsPanel != null) {
                    optionsAreaScrollPane.remove(currentOptionsPanel.getPanel());
                }
                if (caption != null) {
                    currentOptionsPanel = optionPages.get(caption);
                    if (currentOptionsPanel != null) {
                        optionsAreaScrollPane.setViewportView(currentOptionsPanel.getPanel());
                    } else {
                        optionsAreaScrollPane.setViewportView(null);
                    }
                } else {
                    currentOptionsPanel = null;
                    optionsAreaScrollPane.setViewportView(null);
                }
            }
        });
        optionsTree.setRootVisible(true);
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void pagesFinished() {
        optionsTree.setSelectionRow(0);

        // Expand all nodes
        expandJTree(optionsTree, -1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        optionsSplitPane = new javax.swing.JSplitPane();
        categoriesScrollPane = new javax.swing.JScrollPane();
        optionsTree = new javax.swing.JTree();
        optionsPagePanel = new javax.swing.JPanel();
        optionsAreaScrollPane = new javax.swing.JScrollPane();
        optionsAreaTitlePanel = new javax.swing.JPanel();
        optionsAreaTitleLabel = new javax.swing.JLabel();
        separator = new javax.swing.JSeparator();

        setLayout(new java.awt.BorderLayout());

        optionsSplitPane.setDividerLocation(130);

        categoriesScrollPane.setViewportView(optionsTree);

        optionsSplitPane.setLeftComponent(categoriesScrollPane);

        optionsPagePanel.setLayout(new java.awt.BorderLayout());
        optionsPagePanel.add(optionsAreaScrollPane, java.awt.BorderLayout.CENTER);

        optionsAreaTitlePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        optionsAreaTitlePanel.setLayout(new java.awt.BorderLayout());

        optionsAreaTitleLabel.setBackground(javax.swing.UIManager.getDefaults().getColor("EditorPane.selectionBackground"));
        optionsAreaTitleLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("EditorPane.background"));
        optionsAreaTitleLabel.setText(resourceBundle.getString("optionsAreaTitleLabel.text")); // NOI18N
        optionsAreaTitleLabel.setOpaque(true);
        optionsAreaTitleLabel.setVerifyInputWhenFocusTarget(false);
        optionsAreaTitlePanel.add(optionsAreaTitleLabel, java.awt.BorderLayout.NORTH);

        optionsPagePanel.add(optionsAreaTitlePanel, java.awt.BorderLayout.NORTH);

        optionsSplitPane.setRightComponent(optionsPagePanel);

        add(optionsSplitPane, java.awt.BorderLayout.CENTER);
        add(separator, java.awt.BorderLayout.SOUTH);
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
            WindowUtils.invokeWindow(new OptionsTreePanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane categoriesScrollPane;
    private javax.swing.JScrollPane optionsAreaScrollPane;
    private javax.swing.JLabel optionsAreaTitleLabel;
    private javax.swing.JPanel optionsAreaTitlePanel;
    private javax.swing.JPanel optionsPagePanel;
    private javax.swing.JSplitPane optionsSplitPane;
    private javax.swing.JTree optionsTree;
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
    public void addOptionsPage(OptionsPage<?> optionPage, @Nullable List<OptionsPathItem> path) {
        String panelKey;
        if (path == null) {
            panelKey = OPTIONS_PANEL_KEY;
        } else {
            panelKey = path.get(path.size() - 1).getGroupId();
            establishPath(path);
        }

        PageRecord pageRecord = optionPages.get(panelKey);
        if (pageRecord != null) {
            pageRecord.addOptionsPage(optionPage, optionsModifiedListener);
        } else {
            pageRecord = new PageRecord(optionPage);
            optionPages.put(panelKey, pageRecord);
            pageRecord.setOptionsModifiedListener(optionsModifiedListener);
        }
        optionsTree.setSelectionRow(0);
    }

    public void loadAllFromPreferences() {
        optionPages.values().forEach((pageRecord) -> {
            try {
                pageRecord.loadFromPreferences(preferences);
            } catch (Exception ex) {
                Logger.getLogger(OptionsTreePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void saveAndApplyAll() {
        optionPages.values().forEach((pageRecord) -> {
            try {
                pageRecord.saveAndApply(preferences);
            } catch (Exception ex) {
                Logger.getLogger(OptionsTreePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        optionsModule.notifyOptionsChanged();
    }

    public void applyPreferencesChanges() {
        optionPages.values().forEach((pageRecord) -> {
            try {
                pageRecord.applyPreferencesChanges(preferences);
            } catch (Exception ex) {
                Logger.getLogger(OptionsTreePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        OptionsModuleApi windowModule = App.getModule(OptionsModuleApi.class);
        windowModule.notifyOptionsChanged();
    }

    private void establishPath(List<OptionsPathItem> path) {
        OptionsMutableTreeNode node = top;
        if (path.size() == 1 && OPTIONS_PANEL_KEY.equals(path.get(0).getGroupId())) {
            return;
        }

        for (OptionsPathItem pathItem : path) {
            int childIndex = 0;
            OptionsMutableTreeNode child = null;
            if (node == null) {
                return;
            }

            while ((childIndex >= 0) && (childIndex < node.getChildCount())) {
                child = (OptionsMutableTreeNode) node.getChildAt(childIndex);
                String name = child.getName();
                if (name.equals(pathItem.getGroupId())) {
                    Object caption = child.getUserObject();
                    String newCaption = pathItem.getName();
                    if (caption instanceof String && name.equals(caption) && !name.equals(newCaption)) {
                        child.setUserObject(newCaption);
                    }
                    break;
                } else {
                    childIndex++;
                }
            }

            if (childIndex == node.getChildCount()) {
                OptionsMutableTreeNode newNode = new OptionsMutableTreeNode(pathItem.getName(), pathItem.getGroupId());
                node.add(newNode);
                node = newNode;
            } else {
                node = child;
            }
        }

        optionsTree.setModel(new DefaultTreeModel(top, true));
        for (int i = 0; i < optionsTree.getRowCount(); i++) {
            optionsTree.expandRow(i);
        }
    }

    public void setPreferences(OptionsStorage preferences) {
        this.preferences = preferences;
    }

    @Override
    public void addChildComponentListener(LazyComponentListener listener) {
        listeners.add(listener);
        for (PageRecord pageRecord : optionPages.values()) {
            listener.componentCreated(pageRecord.getPanel());
        }
    }

    @Override
    public void removeChildComponentListener(LazyComponentListener listener) {
        listeners.remove(listener);
    }

    @ParametersAreNonnullByDefault
    private class OptionsMutableTreeNode extends DefaultMutableTreeNode {

        private final String name;

        public OptionsMutableTreeNode(Object userObject, String name) {
            super(userObject);
            this.name = name;
        }

        @Nonnull
        public String getName() {
            return name;
        }
    }

    /**
     * Expands all nodes in a JTree.
     *
     * @param tree The JTree to expand.
     * @param depth The depth to which the tree should be expanded. Zero will
     * just expand the root node, a negative value will fully expand the tree,
     * and a positive value will recursively expand the tree to that depth.
     */
    public static void expandJTree(javax.swing.JTree tree, int depth) {
        javax.swing.tree.TreeModel model = tree.getModel();
        expandJTreeNode(tree, model, model.getRoot(), 0, depth);
    } // expandJTree()

    /**
     * Expands a given node in a JTree.
     *
     * @param tree The JTree to expand.
     * @param model The TreeModel for tree.
     * @param node The node within tree to expand.
     * @param row The displayed row in tree that represents node.
     * @param depth The depth to which the tree should be expanded. Zero will
     * just expand node, a negative value will fully expand the tree, and a
     * positive value will recursively expand the tree to that depth relative to
     * node.
     * @return row
     */
    public static int expandJTreeNode(javax.swing.JTree tree,
            javax.swing.tree.TreeModel model,
            Object node, int row, int depth) {
        if (node != null && !model.isLeaf(node)) {
            tree.expandRow(row);
            if (depth != 0) {
                for (int index = 0;
                        row + 1 < tree.getRowCount()
                        && index < model.getChildCount(node);
                        index++) {
                    row++;
                    Object child = model.getChild(node, index);
                    if (child == null) {
                        break;
                    }
                    javax.swing.tree.TreePath path;
                    while ((path = tree.getPathForRow(row)) != null
                            && path.getLastPathComponent() != child) {
                        row++;
                    }
                    if (path == null) {
                        break;
                    }
                    row = expandJTreeNode(tree, model, child, row, depth - 1);
                }
            }
        }
        return row;
    } // expandJTreeNode()
}
