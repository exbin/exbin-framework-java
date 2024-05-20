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
package org.exbin.framework.editor.wave;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.action.api.ComponentActivationService;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.file.api.EditableFileHandler;
import org.exbin.framework.file.api.FileType;
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
import org.exbin.framework.action.api.ComponentActivationProvider;
import org.exbin.framework.action.api.DefaultComponentActivationService;
import org.exbin.framework.operation.undo.api.UndoRedoControl;
import org.exbin.xbup.operation.undo.XBTLinearUndo;
import org.exbin.xbup.operation.undo.UndoRedo;
import org.exbin.framework.operation.undo.api.UndoRedoState;

/**
 * Audio file handler.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AudioFileHandler implements EditableFileHandler, ComponentActivationProvider {

    private AudioPanel audioPanel = new AudioPanel();

    private URI fileUri = null;
    private FileType fileType = null;
    private String title;
    private javax.sound.sampled.AudioFileFormat.Type audioFormatType = null;
    private DefaultComponentActivationService componentActivationService = new DefaultComponentActivationService();
    private UndoRedoControl undoRedoControl = null;

    private String ext;

    public AudioFileHandler() {
        init();
    }

    private void init() {
    }

    public void registerUndoHandler() {
        UndoRedo undoRedo = new XBTLinearUndo(null);
        undoRedoControl = new UndoRedoControl() {
            @Override
            public boolean canUndo() {
                return undoRedo.canUndo();
            }

            @Override
            public boolean canRedo() {
                return undoRedo.canRedo();
            }

            @Override
            public void performUndo() {
                try {
                    undoRedo.performUndo();
                    notifyUndoChanged();
                } catch (Exception ex) {
                    Logger.getLogger(AudioFileHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void performRedo() {
                try {
                    undoRedo.performRedo();
                    notifyUndoChanged();
                } catch (Exception ex) {
                    Logger.getLogger(AudioFileHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        audioPanel.setUndoRedo(undoRedo);
        /* undoHandler.setUndoUpdateListener(() -> {
            notifyUndoChanged();
        }); */
        notifyUndoChanged();
    }

    @Override
    public int getId() {
        return -1;
    }

    @Nonnull
    @Override
    public AudioPanel getComponent() {
        return audioPanel;
    }

    @Override
    public void loadFromFile(URI fileUri, @Nullable FileType fileType) {
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
                Logger.getLogger(AudioFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            XBWave wave = new XBWave();
            wave.loadFromFile(file);
            audioPanel.setWave(wave);
            this.fileUri = fileUri;
        }
        notifyUndoChanged();
    }

    @Override
    public boolean canSave() {
        return fileUri != null;
    }

    @Override
    public void saveFile() {
        saveToFile(fileUri, fileType);
    }

    @Override
    public void saveToFile(URI fileUri, @Nullable FileType fileType) {
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
                Logger.getLogger(AudioFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (getBuildInFileType() == null) {
            audioPanel.getWave().saveToFile(file);
        } else {
            audioPanel.getWave().saveToFile(file, getBuildInFileType());
        }
        audioPanel.notifyFileSaved();
        notifyUndoChanged();
    }

    @Override
    public void clearFile() {
        audioPanel.newWave();
        notifyUndoChanged();
    }

    @Nonnull
    @Override
    public Optional<URI> getFileUri() {
        return Optional.ofNullable(fileUri);
    }

    @Nonnull
    @Override
    public String getTitle() {
        if (fileUri != null) {
            String path = fileUri.getPath();
            int lastSegment = path.lastIndexOf("/");
            String fileName = lastSegment < 0 ? path : path.substring(lastSegment + 1);
            return fileName == null ? "" : fileName;
        }

        return title == null ? "" : title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    @Nonnull
    @Override
    public Optional<FileType> getFileType() {
        return Optional.ofNullable(fileType);
    }

    @Nullable
    public javax.sound.sampled.AudioFileFormat.Type getBuildInFileType() {
        return audioFormatType;
    }

    public void setFileType(javax.sound.sampled.AudioFileFormat.Type fileType) {
        this.audioFormatType = fileType;
    }

    @Override
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public boolean isModified() {
        return audioPanel.isModified();
    }

    @Nonnull
    @Override
    public ComponentActivationService getComponentActivationService() {
        return componentActivationService;
    }

    private void notifyUndoChanged() {
        if (undoRedoControl != null) {
            componentActivationService.updated(UndoRedoState.class, undoRedoControl);
        }
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
