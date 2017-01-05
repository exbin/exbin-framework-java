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
package org.exbin.framework.gui.file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.file.api.FileHandlerApi;
import org.exbin.framework.gui.file.api.FileHandlingActionsApi;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.api.MultiFileHandlerApi;
import org.exbin.framework.gui.frame.api.ApplicationExitListener;
import org.exbin.framework.gui.frame.api.ApplicationFrameHandler;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * File handling operations.
 *
 * @version 0.2.0 2017/01/05
 * @author ExBin Project (http://exbin.org)
 */
public class FileHandlingActions implements FileHandlingActionsApi {

    public static final String PREFERENCES_RECENTFILE_PATH_PREFIX = "recentFile.path.";
    public static final String PREFEFRENCES_RECENTFILE_MODULE_PREFIX = "recentFile.module.";
    public static final String PREFERENCES_RECENFILE_MODE_PREFIX = "recentFile.mode.";

    public static final String ALL_FILES_FILTER = "AllFilesFilter";

    private final ResourceBundle resourceBundle;
    private Preferences preferences;
    private int metaMask;

    private Action newFileAction;
    private Action openFileAction;
    private Action saveFileAction;
    private Action saveAsFileAction;
    private Action closeFileAction;

    private JMenu fileOpenRecentMenu = null;
    private List<RecentItem> recentFiles = null;

    private JFileChooser openFileChooser, saveFileChooser;
    private AllFilesFilter allFilesFilter;

    private FileHandlerApi fileHandler = null;
    private final Map<String, FileType> fileTypes = new HashMap<>();
    private XBApplication application;

    public FileHandlingActions() {
        resourceBundle = LanguageUtils.getResourceBundleByClass(GuiFileModule.class);
    }

    public void init(XBApplication application) {
        this.application = application;

        metaMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        openFileChooser = new JFileChooser();
        openFileChooser.setAcceptAllFileFilterUsed(false);
        saveFileChooser = new JFileChooser();
        saveFileChooser.setAcceptAllFileFilterUsed(false);

        newFileAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionFileNew();
            }
        };
        ActionUtils.setupAction(newFileAction, resourceBundle, "fileNewAction");
        newFileAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, metaMask));

        openFileAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionFileOpen();
            }
        };
        ActionUtils.setupAction(openFileAction, resourceBundle, "fileOpenAction");
        openFileAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, metaMask));
        openFileAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        saveFileAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionFileSave();
            }
        };
        ActionUtils.setupAction(saveFileAction, resourceBundle, "fileSaveAction");
        saveFileAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, metaMask));

        saveAsFileAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                actionFileSaveAs();
            }
        };
        ActionUtils.setupAction(saveAsFileAction, resourceBundle, "fileSaveAsAction");
        saveAsFileAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, metaMask));
        saveAsFileAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        closeFileAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                actionFileClose();
            }
        };
        ActionUtils.setupAction(closeFileAction, resourceBundle, "fileCloseAction");
        closeFileAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, metaMask));
        closeFileAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        AllFilesFilter filesFilter = new AllFilesFilter();
        addFileType(filesFilter);
        allFilesFilter = filesFilter;

        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        frameModule.addExitListener(new ApplicationExitListener() {
            @Override
            public boolean processExit(ApplicationFrameHandler frameHandler) {
                saveState();
                return true;
            }
        });
    }

    /**
     * Attempts to release current file and warn if document was modified.
     *
     * @return true if successfull
     */
    @Override
    public boolean releaseFile() {

        if (fileHandler == null) {
            return true;
        }

        while (fileHandler.isModified()) {
            Object[] options = {
                resourceBundle.getString("Question.modified_save"),
                resourceBundle.getString("Question.modified_discard"),
                resourceBundle.getString("Question.modified_cancel")
            };
            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
            int result = JOptionPane.showOptionDialog(frameModule.getFrame(),
                    resourceBundle.getString("Question.modified"),
                    resourceBundle.getString("Question.modified_title"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            if (result == JOptionPane.NO_OPTION) {
                return true;
            }
            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                return false;
            }

            actionFileSave();
        }

        return true;
    }

    /**
     * Asks whether it's allowed to overwrite file.
     *
     * @return true if allowed
     */
    private boolean overwriteFile() {
        Object[] options = {
            resourceBundle.getString("Question.overwrite_save"),
            resourceBundle.getString("Question.modified_cancel")
        };

        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        int result = JOptionPane.showOptionDialog(
                frameModule.getFrame(),
                resourceBundle.getString("Question.overwrite"),
                resourceBundle.getString("Question.overwrite_title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (result == JOptionPane.YES_OPTION) {
            return true;
        }
        if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION) {
            return false;
        }

        return false;
    }

    @Override
    public Action getNewFileAction() {
        return newFileAction;
    }

    @Override
    public Action getOpenFileAction() {
        return openFileAction;
    }

    @Override
    public Action getSaveFileAction() {
        return saveFileAction;
    }

    @Override
    public Action getSaveAsFileAction() {
        return saveAsFileAction;
    }

    public void actionFileNew() {
        if (fileHandler != null) {
            if (!(fileHandler instanceof MultiFileHandlerApi) && !releaseFile()) {
                return;
            }

            fileHandler.newFile();
        }
    }

    public void actionFileOpen() {
        if (fileHandler != null) {
            if (!(fileHandler instanceof MultiFileHandlerApi) && !releaseFile()) {
                return;
            }

            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
            if (openFileChooser.showOpenDialog(frameModule.getFrame()) == JFileChooser.APPROVE_OPTION) {
//                ((CardLayout) statusPanel.getLayout()).show(statusPanel, "busy");
//                statusPanel.repaint();
                URI fileUri = openFileChooser.getSelectedFile().toURI();

                FileType fileType = null;
                FileFilter fileFilter = openFileChooser.getFileFilter();
                if (fileFilter instanceof FileType) {
                    fileType = fileTypes.get(((FileType) fileFilter).getFileTypeId());
                }
                if (fileType == null) {
                    fileType = fileTypes.get("XBTextEditor.TXTFileType"); // ALL_FILES_FILTER
                }
                fileHandler.loadFromFile(fileUri, fileType);

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

                    recentFiles.add(0, new RecentItem(fileUri.toString(), "", ((FileType) openFileChooser.getFileFilter()).getFileTypeId()));
                    if (recentFiles.size() > 15) {
                        recentFiles.remove(15);
                    }
                    rebuildRecentFilesMenu();
                }
            }
        }
    }

    public void actionFileSave() {
        if (fileHandler != null) {
            URI fileUri = fileHandler.getFileUri();
            if (fileUri == null) {
                actionFileSaveAs();
            } else {
                fileHandler.saveToFile(fileUri, fileHandler.getFileType());
            }
        }
    }

    public void actionFileSaveAs() {
        if (fileHandler != null) {
            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
            if (saveFileChooser.showSaveDialog(frameModule.getFrame()) == JFileChooser.APPROVE_OPTION) {
                if (new File(saveFileChooser.getSelectedFile().getAbsolutePath()).exists()) {
                    if (!overwriteFile()) {
                        return;
                    }
                }

                try {
                    URI fileUri = saveFileChooser.getSelectedFile().toURI();
                    fileHandler.saveToFile(fileUri, (FileType) saveFileChooser.getFileFilter());
                } catch (Exception ex) {
                    Logger.getLogger(FileHandlingActions.class.getName()).log(Level.SEVERE, null, ex);
                    String errorMessage = ex.getLocalizedMessage();
                    JOptionPane.showMessageDialog(frameModule.getFrame(), "Unable to save file: " + ex.getClass().getCanonicalName() + (errorMessage == null || errorMessage.isEmpty() ? "" : errorMessage), "Unable to save file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void actionFileClose() {
        if (releaseFile()) {
            ((MultiFileHandlerApi) fileHandler).closeFile();
        }
    }

    void addFileType(FileType fileType) {
        if (allFilesFilter != null) {
            openFileChooser.removeChoosableFileFilter(allFilesFilter);
            saveFileChooser.removeChoosableFileFilter(allFilesFilter);
        }
        openFileChooser.addChoosableFileFilter((FileFilter) fileType);
        saveFileChooser.addChoosableFileFilter((FileFilter) fileType);
        fileTypes.put(fileType.getFileTypeId(), fileType);

        if (allFilesFilter != null) {
            openFileChooser.addChoosableFileFilter(allFilesFilter);
            saveFileChooser.addChoosableFileFilter(allFilesFilter);
        }
    }

    void loadFromFile(String filename) throws URISyntaxException {
        URI fileUri = new URI(filename);
        fileHandler.loadFromFile(fileUri, null);
    }

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
        recentFiles.clear();
        int recent = 1;
        while (recent < 14) {
            String filePath = preferences.get(PREFERENCES_RECENTFILE_PATH_PREFIX + String.valueOf(recent), null);
            String moduleName = preferences.get(PREFEFRENCES_RECENTFILE_MODULE_PREFIX + String.valueOf(recent), null);
            String fileMode = preferences.get(PREFERENCES_RECENFILE_MODE_PREFIX + String.valueOf(recent), null);
            if (filePath == null) {
                break;
            }
            recentFiles.add(new RecentItem(filePath, moduleName, fileMode));
            recent++;
        }
        rebuildRecentFilesMenu();
    }

    private void saveState() {
        for (int i = 0; i < recentFiles.size(); i++) {
            preferences.put(PREFERENCES_RECENTFILE_PATH_PREFIX + String.valueOf(i + 1), recentFiles.get(i).getFileName());
            preferences.put(PREFEFRENCES_RECENTFILE_MODULE_PREFIX + String.valueOf(i + 1), recentFiles.get(i).getModuleName());
            preferences.put(PREFERENCES_RECENFILE_MODE_PREFIX + String.valueOf(i + 1), recentFiles.get(i).getFileMode());
        }
        preferences.remove(PREFERENCES_RECENTFILE_PATH_PREFIX + String.valueOf(recentFiles.size() + 1));
        try {
            preferences.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(FileHandlingActions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void rebuildRecentFilesMenu() {
        fileOpenRecentMenu.removeAll();
        for (int recentFileIndex = 0; recentFileIndex < recentFiles.size(); recentFileIndex++) {
            File file = new File(recentFiles.get(recentFileIndex).getFileName());
            JMenuItem menuItem = new JMenuItem(file.getName());
            menuItem.setToolTipText(recentFiles.get(recentFileIndex).getFileName());
            menuItem.setIcon(FileSystemView.getFileSystemView().getSystemIcon(file));
            menuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() instanceof JMenuItem) {
                        if (!releaseFile()) {
                            return;
                        }

                        JMenuItem menuItem = (JMenuItem) e.getSource();
                        for (int itemIndex = 0; itemIndex < fileOpenRecentMenu.getItemCount(); itemIndex++) {
                            if (menuItem.equals(fileOpenRecentMenu.getItem(itemIndex))) {
                                RecentItem recentItem = recentFiles.get(itemIndex);
                                FileType fileType = null;
                                for (FileFilter fileFilter : openFileChooser.getChoosableFileFilters()) {
                                    if (fileFilter instanceof FileType) {
                                        if (((FileType) fileFilter).getFileTypeId().equals(recentItem.getFileMode())) {
                                            fileType = (FileType) fileFilter;
                                        }
                                    }
                                }

                                URI fileUri;
                                try {
                                    fileUri = new URI(recentItem.getFileName());
                                    fileHandler.loadFromFile(fileUri, fileType);

                                    if (itemIndex > 0) {
                                        // Move recent item on top
                                        recentFiles.remove(itemIndex);
                                        recentFiles.add(0, recentItem);
                                        rebuildRecentFilesMenu();
                                    }
                                } catch (URISyntaxException ex) {
                                    Logger.getLogger(FileHandlingActions.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
            });

            fileOpenRecentMenu.add(menuItem);
        }
        fileOpenRecentMenu.setEnabled(recentFiles.size() > 0);
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public class AllFilesFilter extends FileFilter implements FileType {

        @Override
        public boolean accept(File f) {
            return true;
        }

        @Override
        public String getDescription() {
            return "All files (*)";
        }

        @Override
        public String getFileTypeId() {
            return ALL_FILES_FILTER;
        }
    }

    @Override
    public FileHandlerApi getFileHandler() {
        return fileHandler;
    }

    @Override
    public void setFileHandler(FileHandlerApi fileHandler) {
        this.fileHandler = fileHandler;
    }

    /**
     * Class for representation of recently opened or saved files.
     */
    public class RecentItem {

        private String fileName;
        private String moduleName;
        private String fileMode;

        public RecentItem(String fileName, String moduleName, String fileMode) {
            this.fileName = fileName;
            this.moduleName = moduleName;
            this.fileMode = fileMode;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String path) {
            this.fileName = path;
        }

        public String getFileMode() {
            return fileMode;
        }

        public void setFileMode(String fileMode) {
            this.fileMode = fileMode;
        }

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }
    }
}
