/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.document.text;

import org.exbin.jaguif.document.text.action.FindReplaceActions;
import org.exbin.jaguif.document.text.action.TextColorAction;
import org.exbin.jaguif.document.text.action.PrintAction;
import org.exbin.jaguif.document.text.action.WordWrappingAction;
import org.exbin.jaguif.document.text.action.PropertiesAction;
import org.exbin.jaguif.document.text.action.GoToLineAction;
import java.awt.Component;
import java.io.File;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.document.text.gui.TextPanel;
import org.exbin.jaguif.document.text.gui.TextStatusPanel;
import org.exbin.jaguif.document.text.action.EditSelectionAction;
import org.exbin.jaguif.file.api.FileType;
import org.exbin.jaguif.file.api.FileModuleApi;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.text.encoding.EncodingsManager;
import org.exbin.jaguif.text.font.TextFontModule;
import org.exbin.jaguif.text.font.action.TextFontAction;
import org.exbin.jaguif.toolbar.api.ToolBarModuleApi;
import org.exbin.jaguif.action.api.clipboard.ClipboardActionsApi;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SeparationSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.document.text.contribution.EditSelectionContribution;
import org.exbin.jaguif.document.text.contribution.GoToLineContribution;
import org.exbin.jaguif.document.text.contribution.PropertiesContribution;
import org.exbin.jaguif.document.text.contribution.TextColorContribution;
import org.exbin.jaguif.document.text.contribution.WordWrappingContribution;
import org.exbin.jaguif.document.text.settings.TextAppearanceOptions;
import org.exbin.jaguif.document.text.settings.TextAppearanceSettingsApplier;
import org.exbin.jaguif.document.text.settings.TextAppearanceSettingsComponent;
import org.exbin.jaguif.document.text.settings.TextColorOptions;
import org.exbin.jaguif.document.text.settings.TextColorSettingsApplier;
import org.exbin.jaguif.document.text.settings.TextColorSettingsComponent;
import org.exbin.jaguif.menu.api.MenuBuilder;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.options.settings.api.ApplySettingsContribution;
import org.exbin.jaguif.options.settings.api.SettingsComponentContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContributionRule;
import org.exbin.jaguif.text.font.contribution.TextFontContribution;
import org.exbin.jaguif.toolbar.api.ToolBarDefinitionManagement;

/**
 * Text editor module.
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
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.TOOLS_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(() -> encodingsManager.getToolsEncodingMenu());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP_LAST));
    }

    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerSettingsOptions(TextAppearanceOptions.class, (optionsStorage) -> new TextAppearanceOptions(optionsStorage));
        settingsManagement.registerSettingsOptions(TextColorOptions.class, (optionsStorage) -> new TextColorOptions(optionsStorage));

        settingsManagement.registerApplySetting(TextAppearanceOptions.class, new ApplySettingsContribution(TextAppearanceSettingsApplier.APPLIER_ID, new TextAppearanceSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextComponent.class, new ApplySettingsContribution(TextAppearanceSettingsApplier.APPLIER_ID, new TextAppearanceSettingsApplier()));
        settingsManagement.registerApplySetting(TextColorOptions.class, new ApplySettingsContribution(TextColorSettingsApplier.APPLIER_ID, new TextColorSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextComponent.class, new ApplySettingsContribution(TextColorSettingsApplier.APPLIER_ID, new TextColorSettingsApplier()));

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
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = new WordWrappingContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerGoToLine() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = new GoToLineContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerEditSelection() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = new EditSelectionContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
    }

    public void registerTextPopupMenu() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        menuModule.registerMenu(TEXT_POPUP_MENU_ID, MODULE_ID);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(TEXT_POPUP_MENU_ID, MODULE_ID);
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
/*
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
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(TEXT_POPUP_FIND_GROUP_ID)); */
    }

    public TextStatusPanel getTextStatusPanel() {
        return textStatusPanel;
    }

    @Nonnull
    private FindReplaceActions getFindReplaceActions() {
        if (findReplaceActions == null) {
            findReplaceActions = new FindReplaceActions();
            findReplaceActions.init(getResourceBundle());
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
        TextColorAction textColorAction = new TextColorAction();
        textColorAction.init(getResourceBundle());
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
        WordWrappingAction wordWrappingAction = new WordWrappingAction();
        wordWrappingAction.init(getResourceBundle());
        return wordWrappingAction;
    }

    @Nonnull
    private GoToLineAction createGoToLineAction() {
        GoToLineAction goToLineAction = new GoToLineAction();
        goToLineAction.init(getResourceBundle());
        return goToLineAction;
    }

    @Nonnull
    private EditSelectionAction createEditSelectionAction() {
        EditSelectionAction editSelectionAction = new EditSelectionAction();
        editSelectionAction.init(getResourceBundle());
        return editSelectionAction;
    }

    @Nonnull
    private PropertiesAction createPropertiesAction() {
        PropertiesAction propertiesAction = new PropertiesAction();
        propertiesAction.init(getResourceBundle());
        return propertiesAction;
    }

    @Nonnull
    private PrintAction createPrintAction() {
        PrintAction printAction = new PrintAction();
        printAction.init(getResourceBundle());
        return printAction;
    }

    public void registerEditFindMenuActions() {
        /* getFindReplaceActions();
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
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(EDIT_FIND_MENU_GROUP_ID)); */
    }

    public void registerEditFindToolBarActions() {
        getFindReplaceActions();
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarDefinitionManagement mgmt = toolBarModule.getMainToolBarDefinition(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(EDIT_FIND_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerToolBarRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = new ActionSequenceContribution() {
            @Override
            public Action createAction() {
                return findReplaceActions.createEditFindAction();
            }

            @Override
            public String getContributionId() {
                return FindReplaceActions.EditFindAction.ACTION_ID;
            }
        };
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(EDIT_FIND_TOOL_BAR_GROUP_ID));
    }

    public void registerToolsOptionsMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.TOOLS_SUBMENU_ID);
        SequenceContribution contribution = new TextFontContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = new TextColorContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    public void registerPropertiesMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = new PropertiesContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerPrintMenu() {
        /* MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition().getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createPrintAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM)); */
    }

    @Nonnull
    public JPopupMenu createPopupMenu(TextPanel textPanel) {
        JPopupMenu popupMenu = new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
                MenuBuilder menuBuilder = menuModule.getMenuBuilder();
                JPopupMenu popupMenu = menuBuilder.createPopupMenu();
                FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
                ContextRegistration contextRegistrar = contextModule.createContextRegistrator(frameModule.getFrameController().getContextManager());
                menuModule.buildMenu(popupMenu, TEXT_POPUP_MENU_ID, contextRegistrar);
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
