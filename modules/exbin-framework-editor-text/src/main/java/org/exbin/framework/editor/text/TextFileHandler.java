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
package org.exbin.framework.editor.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.action.api.ActiveComponent;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.file.api.EditableFileHandler;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.editor.text.gui.TextPanelCompoundUndoManager;
import org.exbin.framework.operation.undo.api.UndoRedoState;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.editor.api.EditorFileHandler;
import org.exbin.framework.text.encoding.TextEncodingController;
import org.exbin.framework.text.font.TextFontController;
import org.exbin.framework.operation.undo.api.UndoRedoController;
import org.exbin.framework.context.api.ActiveContextManager;

/**
 * Text file handler.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFileHandler implements EditableFileHandler, EditorFileHandler {

    protected final TextPanel textPanel = new TextPanel();

    protected URI fileUri = null;
    protected String title;
    protected FileType fileType = null;
    protected ActiveContextManager contextManager;
    protected DialogParentComponent dialogParentComponent;
    protected UndoRedoController undoRedoControl = null;
    protected EditorTextPanelComponent textPanelComponent;
    public TextFileHandler() {
        init();
    }

    private void init() {
        textPanelComponent = new EditorTextPanelComponent(textPanel);
        textPanel.setUpdateListener(() -> {
            if (contextManager != null) {
                contextManager.changeActiveState(ActiveComponent.class, textPanelComponent);
            }
        });
    }

    public void registerUndoHandler() {
        TextPanelCompoundUndoManager undoHandler = textPanel.getUndo();
        undoRedoControl = new UndoRedoController() {
            @Override
            public boolean canUndo() {
                return undoHandler.canUndo();
            }

            @Override
            public boolean canRedo() {
                return undoHandler.canRedo();
            }

            @Override
            public void performUndo() {
                undoHandler.undo();
                notifyUndoChanged();
            }

            @Override
            public void performRedo() {
                undoHandler.redo();
                notifyUndoChanged();
            }
        };
        undoHandler.setUndoRedoChangeListener(() -> {
            notifyUndoChanged();
        });
        notifyUndoChanged();
    }

    @Override
    public int getId() {
        return -1;
    }

    @Nonnull
    @Override
    public TextPanel getComponent() {
        return textPanel;
    }

    @Override
    public void loadFromFile(URI fileUri, @Nullable FileType fileType) {
        File file = new File(fileUri);
        try {
            FileInputStream fileStream = new FileInputStream(file);
            int gotChars;
            char[] buffer = new char[32];
            StringBuilder data = new StringBuilder();
            BufferedReader rdr = new BufferedReader(new InputStreamReader(fileStream, textPanel.getCharset()));
            while ((gotChars = rdr.read(buffer)) != -1) {
                data.append(buffer, 0, gotChars);
            }
            textPanel.setText(data.toString());
            this.fileUri = fileUri;
        } catch (IOException ex) {
            Logger.getLogger(TextFileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        textPanel.setModified(false);
        notifyUndoChanged();
    }

    @Override
    public boolean canSave() {
        return textPanelComponent.isEditable();
    }

    @Override
    public void saveFile() {
        saveToFile(fileUri, fileType);
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        File file = new File(fileUri);
        try {
            try (FileOutputStream output = new FileOutputStream(file)) {
                String text = textPanel.getText();
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, textPanel.getCharset()))) {
                    int fileLength = text.length();
                    int offset = 0;
                    while (offset < fileLength) {
                        int length = Math.min(1024, fileLength - offset);
                        writer.write(text, offset, length);
                        offset += length;
                    }
                    this.fileUri = fileUri;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(TextFileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        textPanel.setModified(false);
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

    @Override
    public void clearFile() {
        textPanel.setText("");
        textPanel.setModified(false);
        notifyUndoChanged();
    }

    @Override
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public boolean isModified() {
        return textPanel.isModified();
    }

    @Override
    public void componentActivated(ActiveContextManager contextManager) {
        this.contextManager = contextManager;
        contextManager.changeActiveState(ActiveComponent.class, textPanelComponent);
        contextManager.changeActiveState(TextFontController.class, textPanelComponent);
        contextManager.changeActiveState(TextEncodingController.class, textPanelComponent);
        contextManager.changeActiveState(UndoRedoState.class, undoRedoControl);
        contextManager.changeActiveState(DialogParentComponent.class, (DialogParentComponent) () -> textPanel);
    }

    @Override
    public void componentDeactivated(ActiveContextManager contextManager) {
        this.contextManager = null;
        contextManager.changeActiveState(ActiveComponent.class, null);
        contextManager.changeActiveState(TextFontController.class, null);
        contextManager.changeActiveState(TextEncodingController.class, null);
        contextManager.changeActiveState(UndoRedoState.class, null);
        contextManager.changeActiveState(DialogParentComponent.class, dialogParentComponent);
    }

    @Override
    public void setDialogParentComponent(DialogParentComponent dialogParentComponent) {
        this.dialogParentComponent = dialogParentComponent;
    }

    public void notifyUndoChanged() {
        if (undoRedoControl != null) {
            if (contextManager != null) {
                contextManager.changeActiveState(UndoRedoState.class, undoRedoControl);
            }
        }
    }
}
