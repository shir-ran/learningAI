package dev.learning.vocabulary;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;


public class InMemoryVocabularyLookupTool implements VocabularyLookupTool {

    private final Map<VocabularyLookupArguments, VocabularyEntry> vocabularyEntries = new HashMap<>();



    @Override
    public VocabularyEntry lookup(VocabularyLookupArguments arguments) {
        var result = vocabularyEntries.get(lookupKey(arguments));
        if (result == null) {
            throw new NoSuchElementException("No vocabulary entry found for the given arguments: " + arguments);
        }
        return result;
    }

    public void addEntry(VocabularyLookupArguments arguments, VocabularyEntry entry) {
        vocabularyEntries.put(lookupKey(arguments), entry);
    }

    private static VocabularyLookupArguments lookupKey(VocabularyLookupArguments arguments) {
        return new VocabularyLookupArguments(
                arguments.term().toLowerCase(Locale.ROOT),
                arguments.language().toLowerCase(Locale.ROOT));
    }
}
