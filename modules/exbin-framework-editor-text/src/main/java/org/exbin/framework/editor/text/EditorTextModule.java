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
import org.exbin.framework.editor.text.options.impl.TextAppearanceOptionsImpl;
import org.exbin.framework.editor.text.options.impl.TextColorOptionsImpl;
import org.exbin.framework.editor.text.options.impl.TextEncodingOptionsImpl;
import org.exbin.framework.editor.text.options.impl.TextFontOptionsImpl;
import org.exbin.framework.editor.text.gui.AddEncodingPanel;
import org.exbin.framework.editor.text.options.gui.TextAppearanceOptionsPanel;
import org.exbin.framework.editor.text.options.gui.TextColorOptionsPanel;
import org.exbin.framework.editor.text.options.gui.TextEncodingOptionsPanel;
import org.exbin.framework.editor.text.options.gui.TextFontOptionsPanel;
import org.exbin.framework.editor.text.gui.TextFontPanel;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.editor.text.gui.TextStatusPanel;
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
import org.exbin.framework.gui.utils.gui.DefaultControlPanel;
import org.exbin.framework.gui.utils.gui.OptionsControlPanel;
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

        optionsModule.addOptionsPage(new DefaultOptionsPage<TextColorOptionsImpl>() {
            private TextColorOptionsPanel panel;

            @Override
            public OptionsCapable<TextColorOptionsImpl> createPanel() {
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
            public TextColorOptionsImpl createOptions() {
                return new TextColorOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, TextColorOptionsImpl options) {
                options.loadFromPreferences(new TextColorPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, TextColorOptionsImpl options) {
                options.saveToPreferences(new TextColorPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(TextColorOptionsImpl options) {
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

        optionsModule.addOptionsPage(new DefaultOptionsPage<TextFontOptionsImpl>() {

            private TextFontOptionsPanel panel;

            @Override
            public OptionsCapable<TextFontOptionsImpl> createPanel() {
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
                            DefaultControlPanel controlPanel = new DefaultControlPanel();
                            JPanel dialogPanel = WindowUtils.createDialogPanel(fontPanel, controlPanel);
                            final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                            WindowUtils.addHeaderPanel(dialog.getWindow(), fontPanel.getClass(), fontPanel.getResourceBundle());
                            frameModule.setDialogTitle(dialog, fontPanel.getResourceBundle());
                            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                                    if (actionType == DefaultControlHandler.ControlActionType.OK) {
                                        TextFontPreferences textFontParameters = new TextFontPreferences(application.getAppPreferences());
                                        textFontParameters.setUseDefaultFont(true);
                                        textFontParameters.setFont(fontPanel.getStoredFont());
                                    }
                                    result.font = fontPanel.getStoredFont();
                                }

                                dialog.close();
                                dialog.dispose();
                            });
                            dialog.showCentered(frameModule.getFrame());

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
            public TextFontOptionsImpl createOptions() {
                return new TextFontOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, TextFontOptionsImpl options) {
                options.loadFromPreferences(new TextFontPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, TextFontOptionsImpl options) {
                options.saveToPreferences(new TextFontPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(TextFontOptionsImpl options) {
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

        optionsModule.addOptionsPage(new DefaultOptionsPage<TextEncodingOptionsImpl>() {
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
                            dialog.dispose();
                        });
                        frameModule.setDialogTitle(dialog, addEncodingPanel.getResourceBundle());
                        dialog.showCentered(frameModule.getFrame());
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
            public TextEncodingOptionsImpl createOptions() {
                return new TextEncodingOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, TextEncodingOptionsImpl options) {
                options.loadFromPreferences(new TextEncodingPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, TextEncodingOptionsImpl options) {
                options.saveToPreferences(new TextEncodingPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(TextEncodingOptionsImpl options) {
                textEncodingService.setSelectedEncoding(options.getSelectedEncoding());
                textEncodingService.setEncodings(options.getEncodings());
            }
        });

        optionsModule.extendAppearanceOptionsPage(new DefaultOptionsPage<TextAppearanceOptionsImpl>() {
            private TextAppearanceOptionsPanel panel;

            @Override
            public OptionsCapable<TextAppearanceOptionsImpl> createPanel() {
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
            public TextAppearanceOptionsImpl createOptions() {
                return new TextAppearanceOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, TextAppearanceOptionsImpl options) {
                options.loadFromPreferences(new TextAppearancePreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, TextAppearanceOptionsImpl options) {
                options.saveToPreferences(new TextAppearancePreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(TextAppearanceOptionsImpl options) {
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
            encodingsHandler = new EncodingsHandler();
            encodingsHandler.setParentComponent(getEditorProvider().getPanel());
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
