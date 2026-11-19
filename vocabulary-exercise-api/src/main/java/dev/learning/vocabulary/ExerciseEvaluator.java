package dev.learning.vocabulary;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ExerciseEvaluator {
    private final VocabularyExerciseService service;

    public ExerciseEvaluator(VocabularyExerciseService service) {
        this.service = Objects.requireNonNull(service);
    }

    public List<EvaluationResult> evaluate(List<ExerciseRequest> requests) {
        List<EvaluationResult> results = new ArrayList<>();

        for (ExerciseRequest request : requests) {
            long startedAt = System.nanoTime();

            try {
                Exercise exercise = service.generateFromRequest(request);

                results.add(new EvaluationResult(
                        request,
                        true,
                        elapsedMillis(startedAt),
                        exercise,
                        null));
            } catch (RuntimeException exception) {
                results.add(new EvaluationResult(
                        request,
                        false,
                        elapsedMillis(startedAt),
                        null,
                        exception.getMessage()));
            }
        }

        return results;
    }

    private long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}