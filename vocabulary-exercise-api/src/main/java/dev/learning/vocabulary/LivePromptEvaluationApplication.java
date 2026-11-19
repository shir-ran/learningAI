package dev.learning.vocabulary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public final class LivePromptEvaluationApplication {
    private LivePromptEvaluationApplication() {}

    public static void main(String[] args) {
        String apiKey = System.getenv("GOOGLE_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GOOGLE_API_KEY is missing");
        }

        String model = System.getenv()
                .getOrDefault("GEMINI_MODEL", "gemini-3.1-flash-lite");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode schema = ExerciseSchemaLoader.load(objectMapper);

        LanguageModelGateway gateway =
                new GeminiRestLanguageModelGateway(apiKey, model, schema);

        List<ExerciseRequest> requests = List.of(
                new ExerciseRequest(
                        "Hebrew", "English", "A1",
                        "apple", "simple concrete noun"),
                new ExerciseRequest(
                        "Hebrew", "English", "B1",
                        "bank", "ambiguous word"),
                new ExerciseRequest(
                        "Hebrew", "English", "B2",
                        "despite", "grammar-sensitive word")
        );

        System.out.println("Model: " + model);
        System.out.println("Date: " + java.time.LocalDate.now());

        for (PromptVersion version : PromptVersion.values()) {
            VocabularyExerciseService service =
                    new VocabularyExerciseService(
                            gateway,
                            new ExercisePromptBuilder(version));

            ExerciseEvaluator evaluator = new ExerciseEvaluator(service);

            for (EvaluationResult result : evaluator.evaluate(requests)) {
                System.out.println();
                System.out.println("Prompt: " + version);
                System.out.println("Word: " + result.request().targetWord());
                System.out.println("Passed: " + result.passed());
                System.out.println("Latency: "
                        + result.latencyMillis() + " ms");

                if (result.passed()) {
                    System.out.println("Exercise: " + result.exercise());
                } else {
                    System.out.println("Failure: " + result.failure());
                }
            }
        }
    }
}
