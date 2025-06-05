package nmarakushev.projects.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NLPProcessorIntegrationTest {

    private NLPProcessor processor;

    @BeforeEach
    void setUp() throws IOException {
        processor = new NLPProcessor();
    }

    @Test
    @DisplayName("Should correctly identify stop words")
    void shouldFilterStopWords() {
        String[] stopWords = {"a", "the", "is", "are", "this", "that", "and", "or", "but"};

        for (String stopWord : stopWords) {
            String comment = stopWord + " important method";
            Set<String> keyTerms = processor.extractKeyTerms(comment);

            assertFalse(keyTerms.contains(stopWord), "Stop word '" + stopWord + "' should be filtered out");
            assertTrue(keyTerms.contains("important"));
            assertTrue(keyTerms.contains("method"));
        }
    }

    @Test
    @DisplayName("Should filter words by length - too short")
    void shouldFilterShortWords() {
        String comment = "abc x a1b2";
        Set<String> keyTerms = processor.extractKeyTerms(comment);

        assertFalse(keyTerms.contains("abc"));
        assertFalse(keyTerms.contains("x"));
        assertFalse(keyTerms.contains("a1b2"));
    }

    @Test
    @DisplayName("Should include valid words")
    void shouldIncludeValidWords() {
        String comment = "test method calculate validation";
        Set<String> keyTerms = processor.extractKeyTerms(comment);

        assertTrue(keyTerms.contains("test"));
        assertTrue(keyTerms.contains("method"));
        assertTrue(keyTerms.contains("calculate"));
        assertTrue(keyTerms.contains("validation"));
    }

    @Test
    @DisplayName("Should filter words containing numbers")
    void shouldFilterWordsWithNumbers() {
        String comment = "method123 test456 valid";
        Set<String> keyTerms = processor.extractKeyTerms(comment);

        assertFalse(keyTerms.contains("method123"));
        assertFalse(keyTerms.contains("test456"));
        assertTrue(keyTerms.contains("valid"));
    }

    @Test
    @DisplayName("Should filter words containing punctuation")
    void shouldFilterWordsWithPunctuation() {
        String comment = "test! method$ valid";
        Set<String> keyTerms = processor.extractKeyTerms(comment);

        assertFalse(keyTerms.contains("test!"));
        assertFalse(keyTerms.contains("method$"));
        assertTrue(keyTerms.contains("valid"));
    }

    @Test
    @DisplayName("Should handle real-world Java comment examples")
    void shouldHandleRealWorldJavaComments() {
        String[] realComments = {
                "Calculate the monthly payment for a loan",
                "This method validates user input parameters",
                "TODO Implement error handling for edge cases",
                "Returns the maximum value from the array"
        };

        for (String comment : realComments) {
            Set<String> keyTerms = processor.extractKeyTerms(comment);
            assertNotNull(keyTerms);
            assertFalse(keyTerms.isEmpty(), "Should extract some meaningful terms from: " + comment);
        }
    }

    @Test
    @DisplayName("Should extract meaningful terms from loan payment comment")
    void shouldExtractTermsFromLoanComment() {
        String comment = "Calculate the monthly payment for a loan";
        Set<String> keyTerms = processor.extractKeyTerms(comment);

        assertTrue(keyTerms.contains("calculate"));
        assertTrue(keyTerms.contains("monthly"));
        assertTrue(keyTerms.contains("payment"));
        assertTrue(keyTerms.contains("loan"));
        assertFalse(keyTerms.contains("the"));
        assertFalse(keyTerms.contains("a"));
    }

    @Test
    @DisplayName("Should extract terms from validation comment")
    void shouldExtractTermsFromValidationComment() {
        String comment = "This method validates user input parameters";
        Set<String> keyTerms = processor.extractKeyTerms(comment);

        assertTrue(keyTerms.contains("method"));
        assertTrue(keyTerms.contains("validates"));
        assertTrue(keyTerms.contains("user"));
        assertTrue(keyTerms.contains("input"));
        assertTrue(keyTerms.contains("parameters"));
        assertFalse(keyTerms.contains("this"));
    }
}