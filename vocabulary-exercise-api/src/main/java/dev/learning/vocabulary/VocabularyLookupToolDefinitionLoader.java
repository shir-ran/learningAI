package dev.learning.vocabulary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class VocabularyLookupToolDefinitionLoader {
    private VocabularyLookupToolDefinitionLoader() {
    }

    public static JsonNode load(ObjectMapper objectMapper) {
        InputStream input = VocabularyLookupToolDefinitionLoader.class
                .getResourceAsStream("/vocabulary-lookup-tool.json");

        Objects.requireNonNull(input, "vocabulary-lookup-tool.json was not found");

        try (input) {
            return objectMapper.readTree(input);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not read vocabulary-lookup-tool.json",
                    exception);
        }
    }
}