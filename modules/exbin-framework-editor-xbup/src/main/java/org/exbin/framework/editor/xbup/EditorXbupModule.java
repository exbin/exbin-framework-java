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
package org.exbin.framework.editor.xbup;

import org.exbin.framework.editor.xbup.action.SampleFilesActions;
import org.exbin.framework.editor.xbup.action.ViewModeActions;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import org.exbin.framework.XBFrameworkUtils;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.client.api.ClientConnectionListener;
import org.exbin.framework.editor.xbup.action.AddItemAction;
import org.exbin.framework.editor.xbup.catalog.action.CatalogsManagerAction;
import org.exbin.framework.editor.xbup.action.DocumentPropertiesAction;
import org.exbin.framework.editor.xbup.action.EditItemAction;
import org.exbin.framework.editor.xbup.action.ExportItemAction;
import org.exbin.framework.editor.xbup.action.ImportItemAction;
import org.exbin.framework.editor.xbup.action.ItemPropertiesAction;
import org.exbin.framework.editor.xbup.viewer.XbupEditorProvider;
import org.exbin.framework.editor.xbup.viewer.XbupSingleEditorProvider;
import org.exbin.framework.editor.xbup.viewer.XbupMultiEditorProvider;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.action.api.ToolBarGroup;
import org.exbin.framework.action.api.ToolBarPosition;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.service.XBFileType;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.xbup.plugin.XBPluginRepository;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.editor.api.EditorProviderVariant;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.utils.LanguageUtils;

/**
 * XBUP editor module.
 *
 * @version 0.2.1 2021/12/05
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditorXbupModule implements XBApplicationModule {

    public static final String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(EditorXbupModule.class);
    public static final String XB_FILE_TYPE = "XBEditor.XBFileType";

    public static final String XBUP_POPUP_MENU_ID = MODULE_ID + ".xbupPopupMenu";
    private static final String EDIT_ITEM_MENU_GROUP_ID = MODULE_ID + ".editItemMenuGroup";
    private static final String EDIT_ITEM_TOOL_BAR_GROUP_ID = MODULE_ID + ".editItemToolBarGroup";
    private static final String VIEW_MODE_MENU_GROUP_ID = MODULE_ID + ".viewModeMenuGroup";
    public static final String DOC_STATUS_BAR_ID = "docStatusBar";
    public static final String SAMPLE_FILE_SUBMENU_ID = MODULE_ID + ".sampleFileSubMenu";

    private XBApplication application;
    private XbupEditorProvider editorProvider;
    private ResourceBundle resourceBundle;
    private XBACatalog catalog;

    private ViewModeActions viewModeHandler;
    private StatusPanelHandler statusPanelHandler;
    private SampleFilesActions sampleFilesHandler;
    private CatalogsManagerAction catalogBrowserAction;
    private ItemPropertiesAction itemPropertiesAction;
    private DocumentPropertiesAction documentPropertiesAction;
    private ImportItemAction importItemAction;
    private ExportItemAction exportItemAction;
    private AddItemAction addItemAction;
    private EditItemAction editItemAction;
    private JPopupMenu itemPopupMenu;

    private boolean devMode;

    public EditorXbupModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    public void initEditorProvider(EditorProviderVariant variant) {
        switch (variant) {
            case SINGLE: {
                editorProvider = createSingleEditorProvider();
                break;
            }
            case MULTI: {
                editorProvider = createMultiEditorProvider();
                break;
            }
            default:
                throw XBFrameworkUtils.getInvalidTypeException(variant);
        }
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    private XbupEditorProvider createSingleEditorProvider() {
        if (editorProvider == null) {
            editorProvider = new XbupSingleEditorProvider();
            ((XbupSingleEditorProvider) editorProvider).setApplication(application);
            ((XbupSingleEditorProvider) editorProvider).setDevMode(devMode);

//            final DocumentViewerProvider docPanel = (DocumentViewerProvider) editorProvider;
//
//            docPanel.getComponentPanel().setPopupMenu(createPopupMenu());
//            docPanel.addUpdateListener((ActionEvent e) -> {
//                if (docEditingHandler != null) {
//                    docEditingHandler.setAddEnabled(docPanel.isAddEnabled());
//                    docEditingHandler.setEditEnabled(docPanel.isEditEnabled());
//                    propertiesHandler.setEditEnabled(docPanel.isEditEnabled());
//                }
//            });
        }

        return editorProvider;
    }

    @Nonnull
    private XbupEditorProvider createMultiEditorProvider() {
        if (editorProvider == null) {
            editorProvider = new XbupMultiEditorProvider(application);
            ((XbupMultiEditorProvider) editorProvider).setDevMode(devMode);
            ((MultiEditorProvider) editorProvider).addActiveFileChangeListener(e -> {
                updateActionStatus();
            });
        }

        return editorProvider;
    }

    public void updateActionStatus() {
        EditorModuleApi editorModule = application.getModuleRepository().getModuleByInterface(EditorModuleApi.class);
        editorModule.updateActionStatus();
        FileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(FileModuleApi.class);
        fileModule.updateForFileOperations();
    }

    public void registerFileTypes() {
        FileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(FileModuleApi.class);
        fileModule.addFileType(new XBFileType());
    }

    @Nonnull
    public EditorProvider getEditorProvider() {
        return Objects.requireNonNull(editorProvider, "Editor provider was not yet initialized");
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
    private ViewModeActions getViewModeHandler() {
        if (viewModeHandler == null) {
            ensureSetup();
            viewModeHandler = new ViewModeActions();
            viewModeHandler.setup(application, editorProvider, resourceBundle);
        }

        return viewModeHandler;
    }

    @Nonnull
    private StatusPanelHandler getStatusPanelHandler() {
        if (statusPanelHandler == null) {
            ensureSetup();
            statusPanelHandler = new StatusPanelHandler();
            statusPanelHandler.setup(application, editorProvider, resourceBundle);
        }

        return statusPanelHandler;
    }

    @Nonnull
    private SampleFilesActions getSampleFilesHandler() {
        if (sampleFilesHandler == null) {
            ensureSetup();
            sampleFilesHandler = new SampleFilesActions();
            sampleFilesHandler.setup(application, editorProvider, resourceBundle);
        }

        return sampleFilesHandler;
    }

    @Nonnull
    private CatalogsManagerAction getCatalogBrowserAction() {
        if (catalogBrowserAction == null) {
            ensureSetup();
            catalogBrowserAction = new CatalogsManagerAction();
            catalogBrowserAction.setup(application);
            catalogBrowserAction.setCatalog(catalog);
        }

        return catalogBrowserAction;
    }

    @Nonnull
    private ItemPropertiesAction getItemPropertiesAction() {
        if (itemPropertiesAction == null) {
            ensureSetup();
            itemPropertiesAction = new ItemPropertiesAction();
            itemPropertiesAction.setup(editorProvider);
            itemPropertiesAction.setDevMode(devMode);
        }
        return itemPropertiesAction;
    }

    @Nonnull
    private DocumentPropertiesAction getDocumentPropertiesAction() {
        if (documentPropertiesAction == null) {
            ensureSetup();
            documentPropertiesAction = new DocumentPropertiesAction();
            documentPropertiesAction.setup(editorProvider);
        }
        return documentPropertiesAction;
    }

    @Nonnull
    public ImportItemAction getImportItemAction() {
        if (importItemAction == null) {
            ensureSetup();
            importItemAction = new ImportItemAction();
            importItemAction.setup(application, editorProvider, resourceBundle);
        }
        return importItemAction;
    }

    @Nonnull
    public ExportItemAction getExportItemAction() {
        if (exportItemAction == null) {
            ensureSetup();
            exportItemAction = new ExportItemAction();
            exportItemAction.setup(application, editorProvider, resourceBundle);
        }
        return exportItemAction;
    }

    @Nonnull
    public AddItemAction getAddItemAction() {
        if (addItemAction == null) {
            addItemAction = new AddItemAction();
            addItemAction.setup(editorProvider);
        }
        return addItemAction;
    }

    @Nonnull
    public EditItemAction getEditItemAction() {
        if (editItemAction == null) {
            editItemAction = new EditItemAction();
            editItemAction.setup(editorProvider);
        }
        return editItemAction;
    }

    public void registerDocEditingMenuActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuGroup(FrameModuleApi.EDIT_MENU_ID, new MenuGroup(EDIT_ITEM_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM), SeparationMode.AROUND));
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, MODULE_ID, getAddItemAction(), new MenuPosition(EDIT_ITEM_MENU_GROUP_ID));
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, MODULE_ID, getEditItemAction(), new MenuPosition(EDIT_ITEM_MENU_GROUP_ID));
    }

    public void registerDocEditingToolBarActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerToolBarGroup(FrameModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(EDIT_ITEM_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.BOTTOM), SeparationMode.AROUND));
        actionModule.registerToolBarItem(FrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, getAddItemAction(), new ToolBarPosition(EDIT_ITEM_TOOL_BAR_GROUP_ID));
        actionModule.registerToolBarItem(FrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, getEditItemAction(), new ToolBarPosition(EDIT_ITEM_TOOL_BAR_GROUP_ID));
    }

    public void registerViewModeMenu() {
        getViewModeHandler();
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuGroup(FrameModuleApi.VIEW_MENU_ID, new MenuGroup(VIEW_MODE_MENU_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerMenuItem(FrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewModeHandler.getShowViewTabAction(), new MenuPosition(VIEW_MODE_MENU_GROUP_ID));
        actionModule.registerMenuItem(FrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewModeHandler.getShowPropertiesTabAction(), new MenuPosition(VIEW_MODE_MENU_GROUP_ID));
        actionModule.registerMenuItem(FrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewModeHandler.getShowTextTabAction(), new MenuPosition(VIEW_MODE_MENU_GROUP_ID));
        actionModule.registerMenuItem(FrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewModeHandler.getShowBinaryTabAction(), new MenuPosition(VIEW_MODE_MENU_GROUP_ID));
    }

    public void registerStatusBar() {
        getStatusPanelHandler();
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        frameModule.registerStatusBar(MODULE_ID, DOC_STATUS_BAR_ID, statusPanelHandler.getDocStatusPanel());
        frameModule.switchStatusBar(DOC_STATUS_BAR_ID);
        // ((XBDocumentPanel) getEditorProvider()).registerTextStatus(docStatusPanel);
    }

    public void registerSampleFilesSubMenuActions() {
        getSampleFilesHandler();
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenu(SAMPLE_FILE_SUBMENU_ID, MODULE_ID);
        actionModule.registerMenuItem(FrameModuleApi.FILE_MENU_ID, MODULE_ID, SAMPLE_FILE_SUBMENU_ID, "Open Sample File", new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenuItem(SAMPLE_FILE_SUBMENU_ID, MODULE_ID, sampleFilesHandler.getSampleHtmlFileAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(SAMPLE_FILE_SUBMENU_ID, MODULE_ID, sampleFilesHandler.getSamplePictureFileAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(SAMPLE_FILE_SUBMENU_ID, MODULE_ID, sampleFilesHandler.getSampleTypesFileAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerCatalogBrowserMenu() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(FrameModuleApi.TOOLS_MENU_ID, MODULE_ID, getCatalogBrowserAction(), new MenuPosition(PositionMode.TOP));
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = application.getModuleRepository().getModuleByInterface(OptionsModuleApi.class);
        // TODO
    }

    public void registerPropertiesMenuAction() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(FrameModuleApi.FILE_MENU_ID, MODULE_ID, getDocumentPropertiesAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    @Nonnull
    public JPopupMenu getItemPopupMenu() {
        if (itemPopupMenu == null) {
            ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
            actionModule.registerMenu(XBUP_POPUP_MENU_ID, MODULE_ID);
            actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, getAddItemAction(), new MenuPosition(PositionMode.TOP));
            actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, getEditItemAction(), new MenuPosition(PositionMode.TOP));

            actionModule.registerClipboardMenuItems(XBUP_POPUP_MENU_ID, MODULE_ID, SeparationMode.AROUND);

            actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, getImportItemAction(), new MenuPosition(PositionMode.BOTTOM));
            actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, getExportItemAction(), new MenuPosition(PositionMode.BOTTOM));

            actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, getItemPropertiesAction(), new MenuPosition(PositionMode.BOTTOM));
            itemPopupMenu = new JPopupMenu();
            actionModule.buildMenu(itemPopupMenu, XBUP_POPUP_MENU_ID);
        }

        return itemPopupMenu;
    }

    @Nonnull
    public ClientConnectionListener getClientConnectionListener() {
        return getStatusPanelHandler().getClientConnectionListener();
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        editorProvider.setCatalog(catalog);
        if (catalogBrowserAction != null) {
            catalogBrowserAction.setCatalog(catalog);
        }
    }

    public void setPluginRepository(XBPluginRepository pluginRepository) {
        editorProvider.setPluginRepository(pluginRepository);
    }
}
