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
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.editor.text.options.TextAppearanceOptions;
import org.exbin.framework.editor.text.options.TextColorOptions;
import org.exbin.framework.editor.text.options.TextEncodingOptions;
import org.exbin.framework.editor.text.options.TextFontOptions;
import org.exbin.framework.editor.text.panel.AddEncodingPanel;
import org.exbin.framework.editor.text.options.panel.TextAppearanceOptionsPanel;
import org.exbin.framework.editor.text.options.panel.TextColorOptionsPanel;
import org.exbin.framework.editor.text.options.panel.TextEncodingOptionsPanel;
import org.exbin.framework.editor.text.options.panel.TextFontOptionsPanel;
import org.exbin.framework.editor.text.panel.TextFontPanel;
import org.exbin.framework.editor.text.panel.TextPanel;
import org.exbin.framework.editor.text.panel.TextStatusPanel;
import org.exbin.framework.editor.text.preferences.TextAppearancePreferences;
import org.exbin.framework.editor.text.preferences.TextColorPreferences;
import org.exbin.framework.editor.text.preferences.TextEncodingPreferences;
import org.exbin.framework.editor.text.preferences.TextFontPreferences;
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
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.handler.OptionsControlHandler;
import org.exbin.framework.gui.utils.panel.DefaultControlPanel;
import org.exbin.framework.gui.utils.panel.OptionsControlPanel;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.editor.text.service.TextAppearanceService;
import org.exbin.framework.editor.text.service.TextEncodingService;
import org.exbin.framework.editor.text.service.TextColorService;
import org.exbin.framework.editor.text.service.TextFontService;
import org.exbin.framework.editor.text.service.impl.TextEncodingServiceImpl;
import org.exbin.framework.gui.options.api.OptionsCapable;
import org.exbin.framework.gui.options.api.DefaultOptionsPage;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Text editor module.
 *
 * @version 0.2.1 2019/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
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
    private GoToPositionHandler goToLineHandler;
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

    @Nonnull
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
        encodingsHandler.rebuildEncodings();

        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, encodingsHandler.getToolsEncodingMenu(), new MenuPosition(PositionMode.TOP_LAST));
    }

    public void registerOptionsPanels() {
        GuiOptionsModuleApi optionsModule = application.getModuleRepository().getModuleByInterface(GuiOptionsModuleApi.class);
        TextColorService textColorService = new TextColorService() {
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

        optionsModule.addOptionsPage(new DefaultOptionsPage<TextColorOptions>() {
            private TextColorOptionsPanel panel;

            @Override
            public OptionsCapable<TextColorOptions> createPanel() {
                if (panel == null) {
                    panel = new TextColorOptionsPanel();
                    panel.setTextColorService(textColorService);
                }
                return panel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return LanguageUtils.getResourceBundleByClass(TextColorOptionsPanel.class);
            }

            @Override
            public TextColorOptions createOptions() {
                return new TextColorOptions();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, TextColorOptions options) {
                options.loadFromParameters(new TextColorPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, TextColorOptions options) {
                options.saveToParameters(new TextColorPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(TextColorOptions options) {
                if (options.isUseDefaultColors()) {
                    textColorService.setCurrentTextColors(textColorService.getDefaultTextColors());
                } else {
                    Color[] colors = new Color[5];
                    colors[0] = intToColor(options.getTextColor());
                    colors[1] = intToColor(options.getTextBackgroundColor());
                    colors[2] = intToColor(options.getSelectionTextColor());
                    colors[3] = intToColor(options.getSelectionBackgroundColor());
                    colors[4] = intToColor(options.getFoundBackgroundColor());
                    textColorService.setCurrentTextColors(colors);
                }
            }

            @Nullable
            private Color intToColor(@Nullable Integer intValue) {
                return intValue == null ? null : new Color(intValue);
            }
        });

        TextFontService textFontService = new TextFontService() {
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

        optionsModule.addOptionsPage(new DefaultOptionsPage<TextFontOptions>() {

            private TextFontOptionsPanel panel;

            @Override
            public OptionsCapable<TextFontOptions> createPanel() {
                if (panel == null) {
                    panel = new TextFontOptionsPanel();
                    panel.setTextFontService(textFontService);
                    panel.setFontChangeAction(new TextFontOptionsPanel.FontChangeAction() {
                        @Override
                        public Font changeFont(Font currentFont) {
                            final Result result = new Result();
                            GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                            final TextFontPanel fontPanel = new TextFontPanel();
                            fontPanel.setStoredFont(currentFont);
                            OptionsControlPanel controlPanel = new OptionsControlPanel();
                            JPanel dialogPanel = WindowUtils.createDialogPanel(fontPanel, controlPanel);
                            final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                            WindowUtils.addHeaderPanel(dialog.getWindow(), fontPanel.getClass(), fontPanel.getResourceBundle(), controlPanel);
                            frameModule.setDialogTitle(dialog, fontPanel.getResourceBundle());
                            controlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
                                if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                                    if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                                        TextFontPreferences textFontParameters = new TextFontPreferences(application.getAppPreferences());
                                        textFontParameters.setUseDefaultFont(true);
                                        textFontParameters.setFont(fontPanel.getStoredFont());
                                    }
                                    result.font = fontPanel.getStoredFont();
                                }

                                dialog.close();
                            });
                            dialog.showCentered(frameModule.getFrame());
                            dialog.dispose();

                            return result.font;
                        }

                        class Result {

                            Font font;
                        }
                    });
                }
                return panel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return LanguageUtils.getResourceBundleByClass(TextFontOptionsPanel.class);
            }

            @Override
            public TextFontOptions createOptions() {
                return new TextFontOptions();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, TextFontOptions options) {
                options.loadFromParameters(new TextFontPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, TextFontOptions options) {
                options.saveToParameters(new TextFontPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(TextFontOptions options) {
                if (options.isUseDefaultFont()) {
                    textFontService.setCurrentFont(textFontService.getDefaultFont());
                } else {
                    textFontService.setCurrentFont(options.getFont(textFontService.getDefaultFont()));
                }
            }
        });

        TextEncodingService textEncodingService = new TextEncodingServiceImpl();
        textEncodingService.setEncodingChangeListener(new TextEncodingService.EncodingChangeListener() {
            @Override
            public void encodingListChanged() {
                getEncodingsHandler().rebuildEncodings();
            }

            @Override
            public void selectedEncodingChanged() {
                ((TextPanel) getEditorProvider()).setCharset(Charset.forName(textEncodingService.getSelectedEncoding()));
            }
        });

        TextAppearanceService textAppearanceService;
        textAppearanceService = new TextAppearanceService() {
            @Override
            public boolean getWordWrapMode() {
                return ((TextPanel) getEditorProvider()).getWordWrapMode();
            }

            @Override
            public void setWordWrapMode(boolean mode) {
                ((TextPanel) getEditorProvider()).setWordWrapMode(mode);
            }
        };

        optionsModule.addOptionsPage(new DefaultOptionsPage<TextEncodingOptions>() {
            private TextEncodingOptionsPanel panel;

            @Override
            public TextEncodingOptionsPanel createPanel() {
                if (panel == null) {
                    panel = new TextEncodingOptionsPanel();
                    panel.setAddEncodingsOperation((List<String> usedEncodings) -> {
                        final List<String> result = new ArrayList<>();
                        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                        final AddEncodingPanel addEncodingPanel = new AddEncodingPanel();
                        addEncodingPanel.setUsedEncodings(usedEncodings);
                        DefaultControlPanel controlPanel = new DefaultControlPanel(addEncodingPanel.getResourceBundle());
                        JPanel dialogPanel = WindowUtils.createDialogPanel(addEncodingPanel, controlPanel);
                        final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                            if (actionType == DefaultControlHandler.ControlActionType.OK) {
                                result.addAll(addEncodingPanel.getEncodings());
                            }

                            dialog.close();
                        });
                        frameModule.setDialogTitle(dialog, addEncodingPanel.getResourceBundle());
                        dialog.showCentered(frameModule.getFrame());
                        dialog.dispose();
                        return result;
                    });
                }

                return panel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return LanguageUtils.getResourceBundleByClass(TextEncodingOptionsPanel.class);
            }

            @Override
            public TextEncodingOptions createOptions() {
                return new TextEncodingOptions();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, TextEncodingOptions options) {
                options.loadFromParameters(new TextEncodingPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, TextEncodingOptions options) {
                options.saveToParameters(new TextEncodingPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(TextEncodingOptions options) {
                textEncodingService.setSelectedEncoding(options.getSelectedEncoding());
                textEncodingService.setEncodings(options.getEncodings());
            }
        });

        optionsModule.extendAppearanceOptionsPage(new DefaultOptionsPage<TextAppearanceOptions>() {
            private TextAppearanceOptionsPanel panel;

            @Override
            public OptionsCapable<TextAppearanceOptions> createPanel() {
                if (panel == null) {
                    panel = new TextAppearanceOptionsPanel();
                }
                return panel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return LanguageUtils.getResourceBundleByClass(TextAppearanceOptionsPanel.class);
            }

            @Override
            public TextAppearanceOptions createOptions() {
                return new TextAppearanceOptions();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, TextAppearanceOptions options) {
                options.loadFromParameters(new TextAppearancePreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, TextAppearanceOptions options) {
                options.saveToParameters(new TextAppearancePreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(TextAppearanceOptions options) {
                textAppearanceService.setWordWrapMode(options.isWordWrapping());
            }
        });
    }

    public void registerWordWrapping() {
        getWordWrappingHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class
        );
        menuModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, wordWrappingHandler.getViewWordWrapAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerGoToLine() {
        getGoToLineHandler();
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class
        );
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
            encodingsHandler = new EncodingsHandler(); // (TextPanel) getEditorProvider(), 
            if (textStatusPanel != null) {
                encodingsHandler.setTextEncodingStatus(textStatusPanel);
            }
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

    private GoToPositionHandler getGoToLineHandler() {
        if (goToLineHandler == null) {
            goToLineHandler = new GoToPositionHandler(application, (TextPanel) getEditorProvider());
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
        encodingsHandler.loadFromPreferences(new TextEncodingPreferences(preferences));
    }

    @ParametersAreNonnullByDefault
    public class XBTFileType extends FileFilter implements FileType {

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            String extension = getExtension(file);
            if (extension != null) {
                if (extension.length() < 3) {
                    return false;
                }
                return "xbt".contains(extension.substring(0, 3));
            }
            return false;
        }

        @Nonnull
        @Override
        public String getDescription() {
            return "XBUP Text Files (*.xbt*)";
        }

        @Nonnull
        @Override
        public String getFileTypeId() {
            return XBT_FILE_TYPE;
        }
    }

    @ParametersAreNonnullByDefault
    public class TXTFileType extends FileFilter implements FileType {

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            String extension = getExtension(file);
            if (extension != null) {
                return "txt".equals(extension);
            }
            return false;
        }

        @Nonnull
        @Override
        public String getDescription() {
            return "Text Files (*.txt)";
        }

        @Nonnull
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
    @Nullable
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
