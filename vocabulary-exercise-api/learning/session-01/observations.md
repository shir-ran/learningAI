# Session 1 observations

## AI Studio repetitions

### Run 1

- Result:
Here is your vocabulary exercise:

**Sentence:**
If you work hard and stay focused, you will eventually ________ your goals.

**Options:**
A) achieve
B) borrow
C) deliver

**Correct option:**
A) achieve

**Explanation:**
המילה "achieve" פירושה "להשיג", והיא מתאימה להקשר של הגעה ליעדים או מטרות בעקבות עבודה קשה.


- Stable elements:the structure of the response
- Unexpected elements: the explanation sentence

### Run 2

- Result:
**Sentence:**
If you work hard and stay focused, you will eventually ________ your goal of becoming a professional musician.

**Options:**
A) achieve
B) attempt
C) approach

**Correct option:**
A) achieve

**Explanation:**
המילה "achieve" פירושה להשיג משהו, בדרך כלל מטרה או הישג, לאחר מאמץ והתמדה.
- Stable elements: same structure, almost same sentence
- Unexpected elements: different options, different explanation

### Run 3

- Result:
**Sentence:**
If you work hard and stay focused, you will eventually ________ your goals.

**Options:**
A) achieve
B) improve
C) organize

**Correct option:**
A) achieve

**Explanation (Hebrew):**
הפועל "achieve" פירושו להשיג או להגיע להישג, לכן הוא מתאים בהקשר של הגשמת מטרות.
- Stable elements: same structure and sentence, almost same explanation
- Unexpected elements: options is different although the sentence is almost the same

## Engineering experiments

| Experiment | Hypothesis | Observation | Prompt, schema, or Java? |
|---|---|---|---|
| Remove CEFR level | average level is used| same response as level B1| prompt |
| Compare A1 and C1 | C1 will use a more complex text| C1 used bank as a verb which is less common | prompt|
| Ambiguous word: bank | used it in the common meaning| used it in teh common meaning in levels A1 and B1 | Java - additional request parameter|
| Single-word options | no change  | no change | Java validation |
| Repeat identical prompt | similar but not identical results| similar but not identical results| prompt|

## Questions to revisit

- What made the output difficult to parse?
The free text response is less easy to parse then json format, for example.
- Which requirements can be enforced by a schema?
The structure that we want for the response, as well as limitation on length
Required fields.
Data types.
Exactly three options using minItems and maxItems.
Allowed exercise types using an enum.
No unexpected properties.
- Which requirements still require Java validation?
I'm not sure, but matbe character validation or array length?
    Java validation should enforce:
    - options contains exactly three distinct values.
    - correctAnswer appears exactly once in options.
    - No field is blank.
    - Every option is a single word.
    - The sentence contains a placeholder.
    - The answer is not accidentally visible in the sentence.
    - The requested target word matches the correct answer.

- What information should be logged, and what must never be logged?
metadata like tokens used or model used, request time, request success or failure should be logged. the request content, response content, api key should not be logged.

