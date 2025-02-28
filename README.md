# LangChain4j Documents Chain

Eine Java-Implementierung für Dokumentenverarbeitung und Zusammenfassung mit LangChain4j und OpenAI.

## Überblick

Dieses Projekt demonstriert, wie man die LangChain4j-Bibliothek verwendet, um eine "StuffDocumentsChain" zu erstellen - ein Muster, das mehrere Dokumente in einen einzigen Kontext kombiniert und an ein Sprachmodell zur Verarbeitung übergibt. Die Implementierung zeigt:

- Dokumentenverwaltung und Metadaten-Handling
- Integration großer Sprachmodelle (LLM) mit OpenAI
- Konfigurierbare Dokumentenketten mit anpassbarer Formatierung
- Saubere Abstraktion über der LangChain4j-Bibliothek

## Funktionen

- **StuffDocumentsChain**: Kombiniert mehrere Dokumente in einen einzigen Kontext für die LLM-Verarbeitung
- **Anpassbare Dokumentenformatierung**: Steuerung der Formatierung und Trennung von Dokumenten
- **Konfigurationsmanagement**: Umgebungsbasierte Konfiguration für API-Schlüssel und Modelleinstellungen
- **Flexible Eingabebehandlung**: Dokumente direkt oder über map-basierte Eingabe verarbeiten
- **Umfassendes Logging**: Detaillierte Protokollierung für Debugging und Transparenz

## Voraussetzungen

- Java 17 oder höher
- Gradle 8.x oder höher
- OpenAI API-Schlüssel (für den Produktiveinsatz)

## Erste Schritte

### Einrichtung

1. Repository klonen:
   ```bash
   git clone https://github.com/yourusername/langchain4j-documents-chain.git
   cd langchain4j-documents-chain
   ```

2. OpenAI API-Schlüssel setzen:
   ```bash
   export OPENAI_API_KEY=dein-api-schlüssel
   ```

### Bauen

Projekt mit Gradle bauen:
```bash
./gradlew build
```

### Beispiel ausführen

Das Projekt enthält eine Beispielanwendung, die die StuffDocumentsChain demonstriert:
```bash
./gradlew runStuffDocumentsChainExample
```

Dieses Beispiel:
1. Erstellt Beispieldokumente
2. Initialisiert eine StuffDocumentsChain
3. Verarbeitet die Dokumente in verschiedenen Konfigurationen
4. Gibt die Ergebnisse aus

## Verwendung

### Grundlegende Verwendung

```java
// Sprachmodell erstellen
OpenAiChatModel openAiModel = OpenAiChatModel.builder()
        .apiKey(apiKey)
        .modelName("gpt-3.5-turbo")
        .temperature(0.0)
        .build();
ChatLanguageModel model = new ChatLanguageModel.Adapter(openAiModel);

// Dokumentenkette erstellen
String promptTemplate = "Analysiere die folgenden Dokumente:\n\n{context}";
StuffDocumentsChain chain = new StuffDocumentsChain.Builder(model, promptTemplate)
        .build();

// Dokumente verarbeiten
List<Document> documents = Arrays.asList(
        new Document("Inhalt von Dokument 1"),
        new Document("Inhalt von Dokument 2")
);
String result = chain.invoke(documents);
```

### Anpassung

Die Kette kann auf verschiedene Weise angepasst werden:

```java
StuffDocumentsChain chain = new StuffDocumentsChain.Builder(model, promptTemplate)
        .documentSeparator(" | ")  // Benutzerdefinierter Separator zwischen Dokumenten
        .documentVariableName("docs")  // Benutzerdefinierter Variablenname im Prompt-Template
        .documentFormatter(doc -> "Titel: " + doc.getMetadata("title") + "\n" + doc.text())  // Benutzerdefinierte Formatierung
        .build();
```

## Projektstruktur

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── aimitjava/
│   │           ├── chain/
│   │           │   └── StuffDocumentsChain.java  // Hauptimplementierung der Kette
│   │           ├── document/
│   │           │   └── Document.java             // Dokumentrepräsentation
│   │           ├── example/
│   │           │   └── StuffDocumentsChainExample.java  // Beispielanwendung
│   │           └── model/
│   │               └── ChatLanguageModel.java    // Modellabstraktion
│   └── resources/
│       ├── application.properties               // Konfigurationseigenschaften
│       └── logback.xml                          // Logging-Konfiguration
└── test/
    └── java/
        └── com/
            └── aimitjava/
                └── chain/
                    └── StuffDocumentsChainTest.java  // Unit-Tests
```

## Konfiguration

Die Anwendung kann über Umgebungsvariablen oder eine Properties-Datei konfiguriert werden:

```properties
# OpenAI-Konfiguration
openai.api.key=$OPENAI_API_KEY
openai.model.name=gpt-3.5-turbo
openai.temperature=0.0
```

## Abhängigkeiten

- [LangChain4j](https://github.com/langchain4j/langchain4j) - Kern-LangChain-Funktionalität
- [LangChain4j OpenAI](https://github.com/langchain4j/langchain4j) - OpenAI-Integration
- [SLF4J](https://www.slf4j.org/) / [Logback](https://logback.qos.ch/) - Logging-Framework
- [JUnit](https://junit.org/junit5/) / [Mockito](https://site.mockito.org/) - Test-Frameworks

## GitHub Packages Integration

Dieses Projekt verwendet die [LangChain4j Configuration](https://github.com/RainerBlessing/langchain4j-configuration) Bibliothek aus GitHub Packages. Um mit dieser Abhängigkeit zu bauen:

```bash
./gradlew build -Pgpr.user=BENUTZERNAME -Pgpr.key=TOKEN
```

Oder setzen Sie Umgebungsvariablen:
```bash
export GITHUB_USERNAME=BENUTZERNAME
export GITHUB_TOKEN=TOKEN
./gradlew build
```

## Lizenz

[MIT-Lizenz](LICENSE)

## Mitwirkung

Beiträge sind willkommen! Bitte reiche einen Pull Request ein.