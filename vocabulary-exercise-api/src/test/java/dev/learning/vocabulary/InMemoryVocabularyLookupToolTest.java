package dev.learning.vocabulary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryVocabularyLookupToolTest {

    private final InMemoryVocabularyLookupTool lookupTool = new InMemoryVocabularyLookupTool();

    @BeforeEach
    void init() {
        // Initialize the InMemoryVocabularyLookupTool with a sample entry
        VocabularyLookupArguments args = new VocabularyLookupArguments("bank", "English");
        VocabularyEntry entry = new VocabularyEntry("bank", "English", "noun", "Land beside a river.");
        lookupTool.addEntry(args, entry);
    }

    @Test
    void testBankExists() {
        VocabularyLookupArguments args = new VocabularyLookupArguments("bank", "English");
        VocabularyEntry entry = lookupTool.lookup(args);
        assertNotNull(entry);
        assertEquals("bank", entry.term());
        assertEquals("English", entry.language());
        assertEquals("noun", entry.partOfSpeech());
        assertEquals("Land beside a river.", entry.definition());
    }

    @Test
    void testAbsentTerm() {
        VocabularyLookupArguments args = new VocabularyLookupArguments("nonexistent", "English");
        assertThrows(NoSuchElementException.class, () -> lookupTool.lookup(args));
    }

    @Test
    void testUppercaseTermAndLanguage() {
        VocabularyLookupArguments args = new VocabularyLookupArguments("BANK", "ENGLISH");
        VocabularyEntry entry = lookupTool.lookup(args);
        assertNotNull(entry);
        assertEquals("bank", entry.term());
        assertEquals("English", entry.language());
        assertEquals("noun", entry.partOfSpeech());
        assertEquals("Land beside a river.", entry.definition());    }

}
