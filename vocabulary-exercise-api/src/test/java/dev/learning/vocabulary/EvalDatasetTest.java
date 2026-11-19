package dev.learning.vocabulary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvalDatasetTest {

    @Test
    void evalTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<ExerciseRequest> dataset = objectMapper.readValue(
                Paths.get("src/test/resources/eval-dataset.json").toFile(),
                new TypeReference<List<ExerciseRequest>>() {}
        );

        assertNotNull(dataset);
        assertFalse(dataset.isEmpty());
        assertEquals(5, dataset.size());
        assertEquals("apple", dataset.get(0).targetWord());
    }
}
