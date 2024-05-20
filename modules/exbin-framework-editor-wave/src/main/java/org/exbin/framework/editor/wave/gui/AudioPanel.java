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
package org.exbin.framework.editor.wave.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.AdjustmentEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseMotionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JPopupMenu;
import org.exbin.framework.editor.wave.AudioEditor;
import org.exbin.framework.editor.wave.command.WaveClipboardData;
import org.exbin.framework.editor.wave.command.WaveCopyCommand;
import org.exbin.framework.editor.wave.command.WaveCutCommand;
import org.exbin.framework.editor.wave.command.WaveDeleteCommand;
import org.exbin.framework.editor.wave.command.WavePasteCommand;
import org.exbin.framework.editor.wave.command.WaveReverseCommand;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.audio.swing.XBWavePanel;
import org.exbin.xbup.audio.wave.XBWave;
import org.exbin.xbup.operation.undo.UndoRedo;

/**
 * Audio panel wave editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AudioPanel extends javax.swing.JPanel implements ClipboardActionsHandler {

    private UndoRedo undoRedo;
    private boolean wavePlayed = false;
    private int drawPosition = -1;
    private int wavePosition = -1;

    private final PlayThread playThread = new PlayThread();
    private final WavePaintThread wavePaintThread = new WavePaintThread();

    private Color[] defaultColors;
    private XBWavePanel wavePanel;
    private SourceDataLine sourceDataLine;
    private AudioInputStream audioInputStream;
    private AudioFormat targetFormat;
    private DataLine.Info targetDataLineInfo;
    private int dataLinePosition;
    private InputMethodListener caretListener;
    private final List<StatusChangeListener> statusChangeListeners = new ArrayList<>();
    private final List<WaveRepaintListener> waveRepaintListeners = new ArrayList<>();
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;

    public AudioPanel() {
        initComponents();
        init();
    }

    private void init() {
        wavePanel = new XBWavePanel();
        wavePanel.setSelectionChangedListener(() -> {
            if (clipboardActionsUpdateListener != null) {
                clipboardActionsUpdateListener.stateChanged();
            }
        });
        wavePanel.setZoomChangedListener(() -> {
            scrollBar.setMaximum(wavePanel.getWaveWidth());
        });
        sourceDataLine = null;
        defaultColors = getAudioPanelColors();

        add(wavePanel);
        /*scrollPane.setViewportView(wavePanel); */

        scrollBar.addAdjustmentListener((AdjustmentEvent evt) -> {
            int valuePosition = evt.getValue();
            if (wavePlayed) {
                if ((int) (wavePosition * wavePanel.getScaleRatio()) != valuePosition) {
                    seekPlaying((int) (valuePosition / wavePanel.getScaleRatio()));
                }
            } else if (wavePosition != valuePosition) {
                wavePanel.setWindowPosition(valuePosition < 0 ? 0 : (int) (valuePosition / wavePanel.getScaleRatio()));
                repaint();
            }
        });

        playThread.start();
        wavePaintThread.start();
        targetDataLineInfo = null;
        audioInputStream = null;
    }

    @Override
    public void performCopy() {
        XBWavePanel.SelectionRange selectionRange = wavePanel.getSelection();
        WaveCopyCommand copyCommand = new WaveCopyCommand(wavePanel, selectionRange.getBegin(), selectionRange.getEnd());
        copyCommand.execute();
    }

    @Override
    public void performCut() {
        XBWavePanel.SelectionRange selectionRange = wavePanel.getSelection();
        WaveCutCommand cutCommand = new WaveCutCommand(wavePanel, selectionRange.getBegin(), selectionRange.getEnd());
        try {
            undoRedo.execute(cutCommand);
        } catch (Exception ex) {
            Logger.getLogger(AudioPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        wavePanel.clearSelection();
    }

    @Override
    public void performDelete() {
        XBWavePanel.SelectionRange selectionRange = wavePanel.getSelection();
        WaveDeleteCommand deleteCommand = new WaveDeleteCommand(wavePanel, selectionRange.getBegin(), selectionRange.getEnd());
        try {
            undoRedo.execute(deleteCommand);
        } catch (Exception ex) {
            Logger.getLogger(AudioPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        wavePanel.clearSelection();
    }

    @Override
    public void performPaste() {
        WavePasteCommand pasteCommand = new WavePasteCommand(wavePanel, wavePanel.getCursorPosition());
        try {
            undoRedo.execute(pasteCommand);
        } catch (Exception ex) {
            Logger.getLogger(AudioPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        wavePanel.clearSelection();
    }

    @Override
    public void performSelectAll() {
        wavePanel.selectAll();
    }

    @Nullable
    public XBWave getWave() {
        return wavePanel.getWave();
    }

    @Nonnull
    public UndoRedo getUndoRedo() {
        return undoRedo;
    }

    public void newWave() {
        wavePanel.setWave(null);
        scrollBar.setMaximum(wavePanel.getWaveWidth());
        undoRedo.clear();
    }

    public void setWave(XBWave wave) {
        wavePanel.setWave(wave);
        scrollBar.setMaximum(wavePanel.getWaveWidth());

        targetFormat = wavePanel.getWave().getAudioFormat();
        targetDataLineInfo = new DataLine.Info(SourceDataLine.class, wavePanel.getWave().getAudioFormat());
        audioInputStream = wavePanel.getWave().getAudioInputStream();

        // If the format is not supported directly (i.e. if it is not PCM
        // encoded, then try to transcode it to PCM.
        if (!AudioSystem.isLineSupported(targetDataLineInfo)) {
            // This is the PCM format we want to transcode to.
            // The parameters here are audio format details that you
            // shouldn't need to understand for casual use.
            AudioFormat pcm = new AudioFormat(targetFormat.getSampleRate(), 16, targetFormat.getChannels(), true, false);

            // Get a wrapper stream around the input stream that does the
            // transcoding for us.
            audioInputStream = AudioSystem.getAudioInputStream(pcm, audioInputStream);

            // Update the format and info variables for the transcoded data
            targetFormat = audioInputStream.getFormat();
            targetDataLineInfo = new DataLine.Info(SourceDataLine.class, targetFormat);
        }

        try {
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(targetDataLineInfo); //(SourceDataLine) AudioSystem.getSourceDataLine(targetFormat, AudioSystem.getMixerInfo()[0]); //Line(targetDataLineInfo);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(AudioEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        wavePanel.setCursorPosition(0);
        wavePanel.setWindowPosition(0);
        wavePanel.repaint();
    }

    public boolean isModified() {
        return undoRedo.getCommandPosition() != undoRedo.getSyncPosition();
    }

    public void notifyFileSaved() {
        undoRedo.setSyncPosition();
    }

    public void performPlay() {
        wavePlay();
        notifyStatusChangeListeners();
    }

    public void performStop() {
        waveStop();
        notifyStatusChangeListeners();
    }

    private void wavePlay() {
        if (isPlaying()) {
            stopPlaying();
        } else {
            stopPlaying();
            startPlaying();
        }
    }

    private void waveStop() {
        if (isPlaying()) {
            stopPlaying();
        }

        wavePanel.setCursorPosition(0);
        wavePanel.setWindowPosition(0);
        wavePanel.repaint();
    }

    public int repaintWave() {
        int position = (int) playThread.getFramePosition();
        if (position > 0) {
            if (position != drawPosition) {
                return repaintOnPosition(position);
            }
        }

        return -1;
    }

    private int repaintOnPosition(int position) {
        drawPosition = position;
        int waveWidth = wavePanel.getWaveLength();
        int windowPosition;
        int screenWidth = (int) (getWidth() / wavePanel.getScaleRatio());
        if (position > waveWidth - screenWidth) {
            windowPosition = waveWidth - screenWidth;
        } else {
            windowPosition = (int) position - (screenWidth / 2);
        }

        if (windowPosition < 0) {
            windowPosition = 0;
        }

        wavePanel.setWindowPosition(windowPosition);
        wavePanel.setCursorPosition(position);
        wavePanel.repaint();
        notifyWaveRepaintListeners();
        return windowPosition;
    }

    private boolean isPlaying() {
        return playThread.playing;
    }

    private synchronized void playingChange(boolean play, boolean stop, int position) {
        if (wavePanel.getWave() != null) {
            wavePaintThread.terminated = true;
            wavePaintThread.terminate();
        }

        if (stop) {
            if (wavePanel.getWave() != null) {
                wavePaintThread.terminate();

                playThread.terminated = true;
                sourceDataLine.stop();
                playThread.terminate();
            }
        }

        if (stop && play) {
            repaintOnPosition(position);
        }

        if (play) {
            if (wavePanel.getWave() != null) {
                dataLinePosition = wavePanel.getCursorPosition();
                if (dataLinePosition >= wavePanel.getWave().getLengthInTicks()) {
                    dataLinePosition = 0;
                }

                synchronized (playThread) {
                    if (dataLinePosition < 0) {
                        playThread.bufferPosition = 0;
                    } else {
                        playThread.bufferPosition = dataLinePosition * targetFormat.getFrameSize();
                    }

                    playThread.playing = true;
                    playThread.notify();
                }

                drawPosition = dataLinePosition;
                synchronized (wavePaintThread) {
                    wavePaintThread.drawing = true;
                    wavePaintThread.notify();
                }
            }
        }
    }

    private void startPlaying() {
        playingChange(true, false, 0);
    }

    private void stopPlaying() {
        playingChange(false, true, 0);
    }

    private void seekPlaying(int position) {
        playingChange(true, true, position);
    }

    public void printFile() {
        PrinterJob job = PrinterJob.getPrinterJob();
        if (job.printDialog()) {
            try {
//                PrintJob myJob = imageArea.getToolkit().getPrintJob(null, fileName, null);
//                if (myJob != null) {
                job.setPrintable((Graphics graphics, PageFormat pageFormat, int pageIndex) -> {
                    //                        imageArea.print(graphics); // TODO: Rescale on page
                    if (pageIndex == 0) {
                        return Printable.PAGE_EXISTS;
                    }
                    return Printable.NO_SUCH_PAGE;
                });
                job.print();
//                }
            } catch (PrinterException ex) {
                Logger.getLogger(AudioPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Color[] getAudioPanelColors() {
        Color[] colors = new Color[6];
        colors[0] = wavePanel.getWaveColor();
        colors[1] = wavePanel.getWaveFillColor();
        colors[2] = wavePanel.getCursorColor();
        colors[3] = wavePanel.getCursorWaveColor();
        colors[4] = wavePanel.getBackground();
        colors[5] = wavePanel.getSelectionColor();
        return colors;
    }

    public void setAudioPanelColors(Color[] colors) {
        wavePanel.setWaveColor(colors[0]);
        wavePanel.setWaveFillColor(colors[1]);
        wavePanel.setCursorColor(colors[2]);
        wavePanel.setCursorWaveColor(colors[3]);
        wavePanel.setBackground(colors[4]);
        wavePanel.setSelectionColor(colors[5]);
        wavePanel.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollBar = new javax.swing.JScrollBar();

        setAutoscrolls(true);
        setInheritsPopupMenu(true);
        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        scrollBar.setBlockIncrement(40);
        scrollBar.setMaximum(0);
        scrollBar.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        scrollBar.setName("scrollBar"); // NOI18N
        add(scrollBar, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollBar scrollBar;
    // End of variables declaration//GEN-END:variables

    public void setUndoRedo(UndoRedo undoRedo) {
        this.undoRedo = undoRedo;
    }

    public void setPopupMenu(JPopupMenu menu) {
        wavePanel.setComponentPopupMenu(menu);
    }

    public void setScale(double ratio) {
        wavePanel.setScaleRatio(ratio);
        scrollBar.setMaximum(wavePanel.getWaveWidth());
        wavePanel.repaint();
    }

    public void scaleAndSeek(double ratio) {
        int windowPosition = wavePanel.getWindowPosition();
        windowPosition = (int) (windowPosition / (wavePanel.getScaleRatio() / ratio)) - wavePanel.getWidth() / 2;
        if (windowPosition < 0) {
            windowPosition = 0;
        }
        setScale(ratio);
        wavePanel.setWindowPosition(windowPosition);
        wavePanel.repaint();
    }

    public double getScale() {
        return wavePanel.getScaleRatio();
    }

    @Override
    public Point getMousePosition() {
        return wavePanel.getMousePosition();
    }

    public void attachCaretListener(MouseMotionListener listener) {
        wavePanel.addMouseMotionListener(listener);
    }

    public String getPositionTime() {
        int position;
        Point point = getMousePosition();
        if (point == null) {
            position = wavePanel.getCursorPosition();
        } else {
            position = (point.x + wavePanel.getWindowPosition());
        }

        return getTimeForTicks(position, wavePanel.getWave());
    }

    public Color[] getDefaultColors() {
        return defaultColors;
    }

    public void setVolume(int value) {
        if (sourceDataLine != null && sourceDataLine.isOpen()) {
            FloatControl control = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(control.getMinimum() * (1 - (float) Math.sqrt((float) value / 100)));
        }
    }

    public boolean getIsPlaying() {
        return isPlaying();
    }

    public void setDrawMode(XBWavePanel.DrawMode drawMode) {
        wavePanel.setDrawMode(drawMode);
    }

    public void setToolMode(XBWavePanel.ToolMode toolMode) {
        wavePanel.setToolMode(toolMode);
        wavePanel.repaint();
    }

    public void performTransformReverse() {
        WaveReverseCommand waveReverseCommand;
        if (isSelection()) {
            XBWavePanel.SelectionRange selectionRange = wavePanel.getSelection();
            waveReverseCommand = new WaveReverseCommand(wavePanel, selectionRange.getBegin(), selectionRange.getEnd());
        } else {
            waveReverseCommand = new WaveReverseCommand(wavePanel);
        }
        try {
            undoRedo.execute(waveReverseCommand);
        } catch (Exception ex) {
            Logger.getLogger(AudioPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isSelection() {
        return wavePanel.hasSelection() && wavePanel.getWave() != null;
    }

    @Override
    public boolean isEditable() {
        return wavePanel.getWave() != null;
    }

    @Override
    public boolean canSelectAll() {
        return true;
    }

    @Override
    public boolean canDelete() {
        return isEditable();
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        this.clipboardActionsUpdateListener = updateListener;
    }

    @Override
    public boolean canPaste() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        return clipboard.isDataFlavorAvailable(WaveClipboardData.WAVE_FLAVOR);
    }

    public boolean isEmpty() {
        return wavePanel.getWave() == null;
    }

    class PlayThread extends Thread {

        boolean playing;
        boolean terminated;
        int bufferPosition;

        @Override
        public void run() {
            playing = false;
            terminated = false;
            mainLoop();
        }

        private void mainLoop() {
            while (true) {
                try {
                    synchronized (this) {
                        wait();
                    }

                    terminated = false;
                    int bufferLength = wavePanel.getWave().getPageSize() / 6; // Workaround for getFramePosition issue in Java 1.6
                    try {
                        sourceDataLine.open(targetFormat, bufferLength); // wavePanel.getWave().getAudioFormat()
                        sourceDataLine.start();

                        //buffer = wavePanel.getWave().getBlock(block);
                        byte[] buffer = new byte[bufferLength];

                        DataLine.Info info = new DataLine.Info(SourceDataLine.class, wavePanel.getWave().getAudioFormat(), bufferLength);
                        boolean bIsSupportedDirectly = AudioSystem.isLineSupported(info);
                        audioInputStream = wavePanel.getWave().getAudioInputStream();
                        audioInputStream.skip(bufferPosition);
                        // TODO Detection doesn't work for > 16 bit lines for some reason
                        if (!bIsSupportedDirectly || wavePanel.getWave().getAudioFormat().getSampleSizeInBits() != 16) {
                            audioInputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
                        }

                        bufferLength = audioInputStream.read(buffer, 0, bufferLength);
                        //wavePanel.getWave().getBlock(block);
                        int offset = 0; //bufferPosition % bufferLength;
                        while ((buffer != null) && (!terminated) && (bufferLength > offset)) {
                            bufferPosition += bufferLength - offset;
                            sourceDataLine.write(buffer, offset, bufferLength - offset);
                            //block++;
                            //buffer = wavePanel.getWave().getBlock(block);
                            bufferLength = audioInputStream.read(buffer, 0, bufferLength);
                            offset = 0;
                        }

                        sourceDataLine.drain();
                        sourceDataLine.flush();
                        sourceDataLine.close();
                        playing = false;

                        if (!terminated) {
                            wavePanel.setCursorPosition(wavePanel.getWaveLength());
                            notifyStatusChangeListeners();
                        }
                    } catch (IOException | LineUnavailableException ex) {
                        Logger.getLogger(AudioPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(AudioPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        private long getFramePosition() {
            return (bufferPosition / targetFormat.getFrameSize());
        }

        private void terminate() {
            while (playing) {
                try {
                    terminated = true;
                    sleep(5);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AudioPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    class WavePaintThread extends Thread {

        boolean terminated;
        boolean drawing;

        @Override
        public void run() {
            terminated = false;
            drawing = false;
            mainLoop();
        }

        private void mainLoop() {
            while (true) {
                try {
                    synchronized (this) {
                        wait();
                    }

                    terminated = false;
                    while (!terminated) {
                        sleep(50);

                        if (!terminated) {
                            wavePosition = repaintWave();
                            if (wavePosition >= 0) {
                                scrollBar.setValue((int) (wavePosition * wavePanel.getScaleRatio()));
                            }
                        }
                    }

                    drawing = false;
                } catch (InterruptedException ex) {
                    Logger.getLogger(AudioPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        private void terminate() {
            while (drawing) {
                try {
                    terminated = true;
                    sleep(5);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AudioPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Prints supported formats to system console.
     *
     * @param li the line information.
     */
    private static void showFormats(Line.Info li) {
        if (li instanceof DataLine.Info) {
            AudioFormat[] afs = ((DataLine.Info) li).getFormats();
            for (AudioFormat af : afs) {
                System.out.println("        " + af.toString());
            }
        }
    }

    public static void main(String[] args) {
        // loop through all mixers, and all source and target lines within each mixer.
        Mixer.Info[] mis = AudioSystem.getMixerInfo();
        for (Mixer.Info mi : mis) {
            Mixer mixer = AudioSystem.getMixer(mi);
            // e.g. com.sun.media.sound.DirectAudioDevice
            System.out.println("mixer: " + mixer.getClass().getName());
            Line.Info[] lis = mixer.getSourceLineInfo();
            for (Line.Info li : lis) {
                System.out.println("    source line: " + li.toString());

                showFormats(li);
            }
            lis = mixer.getTargetLineInfo();
            for (Line.Info li : lis) {
                System.out.println("    target line: " + li.toString());
                showFormats(li);
            }
            Control[] cs = mixer.getControls();
            for (Control c : cs) {
                System.out.println("    control: " + c.toString());
            }
        }
    }

    public AudioFormat getWaveFormat() {
        return wavePanel.getWave().getAudioFormat();
    }

    public String getWaveLength() {
        return getTimeForTicks(wavePanel.getWave().getLengthInTicks(), wavePanel.getWave());
    }

    public interface StatusChangeListener extends EventListener {

        public void statusChanged();
    }

    public void addStatusChangeListener(StatusChangeListener listener) {
        statusChangeListeners.add(listener);
    }

    public void removeStatusChangeListener(StatusChangeListener listener) {
        statusChangeListeners.remove(listener);
    }

    private void notifyStatusChangeListeners() {
        statusChangeListeners.stream().map((listener) -> {
            if (wavePlayed != isPlaying()) {
                wavePlayed = !wavePlayed;
            }
            return listener;
        }).forEachOrdered((listener) -> {
            listener.statusChanged();
        });
    }

    public interface WaveRepaintListener extends EventListener {

        public void waveRepaint();
    }

    public void addWaveRepaintListener(WaveRepaintListener listener) {
        waveRepaintListeners.add(listener);
    }

    public void removeWaveRepaintListener(WaveRepaintListener listener) {
        waveRepaintListeners.remove(listener);
    }

    private void notifyWaveRepaintListeners() {
        waveRepaintListeners.forEach((listener) -> {
            listener.waveRepaint();
        });
    }

    public static String getTimeForTicks(int position, XBWave wave) {
        if (wave == null) {
            return "0:00.00";
        }

        float sampleRate = wave.getAudioFormat().getSampleRate();
        float adjustedPosition = position / sampleRate;

        String sub = String.valueOf((long) ((adjustedPosition - Math.floor(adjustedPosition)) * 100));
        if (sub.length() < 2) {
            sub = "0" + sub;
        }

        String sec = String.valueOf(((long) adjustedPosition) % 60);
        if (sec.length() < 2) {
            sec = "0" + sec;
        }

        return String.valueOf((long) adjustedPosition / 60) + ":" + sec + "." + sub;
    }
}
