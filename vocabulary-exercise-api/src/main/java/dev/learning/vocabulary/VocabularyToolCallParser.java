package dev.learning.vocabulary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class VocabularyToolCallParser {
    public static Optional<VocabularyLookupArguments> parse(String json) {
        var objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        try {
            var parts = objectMapper.readTree(json)
                    .get("candidates").get(0)
                    .get("content").get("parts");

            for (JsonNode part : parts) {
                if (part.has("functionCall")) {
                    var functionCall = part.get("functionCall");
                    if (functionCall == null
                            || !functionCall.has("name")
                            || !functionCall.get("name").isTextual()
                            || !functionCall.get("name").asText().equals("lookup_vocabulary_entry")) {
                        throw new IllegalArgumentException("Invalid function call in JSON");
                    }

                    var argsNode = functionCall.get("args");
                    if (argsNode == null) {
                        throw new IllegalArgumentException("Missing 'args' in function call");
                    }
                    if (!argsNode.has("term") || !argsNode.has("language")) {
                        throw new IllegalArgumentException("Missing 'term' or 'language' in function call arguments");
                    }
                    if (!argsNode.get("term").isTextual() || !argsNode.get("language").isTextual()) {
                        throw new IllegalArgumentException("'term' and 'language' must be strings");
                    }
                    if (argsNode.fieldNames().hasNext()) {
                        for (Iterator<String> it = argsNode.fieldNames(); it.hasNext(); ) {
                            var fieldName = it.next();
                            if (!fieldName.equals("term") && !fieldName.equals("language")) {
                                throw new IllegalArgumentException("Unexpected field in function call arguments: " + fieldName);
                            }
                        }
                    }

                    String term = argsNode.get("term").asText();
                    String language = argsNode.get("language").asText();
                    var args = new VocabularyLookupArguments(term, language);
                    List<String> validationErrors = VocabularyLookupArgumentsValidator.validate(args);
                    if (!validationErrors.isEmpty()) {
                        throw new IllegalArgumentException("Invalid arguments: " + validationErrors);
                    }

                    return Optional.of(args);
                }
            }
            return Optional.empty();


        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse JSON: " + e.getMessage(), e);
        }
    }
}
