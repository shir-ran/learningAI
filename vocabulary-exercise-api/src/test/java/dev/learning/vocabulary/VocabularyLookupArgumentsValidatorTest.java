package dev.learning.vocabulary;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VocabularyLookupArgumentsValidatorTest {
    @Test
    void validatesAppleInEnglish() {
        var args = new VocabularyLookupArguments("apple", "English");
        assertTrue(VocabularyLookupArgumentsValidator.validate(args).isEmpty());
    }

    @Test
    void acceptsUnicodeLettersAndCombiningMarks() {
        var args = new VocabularyLookupArguments("cafe\u0301", "French");
        assertTrue(VocabularyLookupArgumentsValidator.validate(args).isEmpty());
    }

    @Test
    void validatesAfraidOfInEnglish() {
        var args = new VocabularyLookupArguments("afraid of", "english");
        assertTrue(VocabularyLookupArgumentsValidator.validate(args).isEmpty());
    }

    @Test
    void validatesHyphenatedTerm() {
        var args = new VocabularyLookupArguments("mother-in-law", "english");
        assertTrue(VocabularyLookupArgumentsValidator.validate(args).isEmpty());
    }

    @Test
    void validatesTermWithApostrophe() {
        var args = new VocabularyLookupArguments("it's", "english");
        assertTrue(VocabularyLookupArgumentsValidator.validate(args).isEmpty());
    }

    @Test
    void validatesNullArguments() {
        assertFalse(VocabularyLookupArgumentsValidator.validate(null).isEmpty());
    }

    @Test
    void validatesBlankTermAndLanguage() {
        var args = new VocabularyLookupArguments("", "");
        var errors = VocabularyLookupArgumentsValidator.validate(args);
        assertTrue(errors.contains("The 'language' argument is required and cannot be blank."));
        assertTrue(errors.contains("The 'term' argument is required and cannot be blank."));
    }

    @Test
    void validatesTermOver20Characters() {
        var args = new VocabularyLookupArguments("thisisaverylongtermthatexceeds20chars", "english");
        var errors = VocabularyLookupArgumentsValidator.validate(args);
        assertTrue(errors.contains("The 'term' argument must not exceed 20 characters."));
    }

    @Test
    void validatesUnsupportedLanguage() {
        var args = new VocabularyLookupArguments("apple", "german");
        var errors = VocabularyLookupArgumentsValidator.validate(args);
        assertTrue(errors.contains("The 'language' argument must be one of: English, Spanish, French"));
    }

    @Test
    void rejectsLanguageOverTenCharacters() {
        var args = new VocabularyLookupArguments("apple", "averylonglanguage");
        var errors = VocabularyLookupArgumentsValidator.validate(args);
        assertTrue(errors.contains("The 'language' argument must not exceed 10 characters."));
    }

    @Test
    void rejectsLanguageWithSurroundingWhitespace() {
        var args = new VocabularyLookupArguments("apple", " English ");
        var errors = VocabularyLookupArgumentsValidator.validate(args);
        assertTrue(errors.contains(
                "The 'language' argument must not contain leading or trailing whitespace."));
    }

    @Test
    void validatesLeadingAndTrailingSpaces() {
        var args = new VocabularyLookupArguments(" apple ", "english");
        var errors = VocabularyLookupArgumentsValidator.validate(args);
        assertTrue(errors.contains("The 'term' argument must not start with a space."));
        assertTrue(errors.contains("The 'term' argument must not end with a space."));
    }

    @Test
    void validatesRepeatedInternalSpaces() {
        var args = new VocabularyLookupArguments("apple  pie", "english");
        var errors = VocabularyLookupArgumentsValidator.validate(args);
        assertTrue(errors.contains("The 'term' argument must not contain multiple spaces."));
    }

    @Test
    void validatesMoreThanThreeTokens() {
        var args = new VocabularyLookupArguments("one two three four", "english");
        var errors = VocabularyLookupArgumentsValidator.validate(args);
        assertTrue(errors.contains("The 'term' argument must not contain more than 3 words."));
    }

    @Test
    void validatesInvalidPunctuation() {
        var args = new VocabularyLookupArguments("apple!", "english");
        var errors = VocabularyLookupArgumentsValidator.validate(args);
        assertTrue(errors.contains("The 'term' argument contains invalid characters."));
    }

    @Test
    void validatesMultipleViolationsTogether() {
        var args = new VocabularyLookupArguments(" apple  pie ", "german");
        var errors = VocabularyLookupArgumentsValidator.validate(args);
        assertTrue(errors.contains("The 'language' argument must be one of: English, Spanish, French"));
        assertTrue(errors.contains("The 'term' argument must not start with a space."));
        assertTrue(errors.contains("The 'term' argument must not end with a space."));
        assertTrue(errors.contains("The 'term' argument must not contain multiple spaces."));
    }


}
