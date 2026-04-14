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
package org.exbin.jaguif.file;

import java.awt.Component;
import org.exbin.jaguif.file.api.FileDialogsProvider;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JOptionPane;
import org.exbin.jaguif.App;
import org.exbin.jaguif.document.api.Document;
import org.exbin.jaguif.document.api.DocumentManagement;
import org.exbin.jaguif.document.api.DocumentModuleApi;
import org.exbin.jaguif.file.api.ContextFileDialogs;
import org.exbin.jaguif.file.api.FileSourceIdentifier;
import org.exbin.jaguif.file.api.FileType;
import org.exbin.jaguif.file.api.FileModuleApi;
import org.exbin.jaguif.file.api.FileUsageListener;
import org.exbin.jaguif.file.api.SaveModifiedResult;
import org.exbin.jaguif.file.settings.FileOptions;
import org.exbin.jaguif.file.settings.FileSettingsApplier;
import org.exbin.jaguif.file.settings.FileSettingsComponent;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.ApplySettingsContribution;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.SettingsComponentContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContributionRule;

/**
 * Framework file module.
 */
@ParametersAreNonnullByDefault
public class FileModule implements FileModuleApi {

    public static final String SETTINGS_PAGE_ID = "file";
    private java.util.ResourceBundle resourceBundle = null;

    private final List<FileType> registeredFileTypes = new ArrayList<>();
    private final List<FileUsageListener> fileUsageListeners = new ArrayList<>();
    private final Map<String, FileDialogsProvider> fileDialogsProviders = new HashMap<>();
    private String fileDialogProviderId = "";

    public FileModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(FileModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Override
    public void addFileType(FileType fileType) {
        registeredFileTypes.add(fileType);
    }

    @Nonnull
    @Override
    public Collection<FileType> getFileTypes() {
        return Collections.unmodifiableCollection(registeredFileTypes);
    }

    @Override
    public void registerFileDialogsProvider(String providerId, FileDialogsProvider provider) {
        fileDialogsProviders.put(providerId, provider);
    }

    @Nonnull
    @Override
    public Map<String, FileDialogsProvider> getFileDialogsProviders() {
        return fileDialogsProviders;
    }

    @Nonnull
    @Override
    public String getFileDialogProviderId() {
        return fileDialogProviderId;
    }

    @Override
    public void setFileDialogProviderId(String fileDialogProviderId) {
        this.fileDialogProviderId = fileDialogProviderId;
    }

    @Nonnull
    @Override
    public FileDialogsProvider getFileDialogsProvider() {
        FileDialogsProvider fileDialogsProvider = fileDialogsProviders.get(fileDialogProviderId);
        if (fileDialogsProvider == null) {
            throw new IllegalStateException();
        }
        return fileDialogsProvider;
    }

    @Override
    public void openFile(String filename) {
        FileSourceIdentifier documentSource;
        try {
            documentSource = new FileSourceIdentifier(new URI(filename));
            DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
            DocumentManagement documentManager = documentModule.getMainDocumentManager();
            Document document = documentManager.createDocumentForSource(documentSource);
            documentManager.receiveDocument(document);
        } catch (URISyntaxException ex) {
            Logger.getLogger(FileModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void openFile(URI fileUri) {
        FileSourceIdentifier documentSource = new FileSourceIdentifier(fileUri);
        DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
        DocumentManagement documentManager = documentModule.getMainDocumentManager();
        Document document = documentManager.createDocumentForSource(documentSource);
        documentManager.receiveDocument(document);
    }

    @Override
    public void registerFileProviders() {
        ensureSetup();
        getResourceBundle();
        registerFileDialogsProvider(FileDialogsType.SWING.name(), new SwingFileDialogsProvider(resourceBundle));
        registerFileDialogsProvider(FileDialogsType.AWT.name(), new AwtFileDialogsProvider(resourceBundle));
        setFileDialogProviderId(FileDialogsType.SWING.name());

        DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
        DocumentManagement documentManager = documentModule.getMainDocumentManager();
        documentManager.registerDocumentProvider(new FileDocumentProvider());
    }

    @Override
    public void registerSettings() {
        getResourceBundle();
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerSettingsOptions(FileOptions.class, (optionsStorage) -> new FileOptions(optionsStorage));

        settingsManagement.registerApplySetting(FileOptions.class, new ApplySettingsContribution(FileSettingsApplier.APPLIER_ID, new FileSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextFileDialogs.class, new ApplySettingsContribution(FileSettingsApplier.APPLIER_ID, new FileSettingsApplier()));

        SettingsPageContribution pageContribution = new SettingsPageContribution(SETTINGS_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(pageContribution);
        SettingsComponentContribution settingsComponent = settingsManagement.registerComponent(FileSettingsComponent.COMPONENT_ID, new FileSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(pageContribution));
    }

    @Override
    public void addFileUsageListener(FileUsageListener listener) {
        fileUsageListeners.add(listener);
    }

    @Override
    public void removeFileUsageListener(FileUsageListener listener) {
        fileUsageListeners.remove(listener);
    }

    @Override
    public void notifyFileUsed(URI fileUri, @Nullable FileType fileType) {
        // TODO Replace with messaging
        for (FileUsageListener fileUsageListener : fileUsageListeners) {
            fileUsageListener.fileUsed(fileUri, fileType);
        }
    }

    @Nonnull
    @Override
    public SaveModifiedResult showSaveModified(Component parentComponent) {
        getResourceBundle();
        Object[] options = {
            resourceBundle.getString("saveModifiedQuestion.action_save"),
            resourceBundle.getString("saveModifiedQuestion.action_discard"),
            resourceBundle.getString("saveModifiedQuestion.action_cancel")
        };
        int result = JOptionPane.showOptionDialog(
                parentComponent,
                resourceBundle.getString("saveModifiedQuestion.message"),
                resourceBundle.getString("saveModifiedQuestion.title"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (result == JOptionPane.YES_OPTION) {
            return SaveModifiedResult.SAVE;
        } else if (result == JOptionPane.NO_OPTION) {
            return SaveModifiedResult.DISCARD;
        }
        return SaveModifiedResult.CANCEL;
    }

    @Override
    public boolean showAskToOverwrite(Component parentComponent) {
        getResourceBundle();
        Object[] options = {
            resourceBundle.getString("owerwriteFileQuestion.action_save"),
            resourceBundle.getString("owerwriteFileQuestion.action_cancel")
        };

        int result = JOptionPane.showOptionDialog(
                parentComponent,
                resourceBundle.getString("owerwriteFileQuestion.message"),
                resourceBundle.getString("owerwriteFileQuestion.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (result == JOptionPane.YES_OPTION) {
            return true;
        }
        if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION) {
            return false;
        }

        return false;
    }

    @Override
    public void showFileNotFound(Component parentComponent, String filePath) {
        getResourceBundle();
        JOptionPane.showOptionDialog(parentComponent,
                resourceBundle.getString("fileNotFound.title"),
                String.format(resourceBundle.getString("fileNotFound.message"), filePath),
                JOptionPane.CLOSED_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null, null, null);
    }

    @Override
    public void showUnableToSave(Component parentComponent, Exception ex) {
        getResourceBundle();
        String errorMessage = ex.getLocalizedMessage();
        JOptionPane.showMessageDialog(
                parentComponent,
                String.format(resourceBundle.getString("unableToSaveMessage.message"), ex.getClass().getCanonicalName() + (errorMessage == null || errorMessage.isEmpty() ? "" : errorMessage)),
                resourceBundle.getString("unableToSaveMessage.title"), JOptionPane.ERROR_MESSAGE
        );
    }
}
