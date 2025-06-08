package nmarakushev.projects.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class NLPProcessorTest {

    private NLPProcessor nlpProcessor;

    @BeforeEach
    public void setUp() throws IOException {
        nlpProcessor = new NLPProcessor();
    }

    @Test
    public void testExtractKeyTerms() {
        String comment = "This method calculates the sum of two numbers";

        Set<String> terms = nlpProcessor.extractKeyTerms(comment);

        assertNotNull(terms);
        assertTrue(terms.contains("method"));
        assertTrue(terms.contains("calculates"));
        assertTrue(terms.contains("sum"));
        assertTrue(terms.contains("numbers"));
        assertFalse(terms.contains("this"));
        assertFalse(terms.contains("the"));
        assertFalse(terms.contains("of"));
        assertFalse(terms.contains("two"));
    }
    
    @Test
    public void testExtractKeyTerms_EmptyInput() {
        Set<String> terms = nlpProcessor.extractKeyTerms("");

        assertNotNull(terms);
        assertTrue(terms.isEmpty());
    }
    
    @Test
    public void testExtractKeyTerms_NullInput() {
        assertThrows(NullPointerException.class, () -> {
            nlpProcessor.extractKeyTerms(null);
        });
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should initialize successfully with valid model")
        void shouldInitializeSuccessfully() {
            assertDoesNotThrow(NLPProcessor::new);
        }

        @Test
        @DisplayName("Should throw IOException when model file is missing")
        void shouldThrowIOExceptionWhenModelMissing() {
            assertNotNull(nlpProcessor);
        }
    }

    @Nested
    @DisplayName("Extract Key Terms Tests")
    class ExtractKeyTermsTests {

        @Test
        @DisplayName("Should extract meaningful terms from simple comment")
        void shouldExtractMeaningfulTermsFromSimpleComment() {
            String comment = "This method calculates the total amount";

            Set<String> keyTerms = nlpProcessor.extractKeyTerms(comment);

            assertNotNull(keyTerms);
            assertTrue(keyTerms.contains("method"));
            assertTrue(keyTerms.contains("calculates"));
            assertTrue(keyTerms.contains("total"));
            assertTrue(keyTerms.contains("amount"));

            assertFalse(keyTerms.contains("this"));
            assertFalse(keyTerms.contains("the"));
        }

        @Test
        @DisplayName("Should filter out stop words")
        void shouldFilterOutStopWords() {
            String comment = "a the is are this that and or but";

            Set<String> keyTerms = nlpProcessor.extractKeyTerms(comment);

            assertTrue(keyTerms.isEmpty(), "All stop words should be filtered out");
        }

        @Test
        @DisplayName("Should filter out short words")
        void shouldFilterOutShortWords() {
            String comment = "a an it to be do go run walk";

            Set<String> keyTerms = nlpProcessor.extractKeyTerms(comment);

            assertEquals(1, keyTerms.size());
            assertTrue(keyTerms.contains("walk"));
        }

        @Test
        @DisplayName("Should filter out non-alphabetic tokens")
        void shouldFilterOutNonAlphabeticTokens() {
            String comment = "method123 calculate$ total_amount 2023 price";

            Set<String> keyTerms = nlpProcessor.extractKeyTerms(comment);

            assertNotNull(keyTerms);
            assertTrue(keyTerms.contains("price"));

            assertFalse(keyTerms.contains("method123"));
            assertFalse(keyTerms.contains("calculate$"));
            assertFalse(keyTerms.contains("total_amount"));
            assertFalse(keyTerms.contains("2023"));

            for (String term : keyTerms) {
                assertTrue(term.length() > 3, "Term '" + term + "' should be longer than 3 characters");
                assertTrue(term.matches("[a-z]+"), "Term '" + term + "' should only contain lowercase letters");
            }
        }

        @Test
        @DisplayName("Should convert to lowercase")
        void shouldConvertToLowercase() {
            String comment = "METHOD Calculate TOTAL Amount";

            Set<String> keyTerms = nlpProcessor.extractKeyTerms(comment);

            assertTrue(keyTerms.contains("method"));
            assertTrue(keyTerms.contains("calculate"));
            assertTrue(keyTerms.contains("total"));
            assertTrue(keyTerms.contains("amount"));

            assertFalse(keyTerms.contains("METHOD"));
            assertFalse(keyTerms.contains("Calculate"));
        }

        @Test
        @DisplayName("Should handle whitespace-only string")
        void shouldHandleWhitespaceOnlyString() {
            String comment = "   \t\n   ";

            Set<String> keyTerms = nlpProcessor.extractKeyTerms(comment);

            assertNotNull(keyTerms);
            assertTrue(keyTerms.isEmpty());
        }

        @Test
        @DisplayName("Should handle punctuation correctly")
        void shouldHandlePunctuationCorrectly() {
            String comment = "Hello, world! This method does something.";

            Set<String> keyTerms = nlpProcessor.extractKeyTerms(comment);

            assertTrue(keyTerms.contains("hello"));
            assertTrue(keyTerms.contains("world"));
            assertTrue(keyTerms.contains("method"));
            assertTrue(keyTerms.contains("does"));
            assertTrue(keyTerms.contains("something"));
        }

        @Test
        @DisplayName("Should return unique terms only")
        void shouldReturnUniqueTermsOnly() {
            String comment = "method method calculate calculate total total";

            Set<String> keyTerms = nlpProcessor.extractKeyTerms(comment);

            assertEquals(3, keyTerms.size());
            assertTrue(keyTerms.contains("method"));
            assertTrue(keyTerms.contains("calculate"));
            assertTrue(keyTerms.contains("total"));
        }

        @Test
        @DisplayName("Should handle mixed case duplicates")
        void shouldHandleMixedCaseDuplicates() {
            String comment = "Method METHOD method Calculate CALCULATE calculate";

            Set<String> keyTerms = nlpProcessor.extractKeyTerms(comment);

            assertEquals(2, keyTerms.size());
            assertTrue(keyTerms.contains("method"));
            assertTrue(keyTerms.contains("calculate"));
        }

        @Test
        @DisplayName("Should handle complex programming comment")
        void shouldHandleComplexProgrammingComment() {
            String comment = "This method validates user input and returns boolean result";

            Set<String> keyTerms = nlpProcessor.extractKeyTerms(comment);

            assertTrue(keyTerms.contains("method"));
            assertTrue(keyTerms.contains("validates"));
            assertTrue(keyTerms.contains("user"));
            assertTrue(keyTerms.contains("input"));
            assertTrue(keyTerms.contains("returns"));
            assertTrue(keyTerms.contains("boolean"));
            assertTrue(keyTerms.contains("result"));

            assertFalse(keyTerms.contains("this"));
            assertFalse(keyTerms.contains("and"));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Performance")
    class EdgeCasesAndPerformance {

        @Test
        @DisplayName("Should handle very long comment")
        void shouldHandleVeryLongComment() {

            Set<String> keyTerms = nlpProcessor.extractKeyTerms("method calculates total amount ".repeat(1000));

            assertNotNull(keyTerms);
            assertEquals(4, keyTerms.size());
        }

        @Test
        @DisplayName("Should handle special characters")
        void shouldHandleSpecialCharacters() {
            String comment = "method@domain.com #hashtag $variable %percentage";

            Set<String> keyTerms = nlpProcessor.extractKeyTerms(comment);

            assertNotNull(keyTerms);
        }

        @Test
        @DisplayName("Should handle unicode characters")
        void shouldHandleUnicodeCharacters() {
            String comment = "método calcula total montante";

            Set<String> keyTerms = nlpProcessor.extractKeyTerms(comment);

            assertNotNull(keyTerms);
        }
    }
}