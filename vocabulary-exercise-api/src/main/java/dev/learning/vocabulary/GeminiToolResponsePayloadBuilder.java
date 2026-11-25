package dev.learning.vocabulary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GeminiToolResponsePayloadBuilder {
    public static ObjectNode buildPayload(String prompt, JsonNode content, VocabularyEntry entry) {
        var functionCall = findFunctionCall(content);
        var mapper = new ObjectMapper();
        var payload = mapper.createObjectNode();
        var contents = payload.putArray("contents");
        var userTurn = contents.addObject().put("role", "user");
        userTurn.putArray("parts").addObject().put("text", prompt);
        contents.add(content.deepCopy());

        var resultTurn = contents.addObject().put("role", "user");
        var functionResponse = resultTurn.putArray("parts")
                .addObject().putObject("functionResponse");
        functionResponse.put("name", functionCall.path("name").textValue());
        var id = functionCall.path("id");
        if (id.isTextual()) {
            functionResponse.put("id", id.textValue());
        }
        functionResponse.set("response", mapper.valueToTree(entry));

        return payload;
    }

    private static JsonNode findFunctionCall(JsonNode content) {
        for (JsonNode part : content.path("parts")) {
            if (part.has("functionCall")) {
                return part.get("functionCall");
            }
        }
        throw new IllegalArgumentException("Model content is missing a function call");
    }
}
