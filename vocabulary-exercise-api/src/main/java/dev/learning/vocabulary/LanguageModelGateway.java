package dev.learning.vocabulary;

@FunctionalInterface
public interface LanguageModelGateway {
    String generate(String prompt);
}

