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

import java.awt.Font;
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
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.FileType;
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
import org.exbin.xbup.core.type.XBEncodingText;

/**
 * Text file handler.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFileHandler implements FileHandler, TextFontApi {

    private final TextPanel textPanel = new TextPanel();

    private URI fileUri = null;
    private FileType fileType = null;

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
        switch (fileType.getFileTypeId()) {
            case EditorTextModule.XBT_FILE_TYPE: {
                try {
                    XBPCatalog catalog = new XBPCatalog();
                    catalog.addFormatDecl(getContextFormatDecl());
                    XBLFormatDecl formatDecl = new XBLFormatDecl(XBEncodingText.XBUP_FORMATREV_CATALOGPATH);
                    XBEncodingText encodingText = new XBEncodingText();
                    XBDeclaration declaration = new XBDeclaration(formatDecl, encodingText);
                    XBTPullTypeDeclaringFilter typeProcessing = new XBTPullTypeDeclaringFilter(catalog);
                    typeProcessing.attachXBTPullProvider(new XBToXBTPullConvertor(new XBPullReader(new FileInputStream(file))));
                    XBPSerialReader reader = new XBPSerialReader(typeProcessing);
                    reader.read(declaration);
                    textPanel.changeCharset(encodingText.getCharset());
                    textPanel.setText(encodingText.getValue());
                    this.fileUri = fileUri;
                } catch (XBProcessingException | IOException ex) {
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case EditorTextModule.TXT_FILE_TYPE: {
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
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }

        textPanel.setModified(false);
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        File file = new File(fileUri);
        switch (fileType.getFileTypeId()) {
            case EditorTextModule.XBT_FILE_TYPE: {
                try {
                    XBEncodingText encodingString = new XBEncodingText();
                    encodingString.setValue(textPanel.getText());
                    encodingString.setCharset(textPanel.getCharset());

                    try (FileOutputStream output = new FileOutputStream(file)) {
                        XBPCatalog catalog = new XBPCatalog();
                        catalog.addFormatDecl(getContextFormatDecl());
                        XBLFormatDecl formatDecl = new XBLFormatDecl(XBEncodingText.XBUP_FORMATREV_CATALOGPATH);
                        XBDeclaration declaration = new XBDeclaration(formatDecl, encodingString);
                        declaration.realignReservation(catalog);
                        XBTTypeUndeclaringFilter typeProcessing = new XBTTypeUndeclaringFilter(catalog);
                        typeProcessing.attachXBTListener(new XBTEventListenerToListener(new XBTToXBEventConvertor(new XBEventWriter(output))));
                        XBPSerialWriter writer = new XBPSerialWriter(new XBTListenerToEventListener(typeProcessing));
                        writer.write(declaration);
                        this.fileUri = fileUri;
                    }
                } catch (XBProcessingException | IOException ex) {
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            default: // TODO detect extension
            case EditorTextModule.TXT_FILE_TYPE: {
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
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }

        textPanel.setModified(false);
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
    public void newFile() {
        textPanel.setText("");
        textPanel.setModified(false);
    }

    @Override
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public boolean isModified() {
        return textPanel.isModified();
    }

    @Nonnull
    @Override
    public Font getCurrentFont() {
        return textPanel.getCurrentFont();
    }

    @Nonnull
    @Override
    public Font getDefaultFont() {
        return textPanel.getDefaultFont();
    }

    @Override
    public void setCurrentFont(Font font) {
        textPanel.setCurrentFont(font);
    }

    /**
     * Returns local format declaration when catalog or service is not
     * available.
     *
     * @return local format declaration
     */
    @Nullable
    public XBLFormatDecl getContextFormatDecl() {
        /*XBLFormatDef formatDef = new XBLFormatDef();
         List<XBFormatParam> groups = formatDef.getFormatParams();
         XBLGroupDecl stringGroup = new XBLGroupDecl(new XBLGroupDef());
         List<XBGroupParam> stringBlocks = stringGroup.getGroupDef().getGroupParams();
         stringBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 3, 1, 2, 0, 0})));
         stringBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 3, 1, 1, 1, 0})));
         stringBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 3, 1, 2, 2, 0})));
         stringBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 3, 1, 2, 3, 0})));
         stringBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 3, 1, 2, 4, 0})));
         ((XBLGroupDef) stringGroup.getGroupDef()).provideRevision();
         groups.add(new XBFormatParamConsist(stringGroup));
         formatDef.realignRevision();

         XBLFormatDecl formatDecl = new XBLFormatDecl(formatDef);
         formatDecl.setCatalogPath(XBEncodingText.XBUP_FORMATREV_CATALOGPATH);
         return formatDecl;*/

        XBPSerialReader reader = new XBPSerialReader(getClass().getResourceAsStream("/org/exbin/framework/editor/text/resources/xbt_format_decl.xb"));
        XBLFormatDecl formatDecl = new XBLFormatDecl();
        try {
            reader.read(formatDecl);
        } catch (XBProcessingException | IOException ex) {
            return null;
        }
        return formatDecl;
    }
}
