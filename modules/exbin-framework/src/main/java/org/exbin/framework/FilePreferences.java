/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * File preferences class.
 *
 * @version 0.2.0 2016/11/29
 * @author ExBin Project (http://exbin.org)
 */
public class FilePreferences extends AbstractPreferences {

    private static final String PRECERENCES_DTD_URI = "http://java.sun.com/dtd/preferences.dtd";
    private static final String MAP_XML_VERSION_ATTRIBUTE = "MAP_XML_VERSION";
    private static final String MAP_XML_VERSION_VALUE = "1.0";
    private static final String PREFERENCES_DTD
            = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<!-- DTD for preferences -->"
            + "<!ELEMENT map (entry*) >"
            + "<!ATTLIST map"
            + "  MAP_XML_VERSION CDATA \"0.0\"  >"
            + "<!ELEMENT entry EMPTY >"
            + "<!ATTLIST entry"
            + "          key CDATA #REQUIRED"
            + "          value CDATA #REQUIRED >";

    private final File preferencesFile;
    private final Map<String, String> spiValues;
    private Map<String, FilePreferences> children;

    public FilePreferences() {
        this(null, "");
    }

    public FilePreferences(AbstractPreferences parent, String name) {
        super(parent, name);
        this.spiValues = new TreeMap<>();
        this.children = new TreeMap<>();
        preferencesFile = FilePreferencesFactory.getPreferencesFile(absolutePath());

        try {
            sync();
        } catch (BackingStoreException ex) {
            Logger.getLogger(FilePreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void putSpi(String key, String value) {
        spiValues.put(key, value);
        try {
            flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(FilePreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected String getSpi(String key) {
        return spiValues.get(key);
    }

    @Override
    protected void removeSpi(String key) {
        spiValues.remove(key);
        try {
            flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(FilePreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void removeNodeSpi() throws BackingStoreException {
        removeNode();
        flush();
    }

    @Override
    protected String[] keysSpi() throws BackingStoreException {
        Set<String> keySet = spiValues.keySet();
        return (String[]) keySet.toArray(new String[keySet.size()]);
    }

    @Override
    protected String[] childrenNamesSpi() throws BackingStoreException {
        Set<String> keySet = children.keySet();
        return (String[]) keySet.toArray(new String[keySet.size()]);
    }

    @Override
    protected AbstractPreferences childSpi(String name) {
        FilePreferences child = children.get(name);
        if (child == null || child.isRemoved()) {
            child = new FilePreferences(this, name);
            children.put(name, child);
        }
        return child;
    }

    @Override
    protected void syncSpi() throws BackingStoreException {
        if (isRemoved()) {
            clear();
            return;
        }

        if (!preferencesFile.exists()) {
            return;
        }

        synchronized (preferencesFile) {
            try (FileInputStream fileStream = new FileInputStream(preferencesFile)) {
                importFromStream(fileStream, this);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FilePreferences.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FilePreferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    protected void flushSpi() throws BackingStoreException {
        synchronized (preferencesFile) {
            if (!preferencesFile.exists()) {
                try {
                    preferencesFile.getParentFile().mkdirs();
                    preferencesFile.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(FilePreferences.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try (FileOutputStream fileStream = new FileOutputStream(preferencesFile)) {
                exportToStream(fileStream, this);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FilePreferences.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FilePreferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static void exportToStream(OutputStream os, final FilePreferences p)
            throws IOException, BackingStoreException {
        Document doc = createPreferencesDoc("map");
        Element preferences = doc.getDocumentElement();
        preferences.setAttribute(MAP_XML_VERSION_ATTRIBUTE, MAP_XML_VERSION_VALUE);
        putPreferencesInXml(doc.getDocumentElement(), doc, p);

        writeXmlDocument(doc, os);
    }

    /**
     * Creates preferences XML document with given root node qualified name.
     *
     * @param qname root node qualified name
     * @return XML document
     */
    private static Document createPreferencesDoc(String qname) {
        try {
            DOMImplementation di = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().getDOMImplementation();
            DocumentType dt = di.createDocumentType(qname, null, PRECERENCES_DTD_URI);
            return di.createDocument(null, qname, dt);
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Puts the preferences in the specified Preferences node into the specified
     * XML element which is assumed to represent a node in the specified XML
     * document which is assumed to conform to PREFERENCES_DTD.
     *
     * @throws BackingStoreException if it is not possible to read the
     * preferences or children out of the specified preferences node.
     */
    private static void putPreferencesInXml(Element map, Document doc,
            FilePreferences prefs) throws BackingStoreException {
        // Node is locked to export its contents
        synchronized (prefs.lock) {
            // Check if this node was concurrently removed. If yes
            // remove it from XML Document and return.
            if (prefs.isRemoved()) {
                NodeList childNodes = map.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    map.removeChild(childNodes.item(i));
                }
                return;
            }

            // Put map in xml element
            String[] keys = prefs.keys();
            for (String key : keys) {
                Element entry = (Element) map.appendChild(doc.createElement("entry"));
                entry.setAttribute("key", key);
                // NEXT STATEMENT THROWS NULL PTR EXC INSTEAD OF ASSERT FAIL
                entry.setAttribute("value", prefs.get(key, null));
            }
            // release lock
        }
    }

    /**
     * Writes XML document to the specified output stream.
     */
    private static void writeXmlDocument(Document doc, OutputStream out)
            throws IOException {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            try {
                tf.setAttribute("indent-number", 2);
            } catch (IllegalArgumentException iae) {
                // Ignore the IAE. Should not fail the writeout even the
                // transformer provider does not support "indent-number".
            }
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doc.getDoctype().getSystemId());
            t.setOutputProperty(OutputKeys.INDENT, "yes");

            // Transformer resets the "indent" info if the "result" is a StreamResult with
            // an OutputStream object embedded, creating a Writer object on top of that
            // OutputStream object however works.
            t.transform(new DOMSource(doc),
                    new StreamResult(new BufferedWriter(new OutputStreamWriter(out, "UTF-8"))));
        } catch (TransformerException e) {
            throw new AssertionError(e);
        }
    }

    private void importFromStream(FileInputStream fileStream, FilePreferences p) {
        // Lock for complete import
        synchronized (p.lock) {
            try {
                Document doc = loadPrefsDoc(fileStream);
                importPrefs(p, doc.getDocumentElement());
            } catch (SAXException | IOException ex) {
                Logger.getLogger(FilePreferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Imports the preferences described by the specified XML element (a map
     * from a preferences document) into the specified preferences node.
     */
    private static void importPrefs(Preferences prefsNode, Element map) {
        NodeList entries = map.getChildNodes();
        for (int i = 0, numEntries = entries.getLength(); i < numEntries; i++) {
            Element entry = (Element) entries.item(i);
            prefsNode.put(entry.getAttribute("key"),
                    entry.getAttribute("value"));
        }
    }

    /**
     * Loads an XML document from specified input stream, which must have the
     * requisite DTD URI.
     */
    private static Document loadPrefsDoc(InputStream in)
            throws SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setValidating(true);
        dbf.setCoalescing(true);
        dbf.setIgnoringComments(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setEntityResolver(new Resolver());
            db.setErrorHandler(new RethrowErrorHandler());
            return db.parse(new InputSource(in));
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
    }

    private static class Resolver implements EntityResolver {

        @Override
        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException {
            if (systemId.equals(PRECERENCES_DTD_URI)) {
                InputSource is = new InputSource(new StringReader(PREFERENCES_DTD));
                is.setSystemId(PRECERENCES_DTD_URI);
                return is;
            }
            throw new SAXException("Invalid system identifier: " + systemId);
        }
    }

    private static class RethrowErrorHandler implements ErrorHandler {

        @Override
        public void error(SAXParseException ex) throws SAXException {
            throw ex;
        }

        @Override
        public void fatalError(SAXParseException ex) throws SAXException {
            throw ex;
        }

        @Override
        public void warning(SAXParseException ex) throws SAXException {
            throw ex;
        }
    }
}
