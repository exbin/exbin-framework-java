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
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.editor.text.gui.TextStatusPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.editor.text.service.TextAppearanceService;
import org.exbin.framework.editor.text.service.TextColorService;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.menu.GroupMenuContributionRule;
import org.exbin.framework.action.api.toolbar.GroupToolBarContributionRule;
import org.exbin.framework.action.api.menu.MenuContribution;
import org.exbin.framework.action.api.menu.MenuManagement;
import org.exbin.framework.action.api.menu.PositionMenuContributionRule;
import org.exbin.framework.action.api.toolbar.PositionToolBarContributionRule;
import org.exbin.framework.action.api.menu.SeparationMenuContributionRule;
import org.exbin.framework.action.api.toolbar.SeparationToolBarContributionRule;
import org.exbin.framework.action.api.toolbar.ToolBarContribution;
import org.exbin.framework.action.api.toolbar.ToolBarManagement;
import org.exbin.framework.editor.text.options.TextAppearanceOptionsPage;
import org.exbin.framework.editor.text.options.TextColorOptionsPage;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.options.api.GroupOptionsPageRule;
import org.exbin.framework.options.api.OptionsGroup;
import org.exbin.framework.options.api.OptionsPageManagement;
import org.exbin.framework.options.api.ParentOptionsGroupRule;
import org.exbin.framework.options.api.VisualOptionsPageParams;
import org.exbin.framework.options.api.VisualOptionsPageRule;
import org.exbin.framework.text.encoding.EncodingsHandler;
import org.exbin.framework.text.encoding.options.TextEncodingOptionsPage;
import org.exbin.framework.text.encoding.service.TextEncodingService;
import org.exbin.framework.text.font.TextFontModule;
import org.exbin.framework.text.font.action.TextFontAction;
import org.exbin.framework.text.font.options.TextFontOptionsPage;
import org.exbin.framework.text.font.service.TextFontService;
import org.exbin.framework.utils.ClipboardActionsApi;
import org.exbin.framework.utils.UiUtils;

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

    public static final String TXT_FILE_TYPE = "XBTextEditor.TXTFileType";

    public static final String TEXT_STATUS_BAR_ID = "textStatusBar";

    private TextEditorProvider editorProvider;
    private ResourceBundle resourceBundle;
    private TextStatusPanel textStatusPanel;

    private FindReplaceActions findReplaceActions;
    private EncodingsHandler encodingsHandler;

    public EditorTextModule() {
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
            editorProvider = new TextEditorProvider();
        }

        return editorProvider;
    }

    @Nonnull
    public void setEditorProvider(TextEditorProvider editorProvider) {
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
        ((TextPanel) getEditorProvider().getEditorComponent()).registerTextStatus(textStatusPanel);
        if (encodingsHandler != null) {
            encodingsHandler.setTextEncodingStatus(textStatusPanel);
        }
    }

    public void registerOptionsMenuPanels() {
        getEncodingsHandler();
        encodingsHandler.rebuildEncodings();

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(ActionConsts.TOOLS_MENU_ID, () -> encodingsHandler.getToolsEncodingMenu());
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.TOP_LAST));
    }

    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(MODULE_ID);

        OptionsGroup textEditorGroup = optionsModule.createOptionsGroup("textEditor", resourceBundle);
        optionsPageManagement.registerGroup(textEditorGroup);
        optionsPageManagement.registerGroupRule(textEditorGroup, new ParentOptionsGroupRule("editor"));

        OptionsGroup textEditorColorGroup = optionsModule.createOptionsGroup("textEditorColor", resourceBundle);
        optionsPageManagement.registerGroup(textEditorColorGroup);
        optionsPageManagement.registerGroupRule(textEditorColorGroup, new ParentOptionsGroupRule(textEditorGroup));
        TextColorOptionsPage textColorsOptionsPage = new TextColorOptionsPage();
        textColorsOptionsPage.setTextColorService(new TextColorService() {
            @Nonnull
            @Override
            public Color[] getCurrentTextColors() {
                return ((TextPanel) getEditorProvider().getEditorComponent()).getCurrentColors();
            }

            @Nonnull
            @Override
            public Color[] getDefaultTextColors() {
                return ((TextPanel) getEditorProvider().getEditorComponent()).getDefaultColors();
            }

            @Override
            public void setCurrentTextColors(Color[] colors) {
                ((TextPanel) getEditorProvider().getEditorComponent()).setCurrentColors(colors);
            }
        });
        optionsPageManagement.registerPage(textColorsOptionsPage);
        optionsPageManagement.registerPageRule(textColorsOptionsPage, new GroupOptionsPageRule(textEditorColorGroup));

        OptionsGroup textEditorFontGroup = optionsModule.createOptionsGroup("textEditorFont", resourceBundle);
        optionsPageManagement.registerGroup(textEditorFontGroup);
        optionsPageManagement.registerGroupRule(textEditorFontGroup, new ParentOptionsGroupRule(textEditorGroup));
        TextFontOptionsPage textFontOptionsPage = new TextFontOptionsPage();
        textFontOptionsPage.setTextFontService(new TextFontService() {
            @Nonnull
            @Override
            public Font getCurrentFont() {
                return ((TextPanel) getEditorProvider().getEditorComponent()).getCurrentFont();
            }

            @Nonnull
            @Override
            public Font getDefaultFont() {
                return ((TextPanel) getEditorProvider().getEditorComponent()).getDefaultFont();
            }

            @Override
            public void setCurrentFont(Font font) {
                ((TextPanel) getEditorProvider().getEditorComponent()).setCurrentFont(font);
            }
        });
        optionsPageManagement.registerPage(textFontOptionsPage);
        optionsPageManagement.registerPageRule(textFontOptionsPage, new GroupOptionsPageRule(textEditorFontGroup));

        TextAppearanceOptionsPage textAppearanceOptionsPage = new TextAppearanceOptionsPage();
        textAppearanceOptionsPage.setTextAppearanceService(new TextAppearanceService() {
            @Override
            public boolean getWordWrapMode() {
                return ((TextPanel) getEditorProvider().getEditorComponent()).getWordWrapMode();
            }

            @Override
            public void setWordWrapMode(boolean mode) {
                ((TextPanel) getEditorProvider().getEditorComponent()).setWordWrapMode(mode);
            }
        });
        optionsPageManagement.registerPage(textAppearanceOptionsPage);
        optionsPageManagement.registerPageRule(textAppearanceOptionsPage, new GroupOptionsPageRule(textEditorGroup));

        OptionsGroup textEditorEncodingGroup = optionsModule.createOptionsGroup("textEditorEncoding", resourceBundle);
        optionsPageManagement.registerGroup(textEditorEncodingGroup);
        optionsPageManagement.registerGroupRule(textEditorEncodingGroup, new ParentOptionsGroupRule(textEditorGroup));
        TextEncodingOptionsPage textEncodingOptionsPage = new TextEncodingOptionsPage();
        textEncodingOptionsPage.setEncodingsHandler(getEncodingsHandler());
        optionsPageManagement.registerPage(textEncodingOptionsPage);
        optionsPageManagement.registerPageRule(textEncodingOptionsPage, new GroupOptionsPageRule(textEditorEncodingGroup));
        optionsPageManagement.registerPageRule(textEncodingOptionsPage, new VisualOptionsPageRule(new VisualOptionsPageParams(true)));
    }

    public void registerUndoHandler() {
        editorProvider.registerUndoHandler();
    }

    public void registerWordWrapping() {
        createWordWrappingAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(ActionConsts.VIEW_MENU_ID, createWordWrappingAction());
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.BOTTOM));
    }

    public void registerGoToLine() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(ActionConsts.EDIT_MENU_ID, createGoToLineAction());
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.BOTTOM));
    }

    public void registerTextPopupMenu() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        ClipboardActionsApi clipboardActions = actionModule.getClipboardActions();

        mgmt.registerMenu(TEXT_POPUP_MENU_ID);
        MenuContribution contribution = mgmt.registerMenuGroup(TEXT_POPUP_MENU_ID, TEXT_POPUP_VIEW_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.TOP));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(TEXT_POPUP_MENU_ID, TEXT_POPUP_EDIT_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(TEXT_POPUP_MENU_ID, TEXT_POPUP_SELECTION_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(TEXT_POPUP_MENU_ID, TEXT_POPUP_FIND_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(TEXT_POPUP_MENU_ID, TEXT_POPUP_TOOLS_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMode.AROUND));

        contribution = mgmt.registerMenuItem(TEXT_POPUP_MENU_ID, clipboardActions.createCutAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(TEXT_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(TEXT_POPUP_MENU_ID, clipboardActions.createCopyAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(TEXT_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(TEXT_POPUP_MENU_ID, clipboardActions.createPasteAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(TEXT_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(TEXT_POPUP_MENU_ID, clipboardActions.createDeleteAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(TEXT_POPUP_EDIT_GROUP_ID));

        contribution = mgmt.registerMenuItem(TEXT_POPUP_MENU_ID, clipboardActions.createSelectAllAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(TEXT_POPUP_SELECTION_GROUP_ID));
        /* contribution = mgmt.registerMenuItem(TEXT_POPUP_MENU_ID, createEditSelectionAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(TEXT_POPUP_SELECTION_GROUP_ID)); */

        contribution = mgmt.registerMenuItem(TEXT_POPUP_MENU_ID, findReplaceActions.createEditFindAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(TEXT_POPUP_FIND_GROUP_ID));
        contribution = mgmt.registerMenuItem(TEXT_POPUP_MENU_ID, findReplaceActions.createEditReplaceAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(TEXT_POPUP_FIND_GROUP_ID));
        contribution = mgmt.registerMenuItem(TEXT_POPUP_MENU_ID, createGoToLineAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(TEXT_POPUP_FIND_GROUP_ID));

        /* contribution = mgmt.registerMenuItem(TEXT_POPUP_MENU_ID, getOptionsAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(TEXT_POPUP_TOOLS_GROUP_ID)); */
    }

    public TextStatusPanel getTextStatusPanel() {
        return textStatusPanel;
    }

    @Nonnull
    private FindReplaceActions getFindReplaceActions() {
        if (findReplaceActions == null) {
            ensureSetup();
            findReplaceActions = new FindReplaceActions();
            findReplaceActions.setup(editorProvider, resourceBundle);
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
                    ((TextPanel) getEditorProvider().getEditorComponent()).setCharset(Charset.forName(encodingsHandler.getSelectedEncoding()));
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
        wordWrappingAction.setup(editorProvider, resourceBundle);
        return wordWrappingAction;
    }

    @Nonnull
    private GoToLineAction createGoToLineAction() {
        ensureSetup();
        GoToLineAction goToLineAction = new GoToLineAction();
        goToLineAction.setup(editorProvider, resourceBundle);
        return goToLineAction;
    }

    @Nonnull
    private PropertiesAction createPropertiesAction() {
        ensureSetup();
        PropertiesAction propertiesAction = new PropertiesAction();
        propertiesAction.setup(editorProvider, resourceBundle);
        return propertiesAction;
    }

    @Nonnull
    private PrintAction createPrintAction() {
        ensureSetup();
        PrintAction printAction = new PrintAction();
        printAction.setup(editorProvider, resourceBundle);
        return printAction;
    }

    public void registerEditFindMenuActions() {
        getFindReplaceActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuGroup(ActionConsts.EDIT_MENU_ID, EDIT_FIND_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMode.AROUND));
        contribution = mgmt.registerMenuItem(ActionConsts.EDIT_MENU_ID, findReplaceActions.createEditFindAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(EDIT_FIND_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(ActionConsts.EDIT_MENU_ID, findReplaceActions.createEditFindAgainAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(EDIT_FIND_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(ActionConsts.EDIT_MENU_ID, findReplaceActions.createEditReplaceAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(EDIT_FIND_MENU_GROUP_ID));
    }

    public void registerEditFindToolBarActions() {
        getFindReplaceActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        ToolBarManagement mgmt = actionModule.getToolBarManagement(MODULE_ID);
        ToolBarContribution contribution = mgmt.registerToolBarGroup(ActionConsts.MAIN_TOOL_BAR_ID, EDIT_FIND_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionToolBarContributionRule(PositionMode.MIDDLE));
        mgmt.registerToolBarRule(contribution, new SeparationToolBarContributionRule(SeparationMode.AROUND));
        contribution = mgmt.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, findReplaceActions.createEditFindAction());
        mgmt.registerToolBarRule(contribution, new GroupToolBarContributionRule(EDIT_FIND_TOOL_BAR_GROUP_ID));
    }

    public void registerToolsOptionsMenuActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(ActionConsts.TOOLS_MENU_ID, createTextFontAction());
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.TOP));
        contribution = mgmt.registerMenuItem(ActionConsts.TOOLS_MENU_ID, createTextColorAction());
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.TOP));
    }

    public void registerPropertiesMenu() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(ActionConsts.FILE_MENU_ID, createPropertiesAction());
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.BOTTOM));
    }

    public void registerPrintMenu() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(ActionConsts.FILE_MENU_ID, createPrintAction());
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.BOTTOM));
    }

    @Nonnull
    public JPopupMenu createPopupMenu(TextPanel textPanel) {
        JPopupMenu popupMenu = new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                JPopupMenu popupMenu = UiUtils.createPopupMenu();
                FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
                MenuManagement menuManagement = actionModule.getMenuManagement(MODULE_ID);
                menuManagement.buildMenu(popupMenu, TEXT_POPUP_MENU_ID, frameModule.getFrameHandler().getActionContextService());
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
