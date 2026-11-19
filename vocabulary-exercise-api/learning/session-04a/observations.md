# Session 4A observations

## Before coding

### Does the model execute the Java tool? If not, who does?

- the execution in Java is done by the application, not the model. The model generates the arguments and requests a tool call, but the actual execution of the tool is handled by the application.

### What is the difference between a tool declaration and a tool implementation?

- a tool declaration defines the input names and types and what the tool does, while the tool implementation determines how it is done.

### Why should model-generated tool arguments be treated as untrusted input?

- because we cannot rely on an AI model to provide consistently 100% reliable input.  

### Why is a read-only lookup a good first tool?

- a read only tool is safer since it does not change the data.

## Contract decisions

### Why does the tool accept only `term` and `language`?

- these are all the arguments we need to determine if the term is valid and we have it in our map.

### Which validation rules should be added in Session 4B?

- reject blank or oversized values; allow only supported languages; allow one to three tokens separated by single spaces; allow Unicode letters, internal hyphens, and internal apostrophes; reject unexpected properties.

## Trust boundary after implementation

### Which data came from our application?

- the future implementation and the tool declaration came from our application.

### Which future data will come from the model?

- if the model decides to request the term lookup, it will provide the term and language to look up.

### Why is the parameter schema not sufficient protection by itself?

- the schema cannot protect from unsafe, unsupported, or malicious input. It only checks the structure of the input, not its content or intent.

### Who has final authority to execute the tool?

- the application has final authority to execute the tool, since it is the one that will actually run the Java code and return the result.

## Reflection

### How is tool calling different from ordinary structured output?

- we give the model an interface to interact with the application and let it decide what to use, but we keep the specific implementation in the app.

### Why are we defining the boundary before building an agent loop?

- so we know there is a safe limit to what the model can do.

## Validation list
- reject null and blank values;
- enforce reasonable maximum lengths;
- allow only English, Spanish, and French initially;
- allow one to three tokens, internal hyphens, internal apostrophes, and Unicode letters;
- reject unexpected JSON properties;
- normalize case only after validation.

## Optional extension — validation policy

| Decision | Chosen policy               | Reason                                                                                                                                       |
|---|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| Maximum term length | 20                          | words or phrases over 20 characters are outside the initial product scope                                                                    |
| Maximum language length | 10                          | there are hardly languages whose name is longer than 10 characters and since we start with english we certainly don't need anymore than that |
| Supported languages | English, Spanish, and French | most commonly used in my house                                                                                                               |
| Leading/trailing whitespace | reject                      | prevents ambiguous or inconsistent lookup keys                                                                                               |
| Internal spaces and phrases | allow one to three tokens separated by exactly one ordinary space | supports common phrases such as "afraid of" while keeping the input bounded                                              |
| Hyphens | allowed                     | they are common                                                                                                                              |
| Apostrophes | allowed                     | they are common                                                                                                                              |
| Non-Latin Unicode letters | allow                       | java can handle it with Character.isLetter                                                                                                   |
| Unexpected JSON properties | reject                      | Unexpected-property rejection prevents ambiguous or silently ignored input.                                                                  |
| Case normalization | normalize language and the lookup key; preserve the original term for display | keeps lookup behavior consistent without losing the learner's input form                                       |

### Which checks belong to JSON parsing and which belong to business validation?

* json parsing would check:
  - valid JSON;
  - expected field types;
  - required fields;
  - unexpected properties.
* business validation would check:
  - null and blank values
  - maximum lengths
  - supported languages
  - leading/trailing whitespace
  - internal spaces and phrases
  - hyphens
  - apostrophes
  - non-latin unicode letters

### Should invalid arguments return all violations or only the first one?

- invalid arguments should return all violations, so the model/application can fix them all at once instead of having to go through multiple rounds of validation.

### Should validation modify values, or should normalization be a separate step?

- validation should not modify values, normalization should be a separate step. Validation should only check if the input is valid or not, and normalization should be done after validation to ensure consistency.
