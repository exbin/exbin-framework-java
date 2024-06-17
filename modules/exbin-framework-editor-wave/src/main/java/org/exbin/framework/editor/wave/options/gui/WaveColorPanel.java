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
package org.exbin.framework.editor.wave.options.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import org.exbin.framework.App;
import org.exbin.framework.editor.wave.options.impl.WaveColorOptionsImpl;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.options.api.OptionsModifiedListener;
import org.exbin.framework.editor.wave.service.WaveColorService;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;

/**
 * Wave editor color selection panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WaveColorPanel extends javax.swing.JPanel implements OptionsComponent<WaveColorOptionsImpl> {

    private OptionsModifiedListener optionsModifiedListener;
    private WaveColorService waveColorService;
    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(WaveColorPanel.class);

    public WaveColorPanel() {
        initComponents();
    }

    public void setWaveColorService(WaveColorService waveColorService) {
        this.waveColorService = waveColorService;
        fillCurrentButton.setEnabled(true);
        fillDefaultButton.setEnabled(true);
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Override
    public void loadFromOptions(WaveColorOptionsImpl options) {
        Integer rgb;
        try {
            rgb = options.getWaveColor();
            if (rgb != null) {
                setWaveColor(new Color(rgb));
            }
        } catch (NumberFormatException e) {
        }
        try {
            rgb = options.getWaveBackgroundColor();
            if (rgb != null) {
                setWaveBackgroundColor(new Color(rgb));
            }
        } catch (NumberFormatException e) {
        }
        try {
            rgb = options.getWaveSelectionColor();
            if (rgb != null) {
                setWaveSelectionColor(new Color(rgb));
            }
        } catch (NumberFormatException e) {
        }
        try {
            rgb = options.getWaveCursorColor();
            if (rgb != null) {
                setWaveCursorColor(new Color(rgb));
            }
        } catch (NumberFormatException e) {
        }
    }

    @Override
    public void saveToOptions(WaveColorOptionsImpl options) {
        options.setWaveColor(getWaveColor().getRGB());
        options.setWaveFillColor(getWaveFillColor().getRGB());
        options.setWaveBackgroundColor(getWaveBackgroundColor().getRGB());
        options.setWaveSelectionColor(getWaveSelectionColor().getRGB());
        options.setWaveCursorColor(getWaveCursorColor().getRGB());
        options.setWaveCursorWaveColor(getWaveCursorWaveColor().getRGB());
    }

    @Nonnull
    public Color getWaveColor() {
        return waveColorPanel.getBackground();
    }

    @Nonnull
    public Color getWaveFillColor() {
        return waveFillColorPanel.getBackground();
    }

    @Nonnull
    public Color getWaveBackgroundColor() {
        return waveBackgroundColorPanel.getBackground();
    }

    @Nonnull
    public Color getWaveSelectionColor() {
        return waveSelectionColorPanel.getBackground();
    }

    @Nonnull
    public Color getWaveCursorColor() {
        return waveCursorColorPanel.getBackground();
    }

    @Nonnull
    public Color getWaveCursorWaveColor() {
        return waveCursorWaveColorPanel.getBackground();
    }

    public void setDefaultAudioPanelColors() {
        setWaveColorsFromArray(waveColorService.getDefaultWaveColors());
    }

    public void setWaveColor(Color color) {
        waveColorPanel.setBackground(color);
    }

    public void setWaveFillColor(Color color) {
        waveFillColorPanel.setBackground(color);
    }

    public void setWaveBackgroundColor(Color color) {
        waveBackgroundColorPanel.setBackground(color);
    }

    public void setWaveSelectionColor(Color color) {
        waveSelectionColorPanel.setBackground(color);
    }

    public void setWaveCursorColor(Color color) {
        waveCursorColorPanel.setBackground(color);
    }

    public void setWaveCursorWaveColor(Color color) {
        waveCursorWaveColorPanel.setBackground(color);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        waveColorButton.setEnabled(enabled);
        waveFillColorButton.setEnabled(enabled);
        waveBackgroundColorButton.setEnabled(enabled);
        waveSelectionColorButton.setEnabled(enabled);
        waveCursorColorButton.setEnabled(enabled);
        waveCursorWaveColorButton.setEnabled(enabled);
        fillCurrentButton.setEnabled(enabled);
        fillDefaultButton.setEnabled(enabled);
        fillCurrentButton.setEnabled(enabled && waveColorService != null);
        fillDefaultButton.setEnabled(enabled && waveColorService != null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        colorChooser = new javax.swing.JColorChooser();
        waveSelectionColorButton = new javax.swing.JButton();
        waveColorLabel = new javax.swing.JLabel();
        waveColorPanel = new javax.swing.JPanel();
        waveColorButton = new javax.swing.JButton();
        waveSelectionColorLabel = new javax.swing.JLabel();
        waveBackgroundColorLabel = new javax.swing.JLabel();
        waveCursorColorLabel = new javax.swing.JLabel();
        fillDefaultButton = new javax.swing.JButton();
        fillCurrentButton = new javax.swing.JButton();
        waveCursorColorPanel = new javax.swing.JPanel();
        waveCursorColorButton = new javax.swing.JButton();
        waveSelectionColorPanel = new javax.swing.JPanel();
        waveBackgroundColorPanel = new javax.swing.JPanel();
        waveBackgroundColorButton = new javax.swing.JButton();
        waveFillColorLabel = new javax.swing.JLabel();
        waveFillColorPanel = new javax.swing.JPanel();
        waveFillColorButton = new javax.swing.JButton();
        waveCursorWaveColorLabel = new javax.swing.JLabel();
        waveCursorWaveColorPanel = new javax.swing.JPanel();
        waveCursorWaveColorButton = new javax.swing.JButton();

        colorChooser.setName("colorChooser"); // NOI18N

        setName("Form"); // NOI18N

        waveSelectionColorButton.setText(resourceBundle.getString("selectButton.text")); // NOI18N
        waveSelectionColorButton.setName("waveSelectionColorButton"); // NOI18N
        waveSelectionColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waveSelectionColorButtonActionPerformed(evt);
            }
        });

        waveColorLabel.setText(resourceBundle.getString("waveColorLabel.text")); // NOI18N
        waveColorLabel.setName("waveColorLabel"); // NOI18N

        waveColorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        waveColorPanel.setName("waveColorPanel"); // NOI18N

        javax.swing.GroupLayout waveColorPanelLayout = new javax.swing.GroupLayout(waveColorPanel);
        waveColorPanel.setLayout(waveColorPanelLayout);
        waveColorPanelLayout.setHorizontalGroup(
            waveColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        waveColorPanelLayout.setVerticalGroup(
            waveColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        waveColorButton.setText(resourceBundle.getString("selectButton.text")); // NOI18N
        waveColorButton.setName("waveColorButton"); // NOI18N
        waveColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waveColorButtonActionPerformed(evt);
            }
        });

        waveSelectionColorLabel.setText(resourceBundle.getString("waveSelectionColorLabel.text")); // NOI18N
        waveSelectionColorLabel.setName("waveSelectionColorLabel"); // NOI18N

        waveBackgroundColorLabel.setText(resourceBundle.getString("waveBackgroundColorLabel.text")); // NOI18N
        waveBackgroundColorLabel.setName("waveBackgroundColorLabel"); // NOI18N

        waveCursorColorLabel.setText(resourceBundle.getString("waveCursorColorLabel.text")); // NOI18N
        waveCursorColorLabel.setName("waveCursorColorLabel"); // NOI18N

        fillDefaultButton.setText(resourceBundle.getString("fillDefaultButton.text")); // NOI18N
        fillDefaultButton.setName("fillDefaultButton"); // NOI18N
        fillDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillDefaultButtonActionPerformed(evt);
            }
        });

        fillCurrentButton.setText(resourceBundle.getString("fillCurrentButton.text")); // NOI18N
        fillCurrentButton.setName("fillCurrentButton"); // NOI18N
        fillCurrentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillCurrentButtonActionPerformed(evt);
            }
        });

        waveCursorColorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        waveCursorColorPanel.setName("waveCursorColorPanel"); // NOI18N

        javax.swing.GroupLayout waveCursorColorPanelLayout = new javax.swing.GroupLayout(waveCursorColorPanel);
        waveCursorColorPanel.setLayout(waveCursorColorPanelLayout);
        waveCursorColorPanelLayout.setHorizontalGroup(
            waveCursorColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        waveCursorColorPanelLayout.setVerticalGroup(
            waveCursorColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        waveCursorColorButton.setText(resourceBundle.getString("selectButton.text")); // NOI18N
        waveCursorColorButton.setName("waveCursorColorButton"); // NOI18N
        waveCursorColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waveCursorColorButtonActionPerformed(evt);
            }
        });

        waveSelectionColorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        waveSelectionColorPanel.setName("waveSelectionColorPanel"); // NOI18N

        javax.swing.GroupLayout waveSelectionColorPanelLayout = new javax.swing.GroupLayout(waveSelectionColorPanel);
        waveSelectionColorPanel.setLayout(waveSelectionColorPanelLayout);
        waveSelectionColorPanelLayout.setHorizontalGroup(
            waveSelectionColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        waveSelectionColorPanelLayout.setVerticalGroup(
            waveSelectionColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        waveBackgroundColorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        waveBackgroundColorPanel.setName("waveBackgroundColorPanel"); // NOI18N

        javax.swing.GroupLayout waveBackgroundColorPanelLayout = new javax.swing.GroupLayout(waveBackgroundColorPanel);
        waveBackgroundColorPanel.setLayout(waveBackgroundColorPanelLayout);
        waveBackgroundColorPanelLayout.setHorizontalGroup(
            waveBackgroundColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        waveBackgroundColorPanelLayout.setVerticalGroup(
            waveBackgroundColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        waveBackgroundColorButton.setText(resourceBundle.getString("selectButton.text")); // NOI18N
        waveBackgroundColorButton.setName("waveBackgroundColorButton"); // NOI18N
        waveBackgroundColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waveBackgroundColorButtonActionPerformed(evt);
            }
        });

        waveFillColorLabel.setText(resourceBundle.getString("waveFillColorLabel.text")); // NOI18N
        waveFillColorLabel.setName("waveFillColorLabel"); // NOI18N

        waveFillColorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        waveFillColorPanel.setName("waveFillColorPanel"); // NOI18N

        javax.swing.GroupLayout waveFillColorPanelLayout = new javax.swing.GroupLayout(waveFillColorPanel);
        waveFillColorPanel.setLayout(waveFillColorPanelLayout);
        waveFillColorPanelLayout.setHorizontalGroup(
            waveFillColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        waveFillColorPanelLayout.setVerticalGroup(
            waveFillColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        waveFillColorButton.setText(resourceBundle.getString("selectButton.text")); // NOI18N
        waveFillColorButton.setName("waveFillColorButton"); // NOI18N
        waveFillColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waveFillColorButtonActionPerformed(evt);
            }
        });

        waveCursorWaveColorLabel.setText(resourceBundle.getString("waveCursorWaveColorLabel.text")); // NOI18N
        waveCursorWaveColorLabel.setName("waveCursorWaveColorLabel"); // NOI18N

        waveCursorWaveColorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        waveCursorWaveColorPanel.setName("waveCursorWaveColorPanel"); // NOI18N

        javax.swing.GroupLayout waveCursorWaveColorPanelLayout = new javax.swing.GroupLayout(waveCursorWaveColorPanel);
        waveCursorWaveColorPanel.setLayout(waveCursorWaveColorPanelLayout);
        waveCursorWaveColorPanelLayout.setHorizontalGroup(
            waveCursorWaveColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        waveCursorWaveColorPanelLayout.setVerticalGroup(
            waveCursorWaveColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        waveCursorWaveColorButton.setText(resourceBundle.getString("selectButton.text")); // NOI18N
        waveCursorWaveColorButton.setName("waveCursorWaveColorButton"); // NOI18N
        waveCursorWaveColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waveCursorWaveColorButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(waveFillColorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(waveBackgroundColorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(waveSelectionColorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(6, 6, 6)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(waveSelectionColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(waveBackgroundColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(waveFillColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(waveColorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waveColorButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(waveCursorWaveColorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(waveCursorColorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(waveCursorWaveColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(waveCursorColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(waveFillColorLabel)
                            .addComponent(waveBackgroundColorLabel)
                            .addComponent(waveSelectionColorLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fillCurrentButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fillDefaultButton))
                            .addComponent(waveCursorColorLabel)
                            .addComponent(waveCursorWaveColorLabel)
                            .addComponent(waveColorLabel))
                        .addGap(0, 308, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(waveColorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(waveColorButton)
                    .addComponent(waveColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(waveFillColorLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(waveFillColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(waveFillColorButton))
                .addGap(18, 18, 18)
                .addComponent(waveBackgroundColorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(waveBackgroundColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(waveBackgroundColorButton))
                .addGap(18, 18, 18)
                .addComponent(waveSelectionColorLabel)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(waveSelectionColorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(waveSelectionColorButton))
                .addGap(18, 18, 18)
                .addComponent(waveCursorColorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(waveCursorColorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(waveCursorColorButton))
                .addGap(18, 18, 18)
                .addComponent(waveCursorWaveColorLabel)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(waveCursorWaveColorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(waveCursorWaveColorButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fillCurrentButton)
                    .addComponent(fillDefaultButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void waveColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveColorButtonActionPerformed
        colorChooser.setColor(waveColorPanel.getBackground());
        JDialog dialog = JColorChooser.createDialog(this, resourceBundle.getString("JColorChooser.title"), true, colorChooser, (ActionEvent e) -> {
            setWaveColor(colorChooser.getColor());
            setModified(true);
        }, null);
        dialog.setVisible(true);
    }//GEN-LAST:event_waveColorButtonActionPerformed

    private void waveBackgroundColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveBackgroundColorButtonActionPerformed
        colorChooser.setColor(waveBackgroundColorPanel.getBackground());
        JDialog dialog = JColorChooser.createDialog(this, resourceBundle.getString("JColorChooser.title"), true, colorChooser, (ActionEvent e) -> {
            setWaveBackgroundColor(colorChooser.getColor());
            setModified(true);
        }, null);
        dialog.setVisible(true);
    }//GEN-LAST:event_waveBackgroundColorButtonActionPerformed

    private void waveSelectionColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveSelectionColorButtonActionPerformed
        colorChooser.setColor(waveSelectionColorPanel.getBackground());
        JDialog dialog = JColorChooser.createDialog(this, resourceBundle.getString("JColorChooser.title"), true, colorChooser, (ActionEvent e) -> {
            setWaveSelectionColor(colorChooser.getColor());
            setModified(true);
        }, null);
        dialog.setVisible(true);
    }//GEN-LAST:event_waveSelectionColorButtonActionPerformed

    private void waveCursorColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveCursorColorButtonActionPerformed
        colorChooser.setColor(waveCursorColorPanel.getBackground());
        JDialog dialog = JColorChooser.createDialog(this, resourceBundle.getString("JColorChooser.title"), true, colorChooser, (ActionEvent e) -> {
            setWaveCursorColor(colorChooser.getColor());
            setModified(true);
        }, null);
        dialog.setVisible(true);
    }//GEN-LAST:event_waveCursorColorButtonActionPerformed

    private void fillCurrentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillCurrentButtonActionPerformed
        setWaveColorsFromArray(waveColorService.getCurrentWaveColors());
    }//GEN-LAST:event_fillCurrentButtonActionPerformed

    private void fillDefaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillDefaultButtonActionPerformed
        setDefaultAudioPanelColors();
    }//GEN-LAST:event_fillDefaultButtonActionPerformed

    private void waveFillColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveFillColorButtonActionPerformed
        colorChooser.setColor(waveFillColorPanel.getBackground());
        JDialog dialog = JColorChooser.createDialog(this, resourceBundle.getString("JColorChooser.title"), true, colorChooser, (ActionEvent e) -> {
            setWaveFillColor(colorChooser.getColor());
            setModified(true);
        }, null);
        dialog.setVisible(true);
    }//GEN-LAST:event_waveFillColorButtonActionPerformed

    private void waveCursorWaveColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveCursorWaveColorButtonActionPerformed
        colorChooser.setColor(waveCursorWaveColorPanel.getBackground());
        JDialog dialog = JColorChooser.createDialog(this, resourceBundle.getString("JColorChooser.title"), true, colorChooser, (ActionEvent e) -> {
            setWaveCursorWaveColor(colorChooser.getColor());
            setModified(true);
        }, null);
        dialog.setVisible(true);
    }//GEN-LAST:event_waveCursorWaveColorButtonActionPerformed

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            WindowUtils.invokeWindow(new WaveColorPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JColorChooser colorChooser;
    private javax.swing.JButton fillCurrentButton;
    private javax.swing.JButton fillDefaultButton;
    private javax.swing.JButton waveBackgroundColorButton;
    private javax.swing.JLabel waveBackgroundColorLabel;
    private javax.swing.JPanel waveBackgroundColorPanel;
    private javax.swing.JButton waveColorButton;
    private javax.swing.JLabel waveColorLabel;
    private javax.swing.JPanel waveColorPanel;
    private javax.swing.JButton waveCursorColorButton;
    private javax.swing.JLabel waveCursorColorLabel;
    private javax.swing.JPanel waveCursorColorPanel;
    private javax.swing.JButton waveCursorWaveColorButton;
    private javax.swing.JLabel waveCursorWaveColorLabel;
    private javax.swing.JPanel waveCursorWaveColorPanel;
    private javax.swing.JButton waveFillColorButton;
    private javax.swing.JLabel waveFillColorLabel;
    private javax.swing.JPanel waveFillColorPanel;
    private javax.swing.JButton waveSelectionColorButton;
    private javax.swing.JLabel waveSelectionColorLabel;
    private javax.swing.JPanel waveSelectionColorPanel;
    // End of variables declaration//GEN-END:variables

    public void setWaveColorsFromArray(Color[] colors) {
        setWaveColor(colors[0]);
        setWaveFillColor(colors[1]);
        setWaveCursorColor(colors[2]);
        setWaveCursorWaveColor(colors[3]);
        setWaveBackgroundColor(colors[4]);
        setWaveSelectionColor(colors[5]);
    }

    @Nonnull
    public Color[] getWaveColorsAsArray() {
        Color[] colors = new Color[6];
        colors[0] = getWaveColor();
        colors[1] = getWaveFillColor();
        colors[2] = getWaveCursorColor();
        colors[3] = getWaveCursorWaveColor();
        colors[4] = getWaveBackgroundColor();
        colors[5] = getWaveSelectionColor();
        return colors;
    }

    private void setModified(boolean modified) {
        if (optionsModifiedListener != null) {
            optionsModifiedListener.wasModified();
        }
    }

    @Override
    public void setOptionsModifiedListener(OptionsModifiedListener listener) {
        optionsModifiedListener = listener;
    }
}
