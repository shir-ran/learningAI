package dev.learning.vocabulary;

import java.util.List;

public record Exercise(
        String sentence,
        List<String> options,
        String correctAnswer,
        String explanation) {
}