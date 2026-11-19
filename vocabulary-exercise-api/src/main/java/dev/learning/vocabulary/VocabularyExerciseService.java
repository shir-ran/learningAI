package dev.learning.vocabulary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

import static dev.learning.vocabulary.ExerciseValidator.validExercise;

public final class VocabularyExerciseService {
    private final LanguageModelGateway languageModel;
    private final ObjectMapper objectMapper;
    private final ExercisePromptBuilder promptBuilder;

    public VocabularyExerciseService(LanguageModelGateway languageModel, ExercisePromptBuilder promptBuilder) {
        this.languageModel = Objects.requireNonNull(languageModel);
        this.objectMapper = new ObjectMapper();
        this.promptBuilder = Objects.requireNonNull(promptBuilder);
    }

    public VocabularyExerciseService(LanguageModelGateway languageModel) {
        this.languageModel = Objects.requireNonNull(languageModel);
        this.objectMapper = new ObjectMapper();
        this.promptBuilder = Objects.requireNonNull(new ExercisePromptBuilder());
    }

    public Exercise generateFromRequest(ExerciseRequest request) {
        return generateExercise(
                request.nativeLanguage(),
                request.targetLanguage(),
                request.cefrLevel(),
                request.targetWord());
    }

    public Exercise generateExercise(
            String nativeLanguage,
            String targetLanguage,
            String cefrLevel,
            String targetWord) {

        String jsonResponse = languageModel.generate(promptBuilder.build(nativeLanguage, targetLanguage, cefrLevel, targetWord));
        try {
            Exercise exercise =  objectMapper.readValue(jsonResponse, Exercise.class);
            List<String> validationErrors = validExercise(exercise, targetWord);
            if (validationErrors.isEmpty()) {
                return exercise;
            } else {
                throw new IllegalStateException(
                        "The language model response does not conform to the expected Exercise schema: " + validationErrors);
            }
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Failed to parse the language model response into an Exercise object",
                    exception);
        }
    }
}

