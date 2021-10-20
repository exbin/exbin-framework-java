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
package org.exbin.framework.editor.wave;

import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.xbup.audio.wave.XBWave;
import org.exbin.xbup.core.block.declaration.XBDeclaration;
import org.exbin.xbup.core.block.declaration.local.XBLFormatDecl;
import org.exbin.xbup.core.catalog.XBPCatalog;
import org.exbin.xbup.core.parser.XBProcessingException;
import org.exbin.xbup.core.parser.basic.convert.XBTTypeUndeclaringFilter;
import org.exbin.xbup.core.parser.token.event.XBEventWriter;
import org.exbin.xbup.core.parser.token.event.convert.XBTEventListenerToListener;
import org.exbin.xbup.core.parser.token.event.convert.XBTListenerToEventListener;
import org.exbin.xbup.core.parser.token.event.convert.XBTToXBEventConvertor;
import org.exbin.xbup.core.parser.token.pull.XBPullReader;
import org.exbin.xbup.core.parser.token.pull.convert.XBTPullTypeDeclaringFilter;
import org.exbin.xbup.core.parser.token.pull.convert.XBToXBTPullConvertor;
import org.exbin.xbup.core.serial.XBPSerialReader;
import org.exbin.xbup.core.serial.XBPSerialWriter;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.framework.gui.file.api.FileHandler;

/**
 * Audio editor.
 *
 * @version 0.2.2 2021/10/01
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AudioEditor implements EditorProvider {
    
    private javax.sound.sampled.AudioFileFormat.Type audioFormatType = null;

    private AudioPanel audioPanel = new AudioPanel();
    private FileHandler activeFile;
    private JPopupMenu popupMenu;
    private MouseMotionListener mouseMotionListener;
    private AudioPanel.StatusChangeListener statusChangeListener;
    private AudioPanel.WaveRepaintListener waveRepaintListener;
    private XBUndoHandler undoHandler;

    public AudioEditor() {
        init();
    }
    
    private void init() {
        activeFile = new FileHandler() {
            private URI fileUri = null;
            private FileType fileType = null;
            private String ext;

            @Override
            public int getId() {
                return -1;
            }

            @Nonnull
            @Override
            public JComponent getComponent() {
                return audioPanel;
            }

            @Override
            public void loadFromFile(URI fileUri, FileType fileType) {
                File file = new File(fileUri);
                if (EditorWaveModule.XBS_FILE_TYPE.equals(fileType.getFileTypeId())) {
                    try {
                        XBPCatalog catalog = new XBPCatalog();
                        catalog.addFormatDecl(getContextFormatDecl());
                        XBLFormatDecl formatDecl = new XBLFormatDecl(XBWave.XBUP_FORMATREV_CATALOGPATH);
                        XBWave wave = new XBWave();
                        XBDeclaration declaration = new XBDeclaration(formatDecl, wave);
                        XBTPullTypeDeclaringFilter typeProcessing = new XBTPullTypeDeclaringFilter(catalog);
                        typeProcessing.attachXBTPullProvider(new XBToXBTPullConvertor(new XBPullReader(new FileInputStream(file))));
                        XBPSerialReader reader = new XBPSerialReader(typeProcessing);
                        reader.read(declaration);
                        audioPanel.setWave(wave);
                        this.fileUri = fileUri;
                    } catch (XBProcessingException | IOException ex) {
                        Logger.getLogger(AudioEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    XBWave wave = new XBWave();
                    wave.loadFromFile(file);
                    audioPanel.setWave(wave);
                    this.fileUri = fileUri;
                }
            }

            @Override
            public void saveToFile(URI fileUri, FileType fileType) {
                File file = new File(fileUri);
                if (EditorWaveModule.XBS_FILE_TYPE.equals(fileType.getFileTypeId())) {
                    try {
                        FileOutputStream output = new FileOutputStream(file);

                        XBPCatalog catalog = new XBPCatalog();
                        catalog.addFormatDecl(getContextFormatDecl());
                        XBLFormatDecl formatDecl = new XBLFormatDecl(XBWave.XBUP_FORMATREV_CATALOGPATH);
                        XBDeclaration declaration = new XBDeclaration(formatDecl, audioPanel.getWave());
                        declaration.realignReservation(catalog);
                        XBTTypeUndeclaringFilter typeProcessing = new XBTTypeUndeclaringFilter(catalog);
                        typeProcessing.attachXBTListener(new XBTEventListenerToListener(new XBTToXBEventConvertor(new XBEventWriter(output))));
                        XBPSerialWriter writer = new XBPSerialWriter(new XBTListenerToEventListener(typeProcessing));
                        writer.write(declaration);
                    } catch (XBProcessingException | IOException ex) {
                        Logger.getLogger(AudioEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (getBuildInFileType() == null) {
                    audioPanel.getWave().saveToFile(file);
                } else {
                    audioPanel.getWave().saveToFile(file, getBuildInFileType());
                }
                audioPanel.notifyFileSaved();
            }

            @Override
            public void newFile() {
                audioPanel.newWave();
            }

            @Nonnull
            @Override
            public Optional<URI> getFileUri() {
                return Optional.ofNullable(fileUri);
            }

            @Nonnull
            @Override
            public Optional<String> getFileName() {
                if (fileUri != null) {
                    String path = fileUri.getPath();
                    int lastSegment = path.lastIndexOf("/");
                    return Optional.of(lastSegment < 0 ? path : path.substring(lastSegment + 1));
                }

                return Optional.empty();
            }

            @Nonnull
            @Override
            public Optional<FileType> getFileType() {
                return Optional.ofNullable(fileType);
            }

            @Override
            public void setFileType(FileType fileType) {
                this.fileType = fileType;
            }

            @Override
            public boolean isModified() {
                return audioPanel.isModified();
            }
        };
    }

    @Nonnull
    @Override
    public AudioPanel getEditorComponent() {
        return audioPanel;
    }

    @Nonnull
    @Override
    public Optional<FileHandler> getActiveFile() {
        return Optional.of(activeFile);
    }

    @Nonnull
    @Override
    public String getWindowTitle(String parentTitle) {
        URI fileUri = activeFile.getFileUri().orElse(null);
        if (fileUri != null) {
            String path = fileUri.getPath();
            int lastIndexOf = path.lastIndexOf("/");
            if (lastIndexOf < 0) {
                return path + " - " + parentTitle;
            }
            return path.substring(lastIndexOf + 1) + " - " + parentTitle;
        }

        return parentTitle;
    }

    @Override
    public void openFile(URI fileUri, FileType fileType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void newFile() {
        activeFile.newFile();
    }

    @Override
    public void openFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadFromFile(String fileName) throws URISyntaxException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public void saveFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveAsFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean releaseFile(FileHandler fileHandler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean releaseAllFiles() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
        AudioPanel audioPanel = (AudioPanel) activeFile.getComponent();
        audioPanel.setPopupMenu(popupMenu);
    }

    public void setMouseMotionListener(MouseMotionListener mouseMotionListener) {
        this.mouseMotionListener = mouseMotionListener;
        AudioPanel audioPanel = (AudioPanel) activeFile.getComponent();
        audioPanel.attachCaretListener(mouseMotionListener);
    }

    public void setStatusChangeListener(AudioPanel.StatusChangeListener statusChangeListener) {
        this.statusChangeListener = statusChangeListener;
        AudioPanel audioPanel = (AudioPanel) activeFile.getComponent();
        audioPanel.addStatusChangeListener(statusChangeListener);
    }

    public void setWaveRepaintListener(AudioPanel.WaveRepaintListener waveRepaintListener) {
        this.waveRepaintListener = waveRepaintListener;
        AudioPanel audioPanel = (AudioPanel) activeFile.getComponent();
        audioPanel.addWaveRepaintListener(waveRepaintListener);
    }

    public void setUndoHandler(XBUndoHandler undoHandler) {
        this.undoHandler = undoHandler;
        AudioPanel audioPanel = (AudioPanel) activeFile.getComponent();
        audioPanel.setUndoHandler(undoHandler);
    }

    @Nullable
    public javax.sound.sampled.AudioFileFormat.Type getBuildInFileType() {
        return audioFormatType;
    }

    public void setFileType(javax.sound.sampled.AudioFileFormat.Type fileType) {
        this.audioFormatType = fileType;
    }

    /**
     * Returns local format declaration when catalog or service is not
     * available.
     *
     * @return local format declaration
     */
    public XBLFormatDecl getContextFormatDecl() {
        /*XBLFormatDef formatDef = new XBLFormatDef();
         List<XBFormatParam> groups = formatDef.getFormatParams();
         XBLGroupDecl waveGroup = new XBLGroupDecl(new XBLGroupDef());
         List<XBGroupParam> waveBlocks = waveGroup.getGroupDef().getGroupParams();
         waveBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 5, 0, 0})));
         ((XBLGroupDef) waveGroup.getGroupDef()).provideRevision();
         groups.add(new XBFormatParamConsist(waveGroup));
         formatDef.realignRevision();

         XBLFormatDecl formatDecl = new XBLFormatDecl(formatDef);
         formatDecl.setCatalogPath(XBWave.XBUP_FORMATREV_CATALOGPATH);
         return formatDecl;*/

        XBPSerialReader reader = new XBPSerialReader(getClass().getResourceAsStream("/org/exbin/framework/editor/wave/resources/xbs_format_decl.xb"));
        XBLFormatDecl formatDecl = new XBLFormatDecl();
        try {
            reader.read(formatDecl);
        } catch (XBProcessingException | IOException ex) {
            Logger.getLogger(AudioEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return formatDecl;
    }
}
