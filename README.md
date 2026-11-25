# AI Backend Learning Lab

This repository contains a small, Java-first learning project for integrating
LLMs into backend applications. The example domain is vocabulary practice.

The project is intentionally small. It is a lab for learning one AI capability
at a time, not a complete language-learning product.

## Learning format

Each topic is split into a 60-minute core session and, when useful, an optional
second hour. A session normally contains:

1. recall questions and a short summary of the previous session;
2. one new concept;
3. one focused Java implementation;
4. deterministic tests and observations;
5. an optional extension that does not block the next session.

The project uses short prompts, synthetic vocabulary data, and a small number of
live model calls so the learning process remains inexpensive.

## Completed and current sessions

| Session | Topic | Main outcome | Status |
|---|---|---|---|
| [1](vocabulary-exercise-api/learning/session-01/README.md) | From prompt to Java integration | Called Gemini from Java, isolated the model behind a gateway, and handled connectivity, timeouts, retries, model selection, and API-key configuration. | Complete |
| [2](vocabulary-exercise-api/learning/session-02/README.md) | Structured output and semantic validation | Added a JSON Schema, Jackson deserialization, Java business validation, fake-model tests, and a small live integration experiment. | Complete |
| [3](vocabulary-exercise-api/learning/session-03/README.md) | Prompt evaluation and regression testing | Versioned prompts, created a fixed evaluation dataset, separated automated checks from a human rubric, and compared two prompt versions. | Complete |
| [4A](vocabulary-exercise-api/learning/session-04a/README.md) | Safe tool contract | Defined typed tool arguments and results, a Java-owned interface, a model-visible function declaration, the trust boundary, and business validation for model-generated arguments. | Complete |
| [4B](vocabulary-exercise-api/learning/session-04b/README.md) | Parse and validate a tool request | Parsed proposed lookups, handled text-only replies, and tested argument validation offline. | Complete |

## Current session

Open [Session 4C — Execute a read-only vocabulary lookup](vocabulary-exercise-api/learning/session-04c/README.md).

## Upcoming path

1. Implement the read-only vocabulary lookup and return its result to Gemini.
2. Build one bounded agent loop.
3. Add production controls: step limits, timeouts, budgets, tracing, and
   approval boundaries.
4. Explore MCP and reusable skills.
5. Optionally compare multi-agent designs and retrieval-augmented generation.

## Cost policy

- Start with a Google AI Studio free-tier project.
- Use short prompts and short responses.
- Do not enable billing for the learning project unless explicitly needed.
- Never commit an API key.
- Use synthetic vocabulary data only; do not send personal or confidential data.

## Project layout

```text
vocabulary-exercise-api/             Small Java lab application
vocabulary-exercise-api/learning/    Session notes and exercises
```

