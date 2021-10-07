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
package org.exbin.framework.gui.file;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.file.api.FileHandlerApi;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.preferences.RecentFilesPreferences;
import org.exbin.framework.gui.frame.api.ApplicationFrameHandler;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.file.api.FileHandlingActionsApi;

/**
 * File handling operations.
 *
 * @version 0.2.0 2019/08/18
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FileHandlingActions implements FileHandlingActionsApi {

    public static final String ALL_FILES_FILTER = "AllFilesFilter";

    private final ResourceBundle resourceBundle;
    private Preferences preferences;

//    private Action newFileAction;
//    private Action openFileAction;
//    private Action saveFileAction;
//    private Action saveAsFileAction;
//    private Action closeFileAction;

    private JMenu fileOpenRecentMenu = null;

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

        int metaMask = ActionUtils.getMetaMask();
        openFileChooser = new JFileChooser();
        openFileChooser.setAcceptAllFileFilterUsed(false);
        saveFileChooser = new JFileChooser();
        saveFileChooser.setAcceptAllFileFilterUsed(false);

//        newFileAction = new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                actionFileNew();
//            }
//        };
//        ActionUtils.setupAction(newFileAction, resourceBundle, "fileNewAction");
//        newFileAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, metaMask));
//
//        openFileAction = new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                actionFileOpen();
//            }
//        };
//        ActionUtils.setupAction(openFileAction, resourceBundle, "fileOpenAction");
//        openFileAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, metaMask));
//        openFileAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
//
//        saveFileAction = new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                actionFileSave();
//            }
//        };
//        ActionUtils.setupAction(saveFileAction, resourceBundle, "fileSaveAction");
//        saveFileAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, metaMask));
//
//        saveAsFileAction = new AbstractAction() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                actionFileSaveAs();
//            }
//        };
//        ActionUtils.setupAction(saveAsFileAction, resourceBundle, "fileSaveAsAction");
//        saveAsFileAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, metaMask));
//        saveAsFileAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
//
//        closeFileAction = new AbstractAction() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                actionFileClose();
//            }
//        };
//        ActionUtils.setupAction(closeFileAction, resourceBundle, "fileCloseAction");
//        closeFileAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, metaMask));
//        closeFileAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        AllFilesFilter filesFilter = new AllFilesFilter();
        addFileType(filesFilter);
        allFilesFilter = filesFilter;
    }

    /**
     * Attempts to release current file and warn if document was modified.
     *
     * @return true if successful
     */
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

    public void actionFileNew() {
        if (fileHandler != null) {
            fileHandler.newFile();
        }
    }

    public void actionFileOpen() {
        if (fileHandler != null) {
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

//                updateRecentFilesList(fileUri);
            }
        }
    }

    public void actionFileSave() {
        if (fileHandler != null) {
            Optional<URI> fileUri = fileHandler.getFileUri();
            if (fileUri.isPresent()) {
                fileHandler.saveToFile(fileUri.get(), fileHandler.getFileType().get());
            } else {
                actionFileSaveAs();
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
//                    updateRecentFilesList(fileUri);
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
            // TODO
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

    @Override
    public Action getNewFileAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Action getOpenFileAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Action getSaveFileAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Action getSaveAsFileAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @ParametersAreNonnullByDefault
    public class AllFilesFilter extends FileFilter implements FileType {

        @Override
        public boolean accept(File file) {
            return true;
        }

        @Nonnull
        @Override
        public String getDescription() {
            return "All files (*)";
        }

        @Nonnull
        @Override
        public String getFileTypeId() {
            return ALL_FILES_FILTER;
        }
    }

    public FileHandlerApi getFileHandler() {
        return fileHandler;
    }

    public void setFileHandler(FileHandlerApi fileHandler) {
        this.fileHandler = fileHandler;
    }
}
