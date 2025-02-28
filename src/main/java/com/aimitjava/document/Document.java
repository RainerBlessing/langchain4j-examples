package com.aimitjava.document;

import java.util.HashMap;
import java.util.Map;

/**
 * Repräsentiert ein Dokument mit Textinhalt und Metadaten.
 */
public class Document {
    private final String text;
    private final Map<String, Object> metadata;

    /**
     * Erzeugt ein neues Dokument mit dem angegebenen Text und leeren Metadaten.
     *
     * @param text Der Textinhalt des Dokuments
     */
    public Document(String text) {
        this(text, new HashMap<>());
    }

    /**
     * Erzeugt ein neues Dokument mit dem angegebenen Text und Metadaten.
     *
     * @param text Der Textinhalt des Dokuments
     * @param metadata Die Metadaten des Dokuments
     */
    public Document(String text, Map<String, Object> metadata) {
        this.text = text;
        this.metadata = new HashMap<>(metadata);
    }

    /**
     * Gibt den Textinhalt des Dokuments zurück.
     *
     * @return Der Textinhalt
     */
    public String text() {
        return text;
    }

    /**
     * Gibt die Metadaten des Dokuments zurück.
     *
     * @return Die Metadaten
     */
    public Map<String, Object> metadata() {
        return new HashMap<>(metadata);
    }

    /**
     * Gibt einen Metadatenwert für den angegebenen Schlüssel zurück.
     *
     * @param key Der Schlüssel
     * @return Der Metadatenwert oder null, wenn der Schlüssel nicht existiert
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    @Override
    public String toString() {
        return "Document{" +
                "text='" + text + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}