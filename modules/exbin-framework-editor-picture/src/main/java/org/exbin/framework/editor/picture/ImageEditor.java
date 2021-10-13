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
package org.exbin.framework.editor.picture;

import java.awt.Toolkit;
import java.awt.event.MouseMotionListener;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import org.exbin.framework.editor.picture.gui.ImagePanel;
import static org.exbin.framework.editor.picture.gui.ImagePanel.toBufferedImage;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;
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
import org.exbin.xbup.visual.picture.XBBufferedImage;
import org.exbin.framework.gui.file.api.FileHandler;

/**
 * Image editor.
 *
 * @version 0.2.2 2021/10/01
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ImageEditor implements EditorProvider {
    
    private static final String DEFAULT_PICTURE_FILE_EXT = "PNG";

    private ImagePanel imagePanel;
    private FileHandler activeFile;
    private JPopupMenu popupMenu;
    private MouseMotionListener mouseMotionListener;

    public ImageEditor() {
        init();
    }
    
    private void init() {
        activeFile = new FileHandler() {
            private URI fileUri = null;
            private String ext = null;
            private FileType fileType = null;

            @Override
            public int getId() {
                return -1;
            }

            @Nonnull
            @Override
            public JComponent getComponent() {
                return imagePanel;
            }

            @Override
            public void loadFromFile(URI fileUri, FileType fileType) {
                File file = new File(fileUri);
                if (EditorPictureModule.XBPFILETYPE.equals(fileType.getFileTypeId())) {
                    try {
                        if (imagePanel.getImage() == null) {
                            imagePanel.initImage();
                        }

                        XBPCatalog catalog = new XBPCatalog();
                        catalog.addFormatDecl(getContextFormatDecl());
                        XBLFormatDecl formatDecl = new XBLFormatDecl(XBBufferedImage.XBUP_FORMATREV_CATALOGPATH);
                        XBBufferedImage bufferedImage = new XBBufferedImage(toBufferedImage(imagePanel.getImage()));
                        XBDeclaration declaration = new XBDeclaration(formatDecl, bufferedImage);
                        XBTPullTypeDeclaringFilter typeProcessing = new XBTPullTypeDeclaringFilter(catalog);
                        typeProcessing.attachXBTPullProvider(new XBToXBTPullConvertor(new XBPullReader(new FileInputStream(file))));
                        XBPSerialReader reader = new XBPSerialReader(typeProcessing);
                        reader.read(declaration);
                        imagePanel.setImage(bufferedImage.getImage());
                        this.fileUri = fileUri;
                    } catch (XBProcessingException | IOException ex) {
                        Logger.getLogger(ImageEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        imagePanel.setImage(toBufferedImage(Toolkit.getDefaultToolkit().getImage(fileUri.toURL())));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(ImageEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            @Override
            public void saveToFile(URI fileUri, FileType fileType) {
                File file = new File(fileUri);
                if (EditorPictureModule.XBPFILETYPE.equals(fileType.getFileTypeId())) {
                    try {
                        FileOutputStream output = new FileOutputStream(file);

                        XBPCatalog catalog = new XBPCatalog();
                        catalog.addFormatDecl(getContextFormatDecl());
                        XBLFormatDecl formatDecl = new XBLFormatDecl(XBBufferedImage.XBUP_FORMATREV_CATALOGPATH);
                        XBDeclaration declaration = new XBDeclaration(formatDecl, new XBBufferedImage(toBufferedImage(imagePanel.getImage())));
                        declaration.realignReservation(catalog);
                        XBTTypeUndeclaringFilter typeProcessing = new XBTTypeUndeclaringFilter(catalog);
                        typeProcessing.attachXBTListener(new XBTEventListenerToListener(new XBTToXBEventConvertor(new XBEventWriter(output))));
                        XBPSerialWriter writer = new XBPSerialWriter(new XBTListenerToEventListener(typeProcessing));
                        writer.write(declaration);
                    } catch (XBProcessingException | IOException ex) {
                        Logger.getLogger(ImageEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        if (fileType instanceof PictureFileType) {
                            ext = ((PictureFileType) fileType).getExt();
                        }

                        ImageIO.write((RenderedImage) imagePanel.getImage(), ext == null ? DEFAULT_PICTURE_FILE_EXT : ext, file);
                    } catch (IOException ex) {
                        Logger.getLogger(ImageEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
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
                imagePanel.newImage();
                imagePanel.setModified(false);
            }

            @Override
            public void setFileType(FileType fileType) {
                this.fileType = fileType;
            }

            @Override
            public boolean isModified() {
                return imagePanel.isModified();
            }
        };
    }

    @Nonnull
    @Override
    public JComponent getEditorComponent() {
        return imagePanel;
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
    public void loadFromFile(String fileName) throws URISyntaxException {
        URI fileUri = new URI(fileName);
        activeFile.loadFromFile(fileUri, null);
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        activeFile.loadFromFile(fileUri, fileType);
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
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
    public void saveFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveAsFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean releaseAllFiles() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
        ImagePanel imagePanel = (ImagePanel) activeFile.getComponent();
        imagePanel.setPopupMenu(popupMenu);
    }

    public void setMouseMotionListener(MouseMotionListener mouseMotionListener) {
        this.mouseMotionListener = mouseMotionListener;
        ImagePanel imagePanel = (ImagePanel) activeFile.getComponent();
        imagePanel.attachCaretListener(mouseMotionListener);
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
         XBLGroupDecl bitmapGroup = new XBLGroupDecl(new XBLGroupDef());
         List<XBGroupParam> bitmapBlocks = bitmapGroup.getGroupDef().getGroupParams();
         bitmapBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 4, 0, 0, 1, 0})));
         bitmapBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 4, 0, 0, 2, 0})));
         ((XBLGroupDef) bitmapGroup.getGroupDef()).provideRevision();
         XBLGroupDecl paletteGroup = new XBLGroupDecl(new XBLGroupDef());
         List<XBGroupParam> paletteBlocks = paletteGroup.getGroupDef().getGroupParams();
         paletteBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 4, 0, 1, 1, 0})));
         paletteBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 4, 0, 1, 1, 0})));
         paletteBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 4, 0, 1, 2, 0})));
         paletteBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 4, 0, 1, 3, 0})));
         paletteBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 4, 0, 1, 4, 0})));
         paletteBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 4, 0, 1, 5, 0})));
         paletteBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 4, 0, 1, 6, 0})));
         paletteBlocks.add(new XBGroupParamConsist(new XBLBlockDecl(new long[]{1, 4, 0, 1, 7, 0})));
         ((XBLGroupDef) paletteGroup.getGroupDef()).provideRevision();
         groups.add(new XBFormatParamConsist(bitmapGroup));
         groups.add(new XBFormatParamConsist(paletteGroup));
         formatDef.realignRevision();

         XBLFormatDecl formatDecl = new XBLFormatDecl(formatDef);
         formatDecl.setCatalogPath(XBBufferedImage.XBUP_FORMATREV_CATALOGPATH);*/

        XBPSerialReader reader = new XBPSerialReader(getClass().getResourceAsStream("/org/exbin/framework/editor/picture/resources/xbp_format_decl.xb"));
        XBLFormatDecl formatDecl = new XBLFormatDecl();
        try {
            reader.read(formatDecl);
        } catch (XBProcessingException | IOException ex) {
            Logger.getLogger(ImageEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return formatDecl;
    }
}
