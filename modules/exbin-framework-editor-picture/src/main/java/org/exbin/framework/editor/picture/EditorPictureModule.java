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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.editor.picture.gui.ImagePanel;
import org.exbin.framework.editor.picture.gui.ImageStatusPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.action.api.MenuPosition;
import org.exbin.framework.gui.action.api.NextToMode;
import org.exbin.framework.gui.action.api.PositionMode;
import org.exbin.framework.gui.action.api.SeparationMode;
import org.exbin.framework.gui.undo.api.GuiUndoModuleApi;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.gui.action.api.GuiActionModuleApi;

/**
 * XBUP picture editor module.
 *
 * @version 0.2.0 2017/01/19
 * @author ExBin Project (http://exbin.org)
 */
public class EditorPictureModule implements XBApplicationModule {

    public static final String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(EditorPictureModule.class);
    public static final String XBPFILETYPE = "XBPictureEditor.XBPFileType";
    public static final String ZOOM_MODE_SUBMENU_ID = MODULE_ID + ".zoomSubMenu";
    public static final String PICTURE_MENU_ID = MODULE_ID + ".pictureMenu";
    public static final String PICTURE_OPERATION_MENU_ID = MODULE_ID + ".pictureOperationMenu";
    public static final String PICTURE_POPUP_MENU_ID = MODULE_ID + ".picturePopupMenu";

    public static final String IMAGE_STATUS_BAR_ID = "imageStatusBar";

    private XBApplication application;
    private EditorProvider editorProvider;
    private ImageStatusPanel imageStatusPanel;

    private ToolsOptionsHandler toolsOptionsHandler;
    private PropertiesHandler propertiesHandler;
    private PrintHandler printHandler;
    private ZoomControlHandler zoomControlHandler;
    private PictureOperationHandler pictureOperationHandler;

    public EditorPictureModule() {
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
            ImagePanel imagePanel = new ImagePanel();

            GuiUndoModuleApi undoModule = application.getModuleRepository().getModuleByInterface(GuiUndoModuleApi.class);
            // imagePanel.setUndoHandler(undoModule.getUndoHandler());

            editorProvider = imagePanel;

            imagePanel.attachCaretListener(new MouseMotionListener() {

                @Override
                public void mouseDragged(MouseEvent e) {
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    if (editorProvider == null) {
                        return;
                    }

                    updateCurrentPosition();
                }
            });
            imagePanel.setPopupMenu(createPopupMenu());
        }

        return editorProvider;
    }

    public void registerFileTypes() {
        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
        String[] formats = ImageIO.getReaderFormatNames();
        for (String ext : formats) {
            if (ext.toLowerCase().equals(ext)) {
                fileModule.addFileType(new PictureFileType(ext));
            }
        }

        fileModule.addFileType(new XBPFileType());
    }

    private void updateCurrentPosition() {
        if (imageStatusPanel != null) {
            Point mousePosition = ((ImagePanel) editorProvider).getMousePosition();
            double scale = ((ImagePanel) editorProvider).getScale();
            if (mousePosition != null) {
                imageStatusPanel.setCurrentPosition(new Point((int) (mousePosition.x * scale), (int) (mousePosition.y * scale)));
            }
        }
    }

    public void registerStatusBar() {
        imageStatusPanel = new ImageStatusPanel(new ImageControlApi() {
            @Override
            public void editSelection() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        frameModule.registerStatusBar(MODULE_ID, IMAGE_STATUS_BAR_ID, imageStatusPanel);
        frameModule.switchStatusBar(IMAGE_STATUS_BAR_ID);
        ((ImagePanel) getEditorProvider()).registerImageStatus(imageStatusPanel);
    }

    public void registerPropertiesMenu() {
        getPropertiesHandler();
        GuiActionModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, propertiesHandler.getPropertiesAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerPrintMenu() {
        getPrintHandler();
        GuiActionModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, printHandler.getPrintAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    private PictureOperationHandler getPictureOperationHandler() {
        if (pictureOperationHandler == null) {
            pictureOperationHandler = new PictureOperationHandler(application, (ImagePanel) getEditorProvider());
            pictureOperationHandler.init();
        }

        return pictureOperationHandler;
    }

    public void registerOptionsMenuPanels() {
//        getEncodingsHandler();
//        JMenu toolsEncodingMenu = encodingsHandler.getToolsEncodingMenu();
//        encodingsHandler.encodingsRebuild();

//        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
//        menuModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, encodingsHandler.getToolsEncodingMenu(), new MenuPosition(PositionMode.TOP_LAST));
    }

    public void registerOptionsPanels() {
//        GuiOptionsModuleApi optionsModule = application.getModuleRepository().getModuleByInterface(GuiOptionsModuleApi.class);
//        WaveColorPanelApi textColorPanelFrame = new WaveColorPanelApi() {
//            @Override
//            public Color[] getCurrentWaveColors() {
//                return ((AudioPanel) getEditorProvider()).getAudioPanelColors();
//            }
//
//            @Override
//            public Color[] getDefaultWaveColors() {
//                return ((AudioPanel) getEditorProvider()).getDefaultColors();
//            }
//
//            @Override
//            public void setCurrentWaveColors(Color[] colors) {
//                ((AudioPanel) getEditorProvider()).setAudioPanelColors(colors);
//            }
//        };
    }

    private JPopupMenu createPopupMenu() {
        GuiActionModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        menuModule.registerMenu(PICTURE_POPUP_MENU_ID, MODULE_ID);
        menuModule.registerClipboardMenuItems(PICTURE_POPUP_MENU_ID, MODULE_ID, SeparationMode.AROUND);
        JPopupMenu popupMenu = new JPopupMenu();
        menuModule.buildMenu(popupMenu, PICTURE_POPUP_MENU_ID);
        return popupMenu;
    }

    public void registerToolsOptionsMenuActions() {
        getToolsOptionsHandler();
        GuiActionModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, toolsOptionsHandler.getToolsSetColorAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerZoomModeMenu() {
        getZoomControlHandler();
        GuiActionModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, ZOOM_MODE_SUBMENU_ID, "Zoom", new MenuPosition(PositionMode.BOTTOM));
        menuModule.registerMenu(ZOOM_MODE_SUBMENU_ID, MODULE_ID);
        menuModule.registerMenuItem(ZOOM_MODE_SUBMENU_ID, MODULE_ID, zoomControlHandler.getZoomUpAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(ZOOM_MODE_SUBMENU_ID, MODULE_ID, zoomControlHandler.getNormalZoomAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(ZOOM_MODE_SUBMENU_ID, MODULE_ID, zoomControlHandler.getZoomDownAction(), new MenuPosition(PositionMode.TOP));
    }

    private PropertiesHandler getPropertiesHandler() {
        if (propertiesHandler == null) {
            propertiesHandler = new PropertiesHandler(application, (ImagePanel) getEditorProvider());
            propertiesHandler.init();
        }

        return propertiesHandler;
    }

    private ToolsOptionsHandler getToolsOptionsHandler() {
        if (toolsOptionsHandler == null) {
            toolsOptionsHandler = new ToolsOptionsHandler(application, (ImagePanel) getEditorProvider());
            toolsOptionsHandler.init();
        }

        return toolsOptionsHandler;
    }

    private PrintHandler getPrintHandler() {
        if (printHandler == null) {
            printHandler = new PrintHandler(application, (ImagePanel) getEditorProvider());
            printHandler.init();
        }

        return printHandler;
    }

    private ZoomControlHandler getZoomControlHandler() {
        if (zoomControlHandler == null) {
            zoomControlHandler = new ZoomControlHandler(application, (ImagePanel) getEditorProvider());
            zoomControlHandler.init();
        }

        return zoomControlHandler;
    }

    public void registerPictureMenu() {
        GuiActionModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        menuModule.registerMenu(PICTURE_MENU_ID, MODULE_ID);
        menuModule.registerMenuItem(GuiFrameModuleApi.MAIN_MENU_ID, MODULE_ID, PICTURE_MENU_ID, "Picture", new MenuPosition(NextToMode.AFTER, "View"));
    }

    public void registerPictureOperationMenu() {
        getPictureOperationHandler();
        GuiActionModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        menuModule.registerMenuItem(PICTURE_MENU_ID, MODULE_ID, pictureOperationHandler.getRevertAction(), new MenuPosition(PositionMode.TOP));
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

    public class XBPFileType extends FileFilter implements FileType {

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
                return "xbp".contains(extension.substring(0, 3));
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "XBUP Picture Files (*.xbp*)";
        }

        @Override
        public String getFileTypeId() {
            return XBPFILETYPE;
        }
    }
}
