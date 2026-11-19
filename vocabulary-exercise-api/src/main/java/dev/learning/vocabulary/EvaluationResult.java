package dev.learning.vocabulary;

public record EvaluationResult(
        ExerciseRequest request,
       boolean passed,
       long latencyMillis,
       Exercise exercise,
       String failure) {

}
