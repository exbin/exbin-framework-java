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

import org.exbin.framework.editor.text.action.FindReplaceActions;
import org.exbin.framework.editor.text.action.TextColorAction;
import org.exbin.framework.editor.text.action.PrintAction;
import org.exbin.framework.editor.text.action.WordWrappingAction;
import org.exbin.framework.editor.text.action.PropertiesAction;
import org.exbin.framework.editor.text.action.GoToLineAction;
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
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.editor.text.action.TextFontAction;
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
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.action.api.ToolBarGroup;
import org.exbin.framework.action.api.ToolBarPosition;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.handler.DefaultControlHandler;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.editor.text.service.TextAppearanceService;
import org.exbin.framework.editor.text.service.TextEncodingService;
import org.exbin.framework.editor.text.service.TextColorService;
import org.exbin.framework.editor.text.service.TextFontService;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.options.api.OptionsComponent;

/**
 * Text editor module.
 *
 * @author ExBin Project (https://exbin.org)
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
    private ResourceBundle resourceBundle;
    private TextStatusPanel textStatusPanel;

    private FindReplaceActions findReplaceActions;
    private TextFontAction textFontAction;
    private TextColorAction textColorAction;
    private EncodingsHandler encodingsHandler;
    private WordWrappingAction wordWrappingAction;
    private GoToLineAction goToLineAction;
    private PropertiesAction propertiesAction;
    private PrintAction printAction;

    public EditorTextModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    private void ensureSetup() {
        if (editorProvider == null) {
            getEditorProvider();
        }

        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    public EditorProvider getEditorProvider() {
        if (editorProvider == null) {
            editorProvider = new TextEditor(application);
        }

        return editorProvider;
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(EditorTextModule.class);
        }

        return resourceBundle;
    }

    public void registerFileTypes() {
        FileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(FileModuleApi.class);
        fileModule.addFileType(new TXTFileType());
        fileModule.addFileType(new XBTFileType());
    }

    public void registerStatusBar() {
        textStatusPanel = new TextStatusPanel();
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        frameModule.registerStatusBar(MODULE_ID, TEXT_STATUS_BAR_ID, textStatusPanel);
        frameModule.switchStatusBar(TEXT_STATUS_BAR_ID);
        ((TextPanel) getEditorProvider().getEditorComponent()).registerTextStatus(textStatusPanel);
        if (encodingsHandler != null) {
            encodingsHandler.setTextEncodingStatus(textStatusPanel);
        }
    }

    public void registerOptionsMenuPanels() {
        getEncodingsHandler();
        encodingsHandler.rebuildEncodings();

        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(FrameModuleApi.TOOLS_MENU_ID, MODULE_ID, encodingsHandler.getToolsEncodingMenu(), new MenuPosition(PositionMode.TOP_LAST));
    }

    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = application.getModuleRepository().getModuleByInterface(OptionsModuleApi.class);
        TextColorService textColorService = new TextColorService() {
            @Override
            public Color[] getCurrentTextColors() {
                return ((TextPanel) getEditorProvider().getEditorComponent()).getCurrentColors();
            }

            @Override
            public Color[] getDefaultTextColors() {
                return ((TextPanel) getEditorProvider().getEditorComponent()).getDefaultColors();
            }

            @Override
            public void setCurrentTextColors(Color[] colors) {
                ((TextPanel) getEditorProvider().getEditorComponent()).setCurrentColors(colors);
            }
        };

        optionsModule.addOptionsPage(new DefaultOptionsPage<TextColorOptionsImpl>() {
            private TextColorOptionsPanel panel;

            @Nonnull
            @Override
            public OptionsComponent<TextColorOptionsImpl> createPanel() {
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

            @Nonnull
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
                return ((TextPanel) getEditorProvider().getEditorComponent()).getCurrentFont();
            }

            @Override
            public Font getDefaultFont() {
                return ((TextPanel) getEditorProvider().getEditorComponent()).getDefaultFont();
            }

            @Override
            public void setCurrentFont(Font font) {
                ((TextPanel) getEditorProvider().getEditorComponent()).setCurrentFont(font);
            }
        };

        optionsModule.addOptionsPage(new DefaultOptionsPage<TextFontOptionsImpl>() {

            private TextFontOptionsPanel panel;

            @Override
            public OptionsComponent<TextFontOptionsImpl> createPanel() {
                if (panel == null) {
                    panel = new TextFontOptionsPanel();
                    panel.setTextFontService(textFontService);
                    panel.setFontChangeAction(new TextFontOptionsPanel.FontChangeAction() {
                        @Override
                        public Font changeFont(Font currentFont) {
                            final Result result = new Result();
                            FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
                            final TextFontPanel fontPanel = new TextFontPanel();
                            fontPanel.setStoredFont(currentFont);
                            DefaultControlPanel controlPanel = new DefaultControlPanel();
                            final DialogWrapper dialog = frameModule.createDialog(fontPanel, controlPanel);
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

        TextAppearanceService textAppearanceService;
        textAppearanceService = new TextAppearanceService() {
            @Override
            public boolean getWordWrapMode() {
                return ((TextPanel) getEditorProvider().getEditorComponent()).getWordWrapMode();
            }

            @Override
            public void setWordWrapMode(boolean mode) {
                ((TextPanel) getEditorProvider().getEditorComponent()).setWordWrapMode(mode);
            }
        };

        optionsModule.addOptionsPage(new DefaultOptionsPage<TextEncodingOptionsImpl>() {
            private TextEncodingOptionsPanel panel;

            @Override
            public TextEncodingOptionsPanel createPanel() {
                if (panel == null) {
                    panel = new TextEncodingOptionsPanel();
                    panel.setTextEncodingService(getEncodingsHandler().getTextEncodingService());
                    panel.setAddEncodingsOperation((List<String> usedEncodings) -> {
                        final List<String> result = new ArrayList<>();
                        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
                        final AddEncodingPanel addEncodingPanel = new AddEncodingPanel();
                        addEncodingPanel.setUsedEncodings(usedEncodings);
                        DefaultControlPanel controlPanel = new DefaultControlPanel(addEncodingPanel.getResourceBundle());
                        final DialogWrapper dialog = frameModule.createDialog(addEncodingPanel, controlPanel);
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
                getEncodingsHandler();
                encodingsHandler.setSelectedEncoding(options.getSelectedEncoding());
                encodingsHandler.setEncodings(options.getEncodings());
            }
        });

        optionsModule.extendAppearanceOptionsPage(new DefaultOptionsPage<TextAppearanceOptionsImpl>() {
            private TextAppearanceOptionsPanel panel;

            @Override
            public OptionsComponent<TextAppearanceOptionsImpl> createPanel() {
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
        getWordWrappingAction();
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(FrameModuleApi.VIEW_MENU_ID, MODULE_ID, getWordWrappingAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerGoToLine() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, MODULE_ID, getGoToLineAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public TextStatusPanel getTextStatusPanel() {
        return textStatusPanel;
    }

    @Nonnull
    private FindReplaceActions getFindReplaceActions() {
        if (findReplaceActions == null) {
            ensureSetup();
            findReplaceActions = new FindReplaceActions();
            findReplaceActions.setup(application, editorProvider, resourceBundle);
        }

        return findReplaceActions;
    }

    @Nonnull
    private TextFontAction getTextFontAction() {
        if (textFontAction == null) {
            ensureSetup();
            textFontAction = new TextFontAction();
            textFontAction.setup(application, editorProvider, resourceBundle);
        }

        return textFontAction;
    }

    @Nonnull
    private TextColorAction getTextColorAction() {
        if (textColorAction == null) {
            ensureSetup();
            textColorAction = new TextColorAction();
            textColorAction.setup(application, editorProvider, resourceBundle);
        }

        return textColorAction;
    }

    @Nonnull
    private EncodingsHandler getEncodingsHandler() {
        if (encodingsHandler == null) {
            encodingsHandler = new EncodingsHandler();
            encodingsHandler.setApplication(application);
            encodingsHandler.setEncodingChangeListener(new TextEncodingService.EncodingChangeListener() {
                @Override
                public void encodingListChanged() {
                    encodingsHandler.rebuildEncodings();
                }

                @Override
                public void selectedEncodingChanged() {
                    ((TextPanel) getEditorProvider().getEditorComponent()).setCharset(Charset.forName(encodingsHandler.getSelectedEncoding()));
                }
            });
            encodingsHandler.setParentComponent(getEditorProvider().getEditorComponent());
            if (textStatusPanel != null) {
                encodingsHandler.setTextEncodingStatus(textStatusPanel);
            }
            encodingsHandler.init();
        }

        return encodingsHandler;
    }

    @Nonnull
    private WordWrappingAction getWordWrappingAction() {
        if (wordWrappingAction == null) {
            ensureSetup();
            wordWrappingAction = new WordWrappingAction();
            wordWrappingAction.setup(application, editorProvider, resourceBundle);
        }

        return wordWrappingAction;
    }

    @Nonnull
    private GoToLineAction getGoToLineAction() {
        if (goToLineAction == null) {
            ensureSetup();
            goToLineAction = new GoToLineAction();
            goToLineAction.setup(application, editorProvider, resourceBundle);
        }

        return goToLineAction;
    }

    @Nonnull
    private PropertiesAction getPropertiesAction() {
        if (propertiesAction == null) {
            ensureSetup();
            propertiesAction = new PropertiesAction();
            propertiesAction.setup(application, editorProvider, resourceBundle);
        }

        return propertiesAction;
    }

    @Nonnull
    private PrintAction getPrintAction() {
        if (printAction == null) {
            ensureSetup();
            printAction = new PrintAction();
            printAction.setup(application, editorProvider, resourceBundle);
        }

        return printAction;
    }

    public void registerEditFindMenuActions() {
        getFindReplaceActions();
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuGroup(FrameModuleApi.EDIT_MENU_ID, new MenuGroup(EDIT_FIND_MENU_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, MODULE_ID, findReplaceActions.getEditFindAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, MODULE_ID, findReplaceActions.getEditFindAgainAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, MODULE_ID, findReplaceActions.getEditReplaceAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
    }

    public void registerEditFindToolBarActions() {
        getFindReplaceActions();
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerToolBarGroup(FrameModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(EDIT_FIND_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerToolBarItem(FrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, findReplaceActions.getEditFindAction(), new ToolBarPosition(EDIT_FIND_TOOL_BAR_GROUP_ID));
    }

    public void registerToolsOptionsMenuActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(FrameModuleApi.TOOLS_MENU_ID, MODULE_ID, getTextFontAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(FrameModuleApi.TOOLS_MENU_ID, MODULE_ID, getTextColorAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerPropertiesMenu() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(FrameModuleApi.FILE_MENU_ID, MODULE_ID, getPropertiesAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerPrintMenu() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(FrameModuleApi.FILE_MENU_ID, MODULE_ID, getPrintAction(), new MenuPosition(PositionMode.BOTTOM));
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
