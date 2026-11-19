package dev.learning.vocabulary;

import java.util.ArrayList;
import java.util.List;

public class ExerciseValidator {
    public static List<String> validExercise(Exercise exercise, String targetWord) {
        List<String> validationErrors = new ArrayList<>();
        if (exercise.sentence() == null || exercise.sentence().isBlank()) {
            validationErrors.add("Sentence is null or empty");
        }
        if (exercise.options() == null || exercise.options().isEmpty()) {
            validationErrors.add("Options are null or empty");
        }
        if (exercise.correctAnswer() == null || exercise.correctAnswer().isBlank()) {
            validationErrors.add("Correct answer is null or empty");
        }
        if (exercise.correctAnswer() != null && !exercise.correctAnswer().equalsIgnoreCase(targetWord)) {
            validationErrors.add("Correct answer does not match the target word");
        }
        if (exercise.explanation() == null || exercise.explanation().isBlank()) {
            validationErrors.add("Explanation is null or empty");
        }
        if (exercise.options() != null && !exercise.options().contains(exercise.correctAnswer())) {
            validationErrors.add("Correct answer is not in options");
        }
        if (exercise.options() != null && exercise.options().size() != 3) {
            validationErrors.add("Options must contain exactly 3 items");
        }
        if (exercise.sentence() != null && !exercise.sentence().contains("________")) {
            validationErrors.add("Sentence must contain a blank represented by '________'");
        }
        if (exercise.options() != null && exercise.options().stream().distinct().count() != 3) {
            validationErrors.add("Options must contain 3 distinct items");
        }
        if (exercise.sentence() != null && exercise.correctAnswer() != null && exercise.sentence().contains(exercise.correctAnswer())) {
            validationErrors.add("Sentence must not contain the correct answer");
        }
        if (exercise.options() != null) {
            for (String option : exercise.options()) {
                if (option == null || option.isBlank()) {
                    validationErrors.add("Options must not be null or blank");
                } else if (option.chars().anyMatch(Character::isWhitespace)) {
                    validationErrors.add("Options must not contain whitespace or be multi-word");
                }
            }
        }
        return validationErrors;
    }
}
