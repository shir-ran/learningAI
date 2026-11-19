package dev.learning.vocabulary;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VocabularyLookupToolDefinitionLoaderTest {

    /**
     * 1. the tool name is `lookup_vocabulary_entry`;
     * 2. the parameters type is `object`;
     * 3. `word` and `language` are declared as strings;
     * 4. both names appear in `required`;
     * 5. the description explains that the data is trusted.
     */

    @Test
    void testLoad() {
        var objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        var jsonNode = VocabularyLookupToolDefinitionLoader.load(objectMapper);

        assertEquals("lookup_vocabulary_entry", jsonNode.get("name").asText());
        assertEquals("object", jsonNode.get("parameters").get("type").asText());

        var properties = jsonNode.get("parameters").get("properties");
        assertEquals("string", properties.get("word").get("type").asText());
        assertEquals("string", properties.get("language").get("type").asText());

        Set<String> required = new HashSet<>();
        jsonNode.get("parameters").path("required")
                .forEach(node -> required.add(node.asText()));

        assertEquals(Set.of("word", "language"), required);

        var description = jsonNode.get("description").asText();
        assertTrue(description.contains("trusted"));
    }

}