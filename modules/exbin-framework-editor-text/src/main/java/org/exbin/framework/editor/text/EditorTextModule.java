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
import java.awt.Component;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.editor.text.gui.TextStatusPanel;
import org.exbin.framework.editor.text.action.EditSelectionAction;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.toolbar.api.ToolBarManagement;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.text.encoding.EncodingsHandler;
import org.exbin.framework.text.encoding.service.TextEncodingService;
import org.exbin.framework.text.font.TextFontModule;
import org.exbin.framework.text.font.action.TextFontAction;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;
import org.exbin.framework.action.api.clipboard.ClipboardActionsApi;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SeparationSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;

/**
 * Text editor module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditorTextModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(EditorTextModule.class);

    private static final String EDIT_FIND_MENU_GROUP_ID = MODULE_ID + ".editFindMenuGroup";
    private static final String EDIT_FIND_TOOL_BAR_GROUP_ID = MODULE_ID + ".editFindToolBarGroup";

    public static final String TEXT_POPUP_MENU_ID = MODULE_ID + ".textPopupMenu";
    public static final String TEXT_POPUP_VIEW_GROUP_ID = MODULE_ID + ".viewPopupMenuGroup";
    public static final String TEXT_POPUP_EDIT_GROUP_ID = MODULE_ID + ".editPopupMenuGroup";
    public static final String TEXT_POPUP_SELECTION_GROUP_ID = MODULE_ID + ".selectionPopupMenuGroup";
    public static final String TEXT_POPUP_FIND_GROUP_ID = MODULE_ID + ".findPopupMenuGroup";
    public static final String TEXT_POPUP_TOOLS_GROUP_ID = MODULE_ID + ".toolsPopupMenuGroup";
    public static final String SETTINGS_PAGE_ID = "textAppearance";
    public static final String SETTINGS_COLOR_PAGE_ID = "textColor";


    public static final String TXT_FILE_TYPE = "XBTextEditor.TXTFileType";

    public static final String TEXT_STATUS_BAR_ID = "textStatusBar";

    private EditorProvider editorProvider;
    private ResourceBundle resourceBundle;
    private TextStatusPanel textStatusPanel;

    private FindReplaceActions findReplaceActions;
    private EncodingsHandler encodingsHandler;

    public EditorTextModule() {
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(EditorTextModule.class);
        }

        return resourceBundle;
    }

    public void registerFileTypes() {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileModule.addFileType(new TXTFileType());
    }

    public void registerStatusBar() {
        textStatusPanel = new TextStatusPanel();
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        frameModule.registerStatusBar(MODULE_ID, TEXT_STATUS_BAR_ID, textStatusPanel);
        frameModule.switchStatusBar(TEXT_STATUS_BAR_ID);
        JComponent editorComponent = editorProvider.getEditorComponent();
        if (editorComponent instanceof TextPanel) {
            ((TextPanel) editorComponent).registerTextStatus(textStatusPanel);
        }
        if (encodingsHandler != null) {
            encodingsHandler.setTextEncodingStatus(textStatusPanel);
        }
    }

    public void registerOptionsMenuPanels() {
        getEncodingsHandler();
        encodingsHandler.rebuildEncodings();

        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.TOOLS_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(() -> encodingsHandler.getToolsEncodingMenu());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP_LAST));
    }

    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        /* OptionsGroup textEditorGroup = optionsModule.createOptionsGroup("textEditor", resourceBundle);
        optionsPageManagement.registerGroup(textEditorGroup);
        optionsPageManagement.registerGroupRule(textEditorGroup, new ParentOptionsGroupRule("editor"));

        OptionsGroup textEditorColorGroup = optionsModule.createOptionsGroup("textEditorColor", resourceBundle);
        optionsPageManagement.registerGroup(textEditorColorGroup);
        optionsPageManagement.registerGroupRule(textEditorColorGroup, new ParentOptionsGroupRule(textEditorGroup));
        TextColorSettingsComponent textColorsOptionsPage = new TextColorSettingsComponent();
        textColorsOptionsPage.setTextColorService(new TextColorService() {
            @Nonnull
            @Override
            public Color[] getCurrentTextColors() {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof TextFileHandler) {
                    return ((TextFileHandler) fileHandler).getComponent().getCurrentColors();
                }

                return createDefaultColors();
            }

            @Nonnull
            @Override
            public Color[] getDefaultTextColors() {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof TextFileHandler) {
                    return ((TextFileHandler) fileHandler).getComponent().getDefaultColors();
                }

                return createDefaultColors();
            }

            @Override
            public void setCurrentTextColors(Color[] colors) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof TextFileHandler) {
                    ((TextFileHandler) fileHandler).getComponent().setCurrentColors(colors);
                }
            }
            
            @Nonnull
            private Color[] createDefaultColors() {
                Color[] result = new Color[5];
                result[0] = Color.BLACK;
                result[1] = Color.BLACK;
                result[2] = Color.BLACK;
                result[3] = Color.BLACK;
                result[4] = Color.BLACK;
                return result;
            }
        });
        optionsPageManagement.registerPage(textColorsOptionsPage);
        optionsPageManagement.registerPageRule(textColorsOptionsPage, new GroupOptionsPageRule(textEditorColorGroup));

        OptionsGroup textEditorFontGroup = optionsModule.createOptionsGroup("textEditorFont", resourceBundle);
        optionsPageManagement.registerGroup(textEditorFontGroup);
        optionsPageManagement.registerGroupRule(textEditorFontGroup, new ParentOptionsGroupRule(textEditorGroup));
        TextFontSettingsComponent textFontOptionsPage = new TextFontSettingsComponent();
        textFontOptionsPage.setTextFontService(new TextFontService() {
            @Nonnull
            @Override
            public Font getCurrentFont() {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof TextFileHandler) {
                    return ((TextFileHandler) fileHandler).getComponent().getCurrentFont();
                }

                return new JLabel().getFont();
            }

            @Nonnull
            @Override
            public Font getDefaultFont() {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof TextFileHandler) {
                    return ((TextFileHandler) fileHandler).getComponent().getDefaultFont();
                }

                return new JLabel().getFont();
            }

            @Override
            public void setCurrentFont(Font font) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof TextFileHandler) {
                    ((TextFileHandler) fileHandler).getComponent().setCurrentFont(font);
                }
            }
        });
        optionsPageManagement.registerPage(textFontOptionsPage);
        optionsPageManagement.registerPageRule(textFontOptionsPage, new GroupOptionsPageRule(textEditorFontGroup));

        TextAppearanceSettingsComponent textAppearanceOptionsPage = new TextAppearanceSettingsComponent();
        textAppearanceOptionsPage.setTextAppearanceService(new TextAppearanceService() {
            @Override
            public boolean getWordWrapMode() {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof TextFileHandler) {
                    return ((TextFileHandler) fileHandler).getComponent().getWordWrapMode();
                }

                return false;
            }

            @Override
            public void setWordWrapMode(boolean mode) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof TextFileHandler) {
                    ((TextFileHandler) fileHandler).getComponent().setWordWrapMode(mode);
                }
            }
        });
        optionsPageManagement.registerPage(textAppearanceOptionsPage);
        optionsPageManagement.registerPageRule(textAppearanceOptionsPage, new GroupOptionsPageRule(textEditorGroup));

        OptionsGroup textEditorEncodingGroup = optionsModule.createOptionsGroup("textEditorEncoding", resourceBundle);
        optionsPageManagement.registerGroup(textEditorEncodingGroup);
        optionsPageManagement.registerGroupRule(textEditorEncodingGroup, new ParentOptionsGroupRule(textEditorGroup));
        TextEncodingSettingsComponent textEncodingOptionsPage = new TextEncodingSettingsComponent();
        textEncodingOptionsPage.setEncodingsHandler(getEncodingsHandler());
        optionsPageManagement.registerPage(textEncodingOptionsPage);
        optionsPageManagement.registerPageRule(textEncodingOptionsPage, new GroupOptionsPageRule(textEditorEncodingGroup)); */
    }

    public void registerUndoHandler() {
        ((TextEditorProvider) editorProvider).registerUndoHandler();
    }

    public void registerWordWrapping() {
        createWordWrappingAction();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createWordWrappingAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerGoToLine() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createGoToLineAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerEditSelection() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createEditSelectionAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
    }

    public void registerTextPopupMenu() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        menuModule.registerMenu(TEXT_POPUP_MENU_ID, MODULE_ID);
        MenuManagement mgmt = menuModule.getMenuManagement(TEXT_POPUP_MENU_ID, MODULE_ID);
        ClipboardActionsApi clipboardActions = actionModule.getClipboardActions();

        SequenceContribution contribution = mgmt.registerMenuGroup(TEXT_POPUP_VIEW_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(TEXT_POPUP_EDIT_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(TEXT_POPUP_SELECTION_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(TEXT_POPUP_FIND_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(TEXT_POPUP_TOOLS_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));

        contribution = mgmt.registerMenuItem(clipboardActions.createCutAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(TEXT_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(clipboardActions.createCopyAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(TEXT_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(clipboardActions.createPasteAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(TEXT_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(clipboardActions.createDeleteAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(TEXT_POPUP_EDIT_GROUP_ID));

        contribution = mgmt.registerMenuItem(clipboardActions.createSelectAllAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(TEXT_POPUP_SELECTION_GROUP_ID));
        contribution = mgmt.registerMenuItem(createEditSelectionAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(TEXT_POPUP_SELECTION_GROUP_ID));

        contribution = mgmt.registerMenuItem(findReplaceActions.createEditFindAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(TEXT_POPUP_FIND_GROUP_ID));
        contribution = mgmt.registerMenuItem(findReplaceActions.createEditReplaceAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(TEXT_POPUP_FIND_GROUP_ID));
        contribution = mgmt.registerMenuItem(createGoToLineAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(TEXT_POPUP_FIND_GROUP_ID));
    }

    public TextStatusPanel getTextStatusPanel() {
        return textStatusPanel;
    }

    @Nonnull
    private FindReplaceActions getFindReplaceActions() {
        if (findReplaceActions == null) {
            ensureSetup();
            findReplaceActions = new FindReplaceActions();
            findReplaceActions.setup(resourceBundle);
        }

        return findReplaceActions;
    }

    @Nonnull
    private TextFontAction createTextFontAction() {
        TextFontModule textFontModule = App.getModule(TextFontModule.class);
        return textFontModule.createTextFontAction();
    }

    @Nonnull
    private TextColorAction createTextColorAction() {
        ensureSetup();
        TextColorAction textColorAction = new TextColorAction();
        textColorAction.setup(resourceBundle);
        return textColorAction;
    }

    @Nonnull
    private EncodingsHandler getEncodingsHandler() {
        if (encodingsHandler == null) {
            encodingsHandler = new EncodingsHandler();
            encodingsHandler.setEncodingChangeListener(new TextEncodingService.EncodingChangeListener() {
                @Override
                public void encodingListChanged() {
                    encodingsHandler.rebuildEncodings();
                }

                @Override
                public void selectedEncodingChanged() {
                    if (editorProvider instanceof TextEditorProvider) {
                        ((TextEditorProvider) editorProvider).getEditorComponent().setCharset(Charset.forName(encodingsHandler.getSelectedEncoding()));
                    }
                }
            });
            if (textStatusPanel != null) {
                encodingsHandler.setTextEncodingStatus(textStatusPanel);
            }
            encodingsHandler.init();
        }

        return encodingsHandler;
    }

    @Nonnull
    private WordWrappingAction createWordWrappingAction() {
        ensureSetup();
        WordWrappingAction wordWrappingAction = new WordWrappingAction();
        wordWrappingAction.setup(resourceBundle);
        return wordWrappingAction;
    }

    @Nonnull
    private GoToLineAction createGoToLineAction() {
        ensureSetup();
        GoToLineAction goToLineAction = new GoToLineAction();
        goToLineAction.setup(resourceBundle);
        return goToLineAction;
    }

    @Nonnull
    private EditSelectionAction createEditSelectionAction() {
        ensureSetup();
        EditSelectionAction editSelectionAction = new EditSelectionAction();
        editSelectionAction.setup(resourceBundle);
        return editSelectionAction;
    }

    @Nonnull
    private PropertiesAction createPropertiesAction() {
        ensureSetup();
        PropertiesAction propertiesAction = new PropertiesAction();
        propertiesAction.setup(resourceBundle);
        return propertiesAction;
    }

    @Nonnull
    private PrintAction createPrintAction() {
        ensureSetup();
        PrintAction printAction = new PrintAction();
        printAction.setup(resourceBundle);
        return printAction;
    }

    public void registerEditFindMenuActions() {
        getFindReplaceActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(EDIT_FIND_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuItem(findReplaceActions.createEditFindAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(EDIT_FIND_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(findReplaceActions.createEditFindAgainAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(EDIT_FIND_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(findReplaceActions.createEditReplaceAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(EDIT_FIND_MENU_GROUP_ID));
    }

    public void registerEditFindToolBarActions() {
        getFindReplaceActions();
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarManagement mgmt = toolBarModule.getMainToolBarManagement(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(EDIT_FIND_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerToolBarRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerToolBarItem(findReplaceActions.createEditFindAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(EDIT_FIND_TOOL_BAR_GROUP_ID));
    }

    public void registerToolsOptionsMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.TOOLS_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createTextFontAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(createTextColorAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    public void registerPropertiesMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createPropertiesAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerPrintMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createPrintAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    @Nonnull
    public JPopupMenu createPopupMenu(TextPanel textPanel) {
        JPopupMenu popupMenu = new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                JPopupMenu popupMenu = UiUtils.createPopupMenu();
                FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
                menuModule.buildMenu(popupMenu, TEXT_POPUP_MENU_ID, frameModule.getFrameHandler().getActionContextManager());
                popupMenu.show(invoker, x, y);
            }
        };
        return popupMenu;
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
