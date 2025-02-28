package com.aimitjava.chain;

import com.aimitjava.document.Document;
import com.aimitjava.model.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Eine Java-Implementation der 'create_stuff_documents_chain' Funktion aus LangChain Python.
 * Diese Klasse ermöglicht das Zusammenfügen mehrerer Dokumente und deren Übergabe an ein Sprachmodell.
 */
public class StuffDocumentsChain {
    private static final Logger logger = LoggerFactory.getLogger(StuffDocumentsChain.class);

    private static final String DEFAULT_DOCUMENT_SEPARATOR = "\n\n";
    private static final String DEFAULT_DOCUMENT_VARIABLE_NAME = "context";

    private final ChatLanguageModel model;
    private final String promptTemplate;
    private final String documentSeparator;
    private final String documentVariableName;
    private final Function<Document, String> documentFormatter;

    /**
     * Erstellt eine neue StuffDocumentsChain mit Standardwerten.
     *
     * @param model Das zu verwendende Sprachmodell
     * @param promptTemplate Die Prompt-Vorlage, die eine Variable für die Dokumente enthalten muss
     */
    public StuffDocumentsChain(ChatLanguageModel model, String promptTemplate) {
        this(model, promptTemplate, DEFAULT_DOCUMENT_SEPARATOR, DEFAULT_DOCUMENT_VARIABLE_NAME,
                Document::text);
    }

    /**
     * Erstellt eine neue StuffDocumentsChain mit benutzerdefinierten Parametern.
     *
     * @param model Das zu verwendende Sprachmodell
     * @param promptTemplate Die Prompt-Vorlage, die eine Variable für die Dokumente enthalten muss
     * @param documentSeparator Der Trennstring zwischen formatierten Dokumenten
     * @param documentVariableName Der Variablenname für die formatierten Dokumente im Prompt
     * @param documentFormatter Funktion zum Formatieren eines einzelnen Dokuments
     */
    public StuffDocumentsChain(
            ChatLanguageModel model,
            String promptTemplate,
            String documentSeparator,
            String documentVariableName,
            Function<Document, String> documentFormatter) {

        // Validiere, dass der Prompt die Dokumentenvariable enthält
        if (!promptTemplate.contains("{" + documentVariableName + "}")) {
            throw new IllegalArgumentException(
                    "Prompt template must contain the document variable: {" + documentVariableName + "}"
            );
        }

        this.model = model;
        this.promptTemplate = promptTemplate;
        this.documentSeparator = documentSeparator;
        this.documentVariableName = documentVariableName;
        this.documentFormatter = documentFormatter;
    }

    /**
     * Verarbeitet die Eingabedokumente und ruft das Sprachmodell mit dem formatierten Prompt auf.
     *
     * @param documents Liste der zu verarbeitenden Dokumente
     * @param additionalVariables Zusätzliche Variablen für den Prompt (optional)
     * @return Die Antwort des Sprachmodells
     */
    public String invoke(List<Document> documents, Map<String, Object> additionalVariables) {
        // Debug-Logging der Eingabedokumente
        logger.debug("Verarbeitung von {} Dokumenten", documents.size());
        for (int i = 0; i < documents.size(); i++) {
            logger.debug("Dokument {}: {}", i, documents.get(i).text());
        }

        // Formatiere alle Dokumente und verbinde sie mit dem Separator
        String formattedDocs = documents.stream()
                .map(documentFormatter)
                .reduce((a, b) -> a + documentSeparator + b)
                .orElse("");

        logger.debug("Formatierte Dokumente: {}", formattedDocs);
        logger.debug("Verwende Dokumentenseparator: '{}'", documentSeparator);

        // Erstelle die vollständige Map mit allen Variablen
        Map<String, Object> allVariables = new HashMap<>(additionalVariables);
        allVariables.put(documentVariableName, formattedDocs);
        logger.debug("Variablen für den Prompt: {}", allVariables);

        // Fülle den Prompt mit den Variablen
        String filledPrompt = promptTemplate;
        for (Map.Entry<String, Object> entry : allVariables.entrySet()) {
            filledPrompt = filledPrompt.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }

        logger.debug("Vollständiger Prompt: {}", filledPrompt);

        // Rufe das Modell auf
        ChatLanguageModel.Response<String> response = model.generate(filledPrompt);
        logger.debug("Erhaltene Antwort: {}", response.content());

        return response.content();
    }

    /**
     * Überladene Methode, die nur Dokumente ohne zusätzliche Variablen akzeptiert.
     *
     * @param documents Liste der zu verarbeitenden Dokumente
     * @return Die Antwort des Sprachmodells
     */
    public String invoke(List<Document> documents) {
        return invoke(documents, new HashMap<>());
    }

    /**
     * Überladene Methode, die eine Map mit einem Dokumenten-Schlüssel akzeptiert.
     *
     * @param inputs Map, die den Dokumenten-Schlüssel und andere Variablen enthält
     * @return Die Antwort des Sprachmodells
     */
    public String invoke(Map<String, Object> inputs) {
        logger.debug("Invoking mit Map-Input. Schlüssel: {}", inputs.keySet());

        @SuppressWarnings("unchecked")
        List<Document> documents = (List<Document>) inputs.get(documentVariableName);
        if (documents == null) {
            throw new IllegalArgumentException("Input map must contain the key: " + documentVariableName);
        }

        // Entferne das Dokument aus den Eingaben für die zusätzlichen Variablen
        Map<String, Object> additionalVariables = new HashMap<>(inputs);
        additionalVariables.remove(documentVariableName);

        return invoke(documents, additionalVariables);
    }

    /**
     * Builder-Klasse für eine einfachere Konfiguration der StuffDocumentsChain.
     */
    public static class Builder {
        private final ChatLanguageModel model;
        private final String promptTemplate;
        private String documentSeparator = DEFAULT_DOCUMENT_SEPARATOR;
        private String documentVariableName = DEFAULT_DOCUMENT_VARIABLE_NAME;
        private Function<Document, String> documentFormatter = Document::text;

        public Builder(ChatLanguageModel model, String promptTemplate) {
            this.model = model;
            this.promptTemplate = promptTemplate;
        }

        public Builder documentSeparator(String documentSeparator) {
            this.documentSeparator = documentSeparator;
            return this;
        }

        public Builder documentVariableName(String documentVariableName) {
            this.documentVariableName = documentVariableName;
            return this;
        }

        public Builder documentFormatter(Function<Document, String> documentFormatter) {
            this.documentFormatter = documentFormatter;
            return this;
        }

        public StuffDocumentsChain build() {
            return new StuffDocumentsChain(
                    model,
                    promptTemplate,
                    documentSeparator,
                    documentVariableName,
                    documentFormatter
            );
        }
    }
}