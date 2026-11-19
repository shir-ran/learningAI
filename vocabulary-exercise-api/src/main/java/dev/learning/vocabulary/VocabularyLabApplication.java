package dev.learning.vocabulary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import javax.net.ssl.HttpsURLConnection;

public final class VocabularyLabApplication {
    private VocabularyLabApplication() {}

    public static void main(String[] args) {
        System.out.println("Vocabulary Lab is starting...");

        String apiKey = System.getenv("GOOGLE_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "GOOGLE_API_KEY is missing from the IntelliJ Run Configuration");
        }
        verifyGoogleConnectivity();

        String targetWord = args.length == 0 ? "achieve" : args[0];
        String model = System.getenv().getOrDefault("GEMINI_MODEL", "gemini-3.1-flash-lite");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode exerciseSchema = ExerciseSchemaLoader.load(objectMapper);
        LanguageModelGateway gateway =
                new GeminiRestLanguageModelGateway(apiKey, model, exerciseSchema);
        System.out.println("Gemini backend = Gemini Developer API (direct REST)");
        System.out.println("Gemini model = " + model);
        VocabularyExerciseService service = new VocabularyExerciseService(gateway, new ExercisePromptBuilder());

        System.out.printf("Sending a request for the word '%s'...%n", targetWord);
        long startedAt = System.nanoTime();

        Exercise exercise;
        try {
            exercise = service.generateExercise(
                    "Hebrew",
                    "English",
                    "B1",
                    targetWord);
        } catch (RuntimeException exception) {
            long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000;
            System.err.printf(
                    "Gemini request failed after %d ms: %s: %s%n",
                    elapsedMillis,
                    exception.getClass().getSimpleName(),
                    exception.getMessage());
            throw exception;
        }

        long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000;
        System.out.printf("Gemini responded in %d ms.%n%n", elapsedMillis);
        System.out.println(exercise);
    }

    private static void verifyGoogleConnectivity() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) URI
                    .create("https://generativelanguage.googleapis.com/")
                    .toURL()
                    .openConnection();
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(15_000);
            connection.connect();
            System.out.println("Java HTTPS preflight status = " + connection.getResponseCode());
            connection.disconnect();
        } catch (Exception exception) {
            System.err.println("Java HTTPS preflight failed: "
                    + exception.getClass().getSimpleName()
                    + ": "
                    + exception.getMessage());
        }
    }
}

