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
package org.exbin.framework.gui.file.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileSystemView;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.preferences.RecentFilesPreferences;
import org.exbin.framework.gui.frame.api.ApplicationFrameHandler;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;

/**
 * Recent files actions.
 *
 * @version 0.2.2 2021/10/07
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class RecentFilesActions {

    private ResourceBundle resourceBundle;
    private XBApplication application;
    private FilesControl filesControl;
    private Preferences preferences;

    private JMenu fileOpenRecentMenu = null;
    private List<RecentItem> recentFiles = null;

    public RecentFilesActions() {
    }

    public void setup(XBApplication application, ResourceBundle resourceBundle, FilesControl filesControl) {
        this.application = application;
        this.filesControl = filesControl;
        this.resourceBundle = resourceBundle;

        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        frameModule.addExitListener((ApplicationFrameHandler frameHandler) -> {
            saveState();
            return true;
        });
    }

    @Nonnull
    public JMenu getOpenRecentMenu() {
        if (fileOpenRecentMenu == null) {
            fileOpenRecentMenu = new JMenu("Open Recent File");
            recentFiles = new ArrayList<>();
            if (preferences != null) {
                loadState();
            }
        }
        return fileOpenRecentMenu;
    }

    private void loadState() {
        RecentFilesPreferences recentFilesParameters = new RecentFilesPreferences(preferences);
        recentFiles.clear();
        int recent = 1;
        while (recent < 14) {
            String filePath = recentFilesParameters.getFilePath(recent).orElse(null);
            String moduleName = recentFilesParameters.getModuleName(recent).orElse(null);
            String fileMode = recentFilesParameters.getFileMode(recent).orElse(null);
            if (filePath == null) {
                break;
            }
            recentFiles.add(new RecentItem(filePath, moduleName, fileMode));
            recent++;
        }
        rebuildRecentFilesMenu();
    }

    private void saveState() {
        RecentFilesPreferences recentFilesParameters = new RecentFilesPreferences(preferences);
        for (int i = 0; i < recentFiles.size(); i++) {
            recentFilesParameters.setFilePath(recentFiles.get(i).getFileName(), i + 1);
            recentFilesParameters.setModuleName(recentFiles.get(i).getModuleName(), i + 1);
            recentFilesParameters.setFileMode(recentFiles.get(i).getFileMode(), i + 1);
        }
        recentFilesParameters.remove(recentFiles.size() + 1);
        preferences.flush();
    }

    private void rebuildRecentFilesMenu() {
        fileOpenRecentMenu.removeAll();
        for (int recentFileIndex = 0; recentFileIndex < recentFiles.size(); recentFileIndex++) {
            String filename = recentFiles.get(recentFileIndex).getFileName();
            File file = new File(filename);
            JMenuItem menuItem = new JMenuItem(file.getName());
            menuItem.setToolTipText(filename);
            {
                URI fileUri;
                try {
                    fileUri = new URI(filename);
                    try {
                        menuItem.setIcon(FileSystemView.getFileSystemView().getSystemIcon(new File(fileUri)));
                    } catch (Exception ex) {
                        menuItem.setIcon(null);
                    }
                } catch (URISyntaxException ex) {
                    Logger.getLogger(RecentFilesActions.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            menuItem.addActionListener((ActionEvent e) -> {
                if (e.getSource() instanceof JMenuItem) {
                    if (!filesControl.releaseFile()) {
                        return;
                    }
                    JMenuItem menuItem1 = (JMenuItem) e.getSource();
                    for (int itemIndex = 0; itemIndex < fileOpenRecentMenu.getItemCount(); itemIndex++) {
                        if (menuItem1.equals(fileOpenRecentMenu.getItem(itemIndex))) {
                            RecentItem recentItem = recentFiles.get(itemIndex);
                            FileType fileType = null;
                            List<FileType> registeredFileTypes = filesControl.getRegisteredFileTypes();
                            for (FileType regFileType : registeredFileTypes) {
                                if (regFileType.getFileTypeId().equals(recentItem.getFileMode())) {
                                    fileType = regFileType;
                                    break;
                                }
                            }

                            URI fileUri;
                            try {
                                fileUri = new URI(recentItem.getFileName());
                                filesControl.loadFromFile(fileUri, fileType);

                                if (itemIndex > 0) {
                                    // Move recent item on top
                                    recentFiles.remove(itemIndex);
                                    recentFiles.add(0, recentItem);
                                    rebuildRecentFilesMenu();
                                }
                            } catch (URISyntaxException ex) {
                                Logger.getLogger(RecentFilesActions.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            });

            fileOpenRecentMenu.add(menuItem);
        }
        fileOpenRecentMenu.setEnabled(recentFiles.size() > 0);
    }

    @Nullable
    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public void updateRecentFilesList(URI fileUri, FileType fileType) {
        if (recentFiles != null) {
            // Update recent files list
            int i = 0;
            while (i < recentFiles.size()) {
                RecentItem recentItem = recentFiles.get(i);
                if (recentItem.getFileName().equals(fileUri.toString())) {
                    recentFiles.remove(i);
                }
                i++;
            }

            recentFiles.add(0, new RecentItem(fileUri.toString(), "", fileType.getFileTypeId()));
            if (recentFiles.size() > 15) {
                recentFiles.remove(15);
            }
            rebuildRecentFilesMenu();
        }
    }

    /**
     * Class for representation of recently opened or saved files.
     */
    public class RecentItem {

        private String fileName;
        private String moduleName;
        private String fileMode;

        public RecentItem(@Nullable String fileName, @Nullable String moduleName, @Nullable String fileMode) {
            this.fileName = fileName;
            this.moduleName = moduleName;
            this.fileMode = fileMode;
        }

        @Nullable
        public String getFileName() {
            return fileName;
        }

        public void setFileName(@Nullable String path) {
            this.fileName = path;
        }

        @Nullable
        public String getFileMode() {
            return fileMode;
        }

        public void setFileMode(@Nullable String fileMode) {
            this.fileMode = fileMode;
        }

        @Nullable
        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(@Nullable String moduleName) {
            this.moduleName = moduleName;
        }
    }

    @ParametersAreNonnullByDefault
    public interface FilesControl {

        boolean releaseFile();

        void loadFromFile(URI fileUri, @Nullable FileType fileType);

        @Nonnull
        List<FileType> getRegisteredFileTypes();
    }
}
