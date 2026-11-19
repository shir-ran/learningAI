package dev.learning.vocabulary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class GeminiRestLanguageModelGateway implements LanguageModelGateway {
    private static final int MAX_ATTEMPTS = 4;
    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final JsonNode responseSchema;

    public GeminiRestLanguageModelGateway(String apiKey, String model, JsonNode responseSchema) {
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.responseSchema = Objects.requireNonNull(responseSchema, "responseSchema must not be null");
    }

    @Override
    public String generate(String prompt) {
        try {
            Map<String, Object> responseFormat = Map.of(
                    "text", Map.of(
                            "mimeType", "APPLICATION_JSON",
                            "schema", responseSchema));

            Map<String, Object> generationConfig = Map.of(
                    "responseFormat", responseFormat);

            Map<String, Object> payload = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(Map.of("text", prompt)))),
                    "generationConfig", generationConfig);

            String requestBody = objectMapper.writeValueAsString(payload);

            URI endpoint = URI.create(BASE_URL + model + ":generateContent");
            HttpRequest request = HttpRequest.newBuilder(endpoint)
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = sendWithRetry(request);

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException(
                        "Gemini returned HTTP " + response.statusCode() + ": " + response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode text = root.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (text.isMissingNode() || text.asText().isBlank()) {
                throw new IllegalStateException("Gemini returned no text: " + response.body());
            }

            return text.asText();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Gemini request was interrupted", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Gemini request failed", exception);
        }
    }

    private HttpResponse<String> sendWithRetry(HttpRequest request)
            throws IOException, InterruptedException {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            HttpResponse<String> response;
            try {
                response = httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString());
            } catch (IOException exception) {
                if (attempt == MAX_ATTEMPTS) {
                    throw exception;
                }

                long delayMillis = retryDelayMillis(attempt);
                System.err.printf(
                        "Gemini request failed with %s (attempt %d/%d). Retrying in %d ms...%n",
                        exception.getClass().getSimpleName(),
                        attempt,
                        MAX_ATTEMPTS,
                        delayMillis);
                Thread.sleep(delayMillis);
                continue;
            }

            if (!isRetriable(response.statusCode()) || attempt == MAX_ATTEMPTS) {
                return response;
            }

            long delayMillis = retryDelayMillis(attempt);
            System.err.printf(
                    "Gemini returned HTTP %d (attempt %d/%d). Retrying in %d ms...%n",
                    response.statusCode(),
                    attempt,
                    MAX_ATTEMPTS,
                    delayMillis);
            Thread.sleep(delayMillis);
        }

        throw new IllegalStateException("Retry loop ended unexpectedly");
    }

    private long retryDelayMillis(int attempt) {
        return 1_000L << (attempt - 1);
    }

    private boolean isRetriable(int statusCode) {
        return statusCode == 429
                || statusCode == 500
                || statusCode == 502
                || statusCode == 503
                || statusCode == 504;
    }
}

