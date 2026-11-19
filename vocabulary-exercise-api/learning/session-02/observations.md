# Session 2 observations

## Contract design

### Structural properties

- Field names and types are structural. Whether each value fulfills the meaning described by its field is semantic.

### Semantic properties

- I think the distiction between the sentence, the correct answer and the explanation is semantic since there is no factor in the code that can determine for certain which string is which.

### Should the target word be returned by the model?

- I think it does not badly affect the model response and it allows another level of validation, but we decided not to return targetWord; Java will retain it as trusted request data.

## Schema versus Java validation

| Requirement | Prompt | JSON Schema | Java | Reason |
|---|:---:|:---:|:---:|---|
| All four fields are present |  | X| | structural|
| Exactly three options | | X| | structural|
| Options are distinct | | |X |semantic |
| Every option is one word | | |X | semantic |
| Correct answer appears in options | | | X|semantic |
| Sentence is appropriate for CEFR level |X | | |semantic but cannot be tested by code |
| Sentence contains a placeholder |X | | X| semantic |

## Integration runs

| Word | Deserialized? | Valid? | Violations or observations |
|---|:---:|:---:|---|
| achieve |yes | yes| Exercise[sentence=If you work hard and stay focused, you will eventually ________ your long-term goals., options=[achieve, replace, collect], correctAnswer=achieve, explanation=הפועל to achieve פירושו להשיג מטרה או תוצאה רצויה בעקבות מאמץ.]
|
| bank | yes | yes | Exercise[sentence=I need to go to the ________ to withdraw some cash for my trip., options=[bank, market, library], correctAnswer=bank, explanation=המילה bank מתייחסת למוסד פיננסי שבו מפקידים או מושכים כסף.]
|
| improve | yes | yes | Exercise[sentence=If you practice your English every day, your speaking skills will ________ significantly., options=[improve, change, develop], correctAnswer=improve, explanation=המילה improve פירושה להשתפר, והיא משמשת כאן לתיאור תהליך של התקדמות במיומנות שפה.]
|
| despite | yes| yes | Exercise[sentence=________ the heavy rain, the football match continued as planned., options=[Despite, Although, However], correctAnswer=Despite, explanation=המילה despite משמשת לציון ניגוד, ופירושה בעברית הוא 'למרות'.]
|
| approach | yes |yes |Exercise[sentence=We need a new ________ to solve this difficult math problem because our current method is not working., options=[approach, opinion, purpose], correctAnswer=approach, explanation=המילה approach פירושה גישה או דרך פעולה לטיפול בבעיה או נושא מסוים.]
 |

## Reflection

- What did the schema guarantee?
the schema guaranteed property names and types as well as the 3 options
- What did it not guarantee?
It did not guarantee semantic properties like 3 distinct options, or that the correct answer is in the options
- Which validation rule caught the most useful problem?
Actually I experienced invalid json and empty explanation
- Did structured output reduce semantic variability?
I Don't think so.

