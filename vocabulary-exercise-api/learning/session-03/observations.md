# Session 3 observations

## Part 1 — Prompts as code

### Why should the prompt be outside `VocabularyExerciseService`?

- so it would be easier to track version changes?

### Which prompt properties should a unit test verify?

- unit tests should verify the prompt structure, the correct use of the arguments to the prompt builder and maybe the existence of certain key word.

### Why should the test avoid asserting the complete prompt string?

- there is probably data in the prompt that a test cannot evaluate, or there is a specific wording that muight change without a significant impact on the model response.

## Evaluation dataset

| Word | CEFR | Behavior or risk represented |
|---|---|---|
| apple | A1 | I don't see any risk |
| achieve | B1 | a verb maybe used in a wrong way|
| bank | B1 | bank can be used as a verb or a riverbank meaning|
| despite | B2 | could be used in the wrong context|
| nuanced | C1 | for high level english the word has multiple meaning and it would probably be used wrong|

## Automated checks versus human judgment

| Property | Automated, human, or both? | Reason |
|---|---|---|
| Valid JSON and Java mapping | automated| structue properties are easy to examine with code |
| Exactly three distinct options | automated | easy to check with code|
| Sentence sounds natural | human | I don't think a code could check that |
| Sentence fits the requested CEFR level | human|I don't think a code could check that |
| Distractors are plausible but incorrect |human | I don't think a code could check that|
| Explanation is useful to the learner | both |I don't think a code could check explanation quality but it can test language and existence |

## Prompt v2 hypothesis

### Current weakness observed in v1

- V1 may define the target word without explaining why it fits better than the distractors.

### One change proposed for v2

- state that explanation should match the CEFR level and should explain what is the translation of the target word and why is it a better match than the other options.

### Expected measurable effect

- V2 explanations will receive a higher explanation-quality score because they include the translation and explain why the correct answer fits better than the distractors.

## Live comparison

Model:

Date:

| Prompt | Word | Automated result | Latency (ms) | Naturalness (1–3) | CEFR fit (1–3) | Distractors (1–3) | Explanation (1–3) | Notes |
|---|---|---|---:|---:|---:|---:|---:|---|
| v1 | apple | Exercise[sentence=I eat a red ________ for breakfast., options=[apple, bread, cake], correctAnswer=apple, explanation=תפוח (apple) הוא סוג של פרי פופולרי ובריא שבדרך כלל אוכלים כארוחת ביניים או כחלק מארוחת הבוקר.]|3854 |3 |3 | 1|1 | while red cake or red bread are not common they are still possible answers |
| v1 | bank |Exercise[sentence=I need to go to the ________ to withdraw some cash before we go shopping., options=[bank, shop, park], correctAnswer=bank, explanation=המילה bank מתייחסת למוסד פיננסי שבו אנשים מפקידים או מושכים את כספם.] |1245 |3 | 3|3 |3 | |
| v1 | despite |Exercise[sentence=________ the heavy rain, the outdoor concert continued as planned., options=[Despite, Although, However], correctAnswer=Despite, explanation=המילה despite משמשת לציון ניגוד, והיא מופיעה לפני שם עצם או גירנד (פועל ב-ing) כדי לתאר מצב שקרה למרות משהו אחר.] |4979 | 3| 3| 1| 2| explanation is not as clear as it could be|
| v2 | apple | Exercise[sentence=I want to eat a red ________ for my snack., options=[apple, table, house], correctAnswer=apple, explanation=המילה apple פירושה תפוח, וזהו הפרי היחיד ברשימה שמתאים לאכילה.]| 1372| 3|3 |2 |3 | the distractors are hardly plausible |
| v2 | bank |Exercise[sentence=I went to the ________ to withdraw some cash before going shopping., options=[bank, park, store], correctAnswer=bank, explanation=המילה bank פירושה בנק, וזהו המקום המתאים ביותר למשיכת מזומן בהשוואה לאפשרויות האחרות כמו פארק או חנות.] | 4736|3 | 3|3 |3 | the explanation is simpler and clearer for me |
| v2 | despite | Exercise[sentence=________ the heavy rain, the football match continued as planned., options=[Despite, Although, However], correctAnswer=Despite, explanation=המילה despite משמשת לציון ניגוד ומלווה בשם עצם (במקרה זה, the heavy rain), בניגוד ל-although שדורשת משפט שלם, או however שמשמשת לקישור בין משפטים.]| 12666| 3| 3| 1|3 | this explanation is better for the specific sentence |

## Result
Model: gemini-3.1-flash-lite
Date: 2026-10-29

Prompt: V1
Word: apple
Passed: true
Latency: 3854 ms
Exercise: Exercise[sentence=I eat a red ________ for breakfast., options=[apple, bread, cake], correctAnswer=apple, explanation=תפוח (apple) הוא סוג של פרי פופולרי ובריא שבדרך כלל אוכלים כארוחת ביניים או כחלק מארוחת הבוקר.]

Prompt: V1
Word: bank
Passed: true
Latency: 1245 ms
Exercise: Exercise[sentence=I need to go to the ________ to withdraw some cash before we go shopping., options=[bank, shop, park], correctAnswer=bank, explanation=המילה bank מתייחסת למוסד פיננסי שבו אנשים מפקידים או מושכים את כספם.]

Prompt: V1
Word: despite
Passed: true
Latency: 4979 ms
Exercise: Exercise[sentence=________ the heavy rain, the outdoor concert continued as planned., options=[Despite, Although, However], correctAnswer=Despite, explanation=המילה despite משמשת לציון ניגוד, והיא מופיעה לפני שם עצם או גירנד (פועל ב-ing) כדי לתאר מצב שקרה למרות משהו אחר.]

Prompt: V2
Word: apple
Passed: true
Latency: 1372 ms
Exercise: Exercise[sentence=I want to eat a red ________ for my snack., options=[apple, table, house], correctAnswer=apple, explanation=המילה apple פירושה תפוח, וזהו הפרי היחיד ברשימה שמתאים לאכילה.]

Prompt: V2
Word: bank
Passed: true
Latency: 4736 ms
Exercise: Exercise[sentence=I went to the ________ to withdraw some cash before going shopping., options=[bank, park, store], correctAnswer=bank, explanation=המילה bank פירושה בנק, וזהו המקום המתאים ביותר למשיכת מזומן בהשוואה לאפשרויות האחרות כמו פארק או חנות.]

Prompt: V2
Word: despite
Passed: true
Latency: 12666 ms
Exercise: Exercise[sentence=________ the heavy rain, the football match continued as planned., options=[Despite, Although, However], correctAnswer=Despite, explanation=המילה despite משמשת לציון ניגוד ומלווה בשם עצם (במקרה זה, the heavy rain), בניגוד ל-although שדורשת משפט שלם, או however שמשמשת לקישור בין משפטים.]


### Did the evidence support the hypothesis?

- I think so - the explanations are better suited to the level and the specific sentence

### What can we conclude from this small experiment?

- We can conclude that v2 received better explanation scores in this sample while retaining a 100% automated pass rate.

### What can we not conclude?

- We cannot conclude that v2 is generally superior, that it will always produce better explanations, or that its higher latency was caused by the prompt.

## Regression policy

- following your suggestions:
	- all deterministic unit tests must pass;
	- the automated pass rate on the fixed dataset must not decrease;
	- no rubric category may show a clear repeated regression;
	- failures and model/timeouts are reported, not silently discarded;
	- prompt version and model name are recorded with live results.

## Reflection

### Why is exact-text matching usually a poor LLM evaluation?

- exact-text matching is not optimal to evaluate LLM since there are many cases where different wordings have the same correctness.

### What does the offline evaluator protect us from?

- Offline evaluation protects parsing, validation, failure handling, and prompt plumbing without network cost or model variability.

### What can only the live comparison reveal?

- live comparison can reveal problems and improvements in the prompt creation, unexpected failures and changes in the semantic performance of the model.

