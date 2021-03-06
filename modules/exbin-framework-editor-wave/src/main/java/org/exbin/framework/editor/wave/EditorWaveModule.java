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

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ResourceBundle;
import javax.annotation.Nullable;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.editor.wave.options.impl.AudioDevicesOptionsImpl;
import org.exbin.framework.editor.wave.options.impl.WaveColorOptionsImpl;
import org.exbin.framework.editor.wave.options.gui.AudioDevicesOptionsPanel;
import org.exbin.framework.editor.wave.gui.AudioPanel;
import org.exbin.framework.editor.wave.gui.AudioStatusPanel;
import org.exbin.framework.editor.wave.options.gui.WaveColorOptionsPanel;
import org.exbin.framework.editor.wave.preferences.AudioDevicesPreferences;
import org.exbin.framework.editor.wave.preferences.WaveColorPreferences;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.action.api.MenuGroup;
import org.exbin.framework.gui.action.api.MenuPosition;
import org.exbin.framework.gui.action.api.NextToMode;
import org.exbin.framework.gui.action.api.PositionMode;
import org.exbin.framework.gui.action.api.SeparationMode;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.undo.api.GuiUndoModuleApi;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.editor.wave.service.WaveColorService;
import org.exbin.framework.editor.wave.service.impl.WaveColorServiceImpl;
import org.exbin.framework.gui.options.api.OptionsCapable;
import org.exbin.framework.gui.options.api.DefaultOptionsPage;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.action.api.GuiActionModuleApi;

/**
 * XBUP audio editor module.
 *
 * @version 0.2.1 2019/07/20
 * @author ExBin Project (http://exbin.org)
 */
public class EditorWaveModule implements XBApplicationModule {

    public static final String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(EditorWaveModule.class);
    public static final String AUDIO_MENU_ID = MODULE_ID + ".audioMenu";
    public static final String AUDIO_OPERATION_MENU_ID = MODULE_ID + ".audioOperationMenu";
    public static final String AUDIO_POPUP_MENU_ID = MODULE_ID + ".audioPopupMenu";
    public static final String DRAW_MODE_SUBMENU_ID = MODULE_ID + ".drawSubMenu";
    public static final String ZOOM_MODE_SUBMENU_ID = MODULE_ID + ".zoomSubMenu";
    public static final String TOOLS_SELECTION_MENU_GROUP_ID = MODULE_ID + ".toolsSelectionMenuGroup";

    public static final String XBS_FILE_TYPE = "XBWaveEditor.XBSFileFilter";

    public static final String WAVE_STATUS_BAR_ID = "waveStatusBar";

    private XBApplication application;
    private EditorProvider editorProvider;
    private AudioStatusPanel audioStatusPanel;
    private boolean playing = false;

    private ToolsOptionsHandler toolsOptionsHandler;
    private PropertiesHandler propertiesHandler;
    private AudioControlHandler audioControlHandler;
    private DrawingControlHandler drawingControlHandler;
    private ToolsSelectionHandler toolsSelectionHandler;
    private ZoomControlHandler zoomControlHandler;
    private AudioOperationHandler audioOperationHandler;

    public EditorWaveModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    public EditorProvider getEditorProvider() {
        if (editorProvider == null) {
            AudioPanel audioPanel = new AudioPanel();

            GuiUndoModuleApi undoModule = application.getModuleRepository().getModuleByInterface(GuiUndoModuleApi.class);
            audioPanel.setUndoHandler(undoModule.getUndoHandler());

            editorProvider = audioPanel;

            audioPanel.addStatusChangeListener(this::updateStatus);
            audioPanel.addWaveRepaintListener(this::updatePositionTime);

            audioPanel.attachCaretListener(new MouseMotionListener() {

                @Override
                public void mouseDragged(MouseEvent e) {
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    if (editorProvider == null) {
                        return;
                    }

                    updatePositionTime();
                }
            });

            audioPanel.setPopupMenu(createPopupMenu());
        }

        return editorProvider;
    }

    public void registerFileTypes() {
        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);

        String[] formats = new String[]{"wav", "aiff", "au"};
        for (String ext : formats) {
            if (ext.toLowerCase().equals(ext)) {
                fileModule.addFileType(new AudioFileType(ext));
            }
        }

        fileModule.addFileType(new XBSFileType());
    }

    private void updatePositionTime() {
        audioStatusPanel.setCurrentTime(((AudioPanel) editorProvider).getPositionTime());
    }

    private void updateStatus() {
        updatePositionTime();

        AudioPanel audioPanel = (AudioPanel) editorProvider;
        if (audioPanel.getIsPlaying() != playing) {
            playing = !playing;
            audioStatusPanel.setPlayButtonIcon(playing
                    ? new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/editor/wave/resources/images/actions/pause16.png"))
                    : new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/editor/wave/resources/images/actions/play16.png"))
            );
        }
    }

    public void registerStatusBar() {
        audioStatusPanel = new AudioStatusPanel(new AudioControlApi() {
            @Override
            public void performPlay() {
                ((AudioPanel) editorProvider).performPlay();
            }

            @Override
            public void performStop() {
                ((AudioPanel) editorProvider).performStop();
            }

            @Override
            public void setVolume(int volumeLevel) {
                ((AudioPanel) editorProvider).setVolume(volumeLevel);
            }
        });

        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        frameModule.registerStatusBar(MODULE_ID, WAVE_STATUS_BAR_ID, audioStatusPanel);
        frameModule.switchStatusBar(WAVE_STATUS_BAR_ID);
    }

    public void registerOptionsPanels() {
        GuiOptionsModuleApi optionsModule = application.getModuleRepository().getModuleByInterface(GuiOptionsModuleApi.class);
        WaveColorService waveColorService = new WaveColorServiceImpl(getEditorProvider());

        optionsModule.addOptionsPage(new DefaultOptionsPage<WaveColorOptionsImpl>() {
            @Override
            public OptionsCapable<WaveColorOptionsImpl> createPanel() {
                WaveColorOptionsPanel panel = new WaveColorOptionsPanel();
                panel.setWaveColorService(waveColorService);
                return panel;
            }

            @Override
            public ResourceBundle getResourceBundle() {
                return LanguageUtils.getResourceBundleByClass(WaveColorOptionsPanel.class);
            }

            @Override
            public WaveColorOptionsImpl createOptions() {
                return new WaveColorOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, WaveColorOptionsImpl options) {
                options.loadFromPreferences(new WaveColorPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, WaveColorOptionsImpl options) {
                options.saveToPreferences(new WaveColorPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(WaveColorOptionsImpl options) {
                if (options.isUseDefaultColors()) {
                    waveColorService.setCurrentWaveColors(waveColorService.getCurrentWaveColors());
                } else {
                    Color[] colors = new Color[6];
                    colors[0] = intToColor(options.getWaveColor());
                    colors[1] = intToColor(options.getWaveFillColor());
                    colors[2] = intToColor(options.getWaveCursorColor());
                    colors[3] = intToColor(options.getWaveCursorWaveColor());
                    colors[4] = intToColor(options.getWaveBackgroundColor());
                    colors[5] = intToColor(options.getWaveSelectionColor());
                    waveColorService.setCurrentWaveColors(colors);
                }
            }

            @Nullable
            private Color intToColor(@Nullable Integer intValue) {
                return intValue == null ? null : new Color(intValue);
            }
        });
        optionsModule.addOptionsPage(new DefaultOptionsPage<AudioDevicesOptionsImpl>() {
            @Override
            public OptionsCapable<AudioDevicesOptionsImpl> createPanel() {
                return new AudioDevicesOptionsPanel();
            }

            @Override
            public ResourceBundle getResourceBundle() {
                return LanguageUtils.getResourceBundleByClass(AudioDevicesOptionsPanel.class);
            }

            @Override
            public AudioDevicesOptionsImpl createOptions() {
                return new AudioDevicesOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, AudioDevicesOptionsImpl options) {
                options.loadFromPreferences(new AudioDevicesPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, AudioDevicesOptionsImpl options) {
                options.saveToPreferences(new AudioDevicesPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(AudioDevicesOptionsImpl options) {
                // TODO
            }
        });
    }

    public void registerToolsOptionsMenuActions() {
        getToolsOptionsHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, toolsOptionsHandler.getToolsSetColorAction(), new MenuPosition(PositionMode.MIDDLE));
    }

    public void registerToolsMenuActions() {
        getToolsSelectionHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuGroup(GuiFrameModuleApi.TOOLS_MENU_ID, new MenuGroup(TOOLS_SELECTION_MENU_GROUP_ID, new MenuPosition(PositionMode.TOP), SeparationMode.AROUND));
        actionModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, toolsSelectionHandler.getSelectionToolAction(), new MenuPosition(TOOLS_SELECTION_MENU_GROUP_ID));
        actionModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, toolsSelectionHandler.getPencilToolAction(), new MenuPosition(TOOLS_SELECTION_MENU_GROUP_ID));
    }

    public AudioStatusPanel getAudioStatusPanel() {
        return audioStatusPanel;
    }

    private PropertiesHandler getPropertiesHandler() {
        if (propertiesHandler == null) {
            propertiesHandler = new PropertiesHandler(application, (AudioPanel) getEditorProvider());
            propertiesHandler.init();
        }

        return propertiesHandler;
    }

    private AudioControlHandler getAudioControlHandler() {
        if (audioControlHandler == null) {
            audioControlHandler = new AudioControlHandler(application, (AudioPanel) getEditorProvider());
            audioControlHandler.init();
        }

        return audioControlHandler;
    }

    private AudioOperationHandler getAudioOperationHandler() {
        if (audioOperationHandler == null) {
            audioOperationHandler = new AudioOperationHandler(application, (AudioPanel) getEditorProvider());
            audioOperationHandler.init();
        }

        return audioOperationHandler;
    }

    private DrawingControlHandler getDrawingControlHandler() {
        if (drawingControlHandler == null) {
            drawingControlHandler = new DrawingControlHandler(application, (AudioPanel) getEditorProvider());
            drawingControlHandler.init();
        }

        return drawingControlHandler;
    }

    private ToolsSelectionHandler getToolsSelectionHandler() {
        if (toolsSelectionHandler == null) {
            toolsSelectionHandler = new ToolsSelectionHandler(application, (AudioPanel) getEditorProvider());
            toolsSelectionHandler.init();
        }

        return toolsSelectionHandler;
    }

    private ZoomControlHandler getZoomControlHandler() {
        if (zoomControlHandler == null) {
            zoomControlHandler = new ZoomControlHandler(application, (AudioPanel) getEditorProvider());
            zoomControlHandler.init();
        }

        return zoomControlHandler;
    }

    private ToolsOptionsHandler getToolsOptionsHandler() {
        if (toolsOptionsHandler == null) {
            toolsOptionsHandler = new ToolsOptionsHandler(application, (AudioPanel) getEditorProvider());
            toolsOptionsHandler.init();
        }

        return toolsOptionsHandler;
    }

    public void registerPropertiesMenu() {
        getPropertiesHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, propertiesHandler.getPropertiesAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerAudioMenu() {
        getAudioControlHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenu(AUDIO_MENU_ID, MODULE_ID);
        actionModule.registerMenuItem(GuiFrameModuleApi.MAIN_MENU_ID, MODULE_ID, AUDIO_MENU_ID, "Audio", new MenuPosition(NextToMode.AFTER, "View"));
        actionModule.registerMenuItem(AUDIO_MENU_ID, MODULE_ID, audioControlHandler.getPlayAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(AUDIO_MENU_ID, MODULE_ID, audioControlHandler.getStopAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerAudioOperationMenu() {
        getAudioOperationHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenu(AUDIO_OPERATION_MENU_ID, MODULE_ID);
        actionModule.registerMenuItem(AUDIO_MENU_ID, MODULE_ID, AUDIO_OPERATION_MENU_ID, "Operation", new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenuItem(AUDIO_OPERATION_MENU_ID, MODULE_ID, audioOperationHandler.getRevertAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerDrawingModeMenu() {
        getDrawingControlHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, DRAW_MODE_SUBMENU_ID, "Draw Mode", new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerZoomModeMenu() {
        getZoomControlHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, ZOOM_MODE_SUBMENU_ID, "Zoom", new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenu(ZOOM_MODE_SUBMENU_ID, MODULE_ID);
        actionModule.registerMenuItem(ZOOM_MODE_SUBMENU_ID, MODULE_ID, zoomControlHandler.getZoomUpAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(ZOOM_MODE_SUBMENU_ID, MODULE_ID, zoomControlHandler.getNormalZoomAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(ZOOM_MODE_SUBMENU_ID, MODULE_ID, zoomControlHandler.getZoomDownAction(), new MenuPosition(PositionMode.TOP));
    }

    public void bindZoomScrollWheel() {
        // ((AudioPanel) getEditorProvider()).
    }

    private JPopupMenu createPopupMenu() {
        getAudioControlHandler();
        getDrawingControlHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenu(AUDIO_POPUP_MENU_ID, MODULE_ID);
        actionModule.registerMenuItem(AUDIO_POPUP_MENU_ID, MODULE_ID, audioControlHandler.getPlayAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(AUDIO_POPUP_MENU_ID, MODULE_ID, audioControlHandler.getStopAction(), new MenuPosition(PositionMode.TOP));

        actionModule.registerClipboardMenuItems(AUDIO_POPUP_MENU_ID, MODULE_ID, SeparationMode.AROUND);
        actionModule.registerMenu(DRAW_MODE_SUBMENU_ID, MODULE_ID);
        actionModule.registerMenuItem(DRAW_MODE_SUBMENU_ID, MODULE_ID, drawingControlHandler.getDotsModeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(DRAW_MODE_SUBMENU_ID, MODULE_ID, drawingControlHandler.getLineModeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(DRAW_MODE_SUBMENU_ID, MODULE_ID, drawingControlHandler.getIntegralModeAction(), new MenuPosition(PositionMode.TOP));

        actionModule.registerMenuItem(AUDIO_POPUP_MENU_ID, MODULE_ID, DRAW_MODE_SUBMENU_ID, "Draw Mode", new MenuPosition(PositionMode.BOTTOM));
        JPopupMenu popupMenu = new JPopupMenu();
        actionModule.buildMenu(popupMenu, AUDIO_POPUP_MENU_ID);
        return popupMenu;
    }

    public class XBSFileType extends FileFilter implements FileType {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = getExtension(f);
            if (extension != null) {
                if (extension.length() < 3) {
                    return false;
                }
                return "xbs".contains(extension.substring(0, 3));
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "XBUP Sound Files (*.xbs*)";
        }

        @Override
        public String getFileTypeId() {
            return XBS_FILE_TYPE;
        }
    }

    /**
     * Gets the extension part of file name.
     *
     * @param file Source file
     * @return extension part of file name
     */
    public static String getExtension(File file) {
        String ext = null;
        String str = file.getName();
        int i = str.lastIndexOf('.');

        if (i > 0 && i < str.length() - 1) {
            ext = str.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
