package dev.learning.vocabulary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseSchemaLoaderTest {
    @Test
    void loadsTheExerciseSchema() {
        JsonNode schema = ExerciseSchemaLoader.load(new ObjectMapper());

        assertEquals("object", schema.path("type").asText());
        assertEquals(3,
                schema.path("properties")
                        .path("options")
                        .path("minItems")
                        .asInt());
        assertTrue(schema.path("required").isArray());
    }
}