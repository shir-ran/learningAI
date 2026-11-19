package dev.learning.vocabulary;

public record ExerciseRequest(
        String nativeLanguage,
        String targetLanguage,
        String cefrLevel,
        String targetWord,
        String why) {
}