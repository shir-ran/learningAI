package dev.learning.vocabulary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class VocabularyLookupArgumentsValidator {
    private VocabularyLookupArgumentsValidator() {}

    private static final int MAX_TERM_LENGTH = 20;
    private static final int MAX_LANGUAGE_LENGTH = 10;
    private static final String SUPPORTED_LANGUAGES_DESCRIPTION =
            "English, Spanish, French";

    private static final String TOKEN =
            "\\p{L}[\\p{L}\\p{M}]*" +
                    "(?:[-'’]\\p{L}[\\p{L}\\p{M}]*)*";

    private static final Pattern VALID_TERM = Pattern.compile(
            TOKEN + "(?: " + TOKEN + "){0,2}");
    private static final Set<String> SUPPORTED_LANGUAGES =
            Set.of("english", "spanish", "french");

    public static List<String> validate(
            VocabularyLookupArguments arguments) {

        List<String> validationErrors = new ArrayList<>();

        if (arguments == null) {
            validationErrors.add("The 'arguments' object cannot be null.");
        } else {
            if (arguments.language() == null || arguments.language().isBlank()) {
                validationErrors.add("The 'language' argument is required and cannot be blank.");
            } else {
                String language = arguments.language();
                if (language.length() > MAX_LANGUAGE_LENGTH) {
                    validationErrors.add("The 'language' argument must not exceed 10 characters.");
                }
                if (!language.equals(language.strip())) {
                    validationErrors.add("The 'language' argument must not contain leading or trailing whitespace.");
                }
                if (!SUPPORTED_LANGUAGES.contains(language.toLowerCase(Locale.ROOT))) {
                    validationErrors.add(
                            "The 'language' argument must be one of: "
                                    + SUPPORTED_LANGUAGES_DESCRIPTION);
                }
            }

            if (arguments.term() == null || arguments.term().isBlank()) {
                validationErrors.add("The 'term' argument is required and cannot be blank.");
            } else {
                var term = arguments.term();
                if (term.codePointCount(0, term.length()) > MAX_TERM_LENGTH) {
                    validationErrors.add("The 'term' argument must not exceed 20 characters.");
                }

                if (term.contains("  ")) {
                    validationErrors.add("The 'term' argument must not contain multiple spaces.");
                }

                if (term.endsWith(" ")) {
                    validationErrors.add("The 'term' argument must not end with a space.");
                }

                if (term.startsWith(" ")) {
                    validationErrors.add("The 'term' argument must not start with a space.");
                }

                if (term.endsWith("'") || term.endsWith("’")) {
                    validationErrors.add("The 'term' argument must not end with an apostrophe.");
                }

                if (term.startsWith("'") || term.startsWith("’")) {
                    validationErrors.add("The 'term' argument must not start with an apostrophe.");
                }

                if (term.endsWith("-")) {
                    validationErrors.add("The 'term' argument must not end with a hyphen.");
                }

                if (term.startsWith("-")) {
                    validationErrors.add("The 'term' argument must not start with a hyphen.");
                }

                if (term.split(" ", -1).length > 3) {
                    validationErrors.add("The 'term' argument must not contain more than 3 words.");
                }

                if (!VALID_TERM.matcher(term).matches()) {
                    validationErrors.add("The 'term' argument contains invalid characters.");
                }
            }
        }
        return validationErrors;
    }
}
