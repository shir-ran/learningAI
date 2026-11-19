# Session 4A — Define a safe tool contract

Core duration: 60 minutes

Optional extension: up to 45 minutes

## One essential outcome

Define one vocabulary lookup tool as a typed Java contract and a model-visible
function declaration. Do not call Gemini and do not execute the tool yet.

## Mental model

Function calling does not allow the model to invoke arbitrary Java methods.

The flow is:

1. The application sends a prompt plus a description of an allowed tool.
2. The model may return a structured request containing a tool name and arguments.
3. The application treats that request as untrusted input.
4. The application validates and authorizes the request.
5. Only application code may execute the real operation.
6. The application can send the result back to the model for a final response.

Today we build only the contract used in steps 1–3.

## Why this matters for agents

A normal model call produces content. Tool calling allows the model to propose an
action. An agent adds a loop that can repeat the sequence. Safe tool boundaries
must therefore exist before we build an agent loop.

## The tool for this project

Our tool is named `lookup_vocabulary_entry`.

Its purpose is to retrieve trusted vocabulary metadata rather than ask the model
to invent it. For now, the eventual data source will be an in-memory Java map, so
the exercise remains free and deterministic.

The tool accepts:

- `word`: one word to look up;
- `language`: the language of that word.

It returns:

- the normalized word;
- the language;
- the part of speech;
- a short definition.

## Part 1 — Answer the boundary questions

Answer the questions in `observations.md` before coding.

## Part 2 — Define the typed Java contract

Create these records:

```java
public record VocabularyLookupArguments(
        String word,
        String language) {
}
```

```java
public record VocabularyEntry(
        String word,
        String language,
        String partOfSpeech,
        String definition) {
}
```

Create the application-owned interface:

```java
public interface VocabularyLookupTool {
    VocabularyEntry lookup(VocabularyLookupArguments arguments);
}
```

The model never receives a Java method reference. The interface represents what
our application may choose to execute after validation.

## Part 3 — Define the model-visible declaration

Create this resource:

`src/main/resources/vocabulary-lookup-tool.json`

```json
{
  "name": "lookup_vocabulary_entry",
  "description": "Looks up trusted metadata for one vocabulary word in a specified language.",
  "parameters": {
    "type": "object",
    "properties": {
      "word": {
        "type": "string",
        "description": "The single vocabulary word to look up."
      },
      "language": {
        "type": "string",
        "description": "The language of the word, for example English."
      }
    },
    "required": ["word", "language"]
  }
}
```

This declaration is not the implementation. It is an API description that helps
the model construct a request.

## Part 4 — Load and test the declaration

Create `VocabularyLookupToolDefinitionLoader`, following the style of
`ExerciseSchemaLoader`. It should load the JSON resource as a `JsonNode` and fail
clearly if the resource is missing or invalid.

Create `VocabularyLookupToolDefinitionLoaderTest`. Verify meaningful contract
properties rather than comparing the complete JSON text:

1. the tool name is `lookup_vocabulary_entry`;
2. the parameters type is `object`;
3. `word` and `language` are declared as strings;
4. both names appear in `required`;
5. the description explains that the data is trusted.

## Part 5 — Explain the trust boundary

Complete the final section of `observations.md`:

- Which data came from our application?
- Which future data will come from the model?
- Why will schema validation alone not be enough?
- Who has authority to execute the tool?

## Completion checklist

- [ ] Boundary questions are answered.
- [ ] `VocabularyLookupArguments` exists.
- [ ] `VocabularyEntry` exists.
- [ ] `VocabularyLookupTool` exists.
- [ ] The JSON function declaration exists.
- [ ] The declaration loader exists.
- [ ] Deterministic loader tests pass.
- [ ] No Gemini call was made.
- [ ] No tool implementation or execution was added yet.

## Optional second-hour extension

Design validation rules for future model-generated arguments, but do not
implement them yet. Consider blank values, unexpected properties, unsupported
languages, multi-word input, maximum length, and Unicode.

## Reference

- https://ai.google.dev/gemini-api/docs/function-calling

