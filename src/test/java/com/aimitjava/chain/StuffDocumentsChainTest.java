package com.aimitjava.chain;

import com.aimitjava.document.Document;
import com.aimitjava.model.ChatLanguageModel;
import dev.langchain4j.model.output.TokenUsage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class StuffDocumentsChainTest {

    @Test
    void testInvokeWithDocumentList() {
        // Vorbereiten
        ChatLanguageModel mockModel = Mockito.mock(ChatLanguageModel.class);
        when(mockModel.generate(anyString())).thenReturn(
                new ChatLanguageModel.Response<>("Testantwort", new TokenUsage(1, 1, 2))
        );

        String promptTemplate = "Test prompt: {context}";
        StuffDocumentsChain chain = new StuffDocumentsChain(mockModel, promptTemplate);

        List<Document> docs = Arrays.asList(
                new Document("Dokument 1"),
                new Document("Dokument 2")
        );

        // Ausführen
        String result = chain.invoke(docs);

        // Überprüfen
        assertEquals("Testantwort", result);

        // Überprüfen, dass das Modell mit dem richtigen formatierten Prompt aufgerufen wurde
        Mockito.verify(mockModel).generate("Test prompt: Dokument 1\n\nDokument 2");
    }

    @Test
    void testInvokeWithInputMap() {
        // Vorbereiten
        ChatLanguageModel mockModel = Mockito.mock(ChatLanguageModel.class);
        when(mockModel.generate(anyString())).thenReturn(
                new ChatLanguageModel.Response<>("Testantwort", new TokenUsage(1, 1, 2))
        );

        String promptTemplate = "Test prompt: {context}";
        StuffDocumentsChain chain = new StuffDocumentsChain(mockModel, promptTemplate);

        List<Document> docs = Arrays.asList(
                new Document("Dokument 1"),
                new Document("Dokument 2")
        );

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("context", docs);

        // Ausführen
        String result = chain.invoke(inputs);

        // Überprüfen
        assertEquals("Testantwort", result);

        // Überprüfen, dass das Modell mit dem richtigen formatierten Prompt aufgerufen wurde
        Mockito.verify(mockModel).generate("Test prompt: Dokument 1\n\nDokument 2");
    }

    @Test
    void testCustomDocumentFormatter() {
        // Vorbereiten
        ChatLanguageModel mockModel = Mockito.mock(ChatLanguageModel.class);
        when(mockModel.generate(anyString())).thenReturn(
                new ChatLanguageModel.Response<>("Testantwort", new TokenUsage(1, 1, 2))
        );

        String promptTemplate = "Test prompt: {context}";
        StuffDocumentsChain chain = new StuffDocumentsChain.Builder(mockModel, promptTemplate)
                .documentFormatter(doc -> "FORMATTED: " + doc.text())
                .build();

        List<Document> docs = Arrays.asList(
                new Document("Dokument 1"),
                new Document("Dokument 2")
        );

        // Ausführen
        String result = chain.invoke(docs);

        // Überprüfen
        assertEquals("Testantwort", result);

        // Überprüfen, dass das Modell mit dem richtigen formatierten Prompt aufgerufen wurde
        Mockito.verify(mockModel).generate("Test prompt: FORMATTED: Dokument 1\n\nFORMATTED: Dokument 2");
    }

    @Test
    void testCustomDocumentSeparator() {
        // Vorbereiten
        ChatLanguageModel mockModel = Mockito.mock(ChatLanguageModel.class);
        when(mockModel.generate(anyString())).thenReturn(
                new ChatLanguageModel.Response<>("Testantwort", new TokenUsage(1, 1, 2))
        );

        String promptTemplate = "Test prompt: {context}";
        StuffDocumentsChain chain = new StuffDocumentsChain.Builder(mockModel, promptTemplate)
                .documentSeparator(" | ")
                .build();

        List<Document> docs = Arrays.asList(
                new Document("Dokument 1"),
                new Document("Dokument 2")
        );

        // Ausführen
        String result = chain.invoke(docs);

        // Überprüfen
        assertEquals("Testantwort", result);

        // Überprüfen, dass das Modell mit dem richtigen formatierten Prompt aufgerufen wurde
        Mockito.verify(mockModel).generate("Test prompt: Dokument 1 | Dokument 2");
    }

    @Test
    void testInvalidPromptTemplate() {
        // Vorbereiten
        ChatLanguageModel mockModel = Mockito.mock(ChatLanguageModel.class);
        String promptTemplate = "Dieser Prompt hat keine Variable";

        // Ausführen und Überprüfen
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new StuffDocumentsChain(mockModel, promptTemplate));

        // Überprüfen der Fehlermeldung
        assertTrue(exception.getMessage().contains("must contain the document variable"));
    }

    @Test
    void testCustomVariableName() {
        // Vorbereiten
        ChatLanguageModel mockModel = Mockito.mock(ChatLanguageModel.class);
        when(mockModel.generate(anyString())).thenReturn(
                new ChatLanguageModel.Response<>("Testantwort", new TokenUsage(1, 1, 2))
        );

        String promptTemplate = "Test prompt: {documents}";
        StuffDocumentsChain chain = new StuffDocumentsChain.Builder(mockModel, promptTemplate)
                .documentVariableName("documents")
                .build();

        List<Document> docs = Arrays.asList(
                new Document("Dokument 1"),
                new Document("Dokument 2")
        );

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("documents", docs);

        // Ausführen
        String result = chain.invoke(inputs);

        // Überprüfen
        assertEquals("Testantwort", result);

        // Überprüfen, dass das Modell mit dem richtigen formatierten Prompt aufgerufen wurde
        Mockito.verify(mockModel).generate("Test prompt: Dokument 1\n\nDokument 2");
    }
}