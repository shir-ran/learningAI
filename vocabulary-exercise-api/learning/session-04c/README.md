# Session 4C — Execute a read-only vocabulary lookup

Core duration is 60 minutes, including the recap.

## Outcome

Use validated model arguments to retrieve a curated entry from an in-memory
dictionary. Verify a successful lookup and an absent entry locally with JUnit.

## 1. Implement the lookup

Create `InMemoryVocabularyLookupTool` in the `dev.learning.vocabulary`
package and implement the existing `VocabularyLookupTool` interface.

- Start with one immutable `VocabularyEntry` for `bank` in English.
  Use `noun` and the definition “Land beside a river.”
- Key the dictionary by both language and term. A small record can represent
  the key. Normalize each key with `toLowerCase(Locale.ROOT)` and keep the
  original spelling in the stored entry.
- Keep the existing `VocabularyEntry lookup(...)` signature. For this lab,
  use `NoSuchElementException` for a valid lookup with no matching entry.
  The caller will decide how to convey that outcome to Gemini.

Start with two tests. Check that `bank` in English returns the curated
entry and that an absent term produces the defined lookup failure.

## 2. Connect it to the parser in a local test

Pass your existing function-call fixture through `VocabularyToolCallParser`,
using `bank` and `English` as the arguments. Use the parsed arguments to
invoke the lookup and assert that the result contains the stored definition.
Check that the same term requested in French produces a miss. Both languages
are allowed, and only one has a matching entry in this fixture.

## Optional second hour

Prepare a follow-up payload that carries the dictionary result back to Gemini.
Reuse the existing REST endpoint, verify its function-response format in the
official documentation, and preserve the original model turn so the response
can be matched to the request. Start with an offline payload test.

Record your findings in [observations.md](observations.md).
