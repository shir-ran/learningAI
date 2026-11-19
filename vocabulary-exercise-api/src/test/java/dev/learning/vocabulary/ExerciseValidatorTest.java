package dev.learning.vocabulary;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

class ExerciseValidatorTest {

    @Test
    void validExercise() {
        Exercise exercise = new Exercise(
                "This is a ________ sentence.",
                List.of("word1", "word2", "word3"),
                "word1",
                "This is an explanation."
        );
        assertTrue(ExerciseValidator.validExercise(exercise, "word1").isEmpty());
    }

    @Test
    void missingPlaceholder() {
        Exercise exercise = new Exercise(
                "This is a sentence.",
                List.of("word1", "word2", "word3"),
                "word1",
                "This is an explanation."
        );
        List<String> errors = ExerciseValidator.validExercise(exercise, "word1");
        assertTrue(errors.contains("Sentence must contain a blank represented by '________'"));
    }

    @Test
    void duplicateOptions() {
        Exercise exercise = new Exercise(
                "This is a ________ sentence.",
                List.of("word1", "word1", "word3"),
                "word1",
                "This is an explanation."
        );
        List<String> errors = ExerciseValidator.validExercise(exercise, "word1");
        assertTrue(errors.contains("Options must contain 3 distinct items"));
    }

    @Test
    void correctAnswerAbsentFromOptions() {
        Exercise exercise = new Exercise(
                "This is a ________ sentence.",
                List.of("word1", "word2", "word3"),
                "word4",
                "This is an explanation."
        );
        List<String> errors = ExerciseValidator.validExercise(exercise, "word1");
        assertTrue(errors.contains("Correct answer is not in options"));
    }

    @Test
    void blankExplanation() {
        Exercise exercise = new Exercise(
                "This is a ________ sentence.",
                List.of("word1", "word2", "word3"),
                "word1",
                ""
        );
        List<String> errors = ExerciseValidator.validExercise(exercise, "word1");
        assertTrue(errors.contains("Explanation is null or empty"));
    }

    @Test
    void multiWordOption() {
        Exercise exercise = new Exercise(
                "This is a ________ sentence.",
                List.of("word1", "multi word", "word3"),
                "word1",
                "This is an explanation."
        );
        List<String> errors = ExerciseValidator.validExercise(exercise, "word1");
        assertTrue(errors.contains("Options must not contain whitespace or be multi-word"));
    }
}