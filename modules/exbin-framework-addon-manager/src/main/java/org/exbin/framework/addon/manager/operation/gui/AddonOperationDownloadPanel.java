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
package org.exbin.framework.addon.manager.operation.gui;

import java.awt.Component;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import org.exbin.framework.App;
import org.exbin.framework.addon.manager.operation.model.DownloadItemRecord;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;

/**
 * Addons operation file downloads panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonOperationDownloadPanel extends javax.swing.JPanel {

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonOperationDownloadPanel.class);
    private Controller controller;
    private ListModel listModel = new ListModel();

    public AddonOperationDownloadPanel() {
        initComponents();
        init();
    }

    private void init() {
        downloadItemsList.setCellRenderer(new DefaultListCellRenderer() {

            private final ImageIcon uncheckedIcon = new ImageIcon(getClass().getResource(resourceBundle.getString("downloadItem.unchecked.icon")));
            private final ImageIcon checkedIcon = new ImageIcon(getClass().getResource(resourceBundle.getString("downloadItem.checked.icon")));
            private final ImageIcon inprogressIcon = new ImageIcon(getClass().getResource(resourceBundle.getString("downloadItem.inprogress.icon")));
            private final ImageIcon doneIcon = new ImageIcon(getClass().getResource(resourceBundle.getString("downloadItem.done.icon")));

            @Nonnull
            @Override
            public Component getListCellRendererComponent(JList<?> list, @Nullable Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof DownloadItemRecord) {
                    DownloadItemRecord record = (DownloadItemRecord) value;
                    Component component = super.getListCellRendererComponent(list, record.getDescription(), index, isSelected, cellHasFocus);
                    switch (record.getStatus()) {
                        case UNCHECKED:
                            setIcon(uncheckedIcon);
                            break;
                        case CHECKED:
                            setIcon(checkedIcon);
                            break;
                        case INPROGRESS:
                            setIcon(inprogressIcon);
                            break;
                        case DONE:
                            setIcon(doneIcon);
                            break;
                        default:
                            throw new AssertionError();
                    }
                    return component;
                }
                return super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
            }
        });
        downloadItemsList.setModel(listModel);
    }

    public void setDownloadedItemRecords(List<DownloadItemRecord> records) {
        listModel.removeAllElements();
        for (DownloadItemRecord record : records) {
            listModel.addElement(record);
        }
    }

    public void notifyDownloadedItemChanged(int recordIndex) {
        listModel.rowChanged(recordIndex);
        if (recordIndex == listModel.getSize() - 1) {
            DownloadItemRecord record = listModel.get(recordIndex);
            if (record.getStatus() == DownloadItemRecord.Status.DONE) {
                downloadProgressBar.setString("Download finished");
            }
        }
    }

    public void setProgress(String fileName, int progress, boolean indeterminate) {
        downloadProgressBar.setString("Downloading " + fileName + " (" + (progress / 10f) + " %)");
        downloadProgressBar.setValue(progress);
        downloadProgressBar.setIndeterminate(indeterminate);
        downloadProgressBar.repaint();
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        downloadItemsLabel = new javax.swing.JLabel();
        downloadItemsScrollPane = new javax.swing.JScrollPane();
        downloadItemsList = new javax.swing.JList<>();
        overallStatusPanel = new javax.swing.JPanel();
        downloadProgressBar = new javax.swing.JProgressBar();

        downloadItemsLabel.setText(resourceBundle.getString("downloadItemsLabel.text")); // NOI18N

        downloadItemsScrollPane.setViewportView(downloadItemsList);

        downloadProgressBar.setMaximum(1000);
        downloadProgressBar.setStringPainted(true);

        javax.swing.GroupLayout overallStatusPanelLayout = new javax.swing.GroupLayout(overallStatusPanel);
        overallStatusPanel.setLayout(overallStatusPanelLayout);
        overallStatusPanelLayout.setHorizontalGroup(
            overallStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overallStatusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(downloadProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        overallStatusPanelLayout.setVerticalGroup(
            overallStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, overallStatusPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(downloadProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(overallStatusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(downloadItemsScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(downloadItemsLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(downloadItemsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downloadItemsScrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(overallStatusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            WindowUtils.invokeWindow(new AddonOperationDownloadPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel downloadItemsLabel;
    private javax.swing.JList<DownloadItemRecord> downloadItemsList;
    private javax.swing.JScrollPane downloadItemsScrollPane;
    private javax.swing.JProgressBar downloadProgressBar;
    private javax.swing.JPanel overallStatusPanel;
    // End of variables declaration//GEN-END:variables

    public interface Controller {

    }

    private static class ListModel extends DefaultListModel<DownloadItemRecord> {

        void rowChanged(int rowIndex) {
            fireContentsChanged(this, rowIndex, rowIndex);
        }
    }
}
