package com.aimitjava.model;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.response.ChatResponse;

/**
 * Interface für Chat-basierte Sprachmodelle.
 */
public interface ChatLanguageModel {

    /**
     * Generiert eine Antwort basierend auf dem übergebenen Prompt-Text.
     *
     * @param prompt Der Eingabetext für das Sprachmodell
     * @return Die generierte Antwort
     */
    Response<String> generate(String prompt);

    /**
     * Adapter-Klasse zur Verwendung von LangChain4j OpenAI-Modellen.
     */
    class Adapter implements ChatLanguageModel {
        private final dev.langchain4j.model.chat.ChatLanguageModel delegate;

        public Adapter(dev.langchain4j.model.chat.ChatLanguageModel delegate) {
            this.delegate = delegate;
        }

        @Override
        public Response<String> generate(String prompt) {
            ChatResponse response =
                    delegate.chat(new AiMessage(prompt));
            return new Response<>(
                    response.aiMessage().text(),
                    response.tokenUsage()
            );
        }
    }

    /**
     * Response-Klasse für Chat-Modell-Antworten.
     */
    record Response<T>(T content, dev.langchain4j.model.output.TokenUsage tokenUsage) {}
}