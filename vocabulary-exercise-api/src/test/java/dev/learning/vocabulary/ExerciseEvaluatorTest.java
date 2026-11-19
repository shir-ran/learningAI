package dev.learning.vocabulary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseEvaluatorTest {


    @Test
    void evalDatasetTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<ExerciseRequest> requests = objectMapper.readValue(
                new File("src/test/resources/eval-dataset.json"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ExerciseRequest.class)
        );



        int index = 0;
        Queue<String> responses = new ArrayDeque<>();
        for (ExerciseRequest request : requests) {
            String exerciseJson = generateExercise(request, index == 2); // Make the 3rd record invalid
            responses.add(exerciseJson);
            index++;
        }
        List<String> capturedPrompts = new ArrayList<>();

        LanguageModelGateway fakeModel = prompt -> {
            capturedPrompts.add(prompt);
            return responses.remove();
        };
        VocabularyExerciseService service = new VocabularyExerciseService(fakeModel, new ExercisePromptBuilder());
        ExerciseEvaluator evaluator = new ExerciseEvaluator(service);

        var results = evaluator.evaluate(requests);
        assertEquals(5, results.size());
        assertEquals(4, results.stream().filter(EvaluationResult::passed).count());
        assertEquals(1, results.stream().filter(result -> !result.passed()).count());

        assertFalse(results.get(2).passed());
        assertTrue(results.get(3).passed());

        assertEquals(requests.size(), capturedPrompts.size());

        for (int i = 0; i < requests.size(); i++) {
            assertTrue(capturedPrompts.get(i)
                    .contains(requests.get(i).targetWord()));
        }

        assertNotNull(results.get(2).failure());
        assertTrue(results.get(2).failure().contains("does not conform"));
        assertNull(results.get(3).failure());

        assertNotNull(results.get(0).exercise());
        assertNull(results.get(0).failure());

        assertNull(results.get(2).exercise());
        assertNotNull(results.get(2).failure());


    }

    private static String generateExercise(ExerciseRequest request, boolean makeInvalid) throws JsonProcessingException {
        String sentence = "This is a sentence with ________.";
        List<String> options = List.of(request.targetWord(), "wrong1", "wrong2");
        String correctAnswer = request.targetWord();
        String explanation = "This is an explanation in " + request.nativeLanguage() + ".";

        if (makeInvalid) {
            options = List.of("wrong1", "wrong2", "wrong3"); // Correct answer missing in options
        }

        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(new Exercise(sentence, options, correctAnswer, explanation));
    }


}