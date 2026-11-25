package dev.learning.vocabulary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class VocabularyToolCallParserTest {

    private final static String fixture = """
            {
              "candidates": [{
                "content": {
                  "parts": [{
                    "functionCall": {
                      "name": "lookup_vocabulary_entry",
                      "args": {
                        "term": "afraid of",
                        "language": "English"
                      }
                    }
                  }]
                }
              }]
            }""";

    private final static String freeTextFixture = """
            {
              "candidates": [{
                "content": {
                  "parts": [{
                    "text": "I'll look up that term."
                    },
                    {
                    "functionCall": {
                      "name": "lookup_vocabulary_entry",
                      "args": {
                        "term": "afraid of",
                        "language": "English"
                      }
                    }
                  }]
                }
              }]
            }""";

    private final static String failingFixture = """
            {
              "candidates": [{
                "content": {
                  "parts": [{
                    "text": "I'll look up that term."
                  }]
                }
              }]
            }""";
    private final static String bankFixture = """
            {
              "candidates": [{
                "content": {
                  "role": "model",
                  "parts": [{
                    "thoughtSignature": "dGVzdA==",
                    "functionCall": {
                      "name": "lookup_vocabulary_entry",
                      "id": "bank-call-1",
                      "args": {
                        "term": "bank",
                        "language": "English"
                      }
                    }
                  }]
                }
              }]
            }""";

    private final static String frenchBankFixture = """
            {
              "candidates": [{
                "content": {
                  "parts": [{
                    "functionCall": {
                      "name": "lookup_vocabulary_entry",
                      "args": {
                        "term": "bank",
                        "language": "French"
                      }
                    }
                  }]
                }
              }]
            }""";

    @Test
    void testParse() {
        var objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        try {
            var functionCall = objectMapper.readTree(fixture)
                    .get("candidates").get(0)
                    .get("content").get("parts").get(0)
                    .get("functionCall");

            assertEquals("lookup_vocabulary_entry", functionCall.get("name").asText());

            var args = new VocabularyLookupArguments(functionCall.get("args").get("term").asText(), functionCall.get("args").get("language").asText());
            assertEquals(new VocabularyLookupArguments("afraid of", "English"), args);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    //Next, add a test with {"text": "I'll look up that term."} as the first item in parts, followed by the original functionCall. Your current .get(0) will pick the text part, so the new test should fail.
    @Test
    void failOnFreeTextPart(){
        var args = VocabularyToolCallParser.parse(failingFixture);
        assertEquals(Optional.empty(), args);


    }
    @Test
    void testParserWithFixtures() {
        var args = VocabularyToolCallParser.parse(fixture);
        assertEquals(Optional.of(new VocabularyLookupArguments("afraid of", "English")), args);

        var argsFromFailingFixture = VocabularyToolCallParser.parse(freeTextFixture);
        assertEquals(Optional.of(new VocabularyLookupArguments("afraid of", "English")), argsFromFailingFixture);
    }

    @Test
    void rejectUnknownToolName() {
        var invalidToolNameFixture = """
                {
                  "candidates": [{
                    "content": {
                      "parts": [{
                        "functionCall": {
                          "name": "unknown_tool",
                          "args": {
                            "term": "afraid of",
                            "language": "English"
                          }
                        }
                      }]
                    }
                  }]
                }""";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> VocabularyToolCallParser.parse(invalidToolNameFixture));

        String expectedMessage = "Invalid function call in JSON";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void rejectAnExtraDebugField(){
        var invalidArgsFixture = """
                {
                  "candidates": [{
                    "content": {
                      "parts": [{
                        "functionCall": {
                          "name": "lookup_vocabulary_entry",
                          "args": {
                            "term": "afraid of",
                            "language": "English",
                            "debug": true
                          }
                        }
                      }]
                    }
                  }]
                }""";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> VocabularyToolCallParser.parse(invalidArgsFixture));

        String expectedMessage = "Unexpected field in function call arguments: debug";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void rejectNonStringArgument() {
        var invalidArgsFixture = """
                {
                  "candidates": [{
                    "content": {
                      "parts": [{
                        "functionCall": {
                          "name": "lookup_vocabulary_entry",
                          "args": {
                            "term": 123,
                            "language": "English"
                          }
                        }
                      }]
                    }
                  }]
                }""";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> VocabularyToolCallParser.parse(invalidArgsFixture));

        String expectedMessage = "'term' and 'language' must be strings";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void rejectUnsupportedLanguage() {
        var invalidArgsFixture = """
                {
                  "candidates": [{
                    "content": {
                      "parts": [{
                        "functionCall": {
                          "name": "lookup_vocabulary_entry",
                          "args": {
                            "term": "apple",
                            "language": "German"
                          }
                        }
                      }]
                    }
                  }]
                }""";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> VocabularyToolCallParser.parse(invalidArgsFixture));

        String expectedMessage = "Invalid arguments: [The 'language' argument must be one of: English, Spanish, French]";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testParserWithLookUpTool() {
        InMemoryVocabularyLookupTool tool = new InMemoryVocabularyLookupTool();
        tool.addEntry(new VocabularyLookupArguments("afraid of", "English"), new VocabularyEntry("afraid of", "English", "phrase", "Feeling fear or anxiety about something."));

        var args = VocabularyToolCallParser.parse(fixture);
        assertTrue(args.isPresent());
        var entry = tool.lookup(args.get());
        assertEquals("afraid of", entry.term());
        assertEquals("English", entry.language());
    }

    @Test
    void testParserWithLookUpToolOnBank() {
        InMemoryVocabularyLookupTool tool = new InMemoryVocabularyLookupTool();
        tool.addEntry(new VocabularyLookupArguments("bank", "English"), new VocabularyEntry("bank", "English", "noun", "Land beside a river."));

        var args = VocabularyToolCallParser.parse(bankFixture);
        assertTrue(args.isPresent());
        var entry = tool.lookup(args.get());
        assertEquals("bank", entry.term());
        assertEquals("English", entry.language());
        assertEquals("noun", entry.partOfSpeech());
        assertEquals("Land beside a river.", entry.definition());
    }

    @Test
    void buildsFollowUpPayloadFromVocabularyLookup() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var lookup = new InMemoryVocabularyLookupTool();
        lookup.addEntry(
                new VocabularyLookupArguments("bank", "English"),
                new VocabularyEntry("bank", "English", "noun", "Land beside a river."));

        var content = mapper.readTree(bankFixture)
                .get("candidates").get(0)
                .get("content");
        var result = lookup.lookup(VocabularyToolCallParser.parse(bankFixture).orElseThrow());

        String prompt = "Create an exercise for bank using its river meaning.";

        var payload = GeminiToolResponsePayloadBuilder.buildPayload(
                prompt,
                content,
                result
        );
        var sent = mapper.readTree(mapper.writeValueAsString(payload)).path("contents");
        assertEquals(3, sent.size());
        assertEquals("user", sent.at("/0/role").textValue());
        assertEquals(prompt, sent.at("/0/parts/0/text").textValue());
        assertEquals(content, sent.get(1));
        assertEquals("user", sent.at("/2/role").textValue());
        var sentResult = sent.at("/2/parts/0/functionResponse");
        assertEquals("lookup_vocabulary_entry", sentResult.path("name").textValue());
        assertEquals("bank-call-1", sentResult.path("id").textValue());
        assertEquals("Land beside a river.", sentResult.at("/response/definition").textValue());
    }

    @Test
    void buildsFollowUpWhenTextPrecedesFunctionCall() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var content = mapper.readTree(bankFixture).at("/candidates/0/content");
        var parts = (ArrayNode) content.path("parts");
        parts.insert(0, mapper.createObjectNode().put("text", "I'll look up that term."));
        var entry = new VocabularyEntry("bank", "English", "noun", "Land beside a river.");

        var payload = GeminiToolResponsePayloadBuilder.buildPayload("Create an exercise.", content, entry);
        var response = payload.at("/contents/2/parts/0/functionResponse");

        assertEquals(content, payload.at("/contents/1"));
        assertEquals("lookup_vocabulary_entry", response.path("name").textValue());
        assertEquals("bank-call-1", response.path("id").textValue());
    }

    @Test
    void omitsIdWhenFunctionCallHasNoId() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var content = mapper.readTree(bankFixture).at("/candidates/0/content");
        var call = (ObjectNode) content.at("/parts/0/functionCall");
        call.remove("id");
        var entry = new VocabularyEntry("bank", "English", "noun", "Land beside a river.");

        var payload = GeminiToolResponsePayloadBuilder.buildPayload("Create an exercise.", content, entry);
        var response = payload.at("/contents/2/parts/0/functionResponse");

        assertEquals("lookup_vocabulary_entry", response.path("name").textValue());
        assertFalse(response.has("id"));
    }

    @Test
    void testParserWithLookUpToolOnFrenchBank() {
        InMemoryVocabularyLookupTool tool = new InMemoryVocabularyLookupTool();
        tool.addEntry(new VocabularyLookupArguments("bank", "English"), new VocabularyEntry("bank", "English", "noun", "Land beside a river."));


        var args = VocabularyToolCallParser.parse(frenchBankFixture);
        assertTrue(args.isPresent());
        assertThrows(NoSuchElementException.class, () -> tool.lookup(args.get()));
    }



}
