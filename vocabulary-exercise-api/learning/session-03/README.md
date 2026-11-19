# Session 3 — Prompt evaluation and regression testing

Duration: 4 hours

## Outcome

By the end of this session, the project will:

- treat prompts as versioned application code;
- evaluate prompts against a small, repeatable dataset;
- distinguish deterministic checks from human quality judgments;
- compare two prompt versions using the same inputs;
- run most evaluation work without calling Gemini;
- use a small live experiment to detect prompt regressions.

## Why this session matters

A prompt that works once is a demo. A prompt that performs well on a fixed set
of representative cases—and can be compared with its replacement—is an
engineering component.

We will not assert exact generated sentences. Model output is variable, so the
evaluation has two complementary layers:

1. **Automated checks** — parsing, Java validation, and properties that code can
   determine reliably.
2. **Human rubric** — naturalness, level fit, distractor quality, and teaching
   value.

## Cost policy

- All unit tests use fake model responses and cost nothing.
- The live comparison uses only a small fixed dataset.
- Start with three cases per prompt version (six requests total).
- Stop the live run if the free-tier API is unavailable; the offline work still
  remains useful.

## Part 1 — Extract and version the prompt

Move prompt construction out of `VocabularyExerciseService` into a small class:

```java
public final class ExercisePromptBuilder {
    public static final String VERSION = "v1";

    public String build(
            String nativeLanguage,
            String targetLanguage,
            String cefrLevel,
            String targetWord) {
        // Return the current prompt text.
    }
}
```

Inject `ExercisePromptBuilder` into `VocabularyExerciseService`. Keep a
convenience constructor if useful, so existing callers do not become noisy.

Add an `ExercisePromptBuilderTest` that verifies the prompt contains all four
request values and the important constraints. Do not test the entire prompt as
one large string: that would make harmless wording changes painful.

Before coding, answer the Part 1 questions in `observations.md`.

## Part 2 — Create a representative evaluation dataset

Create an immutable request record:

```java
public record ExerciseRequest(
        String nativeLanguage,
        String targetLanguage,
        String cefrLevel,
        String targetWord) {
}
```

Create a small dataset with these cases:

| Native language | Target language | CEFR | Word | Why it is useful |
|---|---|---|---|---|
| Hebrew | English | A1 | apple | simple concrete noun |
| Hebrew | English | B1 | achieve | familiar verb |
| Hebrew | English | B1 | bank | ambiguous word |
| Hebrew | English | B2 | despite | grammar-sensitive word |
| Hebrew | English | C1 | nuanced | advanced adjective |

Store it in test code or a JSON test resource. Five purposeful cases are more
useful here than a large random word list.

Refactor the service to also accept an `ExerciseRequest`, while preserving the
existing four-argument method if you want backward compatibility.

## Part 3 — Define the evaluation contract

For every generated exercise, record the automated outcome:

- response received;
- JSON parsed;
- semantic validation passed;
- latency in milliseconds;
- failure category and message, if any.

For valid exercises, score each human criterion from 1 to 3:

| Criterion | 1 | 2 | 3 |
|---|---|---|---|
| Naturalness | unnatural or incorrect | understandable but awkward | natural target-language sentence |
| CEFR fit | clearly wrong level | debatable | appropriate for requested level |
| Distractors | broken or trivial | partly plausible | plausible but unambiguously incorrect |
| Explanation | wrong/unhelpful | adequate | concise and pedagogically useful |

These scores are ordinal signals, not scientific measurements. Their value is
consistency: use the same rubric for both prompt versions.

## Part 4 — Build a deterministic evaluation test

Use a fake `LanguageModelGateway` that returns one response per dataset case.
Test that:

1. every case is passed to the prompt builder;
2. valid fixture responses pass;
3. one intentionally invalid fixture is classified as a validation failure;
4. evaluation continues after one case fails;
5. the summary counts successes and failures correctly.

Keep this evaluator small. Suggested types are:

```java
public record EvaluationResult(
        ExerciseRequest request,
        boolean passed,
        long latencyMillis,
        String failure) {
}
```

```java
public final class ExerciseEvaluator {
    public List<EvaluationResult> evaluate(List<ExerciseRequest> requests) {
        // Evaluate every request, including those after a failure.
    }
}
```

Do not add concurrency yet. A sequential evaluator is easier to understand and
friendlier to API rate limits.

## Part 5 — Create prompt v2 from a hypothesis

Do not change wording randomly. First write one concrete hypothesis in
`observations.md`, for example:

> If the prompt explicitly says that each distractor must fit grammatically but
> make the completed sentence semantically incorrect, distractor quality will
> improve without reducing the automated pass rate.

Create prompt v2 with only the change needed to test that hypothesis. Preserve
v1 so both versions can be compared. A prompt version may be an enum, separate
builder method, or a constructor parameter; choose the smallest clear design.

## Part 6 — Run the small live comparison

Use only these first three cases for the initial live experiment:

- `apple` at A1;
- `bank` at B1;
- `despite` at B2.

Run the same cases once with v1 and once with v2. Record each result and human
score in `observations.md`.

Do not conclude that v2 is universally better from six calls. Decide only
whether the result supports another experiment or reveals an obvious regression.

## Part 7 — Add a regression policy

Write a short policy for future prompt changes. At minimum:

- all deterministic unit tests must pass;
- the automated pass rate on the fixed dataset must not decrease;
- no rubric category may show a clear repeated regression;
- failures and model/timeouts are reported, not silently discarded;
- prompt version and model name are recorded with live results.

## Completion checklist

- [ ] Prompt construction is outside the service and has a version.
- [ ] A prompt builder test checks meaningful properties.
- [ ] The five-case evaluation dataset exists.
- [ ] Automated and human evaluation criteria are documented.
- [ ] The evaluator continues after individual failures.
- [ ] Evaluator behavior is tested without Gemini.
- [ ] Prompt v2 represents one written hypothesis.
- [ ] Three cases were run against both prompt versions.
- [ ] Results and rubric scores are recorded.
- [ ] A regression policy is written.

## Concepts to retain

- **Golden dataset:** a stable set of representative inputs used across changes.
- **Regression:** an existing behavior becomes worse after a change.
- **Rubric:** explicit criteria used to judge subjective quality consistently.
- **Offline evaluation:** tests based on fixtures or recorded outputs; cheap and
  deterministic.
- **Online evaluation:** calls to the real model; realistic but variable and
  potentially costly.
- **Prompt version:** an identifier that makes generated behavior traceable.

