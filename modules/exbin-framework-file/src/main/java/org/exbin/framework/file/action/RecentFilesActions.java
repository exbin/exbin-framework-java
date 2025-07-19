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
package org.exbin.framework.file.action;

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
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileSystemView;
import org.exbin.framework.App;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.options.RecentFilesOptions;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.utils.UiUtils;

/**
 * Recent files actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class RecentFilesActions {

    private ResourceBundle resourceBundle;
    private FilesController filesController;
    private OptionsStorage preferences;

    private List<RecentItem> recentFiles = null;

    public RecentFilesActions() {
    }

    public void init(ResourceBundle resourceBundle, FilesController filesController) {
        this.filesController = filesController;
        this.resourceBundle = resourceBundle;

        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        frameModule.addExitListener((ApplicationFrameHandler frameHandler) -> {
            saveState();
            return true;
        });
    }

    @Nonnull
    public JMenu getOpenRecentMenu() {
        Action fileOpenRecentAction = new AbstractAction(resourceBundle.getString("openRecentMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        fileOpenRecentAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("openRecentMenu.shortDescription"));
        JMenu fileOpenRecentMenu = UiUtils.createMenu();
        fileOpenRecentMenu.setAction(fileOpenRecentAction);
        recentFiles = new ArrayList<>();
        if (preferences != null) {
            loadState(fileOpenRecentMenu);
        }
        return fileOpenRecentMenu;
    }

    private void loadState(JMenu fileOpenRecentMenu) {
        RecentFilesOptions recentFilesOptions = new RecentFilesOptions(preferences);
        recentFiles.clear();
        int recent = 1;
        while (recent < 14) {
            String filePath = recentFilesOptions.getFilePath(recent).orElse(null);
            String moduleName = recentFilesOptions.getModuleName(recent).orElse(null);
            String fileMode = recentFilesOptions.getFileMode(recent).orElse(null);
            if (filePath == null) {
                break;
            }
            recentFiles.add(new RecentItem(filePath, moduleName, fileMode));
            recent++;
        }
        rebuildRecentFilesMenu(fileOpenRecentMenu);
    }

    private void saveState() {
        if (recentFiles == null) {
            return;
        }

        RecentFilesOptions recentFilesParameters = new RecentFilesOptions(preferences);
        for (int i = 0; i < recentFiles.size(); i++) {
            recentFilesParameters.setFilePath(recentFiles.get(i).getFileName(), i + 1);
            recentFilesParameters.setModuleName(recentFiles.get(i).getModuleName(), i + 1);
            recentFilesParameters.setFileMode(recentFiles.get(i).getFileMode(), i + 1);
        }
        recentFilesParameters.remove(recentFiles.size() + 1);
        preferences.flush();
    }

    private void rebuildRecentFilesMenu(JMenu fileOpenRecentMenu) {
        fileOpenRecentMenu.removeAll();
        for (int recentFileIndex = 0; recentFileIndex < recentFiles.size(); recentFileIndex++) {
            String filename = recentFiles.get(recentFileIndex).getFileName();
            File file = new File(filename);
            JMenuItem menuItem = UiUtils.createMenuItem();
            menuItem.setText(file.getName());
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
                    JMenuItem sourceMenuItem = (JMenuItem) e.getSource();
                    for (int itemIndex = 0; itemIndex < fileOpenRecentMenu.getItemCount(); itemIndex++) {
                        if (sourceMenuItem.equals(fileOpenRecentMenu.getItem(itemIndex))) {
                            RecentItem recentItem = recentFiles.get(itemIndex);
                            FileType fileType = null;
                            List<FileType> registeredFileTypes = filesController.getRegisteredFileTypes();
                            for (FileType regFileType : registeredFileTypes) {
                                if (regFileType.getFileTypeId().equals(recentItem.getFileMode())) {
                                    fileType = regFileType;
                                    break;
                                }
                            }

                            URI fileUri;
                            try {
                                fileUri = new URI(recentItem.getFileName());
                                filesController.loadFromFile(fileUri, fileType);

                                if (itemIndex > 0) {
                                    // Move recent item on top
                                    recentFiles.remove(itemIndex);
                                    recentFiles.add(0, recentItem);
                                    rebuildRecentFilesMenu(fileOpenRecentMenu);
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
        fileOpenRecentMenu.setEnabled(!recentFiles.isEmpty());
    }

    @Nullable
    public OptionsStorage getPreferences() {
        return preferences;
    }

    public void setPreferences(OptionsStorage preferences) {
        this.preferences = preferences;
    }

    public void updateRecentFilesList(URI fileUri, @Nullable FileType fileType) {
        if (recentFiles != null) {
            int i = 0;
            while (i < recentFiles.size()) {
                RecentItem recentItem = recentFiles.get(i);
                if (fileUri.toString().equals(recentItem.getFileName())) {
                    recentFiles.remove(i);
                }
                i++;
            }

            recentFiles.add(0, new RecentItem(fileUri.toString(), "", fileType != null ? fileType.getFileTypeId() : null));
            if (recentFiles.size() > 15) {
                recentFiles.remove(15);
            }
            // TODO rebuildRecentFilesMenu();
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
    public interface FilesController {

        void loadFromFile(URI fileUri, @Nullable FileType fileType);

        @Nonnull
        List<FileType> getRegisteredFileTypes();
    }
}
