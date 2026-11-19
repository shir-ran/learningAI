package dev.learning.vocabulary;

import java.util.Objects;

public final class ExercisePromptBuilder {
    private final PromptVersion version;


    public ExercisePromptBuilder() {
        this(PromptVersion.V1);
    }

    public ExercisePromptBuilder(PromptVersion version) {
        this.version = Objects.requireNonNull(version);
    }

    public String build(
            String nativeLanguage,
            String targetLanguage,
            String cefrLevel,
            String targetWord) {

        return switch (version) {
            case V1 -> buildV1(
                    nativeLanguage, targetLanguage, cefrLevel, targetWord);
            case V2 -> buildV2(
                    nativeLanguage, targetLanguage, cefrLevel, targetWord);
        };
    }


    private String buildV1(
            String nativeLanguage,
            String targetLanguage,
            String cefrLevel,
            String targetWord) {
        return  """
            You create one vocabulary exercise.
            The learner's native language is %s.
            The target language is %s.
            The learner's CEFR level is %s.
            The target word is: %s

            Return:
            - one json object;
            - exactly the four expected properties: "sentence", "options", "correctAnswer", "explanation";
            - all four properties to be required;
            - all values to have the correct JSON type;
            - no additional properties.
                            
            - "sentence" is one fill-in-the-blank sentence with '________' for the missing target word;
            - exactly three option strings under "options" that are plausible alternatives for the missing word in the sentence;
            - the "correctAnswer" which is the target word;
            - a one-sentence explanation in the learner's native language, it can explain the meaning of the word and/or the context in which it is used.
            """.formatted(nativeLanguage, targetLanguage, cefrLevel, targetWord);
    }

    private String buildV2(
            String nativeLanguage,
            String targetLanguage,
            String cefrLevel,
            String targetWord) {
        return  """
            You create one vocabulary exercise.
            The learner's native language is %s.
            The target language is %s.
            The learner's CEFR level is %s.
            The target word is: %s

            Return:
            - one json object;
            - exactly the four expected properties: "sentence", "options", "correctAnswer", "explanation";
            - all four properties to be required;
            - all values to have the correct JSON type;
            - no additional properties.
                            
            - "sentence" is one fill-in-the-blank sentence with '________' for the missing target word;
            - exactly three option strings under "options" that are plausible alternatives for the missing word in the sentence;
            - the "correctAnswer" which is the target word;
            - a one-sentence explanation in the learner's native language that should match the CEFR level, explain the translation of the target word and why it is a better match than the other options.
            """.formatted(nativeLanguage, targetLanguage, cefrLevel, targetWord);
    }
}

