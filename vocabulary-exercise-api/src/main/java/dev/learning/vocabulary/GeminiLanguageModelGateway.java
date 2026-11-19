package dev.learning.vocabulary;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public final class GeminiLanguageModelGateway implements LanguageModelGateway {
    private final Client client;
    private final String model;

    public GeminiLanguageModelGateway(Client client, String model) {
        this.client = client;
        this.model = model;
    }

    @Override
    public String generate(String prompt) {
        GenerateContentResponse response = client.models.generateContent(model, prompt, null);
        return response.text();
    }
}

