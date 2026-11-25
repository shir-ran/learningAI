# Session 4B — Parse and validate a tool request

Core duration: 60 minutes, including the recap.

## Outcome

Turn a model's proposed vocabulary lookup into a validated
`VocabularyLookupArguments` object. Use local JSON fixtures and JUnit.
Tool execution and a live Gemini call belong to the next session.

In Session 4A, you defined what Gemini can ask for and wrote the business
validator. Now you'll connect those pieces at the point where a model response
enters your Java application.

## 1. Verify the happy path

In `VocabularyToolCallParserTest`, read a fixture containing one candidate
with a single function call in its `parts` array. The relevant part is:

```json
{
  "functionCall": {
    "name": "lookup_vocabulary_entry",
    "args": {"term": "afraid of", "language": "English"}
  }
}
```

Verify the name and the resulting
`new VocabularyLookupArguments("afraid of", "English")`.

## 2. Extract the parser and handle mixed parts

Add a second fixture with a text part before the function call. Extract
`VocabularyToolCallParser` and have it search the first candidate's
`content.parts` for a `functionCall`. Both fixtures should yield the same
arguments. A text-only response is a normal no-call outcome. Give that case
an explicit result, such as `Optional.empty()`.

## 3. Check the tool name and arguments

Keep the accepted tool name explicit. Before constructing
`VocabularyLookupArguments`, require an `args` object containing exactly
`term` and `language`, both textual JSON values. You can check this on the
Jackson tree with `isObject()` and `isTextual()`, then map only the arguments
node. Give malformed requests a clear failure distinct from a text-only
response.

Reuse `VocabularyLookupArgumentsValidator.validate(...)` after mapping.
For example, `"German"` is a JSON string, so it passes the type check and
fails the language allowlist.

## Offline acceptance checks

- [ ] A valid call parses when it is the only part.
- [ ] The same call parses when a text part comes first.
- [ ] A text-only response produces a no-call result.
- [ ] An unknown tool name or unexpected field such as `debug` is rejected.
- [ ] Missing, null, or non-string arguments are rejected.
- [ ] A structurally valid call with an unsupported language is rejected.

If you have a second hour, use the same fixtures to add cases for malformed
JSON and multiple function calls. For this single-lookup lab, try rejecting
multiple calls explicitly and testing that decision.

Record your design decisions in [observations.md](observations.md).
