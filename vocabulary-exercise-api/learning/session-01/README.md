# Session 1 — From prompt to a Java integration

Duration: 4 hours

## Outcome

By the end of the session, you should be able to:

- explain the difference between a prompt, model, request, and response;
- create a Gemini API key without placing it in source control;
- call Gemini from a small Java program;
- keep the provider SDK behind an application-owned interface;
- identify what is still unsafe or unreliable about a plain-text response;
- describe why structured output is the next step.

The goal is not to build a web service yet. We start with a console program so
that the model interaction remains visible and easy to debug.

## Before the session

The following is already available on this computer:

- Java 21
- IntelliJ IDEA Community Edition
- Visual Studio Code

Maven is not currently available on the command line. IntelliJ normally ships
with a bundled Maven installation, so the first setup task will be opening the
`vocabulary-exercise-api` folder in IntelliJ and checking whether it can import
the Maven project. If not, we will add Maven Wrapper during the session.

## Part 1 — Explore the behavior in Google AI Studio (35 minutes)

Create or select a dedicated free-tier project in Google AI Studio. Do not
enable billing for this lab.

Run this prompt three times:

```text
You are creating one vocabulary exercise for a Hebrew speaker learning English.
The learner is at CEFR level B1.
Target word: achieve

Return:
- one fill-in-the-blank sentence;
- three answer options;
- the correct option;
- a one-sentence explanation in Hebrew.
```

Record the three responses in `observations.md` and answer:

1. Which parts remained stable?
    the structure and the correct answer stayed the same.
2. Which parts changed? 
    The sentence slightly changed as well as the explanation. the options are completely different.
3. Did every response contain exactly three options?
    yes
4. Could Java parse the response without relying on formatting guesses?
    Probably so, since the structure is fixed, but the format is not as friendly as a json format for example.
5. What would happen if the model returned additional prose?
    then the structure would be different and the parsing would be broken.

The main lesson is that a good-looking response is not yet a reliable API
contract.

## Part 2 — Request anatomy (25 minutes)

For the request above, identify:

- instructions: the stable behavior we want;
    the return content with 4 articles
- input data: level, languages, and target word;
    the arguments to the service
- output contract: fields the application expects;
    It only expects a string back right now.
- model configuration: model name and output-length limits;
model is picked but there are no set limits
- operational metadata: latency, token usage, and error details.
    I see no operational metadata

Write down one example of a rule that belongs in Java rather than in the
prompt. A good example is checking that the options contain the correct answer.
    that the sentence is only in anglish letters and that the explanation has hebrew letters.

## Part 3 — Safe API-key setup (20 minutes)

Create a new key for the learning project in Google AI Studio. Keep it in the
current PowerShell process only:

```powershell
$env:GOOGLE_API_KEY = "paste-the-key-here"
```

Confirm that a value exists without printing the value:

```powershell
if ($env:GOOGLE_API_KEY) { "GOOGLE_API_KEY is set" }
```

Never place the key in Java code, `pom.xml`, a committed properties file, a
screenshot, or an AI prompt.

## Part 4 — Read the starter code (25 minutes)

The code uses this boundary:

```java
public interface LanguageModelGateway {
    String generate(String prompt);
}
```

Discuss why the application owns this interface instead of exposing the Gemini
SDK throughout the codebase:

- tests can use a fake implementation;
- the provider can be replaced;
- retry, metrics, and cost controls have one home;
- domain code does not depend on a vendor response type.

## Part 5 — First Java call (60 minutes)

1. Open `vocabulary-exercise-api` in IntelliJ.
2. Import it as a Maven project.
3. Verify that IntelliJ uses Java 21.
4. Make sure `GOOGLE_API_KEY` is present in the run configuration environment.
5. Run `VocabularyLabApplication`.
6. Change the target word and run it again.

Expected behavior: the console prints one generated vocabulary exercise. The
response is deliberately plain text in this session.

## Part 6 — Engineering experiments (45 minutes)

Perform these experiments one at a time:

1. Remove the learner level from the prompt.
    - without the level it was similar to B1 which I guess is the average level
2. Replace B1 with A1 and then C1.
    - A1 gave an easier sentence and C1 gave a more complex sentence and a different meaning as it used "bank" as a verb, not a noun.
3. Request an ambiguous word such as `bank`.
    - for low level assume bank as the finanacial institute, for higher levels - other meanings
4. Add an instruction that every option must be a single word.
    - There should be no difference since all pre existing responses gave a one word answer
5. Use the same prompt three times and compare the results.

For every experiment, write:

- hypothesis;
- observed result;
- whether the behavior belongs in the prompt, schema, or Java validation.

## Part 7 — Testing without spending API quota (25 minutes)

Run `VocabularyExerciseServiceTest`. It uses a lambda as a fake model and does
not call Gemini. Then add one more assertion of your own.

The important distinction is:

- unit tests should normally not call a paid or nondeterministic model;
- a small separate integration test may call the real model;
- output quality requires evals, which we will add later.

## Completion checklist

- [ ] A dedicated AI Studio learning project exists.
- [ ] Billing has not been enabled unintentionally.
- [ ] The API key is available only as an environment variable.
- [ ] The Java program makes one successful Gemini request.
- [ ] The unit test runs without an API call.
- [ ] `observations.md` contains results from at least three experiments.
- [ ] You can explain why plain text is not a safe application contract.

## Short homework

Design the Java record that you would like the model to return next time. Do
not implement parsing yet. Include only the fields that the application truly
needs.

Suggested starting point:

```java
public record Exercise(
    String sentence,
    List<String> options,
    String correctAnswer,
    String explanation
) {}
```

In Session 2 we will turn this into a JSON Schema, parse it, and validate its
meaning in Java.

