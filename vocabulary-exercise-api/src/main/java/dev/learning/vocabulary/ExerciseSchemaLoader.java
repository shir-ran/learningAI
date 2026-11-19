package dev.learning.vocabulary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class ExerciseSchemaLoader {
    private ExerciseSchemaLoader() {
    }

    public static JsonNode load(ObjectMapper objectMapper) {
        InputStream input = ExerciseSchemaLoader.class
                .getResourceAsStream("/exercise-schema.json");

        Objects.requireNonNull(input, "exercise-schema.json was not found");

        try (input) {
            return objectMapper.readTree(input);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not read exercise-schema.json",
                    exception);
        }
    }
}