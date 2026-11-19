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

### Why does the tool accept only `word` and `language`?

- these are all the arguments we need to determine if the word is valid and we have it in our map.

### Which validation rules should be added in Session 4B?

- that word is a string of letters, and that language is a string of letters.

## Trust boundary after implementation

### Which data came from our application?

- the future implementation and the tool declaration came from our application.

### Which future data will come from the model?

- if the model would decide to call the word lookup it would provide the word and the language to look up.

### Why is the parameter schema not sufficient protection by itself?

- the schema cannot protect from unsafe, unsupported, or malicious input. It only checks the structure of the input, not its content or intent.

### Who has final authority to execute the tool?

- the application has final authority to execute the tool, since it is the one that will actually run the Java code and return the result.

## Reflection

### How is tool calling different from ordinary structured output?

- we give the model an interface to interact with the application and let it decide what to use, but we keep the specific implementation in the app.

### Why are we defining the boundary before building an agent loop?

- so we know there is a safe limit to what the model can do.

## validation list:
- reject null and blank values;
- enforce reasonable maximum lengths;
- initially allow only supported languages, such as English;
- decide explicitly whether phrases, hyphens, apostrophes, and non-Latin letters are allowed;
- reject unexpected JSON properties;
- normalize case only after validation.