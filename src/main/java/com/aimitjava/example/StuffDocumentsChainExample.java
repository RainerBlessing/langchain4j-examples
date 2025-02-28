package com.aimitjava.example;

import com.aimitjava.config.LangchainConfiguration;

import com.aimitjava.chain.StuffDocumentsChain;
import com.aimitjava.document.Document;
import com.aimitjava.model.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Beispielklasse für die Verwendung der StuffDocumentsChain.
 */
public class StuffDocumentsChainExample {

    private static final Logger logger = LoggerFactory.getLogger(StuffDocumentsChainExample.class);
    private static final LangchainConfiguration configuration = new LangchainConfiguration();

    public static void main(String[] args) {

        // Prüfen, ob ein API-Schlüssel konfiguriert ist
        String apiKey = configuration.getOpenAiApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("OPENAI_API_KEY nicht gesetzt. Verwende einen Beispiel-Response.");
            runWithMock();
        } else {
            logger.info("OPENAI_API_KEY gefunden. Verwende OpenAI API.");
            runWithOpenAI(apiKey);
        }
    }

    /**
     * Führt das Beispiel mit einem echten OpenAI-API-Schlüssel aus.
     */
    private static void runWithOpenAI(String apiKey) {
        logger.info("Führe Beispiel mit OpenAI-API aus...");

        String llmModel = configuration.getOpenAiModelName();
        double temperature = configuration.getOpenAiTemperature();

        if (logger.isDebugEnabled()) {
            logger.debug("LLM Name: {}", llmModel);
            logger.debug("LLM Temperatur: {}", temperature);
        }

        OpenAiChatModel openAiModel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(llmModel)
                .temperature(temperature)
                .build();

        // Adaptiere das LangChain4j-Modell zu unserem Interface
        ChatLanguageModel model = new ChatLanguageModel.Adapter(openAiModel);

        runExample(model);
    }

    /**
     * Führt das Beispiel mit einem simulierten Modell aus.
     */
    private static void runWithMock() {
        logger.info("Führe Beispiel mit simuliertem Modell aus...");

        // Ein einfaches Mock-Modell, das immer die gleiche Antwort gibt, aber den Prompt loggt
        ChatLanguageModel mockModel = prompt -> {
            logger.debug("Mock-Modell erhält Prompt: {}", prompt);

            String response = """
        Basierend auf den bereitgestellten Informationen:
        - Jesse mag die Farbe Rot, aber nicht Gelb.
        - Jamal mag die Farbe Grün, aber Orange gefällt ihm noch besser.
        
        Also ist Jesses Lieblingsfarbe Rot und Jamals Lieblingsfarbe Orange.
        """;

            return new ChatLanguageModel.Response<>(
                    response,
                    new dev.langchain4j.model.output.TokenUsage(15, 42, 57)
            );
        };

        runExample(mockModel);
    }

    /**
     * Führt das eigentliche Beispiel mit dem gegebenen Modell aus.
     */
    private static void runExample(ChatLanguageModel model) {
        // Prompt-Template mit der "context"-Variable
        String promptTemplate = "What is everyone's favorite color:\n\n{context}";
        logger.info("Verwende Prompt-Template: {}", promptTemplate);

        // StuffDocumentsChain erstellen
        StuffDocumentsChain chain = new StuffDocumentsChain.Builder(model, promptTemplate)
                .build();

        // Dokumente erstellen
        List<Document> documents = Arrays.asList(
                new Document("Jesse loves red but not yellow"),
                new Document("Jamal loves green but not as much as he loves orange")
        );

        logger.info("Erstelle {} Dokumente für alle Beispiele:", documents.size());
        for (int i = 0; i < documents.size(); i++) {
            logger.info("Dokument {}: {}", i+1, documents.get(i).text());
        }


        List<Document> documents1 = copyDocuments(documents);
        executeExample("\n=== Beispiel 1: Direkte Übergabe der Dokumente ===", "Beispiel 1 verwendet {} Dokumente", documents1, chain.invoke(documents1));


        List<Document> documents2 = copyDocuments(documents);
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("context", documents2);
        executeExample("\n=== Beispiel 2: Übergabe als Map mit dem 'context'-Schlüssel ===", "Beispiel 2 verwendet {} Dokumente via Map", documents2, chain.invoke(inputs));


        List<Document> documents3 = copyDocuments(documents);
        StuffDocumentsChain customChain = new StuffDocumentsChain.Builder(model, promptTemplate)
                .documentSeparator(" | ") // Ändere den Separator zu einem Pipe-Symbol mit Leerzeichen
                .build();

        executeExample("\n=== Beispiel 3: Anpassung des Dokument-Separators ===", "Beispiel 3 verwendet {} Dokumente mit angepasstem Separator", documents3, customChain.invoke(documents3));
    }

    private static void executeExample(String s, String s1, List<Document> documents1, String chain) {
        logger.info(s);
        logger.debug(s1, documents1.size());
        logger.info("Antwort: {}", chain);
    }

    /**
     * Erstellt eine tiefe Kopie der Dokumente, um sicherzustellen, dass jedes Beispiel
     * mit identischen Daten arbeitet.
     */
    private static List<Document> copyDocuments(List<Document> original) {
        return original.stream()
                .map(doc -> new Document(doc.text(), new HashMap<>(doc.metadata())))
                .toList();
    }
}