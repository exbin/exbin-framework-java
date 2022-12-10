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
package org.exbin.framework.editor.picture.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ClipboardActionsUpdateListener;

/**
 * Image panel for picture editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class ImagePanel extends javax.swing.JPanel implements ClipboardActionsHandler {

    private final UndoManager undo;
    private final boolean modified = false;
    private Image image;
    private Graphics graphics;
    private double scaleRatio;
    private final Object highlight;
    private Color selectionColor;
    private Color toolColor;
    private Font defaultFont;
    private Color[] defaultColors;
    private InputMethodListener caretListener;
    private final ToolMode toolMode;
    private ImageStatusPanel imageStatusPanel;
    private final ImageAreaPanel imageArea;

    public ImagePanel() {
        initComponents();
        imageArea = new ImageAreaPanel();

        imageArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imageAreaMouseClicked(evt);
            }
        });
        imageArea.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                imageAreaMouseDragged(evt);
            }
        });

        scrollPane.setViewportView(imageArea);
        scaleRatio = 1;
        toolMode = ToolMode.DOTTER;
        undo = new UndoManager();
        highlight = null;
        toolColor = Color.BLACK;
        selectionColor = Color.YELLOW;

        // if the document is ever edited, assume that it needs to be saved
/*        imageArea.getDocument().addDocumentListener(new DocumentListener() {
         public void changedUpdate(DocumentEvent e) { setModified(true); }
         public void insertUpdate(DocumentEvent e) { setModified(true); }
         public void removeUpdate(DocumentEvent e) { setModified(true); }
         }); */
        // Listener for undo and redo events
/*        imageArea.getDocument().addUndoableEditListener(new UndoableEditListener() {

         public void undoableEditHappened(UndoableEditEvent evt) {
         undo.addEdit(evt.getEdit());
         firePropertyChange("undoAvailable", false, true);
         firePropertyChange("redoAvailable", false, true);
         }
         }); */
        // Create an undo action and add it to the text component
        imageArea.getActionMap().put("Undo", new AbstractAction("Undo") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo.canUndo()) {
                        undo.undo();
                    }
                } catch (CannotUndoException ex) {
                    Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        // Bind the undo action to ctl-Z
        imageArea.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

        // Create a redo action and add it to the text component
        imageArea.getActionMap().put("Redo", new AbstractAction("Redo") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo.canRedo()) {
                        undo.redo();
                    }
                } catch (CannotRedoException ex) {
                    Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        // Bind the redo action to ctl-Y
        imageArea.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

        imageArea.addMouseWheelListener((MouseWheelEvent e) -> {
            int relativeMouseX = e.getPoint().x - scrollPane.getHorizontalScrollBar().getValue();
            int relativeMouseY = e.getPoint().y - scrollPane.getVerticalScrollBar().getValue();
            int positionX = (int) (e.getPoint().x * scaleRatio);
            int positionY = (int) (e.getPoint().y * scaleRatio);
            if (e.getWheelRotation() == 1) {
                setScale(scaleRatio * 2);
                positionX -= (int) (relativeMouseX * scaleRatio);
                positionY -= (int) (relativeMouseY * scaleRatio);

            } else if (e.getWheelRotation() == -1) {
                setScale(scaleRatio / 2);
                positionX -= (int) (relativeMouseX * scaleRatio);
                positionY -= (int) (relativeMouseY * scaleRatio);
            }
            if (positionX < 0) {
                positionX = 0;
            }
            if (positionY < 0) {
                positionY = 0;
            }
            scrollPane.getHorizontalScrollBar().setValue((int) (positionX / scaleRatio));
            scrollPane.getVerticalScrollBar().setValue((int) (positionY / scaleRatio));
            repaint();
        });
    }

    private void imageAreaMouseDragged(java.awt.event.MouseEvent evt) {
        if (null != toolMode) {
            switch (toolMode) {
                case DOTTER:
                    int x = (int) (evt.getX() * scaleRatio);
                    int y = (int) (evt.getY() * scaleRatio);
                    if (graphics != null) {
                        graphics.fillOval(x - 5, y - 5, 10, 10);
                        imageArea.repaint();
                    } else {
                        System.out.println("Graphics is null!");
                    }   //            evt.getComponent().repaint();
                    evt.consume();
                    break;
                case PENCIL:
                    break;
                case LINE:
                    break;
                default:
                    break;
            }
        }
    }

    private void imageAreaMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
            if (null != toolMode) {
                switch (toolMode) {
                    case DOTTER:
                        int x = (int) (evt.getX() * scaleRatio);
                        int y = (int) (evt.getY() * scaleRatio);
                        if (graphics != null) {
                            graphics.fillOval(x - 5, y - 5, 10, 10);
                            repaint();
                        } else {
                            System.out.println("Graphics is null!");
                        }   //                evt.getComponent().repaint();
                        evt.consume();
                        break;
                    case PENCIL:
                        break;
                    case LINE:
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void performCopy() {
        throw new UnsupportedOperationException("Not supported yet.");
//        imageArea.copy();
    }

    @Override
    public void performCut() {
        throw new UnsupportedOperationException("Not supported yet.");
//        imageArea.cut();
    }

    @Override
    public void performDelete() {
        throw new UnsupportedOperationException("Not supported yet.");
//        imageArea.getInputContext().dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DELETE, KeyEvent.CHAR_UNDEFINED));
    }

    @Override
    public void performPaste() {
        throw new UnsupportedOperationException("Not supported yet.");
//        imageArea.paste();
    }

    @Override
    public void performSelectAll() {
        throw new UnsupportedOperationException("Not supported yet.");
//        imageArea.selectAll();
    }

    public void printFile() {
        PrinterJob job = PrinterJob.getPrinterJob();
        if (job.printDialog()) {
            try {
//                PrintJob myJob = imageArea.getToolkit().getPrintJob(null, fileName, null);
//                if (myJob != null) {
                job.setPrintable((Graphics graphics1, PageFormat pageFormat, int pageIndex) -> {
                    imageArea.print(graphics1); // TODO: Rescale on page
                    if (pageIndex == 0) {
                        return Printable.PAGE_EXISTS;
                    }
                    return Printable.NO_SUCH_PAGE;
                });
                job.print();
//                }
            } catch (PrinterException ex) {
                Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Color getSelectionColor() {
        return selectionColor;
    }

    public void setSelectionColor(Color color) {
        selectionColor = color;
        if (highlight != null) {
            // TODO: Replace higlighter with the same with new color
//            ((DefaultHighlighter) highlight).getHighlights()[0].
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();

        setAutoscrolls(true);
        setInheritsPopupMenu(true);
        setName("Form"); // NOI18N
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        scrollPane.setName("scrollPane"); // NOI18N
        add(scrollPane);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        /*        if (highlight != null) {
         imageArea.getHighlighter().removeHighlight(highlight);
         highlight = null;
         }
         boolean oldValue = this.modified;
         this.modified = modified;
         firePropertyChange("modified", oldValue, this.modified); */
    }

    public boolean isEditEnabled() {
        return false;
//        return imageArea.isEditable();
    }

    public boolean isPasteEnabled() {
        return false;
//        return imageArea.isEditable();
    }

    public UndoManager getUndo() {
        return undo;
    }

    public void setPopupMenu(JPopupMenu menu) {
        imageArea.setComponentPopupMenu(menu);
    }

    public Font getDefaultFont() {
        return defaultFont;
    }

    public void setScale(double ratio) {
        scaleRatio = ratio;
        imageArea.updateSize();
        scrollPane.repaint();
    }

    public double getScale() {
        return scaleRatio;
    }

    // This method returns a buffered image with the contents of an image
    // From: http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html
    @Nullable
    public static BufferedImage toBufferedImage(@Nullable Image image) {
        if (image == null) {
            return null;
        }

        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            if ((width == -1) || (height == -1)) {
                // Something gone wrong

            }
            bimage = gc.createCompatibleImage(width, height, transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        if (cm == null) {
            return false;
        }
        return cm.hasAlpha();
    }

    @Override
    public Point getMousePosition() {
        return imageArea.getMousePosition();
    }

    public void attachCaretListener(MouseMotionListener listener) {
        imageArea.addMouseMotionListener(listener);
    }

    public Color getToolColor() {
        return toolColor;
    }

    public void setToolColor(Color toolColor) {
        this.toolColor = toolColor;
        if (graphics != null) {
            graphics.setColor(toolColor);
        }
    }

    public void performResize(int width, int height) {
        image = toBufferedImage(image.getScaledInstance(width, height, Image.SCALE_DEFAULT));
        graphics = image.getGraphics();
        graphics.setColor(toolColor);
        setScale(scaleRatio);
    }

    public void newImage() {
        image = toBufferedImage(createImage(100, 100));
        graphics = image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 100, 100);
        graphics.setColor(toolColor);
    }

    @Nullable
    public Image getImage() {
        return image;
    }

    public void initImage() {
        image = createImage(1, 1);
    }

    public void setImage(Image image) {
        this.image = image;

        graphics = image.getGraphics();
        graphics.setColor(toolColor);
        setScale(scaleRatio);
    }

    @Override
    public boolean isSelection() {
        return false;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean canSelectAll() {
        return false;
    }

    @Override
    public boolean canPaste() {
        return false;
    }

    @Override
    public boolean canDelete() {
        return false;
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
    }

    public void registerImageStatus(ImageStatusPanel imageStatusPanel) {
        this.imageStatusPanel = imageStatusPanel;
    }

    public Point getImageSize() {
        return new Point(image.getWidth(null), image.getHeight(null));
    }

    public enum ToolMode {

        DOTTER,
        PENCIL,
        LINE
    }

    private class ImageAreaPanel extends JPanel {

        private int width;
        private int height;
        private int scaledWidth;
        private int scaledHeight;

        public ImageAreaPanel() {
        }

        private final ImageObserver imageObserver = (Image img, int infoflags, int x1, int y1, int width1, int height1) -> true;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Rectangle clipBounds = g.getClipBounds();
            g.setColor(Color.WHITE);
            g.fillRect(clipBounds.x, clipBounds.y, clipBounds.x + clipBounds.width, clipBounds.y + clipBounds.height);
            g.drawImage(image, 0, 0, scaledWidth, scaledHeight, 0, 0, width, height, imageObserver);
        }

        public void updateSize() {
            width = image.getWidth(imageObserver);
            height = image.getHeight(imageObserver);
            scaledWidth = (int) Math.ceil(width / scaleRatio);
            if (scaledWidth < 1) {
                scaledWidth = 1;
            }
            scaledHeight = (int) Math.ceil(height / scaleRatio);
            if (scaledHeight < 1) {
                scaledHeight = 1;
            }

            setPreferredSize(new Dimension(scaledWidth, scaledHeight));
            revalidate();
        }
    }
}
