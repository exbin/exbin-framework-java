/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.editor.text;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.editor.text.panel.AddEncodingPanel;
import org.exbin.framework.editor.text.panel.TextAppearanceOptionsPanel;
import org.exbin.framework.editor.text.panel.TextAppearanceOptionsPanelApi;
import org.exbin.framework.editor.text.panel.TextColorOptionsPanel;
import org.exbin.framework.editor.text.panel.TextColorPanelApi;
import org.exbin.framework.editor.text.panel.TextEncodingOptionsPanel;
import org.exbin.framework.editor.text.panel.TextEncodingPanel;
import org.exbin.framework.editor.text.panel.TextEncodingPanelApi;
import org.exbin.framework.editor.text.panel.TextFontOptionsPanel;
import org.exbin.framework.editor.text.panel.TextFontPanel;
import org.exbin.framework.editor.text.panel.TextFontPanelApi;
import org.exbin.framework.editor.text.panel.TextPanel;
import org.exbin.framework.editor.text.panel.TextStatusPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.menu.api.MenuGroup;
import org.exbin.framework.gui.menu.api.MenuPosition;
import org.exbin.framework.gui.menu.api.PositionMode;
import org.exbin.framework.gui.menu.api.SeparationMode;
import org.exbin.framework.gui.menu.api.ToolBarGroup;
import org.exbin.framework.gui.menu.api.ToolBarPosition;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.handler.OptionsControlHandler;
import org.exbin.framework.gui.utils.panel.DefaultControlPanel;
import org.exbin.framework.gui.utils.panel.OptionsControlPanel;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Text editor module.
 *
 * @version 0.2.0 2017/01/06
 * @author ExBin Project (http://exbin.org)
 */
public class EditorTextModule implements XBApplicationModule {

    public static final String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(EditorTextModule.class);

    private static final String EDIT_FIND_MENU_GROUP_ID = MODULE_ID + ".editFindMenuGroup";
    private static final String EDIT_FIND_TOOL_BAR_GROUP_ID = MODULE_ID + ".editFindToolBarGroup";

    public static final String XBT_FILE_TYPE = "XBTextEditor.XBTFileType";
    public static final String TXT_FILE_TYPE = "XBTextEditor.TXTFileType";

    public static final String TEXT_STATUS_BAR_ID = "textStatusBar";

    private XBApplication application;
    private EditorProvider editorProvider;
    private TextStatusPanel textStatusPanel;

    private FindReplaceHandler findReplaceHandler;
    private ToolsOptionsHandler toolsOptionsHandler;
    private EncodingsHandler encodingsHandler;
    private WordWrappingHandler wordWrappingHandler;
    private GoToLineHandler goToLineHandler;
    private PropertiesHandler propertiesHandler;
    private PrintHandler printHandler;

    public EditorTextModule() {
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
            editorProvider = new TextPanel();
        }

        return editorProvider;
    }

    public void registerFileTypes() {
        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
        fileModule.addFileType(new TXTFileType());
        fileModule.addFileType(new XBTFileType());
    }

    public void registerStatusBar() {
        textStatusPanel = new TextStatusPanel();
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        frameModule.registerStatusBar(MODULE_ID, TEXT_STATUS_BAR_ID, textStatusPanel);
        frameModule.switchStatusBar(TEXT_STATUS_BAR_ID);
        ((TextPanel) getEditorProvider()).registerTextStatus(textStatusPanel);
        if (encodingsHandler != null) {
            encodingsHandler.setTextEncodingStatus(textStatusPanel);
        }
    }

    public void registerOptionsMenuPanels() {
        getEncodingsHandler();
        encodingsHandler.encodingsRebuild();

        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, encodingsHandler.getToolsEncodingMenu(), new MenuPosition(PositionMode.TOP_LAST));
    }

    public void registerOptionsPanels() {
        GuiOptionsModuleApi optionsModule = application.getModuleRepository().getModuleByInterface(GuiOptionsModuleApi.class);
        TextColorPanelApi textColorPanelFrame = new TextColorPanelApi() {
            @Override
            public Color[] getCurrentTextColors() {
                return ((TextPanel) getEditorProvider()).getCurrentColors();
            }

            @Override
            public Color[] getDefaultTextColors() {
                return ((TextPanel) getEditorProvider()).getDefaultColors();
            }

            @Override
            public void setCurrentTextColors(Color[] colors) {
                ((TextPanel) getEditorProvider()).setCurrentColors(colors);
            }
        };

        optionsModule.addOptionsPanel(new TextColorOptionsPanel(textColorPanelFrame));

        TextFontPanelApi textFontPanelFrame = new TextFontPanelApi() {
            @Override
            public Font getCurrentFont() {
                return ((TextPanel) getEditorProvider()).getCurrentFont();
            }

            @Override
            public Font getDefaultFont() {
                return ((TextPanel) getEditorProvider()).getDefaultFont();
            }

            @Override
            public void setCurrentFont(Font font) {
                ((TextPanel) getEditorProvider()).setCurrentFont(font);
            }
        };

        TextFontOptionsPanel textFontOptionsPanel = new TextFontOptionsPanel(textFontPanelFrame);
        textFontOptionsPanel.setFontChangeAction(new TextFontOptionsPanel.FontChangeAction() {
            @Override
            public Font changeFont(Font currentFont) {
                final Result result = new Result();
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                final TextFontPanel fontPanel = new TextFontPanel();
                fontPanel.setStoredFont(currentFont);
                OptionsControlPanel controlPanel = new OptionsControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(fontPanel, controlPanel);
                final JDialog dialog = frameModule.createDialog(dialogPanel);
                WindowUtils.addHeaderPanel(dialog, fontPanel.getResourceBundle());
                frameModule.setDialogTitle(dialog, fontPanel.getResourceBundle());
                controlPanel.setHandler(new OptionsControlHandler() {
                    @Override
                    public void controlActionPerformed(OptionsControlHandler.ControlActionType actionType) {
                        if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                            if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                                fontPanel.saveToPreferences(application.getAppPreferences());
                            }
                            result.font = fontPanel.getStoredFont();
                        }

                        WindowUtils.closeWindow(dialog);
                    }
                });
                WindowUtils.assignGlobalKeyListener(dialog, controlPanel.createOkCancelListener());
                dialog.setLocationRelativeTo(dialog.getParent());
                dialog.setVisible(true);

                return result.font;
            }

            class Result {

                Font font;
            }
        });
        optionsModule.addOptionsPanel(textFontOptionsPanel);

        TextEncodingPanelApi textEncodingPanelApi = new TextEncodingPanelApi() {
            @Override
            public List<String> getEncodings() {
                return getEncodingsHandler().getEncodings();
            }

            @Override
            public String getSelectedEncoding() {
                return ((TextPanel) getEditorProvider()).getCharset().name();
            }

            @Override
            public void setEncodings(List<String> encodings) {
                getEncodingsHandler().setEncodings(encodings);
                getEncodingsHandler().encodingsRebuild();
            }

            @Override
            public void setSelectedEncoding(String encoding) {
                if (encoding != null) {
                    ((TextPanel) getEditorProvider()).setCharset(Charset.forName(encoding));
                }
            }
        };

        TextAppearanceOptionsPanelApi textAppearanceOptionsPanelApi;
        textAppearanceOptionsPanelApi = new TextAppearanceOptionsPanelApi() {
            @Override
            public boolean getWordWrapMode() {
                return ((TextPanel) getEditorProvider()).getWordWrapMode();
            }

            @Override
            public void setWordWrapMode(boolean mode) {
                ((TextPanel) getEditorProvider()).setWordWrapMode(mode);
            }
        };

        TextEncodingOptionsPanel textEncodingOptionsPanel = new TextEncodingOptionsPanel(textEncodingPanelApi);
        textEncodingOptionsPanel.setAddEncodingsOperation(new TextEncodingPanel.AddEncodingsOperation() {
            @Override
            public List<String> run(List<String> usedEncodings) {
                final List<String> result = new ArrayList<>();
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                final AddEncodingPanel addEncodingPanel = new AddEncodingPanel();
                addEncodingPanel.setUsedEncodings(usedEncodings);
                DefaultControlPanel controlPanel = new DefaultControlPanel(addEncodingPanel.getResourceBundle());
                JPanel dialogPanel = WindowUtils.createDialogPanel(addEncodingPanel, controlPanel);
                final JDialog addEncodingDialog = frameModule.createDialog(dialogPanel);
                controlPanel.setHandler(new DefaultControlHandler() {
                    @Override
                    public void controlActionPerformed(DefaultControlHandler.ControlActionType actionType) {
                        if (actionType == DefaultControlHandler.ControlActionType.OK) {
                            result.addAll(addEncodingPanel.getEncodings());
                        }

                        WindowUtils.closeWindow(addEncodingDialog);
                    }
                });
                frameModule.setDialogTitle(addEncodingDialog, addEncodingPanel.getResourceBundle());
                WindowUtils.assignGlobalKeyListener(addEncodingDialog, controlPanel.createOkCancelListener());
                addEncodingDialog.setLocationRelativeTo(addEncodingDialog.getParent());
                addEncodingDialog.setVisible(true);
                return result;
            }
        });
        optionsModule.addOptionsPanel(textEncodingOptionsPanel);
        optionsModule.extendAppearanceOptionsPanel(new TextAppearanceOptionsPanel(textAppearanceOptionsPanelApi));
    }

    public void registerWordWrapping() {
        getWordWrappingHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, wordWrappingHandler.getViewWordWrapAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerGoToLine() {
        getGoToLineHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, goToLineHandler.getGoToLineAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public TextStatusPanel getTextStatusPanel() {
        return textStatusPanel;
    }

    private FindReplaceHandler getFindReplaceHandler() {
        if (findReplaceHandler == null) {
            findReplaceHandler = new FindReplaceHandler(application, (TextPanel) getEditorProvider());
            findReplaceHandler.init();
        }

        return findReplaceHandler;
    }

    private ToolsOptionsHandler getToolsOptionsHandler() {
        if (toolsOptionsHandler == null) {
            toolsOptionsHandler = new ToolsOptionsHandler(application, (TextPanel) getEditorProvider());
            toolsOptionsHandler.init();
        }

        return toolsOptionsHandler;
    }

    private EncodingsHandler getEncodingsHandler() {
        if (encodingsHandler == null) {
            encodingsHandler = new EncodingsHandler(application, (TextPanel) getEditorProvider(), getTextStatusPanel());
            encodingsHandler.init();
        }

        return encodingsHandler;
    }

    private WordWrappingHandler getWordWrappingHandler() {
        if (wordWrappingHandler == null) {
            wordWrappingHandler = new WordWrappingHandler(application, (TextPanel) getEditorProvider());
            wordWrappingHandler.init();
        }

        return wordWrappingHandler;
    }

    private GoToLineHandler getGoToLineHandler() {
        if (goToLineHandler == null) {
            goToLineHandler = new GoToLineHandler(application, (TextPanel) getEditorProvider());
            goToLineHandler.init();
        }

        return goToLineHandler;
    }

    private PropertiesHandler getPropertiesHandler() {
        if (propertiesHandler == null) {
            propertiesHandler = new PropertiesHandler(application, (TextPanel) getEditorProvider());
            propertiesHandler.init();
        }

        return propertiesHandler;
    }

    private PrintHandler getPrintHandler() {
        if (printHandler == null) {
            printHandler = new PrintHandler(application, (TextPanel) getEditorProvider());
            printHandler.init();
        }

        return printHandler;
    }

    public void registerEditFindMenuActions() {
        getFindReplaceHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuGroup(GuiFrameModuleApi.EDIT_MENU_ID, new MenuGroup(EDIT_FIND_MENU_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        menuModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, findReplaceHandler.getEditFindAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
        menuModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, findReplaceHandler.getEditFindAgainAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
        menuModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, findReplaceHandler.getEditReplaceAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
    }

    public void registerEditFindToolBarActions() {
        getFindReplaceHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerToolBarGroup(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(EDIT_FIND_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        menuModule.registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, findReplaceHandler.getEditFindAction(), new ToolBarPosition(EDIT_FIND_TOOL_BAR_GROUP_ID));
    }

    public void registerToolsOptionsMenuActions() {
        getToolsOptionsHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, toolsOptionsHandler.getToolsSetFontAction(), new MenuPosition(PositionMode.TOP));
        menuModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, toolsOptionsHandler.getToolsSetColorAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerPropertiesMenu() {
        getPropertiesHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, propertiesHandler.getPropertiesAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerPrintMenu() {
        getPrintHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, printHandler.getPrintAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void loadFromPreferences(Preferences preferences) {
        encodingsHandler.loadFromPreferences(preferences);
    }

    public class XBTFileType extends FileFilter implements FileType {

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
                return "xbt".contains(extension.substring(0, 3));
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "XBUP Text Files (*.xbt*)";
        }

        @Override
        public String getFileTypeId() {
            return XBT_FILE_TYPE;
        }
    }

    public class TXTFileType extends FileFilter implements FileType {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = getExtension(f);
            if (extension != null) {
                return "txt".equals(extension);
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Text Files (*.txt)";
        }

        @Override
        public String getFileTypeId() {
            return TXT_FILE_TYPE;
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
