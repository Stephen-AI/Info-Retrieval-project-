package ir.vsr;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple data structure for storing a reference to a document file
 * that includes information on the length of its document vector.
 * The goal is to have a lightweight object to store in an inverted index
 * without having to store an entire Document object.
 *
 * @author Ray Mooney
 */

public class DocumentReference {
    /**
     * The file where the referenced document is stored.
     */
    public File file = null;
    /**
     * The length of the corresponding Document vector.
     */
    public double length = 0.0;

    /**
     * ADDED:
     * The locations of every token in the document
     */
    public Map<String, List<Integer>> tokenLocations;

    public DocumentReference(File file, double length) {
        this.file = file;
        this.length = length;
    }

    /**
     * Create a reference to this document, initializing its length to 0
     */
    public DocumentReference(FileDocument doc) {
        this(doc.file, 0.0);
        this.tokenLocations = doc.getTokenLocations();
    }

    public String toString() {
        return file.getName();
    }

    /**
     * Get the full Document for this Document reference by recreating it
     * with the given docType and stemming
     */
    public Document getDocument(short docType, boolean stem) {
        Document doc = null;
        switch (docType) {
            case DocumentIterator.TYPE_TEXT:
                doc = new TextFileDocument(file, stem);
                break;
            case DocumentIterator.TYPE_HTML:
                doc = new HTMLFileDocument(file, stem);
                break;
        }
        return doc;
    }

    /**
     * ADDED:
     * Set the token-location mapping for this document reference
     * @param tokenLocations token-locations mapping
     */
    public void setTokenLocations(Map<String, List<Integer>> tokenLocations) {
        this.tokenLocations = tokenLocations;
    }

    public Map<String, List<Integer>> getTokenLocations() {
        return this.tokenLocations;
    }
}
