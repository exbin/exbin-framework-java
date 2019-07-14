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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.ApplicationFrameHandler;
import org.exbin.framework.gui.options.api.OptionsCapable;
import org.exbin.framework.gui.options.api.OptionsModifiedListener;
import org.exbin.framework.gui.options.api.OptionsPathItem;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;

/**
 * Panel for application options and preferences setting.
 *
 * @version 0.2.1 2019/07/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsTreePanel extends javax.swing.JPanel {

    private Preferences preferences = null;
    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(OptionsTreePanel.class);
    private Map<String, JPanel> optionPanels;
    private JPanel currentOptionsPanel = null;
    private OptionsModifiedListener optionsModifiedListener;

    private boolean modified;
    private OptionsMutableTreeNode top;
    private XBApplication appEditor;
    private final ApplicationFrameHandler frame;
    private MainOptionsPanel mainOptionsPanel;
    private AppearanceOptionsPanel appearanceOptionsPanel;

    public OptionsTreePanel(ApplicationFrameHandler frame) {
        initComponents();
        this.frame = frame;
        init();
    }

    private void init() {
        initComponents();

        optionPanels = new HashMap<>();
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
        top = new OptionsMutableTreeNode(resourceBundle.getString("options.root.caption"), "options");
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
                    optionsAreaScrollPane.remove(currentOptionsPanel);
                }
                if (caption != null) {
                    currentOptionsPanel = optionPanels.get(caption);
                    optionsAreaScrollPane.setViewportView(currentOptionsPanel);
                } else {
                    currentOptionsPanel = null;
                    optionsAreaScrollPane.setViewportView(null);
                }
            }
        });

        mainOptionsPanel = new MainOptionsPanel();
        addOptionsPanel(mainOptionsPanel, null);
        appearanceOptionsPanel = new AppearanceOptionsPanel(frame);
        {
            ResourceBundle optionsResourceBundle = appearanceOptionsPanel.getResourceBundle();
            List<OptionsPathItem> optionsPath = new ArrayList<>();
            optionsPath.add(new OptionsPathItem(optionsResourceBundle.getString("options.name"), optionsResourceBundle.getString("options.caption")));
            addOptionsPanel(appearanceOptionsPanel, optionsPath);
        }
        optionsTree.setSelectionRow(0);

        // Expand all nodes
        expandJTree(optionsTree, -1);
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void loadPreferences(Preferences preferences) {
        for (Iterator optionPanelsIterator = optionPanels.values().iterator(); optionPanelsIterator.hasNext();) {
            Object optionPanel = optionPanelsIterator.next();
            if (optionPanel instanceof org.exbin.framework.gui.options.api.OptionsCapable) {
                ((org.exbin.framework.gui.options.api.OptionsCapable) optionPanel).loadFromPreferences(preferences);
            }
        }
    }

    public void savePreferences(Preferences preferences) {
        for (Iterator optionPanelsIterator = optionPanels.values().iterator(); optionPanelsIterator.hasNext();) {
            Object optionPanel = optionPanelsIterator.next();
            if (optionPanel instanceof org.exbin.framework.gui.options.api.OptionsCapable) {
                ((org.exbin.framework.gui.options.api.OptionsCapable) optionPanel).saveToPreferences(preferences);
            }
        }

        preferences.flush();
    }

    public void applyPreferencesChanges() {
        for (Iterator optionPanelsIterator = optionPanels.values().iterator(); optionPanelsIterator.hasNext();) {
            Object optionPanel = optionPanelsIterator.next();
            if (optionPanel instanceof org.exbin.framework.gui.options.api.OptionsCapable) {
                ((org.exbin.framework.gui.options.api.OptionsCapable) optionPanel).applyPreferencesChanges();
            }
        }
    }

    public void saveToPreferences() {
        savePreferences(preferences);
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
        optionsTreeScrollPane = new javax.swing.JScrollPane();
        optionsTree = new javax.swing.JTree();
        optionsTreePanel = new javax.swing.JPanel();
        optionsAreaScrollPane = new javax.swing.JScrollPane();
        optionsAreaTitlePanel = new javax.swing.JPanel();
        optionsAreaTitleLabel = new javax.swing.JLabel();
        separator = new javax.swing.JSeparator();

        setLayout(new java.awt.BorderLayout());

        optionsSplitPane.setDividerLocation(130);

        optionsTreeScrollPane.setViewportView(optionsTree);

        optionsSplitPane.setLeftComponent(optionsTreeScrollPane);

        optionsTreePanel.setLayout(new java.awt.BorderLayout());
        optionsTreePanel.add(optionsAreaScrollPane, java.awt.BorderLayout.CENTER);

        optionsAreaTitlePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        optionsAreaTitlePanel.setLayout(new java.awt.BorderLayout());

        optionsAreaTitleLabel.setBackground(javax.swing.UIManager.getDefaults().getColor("EditorPane.selectionBackground"));
        optionsAreaTitleLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("EditorPane.background"));
        optionsAreaTitleLabel.setText(resourceBundle.getString("optionsAreaTitleLabel.text")); // NOI18N
        optionsAreaTitleLabel.setOpaque(true);
        optionsAreaTitleLabel.setVerifyInputWhenFocusTarget(false);
        optionsAreaTitlePanel.add(optionsAreaTitleLabel, java.awt.BorderLayout.NORTH);

        optionsTreePanel.add(optionsAreaTitlePanel, java.awt.BorderLayout.NORTH);

        optionsSplitPane.setRightComponent(optionsTreePanel);

        add(optionsSplitPane, java.awt.BorderLayout.CENTER);
        add(separator, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new OptionsTreePanel(null));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane optionsAreaScrollPane;
    private javax.swing.JLabel optionsAreaTitleLabel;
    private javax.swing.JPanel optionsAreaTitlePanel;
    private javax.swing.JSplitPane optionsSplitPane;
    private javax.swing.JTree optionsTree;
    private javax.swing.JPanel optionsTreePanel;
    private javax.swing.JScrollPane optionsTreeScrollPane;
    private javax.swing.JSeparator separator;
    // End of variables declaration//GEN-END:variables

    /**
     * @param modified the modified to set
     */
    public void setModified(boolean modified) {
        this.modified = modified;
        // applyButton.setEnabled(modified);
    }

    public void addOptionsPanel(OptionsCapable optionPanel, @Nullable List<OptionsPathItem> path) {
        String panelKey;
        if (path == null) {
            panelKey = "options";
        } else {
            panelKey = path.get(path.size() - 1).getName();
            establishPath(path);
        }

        optionPanels.put(panelKey, (JPanel) optionPanel);
        optionPanel.setOptionsModifiedListener(optionsModifiedListener);
        optionsTree.setSelectionRow(0);
    }

    public void extendMainOptionsPanel(OptionsCapable panel) {
        mainOptionsPanel.addExtendedPanel(panel);
    }

    public void extendAppearanceOptionsPanel(OptionsCapable panel) {
        appearanceOptionsPanel.addExtendedPanel(panel);
    }

    private void establishPath(List<OptionsPathItem> path) {
        OptionsMutableTreeNode node = top;
        for (OptionsPathItem pathItem : path) {
            int childIndex = 0;
            OptionsMutableTreeNode child = null;
            if (node == null) {
                return;
            }

            while ((childIndex >= 0) && (childIndex < node.getChildCount())) {
                child = (OptionsMutableTreeNode) node.getChildAt(childIndex);
                String name = child.getName();
                if (name.equals(pathItem.getName())) {
                    Object caption = child.getUserObject();
                    String newCaption = pathItem.getCaption();
                    if (caption instanceof String && name.equals(caption) && !name.equals(newCaption)) {
                        child.setUserObject(newCaption);
                    }
                    break;
                } else {
                    childIndex++;
                }
            }

            if (childIndex == node.getChildCount()) {
                OptionsMutableTreeNode newNode = new OptionsMutableTreeNode(pathItem.getCaption(), pathItem.getName());
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

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public XBApplication getAppEditor() {
        return appEditor;
    }

    public void setAppEditor(XBApplication appEditor) {
        this.appEditor = appEditor;
    }

    public void setLanguageLocales(Collection<Locale> locales) {
        mainOptionsPanel.setLanguageLocales(locales);
    }

    @ParametersAreNonnullByDefault
    private class OptionsMutableTreeNode extends DefaultMutableTreeNode {

        private final String name;

        public OptionsMutableTreeNode(Object userObject, String name) {
            super(userObject);
            this.name = name;
        }

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

    @Override
    public void setVisible(boolean visibility) {
        if (visibility) {
            loadPreferences(preferences);
        }

        super.setVisible(visibility);
    }
}
