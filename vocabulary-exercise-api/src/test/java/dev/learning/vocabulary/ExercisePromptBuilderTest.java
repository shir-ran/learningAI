package dev.learning.vocabulary;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExercisePromptBuilderTest {

    @Test
    void buildsPromptWithAllRequiredValues() {
        ExercisePromptBuilder builder = new ExercisePromptBuilder();
        String prompt = builder.build("English", "Spanish", "B2", "comida");

        assertTrue(prompt.contains("The learner's native language is English."));
        assertTrue(prompt.contains("The target language is Spanish."));
        assertTrue(prompt.contains("The learner's CEFR level is B2."));
        assertTrue(prompt.contains("The target word is: comida"));
    }

    @Test
    void promptContainsRequiredJsonProperties() {
        ExercisePromptBuilder builder = new ExercisePromptBuilder();
        String prompt = builder.build("English", "Spanish", "B2", "comida");

        assertTrue(prompt.contains("\"sentence\""));
        assertTrue(prompt.contains("\"options\""));
        assertTrue(prompt.contains("\"correctAnswer\""));
        assertTrue(prompt.contains("\"explanation\""));
    }

    @Test
    void promptDoesNotAllowAdditionalProperties() {
        ExercisePromptBuilder builder = new ExercisePromptBuilder();
        String prompt = builder.build("English", "Spanish", "B2", "comida");

        assertFalse(prompt.contains("additionalProperties"));
    }

    @Test
    void handlesEmptyTargetWordGracefully() {
        ExercisePromptBuilder builder = new ExercisePromptBuilder();
        String prompt = builder.build("English", "Spanish", "B2", "");

        assertTrue(prompt.contains("The target word is: "));
        assertFalse(prompt.contains("The target word is: null"));
    }

    @Test
    void handlesNullValuesGracefully() {
        ExercisePromptBuilder builder = new ExercisePromptBuilder();
        String prompt = builder.build(null, null, null, null);

        assertTrue(prompt.contains("The learner's native language is null."));
        assertTrue(prompt.contains("The target language is null."));
        assertTrue(prompt.contains("The learner's CEFR level is null."));
        assertTrue(prompt.contains("The target word is: null"));
    }

    @Test
    void versionTwoRequestsMoreSpecificExplanation() {
        ExercisePromptBuilder builder =
                new ExercisePromptBuilder(PromptVersion.V2);

        String prompt = builder.build(
                "Hebrew", "English", "B1", "achieve");

        assertTrue(prompt.contains("match the CEFR level"));
        assertTrue(prompt.contains("translation"));
        assertTrue(prompt.contains("better match"));
    }
}