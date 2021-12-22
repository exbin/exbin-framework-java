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
import org.exbin.framework.editor.xbup.catalog.action.CatalogBrowserAction;
import org.exbin.framework.editor.xbup.action.DocumentPropertiesAction;
import org.exbin.framework.editor.xbup.action.EditItemAction;
import org.exbin.framework.editor.xbup.action.ExportItemAction;
import org.exbin.framework.editor.xbup.action.ImportItemAction;
import org.exbin.framework.editor.xbup.action.ItemPropertiesAction;
import org.exbin.framework.editor.xbup.viewer.XbupEditorProvider;
import org.exbin.framework.editor.xbup.viewer.XbupSingleEditorProvider;
import org.exbin.framework.editor.xbup.viewer.XbupMultiEditorProvider;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.action.api.MenuGroup;
import org.exbin.framework.gui.action.api.MenuPosition;
import org.exbin.framework.gui.action.api.PositionMode;
import org.exbin.framework.gui.action.api.SeparationMode;
import org.exbin.framework.gui.action.api.ToolBarGroup;
import org.exbin.framework.gui.action.api.ToolBarPosition;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.service.XBFileType;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.xbup.plugin.XBPluginRepository;
import org.exbin.framework.gui.action.api.GuiActionModuleApi;
import org.exbin.framework.gui.editor.api.EditorProviderVariant;
import org.exbin.framework.gui.utils.LanguageUtils;

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
    private XBUndoHandler undoHandler;

    private ViewModeActions viewModeHandler;
    private StatusPanelHandler statusPanelHandler;
    private SampleFilesActions sampleFilesHandler;
    private CatalogBrowserAction catalogBrowserAction;
    private ItemPropertiesAction itemPropertiesAction;
    private DocumentPropertiesAction documentPropertiesAction;
    private ImportItemAction importItemAction;
    private ExportItemAction exportItemAction;
    private AddItemAction addItemAction;
    private EditItemAction editItemAction;

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
            editorProvider = new XbupSingleEditorProvider(undoHandler);
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
        }

        return editorProvider;
    }

    public void registerFileTypes() {
        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
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
    private CatalogBrowserAction getCatalogBrowserAction() {
        if (catalogBrowserAction == null) {
            ensureSetup();
            catalogBrowserAction = new CatalogBrowserAction();
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
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuGroup(GuiFrameModuleApi.EDIT_MENU_ID, new MenuGroup(EDIT_ITEM_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM), SeparationMode.AROUND));
        actionModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, getAddItemAction(), new MenuPosition(EDIT_ITEM_MENU_GROUP_ID));
        actionModule.registerMenuItem(GuiFrameModuleApi.EDIT_MENU_ID, MODULE_ID, getEditItemAction(), new MenuPosition(EDIT_ITEM_MENU_GROUP_ID));
    }

    public void registerDocEditingToolBarActions() {
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerToolBarGroup(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(EDIT_ITEM_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.BOTTOM), SeparationMode.AROUND));
        actionModule.registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, getAddItemAction(), new ToolBarPosition(EDIT_ITEM_TOOL_BAR_GROUP_ID));
        actionModule.registerToolBarItem(GuiFrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, getEditItemAction(), new ToolBarPosition(EDIT_ITEM_TOOL_BAR_GROUP_ID));
    }

    public void registerViewModeMenu() {
        getViewModeHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuGroup(GuiFrameModuleApi.VIEW_MENU_ID, new MenuGroup(VIEW_MODE_MENU_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewModeHandler.getShowViewTabAction(), new MenuPosition(VIEW_MODE_MENU_GROUP_ID));
        actionModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewModeHandler.getShowPropertiesTabAction(), new MenuPosition(VIEW_MODE_MENU_GROUP_ID));
        actionModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewModeHandler.getShowTextTabAction(), new MenuPosition(VIEW_MODE_MENU_GROUP_ID));
        actionModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, viewModeHandler.getShowBinaryTabAction(), new MenuPosition(VIEW_MODE_MENU_GROUP_ID));
    }

    public void registerStatusBar() {
        getStatusPanelHandler();
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        frameModule.registerStatusBar(MODULE_ID, DOC_STATUS_BAR_ID, statusPanelHandler.getDocStatusPanel());
        frameModule.switchStatusBar(DOC_STATUS_BAR_ID);
        // ((XBDocumentPanel) getEditorProvider()).registerTextStatus(docStatusPanel);
    }

    public void registerSampleFilesSubMenuActions() {
        getSampleFilesHandler();
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenu(SAMPLE_FILE_SUBMENU_ID, MODULE_ID);
        actionModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, SAMPLE_FILE_SUBMENU_ID, "Open Sample File", new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenuItem(SAMPLE_FILE_SUBMENU_ID, MODULE_ID, sampleFilesHandler.getSampleHtmlFileAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(SAMPLE_FILE_SUBMENU_ID, MODULE_ID, sampleFilesHandler.getSamplePictureFileAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(SAMPLE_FILE_SUBMENU_ID, MODULE_ID, sampleFilesHandler.getSampleTypesFileAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerCatalogBrowserMenu() {
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, getCatalogBrowserAction(), new MenuPosition(PositionMode.TOP));
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public void registerOptionsPanels() {
        GuiOptionsModuleApi optionsModule = application.getModuleRepository().getModuleByInterface(GuiOptionsModuleApi.class);
        // TODO
    }

    public void registerPropertiesMenuAction() {
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, getDocumentPropertiesAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    @Nonnull
    public JPopupMenu createItemPopupMenu() {
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenu(XBUP_POPUP_MENU_ID, MODULE_ID);
        actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, getAddItemAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, getEditItemAction(), new MenuPosition(PositionMode.TOP));

        actionModule.registerClipboardMenuItems(XBUP_POPUP_MENU_ID, MODULE_ID, SeparationMode.AROUND);

        actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, getImportItemAction(), new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, getExportItemAction(), new MenuPosition(PositionMode.BOTTOM));

        actionModule.registerMenuItem(XBUP_POPUP_MENU_ID, MODULE_ID, getItemPropertiesAction(), new MenuPosition(PositionMode.BOTTOM));
        JPopupMenu popupMenu = new JPopupMenu();
        actionModule.buildMenu(popupMenu, XBUP_POPUP_MENU_ID);
        return popupMenu;
    }

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

    public void setUndoHandler(XBUndoHandler undoHandler) {
        this.undoHandler = undoHandler;
    }

    public void setPluginRepository(XBPluginRepository pluginRepository) {
        editorProvider.setPluginRepository(pluginRepository);
    }
}
