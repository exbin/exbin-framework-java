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
package org.exbin.framework.gui.about.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.utils.BareBonesBrowserLaunch;
import org.exbin.framework.gui.utils.GuiUtilsModule;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.xbup.plugin.XBModuleRecord;

/**
 * Basic about dialog.
 *
 * @version 0.2.0 2016/11/30
 * @author ExBin Project (http://exbin.org)
 */
public class AboutDialog extends javax.swing.JDialog implements HyperlinkListener {

    private final XBApplication appEditor;
    private ResourceBundle appBundle;
    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(AboutDialog.class);
    private JComponent sideComponent = null;
    private boolean darkMode = false;

    public AboutDialog(java.awt.Frame parent, boolean modal, XBApplication appEditor) {
        super(parent, modal);

        this.appEditor = appEditor;
        if (appEditor != null) {
            appBundle = appEditor.getAppBundle();
        } else {
            appBundle = resourceBundle;
        }

        init();
    }

    private void init() {
        initComponents();
        Color backgroundColor = mainPanel.getBackground();
        int medium = (backgroundColor.getRed() + backgroundColor.getBlue() + backgroundColor.getGreen()) / 3;
        darkMode = medium < 96;
        if (darkMode) {
            aboutHeaderPanel.setBackground(Color.BLACK);
            appTitleLabel.setForeground(Color.WHITE);
            appDescLabel.setForeground(Color.WHITE);
        }

        getRootPane().setDefaultButton(closeButton);
        HashMap<TextAttribute, Object> attribs = new HashMap<>();
        attribs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);

        // Fill system properties tab
        environmentTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    resourceBundle.getString("environmentTable.propertyColumn"), resourceBundle.getString("environmentTable.valueColumn")
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        Properties systemProperties = System.getProperties();
        DefaultTableModel tableModel = (DefaultTableModel) environmentTable.getModel();
        Set<java.util.Map.Entry<Object, Object>> items = systemProperties.entrySet();
        for (java.util.Map.Entry<Object, Object> entry : items) {
            Object[] line = new Object[2];
            line[0] = entry.getKey();
            line[1] = entry.getValue();
            tableModel.addRow(line);
        }

        // Fill list of modules
        modulesTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    resourceBundle.getString("modulesTable.moduleNameColumn"), resourceBundle.getString("modulesTable.moduleDescriptionColumn")
                }
        ) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                false, false
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        if (appEditor.getModuleRepository() != null) {
            DefaultTableModel modulesTableModel = (DefaultTableModel) modulesTable.getModel();
            List<XBModuleRecord> modulesList = appEditor.getModuleRepository().getModulesList();
            for (XBModuleRecord moduleRecord : modulesList) {
                String moduleName;
                if (moduleRecord.getName() == null || moduleRecord.getName().isEmpty()) {
                    moduleName = moduleRecord.getModuleId();
                } else {
                    moduleName = moduleRecord.getName();
                }
                String[] newRow = {moduleName, moduleRecord.getDescription()};
                modulesTableModel.addRow(newRow);
            }
        }

        // Load license
        try {
            String licenseFilePath = appBundle.getString("Application.licenseFile");
            if (licenseFilePath != null && !licenseFilePath.isEmpty()) {
                licenseEditorPane.setPage(getClass().getResource(licenseFilePath));
            }
            licenseEditorPane.addHyperlinkListener(this);
        } catch (IOException ex) {
            Logger.getLogger(AboutDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        WindowUtils.initWindow(this);
        WindowUtils.assignGlobalKeyListener(this, closeButton);
        pack();
    }

    /**
     * Opens hyperlink in external browser.
     *
     * @param event hyperlink event
     */
    @Override
    public void hyperlinkUpdate(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            BareBonesBrowserLaunch.openURL(event.getURL().toExternalForm());
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

        linkPopupMenu = new javax.swing.JPopupMenu();
        copyLinkMenuItem = new javax.swing.JMenuItem();
        mainPanel = new javax.swing.JPanel();
        productTabbedPane = new javax.swing.JTabbedPane();
        applicationPanel = new javax.swing.JPanel();
        javax.swing.JLabel nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        javax.swing.JLabel versionLabel = new javax.swing.JLabel();
        versionTextField = new javax.swing.JTextField();
        javax.swing.JLabel vendorLabel = new javax.swing.JLabel();
        vendorTextField = new javax.swing.JTextField();
        javax.swing.JLabel appLicenseLabel = new javax.swing.JLabel();
        licenseTextField = new javax.swing.JTextField();
        javax.swing.JLabel homepageLabel = new javax.swing.JLabel();
        appHomepageLabel = new javax.swing.JLabel();
        authorsPanel = new javax.swing.JPanel();
        authorsScrollPane = new javax.swing.JScrollPane();
        authorsTextArea = new javax.swing.JTextArea();
        licensePanel = new javax.swing.JPanel();
        licenseScrollPane = new javax.swing.JScrollPane();
        licenseEditorPane = new javax.swing.JEditorPane();
        modulesPanel = new javax.swing.JPanel();
        modulesScrollPane = new javax.swing.JScrollPane();
        modulesTable = new javax.swing.JTable();
        environmentPanel = new javax.swing.JPanel();
        environmentScrollPane = new javax.swing.JScrollPane();
        environmentTable = new javax.swing.JTable();
        aboutHeaderPanel = new javax.swing.JPanel();
        javax.swing.JLabel imageLabel = new javax.swing.JLabel();
        appTitleLabel = new javax.swing.JLabel();
        appDescLabel = new javax.swing.JLabel();
        headerSeparator = new javax.swing.JSeparator();
        controlPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();

        linkPopupMenu.setName("linkPopupMenu"); // NOI18N

        copyLinkMenuItem.setText(resourceBundle.getString("copyLinkMenuItem.text")); // NOI18N
        copyLinkMenuItem.setName("copyLinkMenuItem"); // NOI18N
        copyLinkMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyLinkMenuItemActionPerformed(evt);
            }
        });
        linkPopupMenu.add(copyLinkMenuItem);

        setTitle(resourceBundle.getString("aboutBox.title")); // NOI18N
        setLocationByPlatform(true);

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        productTabbedPane.setMinimumSize(new java.awt.Dimension(38, 15));
        productTabbedPane.setName("productTabbedPane"); // NOI18N

        applicationPanel.setAutoscrolls(true);
        applicationPanel.setName("applicationPanel"); // NOI18N

        nameLabel.setFont(nameLabel.getFont().deriveFont(nameLabel.getFont().getStyle() | java.awt.Font.BOLD));
        nameLabel.setText(resourceBundle.getString("nameLabel.text")); // NOI18N
        nameLabel.setName("nameLabel"); // NOI18N

        nameTextField.setEditable(false);
        nameTextField.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        nameTextField.setText(appBundle.getString("Application.name"));
        nameTextField.setBorder(null);
        nameTextField.setName("nameTextField"); // NOI18N

        versionLabel.setFont(versionLabel.getFont().deriveFont(versionLabel.getFont().getStyle() | java.awt.Font.BOLD));
        versionLabel.setText(resourceBundle.getString("versionLabel.text")); // NOI18N
        versionLabel.setName("versionLabel"); // NOI18N

        versionTextField.setEditable(false);
        versionTextField.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        versionTextField.setText(appBundle.getString("Application.version"));
        versionTextField.setBorder(null);
        versionTextField.setName("versionTextField"); // NOI18N

        vendorLabel.setFont(vendorLabel.getFont().deriveFont(vendorLabel.getFont().getStyle() | java.awt.Font.BOLD));
        vendorLabel.setText(resourceBundle.getString("vendorLabel.text")); // NOI18N
        vendorLabel.setName("vendorLabel"); // NOI18N

        vendorTextField.setEditable(false);
        vendorTextField.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        vendorTextField.setText(appBundle.getString("Application.vendor"));
        vendorTextField.setBorder(null);
        vendorTextField.setName("vendorTextField"); // NOI18N

        appLicenseLabel.setFont(appLicenseLabel.getFont().deriveFont(appLicenseLabel.getFont().getStyle() | java.awt.Font.BOLD));
        appLicenseLabel.setText(resourceBundle.getString("appLicenseLabel.text")); // NOI18N
        appLicenseLabel.setName("appLicenseLabel"); // NOI18N

        licenseTextField.setEditable(false);
        licenseTextField.setFont(new java.awt.Font("Dialog 12", 1, 12)); // NOI18N
        licenseTextField.setText(appBundle.getString("Application.license"));
        licenseTextField.setBorder(null);
        licenseTextField.setName("licenseTextField"); // NOI18N

        homepageLabel.setFont(homepageLabel.getFont().deriveFont(homepageLabel.getFont().getStyle() | java.awt.Font.BOLD));
        homepageLabel.setText(resourceBundle.getString("homepageLabel.text")); // NOI18N
        homepageLabel.setName("homepageLabel"); // NOI18N

        appHomepageLabel.setForeground(java.awt.Color.blue);
        appHomepageLabel.setText(appBundle.getString("Application.homepage"));
        appHomepageLabel.setComponentPopupMenu(linkPopupMenu);
        appHomepageLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        appHomepageLabel.setName("appHomepageLabel"); // NOI18N
        HashMap<TextAttribute, Object> attribs = new HashMap<TextAttribute, Object>();
        attribs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        appHomepageLabel.setFont(appHomepageLabel.getFont().deriveFont(attribs));
        appHomepageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                appHomepageLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout applicationPanelLayout = new javax.swing.GroupLayout(applicationPanel);
        applicationPanel.setLayout(applicationPanelLayout);
        applicationPanelLayout.setHorizontalGroup(
            applicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(applicationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(applicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vendorLabel)
                    .addComponent(homepageLabel)
                    .addComponent(appLicenseLabel)
                    .addComponent(versionLabel)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(applicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                    .addComponent(vendorTextField)
                    .addComponent(licenseTextField)
                    .addComponent(appHomepageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(versionTextField))
                .addContainerGap())
        );
        applicationPanelLayout.setVerticalGroup(
            applicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(applicationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(applicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(applicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(versionLabel)
                    .addComponent(versionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(applicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vendorLabel)
                    .addComponent(vendorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(applicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appLicenseLabel)
                    .addComponent(licenseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(applicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(homepageLabel)
                    .addComponent(appHomepageLabel))
                .addContainerGap(71, Short.MAX_VALUE))
        );

        productTabbedPane.addTab(resourceBundle.getString("applicationPanel.TabConstraints.tabTitle"), applicationPanel); // NOI18N

        authorsPanel.setName("authorsPanel"); // NOI18N

        authorsScrollPane.setName("authorsScrollPane"); // NOI18N

        authorsTextArea.setEditable(false);
        authorsTextArea.setText(appBundle.getString("Application.authors"));
        authorsTextArea.setName("authorsTextArea"); // NOI18N
        authorsScrollPane.setViewportView(authorsTextArea);

        javax.swing.GroupLayout authorsPanelLayout = new javax.swing.GroupLayout(authorsPanel);
        authorsPanel.setLayout(authorsPanelLayout);
        authorsPanelLayout.setHorizontalGroup(
            authorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(authorsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
        );
        authorsPanelLayout.setVerticalGroup(
            authorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(authorsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
        );

        productTabbedPane.addTab(resourceBundle.getString("authorsPanel.TabConstraints.tabTitle"), authorsPanel); // NOI18N

        licensePanel.setName("licensePanel"); // NOI18N

        licenseScrollPane.setName("licenseScrollPane"); // NOI18N

        licenseEditorPane.setEditable(false);
        licenseEditorPane.setContentType("text/html"); // NOI18N
        licenseEditorPane.setText("<html>   <head>    </head>   <body>     <p style=\"margin-top: 0\"></p>   </body> </html> ");
        licenseEditorPane.setName("licenseEditorPane"); // NOI18N
        licenseScrollPane.setViewportView(licenseEditorPane);

        javax.swing.GroupLayout licensePanelLayout = new javax.swing.GroupLayout(licensePanel);
        licensePanel.setLayout(licensePanelLayout);
        licensePanelLayout.setHorizontalGroup(
            licensePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(licenseScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
        );
        licensePanelLayout.setVerticalGroup(
            licensePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(licenseScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
        );

        productTabbedPane.addTab(resourceBundle.getString("licensePanel.TabConstraints.tabTitle"), licensePanel); // NOI18N

        modulesPanel.setEnabled(false);
        modulesPanel.setName("modulesPanel"); // NOI18N

        modulesScrollPane.setName("modulesScrollPane"); // NOI18N
        modulesScrollPane.setViewportView(modulesTable);

        javax.swing.GroupLayout modulesPanelLayout = new javax.swing.GroupLayout(modulesPanel);
        modulesPanel.setLayout(modulesPanelLayout);
        modulesPanelLayout.setHorizontalGroup(
            modulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(modulesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
        );
        modulesPanelLayout.setVerticalGroup(
            modulesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(modulesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
        );

        productTabbedPane.addTab(resourceBundle.getString("modulesPanel.TabConstraints.tabTitle"), modulesPanel); // NOI18N

        environmentPanel.setName("environmentPanel"); // NOI18N

        environmentScrollPane.setName("environmentScrollPane"); // NOI18N

        environmentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Property", "Value"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        environmentScrollPane.setViewportView(environmentTable);

        javax.swing.GroupLayout environmentPanelLayout = new javax.swing.GroupLayout(environmentPanel);
        environmentPanel.setLayout(environmentPanelLayout);
        environmentPanelLayout.setHorizontalGroup(
            environmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(environmentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
        );
        environmentPanelLayout.setVerticalGroup(
            environmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(environmentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
        );

        productTabbedPane.addTab(resourceBundle.getString("environmentPanel.TabConstraints.tabTitle"), environmentPanel); // NOI18N

        mainPanel.add(productTabbedPane, java.awt.BorderLayout.CENTER);

        aboutHeaderPanel.setBackground(new java.awt.Color(255, 255, 255));
        aboutHeaderPanel.setName("aboutHeaderPanel"); // NOI18N

        imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource(appBundle.getString("Application.aboutImage"))));
        imageLabel.setName("imageLabel"); // NOI18N

        appTitleLabel.setFont(appTitleLabel.getFont().deriveFont(appTitleLabel.getFont().getStyle() | java.awt.Font.BOLD, appTitleLabel.getFont().getSize()+4));
        appTitleLabel.setForeground(java.awt.Color.black);
        appTitleLabel.setText(appBundle.getString("Application.title"));
        appTitleLabel.setName("appTitleLabel"); // NOI18N

        appDescLabel.setForeground(java.awt.Color.black);
        appDescLabel.setText(appBundle.getString("Application.description"));
        appDescLabel.setName("appDescLabel"); // NOI18N

        headerSeparator.setName("headerSeparator"); // NOI18N

        javax.swing.GroupLayout aboutHeaderPanelLayout = new javax.swing.GroupLayout(aboutHeaderPanel);
        aboutHeaderPanel.setLayout(aboutHeaderPanelLayout);
        aboutHeaderPanelLayout.setHorizontalGroup(
            aboutHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutHeaderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(aboutHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(appDescLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(appTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(headerSeparator)
        );
        aboutHeaderPanelLayout.setVerticalGroup(
            aboutHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutHeaderPanelLayout.createSequentialGroup()
                .addGroup(aboutHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(aboutHeaderPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(appTitleLabel)
                        .addGap(7, 7, 7)
                        .addComponent(appDescLabel))
                    .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(headerSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(aboutHeaderPanel, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        controlPanel.setName("controlPanel"); // NOI18N

        closeButton.setText(resourceBundle.getString("closeButton.text")); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controlPanelLayout = new javax.swing.GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controlPanelLayout.createSequentialGroup()
                .addContainerGap(340, Short.MAX_VALUE)
                .addComponent(closeButton)
                .addContainerGap())
        );
        controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controlPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton)
                .addContainerGap())
        );

        getContentPane().add(controlPanel, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void appHomepageLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appHomepageLabelMouseClicked
        if (!evt.isPopupTrigger()) {
            String targetURL = ((JLabel) evt.getSource()).getText();
            BareBonesBrowserLaunch.openDesktopURL(targetURL);
        }
    }//GEN-LAST:event_appHomepageLabelMouseClicked

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        productTabbedPane.setSelectedIndex(0);
        WindowUtils.closeWindow(this);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void copyLinkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyLinkMenuItemActionPerformed
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(appHomepageLabel.getText()), null);
    }//GEN-LAST:event_copyLinkMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeWindow(new AboutDialog(new javax.swing.JFrame(), true, GuiUtilsModule.getDefaultAppEditor()));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel aboutHeaderPanel;
    private javax.swing.JLabel appDescLabel;
    private javax.swing.JLabel appHomepageLabel;
    private javax.swing.JLabel appTitleLabel;
    private javax.swing.JPanel applicationPanel;
    private javax.swing.JPanel authorsPanel;
    private javax.swing.JScrollPane authorsScrollPane;
    private javax.swing.JTextArea authorsTextArea;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JMenuItem copyLinkMenuItem;
    private javax.swing.JPanel environmentPanel;
    private javax.swing.JScrollPane environmentScrollPane;
    private javax.swing.JTable environmentTable;
    private javax.swing.JSeparator headerSeparator;
    private javax.swing.JEditorPane licenseEditorPane;
    private javax.swing.JPanel licensePanel;
    private javax.swing.JScrollPane licenseScrollPane;
    private javax.swing.JTextField licenseTextField;
    private javax.swing.JPopupMenu linkPopupMenu;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel modulesPanel;
    private javax.swing.JScrollPane modulesScrollPane;
    private javax.swing.JTable modulesTable;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTabbedPane productTabbedPane;
    private javax.swing.JTextField vendorTextField;
    private javax.swing.JTextField versionTextField;
    // End of variables declaration//GEN-END:variables

    public ResourceBundle getProjectResourceBundle() {
        return appBundle;
    }

    public void setProjectResourceBundle(ResourceBundle projectResourceBundle) {
        this.appBundle = projectResourceBundle;
    }

    public void setSideComponent(JComponent sideComponent) {
        if (this.sideComponent != null) {
            remove(this.sideComponent);
        }

        if (sideComponent != null) {
            add(sideComponent, BorderLayout.WEST);
            this.sideComponent = sideComponent;
            pack();
            setLocationByPlatform(true);
        }
    }
}
