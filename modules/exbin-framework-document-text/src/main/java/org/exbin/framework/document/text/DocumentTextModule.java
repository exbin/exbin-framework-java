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
package org.exbin.framework.document.text;

import org.exbin.framework.document.text.action.FindReplaceActions;
import org.exbin.framework.document.text.action.TextColorAction;
import org.exbin.framework.document.text.action.PrintAction;
import org.exbin.framework.document.text.action.WordWrappingAction;
import org.exbin.framework.document.text.action.PropertiesAction;
import org.exbin.framework.document.text.action.GoToLineAction;
import java.awt.Component;
import java.io.File;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionContextRegistration;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.document.text.gui.TextPanel;
import org.exbin.framework.document.text.gui.TextStatusPanel;
import org.exbin.framework.document.text.action.EditSelectionAction;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.text.encoding.EncodingsManager;
import org.exbin.framework.text.font.TextFontModule;
import org.exbin.framework.text.font.action.TextFontAction;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;
import org.exbin.framework.action.api.clipboard.ClipboardActionsApi;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SeparationSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.document.text.settings.TextAppearanceOptions;
import org.exbin.framework.document.text.settings.TextAppearanceSettingsApplier;
import org.exbin.framework.document.text.settings.TextAppearanceSettingsComponent;
import org.exbin.framework.document.text.settings.TextColorOptions;
import org.exbin.framework.document.text.settings.TextColorSettingsApplier;
import org.exbin.framework.document.text.settings.TextColorSettingsComponent;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.menu.api.MenuDefinitionManagement;
import org.exbin.framework.options.settings.api.ApplySettingsContribution;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsPageContribution;
import org.exbin.framework.options.settings.api.SettingsPageContributionRule;
import org.exbin.framework.toolbar.api.ToolBarDefinitionManagement;

/**
 * Text editor module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DocumentTextModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(DocumentTextModule.class);

    public static final String EDIT_FIND_MENU_GROUP_ID = MODULE_ID + ".editFindMenuGroup";
    public static final String EDIT_FIND_TOOL_BAR_GROUP_ID = MODULE_ID + ".editFindToolBarGroup";

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

    private ResourceBundle resourceBundle;
    private TextStatusPanel textStatusPanel;

    private FindReplaceActions findReplaceActions;
    private EncodingsManager encodingsManager;

    public DocumentTextModule() {
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(DocumentTextModule.class);
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
        // TODO
//        JComponent editorComponent = editorProvider.getEditorComponent();
//        if (editorComponent instanceof TextPanel) {
//            ((TextPanel) editorComponent).registerTextStatus(textStatusPanel);
//        }
//        if (encodingsManager != null) {
//            // TODO encodingsManager.setTextEncodingStatus(textStatusPanel);
//        }
    }

    public void registerOptionsMenuPanels() {
        getEncodingsManager();

        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.TOOLS_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(() -> encodingsManager.getToolsEncodingMenu());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP_LAST));
    }

    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerOptionsSettings(TextAppearanceOptions.class, (optionsStorage) -> new TextAppearanceOptions(optionsStorage));
        settingsManagement.registerOptionsSettings(TextColorOptions.class, (optionsStorage) -> new TextColorOptions(optionsStorage));

        settingsManagement.registerApplySetting(EditorTextPanelComponent.class, new ApplySettingsContribution(SETTINGS_PAGE_ID, new TextAppearanceSettingsApplier()));
        settingsManagement.registerApplySetting(EditorTextPanelComponent.class, new ApplySettingsContribution(SETTINGS_PAGE_ID, new TextColorSettingsApplier()));

        SettingsPageContribution pageContribution = new SettingsPageContribution(SETTINGS_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(pageContribution);
        SettingsComponentContribution settingsComponent = settingsManagement.registerComponent(TextAppearanceSettingsComponent.COMPONENT_ID, new TextAppearanceSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(pageContribution));

        settingsComponent = settingsManagement.registerComponent(TextColorSettingsComponent.COMPONENT_ID, new TextColorSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(pageContribution));
    }

    public void registerUndoHandler() {
        //TODO
        // ((TextEditorProvider) editorProvider).registerUndoHandler();
    }

    public void registerWordWrapping() {
        createWordWrappingAction();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createWordWrappingAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerGoToLine() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createGoToLineAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerEditSelection() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createEditSelectionAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
    }

    public void registerTextPopupMenu() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        menuModule.registerMenu(TEXT_POPUP_MENU_ID, MODULE_ID);
        MenuDefinitionManagement mgmt = menuModule.getMenuManager(TEXT_POPUP_MENU_ID, MODULE_ID);
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
    private EncodingsManager getEncodingsManager() {
        if (encodingsManager == null) {
            encodingsManager = new EncodingsManager();
            /* encodingsManager.setEncodingChangeListener(new TextEncodingService.EncodingChangeListener() {
                @Override
                public void encodingListChanged() {
                    encodingsManager.rebuildEncodings();
                }

                @Override
                public void selectedEncodingChanged() {
                    if (editorProvider instanceof TextEditorProvider) {
                        ((TextEditorProvider) editorProvider).getEditorComponent().setCharset(Charset.forName(encodingsManager.getSelectedEncoding()));
                    }
                }
            });
            if (textStatusPanel != null) {
                encodingsManager.setTextEncodingStatus(textStatusPanel);
            } */
            encodingsManager.init();
        }

        return encodingsManager;
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
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
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
        ToolBarDefinitionManagement mgmt = toolBarModule.getMainToolBarManager(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(EDIT_FIND_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerToolBarRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerToolBarItem(findReplaceActions.createEditFindAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(EDIT_FIND_TOOL_BAR_GROUP_ID));
    }

    public void registerToolsOptionsMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.TOOLS_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createTextFontAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(createTextColorAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    public void registerPropertiesMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createPropertiesAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerPrintMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
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
                ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
                ActionContextRegistration actionContextRegistrar = actionModule.createActionContextRegistrar(frameModule.getFrameHandler().getActionManager());
                menuModule.buildMenu(popupMenu, TEXT_POPUP_MENU_ID, actionContextRegistrar);
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
