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
package org.exbin.framework.basic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Basic module provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BasicModuleProvider implements ModuleProvider {

    private static final String MODULE_ID = "MODULE_ID";
    private static final String MODULE_FILE = "module.xml";
    private final Map<String, ModuleRecord> modules = new HashMap<>();
    private DynamicClassLoader contextClassLoader = new DynamicClassLoader();

    public BasicModuleProvider() {
    }

    @Override
    public void launch(Runnable runnable) {
        Thread runThread = new Thread(runnable);
        runThread.setContextClassLoader(contextClassLoader);
        runThread.start();
    }

    @Nonnull
    @Override
    public <T extends Module> T getModule(Class<T> interfaceClass) {
        try {
            Field declaredField = interfaceClass.getDeclaredField(MODULE_ID);
            if (declaredField != null) {
                Object moduleId = declaredField.get(null);
                if (moduleId instanceof String) {
                    @SuppressWarnings("unchecked")
                    T module = (T) getModuleById((String) moduleId);
                    return module;
                }
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(BasicModuleProvider.class.getName()).log(Level.SEVERE, null, ex);
        }

        throw new IllegalArgumentException("Module for class " + interfaceClass.getCanonicalName() + " was not found.");
    }

    public void addModulesFrom(URI pathUri) {
        File directory = new File(pathUri);
        if (directory.exists() && directory.isDirectory()) {
            File[] jarFiles = directory.listFiles((File pathname) -> pathname.isFile() && pathname.getName().endsWith(".jar"));
            for (File jarFile : jarFiles) {
                addModulePlugin(jarFile.toURI(), false);
            }
        }
    }

    public void addModulesFrom(URL moduleClassUrl) {
        try {
            addModulesFrom(moduleClassUrl.toURI());
        } catch (URISyntaxException ex) {
            Logger.getLogger(BasicModuleProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addModulesFromPath(URI pathUri) {
        File directory = new File(pathUri);
        if (directory.exists() && directory.isDirectory()) {
            File[] jarFiles = directory.listFiles((File pathname) -> pathname.isFile() && pathname.getName().endsWith(".jar"));
            for (File jarFile : jarFiles) {
                addModulePlugin(jarFile.toURI(), false);
            }
        }
    }

    public void addModulesFromPath(URL pathUrl) {
        try {
            addModulesFromPath(pathUrl.toURI());
        } catch (URISyntaxException ex) {
            Logger.getLogger(BasicModuleProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addClassPathModules() {
        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);
        for (String classpathEntry : classpathEntries) {
            addModulePlugin(new File(classpathEntry).toURI(), true);
        }
    }

    public void addModulesFromManifest(Class manifestClass) {
        try {
            URL moduleClassLocation = manifestClass.getProtectionDomain().getCodeSource().getLocation();
            URL manifestUrl = new URL("jar:" + moduleClassLocation.toExternalForm() + "!/META-INF/MANIFEST.MF");

            Manifest manifest = new Manifest(manifestUrl.openStream());
            String classPaths = manifest.getMainAttributes().getValue(new Attributes.Name("Class-Path"));
            String[] paths = classPaths.split(" ");
            String rootDirectory = new File(moduleClassLocation.toURI()).getParentFile().toURI().toString();
            for (String path : paths) {
                try {
                    addModulePlugin(new URI(rootDirectory + path), true);
                } catch (URISyntaxException ex) {
                    // Ignore
                }
            }
        } catch (IOException | URISyntaxException ex) {
            // Ignore
        }
    }

    @Nonnull
    public ClassLoader getContextClassLoader() {
        return contextClassLoader;
    }

    @Nonnull
    private Module loadModule(BasicModuleRecord moduleRecord) {
        Module module = moduleRecord.getModule();
        String moduleId = moduleRecord.getModuleId();
        if (moduleId.isEmpty()) {
            throw new IllegalStateException("Library is not a module");
        }
        
        if (!(module instanceof BasicModuleRecord.ModuleLink)) {
            throw new IllegalStateException("Module is already loaded");
        }

        boolean preloaded = ((BasicModuleRecord.ModuleLink) module).isPreloaded();
        URI moduleLink = ((BasicModuleRecord.ModuleLink) module).getModuleLink();

        if (preloaded) {
            try {
                contextClassLoader.add(moduleLink.toURL());
                Class<?> clazz = contextClassLoader.loadClass(moduleId);
                moduleRecord.setClassLoader(contextClassLoader);
                Constructor<?> ctor = clazz.getConstructor();
                module = (Module) ctor.newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | MalformedURLException | NoClassDefFoundError ex) {
                Logger.getLogger(BasicModuleProvider.class.getName()).log(Level.SEVERE, "Module: " + moduleRecord.getModuleId(), ex);
                // ignore
            }
        } else {
            try {
                DynamicClassLoader loader;
                //                    try {
                //                        loader = new DynamicClassLoader("classpath", contextClassLoader);
                //                    } catch (Throwable tw) {
                // Alternative when executed from Java 8
                loader = new DynamicClassLoader(contextClassLoader);
                //                    }

                loader.add(moduleLink.toURL());
                Class<?> clazz = Class.forName(moduleRecord.getModuleId(), true, loader);

                //                    if (LookAndFeelApplier.class.isAssignableFrom(clazz)) {
                //                        loader = contextClassLoader;
                //                        loader.add(libraryUri.toURL());
                //                        clazz = Class.forName(moduleRecord.getModuleId(), true, loader);
                //                    }
                moduleRecord.setClassLoader(loader);
                Constructor<?> ctor = clazz.getConstructor();
                module = (Module) ctor.newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | MalformedURLException | NoClassDefFoundError ex) {
                Logger.getLogger(BasicModuleProvider.class.getName()).log(Level.SEVERE, "Module: " + moduleRecord.getModuleId(), ex);
                // ignore
            }
        }
        moduleRecord.setModule(module);

        return module;
    }

    /**
     * Attempts to load main library class if library URL contains valid module
     * declaration.
     *
     * @param libraryUri library URI
     */
    @Nonnull
    private BasicModuleRecord addModulePlugin(URI libraryUri, boolean preloaded) {
        final BasicModuleRecord moduleRecord = new BasicModuleRecord();
        moduleRecord.setClassLoader(contextClassLoader);
        moduleRecord.setModule(new BasicModuleRecord.ModuleLink(libraryUri, preloaded));

        URL moduleRecordUrl = null;
        InputStream moduleRecordStream = null;
        try {
            moduleRecordUrl = new URL("jar:" + libraryUri.toURL().toExternalForm() + "!/META-INF/" + MODULE_FILE);
            moduleRecordStream = moduleRecordUrl.openStream();

            // TODO Don't guess it from file name
            String fileName = libraryUri.toURL().getFile();
            if (fileName.endsWith(".jar")) {
                int lastIndex = fileName.lastIndexOf("-");
                if (lastIndex > 0) {
                    if (fileName.length() > lastIndex + 9 && "SNAPSHOT".equals(fileName.substring(lastIndex + 1, lastIndex + 9))) {
                        int prevIndex = fileName.lastIndexOf("-", lastIndex - 1);
                        if (prevIndex > 0) {
                            moduleRecord.setVersion(fileName.substring(prevIndex + 1, fileName.length() - 4));
                        }
                    } else {
                        moduleRecord.setVersion(fileName.substring(lastIndex + 1, fileName.length() - 4));
                    }
                }
            }
        } catch (IOException ex) {
            // ignore
        }

        if (moduleRecordStream != null) {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(moduleRecordStream);
                NodeList moduleNodeList = document.getElementsByTagName("module");
                if (moduleNodeList.getLength() > 0) {
                    Node moduleNode = moduleNodeList.item(0);
                    NodeList childModuleNode = moduleNode.getChildNodes();
                    for (int i = 0; i < childModuleNode.getLength(); i++) {
                        Node node = childModuleNode.item(i);
                        if ("id".equals(node.getNodeName())) {
                            moduleRecord.setModuleId(node.getTextContent());
                        } else if ("name".equals(node.getNodeName())) {
                            moduleRecord.setName(node.getTextContent());
                        } else if ("description".equals(node.getNodeName())) {
                            moduleRecord.setDescription(node.getTextContent());
                        } else if ("homepage".equals(node.getNodeName())) {
                            moduleRecord.setHomepage(node.getTextContent());
                        } else if ("provider".equals(node.getNodeName())) {
                            moduleRecord.setProvider(node.getTextContent());
                        }
                    }
                }
//                XBPullReader pullReader = new XBPullReader(moduleRecordStream);
//                XBPProviderSerialHandler serial = new XBPProviderSerialHandler(new XBToXBTPullConvertor(pullReader));
//                serial.process(moduleInfo);
            } catch (IOException | SAXException | ParserConfigurationException ex) {
                Logger.getLogger(BasicModuleProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        modules.put(moduleRecord.getModuleId(), moduleRecord);

        return moduleRecord;
    }

    private void addModule(Module module) {
        String canonicalName = module.getClass().getCanonicalName();
        BasicModuleRecord moduleInfo = new BasicModuleRecord(canonicalName, module, contextClassLoader);
        URL moduleClassLocation = moduleInfo.getClass().getProtectionDomain().getCodeSource().getLocation();
        URL moduleRecordUrl;
        InputStream moduleRecordStream = null;
        try {
            moduleRecordUrl = new URL("jar:" + moduleClassLocation.toExternalForm() + "!/META-INF/" + MODULE_FILE);
            moduleRecordStream = moduleRecordUrl.openStream();
        } catch (IOException ex) {
            // ignore
        }
        /*        if (moduleRecordStream != null) {
            try {
                XBPullReader pullReader = new XBPullReader(moduleRecordStream);
                XBPProviderSerialHandler serial = new XBPProviderSerialHandler(new XBToXBTPullConvertor(pullReader));
                serial.process(moduleInfo);
            } catch (IOException ex) {
                // ignore
            }
        } */
        modules.put(canonicalName, moduleInfo);
    }

    /**
     * Initializes all modules in order of their dependencies.
     */
    public void initModules() {
        List<ModuleRecord> unprocessedModules = new ArrayList<>(modules.values());
        // Priority modules first, ignore dependecy for now
        {
            int moduleIndex = 0;
            while (moduleIndex < unprocessedModules.size()) {
                ModuleRecord moduleRecord = unprocessedModules.get(moduleIndex);
//                Module module = moduleRecord.getModule();
//                if (module instanceof LookAndFeelApplier) {
                ////                    module.init(moduleHandler);
//                    unprocessedModules.remove(moduleIndex);
//                } else {
                moduleIndex++;
//                }
            }
        }

        // Process dependencies
        int preRoundCount;
        int postRoundCount;
        do {
            preRoundCount = unprocessedModules.size();

            int moduleIndex = 0;
            while (moduleIndex < unprocessedModules.size()) {
                ModuleRecord moduleRecord = unprocessedModules.get(moduleIndex);
                // Process single module
                List<String> dependencyModuleIds = moduleRecord.getDependencyModuleIds();
                boolean dependecySatisfied = true;
                for (String dependecyModuleId : dependencyModuleIds) {
                    ModuleRecord dependecyModule = getModuleRecordById(dependecyModuleId);
                    if (dependecyModule == null || findModule(unprocessedModules, dependecyModuleId)) {
                        dependecySatisfied = false;
                        break;
                    }
                }

                if (dependecySatisfied) {
                    Module module = moduleRecord.getModule();
                    String moduleId = ((BasicModuleRecord) moduleRecord).getModuleId();
                    if (module instanceof BasicModuleRecord.ModuleLink && !moduleId.isEmpty()) {
                        loadModule((BasicModuleRecord) moduleRecord);
                    }
//                    module.init(moduleHandler);
                    unprocessedModules.remove(moduleIndex);
                } else {
                    moduleIndex++;
                }
            }

            postRoundCount = unprocessedModules.size();
        } while (postRoundCount > 0 && postRoundCount < preRoundCount);

        if (postRoundCount > 0) {
            throw new IllegalStateException("Circular dependency detected");
        }
    }

    /**
     * Gets info about module.
     *
     * @param moduleId module identifier
     * @return application module record
     */
    @Nullable
    public ModuleRecord getModuleRecordById(String moduleId) {
        return modules.get(moduleId);
    }

    private boolean findModule(List<ModuleRecord> modules, String moduleId) {
        return modules.stream().anyMatch((module) -> (moduleId.equals(module.getModuleId())));
    }

    /**
     * Gets info about module.
     *
     * @param moduleId module identifier
     * @return application module record
     * @throws IllegalArgumentException when module not found
     */
    @Nonnull
    public Module getModuleById(String moduleId) {
        ModuleRecord moduleRecord = getModuleRecordById(moduleId);
        if (moduleRecord == null) {
            throw new IllegalArgumentException("Module for id " + moduleId + " was not found.");
        }
        Module module = moduleRecord.getModule();
        if (module instanceof BasicModuleRecord.ModuleLink) {
            throw new IllegalArgumentException("Module for id " + moduleId + " is not loaded.");

        }
        return module;
    }

    /**
     * Gets list of modules.
     *
     * @return list of modules
     */
    @Nonnull
    public List<ModuleRecord> getModulesList() {
        return new ArrayList<>(modules.values());
    }
}
