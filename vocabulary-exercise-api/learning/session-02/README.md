# Session 2 — Structured output and semantic validation

Duration: 4 hours

## Outcome

By the end of this session, the application will:

- request JSON that conforms to an explicit JSON Schema;
- deserialize the JSON into a Java `Exercise` record;
- reject structurally valid but semantically invalid exercises;
- test parsing and business validation without calling Gemini;
- make one real integration call after the deterministic tests pass.

## Why this session matters

Session 1 returned attractive free-form text, but the Java application could not
reliably consume it. Structured output gives us a machine-readable contract.

It does not make model output automatically correct. We will deliberately keep
three layers separate:

1. **Prompt** — describes the learning task and desired meaning.
2. **JSON Schema** — constrains the response shape.
3. **Java validation** — enforces business invariants.

## Part 1 — Design the Java contract

Create an immutable Java record with these fields:

```java
public record Exercise(
        String sentence,
        List<String> options,
        String correctAnswer,
        String explanation) {
}
```

Before writing validation, answer in `observations.md`:

- Which properties are structural?
- Which properties are semantic?
- Should the target word be included in the response, or remain request data?

## Part 2 — Define the JSON Schema

The schema should require:

- one object;
- exactly the four expected properties;
- all four properties to be required;
- all values to have the correct JSON type;
- exactly three option strings;
- no additional properties.

Use descriptions to tell the model what each property means. Do not rely on
unsupported JSON Schema constraints for business validation.

## Part 3 — Request structured output

Extend the Gemini REST request with a generation configuration that requests
`application/json` and supplies the schema.

The transport layer should still return the text contained in Gemini's response.
That text is now expected to be a JSON document rather than formatted prose.

## Part 4 — Deserialize

Use Jackson to deserialize the returned JSON into `Exercise`.

Treat these as distinct failures:

- HTTP or network failure;
- Gemini returns no candidate text;
- candidate text is not valid JSON;
- JSON cannot be mapped to `Exercise`;
- the mapped `Exercise` violates a business invariant.

## Part 5 — Semantic validation

At minimum, reject an exercise when:

- any required string is blank;
- the options do not contain exactly three distinct values;
- `correctAnswer` does not appear exactly once in `options`;
- an option contains whitespace when only one-word options are allowed;
- the sentence does not contain the `________` placeholder;
- the completed sentence already exposes the correct answer instead of the placeholder.

Return all validation violations together rather than failing at the first one.

## Part 6 — Deterministic tests

Add tests for:

1. one valid exercise;
2. a missing placeholder;
3. duplicate options;
4. a correct answer absent from the options;
5. a blank explanation;
6. a multi-word option;
7. valid JSON deserialization;
8. malformed or incompatible JSON.

These tests must not call Gemini.

## Part 7 — Integration experiment

Call Gemini at least five times with different words. Record:

- whether every response deserialized successfully;
- whether every response passed semantic validation;
- which invariant failed most often;
- whether the schema changed the variability of the content or only its shape.

## Completion checklist

- [ ] `Exercise` is an immutable Java record.
- [ ] The request includes a JSON Schema.
- [ ] Gemini returns JSON rather than Markdown.
- [ ] Jackson maps the response to `Exercise`.
- [ ] Semantic validation reports all violations.
- [ ] Unit tests run without Gemini.
- [ ] At least five real responses were evaluated.
- [ ] Findings are recorded in `observations.md`.

## References

- https://ai.google.dev/gemini-api/docs/structured-output
- https://ai.google.dev/api/generate-content

