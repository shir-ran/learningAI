package dev.learning.vocabulary;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class VocabularyExerciseServiceTest {
    @Test
    void passesTheLearningContextToTheModel() {
        InputStream input = ExerciseSchemaLoader.class
                .getResourceAsStream("/sample-response.json");

        Objects.requireNonNull(input, "sample-response.json was not found");

        try (input) {
            ObjectMapper objectMapper = new ObjectMapper();
            String response =  objectMapper.readTree(input).toString();
            StringBuilder capturedPrompt = new StringBuilder();
            LanguageModelGateway fakeModel = prompt -> {
                capturedPrompt.append(prompt);
                return response;
            };
            VocabularyExerciseService service = new VocabularyExerciseService(fakeModel, new ExercisePromptBuilder());

            Exercise result = service.generateExercise("Hebrew", "English", "B1", "achieve");

            assertEquals("With enough practice, you can ________ your goal.", result.sentence());
            assertEquals("achieve", result.correctAnswer());
            assertEquals(3, result.options().size());
            assertTrue(capturedPrompt.toString().contains("Hebrew"));
            assertTrue(capturedPrompt.toString().contains("English"));
            assertTrue(capturedPrompt.toString().contains("B1"));
            assertTrue(capturedPrompt.toString().contains("achieve"));
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not read sample-response.json",
                    exception);
        }


    }

    @Test
    void rejectsSemanticallyInvalidJson() {
        LanguageModelGateway fakeModel = prompt -> """
            {
              "sentence": "There is no placeholder",
              "options": ["two words", "same", "same"],
              "correctAnswer": "missing",
              "explanation": " "
            }
            """;

        VocabularyExerciseService service =
                new VocabularyExerciseService(fakeModel);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.generateExercise(
                        "Hebrew", "English", "B1", "achieve"));

        assertTrue(exception.getMessage().contains(
                "does not conform"));
    }

    @Test
    void rejectsMalformedJson() {
        LanguageModelGateway fakeModel = prompt -> "{not valid json";

        VocabularyExerciseService service =
                new VocabularyExerciseService(fakeModel);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.generateExercise(
                        "Hebrew", "English", "B1", "achieve"));

        assertTrue(exception.getMessage().contains(
                "Failed to parse"));
    }
}

